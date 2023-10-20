package com.konigsoftware.validation.proto.example

import com.konigsoftware.validation.KMessages
import com.konigsoftware.validation.examples.example
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ExampleTest {

    @Test
    fun testSanity() {
        println("Running Test")

        val validExample = example {
            myString = "another string value"
            myInt = 456
        }

        val kExample =
            KMessages.defaultKast(validExample, KExample::class)

        assertNotNull(kExample)

        println("my_string: ${kExample.my_string}")
        println("my_int: ${kExample.my_int}")

        val invalidExample = example {
            myString = "" // Empty strings are disallowed
            myInt = 456
        }

        val kInvalidExample = KMessages.defaultKast(invalidExample, KExample::class)

        assertNull(kInvalidExample)

        val kMessagesCustom = KMessages(mapOf(/* custom kasts go here */))

        val kExampleCustomKasts =
            kMessagesCustom.kast(validExample, KExample::class)

        assertNotNull(kExampleCustomKasts)

        println("(custom) my_string: ${kExampleCustomKasts.my_string}")
        println("(custom) my_int: ${kExampleCustomKasts.my_int}")
    }
}
