package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableNumberValue
import java.util.*

/**
 * A `NumberExpression` is a [ObservableNumberValue] plus additional convenience methods to generate bindings in a
 * fluent style.
 *
 * This API allows to mix types when defining arithmetic operations. The type of the result is defined by the same rules
 * as in the Java Language.
 *
 * 1. If one of the operands is a double, the result is a double.
 * 2. If not and one of the operands is a float, the result is a float.
 * 3. If not and one of the operands is a long, the result is a long.
 * 4. The result is an integer otherwise.
 *
 * To be able to deal with an unspecified return type, two interfaces `NumberExpression` and its counterpart
 * [NumberBinding] were introduced. That means if the return type is specified as `NumberBinding`, the method will
 * either return a [DoubleBinding], [FloatBinding], [LongBinding] or [IntegerBinding], depending on the types of the
 * operands.
 *
 * The API tries to do its best in determining the correct return type, e.g. combining a [ObservableNumberValue] with a
 * primitive double will always result in a [DoubleBinding]. In cases where the return type is not known by the API, it
 * is the responsibility of the developer to call the correct getter ([intValue], etc.). If the internal representation
 * does not match the type of the getter, a standard cast is done.
 *
 * @since JavaFX 2.0
 */
interface NumberExpression : ObservableNumberValue {

    // ===============================================================
    // Negation

    /**
     * Creates a new [NumberBinding] that calculates the negation of `NumberExpression`.
     *
     * @return the new `NumberBinding`
     */
    fun negate(): NumberBinding

    // ===============================================================
    // Plus

    /**
     * Creates a new [NumberBinding] that calculates the sum of this `NumberExpression` and another
     * [ObservableNumberValue].
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    operator fun plus(other: ObservableNumberValue): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the sum of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun plus(other: Double): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the sum of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun plus(other: Float): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the sum of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun plus(other: Long): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the sum of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun plus(other: Int): NumberBinding

    // ===============================================================
    // Minus

    /**
     * Creates a new [NumberBinding] that calculates the difference of this `NumberExpression` and another
     * [ObservableNumberValue].
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     *
     * @throws NullPointerException
     * if the other `ObservableNumberValue` is `null`
     */
    operator fun minus(other: ObservableNumberValue): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the difference of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun minus(other: Double): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the difference of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun minus(other: Float): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the difference of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun minus(other: Long): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the difference of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun minus(other: Int): NumberBinding

    // ===============================================================
    // Times

    /**
     * Creates a new [NumberBinding] that calculates the product of this `NumberExpression` and another
     * [ObservableNumberValue].
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    operator fun times(other: ObservableNumberValue): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the product of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun times(other: Double): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the product of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun times(other: Float): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the product of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun times(other: Long): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the product of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun times(other: Int): NumberBinding

    // ===============================================================
    // DividedBy

    /**
     * Creates a new [NumberBinding] that calculates the division of this `NumberExpression` and another
     * [ObservableNumberValue].
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    operator fun div(other: ObservableNumberValue): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the division of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun div(other: Double): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the division of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun div(other: Float): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the division of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun div(other: Long): NumberBinding

    /**
     * Creates a new [NumberBinding] that calculates the division of this `NumberExpression` and a constant value.
     *
     * @param other the constant value
     *
     * @return the new `NumberBinding`
     */
    operator fun div(other: Int): NumberBinding

    // ===============================================================
    // IsEqualTo

    /**
     * Creates a new [BooleanBinding] that holds `true` if this and another [ObservableNumberValue] are equal.
     *
     * When comparing floating-point numbers it is recommended to use the [isNotEqualTo] method that allows a small
     * tolerance.
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: ObservableNumberValue): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this and another [ObservableNumberValue] are equal (with a
     * tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param other the second `ObservableNumberValue`
     * @param epsilon the tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: ObservableNumberValue, epsilon: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is equal to a constant value (with a
     * tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param other the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: Double, epsilon: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is equal to a constant value (with a
     * tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param other the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: Float, epsilon: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is equal to a constant value.
     *
     * When comparing floating-point numbers it is recommended to use the [isNotEqualTo] method that allows a small
     * tolerance.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: Long): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is equal to a constant value (with a
     * tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param other the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: Long, epsilon: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is equal to a constant value.
     *
     * When comparing floating-point numbers it is recommended to use the [isNotEqualTo] method that allows a small
     * tolerance.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: Int): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is equal to a constant value (with a
     * tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers.
     *
     * @param other the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isEqualTo(other: Int, epsilon: Double): BooleanBinding

    // ===============================================================
    // IsNotEqualTo

    /**
     * Creates a new [BooleanBinding] that holds `true` if this and another [ObservableNumberValue]
     * are not equal.
     *
     *
     * When comparing floating-point numbers it is recommended to use the [isNotEqualTo] method that allows a small
     * tolerance.
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: ObservableNumberValue): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this and another [ObservableNumberValue] are not equal (with
     * a tolerance).
     *
     * Two operands `a` and `b` are considered not equal if `abs(a-b) > epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param other the second `ObservableNumberValue`
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: ObservableNumberValue, epsilon: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is not equal to a constant value
     * (with a tolerance).
     *
     * Two operands `a` and `b` are considered not equal if `abs(a-b) > epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers.
     *
     * @param other the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: Double, epsilon: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is not equal to a constant value
     * (with a tolerance).
     *
     * Two operands `a` and `b` are considered not equal if `abs(a-b) > epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers.
     *
     * @param other the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: Float, epsilon: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is not equal to a constant value.
     *
     * When comparing floating-point numbers it is recommended to use the [isNotEqualTo] method that allows a small
     * tolerance.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: Long): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is not equal to a constant value
     * (with a tolerance).
     *
     * Two operands `a` and `b` are considered not equal if `abs(a-b) > epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers.
     *
     * @param other the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: Long, epsilon: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is not equal to a
     * constant value.
     *
     * When comparing floating-point numbers it is recommended to use the [isNotEqualTo] method that allows a small
     * tolerance.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: Int): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is not equal to a constant value
     * (with a tolerance).
     *
     * Two operands `a` and `b` are considered not equal if `abs(a-b) > epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers.
     *
     * @param other the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun isNotEqualTo(other: Int, epsilon: Double): BooleanBinding

    // ===============================================================
    // IsGreaterThan

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than another
     * [ObservableNumberValue].
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     *
     * @throws NullPointerException
     * if the other `ObservableNumberValue` is `null`
     */
    fun greaterThan(other: ObservableNumberValue): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than a constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(other: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than a constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(other: Float): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than a constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(other: Long): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than a constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(other: Int): BooleanBinding

    // ===============================================================
    // IsLesserThan

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is lesser than another
     * [ObservableNumberValue].
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     *
     * @throws NullPointerException
     * if the other `ObservableNumberValue` is `null`
     */
    fun lessThan(other: ObservableNumberValue): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is lesser than a constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(other: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is lesser than a constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(other: Float): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is lesser than a constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(other: Long): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is lesser than a constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(other: Int): BooleanBinding

    // ===============================================================
    // IsGreaterThanOrEqualTo

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than or equal to another
     * [ObservableNumberValue].
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     *
     * @throws NullPointerException
     * if the other `ObservableNumberValue` is `null`
     */
    fun greaterThanOrEqualTo(other: ObservableNumberValue): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than or equal to a
     * constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqualTo(other: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than or equal to a
     * constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqualTo(other: Float): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than or equal to a
     * constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqualTo(other: Long): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is greater than or equal to a
     * constant value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqualTo(other: Int): BooleanBinding

    // ===============================================================
    // IsLessThanOrEqualTo

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is less than or equal to another
     * [ObservableNumberValue].
     *
     * @param other the second `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     *
     * @throws NullPointerException
     * if the other `ObservableNumberValue` is `null`
     */
    fun lessThanOrEqualTo(other: ObservableNumberValue): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is less than or equal to a constant
     * value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqualTo(other: Double): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is less than or equal to a constant
     * value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqualTo(other: Float): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is less than or equal to a constant
     * value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqualTo(other: Long): BooleanBinding

    /**
     * Creates a new [BooleanBinding] that holds `true` if this `NumberExpression` is less than or equal to a constant
     * value.
     *
     * @param other the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqualTo(other: Int): BooleanBinding

    // ===============================================================
    // String conversions

    /**
     * Creates a [StringBinding] that holds the value of the `NumberExpression` turned into a `String`. If the value of
     * this `NumberExpression` changes, the value of the `StringBinding` will be updated automatically.
     *
     *
     * The conversion is done without any formatting applied.
     *
     * @return the new `StringBinding`
     */
    fun asString(): StringBinding

    /**
     * Creates a [StringBinding] that holds the value of the `NumberExpression` turned into a `String`. If the value of
     * this `NumberExpression` changes, the value of the `StringBinding` will be updated automatically.
     *
     * The result is formatted according to the formatting `String`. See [java.util.Formatter] for formatting rules.
     *
     * @param format
     * the formatting `String`
     *
     * @return the new `StringBinding`
     */
    fun asString(format: String): StringBinding

    /**
     * Creates a [StringBinding] that holds the value of the `NumberExpression` turned into a `String`. If the value of
     * this `NumberExpression` changes, the value of the `StringBinding` will be updated automatically.
     *
     * The result is formatted according to the formatting `String` and the passed in `Locale`. See
     * [java.util.Formatter] for formatting rules. See [Locale] for details on `Locale`.
     *
     * @param locale the Locale
     * @param format the formatting `String`
     *
     * @return the new `StringBinding`
     *
     * @see Locale
     */
    fun asString(locale: Locale, format: String): StringBinding

}