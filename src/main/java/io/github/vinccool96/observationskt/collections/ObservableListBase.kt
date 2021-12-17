package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.sun.collections.ListListenerHelper

/**
 * Abstract class that serves as a base class for [ObservableList] implementations. The base class provides two
 * functionalities for the implementing classes.
 *
 * * Listener handling by implementing `addListener` and `removeListener` methods. [fireChange] method is provided for
 * notifying the listeners with a `Change` object.
 *
 * * Methods for building up a [Change] object. There are various methods called `next*`, like [nextAdd] for new items
 * in the lists or [nextRemove] for an item being removed from the list. **These methods must be always enclosed in
 * [beginChange] and [endChange] block.** See the example below.
 *
 * The following example shows how the Change build-up works:
 *
 * ```
 * fun removeOddIndexes() {
 *     beginChange()
 *     try {
 *         for (i in 1 until size) {
 *             remove(i)
 *         }
 *     } finally {
 *         endChange()
 *     }
 * }
 *
 * fun remove(i: Int) {
 *     beginChange()
 *     try {
 *         val removed: E = ... //do some stuff that will actually remove the element at index i
 *         nextRemove(i, removed)
 *     } finally {
 *         endChange()
 *     }
 * }
 * ```
 *
 * The `try`/`finally` blocks in the example are needed only if there's a possibility for an exception to occur
 * inside a `beginChange` / `endChange` block
 *
 * Note: If you want to create modifiable [ObservableList] implementation, consider using [ModifiableObservableListBase]
 * as a superclass.
 *
 * Note: In order to create list with sequential access, you should override [listIterator], [iterator] methods and use
 * them in [get], [size] and other methods accordingly.
 *
 * @param E the type of the elements contained in the List
 *
 * @see ObservableList
 * @see Change
 * @see ModifiableObservableListBase
 */
abstract class ObservableListBase<E> : AbstractMutableList<E>(), ObservableList<E> {

    private var helper: ListListenerHelper<E>? = null

    private val changeBuilder: ListChangeBuilder<E> = ListChangeBuilder(this)

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
    protected fun nextSet(idx: Int, old: E) {
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
    protected fun nextReplace(from: Int, to: Int, removed: MutableList<out E>) {
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
    protected fun nextRemove(idx: Int, removed: MutableList<out E>) {
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
    protected fun nextRemove(idx: Int, removed: E) {
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
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ListListenerHelper.addListener(this.helper, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ListListenerHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ListChangeListener<in E>) {
        if (!isListChangeListenerAlreadyAdded(listener)) {
            this.helper = ListListenerHelper.addListener(this.helper, listener)
        }
    }

    override fun removeListener(listener: ListChangeListener<in E>) {
        if (isListChangeListenerAlreadyAdded(listener)) {
            this.helper = ListListenerHelper.removeListener(this.helper, listener)
        }
    }

    override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in E>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.listChangeListeners.contains(listener)
    }

    internal fun fireChanges(change: Change<out E>) {
        fireChange(change)
    }

    /**
     * Notifies all listeners of a change.
     *
     * You surely also want an `internal` method to access this method.
     *
     * @param change the change to be fired
     */
    protected fun fireChange(change: Change<out E>) {
        ListListenerHelper.fireValueChangedEvent(this.helper, change)
    }

    protected val hasListeners: Boolean
        get() = ListListenerHelper.hasListeners(this.helper)

    override fun addAll(vararg elements: E): Boolean {
        return addAll(elements.asList())
    }

    override fun setAll(vararg elements: E): Boolean {
        return setAll(elements.asList())
    }

    override fun setAll(col: Collection<E>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeAll(vararg elements: E): Boolean {
        return removeAll(elements.asList())
    }

    override fun retainAll(vararg elements: E): Boolean {
        return retainAll(elements.asList())
    }

    override fun remove(from: Int, to: Int) {
        removeRange(from, to)
    }

    override fun add(index: Int, element: E) {
        throw UnsupportedOperationException()
    }

    override fun removeAt(index: Int): E {
        throw UnsupportedOperationException()
    }

    override fun set(index: Int, element: E): E {
        throw UnsupportedOperationException()
    }

}