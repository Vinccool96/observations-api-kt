package io.github.vinccool96.observationskt.collections

/**
 * Interface that receives notifications of changes to an [ObservableList].
 *
 * @param E the list element type
 *
 * @see Change
 */
@FunctionalInterface
interface ListChangeListener<E> {

    /**
     * Represents a report of a changes done to an [ObservableList]. The Change may consist of one or more actual
     * changes and must be iterated by [next] method.
     *
     * Each change must be one of the following:
     *
     * * **Permutation change** : [wasPermutated] returns `true` in this case. The permutation happened at range between
     * [from]\(inclusive\) and [to]\(exclusive\) and can be queried by calling [getPermutation] method.
     * * **Add or remove change** : In this case, at least one of the [wasAdded], [wasRemoved] returns `true`. If both
     * methods return `true`, [wasReplaced] will also return `true`.
     *
     * The [wasRemoved] value returns a list of elements that have been replaced or removed from the list.
     *
     * The range between [from]\(inclusive\) and [to]\(exclusive\) denotes the sublist of the list that contain new
     * elements. Note that this is a half-open interval, so if no elements were added, `from` is equal to `to`.
     *
     * It is possible to get a list of added elements by calling [addedSubList].
     *
     * Note that in order to maintain correct indexes of the separate add/remove changes, these changes **must** be
     * sorted by their `from` index.
     * * **Update change** : [wasUpdated] return true on an update change. All elements between [from]\(inclusive\) and
     * [to]\(exclusive\) were updated.
     *
     * **Important:** It's necessary to call [next] method before calling any other method of `Change`. The same applies
     * after calling [reset]. The only methods that works at any time is [list].
     *
     * Typical usage is to observe changes on an `ObservableList` in order to hook or unhook (or add or remove a
     * listener) or in order to maintain some invariant on every element in that `ObservableList`. A common code pattern
     * for doing this looks something like the following:
     *
     * ```
     * val theList: ObservableList<Item> = ...
     *
     * theList.addListener(object : ListChangeListener<Item> {
     *     override fun onChanged(c: ListChangeListener.Change<out Item>) {
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
     * **Warning:** This class directly accesses the source list to acquire information about the changes.
     *
     * This effectively makes the `Change` object invalid when another change occurs on the list.
     *
     * For this reason it is **not safe to use this class on a different thread**.
     *
     * It also means **the source list cannot be modified inside the listener** since that would invalidate this
     * `Change` object for all subsequent listeners.
     *
     * Note: in case the change contains multiple changes of different type, these changes must be in the following
     * order: *permutation change(s), add or remove changes, update changes*. This is because permutation changes cannot
     * go after add/remove changes as they would change the position of added elements. And on the other hand, update
     * changes must go after add/remove changes because they refer with their indexes to the current state of the list,
     * which means with all add/remove changes applied.
     *
     * @param E the list element type
     *
     * @constructor Constructs a new change done to a list.
     *
     * @param list that was changed
     */
    abstract class Change<E>(val list: ObservableList<E>) {

        /**
         * Go to the next change. The `Change` in the initial state is invalid a requires a call to `next()` before
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
         * `to` should return the same number - the place where the removed elements were positioned in the list.
         *
         * @return a beginning (inclusive) of an interval related to the change
         *
         * @throws IllegalStateException
         * if this Change is in initial state
         */
        abstract val from: Int

        /**
         * The end of the change interval.
         *
         * @return a end (exclusive) of an interval related to the change.
         *
         * @throws IllegalStateException
         * if this Change is in initial state
         * @see .getFrom
         */
        abstract val to: Int

        /**
         * An immutable list of removed/replaced elements. If no elements were removed from the list, an empty list is
         * returned.
         *
         * @return a list with all the removed elements
         *
         * @throws IllegalStateException
         * if this Change is in initial state
         */
        abstract val removed: MutableList<E>

        /**
         * Indicates if the change was only a permutation.
         *
         * @return `true` if the change was just a permutation.
         *
         * @throws IllegalStateException
         * if this Change is in initial state
         */
        val wasPermutated: Boolean
            get() = this.permutation.isNotEmpty()

        /**
         * Indicates if elements were added during this change
         *
         * @return `true` if something was added to the list
         *
         * @throws IllegalStateException
         * if this Change is in initial state
         */
        val wasAdded: Boolean
            get() = !this.wasPermutated && !this.wasUpdated && this.from < this.to

        /**
         * Indicates if elements were removed during this change. Note that using set will also produce a change with
         * `wasRemoved` returning `true`. See [wasReplaced].
         *
         * @return `true` if something was removed from the list
         *
         * @throws IllegalStateException
         * if this Change is in initial state
         */
        open val wasRemoved: Boolean
            get() = this.removed.isNotEmpty()

        /**
         * Indicates if elements were replaced during this change. This is usually true when set is called on the list.
         * Set operation will act like remove and add operation at the same time.
         *
         * Usually, it's not necessary to use this method directly. Handling remove operation and then add operation, as
         * in the example [above][Change], will effectively handle also set operation.
         *
         * @return same as `added && removed`
         *
         * @throws IllegalStateException
         * if this Change is in initial state
         */
        val wasReplaced: Boolean
            get() = this.wasAdded && this.wasRemoved

        /**
         * Indicates that the elements between getFrom() (inclusive) to getTo() exclusive has changed. This is the only
         * optional event type and may not be fired by all ObservableLists.
         *
         * @return `true` if the current change is an update change.
         */
        open val wasUpdated: Boolean
            get() = false

        /**
         * To get a subList view of the list that contains only the elements added, use getAddedSubList() method. This
         * is actually a shortcut to `c.getList().subList(c.getFrom(), c.getTo());`
         *
         * ```
         * for (n in change.addedSubList) {
         *     // do something
         * }
         *```
         *
         * @return the newly created sublist view that contains all the added elements.
         *
         * @throws IllegalStateException
         * if this Change is in initial state
         */
        val addedSubList: List<E>
            get() {
                return if (this.wasAdded) this.list.subList(this.from, this.to) else emptyList()
            }

        /**
         * Size of getRemoved() list.
         *
         * @return the number of removed items
         *
         * @throws IllegalStateException
         * if this Change is in initial state
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
         * @throws IllegalStateException
         * if this Change is in initial state
         */
        val addedSize: Int
            get() {
                return if (this.wasAdded) this.to - this.from else 0
            }

        /**
         * If this change is an permutation, it returns an int array that describes the permutation. This array maps
         * directly from the previous indexes to the new ones. This method is not publicly accessible and therefore can
         * return an array safely. The 0 index of the array corresponds to index [from] of the list. The
         * same applies for the last index and [to]. The method is used by [wasPermutated] and
         * [getPermutation] methods.
         *
         * @return empty array if this is not permutation or an int array containing the permutation
         *
         * @throws IllegalStateException
         * if this Change is in initial state
         */
        protected abstract val permutation: IntArray

        /**
         * By calling these method, you can observe the permutation that happened. In order to get the new position of
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
         * @throws IndexOutOfBoundsException
         * if i is out of the bounds of the list
         * @throws IllegalStateException
         * if this is not a permutation change
         */
        fun getPermutation(i: Int): Int {
            check(this.wasPermutated) {"Not a permutation change"}
            return this.permutation[i - this.from]
        }
    }

    /**
     * Called after a change has been made to an ObservableList.
     *
     * @param change an object representing the change that was done
     *
     * @see Change
     */
    fun onChanged(change: Change<out E>)

}