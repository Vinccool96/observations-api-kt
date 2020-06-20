package io.github.vinccool96.observationskt.collections

/**
 * Interface that receives notifications of changes to an [ObservableMap].
 *
 * @param K
 *         the key element type
 * @param V
 *         the value element type
 *
 * @since JavaFX 2.0
 */
@FunctionalInterface
interface MapChangeListener<K, V> {

    /**
     * An elementary change done to an [ObservableMap]. Change contains information about a put or remove operation. Note
     * that put operation might remove an element if there was already a value associated with the same key. In this
     * case wasAdded() and wasRemoved() will both return true.
     *
     * @param K
     *         key type
     * @param V
     *         value type
     *
     * @since JavaFX 2.0
     *
     * @constructor Constructs a change associated with a map.
     *
     * @param map
     *         the source of the change
     */
    abstract class Change<K, V>(val map: ObservableMap<K, V>) {

        /**
         * If this change is a result of add operation.
         *
         * @return `true` if a new value (or key-value) entry was added to the map
         */
        abstract val added: Boolean

        /**
         * If this change is a result of removal operation. Note that an element might be removed even as a result of
         * put operation.
         *
         * @return `true` if an old value (or key-value) entry was removed from the map
         */
        abstract val removed: Boolean

        /**
         * A key associated with the change. If the change is a remove change, the key no longer exist in a map.
         * Otherwise, the key got set to a new value.
         *
         * @return the key that changed
         */
        abstract val key: K

        /**
         * Get the new value of the key. Return `null` if this is a removal.
         *
         * @return the value that is now associated with the key
         */
        abstract val valueAdded: V

        /**
         * Get the old value of the key. This is `null` if and only if the value was added to the key that was not
         * previously in the map.
         *
         * @return the value previously associated with the key
         */
        abstract val valueRemoved: V

    }

    /**
     * Called after a change has been made to an [ObservableMap]. This method is called on every elementary change
     * (put/remove) once. This means, complex changes like `keySet().removeAll(Collection)` or `clear()` may result in
     * more than one call of `onChanged` method.
     *
     * @param change
     *         the change that was made
     */
    fun onChanged(change: Change<out K, out V>)

}