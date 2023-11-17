package com.konigsoftware.validation

import com.konigsoftware.validation.kasts.*
import kotlin.reflect.KProperty

abstract class KMessage {

    val values: MutableMap<String, Any> = mutableMapOf()
    val kMessages: KMessages = KMessages.default()

    inner class KField<InMemoryType : Any>(
        private val kast: Kast<*, InMemoryType>,
        vararg val options: KastOption
    ) {
        private fun getBasePropertyName(property: KProperty<*>): String =
            options.getOption<WireFieldName>()?.name ?: property.name // TODO: CamelCase -> snake_case ?

        operator fun getValue(thisRef: Any?, property: KProperty<*>): InMemoryType {
            val propName = getBasePropertyName(property)

            if (!values.containsKey(propName))
                throw IllegalStateException("Trying to get property $propName but no value has been set")

            val wireValue = values[propName]!!

            val memValue = kast.fromWire(wireValue, listOf())

            if (kast.validate(memValue, listOf()) is ValidationResult.Failure)
                throw IllegalStateException("Trying to get property $propName but value is invalid")

            @Suppress("UNCHECKED_CAST")
            return memValue as InMemoryType
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: InMemoryType): InMemoryType {
            val propName = getBasePropertyName(property)

            if (kast.validate(value, listOf()) is ValidationResult.Failure)
                throw IllegalStateException("Trying to set property $propName but new value is invalid")

            values[propName] = kast.toWire(value, listOf())

            return value
        }
    }

}
