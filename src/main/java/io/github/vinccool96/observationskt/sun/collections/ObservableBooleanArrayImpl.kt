package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableArrayBase
import io.github.vinccool96.observationskt.collections.ObservableBooleanArray
import kotlin.math.min

/**
 * [ObservableBooleanArray] default implementation.
 *
 * @constructor Creates empty observable boolean array
 */
class ObservableBooleanArrayImpl() : ObservableArrayBase<ObservableBooleanArray>(), ObservableBooleanArray {

    private var array: BooleanArray = INITIAL

    private var sizeState: Int = 0

    /**
     * Creates observable boolean array with copy of given initial values
     *
     * @param elements initial values to copy to observable boolean array
     */
    constructor(vararg elements: Boolean) : this() {
        setAll(*elements)
    }

    /**
     * Creates observable boolean array with copy of given boolean array
     *
     * @param src boolean array to copy
     */
    constructor(src: Array<Boolean>) : this() {
        setAll(*src.toBooleanArray())
    }

    /**
     * Creates observable boolean array with copy of given observable boolean array
     *
     * @param src observable boolean array to copy
     */
    constructor(src: ObservableBooleanArray) : this() {
        setAll(src)
    }

    override fun clear() {
        resize(0)
    }

    override val size: Int
        get() = this.sizeState

    private fun addAllInternal(src: BooleanArray, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
        fireChange(length != 0, this.sizeState - length, this.sizeState)
    }

    private fun addAllInternal(src: ObservableBooleanArray, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
        fireChange(length != 0, this.sizeState - length, this.sizeState)
    }

    override fun addAll(vararg elements: Boolean) {
        addAllInternal(elements, 0, elements.size)
    }

    override fun addAll(src: ObservableBooleanArray) {
        addAllInternal(src, 0, src.size)
    }

    override fun addAll(src: BooleanArray, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        addAllInternal(src, startIndex, endIndex)
    }

    override fun addAll(src: ObservableBooleanArray, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        addAllInternal(src, startIndex, endIndex)
    }

    private fun setAllInternal(src: BooleanArray, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val sizeChanged = this.size != length
        this.sizeState = 0
        ensureCapacity(endIndex)
        src.copyInto(this.array, 0, startIndex, endIndex)
        this.sizeState = length
        fireChange(sizeChanged, 0, this.sizeState)
    }

    private fun setAllInternal(src: ObservableBooleanArray, startIndex: Int, endIndex: Int) {
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
            src.copyInto(this.array, 0, startIndex, endIndex)
            this.sizeState = length
            fireChange(sizeChanged, 0, this.sizeState)
        }
    }

    override fun setAll(vararg elements: Boolean) {
        setAllInternal(elements, 0, elements.size)
    }

    override fun setAll(src: ObservableBooleanArray) {
        setAllInternal(src, 0, src.size)
    }

    override fun setAll(src: BooleanArray, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        setAllInternal(src, startIndex, endIndex)
    }

    override fun setAll(src: ObservableBooleanArray, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        setAllInternal(src, startIndex, endIndex)
    }

    override fun set(src: BooleanArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        src.copyInto(this.array, destinationOffset, startIndex, endIndex)
        fireChange(false, destinationOffset, destinationOffset + length)
    }

    override fun set(src: ObservableBooleanArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        src.copyInto(this.array, destinationOffset, startIndex, endIndex)
        fireChange(false, destinationOffset, destinationOffset + length)
    }

    override operator fun get(index: Int): Boolean {
        rangeCheck(index + 1)
        return this.array[index]
    }

    override operator fun set(index: Int, value: Boolean) {
        rangeCheck(index + 1)
        this.array[index] = value
        fireChange(false, index, index + 1)
    }

    override fun toBooleanArray(): BooleanArray {
        return BooleanArray(this.size) { i: Int -> this.array[i] }
    }

    override fun toBooleanArray(startIndex: Int, endIndex: Int): BooleanArray {
        val length = endIndex - startIndex
        rangeCheck(endIndex)
        return BooleanArray(length) { i: Int -> this.array[i + startIndex] }
    }

    override fun copyInto(destination: BooleanArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: Array<Boolean>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.toTypedArray().copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: ObservableBooleanArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        destination.set(this.array, destinationOffset, startIndex, endIndex)
    }

    override fun resize(size: Int) {
        if (size < 0) {
            throw NegativeArraySizeException("Can't resize to negative value: $size")
        }
        ensureCapacity(size)
        val minSize = min(this.sizeState, size)
        val sizeChanged = this.sizeState != size
        this.sizeState = size
        this.array.fill(false, minSize, this.sizeState)
        fireChange(sizeChanged, minSize, size)
    }

    private fun growCapacity(length: Int) {
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
            this.array = BooleanArray(this.size) { i: Int -> this.array[i] }
        }
    }

    private fun rangeCheck(size: Int) {
        if (size > this.sizeState) {
            throw ArrayIndexOutOfBoundsException(this.sizeState)
        }
    }

    private fun rangeCheck(src: BooleanArray, startIndex: Int, endIndex: Int) {
        if (startIndex < 0 || endIndex > src.size) {
            throw ArrayIndexOutOfBoundsException(src.size)
        }
        if (endIndex < startIndex) {
            throw ArrayIndexOutOfBoundsException(endIndex)
        }
    }

    private fun rangeCheck(src: ObservableBooleanArray, startIndex: Int, endIndex: Int) {
        if (startIndex < 0 || endIndex > src.size) {
            throw ArrayIndexOutOfBoundsException(src.size)
        }
        if (endIndex < startIndex) {
            throw ArrayIndexOutOfBoundsException(endIndex)
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

        private val INITIAL: BooleanArray = BooleanArray(0)

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