package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.NamedArg
import io.github.vinccool96.observationskt.beans.WeakListener
import java.lang.ref.WeakReference

class WeakListChangeListener<E>(@NamedArg("listener") listener: ListChangeListener<E>) : ListChangeListener<E>,
        WeakListener {

    private val ref: WeakReference<ListChangeListener<E>> = WeakReference(listener)

    override val wasGarbageCollected: Boolean
        get() = this.ref.get() == null

    override fun onChanged(change: ListChangeListener.Change<out E>) {
        val listener = this.ref.get()
        if (listener != null) {
            listener.onChanged(change)
        } else {
            // The weakly reference listener has been garbage collected, so this WeakListener will now unhook itself
            // from the source bean
            change.list.removeListener(this)
        }
    }
}