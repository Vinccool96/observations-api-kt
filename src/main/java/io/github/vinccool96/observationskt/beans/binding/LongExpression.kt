package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableLongValue
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `LongExpression` is a [ObservableLongValue] plus additional convenience methods to generate bindings in a fluent
 * style.
 *
 * A concrete sub-class of `LongExpression` has to implement the method [get], which provides the actual value of this
 * expression.
 */
abstract class LongExpression : NumberExpressionBase(), ObservableLongValue {

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val value: Number?
        get() = this.get()

    override fun unaryMinus(): LongBinding {
        return Bindings.negate(this) as LongBinding
    }

    override fun plus(other: Double): DoubleBinding {
        return Bindings.add(this, other)
    }

    override fun plus(other: Float): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override fun plus(other: Long): LongBinding {
        return Bindings.add(this, other) as LongBinding
    }

    override fun plus(other: Int): LongBinding {
        return Bindings.add(this, other) as LongBinding
    }

    override fun minus(other: Double): DoubleBinding {
        return Bindings.subtract(this, other)
    }

    override fun minus(other: Float): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override fun minus(other: Long): LongBinding {
        return Bindings.subtract(this, other) as LongBinding
    }

    override fun minus(other: Int): LongBinding {
        return Bindings.subtract(this, other) as LongBinding
    }

    override fun times(other: Double): DoubleBinding {
        return Bindings.multiply(this, other)
    }

    override fun times(other: Float): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override fun times(other: Long): LongBinding {
        return Bindings.multiply(this, other) as LongBinding
    }

    override fun times(other: Int): LongBinding {
        return Bindings.multiply(this, other) as LongBinding
    }

    override fun div(other: Double): DoubleBinding {
        return Bindings.divide(this, other)
    }

    override fun div(other: Float): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    override fun div(other: Long): LongBinding {
        return Bindings.divide(this, other) as LongBinding
    }

    override fun div(other: Int): LongBinding {
        return Bindings.divide(this, other) as LongBinding
    }

    /**
     * Creates an [ObjectExpression] that holds the value of this `LongExpression`. If the value of this
     * `LongExpression` changes, the value of the `ObjectExpression` will be updated automatically.
     *
     * @return the new `ObjectExpression`
     */
    open fun asObject(): ObjectExpression<Long> {
        return object : ObjectBinding<Long>() {

            init {
                super.bind(this@LongExpression)
            }

            override fun dispose() {
                super.unbind(this@LongExpression)
            }

            override fun computeValue(): Long {
                return this@LongExpression.longValue
            }

        }
    }

    companion object {

        /**
         * Returns a `LongExpression` that wraps a [ObservableLongValue]. If the `ObservableLongValue` is already a
         * `LongExpression`, it will be returned. Otherwise a new [LongBinding] is created that is bound to the
         * `ObservableLongValue`.
         *
         * @param value The source `ObservableLongValue`
         *
         * @return A `LongExpression` that wraps the `ObservableLongValue` if necessary
         */
        fun longExpression(value: ObservableLongValue): LongExpression {
            return if (value is LongExpression) value else object : LongBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Long {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

        /**
         * Returns a `LongExpression` that wraps an [ObservableValue]. If the `ObservableValue` is already a
         * `LongExpression`, it will be returned. Otherwise a new [LongBinding] is created that is bound to the
         * `ObservableValue`.
         *
         * Note: this method can be used to convert an [ObjectExpression] or
         * [io.github.vinccool96.observationskt.beans.property.ObjectProperty] of specific number type to
         * `LongExpression`, which is essentially an `ObservableValue<Number>`. See sample below.
         *
         * ```
         * val longProperty: LongProperty = SimpleLongProperty(1L)
         * val objectProperty: ObjectProperty<Long> = SimpleObjectProperty(2L)
         * val binding: BooleanBinding = longProperty.greaterThan(LongExpression.longExpression(objectProperty))
         * ```
         *
         * Note: null values will be interpreted as `0L`
         *
         * @param value The source `ObservableValue`
         * @param T The type of the wrapped number
         *
         * @return An `LongExpression` that wraps the `ObservableValue` if necessary
         */
        fun <T : Number?> longExpression(value: ObservableValue<T>): LongExpression {
            return if (value is LongExpression) value else object : LongBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Long {
                    val v: T = value.value
                    return v?.toLong() ?: 0L
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

    }

}