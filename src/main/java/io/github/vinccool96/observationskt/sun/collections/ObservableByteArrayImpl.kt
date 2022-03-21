package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableArrayBase
import io.github.vinccool96.observationskt.collections.ObservableByteArray
import kotlin.math.min

/**
 * [ObservableByteArray] default implementation.
 *
 * @constructor Creates empty observable byte array
 */
class ObservableByteArrayImpl() : ObservableArrayBase<Byte>(), ObservableByteArray {

    private var array: ByteArray = INITIAL

    private var sizeState: Int = 0

    /**
     * Creates observable byte array with copy of given initial values
     *
     * @param elements initial values to copy to observable byte array
     */
    constructor(vararg elements: Byte) : this() {
        setAll(*elements)
    }

    /**
     * Creates observable byte array with copy of given byte array
     *
     * @param src byte array to copy
     */
    constructor(src: Array<Byte>) : this() {
        setAll(*src.toByteArray())
    }

    /**
     * Creates observable byte array with copy of given observable byte array
     *
     * @param src observable byte array to copy
     */
    constructor(src: ObservableArray<Byte>) : this() {
        setAll(src)
    }

    override val size: Int
        get() = this.sizeState

    override fun addAllInternal(src: Array<Byte>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.toByteArray().copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
    }

    override fun addAll(vararg elements: Byte) {
        addAll(*elements.toTypedArray())
    }

    override fun addAll(src: ByteArray, startIndex: Int, endIndex: Int) {
        addAll(src.toTypedArray(), startIndex, endIndex)
    }

    override operator fun plusAssign(bytes: ByteArray) {
        addAll(*bytes.toTypedArray())
    }

    override fun setAllInternal(src: Array<Byte>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        this.sizeState = 0
        ensureCapacity(endIndex)
        src.toByteArray().copyInto(this.array, 0, startIndex, endIndex)
        this.sizeState = length
    }

    override fun setAllInternal(src: ObservableArray<Byte>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        if (src === this) {
            if (startIndex == 0) {
                resize(length)
            } else {
                this.array.copyInto(this.array, 0, startIndex, endIndex)
                this.sizeState = length
            }
        } else {
            ensureCapacity(length)
            src.toTypedArray().toByteArray().copyInto(this.array, 0, startIndex, endIndex)
            this.sizeState = length
        }
    }

    override fun setAll(vararg elements: Byte) {
        setAll(*elements.toTypedArray())
    }

    override fun setInternal(src: Array<Byte>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        src.toByteArray().copyInto(this.array, destinationOffset, startIndex, endIndex)
    }

    override fun setAll(src: ByteArray, startIndex: Int, endIndex: Int) {
        setAll(src.toTypedArray(), startIndex, endIndex)
    }

    override fun set(src: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        set(src.toTypedArray(), destinationOffset, startIndex, endIndex)
    }

    override operator fun get(index: Int): Byte {
        rangeCheck(index + 1)
        return this.array[index]
    }

    override fun doOperatorSet(index: Int, value: Byte) {
        rangeCheck(index + 1)
        this.array[index] = value
    }

    override fun toByteArray(): ByteArray {
        return ByteArray(this.size) { i: Int -> this.array[i] }
    }

    override fun toByteArray(startIndex: Int, endIndex: Int): ByteArray {
        val length = endIndex - startIndex
        rangeCheck(endIndex)
        return ByteArray(length) { i: Int -> this.array[i + startIndex] }
    }

    override fun toTypedArray(): Array<Byte> {
        return this.toByteArray().toTypedArray()
    }

    override fun toTypedArray(startIndex: Int, endIndex: Int): Array<Byte> {
        return this.toByteArray(startIndex, endIndex).toTypedArray()
    }

    override fun copyInto(destination: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: Array<Byte>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.toTypedArray().copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: ObservableArray<Byte>, destinationOffset: Int, startIndex: Int,
            endIndex: Int) {
        rangeCheck(endIndex)
        destination.set(this.array.toTypedArray(), destinationOffset, startIndex, endIndex)
    }

    override fun resize(size: Int) {
        if (size < 0) {
            throw NegativeArraySizeException("Can't resize to negative value: $size")
        }
        try {
            beginChange()
            ensureCapacity(size)
            val minSize = min(this.sizeState, size)
            this.sizeState = size
            val removed = this.array.copyOfRange(size, this.array.size).toMutableList()
            this.array.fill(0, minSize, this.sizeState)
            nextRemove(size, removed)
        } finally {
            endChange()
        }
    }

    override fun growCapacity(length: Int) {
        val minCapacity = this.sizeState + length
        val oldCapacity = this.array.size
        if (minCapacity > this.array.size) {
            var newCapacity = oldCapacity + (oldCapacity shr 1)
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity
            }
            if (newCapacity > MAX_ARRAY_SIZE) {
                newCapacity = hugeCapacity(minCapacity)
            }
            ensureCapacity(newCapacity)
        } else if (length > 0 && minCapacity < 0) {
            throw OutOfMemoryError() // overflow
        }
    }

    override fun ensureCapacity(capacity: Int) {
        if (this.array.size < capacity) {
            this.array = this.array.copyOf(capacity)
        }
    }

    override fun trimToSize() {
        if (this.array.size != this.sizeState) {
            this.array = ByteArray(this.size) { i: Int -> this.array[i] }
        }
    }

    override fun toString(): String {
        if (this.size == 0) {
            return "[]"
        }
        val b = StringBuilder("[")
        for (i in 0 until this.size) {
            b.append(this.array[i])
            if (i != this.size - 1) {
                b.append(", ")
            }
        }
        return b.append("]").toString()
    }

    companion object {

        private val INITIAL: ByteArray = ByteArray(0)

        /**
         * The maximum size of array to allocate. Some VMs reserve some header words in an array. Attempts to allocate
         * larger arrays may result in [OutOfMemoryError]: Requested array size exceeds VM limit
         */
        private const val MAX_ARRAY_SIZE: Int = Int.MAX_VALUE - 8

        private fun hugeCapacity(minCapacity: Int): Int {
            if (minCapacity < 0) { // overflow
                throw OutOfMemoryError()
            }
            return if (minCapacity > MAX_ARRAY_SIZE) Int.MAX_VALUE else MAX_ARRAY_SIZE
        }

    }

}