package com.konigsoftware.validation.proto.example

import com.konigsoftware.validation.KMessages
import com.konigsoftware.validation.checkInitialized
import com.konigsoftware.validation.examples.example
import com.konigsoftware.validation.fromProto
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ExampleTest {

    @Test
    fun `valid message`() {
        val validExample = example {
            stringDefault = "a string value"
            stringNonempty = "a non-empty string"
            stringMaxlength = "a short one"
            intDefault = 456
        }

        val kExample: KExample? = KMessages.defaultInstance().fromProto(validExample)

        assertNotNull(kExample)

        assertTrue(kExample.checkInitialized())

        assertEquals("a string value", kExample.string_default)
        assertEquals("a non-empty string", kExample.string_nonempty)
        assertEquals("a short one", kExample.string_maxlength)
        assertEquals(456, kExample.int_default)
    }

    @Test
    fun `invalid - empty string`() {
        val validExample = example {
            stringDefault = "a string value"
            stringNonempty = ""
            stringMaxlength = "a short one"
            intDefault = 0
        }

        val kExample: KExample? = KMessages.defaultInstance().fromProto(validExample)

        assertNull(kExample)
    }

    @Test
    fun `invalid - string too long`() {
        val validExample = example {
            stringDefault = "a string value"
            stringNonempty = "a non-empty string"
            stringMaxlength = "a very long one which is in fact too long"
            intDefault = 0
        }

        val kExample: KExample? = KMessages.defaultInstance().fromProto(validExample)

        assertNull(kExample)
    }

    @Test
    fun `advanced message - one`() {
        val validExample = example {
            stringNonempty = "a non-empty string"
            intDefault = 10
        }

        val kExample: KExampleAdvanced.KExampleSubType1? =
            KMessages.defaultInstance().fromProto(validExample)

        assertNotNull(kExample)
        assertEquals("a non-empty string", kExample.string_nonempty)
    }

    @Test
    fun `advanced message - two`() {
        val validExample = example {
            stringMaxlength = "a short string"
            intDefault = -10
        }

        val kExample: KExampleAdvanced.KExampleSubType2? =
            KMessages.defaultInstance().fromProto(validExample)

        assertNotNull(kExample)
        assertEquals("a short string", kExample.string_maxlength)
    }

    @Test
    fun `advanced message - invalid`() {
        val validExample = example {
            intDefault = 0
        }

        val kExample: KExampleAdvanced? =
            KMessages.defaultInstance().fromProto(validExample)

        assertNull(kExample)
    }
}
