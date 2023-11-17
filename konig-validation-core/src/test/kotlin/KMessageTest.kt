package com.konigsoftware.validation

import com.konigsoftware.validation.kasts.StringKast
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SimpleKMessage() : KMessage() {

    val testString: String by KField(StringKast)

}

class KMessageTest {


    @Test
    fun `can't get unset field`() {
        assertThrows<IllegalStateException> { println(SimpleKMessage().testString) }
    }
}
