package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableArrayBase
import io.github.vinccool96.observationskt.collections.ObservableDoubleArray
import kotlin.math.min

/**
 * [ObservableDoubleArray] default implementation.
 *
 * @constructor Creates empty observable double array
 */
class ObservableDoubleArrayImpl() : ObservableArrayBase<ObservableDoubleArray>(), ObservableDoubleArray {

    private var array: DoubleArray = INITIAL

    private var sizeState: Int = 0

    /**
     * Creates observable double array with copy of given initial values
     *
     * @param elements initial values to copy to observable double array
     */
    constructor(vararg elements: Double) : this() {
        setAll(*elements)
    }

    /**
     * Creates observable double array with copy of given double array
     *
     * @param src double array to copy
     */
    constructor(src: Array<Double>) : this() {
        setAll(*src.toDoubleArray())
    }

    /**
     * Creates observable double array with copy of given observable double array
     *
     * @param src observable double array to copy
     */
    constructor(src: ObservableDoubleArray) : this() {
        setAll(src)
    }

    override fun clear() {
        resize(0)
    }

    override val size: Int
        get() = this.sizeState

    private fun addAllInternal(src: DoubleArray, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
        fireChange(length != 0, this.sizeState - length, this.sizeState)
    }

    private fun addAllInternal(src: ObservableDoubleArray, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
        fireChange(length != 0, this.sizeState - length, this.sizeState)
    }

    override fun addAll(vararg elements: Double) {
        addAllInternal(elements, 0, elements.size)
    }

    override fun addAll(src: ObservableDoubleArray) {
        addAllInternal(src, 0, src.size)
    }

    override fun addAll(src: DoubleArray, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        addAllInternal(src, startIndex, endIndex)
    }

    override fun addAll(src: ObservableDoubleArray, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        addAllInternal(src, startIndex, endIndex)
    }

    private fun setAllInternal(src: DoubleArray, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val sizeChanged = this.size != length
        this.sizeState = 0
        ensureCapacity(endIndex)
        src.copyInto(this.array, 0, startIndex, endIndex)
        this.sizeState = length
        fireChange(sizeChanged, 0, this.sizeState)
    }

    private fun setAllInternal(src: ObservableDoubleArray, startIndex: Int, endIndex: Int) {
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

    override fun setAll(vararg elements: Double) {
        setAllInternal(elements, 0, elements.size)
    }

    override fun setAll(src: ObservableDoubleArray) {
        setAllInternal(src, 0, src.size)
    }

    override fun setAll(src: DoubleArray, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        setAllInternal(src, startIndex, endIndex)
    }

    override fun setAll(src: ObservableDoubleArray, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        setAllInternal(src, startIndex, endIndex)
    }

    override fun set(src: DoubleArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        src.copyInto(this.array, destinationOffset, startIndex, endIndex)
        fireChange(false, destinationOffset, destinationOffset + length)
    }

    override fun set(src: ObservableDoubleArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        src.copyInto(this.array, destinationOffset, startIndex, endIndex)
        fireChange(false, destinationOffset, destinationOffset + length)
    }

    override operator fun get(index: Int): Double {
        rangeCheck(index + 1)
        return this.array[index]
    }

    override operator fun set(index: Int, value: Double) {
        rangeCheck(index + 1)
        this.array[index] = value
        fireChange(false, index, index + 1)
    }

    override fun toDoubleArray(): DoubleArray {
        return DoubleArray(this.size) { i: Int -> this.array[i] }
    }

    override fun toDoubleArray(startIndex: Int, endIndex: Int): DoubleArray {
        val length = endIndex - startIndex
        rangeCheck(endIndex)
        return DoubleArray(length) { i: Int -> this.array[i + startIndex] }
    }

    override fun copyInto(destination: DoubleArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: Array<Double>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.toTypedArray().copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: ObservableDoubleArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
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
        this.array.fill(0.0, minSize, this.sizeState)
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
            this.array = DoubleArray(this.size) { i: Int -> this.array[i] }
        }
    }

    private fun rangeCheck(size: Int) {
        if (size > this.sizeState) {
            throw ArrayIndexOutOfBoundsException(this.sizeState)
        }
    }

    private fun rangeCheck(src: DoubleArray, startIndex: Int, endIndex: Int) {
        if (startIndex < 0 || endIndex > src.size) {
            throw ArrayIndexOutOfBoundsException(src.size)
        }
        if (endIndex < startIndex) {
            throw ArrayIndexOutOfBoundsException(endIndex)
        }
    }

    private fun rangeCheck(src: ObservableDoubleArray, startIndex: Int, endIndex: Int) {
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

        private val INITIAL: DoubleArray = DoubleArray(0)

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