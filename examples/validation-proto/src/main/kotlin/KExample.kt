package com.konigsoftware.validation.proto.example

import com.konigsoftware.validation.KMessage

class KExample : KMessage() {
    val my_string: String by Default()
    val my_int: Int by Default()
}
