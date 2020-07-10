package io.github.vinccool96.observationskt.beans

import java.lang.ref.WeakReference

/**
 * A `WeakInvalidationListener` can be used, if an [Observable] should only maintain a weak reference to the listener.
 * This helps to avoid memory leaks, that can occur if observers are not unregistered from observed objects after use.
 *
 * `WeakInvalidationListener` are created by passing in the original [InvalidationListener]. The
 * `WeakInvalidationListener` should then be registered to listen for changes of the observed object.
 *
 * Note: You have to keep a reference to the `InvalidationListener`, that was passed in as long as it is in use,
 * otherwise it will be garbage collected too soon.
 *
 * @see InvalidationListener
 * @see Observable
 */
class WeakInvalidationListener : InvalidationListener, WeakListener {

    private val ref: WeakReference<InvalidationListener>

    /**
     * The constructor of {@code WeakInvalidationListener}.
     *
     * @param listener
     *         The original listener that should be notified
     */
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(@NamedArg("listener") listener: InvalidationListener?) {
        if (listener == null) {
            throw NullPointerException("Listener must be specified.")
        }
        this.ref = WeakReference(listener)
    }

    override fun invalidated(observable: Observable) {
        val listener = this.ref.get()
        if (listener != null) {
            // The weakly reference listener has been garbage collected, so this WeakListener will now unhook itself
            // from the source bean
            listener.invalidated(observable)
        } else {
            observable.removeListener(this)
        }
    }

    override val wasGarbageCollected: Boolean
        get() = this.ref.get() == null

}