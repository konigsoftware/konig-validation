package com.konigsoftware.validation.kasts

object StringKast : Kast<String, String>() {
    override fun kast(fromValue: String, options: List<String>): TypedKastResult<String> {
        if (options.contains("disallow_empty") && fromValue.isEmpty()) return TypedKastResult.Failure()

        return TypedKastResult.Success(fromValue)
    }
}
