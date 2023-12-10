package com.konigsoftware.validation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties

private val logger = KotlinLogging.logger { }

abstract class KMessage(protected val kMessages: KMessages = KMessages.defaultInstance()) {

    inner class KField<InMemoryType>(
        private val validator: Validator<InMemoryType>,
    ) {
        // Couldn't use lateinit because upper bound of InMemoryType is null.
        // Feels weird to declare it nullable if InMemoryType can itself be nullable...
        // So, wrap it in a list (sorry, wasteful), to denote whether the
        // [possibly null] value has ever been assigned to.
        // ...thread-safety?
        var value: List<InMemoryType> = listOf()

        operator fun getValue(thisRef: Any?, property: KProperty<*>): InMemoryType {
            if (value.isEmpty())
                throw IllegalStateException(
                    "Trying to get property which has never been set: ${property.name}"
                )

            return value.single()
        }

        operator fun setValue(
            thisRef: Any?,
            property: KProperty<*>,
            newValue: InMemoryType
        ) = when (val validationResult = validator.validate(newValue)) {
            is ValidationResult.Valid -> this.value = listOf(newValue)
            is ValidationResult.Invalid -> throw IllegalArgumentException(
                "Trying to set invalid value for property ${property.name}:" +
                        " ${validationResult.errorMessage}"
            )
        }
    }
}

fun <KMessageType : KMessage> KMessageType.checkInitialized(): Boolean {
    return this::class.declaredMemberProperties
        .map { prop ->
            runCatching { prop.getter.call(this) }
                .onSuccess {
                    logger.info { "Successfully got property [${prop.name}]: $it" }
                }
                .onFailure {
                    logger.info { "Could not get property [${prop.name}]" }
                }
                .isSuccess
        }
        .all { it /* == true */ }
}
