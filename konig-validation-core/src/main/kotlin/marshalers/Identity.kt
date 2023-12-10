package com.konigsoftware.validation.marshalers

import com.konigsoftware.validation.Marshaller

class IdentityMarshaller<SingleType : Any> : Marshaller<SingleType, SingleType> {
    override fun toWire(value: SingleType) = value
    override fun fromWire(value: SingleType) = value
}
