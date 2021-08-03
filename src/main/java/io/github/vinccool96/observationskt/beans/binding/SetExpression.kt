package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntProperty
import io.github.vinccool96.observationskt.beans.value.ObservableSetValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.sun.binding.StringFormatter
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `SetExpression` is a [ObservableSetValue] plus additional convenience methods to generate bindings in a fluent
 * style.
 *
 * A concrete sub-class of `SetExpression` has to implement the method [ObservableSetValue.get], which provides the
 * actual value of this expression.
 *
 * If the wrapped set of a `SetExpression` is `null`, all methods implementing the `MutableSet` interface will behave as
 * if they were applied to an immutable empty set.
 *
 * @param E the type of the `MutableSet` elements
 */
@Suppress("PrivatePropertyName")
abstract class SetExpression<E> : ObservableSetValue<E> {

    private val EMPTY_SET: ObservableSet<E> = ObservableCollections.emptyObservableSet()

    override val value: ObservableSet<E>?
        get() = this.get()

    /**
     * An integer property that represents the size of the set.
     *
     * @return the property
     */
    abstract val sizeProperty: ReadOnlyIntProperty

    /**
     * A boolean property that is `true`, if the set is empty.
     *
     * @return the property
     */
    abstract val emptyProperty: ReadOnlyBooleanProperty

    /**
     * Creates a new [BooleanBinding] that holds `true` if this set is equal to another [ObservableSet].
     *
     * @param other the other `ObservableSet`
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: ObservableSet<*>): BooleanBinding {
        return Bindings.equal(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this set is equal to another [ObservableSet].
     *
     * @param other the other `ObservableSet`
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: ObservableSet<*>): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the wrapped set is `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNull(): BooleanBinding {
        return Bindings.isNull(this)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the wrapped set is `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNotNull(): BooleanBinding {
        return Bindings.isNotNull(this)
    }

    /**
     * Creates a [StringBinding] that holds the value of the `SetExpression` turned into a `String`. If the value of
     * this `SetExpression` changes, the value of the `StringBinding` will be updated automatically.
     *
     * @return the new `StringBinding`
     */
    fun asString(): StringBinding {
        return StringFormatter.convert(this) as StringBinding
    }

    override val size: Int
        get() = (this.get() ?: EMPTY_SET).size

    override fun isEmpty(): Boolean {
        return (this.get() ?: EMPTY_SET).isEmpty()
    }

    override fun contains(element: E): Boolean {
        return (this.get() ?: EMPTY_SET).contains(element)
    }

    override fun iterator(): MutableIterator<E> {
        return (this.get() ?: EMPTY_SET).iterator()
    }

    override fun add(element: E): Boolean {
        return (this.get() ?: EMPTY_SET).add(element)
    }

    override fun remove(element: E): Boolean {
        return (this.get() ?: EMPTY_SET).remove(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_SET).containsAll(elements)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_SET).addAll(elements)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_SET).removeAll(elements)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_SET).retainAll(elements)
    }

    override fun clear() {
        (this.get() ?: EMPTY_SET).clear()
    }

    companion object {

        /**
         * Returns a `SetExpression` that wraps a [ObservableSetValue]. If the `ObservableSetValue` is already a
         * `SetExpression`, it will be returned. Otherwise a new [SetBinding] is created that is bound to the
         * `ObservableSetValue`.
         *
         * @param value The source `ObservableSetValue`
         * @param E the type of the `MutableSet` elements
         *
         * @return A `SetExpression` that wraps the `ObservableSetValue` if necessary
         */
        fun <E> setExpression(value: ObservableSetValue<E>): SetExpression<E> {
            return if (value is SetExpression<E>) value else object : SetBinding<E>() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): ObservableSet<E>? {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*> = ObservableCollections.singletonObservableList(value)

            }
        }

    }

}