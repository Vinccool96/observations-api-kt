package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableFloatValue
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `FloatExpression` is a [ObservableFloatValue] plus additional convenience methods to generate bindings in a fluent
 * style.
 *
 * A concrete sub-class of `FloatExpression` has to implement the method [get], which provides the actual value of this
 * expression.
 */
abstract class FloatExpression : NumberExpressionBase(), ObservableFloatValue {

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val shortValue: Short
        get() = this.intValue.toShort()

    override val byteValue: Byte
        get() = this.intValue.toByte()

    override val value: Number?
        get() = this.get()

    override operator fun unaryMinus(): FloatBinding {
        return Bindings.negate(this) as FloatBinding
    }

    override operator fun plus(other: Double): DoubleBinding {
        return Bindings.add(this, other)
    }

    override operator fun plus(other: Float): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override operator fun plus(other: Long): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override operator fun plus(other: Int): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override operator fun plus(other: Short): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override operator fun plus(other: Byte): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override operator fun minus(other: Double): DoubleBinding {
        return Bindings.subtract(this, other)
    }

    override operator fun minus(other: Float): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override operator fun minus(other: Long): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override operator fun minus(other: Int): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override operator fun minus(other: Short): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override operator fun minus(other: Byte): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override operator fun times(other: Double): DoubleBinding {
        return Bindings.multiply(this, other)
    }

    override operator fun times(other: Float): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override operator fun times(other: Long): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override operator fun times(other: Int): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override operator fun times(other: Short): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override operator fun times(other: Byte): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override operator fun div(other: Double): DoubleBinding {
        return Bindings.divide(this, other)
    }

    override operator fun div(other: Float): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    override operator fun div(other: Long): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    override operator fun div(other: Int): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    override operator fun div(other: Short): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    override operator fun div(other: Byte): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    /**
     * Creates an [ObjectExpression] that holds the value of this `FloatExpression`. If the value of this
     * `FloatExpression` changes, the value of the `ObjectExpression` will be updated automatically.
     *
     * @return the new `ObjectExpression`
     */
    open fun asObject(): ObjectExpression<Float> {
        return object : ObjectBinding<Float>() {

            init {
                super.bind(this@FloatExpression)
            }

            override fun dispose() {
                super.unbind(this@FloatExpression)
            }

            override fun computeValue(): Float {
                return this@FloatExpression.floatValue
            }

        }
    }

    companion object {

        /**
         * Returns a `FloatExpression` that wraps a [ObservableFloatValue]. If the `ObservableFloatValue` is already
         * a `FloatExpression`, it will be returned. Otherwise, a new [FloatBinding] is created that is bound to the
         * `ObservableFloatValue`.
         *
         * @param value The source `ObservableFloatValue`
         *
         * @return A `FloatExpression` that wraps the `ObservableFloatValue` if necessary
         */
        fun floatExpression(value: ObservableFloatValue): FloatExpression {
            return if (value is FloatExpression) value else object : FloatBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Float {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

        /**
         * Returns a `FloatExpression` that wraps an [ObservableValue]. If the `ObservableValue` is already a
         * `FloatExpression`, it will be returned. Otherwise, a new [FloatBinding] is created that is bound to the
         * `ObservableValue`.
         *
         * Note: this method can be used to convert an [ObjectExpression] or
         * [io.github.vinccool96.observationskt.beans.property.ObjectProperty] of specific number type to
         * `FloatExpression`, which is essentially an `ObservableValue<Number>`. See sample below.
         *
         * ```
         * val floatProperty: FloatProperty = SimpleFloatProperty(1.0F)
         * val objectProperty: ObjectProperty<Float> = SimpleObjectProperty(2.0F)
         * val binding: BooleanBinding = floatProperty.greaterThan(FloatExpression.floatExpression(objectProperty))
         * ```
         *
         * Note: null values will be interpreted as `0.0`
         *
         * @param value The source `ObservableValue`
         * @param T The type of the wrapped number
         *
         * @return A `FloatExpression` that wraps the `ObservableValue` if necessary
         */
        fun <T : Number?> floatExpression(value: ObservableValue<T>): FloatExpression {
            return if (value is FloatExpression) value else object : FloatBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Float {
                    val v: T = value.value
                    return v?.toFloat() ?: 0.0F
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

    }

}