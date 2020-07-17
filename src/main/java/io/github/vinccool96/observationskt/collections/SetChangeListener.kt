package io.github.vinccool96.observationskt.collections

/**
 * Interface that receives notifications of changes to an [ObservableSet].
 *
 * @param E the element type
 */
fun interface SetChangeListener<E> {

    /**
     * An elementary change done to an [ObservableSet]. Change contains information about an add or remove operation.
     * Note that adding element that is already in the set does not modify the set and hence no change will be
     * generated.
     *
     * @param E element type
     *
     * @constructor Constructs a change associated with a set.
     *
     * @param set the source of the change
     */
    abstract class Change<E>(val set: ObservableSet<E>) {

        /**
         * If this change is a result of add operation.
         *
         * @return `true` if a new element was added to the set
         */
        abstract val added: Boolean

        /**
         * If this change is a result of removal operation.
         *
         * @return `true` if an old element was removed from the set
         */
        abstract val removed: Boolean

        /**
         * Get the new element. Return `null` if this is a removal.
         *
         * @return the element that was just added
         */
        abstract val elementAdded: E?

        /**
         * Get the old element. Return `null` if this is an addition.
         *
         * @return the element that was just removed
         */
        abstract val elementRemoved: E?

    }

    /**
     * Called after a change has been made to an [ObservableSet]. This method is called on every elementary change
     * (add/remove) once. This means, complex changes like `removeAll(Collection)` or `clear()` may result in more than
     * one call of `onChanged` method.
     *
     * @param change the change that was made
     */
    fun onChanged(change: Change<out E>)

}