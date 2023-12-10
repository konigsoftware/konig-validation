package com.konigsoftware.validation

interface Marshaller<InMemoryType : Any, WireType : Any> {
    fun toWire(value: InMemoryType): WireType
    fun fromWire(value: WireType): InMemoryType
}
