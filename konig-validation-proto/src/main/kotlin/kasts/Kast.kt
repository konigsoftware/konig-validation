package com.konigsoftware.validation.kasts

abstract class Kast<FromType, ToType> {
    operator fun invoke(fromValue: Any, options: List<String>): Any {
        val typedFromValue = runCatching {
            @Suppress("UNCHECKED_CAST")
            fromValue as FromType
        }.getOrElse {
            throw IllegalArgumentException("document this")
        }

        return kast(typedFromValue, options) as Any
    }

    abstract fun kast(fromValue: FromType, options: List<String>): ToType
}
