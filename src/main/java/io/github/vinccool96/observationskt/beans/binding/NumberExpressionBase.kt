package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.*

/**
 * A {@code NumberExpressionBase} contains convenience methods to generate bindings in a fluent style, that are common
 * to all NumberExpression subclasses.
 * <p>
 * NumberExpressionBase serves as a place for common code of specific NumberExpression subclasses for the specific
 * number type.
 *
 * @see IntegerExpression
 * @see LongExpression
 * @see FloatExpression
 * @see DoubleExpression
 * @since JavaFX 2.0
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

    companion object {

        /**
         * Returns a {@code NumberExpressionBase} that wraps a {@link ObservableNumberValue}. If the {@code
         * ObservableNumberValue} is already an instance of {@code NumberExpressionBase}, it will be returned. Otherwise a
         * new {@link NumberBinding} is created that is bound to the {@code ObservableNumberValue}.
         *
         * @param value The source {@code ObservableNumberValue}
         *
         * @return An {@code NumberExpressionBase} that wraps the {@code ObservableNumberValue} if necessary
         */
        fun numberExpression(value: ObservableNumberValue): NumberExpressionBase {
            return when (value) {
                is NumberExpressionBase -> value
                is ObservableIntegerValue -> IntegerExpression.integerExpression(value)
                is ObservableDoubleValue -> DoubleExpression.doubleExpression(value)
                is ObservableFloatValue -> FloatExpression.floatExpression(value)
                is ObservableLongValue -> LongExpression.longExpression(value)
                else -> null
            } ?: throw IllegalArgumentException("Unsupported Type")
        }

    }

}