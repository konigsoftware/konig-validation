package com.konigsoftware.validation

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import com.konigsoftware.validation.kasts.Int32Kast
import com.konigsoftware.validation.kasts.Kast
import com.konigsoftware.validation.kasts.StringKast
import kotlin.reflect.KClass
import kotlin.reflect.full.*

class KProtoMessages(userKasts: Map<String, Kast<*, *>> = mapOf()) {

    companion object {
        private val defaultKastIdForType = mapOf(
            Descriptors.FieldDescriptor.Type.STRING to "string",
            Descriptors.FieldDescriptor.Type.INT32 to "int32",
        )

        fun getDefaultKastIdForType(type: Descriptors.FieldDescriptor.Type) =
            defaultKastIdForType[type] ?: throw java.lang.IllegalArgumentException("No default parser for this type")

        private val defaultFieldKasts: Map<String, Kast<*, *>> =
            mapOf(
                "string" to StringKast,
                "int32" to Int32Kast,
            )

        private val defaultInstance = KProtoMessages()

        fun <KMessageType : KProtoMessage> fromProtoDefault(
            message: Message, kMessage: KClass<KMessageType>
        ): KMessageType? = defaultInstance.fromProto(message, kMessage)

        inline fun <reified KMessageType : KProtoMessage> fromProtoDefault(
            message: Message
        ): KMessageType? = fromProtoDefault(message, KMessageType::class)
    }

    // TODO: Check for overlap first
    private val kasts = defaultFieldKasts + userKasts

    internal fun getFieldKast(kastId: String): Kast<*, *> =
        kasts[kastId] ?: throw IllegalArgumentException("No field validator for")

    fun <MessageType : Message, KMessageType : KProtoMessage> fromProto(
        message: MessageType, kMessage: KClass<KMessageType>
    ): KMessageType? {
        var kVariantMessage: KClass<KMessageType>? = kMessage

        val companionObject = kMessage.companionObject
            ?.takeIf { it.isSubclassOf(KProtoMessage.KTypeSelector::class) }

        if (companionObject != null) {
            kVariantMessage =
                companionObject.memberFunctions.single { it.name == "getKTypeForMessage" /* TODO: Find a better way to identify this function */ }
                    .call(kMessage.companionObjectInstance, message) as KClass<KMessageType>
        }

        if (kVariantMessage == null) return null

        val newMessage = kVariantMessage.createInstance()
        newMessage.initialize(this, message)
        return if (checkMessage(newMessage, kMessage)) {
            newMessage
        } else {
            null
        }
    }

    inline fun <reified KMessageType : KProtoMessage, MessageType : Message> fromProto(message: MessageType) =
        fromProto(message, KMessageType::class)

    private fun <KMessageType : KMessage> checkMessage(message: KMessageType, kMessage: KClass<KMessageType>): Boolean {
        return kMessage.memberProperties
            .map { runCatching { it.get(message) }.isSuccess }
            .all { it }
    }
}