package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableArrayBase
import io.github.vinccool96.observationskt.collections.ObservableObjectArray
import kotlin.math.min

/**
 * [ObservableObjectArray] default implementation.
 *
 * @constructor Creates empty observable double array
 */
@Suppress("UNCHECKED_CAST")
class ObservableObjectArrayImpl<T>(baseArray: Array<T>) : ObservableArrayBase<T>(),
        ObservableObjectArray<T> {

    private val internalBaseArray = baseArray

    override val baseArray: Array<T>
        get() = this.internalBaseArray.copyOf()

    private val initialArray: Array<T> = this.baseArray.copyOfRange(0, 0)

    private var array: Array<T> = this.initialArray

    private var sizeState: Int = 0

    init {
        if (this.baseArray.size != 1) {
            throw IllegalArgumentException("baseArray must be of size 1, it was provided with a size of " +
                    this.baseArray.size)
        }
    }

    /**
     * Creates observable double array with copy of given initial values
     *
     * @param baseArray the base array of size `1` containing the base element
     * @param elements initial values to copy to observable double array
     */
    constructor(baseArray: Array<T>, vararg elements: T) : this(baseArray) {
        setAll(*elements)
    }

    /**
     * Creates observable double array with copy of given observable double array
     *
     * @param src observable double array to copy
     */
    constructor(baseArray: Array<T>, src: ObservableArray<T>) : this(baseArray) {
        setAll(src)
    }

    private fun arrayWith(value: T): Array<T> {
        return this.baseArray.apply { this[0] = value }
    }

    override var size: Int
        get() = this.sizeState
        set(value) {
            this.sizeState = value
        }

    override fun addAllInternal(src: Array<T>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
    }

    override fun addAllInternal(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        src.copyInto(this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
    }

    override fun setAllInternal(src: Array<T>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        this.sizeState = 0
        ensureCapacity(endIndex)
        src.copyInto(this.array, 0, startIndex, endIndex)
        this.sizeState = length
    }

    override fun setAllInternal(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
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
            src.toTypedArray().copyInto(this.array, 0, startIndex, endIndex)
            this.sizeState = length
        }
    }

    override fun setInternal(src: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        src.copyInto(this.array, destinationOffset, startIndex, endIndex)
    }

    override operator fun get(index: Int): T {
        rangeCheck(index + 1)
        return this.array[index]
    }

    override fun doOperatorSet(index: Int, value: T) {
        rangeCheck(index + 1)
        this.array[index] = value
    }

    override fun toTypedArray(): Array<T> {
        var result = this.initialArray
        for (i in 0 until this.sizeState) {
            result += arrayWith(this.array[i])
        }
        return result
    }

    override fun toTypedArray(startIndex: Int, endIndex: Int): Array<T> {
        rangeCheck(endIndex)
        var result = this.initialArray
        for (i in startIndex until endIndex) {
            result += arrayWith(this.array[i])
        }
        return result
    }

    override fun copyInto(destination: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.array.copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: ObservableArray<T>, destinationOffset: Int, startIndex: Int,
            endIndex: Int) {
        rangeCheck(endIndex)
        destination.set(this.array, destinationOffset, startIndex, endIndex)
    }

    override fun fillArray(fromIndex: Int, toIndex: Int) {
        this.array.fill(this.baseArray[0], fromIndex, toIndex)
    }

    override fun internalArray(fromIndex: Int, toIndex: Int): Array<T> {
        return this.array.copyOfRange(fromIndex, toIndex)
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

    private fun copyOfArray(capacity: Int): Array<T> {
        var copy = this.array.copyOfRange(0, min(this.array.size, capacity))
        while (copy.size < capacity) {
            copy += if (this.array.isNotEmpty()) this.array else this.baseArray
        }
        return copy
    }

    companion object {

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