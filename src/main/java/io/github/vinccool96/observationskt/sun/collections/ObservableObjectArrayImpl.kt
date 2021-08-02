package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableArrayBase
import io.github.vinccool96.observationskt.collections.ObservableObjectArray
import kotlin.math.min

/**
 * [ObservableObjectArray] default implementation.
 *
 * @constructor Creates empty observable double array
 */
@Suppress("UNCHECKED_CAST")
class ObservableObjectArrayImpl<T>(override val baseArray: Array<T>) : ObservableArrayBase<ObservableObjectArray<T>>(),
        ObservableObjectArray<T> {

    private val initialArray: Array<T> = this.baseArray.copyOfRange(0, 0)

    private var array: Array<ObjectWrapper<T>?> = arrayOf()

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
    constructor(baseArray: Array<T>, src: ObservableObjectArray<T>) : this(baseArray) {
        setAll(src)
    }

    private fun arrayWith(value: T): Array<T> {
        return this.baseArray.copyOf().apply { this[0] = value }
    }

    override fun clear() {
        resize(0)
    }

    override val size: Int
        get() = this.sizeState

    private fun addAllInternal(src: Array<T>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        copyIntoWrapped(src, this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
        fireChange(length != 0, this.sizeState - length, this.sizeState)
    }

    private fun addAllInternal(src: ObservableObjectArray<T>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        growCapacity(length)
        copyIntoWrapped(src, this.array, this.sizeState, startIndex, endIndex)
        this.sizeState += length
        fireChange(length != 0, this.sizeState - length, this.sizeState)
    }

    @Suppress("UNCHECKED_CAST")
    override fun addAll(vararg elements: T) {
        addAllInternal(elements as Array<T>, 0, elements.size)
    }

    override fun addAll(src: ObservableObjectArray<T>) {
        addAllInternal(src, 0, src.size)
    }

    override fun addAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        addAllInternal(src, startIndex, endIndex)
    }

    override fun addAll(src: ObservableObjectArray<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        addAllInternal(src, startIndex, endIndex)
    }

    private fun setAllInternal(src: Array<T>, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val sizeChanged = this.size != length
        this.sizeState = 0
        ensureCapacity(endIndex)
        copyIntoWrapped(src, this.array, 0, startIndex, endIndex)
        this.sizeState = length
        fireChange(sizeChanged, 0, this.sizeState)
    }

    private fun setAllInternal(src: ObservableObjectArray<T>, startIndex: Int, endIndex: Int) {
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
            copyIntoWrapped(src, this.array, 0, startIndex, endIndex)
            this.sizeState = length
            fireChange(sizeChanged, 0, this.sizeState)
        }
    }

    override fun setAll(vararg elements: T) {
        setAllInternal(elements as Array<T>, 0, elements.size)
    }

    override fun setAll(src: ObservableObjectArray<T>) {
        setAllInternal(src, 0, src.size)
    }

    override fun setAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        setAllInternal(src, startIndex, endIndex)
    }

    override fun setAll(src: ObservableObjectArray<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        setAllInternal(src, startIndex, endIndex)
    }

    override fun set(src: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        copyIntoWrapped(src, this.array, destinationOffset, startIndex, endIndex)
        fireChange(false, destinationOffset, destinationOffset + length)
    }

    override fun set(src: ObservableObjectArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        rangeCheck(destinationOffset + length)
        copyIntoWrapped(src, this.array, destinationOffset, startIndex, endIndex)
        fireChange(false, destinationOffset, destinationOffset + length)
    }

    override operator fun get(index: Int): T {
        rangeCheck(index + 1)
        return this.array[index]!!.obj
    }

    override operator fun set(index: Int, value: T) {
        rangeCheck(index + 1)
        this.array[index]!!.obj = value
        fireChange(false, index, index + 1)
    }

    override fun toTypedArray(): Array<T> {
        var result = this.initialArray
        for (i in 0 until this.sizeState) {
            result += arrayWith(this.array[i]!!.obj)
        }
        return result
    }

    override fun toTypedArray(startIndex: Int, endIndex: Int): Array<T> {
        rangeCheck(endIndex)
        var result = this.initialArray
        for (i in startIndex until endIndex) {
            result += arrayWith(this.array[i]!!.obj)
        }
        return result
    }

    override fun copyInto(destination: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        rangeCheck(endIndex)
        this.toTypedArray().copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: ObservableObjectArray<T>, destinationOffset: Int, startIndex: Int,
            endIndex: Int) {
        rangeCheck(endIndex)
        destination.set(toTypedArray(), destinationOffset, startIndex, endIndex)
    }

    override fun resize(size: Int) {
        if (size < 0) {
            throw NegativeArraySizeException("Can't resize to negative value: $size")
        }
        ensureCapacity(size)
        val minSize = min(this.sizeState, size)
        val sizeChanged = this.sizeState != size
        this.sizeState = size
        this.array.fill(ObjectWrapper(this.baseArray[0]), minSize, this.sizeState)
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
            this.array = Array(this.sizeState) { i -> this.array[i] }
        }
    }

    private fun rangeCheck(size: Int) {
        if (size > this.sizeState) {
            throw ArrayIndexOutOfBoundsException(this.sizeState)
        }
    }

    private fun rangeCheck(src: Array<T>, startIndex: Int, endIndex: Int) {
        if (startIndex < 0 || endIndex > src.size) {
            throw ArrayIndexOutOfBoundsException(src.size)
        }
        if (endIndex < startIndex) {
            throw ArrayIndexOutOfBoundsException(endIndex)
        }
    }

    private fun rangeCheck(src: ObservableObjectArray<T>, startIndex: Int, endIndex: Int) {
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
            b.append(this.array[i]!!.obj)
            if (i != this.size - 1) {
                b.append(", ")
            }
        }
        return b.append("]").toString()
    }

    private fun copyIntoWrapped(src: Array<T>, destination: Array<ObjectWrapper<T>?>, destinationOffset: Int,
            startIndex: Int, endIndex: Int) {
        if (startIndex > endIndex || endIndex > src.size) {
            throw ArrayIndexOutOfBoundsException(startIndex)
        }
        for (i in 0 until endIndex - startIndex) {
            if (destination[i + destinationOffset] != null) {
                destination[i + destinationOffset]?.obj = src[i + startIndex]
            } else {
                destination[i + destinationOffset] = ObjectWrapper(src[i + startIndex])
            }
        }
    }

    private fun copyIntoWrapped(src: ObservableObjectArray<T>, destination: Array<ObjectWrapper<T>?>,
            destinationOffset: Int, startIndex: Int, endIndex: Int) {
        copyIntoWrapped(src.toTypedArray(), destination, destinationOffset, startIndex, endIndex)
    }

    private class ObjectWrapper<T>(var obj: T) {

        override fun toString(): String {
            return "ObjectWrapper($obj)"
        }

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