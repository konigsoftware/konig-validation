package com.konigsoftware.validation

import com.google.protobuf.Descriptors
import com.google.protobuf.Descriptors.FieldDescriptor
import com.google.protobuf.Message
import com.konigsoftware.validation.marshalers.IdentityMarshaller
import com.konigsoftware.validation.validators.StringValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

val logger = KotlinLogging.logger { }

object KProtoMessages {

    private val defaultValidatorForWireType = mapOf<FieldDescriptor.Type, KClass<out Validator<*>>>(
        Descriptors.FieldDescriptor.Type.STRING to StringValidator::class
    )

    internal fun <InMemoryType> getValidatorForField(
        fieldDescriptor: FieldDescriptor,
        kMessages: KMessages
    ): Validator<InMemoryType> {
        val options: Array<out ValidatorOption> =
            getValidatorOptionsForField(fieldDescriptor, kMessages).toTypedArray()

        val validatorClass = defaultValidatorForWireType[fieldDescriptor.type]

        return if (validatorClass != null) {
            validatorClass.primaryConstructor?.call(options)
                    as Validator<InMemoryType>
        } else {
            NoOpValidator()
        }
    }

    private fun getValidatorOptionsForField(
        fieldDescriptor: FieldDescriptor,
        kMessages: KMessages
    ): List<ValidatorOption> =
        getFieldAnnotations<List<String>>("ValidatorOption", fieldDescriptor)
            ?.map {
                kMessages.getValidatorOptionFromString(it)
                    ?: throw IllegalArgumentException(
                        "Invalid ValidatorOption annotation on proto field " +
                                "[${fieldDescriptor.name}]: $it"
                    )
            } ?: listOf()

    private inline fun <reified AnnotationType> getFieldAnnotations(
        annotationName: String,
        fieldDescriptor: FieldDescriptor
    ) = fieldDescriptor.options.allFields.entries.singleOrNull {
        it.key.name == annotationName
    }?.value as AnnotationType?
}


inline fun <reified KMessageType : KProtoMessage, MessageType : Message> KMessages.fromProto(
    message: MessageType
): KMessageType? {
    var kVariantMessage: KClass<KMessageType>? = KMessageType::class

    val companionObject = KMessageType::class.companionObject
        ?.takeIf { it.isSubclassOf(KProtoMessage.KTypeSelector::class) }

    if (companionObject != null) {
        kVariantMessage =
            companionObject.memberFunctions.single { it.name == "getKTypeForMessage" /* TODO: Find a better way to identify this function */ }
                .call(KMessageType::class.companionObjectInstance, message) as KClass<KMessageType>?
    }

    if (kVariantMessage == null) return null

    // TODO: Support passing custom KMessages ('this')
    val newMessage: KMessageType = kVariantMessage.createInstance()

    val initializeSuccess = newMessage::class.declaredMemberProperties
        .map {
            val fieldName = it.name  // Support custom mappings?

            val fieldDescriptor = message.descriptorForType.findFieldByName(fieldName)

            initializePropertyFromField(
                fieldDescriptor,
                message,
                it,
                newMessage,
                this
            ).also {
                if (!it) logger.warn { "Failed to set proto field: $fieldName" }
            }
        }.all { it /* == true */ }

    return if (initializeSuccess) newMessage else null
}

fun <KMessageType> initializePropertyFromField(
    fieldDescriptor: FieldDescriptor,
    message: Message,
    property: KProperty1<*, *>,
    kMessage: KMessageType,
    kMessages: KMessages
): Boolean {
    val fieldType = fieldDescriptor.type
    val propertyType = property.returnType.classifier

    return when {
        fieldType == FieldDescriptor.Type.INT32 && propertyType == Int::class -> {
            val marshaller = IdentityMarshaller<Int>()

            val fieldValue: Int = message.getField(fieldDescriptor) as Int
            val unmarshaledValue: Int = marshaller.fromWire(fieldValue)

            val typedProperty =
                property.apply { isAccessible = true } as KProperty1<KMessageType, *>

            val kFieldDelegate = typedProperty.getDelegate(kMessage) as KMessage.KField<Int>

            runCatching { kFieldDelegate.setValue(null, property, unmarshaledValue) }
                .onFailure { logger.warn { "Error setting value for property ${property.name}: $fieldValue" } }
                .isSuccess
        }

        fieldType == FieldDescriptor.Type.STRING && propertyType == String::class -> {
            val marshaller = IdentityMarshaller<String>()

            val fieldValue: String = message.getField(fieldDescriptor) as String
            val unmarshaledValue: String = marshaller.fromWire(fieldValue)

            val typedProperty =
                property.apply { isAccessible = true } as KProperty1<KMessageType, *>

            val kFieldDelegate = typedProperty.getDelegate(kMessage) as KMessage.KField<String>

            runCatching { kFieldDelegate.setValue(null, property, unmarshaledValue) }
                .onFailure { logger.warn { "Error setting value for property ${property.name}: $fieldValue" } }
                .isSuccess
        }

        else -> {
            logger.warn { "Skipping field: ${fieldDescriptor.type to property.returnType}" }
            false
        }
    }
}
