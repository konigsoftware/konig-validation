package com.konigsoftware.validation.proto.example

import com.konigsoftware.validation.KProtoMessage
import com.konigsoftware.validation.examples.ExampleOuterClass.Example
import kotlin.reflect.KClass

class KExample : KProtoMessage(Example.getDescriptor()) {
    val string_default: String by ProtoField()
    val string_nonempty: String by ProtoField()
    val string_maxlength: String by ProtoField()
    val int_default: Int by ProtoField()
}

sealed class KExampleAdvanced : KProtoMessage(Example.getDescriptor()) {

    companion object : KTypeSelector<Example> {
        override fun getKTypeForMessage(message: Example): KClass<*>? {
            return if (message.intDefault > 0) {
                KExampleSubType1::class
            } else if (message.intDefault < 0) {
                KExampleSubType2::class
            } else {
                null
            }
        }
    }

    class KExampleSubType1 : KExampleAdvanced() {
        var string_nonempty: String by ProtoField()
    }

    class KExampleSubType2 : KExampleAdvanced() {
        var string_maxlength: String by ProtoField()
    }
}
