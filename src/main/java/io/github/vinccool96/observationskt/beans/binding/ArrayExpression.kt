package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntProperty
import io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
import io.github.vinccool96.observationskt.beans.value.ObservableIntValue
import io.github.vinccool96.observationskt.beans.value.ObservableNumberValue
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.StringFormatter
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * An `ArrayExpression` is a [ObservableArrayValue] plus additional convenience methods to generate bindings in a fluent
 * style.
 *
 * A concrete sub-class of `ArrayExpression` has to implement the method [get], which provides the actual value of this
 * expression.
 *
 * If the wrapped array of an `ArrayExpression` is `null`, all methods implementing the `ObservableArray` interface will
 * behave as if they were applied to an immutable empty array.
 *
 * @param T the type of the `Array` elements.
 *
 * @constructor The constructor needs a base array that [baseArray] will return when [ObservableArrayValue.get] returns
 * `null`. Therefore, `baseArray` will return the equivalent of `get()?.baseArray ?: baseArrayOfNull`.
 *
 * @param baseArrayOfNull the base array when the value is `null`
 */
@Suppress("PrivatePropertyName")
abstract class ArrayExpression<T>(baseArrayOfNull: Array<T>) : ObservableArrayValue<T> {

    private val EMPTY_ARRAY: ObservableArray<T> = ObservableCollections.emptyObservableArray(baseArrayOfNull)

    override val value: ObservableArray<T>?
        get() = this.get()

    /**
     * An int property that represents the size of the array.
     *
     * @return the property
     */
    abstract val sizeProperty: ReadOnlyIntProperty

    /**
     * A boolean property that is `true`, if the list is array.
     *
     * @return the property
     */
    abstract val emptyProperty: ReadOnlyBooleanProperty

    /**
     * Creates a new [ObjectBinding] that contains the element at the specified position. The `ObjectBinding` will
     * contain `null`, if the `index` points behind the array.
     *
     * @param index the position in the `Array`
     *
     * @return the new `ObjectBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun valueAt(index: Int): ObjectBinding<T?> {
        return Bindings.valueAt(this, index)
    }

    /**
     * Creates a new [ObjectBinding] that contains the element at the specified position. The `ObjectBinding` will
     * contain `null`, if the `index` points behind the array.
     *
     * @param index the position in the `Array`
     *
     * @return the new `ObjectBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun valueAt(index: ObservableIntValue): ObjectBinding<T?> {
        return Bindings.valueAt(this, index)
    }

    /**
     * Creates a new [ObjectBinding] that contains the element at the specified position. The `ObjectBinding` will
     * contain `null`, if the `index` points behind the array.
     *
     * @param index the position in the `Array`
     *
     * @return the new `ObjectBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun valueAt(index: ObservableNumberValue): ObjectBinding<T?> {
        return Bindings.valueAt(this, index)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this array is equal to another [ObservableArray].
     *
     * @param other the other `ObservableArray`
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: ObservableArray<*>): BooleanBinding {
        return Bindings.equal(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this array is not equal to another [ObservableArray].
     *
     * @param other the other `ObservableArray`
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: ObservableArray<*>): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the wrapped array is `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNull(): BooleanBinding {
        return Bindings.isNull(this)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the wrapped array is not `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNotNull(): BooleanBinding {
        return Bindings.isNull(this)
    }

    /**
     * Creates a [StringBinding] that holds the value of the `ArrayExpression` turned into a `String`. If the value of
     * this `ArrayExpression` changes, the value of the `StringBinding` will be updated automatically.
     *
     * @return the new `StringBinding`
     */
    fun asString(): StringBinding {
        return StringFormatter.convert(this) as StringBinding
    }

    override val size: Int
        get() = (this.get() ?: EMPTY_ARRAY).size

    override fun clear() {
        (this.get() ?: EMPTY_ARRAY).clear()
    }

    override operator fun get(index: Int): T {
        return (this.get() ?: EMPTY_ARRAY)[index]
    }

    override operator fun set(index: Int, value: T) {
        (this.get() ?: EMPTY_ARRAY)[index] = value
    }

    override fun addAll(vararg elements: T) {
        (this.get() ?: EMPTY_ARRAY).addAll(*elements)
    }

    override fun addAll(src: ObservableArray<T>) {
        (this.get() ?: EMPTY_ARRAY).addAll(src)
    }

    override fun addAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).addAll(src, startIndex, endIndex)
    }

    override fun addAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).addAll(src, startIndex, endIndex)
    }

    override fun setAll(vararg elements: T) {
        (this.get() ?: EMPTY_ARRAY).setAll(*elements)
    }

    override fun setAll(src: ObservableArray<T>) {
        (this.get() ?: EMPTY_ARRAY).setAll(src)
    }

    override fun setAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).setAll(src, startIndex, endIndex)
    }

    override fun setAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).setAll(src, startIndex, endIndex)
    }

    override fun set(src: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).set(src, destinationOffset, startIndex, endIndex)
    }

    override fun set(src: ObservableArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).set(src, destinationOffset, startIndex, endIndex)
    }

    override fun trimToSize() {
        (this.get() ?: EMPTY_ARRAY).trimToSize()
    }

    override fun resize(size: Int) {
        (this.get() ?: EMPTY_ARRAY).resize(size)
    }

    override fun ensureCapacity(capacity: Int) {
        (this.get() ?: EMPTY_ARRAY).ensureCapacity(capacity)
    }

    override fun copyInto(destination: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: ObservableArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override val baseArray: Array<T>
        get() = (this.get() ?: EMPTY_ARRAY).baseArray

    override operator fun iterator(): Iterator<T> {
        return (this.get() ?: EMPTY_ARRAY).iterator()
    }

    override fun toTypedArray(): Array<T> {
        return (this.get() ?: EMPTY_ARRAY).toTypedArray()
    }

    override fun toTypedArray(startIndex: Int, endIndex: Int): Array<T> {
        return (this.get() ?: EMPTY_ARRAY).toTypedArray(startIndex, endIndex)
    }

    companion object {

        /**
         * Returns an `ArrayExpression` that wraps a [ObservableArrayValue]. If the `ObservableArrayValue` is already an
         * `ArrayExpression`, it will be returned. Otherwise, a new [ArrayBinding] is created that is bound to the
         * `ObservableArrayValue`.
         *
         * @param value The source `ObservableArrayValue`
         * @param T the type of the `Array` elements.
         *
         * @return An `ArrayExpression` that wraps the `ObservableArrayValue` if necessary
         */
        fun <T> arrayExpression(value: ObservableArrayValue<T>): ArrayExpression<T> {
            return if (value is ArrayExpression<T>) value else object : ArrayBinding<T>(value.baseArray) {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): ObservableArray<T>? {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*> = ObservableCollections.singletonObservableList(value)

            }
        }

    }

}