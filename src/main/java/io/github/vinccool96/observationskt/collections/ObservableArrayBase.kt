package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import io.github.vinccool96.observationskt.sun.collections.ArrayListenerHelper
import kotlin.math.min

/**
 * Abstract class that serves as a base class for [ObservableArray] implementations. The base class provides listener
 * handling functionality by implementing `addListener` and `removeListener` methods. [fireChange] method is provided
 * for notifying the listeners.
 *
 * @param T the type of the `Array` elements
 *
 * @see ObservableArray
 * @see ArrayChangeListener
 */
@Suppress("UNCHECKED_CAST")
abstract class ObservableArrayBase<T> : ObservableArray<T> {

    abstract override var size: Int
        protected set

    private var helper: ArrayListenerHelper<T>? = null

    private val changeBuilder = ArrayChangeBuilder(this)

    /**
     * Adds a new update operation to the change.
     *
     * **Note**: needs to be called inside `beginChange()` / `endChange()` block.
     *
     * **Note**: needs to reflect the *current* state of the list.
     *
     * @param pos the position in the list where the updated element resides.
     */
    protected fun nextUpdate(pos: Int) {
        this.changeBuilder.nextUpdate(pos)
    }

    /**
     * Adds a new set operation to the change. Equivalent to `nextRemove(idx); nextAdd(idx, idx + 1)`.
     *
     * **Note**: needs to be called inside `beginChange()` / `endChange()` block.
     *
     * **Note**: needs to reflect the *current* state of the list.
     *
     * @param idx the index of the item that was set
     * @param old the old value at the `idx` position.
     */
    protected fun nextSet(idx: Int, old: T) {
        this.changeBuilder.nextSet(idx, old)
    }

    /**
     * Adds a new replace operation to the change. Equivalent to `nextRemove(from, removed); nextAdd(from, to)`
     *
     * **Note**: needs to be called inside `beginChange()` / `endChange()` block.
     *
     * **Note**: needs to reflect the *current* state of the list.
     *
     * @param from the index where the items were replaced
     * @param to the end index (exclusive) of the range where the new items reside
     * @param removed the list of items that were removed
     */
    protected fun nextReplace(from: Int, to: Int, removed: MutableList<out T>) {
        this.changeBuilder.nextReplace(from, to, removed)
    }

    /**
     * Adds a new remove operation to the change with multiple items removed.
     *
     * **Note**: needs to be called inside `beginChange()` / `endChange()` block.
     *
     * **Note**: needs to reflect the *current* state of the list.
     *
     * @param idx the index where the items were removed
     * @param removed the list of items that were removed
     */
    protected fun nextRemove(idx: Int, removed: MutableList<out T>) {
        this.changeBuilder.nextRemove(idx, removed)
    }

    /**
     * Adds a new remove operation to the change with single item removed.
     *
     * **Note**: needs to be called inside `beginChange()` / `endChange()` block.
     *
     * **Note**: needs to reflect the *current* state of the list.
     *
     * @param idx the index where the item was removed
     * @param removed the item that was removed
     */
    protected fun nextRemove(idx: Int, removed: T) {
        this.changeBuilder.nextRemove(idx, removed)
    }

    /**
     * Adds a new permutation operation to the change. The permutation on index `"i"` contains the index, where the item
     * from the index `"i"` was moved.
     *
     * It's not necessary to provide the smallest permutation possible. It's correct to always call this method
     * with `nextPermutation(0, size, permutation)`
     *
     * **Note**: needs to be called inside `beginChange()` / `endChange()` block.
     *
     * **Note**: needs to reflect the *current* state of the list.
     *
     * @param from marks the beginning (inclusive) of the range that was permutated
     * @param to marks the end (exclusive) of the range that was permutated
     * @param perm the permutation in that range. Even if `from != 0`, the array should contain the indexes of the list.
     *         Therefore, such permutation would not contain indexes of range `(0, from)`
     */
    protected fun nextPermutation(from: Int, to: Int, perm: IntArray) {
        this.changeBuilder.nextPermutation(from, to, perm)
    }

    /**
     * Adds a new add operation to the change. There's no need to provide the list of added items as they can be found
     * directly in the list under the specified indexes.
     *
     * **Note**: needs to be called inside `beginChange()` / `endChange()` block.
     *
     * **Note**: needs to reflect the *current* state of the list.
     *
     * @param from marks the beginning (inclusive) of the range that was added
     * @param to marks the end (exclusive) of the range that was added
     */
    protected fun nextAdd(from: Int, to: Int) {
        this.changeBuilder.nextAdd(from, to)
    }

    /**
     * Begins a change block.
     *
     * Must be called before any of the `next*` methods is called. For every `beginChange()`, there must be a
     * corresponding [endChange] call.
     *
     * `beginChange()` calls can be nested in a `beginChange()`/`endChange()` block.
     *
     * @see endChange
     */
    protected fun beginChange() {
        this.changeBuilder.beginChange()
    }

    /**
     * Ends the change block.
     *
     * If the block is the outer-most block for the `ObservableList`, the `Change` is constructed and all listeners are
     * notified.
     *
     * Ending a nested block doesn't fire a notification.
     *
     * @see beginChange
     */
    protected fun endChange() {
        this.changeBuilder.endChange()
    }

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.helper = ArrayListenerHelper.addListener(this.helper, this as ObservableArray<T>, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.helper = ArrayListenerHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ArrayChangeListener<in T>) {
        if (!hasListener(listener)) {
            this.helper = ArrayListenerHelper.addListener(this.helper, this as ObservableArray<T>, listener)
        }
    }

    override fun removeListener(listener: ArrayChangeListener<in T>) {
        if (hasListener(listener)) {
            this.helper = ArrayListenerHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ArrayChangeListener<in T>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.arrayChangeListeners.contains(listener)
    }

    internal fun fireChanges(change: Change<out T>) {
        fireChange(change)
    }

    /**
     * Notifies all listeners of a change.
     *
     * You surely also want an `internal` method to access this method.
     *
     * @param change the change to be fired
     */
    protected fun fireChange(change: Change<out T>) {
        ArrayListenerHelper.fireValueChangedEvent(this.helper, change)
    }

    override fun resize(size: Int) {
        if (size < 0) {
            throw NegativeArraySizeException("Can't resize to negative value: $size")
        }
        if (this.size != size) {
            try {
                beginChange()
                ensureCapacity(size)
                val minSize = min(this.size, size)
                val oldSize = this.size
                this.size = size
                if (oldSize > this.size) {
                    val removedList = mutableListOf(this.baseArray[0])
                    removedList.clear()
                    val removedArray = this.internalArray(size, oldSize)
                    for (e in removedArray) {
                        removedList.add(e)
                    }
                    nextRemove(size, removedList)
                } else {
                    nextAdd(oldSize, this.size)
                }
                fillArray(minSize, this.size)
            } finally {
                endChange()
            }
        }
    }

    override fun clear() {
        resize(0)
    }

    override fun isEmpty(): Boolean {
        return this.size == 0
    }

    override operator fun set(index: Int, value: T) {
        try {
            beginChange()
            val old = this[index]
            doOperatorSet(index, value)
            nextSet(index, old)
        } finally {
            endChange()
        }
    }

    override operator fun contains(element: T): Boolean {
        return element in this.toTypedArray()
    }

    override fun containsAll(elements: ObservableArray<T>): Boolean {
        return elements.all { element: T -> element in this@ObservableArrayBase }
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { element: T -> element in this@ObservableArrayBase }
    }

    override fun containsAll(vararg elements: T): Boolean {
        return elements.all { element: T -> element in this@ObservableArrayBase }
    }

    override fun addAll(vararg elements: T) {
        if (elements.isNotEmpty()) {
            try {
                beginChange()
                val start = this.size
                addAllInternal(elements as Array<T>, 0, elements.size)
                nextAdd(start, this.size)
            } finally {
                endChange()
            }
        }
    }

    override fun addAll(src: ObservableArray<T>) {
        if (src.size != 0) {
            try {
                beginChange()
                val start = this.size
                addAllInternal(src, 0, src.size)
                nextAdd(start, this.size)
            } finally {
                endChange()
            }
        }
    }

    override fun addAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        if (endIndex - startIndex != 0) {
            try {
                beginChange()
                val start = this.size
                addAllInternal(src, startIndex, endIndex)
                nextAdd(start, this.size)
            } finally {
                endChange()
            }
        }
    }

    override fun addAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        if (endIndex - startIndex != 0) {
            try {
                beginChange()
                val start = this.size
                addAllInternal(src, startIndex, endIndex)
                nextAdd(start, this.size)
            } finally {
                endChange()
            }
        }
    }

    override fun setAll(vararg elements: T) {
        beginChange()
        try {
            clear()
            addAll(*elements)
        } finally {
            endChange()
        }
    }

    override fun setAll(src: ObservableArray<T>) {
        if (src === this) {
            setAll(*src.toTypedArray())
            return
        }
        beginChange()
        try {
            clear()
            addAll(src)
        } finally {
            endChange()
        }
    }

    override fun setAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        if (!(this.size == 0 && endIndex - startIndex == 0)) {
            rangeCheck(src, startIndex, endIndex)
            try {
                beginChange()
                clear()
                addAll(*src.copyOfRange(startIndex, endIndex))
            } finally {
                endChange()
            }
        }
    }

    override fun setAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src.toTypedArray(), startIndex, endIndex)
        try {
            beginChange()
            val before = src.toTypedArray()
            clear()
            addAll(*before.copyOfRange(startIndex, endIndex))
        } finally {
            endChange()
        }
    }

    override fun set(src: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        require(endIndex >= startIndex)
        if (!(this.size == 0 && endIndex - startIndex == 0)) {
            try {
                beginChange()
                val length = endIndex - startIndex
                val removed = this.toTypedArray(destinationOffset, destinationOffset + length).toMutableList()
                setInternal(src, destinationOffset, startIndex, endIndex)
                nextReplace(destinationOffset, destinationOffset + length, removed)
            } finally {
                endChange()
            }
        }
    }

    override fun set(src: ObservableArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        set(src.toTypedArray(), destinationOffset, startIndex, endIndex)
    }

    override operator fun iterator(): Iterator<T> {
        return object : Iterator<T> {

            private val iterator = this@ObservableArrayBase.toTypedArray().iterator()

            override fun hasNext(): Boolean {
                return this.iterator.hasNext()
            }

            override fun next(): T {
                return this.iterator.next()
            }

        }
    }

    protected abstract fun growCapacity(length: Int)

    protected abstract fun addAllInternal(src: Array<T>, startIndex: Int, endIndex: Int)

    protected open fun addAllInternal(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        throw NotImplementedError()
    }

    protected abstract fun setAllInternal(src: Array<T>, startIndex: Int, endIndex: Int)

    protected abstract fun setAllInternal(src: ObservableArray<T>, startIndex: Int, endIndex: Int)

    protected abstract fun doOperatorSet(index: Int, value: T)

    protected abstract fun setInternal(src: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int)

    protected open fun fillArray(fromIndex: Int, toIndex: Int) {
        throw NotImplementedError()
    }

    protected open fun internalArray(fromIndex: Int, toIndex: Int): Array<T> {
        throw NotImplementedError()
    }

    protected fun rangeCheck(size: Int) {
        if (size > this.size) {
            throw ArrayIndexOutOfBoundsException(this.size)
        }
    }

    protected fun rangeCheck(src: Array<T>, startIndex: Int, endIndex: Int) {
        if (startIndex < 0 || endIndex > src.size) {
            throw ArrayIndexOutOfBoundsException(src.size)
        }
        if (endIndex < startIndex) {
            throw ArrayIndexOutOfBoundsException(endIndex)
        }
    }

    protected fun rangeCheck(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        if (startIndex < 0 || endIndex > src.size) {
            throw ArrayIndexOutOfBoundsException(src.size)
        }
        if (endIndex < startIndex) {
            throw ArrayIndexOutOfBoundsException(endIndex)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (super.equals(other)) {
            return true
        }
        if (other == null) {
            return false
        }
        if (other !is ObservableArray<*>) {
            return false
        }
        val ob = other as ObservableArray<T>
        return this.containsAll(ob) && ob.containsAll(this)
    }

    override fun hashCode(): Int {
        var hash = 0
        for (e in this) {
            hash = 31 * hash + (e?.hashCode() ?: 0)
        }
        return hash
    }

}