package com.konigsoftware.validation.validators

import com.konigsoftware.validation.*

@ValidatorId("stringDefault")
@SupportedOptions(
    DisallowEmptyOption::class,
    MaxLengthOption::class
)
class StringValidator(vararg options: ValidatorOption) : Validator<String> {

    private val disallowEmpty: Boolean = options.hasOption<DisallowEmptyOption>()
    private val maxLength: Int? = options.getOption<MaxLengthOption>()?.maxLength

    override fun validate(value: String): ValidationResult {
        if (disallowEmpty && value.isEmpty())
            return ValidationResult.Invalid("Empty strings not allowed, but string was empty")

        if (maxLength != null && value.length > maxLength)
            return ValidationResult.Invalid(
                """Max string length is [${maxLength}],  
                       but string length was [${value.length}]""".trimMargin()
            )

        return ValidationResult.Valid
    }
}

