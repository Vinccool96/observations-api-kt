package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.*
import io.github.vinccool96.observationskt.sun.binding.StringFormatter
import java.util.*

/**
 * A `NumberExpressionBase` contains convenience methods to generate bindings in a fluent style, that are common
 * to all NumberExpression subclasses.
 *
 * NumberExpressionBase serves as a place for common code of specific NumberExpression subclasses for the specific
 * number type.
 *
 * @see IntExpression
 * @see LongExpression
 * @see FloatExpression
 * @see DoubleExpression
 */
abstract class NumberExpressionBase : NumberExpression {

    override fun plus(other: ObservableNumberValue): NumberBinding {
        return Bindings.add(this, other)
    }

    override fun minus(other: ObservableNumberValue): NumberBinding {
        return Bindings.subtract(this, other)
    }

    override fun times(other: ObservableNumberValue): NumberBinding {
        return Bindings.multiply(this, other)
    }

    override fun div(other: ObservableNumberValue): NumberBinding {
        return Bindings.divide(this, other)
    }

    // ===============================================================
    // IsEqualTo

    override fun isEqualTo(other: ObservableNumberValue): BooleanBinding {
        return Bindings.equal(this, other)
    }

    override fun isEqualTo(other: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return Bindings.equal(this, other, epsilon)
    }

    override fun isEqualTo(other: Double, epsilon: Double): BooleanBinding {
        return Bindings.equal(this, other, epsilon)
    }

    override fun isEqualTo(other: Float, epsilon: Double): BooleanBinding {
        return Bindings.equal(this, other, epsilon)
    }

    override fun isEqualTo(other: Long): BooleanBinding {
        return Bindings.equal(this, other)
    }

    override fun isEqualTo(other: Long, epsilon: Double): BooleanBinding {
        return Bindings.equal(this, other, epsilon)
    }

    override fun isEqualTo(other: Int): BooleanBinding {
        return Bindings.equal(this, other)
    }

    override fun isEqualTo(other: Int, epsilon: Double): BooleanBinding {
        return Bindings.equal(this, other, epsilon)
    }

    // ===============================================================
    // IsNotEqualTo

    override fun isNotEqualTo(other: ObservableNumberValue): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    override fun isNotEqualTo(other: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return Bindings.notEqual(this, other, epsilon)
    }

    override fun isNotEqualTo(other: Double, epsilon: Double): BooleanBinding {
        return Bindings.notEqual(this, other, epsilon)
    }

    override fun isNotEqualTo(other: Float, epsilon: Double): BooleanBinding {
        return Bindings.notEqual(this, other, epsilon)
    }

    override fun isNotEqualTo(other: Long): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    override fun isNotEqualTo(other: Long, epsilon: Double): BooleanBinding {
        return Bindings.notEqual(this, other, epsilon)
    }

    override fun isNotEqualTo(other: Int): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    override fun isNotEqualTo(other: Int, epsilon: Double): BooleanBinding {
        return Bindings.notEqual(this, other, epsilon)
    }

    // ===============================================================
    // IsGreaterThan

    override fun greaterThan(other: ObservableNumberValue): BooleanBinding {
        return Bindings.greaterThan(this, other)
    }

    override fun greaterThan(other: Double): BooleanBinding {
        return Bindings.greaterThan(this, other)
    }

    override fun greaterThan(other: Float): BooleanBinding {
        return Bindings.greaterThan(this, other)
    }

    override fun greaterThan(other: Long): BooleanBinding {
        return Bindings.greaterThan(this, other)
    }

    override fun greaterThan(other: Int): BooleanBinding {
        return Bindings.greaterThan(this, other)
    }

    // ===============================================================
    // IsLessThan

    override fun lessThan(other: ObservableNumberValue): BooleanBinding {
        return Bindings.lessThan(this, other)
    }

    override fun lessThan(other: Double): BooleanBinding {
        return Bindings.lessThan(this, other)
    }

    override fun lessThan(other: Float): BooleanBinding {
        return Bindings.lessThan(this, other)
    }

    override fun lessThan(other: Long): BooleanBinding {
        return Bindings.lessThan(this, other)
    }

    override fun lessThan(other: Int): BooleanBinding {
        return Bindings.lessThan(this, other)
    }

    // ===============================================================
    // IsGreaterThanOrEqualTo

    override fun greaterThanOrEqualTo(other: ObservableNumberValue): BooleanBinding {
        return Bindings.greaterThanOrEqual(this, other)
    }

    override fun greaterThanOrEqualTo(other: Double): BooleanBinding {
        return Bindings.greaterThanOrEqual(this, other)
    }

    override fun greaterThanOrEqualTo(other: Float): BooleanBinding {
        return Bindings.greaterThanOrEqual(this, other)
    }

    override fun greaterThanOrEqualTo(other: Long): BooleanBinding {
        return Bindings.greaterThanOrEqual(this, other)
    }

    override fun greaterThanOrEqualTo(other: Int): BooleanBinding {
        return Bindings.greaterThanOrEqual(this, other)
    }

    // ===============================================================
    // IsLessThanOrEqualTo

    override fun lessThanOrEqualTo(other: ObservableNumberValue): BooleanBinding {
        return Bindings.lessThanOrEqual(this, other)
    }

    override fun lessThanOrEqualTo(other: Double): BooleanBinding {
        return Bindings.lessThanOrEqual(this, other)
    }

    override fun lessThanOrEqualTo(other: Float): BooleanBinding {
        return Bindings.lessThanOrEqual(this, other)
    }

    override fun lessThanOrEqualTo(other: Long): BooleanBinding {
        return Bindings.lessThanOrEqual(this, other)
    }

    override fun lessThanOrEqualTo(other: Int): BooleanBinding {
        return Bindings.lessThanOrEqual(this, other)
    }

    // ===============================================================
    // String conversions

    override fun asString(): StringBinding {
        return StringFormatter.convert(this) as StringBinding
    }

    override fun asString(format: String): StringBinding {
        return Bindings.format(format, this) as StringBinding
    }

    override fun asString(locale: Locale, format: String): StringBinding {
        return Bindings.format(locale, format, this) as StringBinding
    }

    companion object {

        /**
         * Returns a `NumberExpressionBase` that wraps a [ObservableNumberValue]. If the `ObservableNumberValue` is
         * already an instance of `NumberExpressionBase`, it will be returned. Otherwise a new [NumberBinding] is
         * created that is bound to the `ObservableNumberValue`.
         *
         * @param value The source `ObservableNumberValue`
         *
         * @return An `NumberExpressionBase` that wraps the `ObservableNumberValue` if necessary
         */
        fun numberExpression(value: ObservableNumberValue): NumberExpressionBase {
            return when (value) {
                is NumberExpressionBase -> value
                is ObservableIntValue -> IntExpression.intExpression(value)
                is ObservableDoubleValue -> DoubleExpression.doubleExpression(value)
                is ObservableFloatValue -> FloatExpression.floatExpression(value)
                is ObservableLongValue -> LongExpression.longExpression(value)
                else -> null
            } ?: throw IllegalArgumentException("Unsupported Type")
        }

    }

}