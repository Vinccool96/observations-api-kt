package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableDoubleValue
import io.github.vinccool96.observationskt.beans.value.ObservableNumberValue
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `DoubleExpression` is a [ObservableDoubleValue] plus additional convenience methods to generate bindings in a
 * fluent style.
 *
 * A concrete sub-class of `DoubleExpression` has to implement the method [get], which provides the actual value of this
 * expression.
 */
abstract class DoubleExpression : NumberExpressionBase(), ObservableDoubleValue {

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get()

    override val shortValue: Short
        get() = this.longValue.toShort()

    override val byteValue: Byte
        get() = this.longValue.toByte()

    override val value: Number?
        get() = this.get()

    override operator fun unaryMinus(): DoubleBinding {
        return Bindings.negate(this) as DoubleBinding
    }

    override operator fun plus(other: ObservableNumberValue): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override operator fun plus(other: Double): DoubleBinding {
        return Bindings.add(this, other)
    }

    override operator fun plus(other: Float): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override operator fun plus(other: Long): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override operator fun plus(other: Int): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override operator fun plus(other: Short): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override operator fun plus(other: Byte): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override operator fun minus(other: ObservableNumberValue): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override operator fun minus(other: Double): DoubleBinding {
        return Bindings.subtract(this, other)
    }

    override operator fun minus(other: Float): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override operator fun minus(other: Long): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override operator fun minus(other: Int): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override operator fun minus(other: Short): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override operator fun minus(other: Byte): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override operator fun times(other: ObservableNumberValue): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override operator fun times(other: Double): DoubleBinding {
        return Bindings.multiply(this, other)
    }

    override operator fun times(other: Float): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override operator fun times(other: Long): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override operator fun times(other: Int): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override operator fun times(other: Short): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override operator fun times(other: Byte): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override operator fun div(other: ObservableNumberValue): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    override operator fun div(other: Double): DoubleBinding {
        return Bindings.divide(this, other)
    }

    override operator fun div(other: Float): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    override operator fun div(other: Long): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    override operator fun div(other: Int): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    override operator fun div(other: Short): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    override operator fun div(other: Byte): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    /**
     * Creates an [ObjectExpression] that holds the value of this `DoubleExpression`. If the value of this
     * `DoubleExpression` changes, the value of the `ObjectExpression` will be updated automatically.
     *
     * @return the new `ObjectExpression`
     */
    open fun asObject(): ObjectExpression<Double> {
        return object : ObjectBinding<Double>() {

            init {
                super.bind(this@DoubleExpression)
            }

            override fun dispose() {
                super.unbind(this@DoubleExpression)
            }

            override fun computeValue(): Double {
                return this@DoubleExpression.doubleValue
            }

        }
    }

    companion object {

        /**
         * Returns a `DoubleExpression` that wraps a [ObservableDoubleValue]. If the `ObservableDoubleValue` is already
         * a `DoubleExpression`, it will be returned. Otherwise, a new [DoubleBinding] is created that is bound to the
         * `ObservableDoubleValue`.
         *
         * @param value The source `ObservableDoubleValue`
         *
         * @return A `DoubleExpression` that wraps the `ObservableDoubleValue` if necessary
         */
        fun doubleExpression(value: ObservableDoubleValue): DoubleExpression {
            return if (value is DoubleExpression) value else object : DoubleBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Double {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

        /**
         * Returns a `DoubleExpression` that wraps an [ObservableValue]. If the `ObservableValue` is already a
         * `DoubleExpression`, it will be returned. Otherwise, a new [DoubleBinding] is created that is bound to the
         * `ObservableValue`.
         *
         * Note: this method can be used to convert an [ObjectExpression] or
         * [io.github.vinccool96.observationskt.beans.property.ObjectProperty] of specific number type to
         * `DoubleExpression`, which is essentially an `ObservableValue<Number>`. See sample below.
         *
         * ```
         * val doubleProperty: DoubleProperty = SimpleDoubleProperty(1.0)
         * val objectProperty: ObjectProperty<Double> = SimpleObjectProperty(2.0)
         * val binding: BooleanBinding = doubleProperty.greaterThan(DoubleExpression.doubleExpression(objectProperty))
         * ```
         *
         * Note: null values will be interpreted as `0.0`
         *
         * @param value The source `ObservableValue`
         * @param T The type of the wrapped number
         *
         * @return A `DoubleExpression` that wraps the `ObservableValue` if necessary
         */
        fun <T : Number?> doubleExpression(value: ObservableValue<T>): DoubleExpression {
            return if (value is DoubleExpression) value else object : DoubleBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Double {
                    val v: T = value.value
                    return v?.toDouble() ?: 0.0
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<ObservableValue<T>>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

    }

}