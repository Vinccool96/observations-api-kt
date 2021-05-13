package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change

class SetAdapterChange<E>(set: ObservableSet<E>, private val change: Change<out E>) : Change<E>(set) {

    override val wasAdded: Boolean
        get() = this.change.wasAdded

    override val wasRemoved: Boolean
        get() = this.change.wasRemoved

    override val elementAdded: E?
        get() = this.change.elementAdded

    override val elementRemoved: E?
        get() = this.change.elementRemoved

    override fun toString(): String {
        return this.change.toString()
    }

}