package  com.konigsoftware.validation

import com.konigsoftware.validation.validators.StringValidator
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class KMessages {

    private val validators = mutableMapOf<String, KClass<out Validator<*>>>()
    private val validatorOptions = mutableMapOf<String, KClass<out ValidatorOption>>()

    fun <InMemoryType> registerValidator(validator: KClass<out Validator<InMemoryType>>) {
        val validatorId = validator.getValidatorId()

        if (validators.containsKey(validatorId))
            throw IllegalArgumentException(
                "Validator with id [${validatorId}] already registered."
            )

        validators += validatorId to validator

        validator.getSupportedOptions().forEach {
            val existing = validatorOptions.putIfAbsent(it.getOptionId(), it)
            if (existing != null && existing != it)
                throw IllegalStateException("Validator with duplicate id [${it.getOptionId()}]")
        }
    }

    fun getValidatorOptionFromString(stringOption: String): ValidatorOption? {
        val (optionName, _, optionParams) =
            VALIDATOR_OPTION_STRING_FORMAT.matchEntire(stringOption)?.destructured
                ?: return null

        val option = validatorOptions[optionName]?.objectInstance
            ?: validatorOptions[optionName]?.createInstance()
            ?: throw IllegalStateException("Invalid ValidatorOption: stringOption")

        if (!option.initializeFromString(optionParams))
            throw IllegalStateException(
                "Failed to initialize validator option [$optionName] " +
                        "with params: $optionParams"
            )

        return option
    }

    companion object {
        private val instance by lazy {
            KMessages().apply {
                registerValidator(StringValidator::class)
            }
        }

        fun defaultInstance() = instance
    }
}
