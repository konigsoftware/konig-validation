package com.konigsoftware.validation.kasts

sealed interface ValidationResult {
    object Success : ValidationResult
    class Failure(val errorDescription: String) : ValidationResult
}
