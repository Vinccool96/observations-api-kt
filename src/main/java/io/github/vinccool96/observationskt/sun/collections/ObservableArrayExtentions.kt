package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableArray

internal fun ObservableArray<Boolean>.copyInto(destination: BooleanArray, destinationOffset: Int = 0,
        startIndex: Int = 0, endIndex: Int = this.size) {
    this.toTypedArray().toBooleanArray().copyInto(destination, destinationOffset, startIndex, endIndex)
}

internal fun ObservableArray<Double>.copyInto(destination: DoubleArray, destinationOffset: Int = 0,
        startIndex: Int = 0, endIndex: Int = this.size) {
    this.toTypedArray().toDoubleArray().copyInto(destination, destinationOffset, startIndex, endIndex)
}

internal fun ObservableArray<Float>.copyInto(destination: FloatArray, destinationOffset: Int = 0,
        startIndex: Int = 0, endIndex: Int = this.size) {
    this.toTypedArray().toFloatArray().copyInto(destination, destinationOffset, startIndex, endIndex)
}

internal fun ObservableArray<Int>.copyInto(destination: IntArray, destinationOffset: Int = 0,
        startIndex: Int = 0, endIndex: Int = this.size) {
    this.toTypedArray().toIntArray().copyInto(destination, destinationOffset, startIndex, endIndex)
}

internal fun ObservableArray<Long>.copyInto(destination: LongArray, destinationOffset: Int = 0,
        startIndex: Int = 0, endIndex: Int = this.size) {
    this.toTypedArray().toLongArray().copyInto(destination, destinationOffset, startIndex, endIndex)
}

internal fun ObservableArray<Short>.copyInto(destination: ShortArray, destinationOffset: Int = 0,
        startIndex: Int = 0, endIndex: Int = this.size) {
    this.toTypedArray().toShortArray().copyInto(destination, destinationOffset, startIndex, endIndex)
}

internal fun ObservableArray<Byte>.copyInto(destination: ByteArray, destinationOffset: Int = 0,
        startIndex: Int = 0, endIndex: Int = this.size) {
    this.toTypedArray().toByteArray().copyInto(destination, destinationOffset, startIndex, endIndex)
}