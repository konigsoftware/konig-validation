package com.konigsoftware.validation.kasts

object Int32Kast : Kast<Int, Int>() {
    override fun kast(fromValue: Int, options: List<String>): TypedKastResult<Int> {
        return TypedKastResult.Success(fromValue)
    }
}
