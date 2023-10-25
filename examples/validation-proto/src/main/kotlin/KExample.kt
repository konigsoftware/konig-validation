package com.konigsoftware.validation.proto.example

import com.konigsoftware.validation.KProtoMessage
import com.konigsoftware.validation.examples.ExampleOuterClass.Example
import kotlin.reflect.KClass

class KExample : KProtoMessage() {
    val my_string: String by Default()
    val my_int: Int by Default()
}

sealed class KExampleAdvanced : KProtoMessage() {

    companion object : KProtoMessage.KTypeSelector<Example> {
        override fun getKTypeForMessage(message: Example): KClass<*>? {
            return if (message.myString2.isNotEmpty()) {
                KExampleSubType1::class
            } else if (message.myString3.isNotEmpty()) {
                KExampleSubType2::class
            } else {
                null
            }
        }
    }

    class KExampleSubType1 : KExampleAdvanced() {
        val my_string_2: String by Default()
        val my_int: Int by Default()
    }

    class KExampleSubType2 : KExampleAdvanced() {
        val my_string_3: String by Default()
        val my_int: Int by Default()
    }
}
