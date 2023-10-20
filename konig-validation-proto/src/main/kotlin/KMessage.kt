package com.konigsoftware.validation

import com.google.protobuf.Descriptors.FieldDescriptor
import com.google.protobuf.Message
import kotlin.reflect.KProperty

open class KMessage {

    private lateinit var kMessages: KMessages
    private lateinit var message: Message

    internal fun initialize(kMessages: KMessages, message: Message) {
        this.kMessages = kMessages
        this.message = message
    }

    fun getKastIdForField(fieldDescriptor: FieldDescriptor): String {
        return fieldDescriptor.options
            .allFields
            .entries
            .singleOrNull { it.key.name == "KastId" }
            ?.value as String?
            ?: KMessages.getDefaultKastIdForType(fieldDescriptor.type)
    }

    fun getKastOptionsForField(fieldDescriptor: FieldDescriptor): List<String> {
        return fieldDescriptor.options
            .allFields
            .entries
            .singleOrNull { it.key.name == "KastOption" }
            ?.value as List<String>?
            ?: listOf()
    }

    fun getFieldValue(fieldDescriptor: FieldDescriptor): Any {
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
            val kastValue = kast(fieldValue, kastOptions)

            return kastValue as Type
        }
    }

}
