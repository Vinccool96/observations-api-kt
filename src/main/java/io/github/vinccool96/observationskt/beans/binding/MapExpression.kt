package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntProperty
import io.github.vinccool96.observationskt.beans.value.ObservableMapValue
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.sun.binding.StringFormatter
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `MapExpression` is a [ObservableMapValue] plus additional convenience methods to generate bindings in a fluent
 * style.
 *
 * A concrete sub-class of `MapExpression` has to implement the method [ObservableMapValue.get], which provides the
 * actual value of this expression.
 *
 * If the wrapped map of a `MapExpression` is `null`, all methods implementing the `MutableMap` interface will behave as
 * if they were applied to an immutable empty map.
 *
 * @param K the type of the key elements
 * @param V the type of the value elements
 */
@Suppress("PrivatePropertyName")
abstract class MapExpression<K, V> : ObservableMapValue<K, V> {

    private val EMPTY_MAP: ObservableMap<K, V> = ObservableCollections.emptyObservableMap()

    override val value: ObservableMap<K, V>?
        get() = this.get()

    /**
     * An int property that represents the size of the map.
     *
     * @return the property
     */
    abstract val sizeProperty: ReadOnlyIntProperty

    /**
     * A boolean property that is `true`, if the map is empty.
     *
     * @return the property
     */
    abstract val emptyProperty: ReadOnlyBooleanProperty

    /**
     * Creates a new [ObjectBinding] that contains the mapping of the specified key.
     *
     * @param key the key of the mapping
     *
     * @return the `ObjectBinding`
     */
    fun valueAt(key: K): ObjectBinding<V?> {
        return Bindings.valueAt(this, key)
    }

    /**
     * Creates a new [ObjectBinding] that contains the mapping of the specified key.
     *
     * @param key the key of the mapping
     *
     * @return the `ObjectBinding`
     */
    fun valueAt(key: ObservableValue<K>): ObjectBinding<V?> {
        return Bindings.valueAt(this, key)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this map is equal to another [ObservableMap].
     *
     * @param other the other `ObservableMap`
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: ObservableMap<*, *>): BooleanBinding {
        return Bindings.equal(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this map is not equal to another [ObservableMap].
     *
     * @param other the other `ObservableMap`
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: ObservableMap<*, *>): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the wrapped map is `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNull(): BooleanBinding {
        return Bindings.isNull(this)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the wrapped map is `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNotNull(): BooleanBinding {
        return Bindings.isNotNull(this)
    }

    /**
     * Creates a [StringBinding] that holds the value of the `MapExpression` turned into a `String`. If the value of
     * this `MapExpression` changes, the value of the `StringBinding` will be updated automatically.
     *
     * @return the new `StringBinding`
     */
    fun asString(): StringBinding {
        return StringFormatter.convert(this) as StringBinding
    }

    override val size: Int
        get() = (get() ?: EMPTY_MAP).size

    override fun isEmpty(): Boolean {
        return (get() ?: EMPTY_MAP).isEmpty()
    }

    override fun containsKey(key: K): Boolean {
        return (get() ?: EMPTY_MAP).containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return (get() ?: EMPTY_MAP).containsValue(value)
    }

    override fun put(key: K, value: V): V? {
        return (get() ?: EMPTY_MAP).put(key, value)
    }

    override fun remove(key: K): V? {
        return (get() ?: EMPTY_MAP).remove(key)
    }

    override fun putAll(from: Map<out K, V>) {
        (get() ?: EMPTY_MAP).putAll(from)
    }

    override fun clear() {
        (get() ?: EMPTY_MAP).clear()
    }

    override val keys: MutableSet<K>
        get() = (get() ?: EMPTY_MAP).keys

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = (get() ?: EMPTY_MAP).entries

    override val values: MutableCollection<V>
        get() = (get() ?: EMPTY_MAP).values

    override fun get(key: K): V? {
        return (get() ?: EMPTY_MAP)[key]
    }

    companion object {

        /**
         * Returns a `MapExpression` that wraps a [ObservableMapValue]. If the `ObservableMapValue` is already a
         * `MapExpression`, it will be returned. Otherwise, a new [MapBinding] is created that is bound to the
         * `ObservableMapValue`.
         *
         * @param value The source `ObservableMapValue`
         * @param K the type of the key elements
         * @param V the type of the value elements
         *
         * @return A `MapExpression` that wraps the `ObservableMapValue` if necessary
         */
        fun <K, V> mapExpression(value: ObservableMapValue<K, V>): MapExpression<K, V> {
            return if (value is MapExpression<K, V>) value else object : MapBinding<K, V>() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): ObservableMap<K, V>? {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

    }

}