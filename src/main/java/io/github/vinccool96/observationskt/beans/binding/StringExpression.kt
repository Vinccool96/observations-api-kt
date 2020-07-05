package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableStringValue
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.sun.binding.StringFormatter

/**
 * A `StringExpression` is a [ObservableStringValue] plus additional convenience methods to generate
 * bindings in a fluent style.
 *
 * A concrete sub-class of `StringExpression` has to implement the method [get], which provides the actual value of this
 * expression.
 *
 * Note: all implementation of [BooleanBinding] returned by the comparisons in this class consider a `String` that is
 * `null` equal to an empty `String`.
 *
 * @since JavaFX 2.0
 */
abstract class StringExpression : ObservableStringValue {

    override val value: String?
        get() = this.get()

    /**
     * Returns usually the value of this `StringExpression`. Only if the value is `null` an empty `String` is returned
     * instead.
     *
     * @return the value of this `StringExpression` or the empty `String`
     */
    val valueSafe: String
        get() = this.get() ?: ""

    /**
     * Creates a new [BooleanBinding] that holds `true` if this and another [ObservableStringValue] are equal.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: ObservableStringValue): BooleanBinding {
        return Bindings.equal(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is equal to a constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: String?): BooleanBinding {
        return Bindings.equal(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this and another [ObservableStringValue] are not equal.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: ObservableStringValue): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is not equal to a constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: String?): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this and another [ObservableStringValue] are equal ignoring
     * case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the second `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualToIgnoreCase(other: ObservableStringValue): BooleanBinding {
        return Bindings.equalIgnoreCase(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is equal to a constant value ignoring
     * case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualToIgnoreCase(other: String?): BooleanBinding {
        return Bindings.equalIgnoreCase(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this and another [ObservableStringValue] are not equal
     * ignoring case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the second `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualToIgnoreCase(other: ObservableStringValue): BooleanBinding {
        return Bindings.notEqualIgnoreCase(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is not equal to a constant value
     * ignoring case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualToIgnoreCase(other: String?): BooleanBinding {
        return Bindings.notEqualIgnoreCase(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is greater than another
     * [ObservableStringValue].
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the second `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(other: ObservableStringValue): BooleanBinding {
        return Bindings.greaterThan(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is greater than a constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(other: String?): BooleanBinding {
        return Bindings.greaterThan(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is less than another
     * [ObservableStringValue].
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the second `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(other: ObservableStringValue): BooleanBinding {
        return Bindings.lessThan(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is less than a constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(other: String?): BooleanBinding {
        return Bindings.lessThan(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is greater than or equal to another
     * [ObservableStringValue].
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the second `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqualTo(other: ObservableStringValue): BooleanBinding {
        return Bindings.greaterThanOrEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is greater than or equal to a
     * constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqualTo(other: String?): BooleanBinding {
        return Bindings.greaterThanOrEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is less than or equal to another
     * [ObservableStringValue].
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the second `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqualTo(other: ObservableStringValue): BooleanBinding {
        return Bindings.lessThanOrEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is less than or equal to a constant
     * value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqualTo(other: String?): BooleanBinding {
        return Bindings.lessThanOrEqual(this, other)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNull(): BooleanBinding {
        return Bindings.isNull(this)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is not `null`.
     *
     * @return the new `BooleanBinding`
     */
    fun isNotNull(): BooleanBinding {
        return Bindings.isNotNull(this)
    }

    /**
     * Creates a new [IntBinding] that holds the length of this `StringExpression`.
     *
     * Note: If the value of this `StringExpression` is `null`, the length is considered to be `0`.
     *
     * @return the new `IntBinding`
     *
     * @since JavaFX 8.0
     */
    fun length(): IntBinding? {
        return Bindings.length(this)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is empty.
     *
     * Note: If the value of this `StringExpression` is `null`, it is considered to be empty.
     *
     * @return the new `BooleanBinding`
     *
     * @since JavaFX 8.0
     */
    fun isEmpty(): BooleanBinding {
        return Bindings.isEmpty(this)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `StringExpression` is not empty.
     *
     * Note: If the value of this `StringExpression` is `null`, it is considered to be empty.
     *
     * @return the new `BooleanBinding`
     *
     * @since JavaFX 8.0
     */
    fun isNotEmpty(): BooleanBinding {
        return Bindings.isNotEmpty(this)
    }

    companion object {

        /**
         * Returns a `StringExpression` that wraps a [ObservableValue]. If the `ObservableValue` is already a
         * `StringExpression`, it will be returned. Otherwise a new [StringBinding] is created that holds the value of
         * the `ObservableValue` converted to a `String`.
         *
         * @param value The source `ObservableValue`
         *
         * @return A `StringExpression` that wraps the `ObservableValue` if necessary
         */
        fun stringExpression(value: ObservableValue<*>): StringExpression {
            return StringFormatter.convert(value)
        }

    }

}