package com.konigsoftware.validation.kasts

sealed interface KastResult {
    class Success(val value: Any) : KastResult
    object Failure : KastResult
}

sealed interface TypedKastResult<Type> {
    class Success<Type>(val value: Type) : TypedKastResult<Type>
    class Failure<Type> : TypedKastResult<Type>
}
