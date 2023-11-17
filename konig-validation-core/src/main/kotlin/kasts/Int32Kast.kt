package com.konigsoftware.validation.kasts

object Int32Kast : Kast<Int, Int>() {
    override fun fromWire(fromValue: Int): TypedKastResult<Int> {
        return TypedKastResult.Success(fromValue)
    }

    override fun toWire(fromValue: Int): TypedKastResult<Int> {
        return TypedKastResult.Success(fromValue)
    }

    override fun validate(value: Int, options: List<String>): ValidationResult {
        return ValidationResult.Success
    }
}
