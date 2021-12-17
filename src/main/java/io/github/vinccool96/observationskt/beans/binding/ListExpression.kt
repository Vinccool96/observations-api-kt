package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntProperty
import io.github.vinccool96.observationskt.beans.value.ObservableIntValue
import io.github.vinccool96.observationskt.beans.value.ObservableListValue
import io.github.vinccool96.observationskt.beans.value.ObservableNumberValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.StringFormatter
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `ListExpression` is a [ObservableListValue] plus additional convenience methods to generate bindings in a fluent
 * style.
 *
 * A concrete sub-class of `ListExpression` has to implement the method [get], which provides the actual value of this
 * expression.
 *
 * If the wrapped list of a `ListExpression` is `null`, all methods implementing the `MutableList` interface will behave
 * as if they were applied to an immutable empty list.
 *
 * @param E the type of the `List` elements.
 */
@Suppress("PrivatePropertyName")
abstract class ListExpression<E> : ObservableListValue<E> {

    private val EMPTY_LIST: ObservableList<E> = ObservableCollections.emptyObservableList()

    override val value: ObservableList<E>?
        get() = this.get()

    /**
     * An int property that represents the size of the list.
     *
     * @return the property
     */
    abstract val sizeProperty: ReadOnlyIntProperty

    /**
     * A boolean property that is `true`, if the list is empty.
     *
     * @return the property
     */
    abstract val emptyProperty: ReadOnlyBooleanProperty

    /**
     * Creates a new [ObjectBinding] that contains the element at the specified position. The `ObjectBinding` will
     * contain `null`, if the `index` points behind the list.
     *
     * @param index the position in the `List`
     *
     * @return the new `ObjectBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun valueAt(index: Int): ObjectBinding<E?> {
        return Bindings.valueAt(this, index)
    }

    /**
     * Creates a new [ObjectBinding] that contains the element at the specified position. The `ObjectBinding` will
     * contain `null`, if the `index` points behind the list.
     *
     * @param index the position in the `List`
     *
     * @return the new `ObjectBinding`
     *
     * @throws IllegalArgumentException if `index.intValue < 0`
     */
    fun valueAt(index: ObservableIntValue): ObjectBinding<E?> {
        return Bindings.valueAt(this, index)
    }

    /**
     * Creates a new [ObjectBinding] that contains the element at the specified position. The `ObjectBinding` will
     * contain `null`, if the `index` points behind the list.
     *
     * @param index the position in the `List`
     *
     * @return the new `ObjectBinding`
     *
     * @throws IllegalArgumentException if `index.intValue < 0`
     */
    fun valueAt(index: ObservableNumberValue): ObjectBinding<E?> {
        return Bindings.valueAt(this, index)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this list is equal to another [ObservableList].
     *
     * @param other the other `ObservableList`
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: ObservableList<*>): BooleanBinding {
        return Bindings.equal(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this list is not equal to another [ObservableList].
     *
     * @param other the other `ObservableList`
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: ObservableList<*>): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the wrapped list is `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNull(): BooleanBinding {
        return Bindings.isNull(this)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the wrapped list is not `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNotNull(): BooleanBinding {
        return Bindings.isNotNull(this)
    }

    /**
     * Creates a [StringBinding] that holds the value of the `ListExpression` turned into a `String`. If the value of
     * this `ListExpression` changes, the value of the `StringBinding` will be updated automatically.
     *
     * @return the new `StringBinding`
     */
    fun asString(): StringBinding {
        return StringFormatter.convert(this) as StringBinding
    }

    override val size: Int
        get() = (this.get() ?: EMPTY_LIST).size

    override fun isEmpty(): Boolean {
        return (this.get() ?: EMPTY_LIST).isEmpty()
    }

    override operator fun contains(element: E): Boolean {
        return (this.get() ?: EMPTY_LIST).contains(element)
    }

    override fun iterator(): MutableIterator<E> {
        return (this.get() ?: EMPTY_LIST).iterator()
    }

    override fun add(element: E): Boolean {
        return (this.get() ?: EMPTY_LIST).add(element)
    }

    override fun remove(element: E): Boolean {
        return (this.get() ?: EMPTY_LIST).remove(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).containsAll(elements)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).addAll(index, elements)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).removeAll(elements)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).retainAll(elements)
    }

    override fun clear() {
        (this.get() ?: EMPTY_LIST).clear()
    }

    override fun get(index: Int): E {
        return (this.get() ?: EMPTY_LIST)[index]
    }

    override fun set(index: Int, element: E): E {
        return (this.get() ?: EMPTY_LIST).set(index, element)
    }

    override fun add(index: Int, element: E) {
        (this.get() ?: EMPTY_LIST).add(index, element)
    }

    override fun removeAt(index: Int): E {
        return (this.get() ?: EMPTY_LIST).removeAt(index)
    }

    override fun indexOf(element: E): Int {
        return (this.get() ?: EMPTY_LIST).indexOf(element)
    }

    override fun lastIndexOf(element: E): Int {
        return (this.get() ?: EMPTY_LIST).lastIndexOf(element)
    }

    override fun listIterator(): MutableListIterator<E> {
        return (this.get() ?: EMPTY_LIST).listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<E> {
        return (this.get() ?: EMPTY_LIST).listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        return (this.get() ?: EMPTY_LIST).subList(fromIndex, toIndex)
    }

    override fun addAll(vararg elements: E): Boolean {
        return (this.get() ?: EMPTY_LIST).addAll(*elements)
    }

    override fun setAll(vararg elements: E): Boolean {
        return (this.get() ?: EMPTY_LIST).setAll(*elements)
    }

    override fun setAll(col: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).setAll(col)
    }

    override fun removeAll(vararg elements: E): Boolean {
        return (this.get() ?: EMPTY_LIST).removeAll(*elements)
    }

    override fun retainAll(vararg elements: E): Boolean {
        return (this.get() ?: EMPTY_LIST).retainAll(*elements)
    }

    override fun remove(from: Int, to: Int) {
        (this.get() ?: EMPTY_LIST).remove(from, to)
    }

    companion object {

        /**
         * Returns a `ListExpression` that wraps a [ObservableListValue]. If the `ObservableListValue` is already a
         * `ListExpression`, it will be returned. Otherwise, a new [ListBinding] is created that is bound to the
         * `ObservableListValue`.
         *
         * @param value The source `ObservableListValue`
         * @param E the type of the `List` elements.
         *
         * @return A `ListExpression` that wraps the `ObservableListValue` if necessary
         */
        fun <E> listExpression(value: ObservableListValue<E>): ListExpression<E> {
            return if (value is ListExpression<E>) value else object : ListBinding<E>() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): ObservableList<E>? {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*> = ObservableCollections.singletonObservableList(value)

            }
        }

    }

}