package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.binding.MapExpression
import io.github.vinccool96.observationskt.collections.ObservableMap

/**
 * Super class for all readonly properties wrapping an [ObservableMap].
 *
 * @param K the type of the key elements of the map
 * @param V the type of the value elements of the map
 *
 * @see ObservableMap
 * @see io.github.vinccool96.observationskt.beans.value.ObservableMapValue
 * @see MapExpression
 * @see ReadOnlyProperty
 */
abstract class ReadOnlyMapProperty<K, V> : MapExpression<K, V>(), ReadOnlyProperty<ObservableMap<K, V>?> {

    /**
     * Creates a bidirectional content binding of the [ObservableMap], that is wrapped in this `ReadOnlyMapProperty`,
     * and another `ObservableMap`.
     *
     * A bidirectional content binding ensures that the content of two `ObservableMaps` is the same. If the content of
     * one of the maps changes, the other one will be updated automatically.
     *
     * @param map the `ObservableMap` this property should be bound to
     *
     * @throws IllegalArgumentException if `map` is the same map that this `ReadOnlyMapProperty` points to
     */
    fun bindContentBidirectional(map: ObservableMap<K, V>) {
        Bindings.bindContentBidirectional(this, map)
    }

    /**
     * Deletes a bidirectional content binding between the [ObservableMap], that is wrapped in this
     * `ReadOnlyMapProperty`, and another `Object`.
     *
     * @param obj the `Object` to which the bidirectional binding should be removed
     *
     * @throws IllegalArgumentException if `obj` is the same map that this `ReadOnlyMapProperty` points to
     */
    fun unbindContentBidirectional(obj: Any) {
        Bindings.unbindContentBidirectional(this, obj)
    }

    /**
     * Creates a content binding between the [ObservableMap], that is wrapped in this `ReadOnlyMapProperty`,
     * and another `ObservableMap`.
     *
     * A content binding ensures that the content of the wrapped `ObservableMaps` is the same as that of the other
     * map. If the content of the other map changes, the wrapped map will be updated automatically. Once the wrapped map
     * is bound to another map, you must not change it directly.
     *
     * @param map the `ObservableMap` this property should be bound to
     *
     * @throws IllegalArgumentException if `map` is the same map that this `ReadOnlyMapProperty` points to
     */
    fun bindContent(map: ObservableMap<K, V>) {
        Bindings.bindContent(this, map)
    }

    /**
     * Deletes a content binding between the [ObservableMap], that is wrapped in this `ReadOnlyMapProperty`,
     * and another `Object`.
     *
     * @param obj the `Object` to which the binding should be removed
     *
     * @throws IllegalArgumentException if `obj` is the same map that this `ReadOnlyMapProperty` points to
     */
    fun unbindContent(obj: Any) {
        Bindings.unbindContent(this, obj)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is Map<*, *>) {
            return false
        }

        try {
            for (e in this.entries) {
                val key = e.key
                val value = e.value
                if (value == null) {
                    if (!(other[key] == null && other.containsKey(key))) {
                        return false
                    }
                } else {
                    if (value != other[key]) {
                        return false
                    }
                }
            }
        } catch (_: ClassCastException) {
            return false
        } catch (_: NullPointerException) {
            return false
        }

        return true
    }

    /**
     * Returns a hash code for this `ReadOnlyMapProperty` object.
     *
     * @return a hash code for this `ReadOnlyMapProperty` object.
     */
    override fun hashCode(): Int {
        var h = 0
        for (e in this.entries) {
            h += e.hashCode()
        }
        return h
    }

    /**
     * Returns a string representation of this `ReadOnlyMapProperty` object.
     *
     * @return a string representation of this `ReadOnlyMapProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyMapProperty [")
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