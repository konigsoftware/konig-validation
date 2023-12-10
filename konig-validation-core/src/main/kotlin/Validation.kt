package com.konigsoftware.validation

import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

// Validators

annotation class ValidatorId(val id: String)
annotation class SupportedOptions(vararg val options: KClass<out ValidatorOption>)

interface Validator<InMemoryType : Any?> {
    fun validate(value: InMemoryType): ValidationResult
}

class NoOpValidator<InMemoryType> : Validator<InMemoryType> {
    override fun validate(value: InMemoryType) =
        ValidationResult.Valid
}

fun <ValidatorType : Validator<*>> KClass<ValidatorType>.getValidatorId(): String =
    this.findAnnotation<ValidatorId>()?.id
        ?: throw IllegalArgumentException("Missing ValidatorId annotation ${this.qualifiedName}")

fun <InMemoryType, ValidatorType : Validator<InMemoryType>> KClass<ValidatorType>.getSupportedOptions():
        List<KClass<out ValidatorOption>> =
    this.findAnnotation<SupportedOptions>()?.options?.toList()
        ?: throw IllegalArgumentException("Missing ValidatorId annotation ${this.qualifiedName}")

// Validator Options

val VALIDATOR_OPTION_STRING_FORMAT = Regex("([a-z_]+)(:(.+))?")

annotation class ValidatorOptionId(val id: String)

abstract class ValidatorOption {
    open fun initializeFromString(option: String): Boolean = true
}

inline fun <reified OptionType : ValidatorOption> Array<out ValidatorOption>.hasOption(): Boolean =
    count { it is OptionType } == 1

inline fun <reified OptionType> Array<out ValidatorOption>.getOption(): OptionType? =
    singleOrNull { it is OptionType } as OptionType?

fun <OptionType : ValidatorOption> KClass<OptionType>.getOptionId(): String =
    this.findAnnotation<ValidatorOptionId>()?.id
        ?: throw IllegalArgumentException("Missing ValidatorOptionId annotation ${this.qualifiedName}")

// Validation Result

sealed interface ValidationResult {
    data object Valid : ValidationResult
    class Invalid(val errorMessage: String) : ValidationResult
}
