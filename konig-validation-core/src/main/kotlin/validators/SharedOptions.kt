package com.konigsoftware.validation.validators

import com.konigsoftware.validation.ValidatorOption
import com.konigsoftware.validation.ValidatorOptionId
import kotlin.properties.Delegates

@ValidatorOptionId(id = "disallow_empty")
object DisallowEmptyOption : ValidatorOption()

@ValidatorOptionId(id = "max_length")
class MaxLengthOption() : ValidatorOption() {

    var maxLength by Delegates.notNull<Int>()

    constructor(maxLength: Int) : this() {
        this.maxLength = maxLength
    }

    override fun initializeFromString(option: String) =
        runCatching { this.maxLength = option.toInt() }.isSuccess
}
