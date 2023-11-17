package com.konigsoftware.validation.kasts

abstract class Kast<WireType, InMemoryType> {

    fun id() = "test_id"

    fun fromWire(fromValue: Any, options: List<String>): KastResult {
        val typedFromValue = runCatching {
            @Suppress("UNCHECKED_CAST")
            fromValue as WireType
        }.getOrElse {
            throw IllegalArgumentException("document this")
        }

        return when (val result = fromWire(typedFromValue)) {
            // TODO validate
            is TypedKastResult.Success -> KastResult.Success(result.value as Any)
            is TypedKastResult.Failure -> KastResult.Failure
        }
    }

    fun toWire(fromValue: Any, options: List<String>): KastResult {
        val typedFromValue = runCatching {
            @Suppress("UNCHECKED_CAST")
            fromValue as InMemoryType
        }.getOrElse {
            throw IllegalArgumentException("document this")
        }

        return when (val result = toWire(typedFromValue)) {
            // TODO validate
            is TypedKastResult.Success -> KastResult.Success(result.value as Any)
            is TypedKastResult.Failure -> KastResult.Failure
        }
    }

    fun validate(value: Any, options: List<String>): ValidationResult {
        val typedValue = runCatching {
            @Suppress("UNCHECKED_CAST")
            value as InMemoryType
        }.getOrElse {
            throw IllegalArgumentException("document this")
        }

        return validateTyped(typedValue, options)
    }

    protected abstract fun fromWire(fromValue: WireType): TypedKastResult<InMemoryType>

    protected abstract fun toWire(fromValue: InMemoryType): TypedKastResult<WireType>

    protected abstract fun validateTyped(value: InMemoryType, options: List<String>): ValidationResult
}
