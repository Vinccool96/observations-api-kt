package io.github.vinccool96.observationskt.beans

/**
 * `WeakListener` is the super interface of all weak listener implementations JavaFX runtime. Usually it should
 * not be used directly, but instead one of the sub-interfaces will be used.
 *
 * @see WeakInvalidationListener
 * @see io.github.vinccool96.observationskt.beans.value.WeakChangeListener
 * @since JavaFX 2.1
 */
interface WeakListener {

    /**
     * Returns `true` if the linked listener was garbage-collected. In this case, the listener can be removed from
     * the observable.
     *
     * @return `true` if the linked listener was garbage-collected.
     */
    val wasGarbageCollected: Boolean
}