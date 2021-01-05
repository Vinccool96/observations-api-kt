package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.NamedArg
import io.github.vinccool96.observationskt.beans.WeakListener
import java.lang.ref.WeakReference

/**
 * A `WeakMapChangeListener` can be used, if an [ObservableMap] should only maintain a weak reference to the listener.
 * This helps to avoid memory leaks, that can occur if observers are not unregistered from observed objects after use.
 *
 * `WeakMapChangeListener` are created by passing in the original [MapChangeListener]. The `WeakMapChangeListener`
 * should then be registered to listen for changes of the observed object.
 *
 * Note: You have to keep a reference to the `MapChangeListener`, that was passed in as long as it is in use, otherwise
 * it will be garbage collected too soon.
 *
 * @param K the key element type
 * @param V the value element type
 *
 * @see MapChangeListener
 * @see ObservableMap
 * @see WeakListener
 *
 * @constructor The constructor of `WeakMapChangeListener`.
 *
 * @param listener The original listener that should be notified
 */
class WeakMapChangeListener<K, V>(@NamedArg("listener") listener: MapChangeListener<K, V>) : MapChangeListener<K, V>,
        WeakListener {

    private val ref: WeakReference<MapChangeListener<K, V>> = WeakReference(listener)

    override val wasGarbageCollected: Boolean
        get() = this.ref.get() == null

    override fun onChanged(change: MapChangeListener.Change<out K, out V>) {
        val listener = this.ref.get()
        if (listener != null) {
            listener.onChanged(change)
        } else {
            // The weakly reference listener has been garbage collected, so this WeakListener will now unhook itself
            // from the source bean
            change.map.removeListener(this)
        }
    }

}