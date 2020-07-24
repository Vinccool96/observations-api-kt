package io.github.vinccool96.observationskt.beans

class WeakInvalidationListenerMock : InvalidationListener, WeakListener {

    override fun invalidated(observable: Observable) {
    }

    override val wasGarbageCollected: Boolean = true

}