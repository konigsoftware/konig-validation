package com.konigsoftware.validation

import com.google.protobuf.Descriptors.FieldDescriptor
import com.google.protobuf.Message
import com.konigsoftware.validation.kasts.KastResult
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class KMessage() {

    interface KTypeSelector<MessageType : Message> {
        fun getKTypeForMessage(message: MessageType): KClass<*>?
    }

    private lateinit var kMessages: KMessages
    private lateinit var message: Message

    internal fun initialize(kMessages: KMessages, message: Message) {
        this.kMessages = kMessages
        this.message = message
    }

    private fun getFieldValue(fieldDescriptor: FieldDescriptor): Any {
        return message.getField(fieldDescriptor)
    }

    inner class Default<Type : Any> {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Type {
            /* Do CamelCase->snake_case conversion here?? */
            val fieldName = property.name

            val fieldDescriptor = message.descriptorForType.fields
                .singleOrNull { it.name == fieldName }
                ?: throw IllegalArgumentException("No field named $fieldName in ${message.descriptorForType.name}")

            val fieldKastId = getKastIdForField(fieldDescriptor)
            val kast = kMessages.getFieldKast(fieldKastId)

            val kastOptions = getKastOptionsForField(fieldDescriptor)

            val fieldValue = getFieldValue(fieldDescriptor)

            when (val kastResult = kast(fieldValue, kastOptions)) {
                is KastResult.Success -> return kastResult.value as Type
                is KastResult.Failure -> throw IllegalStateException("Trying to get property which is invalid")
            }
        }
    }
}

private fun getKastIdForField(fieldDescriptor: FieldDescriptor): String {
    return fieldDescriptor.options
        .allFields
        .entries
        .singleOrNull { it.key.name == "KastId" }
        ?.value as String?
        ?: KMessages.getDefaultKastIdForType(fieldDescriptor.type)
}

private fun getKastOptionsForField(fieldDescriptor: FieldDescriptor): List<String> {
    return fieldDescriptor.options
        .allFields
        .entries
        .singleOrNull { it.key.name == "KastOption" }
        ?.value as List<String>?
        ?: listOf()
}
