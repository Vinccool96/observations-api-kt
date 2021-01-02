package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableMapValue
import io.github.vinccool96.observationskt.collections.ObservableMap

/**
 * This class provides a full implementation of a [Property] wrapping a [ObservableMap].
 *
 * The value of a `MapProperty` can be get and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `MapProperty` can be read with [bean] and [name].
 *
 * @param K the type of the key elements of the `Map`
 * @param V the type of the value elements of the `Map`
 *
 * @see ObservableMap
 * @see io.github.vinccool96.observationskt.beans.value.ObservableMapValue
 * @see WritableMapValue
 * @see ReadOnlyMapProperty
 * @see Property
 */
abstract class MapProperty<K, V> : ReadOnlyMapProperty<K, V>(), Property<ObservableMap<K, V>?>, WritableMapValue<K, V> {

    override var value: ObservableMap<K, V>?
        get() = this.get()
        set(value) = this.set(value)

    override fun bindBidirectional(other: Property<ObservableMap<K, V>?>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<ObservableMap<K, V>?>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `MapProperty` object.
     *
     * @return a string representation of this `MapProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("MapProperty [")
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ")
        }
        if (name != null && name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        result.append("value: ").append(get()).append("]")
        return result.toString()
    }

}