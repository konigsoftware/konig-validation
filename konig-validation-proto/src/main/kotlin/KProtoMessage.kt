package com.konigsoftware.validation

import com.google.protobuf.Descriptors.Descriptor
import com.google.protobuf.Message
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class KProtoMessage(
    private val descriptor: Descriptor,
    kMessages: KMessages = KMessages.defaultInstance()
) : KMessage(kMessages) {

    interface KTypeSelector<MessageType : Message> {
        fun getKTypeForMessage(message: MessageType): KClass<*>?
    }

    inner class ProtoField<InMemoryType> {
        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): KField<InMemoryType> {
            /* Do CamelCase->snake_case conversion here?? */
            val fieldName = property.name

            val fieldDescriptor = descriptor.fields
                .singleOrNull { it.name == fieldName }
                ?: throw IllegalArgumentException(
                    "No field named $fieldName in ${descriptor.name}"
                )

            return KField(
                KProtoMessages.getValidatorForField(fieldDescriptor, kMessages)
            )
        }
    }
}

