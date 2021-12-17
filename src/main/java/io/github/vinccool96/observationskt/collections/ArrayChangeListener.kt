package io.github.vinccool96.observationskt.collections

/**
 * Interface that receives notifications of changes to an `ObservableArray`.
 *
 * @param T The array element type
 *
 * @see ObservableArray
 */
fun interface ArrayChangeListener<T> {

    /**
     * Represents a report of a changes done to an [ObservableArray]. The Change may consist of one or more actual
     * changes and must be iterated by [next] method.
     *
     * Each change must be one of the following:
     *
     * * **Permutation change** : [wasPermutated] returns `true` in this case. The permutation happened at range between
     * [from] (inclusive) and [to] (exclusive) and can be queried by calling [getPermutation] method.
     *
     * * **Add or remove change** : In this case, at least one of the [wasAdded], [wasRemoved] returns `true`. If both
     * methods return `true`, [wasReplaced] will also return `true`.
     *
     * The [removed] value returns a list of elements that have been replaced or removed from the array.
     *
     * The range between [from] (inclusive) and [to] (exclusive) denotes the sublist of the array that contain new
     * elements. Note that this is a half-open interval, so if no elements were added, `from` is equal to `to`.
     *
     * It is possible to get an array of added elements by calling [addedSubArray].
     *
     * Note that in order to maintain correct indexes of the separate add/remove changes, these changes **must** be
     * sorted by their `from` index.
     *
     * * **Update change** : [wasUpdated] return true on an update change. All elements between [from] (inclusive) and
     * [to] (exclusive) were updated.
     *
     * **Important:** It's necessary to call [next] method before calling any other method of `Change`. The same applies
     * after calling [reset]. The only methods that works at any time is [array].
     *
     * Typical usage is to observe changes on an `ObservableArray` in order to hook or unhook (or add or remove a
     * listener) or in order to maintain some invariant on every element in that `ObservableArray`. A common code
     * pattern for doing this looks something like the following:
     *
     * ```
     * val theArray: ObservableArray<Item> = ...
     *
     * theArray.addListener(object : ArrayChangeListener<Item> {
     *     override fun onChanged(c: ArrayChangeListener.Change<out Item>) {
     *         while (c.next()) {
     *             if (c.wasPermutated) {
     *                 for (i in c.from until c.to) {
     *                     // permutate
     *                 }
     *             } else if (c.wasUpdated) {
     *                 // update item
     *             } else {
     *                 for (remitem in c.removed) {
     *                     remitem.remove(this@Outer)
     *                 }
     *                 for (additem in c.addedSubList) {
     *                     additem.add(this@Outer)
     *                 }
     *             }
     *         }
     *     }
     * })
     * ```
     *
     * **Warning:** This class directly accesses the source array to acquire information about the changes.
     *
     * This effectively makes the `Change` object invalid when another change occurs on the array.
     *
     * For this reason it is **not safe to use this class on a different thread**.
     *
     * It also means **the source array cannot be modified inside the listener** since that would invalidate this
     * `Change` object for all subsequent listeners.
     *
     * Note: in case the change contains multiple changes of different type, these changes must be in the following
     * order: *permutation change(s), add or remove changes, update changes*. This is because permutation changes cannot
     * go after add/remove changes as they would change the position of added elements. And on the other hand, update
     * changes must go after add/remove changes because they refer with their indexes to the current state of the array,
     * which means with all add/remove changes applied.
     *
     * @param T The array element type
     *
     * @constructor Constructs a new change done to an array.
     *
     * @param array the array that was changed
     *
     * @property array The source array of the change.
     */
    abstract class Change<T>(val array: ObservableArray<T>) {

        /**
         * Go to the next change. The `Change` in the initial state is invalid and requires a call to `next()` before
         * calling other methods. The first `next()` call will make this object represent the first change.
         *
         * @return `true` if switched to the next change, false if this is the last change.
         */
        abstract fun next(): Boolean

        /**
         * Reset to the initial stage. After this call, the [next] must be called before working with the first change.
         */
        abstract fun reset()

        /**
         * If wasAdded is `true`, the interval contains all the values that were added. If wasPermutated is `true`, the
         * interval marks the values that were permutated. If wasRemoved is `true` and wasAdded is `false`, `from` and
         * `to` should return the same number - the place where the removed elements were positioned in the array.
         *
         * @return a beginning (inclusive) of an interval related to the change
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        abstract val from: Int

        /**
         * The end of the change interval.
         *
         * @return an end (exclusive) of an interval related to the change.
         *
         * @throws IllegalStateException if this `Change` is in initial state
         *
         * @see from
         */
        abstract val to: Int

        /**
         * An immutable list of removed/replaced elements. If no elements were removed from the array, an empty list is
         * returned.
         *
         * @return a list with all the removed elements
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        abstract val removed: Array<T>

        /**
         * Indicates if the change was only a permutation.
         *
         * @return `true` if the change was just a permutation.
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        val wasPermutated: Boolean
            get() = this.permutation.isNotEmpty()

        /**
         * Indicates if elements were added during this change
         *
         * @return `true` if something was added to the array
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        val wasAdded: Boolean
            get() = !this.wasPermutated && !this.wasUpdated && this.from < this.to

        /**
         * Indicates if elements were removed during this change. Note that using set will also produce a change with
         * `wasRemoved` returning `true`. See [wasReplaced].
         *
         * @return `true` if something was removed from the array
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        open val wasRemoved: Boolean
            get() = this.removed.isNotEmpty()

        /**
         * Indicates if elements were replaced during this change. This is usually true when set is called on the array.
         * Set operation will act like remove and add operation at the same time.
         *
         * Usually, it's not necessary to use this method directly. Handling remove operation and then add operation, as
         * in the example [above][Change], will effectively handle also set operation.
         *
         * @return same as `wasAdded && wasRemoved`
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        val wasReplaced: Boolean
            get() = this.wasAdded && this.wasRemoved

        /**
         * Indicates that the elements between [from] (inclusive) to [to] exclusive has changed. This is the only
         * optional event type and may not be fired by all ObservableArrays.
         *
         * @return `true` if the current change is an update change.
         */
        open val wasUpdated: Boolean
            get() = false

        /**
         * Indicates if the size of the array changed.
         *
         * @return `true` if the size changed.
         */
        val sizeChanged: Boolean
            get() = this.addedSize != this.removedSize

        /**
         * To get a subArray view of the array that contains only the elements added, use `addedSubArray`. This is
         * actually a shortcut to `c.array.toTypedArray(this.from, this.to);`
         *
         * ```
         * for (n in change.addedSubArray) {
         *     // do something
         * }
         *```
         *
         * @return the newly created subarray view that contains all the added elements.
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        val addedSubArray: Array<T>
            get() {
                return if (this.wasAdded) this.array.toTypedArray(this.from, this.to)
                else this.array.baseArray.copyOfRange(0, 0)
            }

        /**
         * Size of [removed] list.
         *
         * @return the number of removed items
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        val removedSize: Int
            get() {
                return this.removed.size
            }

        /**
         * Size of the interval that was added.
         *
         * @return the number of added items
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        val addedSize: Int
            get() {
                return if (this.wasAdded) this.to - this.from else 0
            }

        /**
         * If this change is a permutation, it returns an int array that describes the permutation. This array maps
         * directly from the previous indexes to the new ones. This method is not publicly accessible and therefore can
         * return an array safely. The 0 index of the array corresponds to index [from] of the array. The same applies
         * for the last index and [to]. The value is used by [wasPermutated] and [getPermutation] methods.
         *
         * @return empty array if this is not permutation or an int array containing the permutation
         *
         * @throws IllegalStateException if this `Change` is in initial state
         */
        protected abstract val permutation: IntArray

        /**
         * By calling these methods, you can observe the permutation that happened. In order to get the new position of
         * an element, you must call:
         * ```
         * change.getPermutation(oldIndex)
         * ```
         *
         * Note: default implementation of this method takes the information from [permutation] method. You don't have
         * to override this method.
         *
         * @param i the old index that contained the element prior to this change
         *
         * @return the new index of the same element
         *
         * @throws IndexOutOfBoundsException if `i` is out of the bounds of the list
         * @throws IllegalStateException if this is not a permutation change
         */
        fun getPermutation(i: Int): Int {
            check(this.wasPermutated) { "Not a permutation change" }
            return this.permutation[i - this.from]
        }

    }

    /**
     * Called after a change has been made to an [ObservableArray].
     *
     * @param change an object representing the change that was done
     */
    fun onChanged(change: Change<out T>)

}