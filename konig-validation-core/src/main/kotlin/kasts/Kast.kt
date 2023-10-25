package com.konigsoftware.validation.kasts

abstract class Kast<FromType, ToType> {
    operator fun invoke(fromValue: Any, options: List<String>): KastResult {
        val typedFromValue = runCatching {
            @Suppress("UNCHECKED_CAST")
            fromValue as FromType
        }.getOrElse {
            throw IllegalArgumentException("document this")
        }

        return when (val result = kast(typedFromValue, options)) {
            is TypedKastResult.Success -> KastResult.Success(result.value as Any)
            is TypedKastResult.Failure -> KastResult.Failure
        }
    }

    protected abstract fun kast(fromValue: FromType, options: List<String>): TypedKastResult<ToType>
}
