package com.konigsoftware.validation

import com.konigsoftware.validation.validators.DisallowEmptyOption
import com.konigsoftware.validation.validators.MaxLengthOption
import com.konigsoftware.validation.validators.StringValidator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse

class SimpleKMessage() : KMessage() {
    var testString: String by KField(
        StringValidator(
            DisallowEmptyOption,
            MaxLengthOption(10)
        )
    )
}

class KMessageTest {

    @Test
    fun `can't get unset field`() {
        assertThrows<IllegalStateException> { println(SimpleKMessage().testString) }
    }

    @Test
    fun `can't set invalid string value`() {
        val kMessage = SimpleKMessage()

        assertThrows<IllegalArgumentException> { kMessage.testString = "" }
    }

    @Test
    fun `check initialized`() {
        val kMessage = SimpleKMessage()

        assertFalse { kMessage.checkInitialized() }

        kMessage.testString = "test"

        assertTrue { kMessage.checkInitialized() }
    }
}
