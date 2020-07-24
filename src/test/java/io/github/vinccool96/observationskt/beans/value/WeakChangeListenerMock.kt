package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.beans.WeakListener

class WeakChangeListenerMock<T> : ChangeListener<T>, WeakListener {

    override fun changed(observable: ObservableValue<out T>, oldValue: T, newValue: T) {
    }

    override val wasGarbageCollected: Boolean = true

}