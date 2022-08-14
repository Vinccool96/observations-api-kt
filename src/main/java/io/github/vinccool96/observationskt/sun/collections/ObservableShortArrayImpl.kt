package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableArrayBase
import io.github.vinccool96.observationskt.collections.ObservableShortArray
import kotlin.math.min

/**
 * [ObservableShortArray] default implementation.
 *
 * @constructor Creates empty observable short array
 */
class ObservableShortArrayImpl() : ObservableArrayBase<Short>(), ObservableShortArray {

    private var array: ShortArray = INITIAL

    private var sizeState: Int = 0

    /**
     * Creates observable short array with copy of given initial values
     *
     * @param elements initial values to copy to observable short array
     */
    constructor(vararg elements: Short) : this() {
        setAll(*elements)
    }

    /**
     * Creates observable short array with copy of given short array
     *
     * @param src short array to copy
     */
    constructor(src: Array<Short>) : this() {
        setAll(*src.toShortArray())
    }

    /**
     * Creates observable short array with copy of given observable short array
     *
     * @param src observable short array to copy
     */
    constructor(src: ObservableArray<Short>) : this() {
        setAll(src)
    }

    override var size: Int
        get() = this.sizeState
        set(value) {
            this.sizeState = value
        }

    override fun addAllInternal(src: Array<Short>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.toShortArray().copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
    }

    override fun addAllInternal(src: ObservableArray<Short>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
    }

    override fun addAll(vararg elements: Short) {
        addAll(*elements.toTypedArray())
    }

    override fun addAll(src: ShortArray, startIndex: Int, endIndex: Int) {
        addAll(src.toTypedArray(), startIndex, endIndex)
    }

    override operator fun plusAssign(shorts: ShortArray) {
        addAll(*shorts.toTypedArray())
    }

    override fun setAllInternal(src: Array<Short>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        this.sizeState = 0
        ensureCapacity(endIndex)
        src.toShortArray().copyInto(this.array, 0, startIndex, endIndex)
        this.sizeState = length
    }

    override fun setAllInternal(src: ObservableArray<Short>, startIndex: Int, endIndex: Int) {
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
            src.toTypedArray().toShortArray().copyInto(this.array, 0, startIndex, endIndex)
            this.sizeState = length
        }
    }

    override fun setAll(vararg elements: Short) {
        setAll(*elements.toTypedArray())
    }

    override fun setInternal(src: Array<Short>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        src.toShortArray().copyInto(this.array, destinationOffset, startIndex, endIndex)
    }

    override fun setAll(src: ShortArray, startIndex: Int, endIndex: Int) {
        setAll(src.toTypedArray(), startIndex, endIndex)
    }

    override fun set(src: ShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        set(src.toTypedArray(), destinationOffset, startIndex, endIndex)
    }

    override fun containsAll(vararg elements: Short): Boolean {
        return elements.all { element: Short -> element in this@ObservableShortArrayImpl }
    }

    override operator fun get(index: Int): Short {
        rangeCheck(index + 1)
        return this.array[index]
    }

    override fun doOperatorSet(index: Int, value: Short) {
        rangeCheck(index + 1)
        this.array[index] = value
    }

    override fun toShortArray(): ShortArray {
        return ShortArray(this.size) { i: Int -> this.array[i] }
    }

    override fun toShortArray(startIndex: Int, endIndex: Int): ShortArray {
        val length = endIndex - startIndex
        rangeCheck(endIndex)
        return ShortArray(length) { i: Int -> this.array[i + startIndex] }
    }

    override fun toTypedArray(): Array<Short> {
        return this.toShortArray().toTypedArray()
    }

    override fun toTypedArray(startIndex: Int, endIndex: Int): Array<Short> {
        return this.toShortArray(startIndex, endIndex).toTypedArray()
    }

    override fun copyInto(destination: ShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: Array<Short>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.toTypedArray().copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: ObservableArray<Short>, destinationOffset: Int, startIndex: Int,
            endIndex: Int) {
        rangeCheck(endIndex)
        if ((destination !== this || destinationOffset != 0 || startIndex != 0 || endIndex != this.sizeState)
                && startIndex != endIndex) {
            destination.set(this.array.toTypedArray(), destinationOffset, startIndex, endIndex)
        }
    }

    override fun fillArray(fromIndex: Int, toIndex: Int) {
        this.array.fill(0, fromIndex, toIndex)
    }

    override fun internalArray(fromIndex: Int, toIndex: Int): Array<Short> {
        return this.array.toTypedArray().copyOfRange(fromIndex, toIndex)
    }

    override fun growCapacity(length: Int) {
        val minCapacity = this.sizeState + length
        val oldCapacity = this.array.size
        if (minCapacity > this.array.size) {
            var newCapacity = oldCapacity * 2
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
            this.array = copyOfArray(capacity)
        }
    }

    override fun trimToSize() {
        if (this.array.size != this.sizeState) {
            this.array = copyOfArray(this.sizeState)
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

    private fun copyOfArray(capacity: Int): ShortArray {
        var copy = this.array.copyOfRange(0, min(this.array.size, capacity))
        while (copy.size < capacity) {
            copy += if (this.array.isNotEmpty()) this.array else shortArrayOf(0)
        }
        return copy
    }

    companion object {

        private val INITIAL: ShortArray = ShortArray(0)

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