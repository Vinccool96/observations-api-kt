package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.beans.WeakListener
import java.lang.ref.WeakReference

/**
 * A `WeakChangeListener` can be used, if an [ObservableValue] should only maintain a weak reference to the listener.
 * This helps to avoid memory leaks, that can occur if observers are not unregistered from observed objects after use.
 *
 * `WeakChangeListener` are created by passing in the original [ChangeListener]. The `WeakChangeListener` should then be
 * registered to listen for changes of the observed object.
 *
 * Note: You have to keep a reference to the `ChangeListener`, that was passed in as long as it is in use, otherwise it
 * will be garbage collected too soon.
 *
 * @param T The type of the observed value
 *
 * @see ChangeListener
 * @see ObservableValue
 *
 * @constructor The constructor of `WeakChangeListener`.
 *
 * @param listener The original listener that should be notified
 */
class WeakChangeListener<T>(listener: ChangeListener<T>) : ChangeListener<T>, WeakListener {

    private val ref: WeakReference<ChangeListener<T>> = WeakReference(listener)

    override val wasGarbageCollected: Boolean
        get() = this.ref.get() == null

    override fun changed(observable: ObservableValue<out T>, oldValue: T, newValue: T) {
        val listener: ChangeListener<T>? = this.ref.get()
        if (listener != null) {
            listener.changed(observable, oldValue, newValue)
        } else {
            // The weakly reference listener has been garbage collected, so this WeakListener will now unhook itself
            // from the source bean
            observable.removeListener(this)
        }
    }

}