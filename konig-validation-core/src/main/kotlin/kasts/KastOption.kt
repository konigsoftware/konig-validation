package com.konigsoftware.validation.kasts

interface KastOption

inline fun <reified OutType : KastOption> Array<out KastOption>.getOption(): OutType? =
    singleOrNull { it is OutType }?.let { it as OutType }

data class WireFieldName(val name: String) : KastOption
