package com.konigsoftware.validation.kasts

object StringKast : Kast<String, String>() {
    override fun kast(fromValue: String, options: List<String>): String {
        return fromValue
    }
}
