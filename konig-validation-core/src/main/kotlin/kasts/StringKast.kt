package com.konigsoftware.validation.kasts

object StringKast : Kast<String, String>() {
    override fun fromWire(fromValue: String): TypedKastResult<String> = TypedKastResult.Success(fromValue)
    override fun toWire(fromValue: String): TypedKastResult<String> = TypedKastResult.Success(fromValue)

    override fun validate(value: String, options: List<String>): ValidationResult {
        if (options.contains("disallow_empty") && value.isEmpty()) return ValidationResult.Failure("should not be empty")
        if (options.contains("email") && value.isEmpty()) return ValidationResult.Failure("should be an email")

        return ValidationResult.Success
    }
}
