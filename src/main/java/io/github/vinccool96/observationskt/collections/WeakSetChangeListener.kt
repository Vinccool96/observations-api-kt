package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.NamedArg
import io.github.vinccool96.observationskt.beans.WeakListener
import java.lang.ref.WeakReference

/**
 * A `WeakSetChangeListener` can be used, if an [ObservableSet] should only maintain a weak reference to the listener.
 * This helps to avoid memory leaks, that can occur if observers are not unregistered from observed objects after use.
 *
 * `WeakSetChangeListener` are created by passing in the original [SetChangeListener]. The `WeakSetChangeListener`
 * should then be registered to listen for changes of the observed object.
 *
 * Note: You have to keep a reference to the `SetChangeListener`, that was passed in as long as it is in use, otherwise
 * it will be garbage collected too soon.
 *
 * @param E The type of the observed value
 *
 * @see SetChangeListener
 * @see ObservableSet
 * @see WeakListener
 *
 * @constructor The constructor of `WeakSetChangeListener`.
 *
 * @param listener The original listener that should be notified
 */
class WeakSetChangeListener<E>(@NamedArg("listener") listener: SetChangeListener<E>) : SetChangeListener<E>,
        WeakListener {

    private val ref: WeakReference<SetChangeListener<E>> = WeakReference(listener)

    override val wasGarbageCollected: Boolean
        get() = this.ref.get() == null

    override fun onChanged(change: SetChangeListener.Change<out E>) {
        val listener = this.ref.get()
        if (listener != null) {
            listener.onChanged(change)
        } else {
            // The weakly reference listener has been garbage collected, so this WeakListener will now unhook itself
            // from the source bean
            change.set.removeListener(this)
        }
    }

}