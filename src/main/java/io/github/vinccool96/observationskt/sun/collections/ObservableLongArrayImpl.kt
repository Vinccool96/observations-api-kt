package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableArrayBase
import io.github.vinccool96.observationskt.collections.ObservableLongArray
import kotlin.math.min

/**
 * [ObservableLongArray] default implementation.
 *
 * @constructor Creates empty observable long array
 */
class ObservableLongArrayImpl() : ObservableArrayBase<Long>(), ObservableLongArray {

    private var array: LongArray = INITIAL

    private var sizeState: Int = 0

    /**
     * Creates observable long array with copy of given initial values
     *
     * @param elements initial values to copy to observable long array
     */
    constructor(vararg elements: Long) : this() {
        setAll(*elements)
    }

    /**
     * Creates observable long array with copy of given long array
     *
     * @param src long array to copy
     */
    constructor(src: Array<Long>) : this() {
        setAll(*src.toLongArray())
    }

    /**
     * Creates observable long array with copy of given observable long array
     *
     * @param src observable long array to copy
     */
    constructor(src: ObservableArray<Long>) : this() {
        setAll(src)
    }

    override val size: Int
        get() = this.sizeState

    override fun addAllInternal(src: Array<Long>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.toLongArray().copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
        fireChange(length != 0, this.sizeState - length, this.sizeState)
    }

    override fun addAll(vararg elements: Long) {
        addAll(*elements.toTypedArray())
    }

    override fun addAll(src: LongArray, startIndex: Int, endIndex: Int) {
        addAll(src.toTypedArray(), startIndex, endIndex)
    }

    override operator fun plusAssign(longs: LongArray) {
        addAll(*longs.toTypedArray())
    }

    override fun setAllInternal(src: Array<Long>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val sizeChanged = this.size != length
        this.sizeState = 0
        ensureCapacity(endIndex)
        src.toLongArray().copyInto(this.array, 0, startIndex, endIndex)
        this.sizeState = length
        fireChange(sizeChanged, 0, this.sizeState)
    }

    override fun setAllInternal(src: ObservableArray<Long>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val sizeChanged = this.size != length
        if (src === this) {
            if (startIndex == 0) {
                resize(length)
            } else {
                this.array.copyInto(this.array, 0, startIndex, endIndex)
                this.sizeState = length
                fireChange(sizeChanged, 0, this.sizeState)
            }
        } else {
            ensureCapacity(length)
            src.toTypedArray().toLongArray().copyInto(this.array, 0, startIndex, endIndex)
            this.sizeState = length
            fireChange(sizeChanged, 0, this.sizeState)
        }
    }

    override fun setAll(vararg elements: Long) {
        setAll(*elements.toTypedArray())
    }

    override fun setAll(src: LongArray, startIndex: Int, endIndex: Int) {
        setAll(src.toTypedArray(), startIndex, endIndex)
    }

    override fun set(src: LongArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        src.copyInto(this.array, destinationOffset, startIndex, endIndex)
        fireChange(false, destinationOffset, destinationOffset + length)
    }

    override fun set(src: Array<Long>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        src.toLongArray().copyInto(this.array, destinationOffset, startIndex, endIndex)
        fireChange(false, destinationOffset, destinationOffset + length)
    }

    override operator fun get(index: Int): Long {
        rangeCheck(index + 1)
        return this.array[index]
    }

    override operator fun set(index: Int, value: Long) {
        rangeCheck(index + 1)
        this.array[index] = value
        fireChange(false, index, index + 1)
    }

    override fun toLongArray(): LongArray {
        return LongArray(this.size) { i: Int -> this.array[i] }
    }

    override fun toLongArray(startIndex: Int, endIndex: Int): LongArray {
        val length = endIndex - startIndex
        rangeCheck(endIndex)
        return LongArray(length) { i: Int -> this.array[i + startIndex] }
    }

    override fun toTypedArray(): Array<Long> {
        return this.toLongArray().toTypedArray()
    }

    override fun toTypedArray(startIndex: Int, endIndex: Int): Array<Long> {
        return this.toLongArray(startIndex, endIndex).toTypedArray()
    }

    override fun copyInto(destination: LongArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: Array<Long>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.toTypedArray().copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: ObservableArray<Long>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        destination.set(this.array.toTypedArray(), destinationOffset, startIndex, endIndex)
    }

    override fun resize(size: Int) {
        if (size < 0) {
            throw NegativeArraySizeException("Can't resize to negative value: $size")
        }
        ensureCapacity(size)
        val minSize = min(this.sizeState, size)
        val sizeChanged = this.sizeState != size
        this.sizeState = size
        this.array.fill(0L, minSize, this.sizeState)
        fireChange(sizeChanged, minSize, size)
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
            this.array = LongArray(this.size) { i: Int -> this.array[i] }
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

        private val INITIAL: LongArray = LongArray(0)

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