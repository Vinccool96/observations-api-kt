package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.MapChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableMap

class MapAdapterChange<K, V>(map: ObservableMap<K, V>, private val change: Change<out K, out V>) : Change<K, V>(map) {

    override val wasAdded: Boolean
        get() = this.change.wasAdded

    override val wasRemoved: Boolean
        get() = this.change.wasRemoved

    override val key: K
        get() = this.change.key

    override val valueAdded: V?
        get() = this.change.valueAdded

    override val valueRemoved: V?
        get() = this.change.valueRemoved

    override fun toString(): String {
        return this.change.toString()
    }

}