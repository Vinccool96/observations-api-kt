package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableShortValue
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `ShortExpression` is a [ObservableShortValue] plus additional convenience methods to generate bindings in a fluent
 * style.
 *
 * A concrete sub-class of `ShortExpression` has to implement the method [get], which provides the actual value of this
 * expression.
 */
abstract class ShortExpression : NumberExpressionBase(), ObservableShortValue {

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val shortValue: Short
        get() = this.get()

    override val byteValue: Byte
        get() = this.get().toByte()

    override val value: Number?
        get() = this.get()

    override operator fun unaryMinus(): IntBinding {
        return Bindings.negate(this) as IntBinding
    }

    override operator fun plus(other: Double): DoubleBinding {
        return Bindings.add(this, other)
    }

    override operator fun plus(other: Float): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override operator fun plus(other: Long): LongBinding {
        return Bindings.add(this, other) as LongBinding
    }

    override operator fun plus(other: Int): IntBinding {
        return Bindings.add(this, other) as IntBinding
    }

    override operator fun plus(other: Short): IntBinding {
        return Bindings.add(this, other) as IntBinding
    }

    override operator fun minus(other: Double): DoubleBinding {
        return Bindings.subtract(this, other)
    }

    override operator fun minus(other: Float): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override operator fun minus(other: Long): LongBinding {
        return Bindings.subtract(this, other) as LongBinding
    }

    override operator fun minus(other: Int): IntBinding {
        return Bindings.subtract(this, other) as IntBinding
    }

    override operator fun minus(other: Short): IntBinding {
        return Bindings.subtract(this, other) as IntBinding
    }

    override operator fun times(other: Double): DoubleBinding {
        return Bindings.multiply(this, other)
    }

    override operator fun times(other: Float): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override operator fun times(other: Long): LongBinding {
        return Bindings.multiply(this, other) as LongBinding
    }

    override operator fun times(other: Int): IntBinding {
        return Bindings.multiply(this, other) as IntBinding
    }

    override operator fun times(other: Short): IntBinding {
        return Bindings.multiply(this, other) as IntBinding
    }

    override operator fun div(other: Double): DoubleBinding {
        return Bindings.divide(this, other)
    }

    override operator fun div(other: Float): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    override operator fun div(other: Long): LongBinding {
        return Bindings.divide(this, other) as LongBinding
    }

    override operator fun div(other: Int): IntBinding {
        return Bindings.divide(this, other) as IntBinding
    }

    override operator fun div(other: Short): IntBinding {
        return Bindings.divide(this, other) as IntBinding
    }

    /**
     * Creates an [ObjectExpression] that holds the value of this `ShortExpression`. If the value of this
     * `ShortExpression` changes, the value of the `ObjectExpression` will be updated automatically.
     *
     * @return the new `ObjectExpression`
     */
    open fun asObject(): ObjectExpression<Short> {
        return object : ObjectBinding<Short>() {

            init {
                super.bind(this@ShortExpression)
            }

            override fun dispose() {
                super.unbind(this@ShortExpression)
            }

            override fun computeValue(): Short {
                return this@ShortExpression.shortValue
            }

        }
    }

    companion object {

        /**
         * Returns a `ShortExpression` that wraps a [ObservableShortValue]. If the `ObservableShortValue` is already a
         * `ShortExpression`, it will be returned. Otherwise, a new [ShortBinding] is created that is bound to the
         * `ObservableShortValue`.
         *
         * @param value The source `ObservableShortValue`
         *
         * @return A `ShortExpression` that wraps the `ObservableShortValue` if necessary
         */
        fun shortExpression(value: ObservableShortValue): ShortExpression {
            return if (value is ShortExpression) value else object : ShortBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Short {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

        /**
         * Returns a `ShortExpression` that wraps an [ObservableValue]. If the `ObservableValue` is already a
         * `ShortExpression`, it will be returned. Otherwise, a new [ShortBinding] is created that is bound to the
         * `ObservableValue`.
         *
         * Note: this method can be used to convert an [ObjectExpression] or
         * [io.github.vinccool96.observationskt.beans.property.ObjectProperty] of specific number type to
         * `ShortExpression`, which is essentially an `ObservableValue<Number>`. See sample below.
         *
         * ```
         * val shortProperty: ShortProperty = SimpleShortProperty(1)
         * val objectProperty: ObjectProperty<Short> = SimpleObjectProperty(2)
         * val binding: BooleanBinding = shortProperty.greaterThan(Short.shortExpression(objectProperty))
         * ```
         *
         * Note: null values will be interpreted as `0`
         *
         * @param value The source `ObservableValue`
         * @param T The type of the wrapped number
         *
         * @return A `ShortExpression` that wraps the `ObservableValue` if necessary
         */
        fun <T : Number?> shortExpression(value: ObservableValue<T>): ShortExpression {
            return if (value is ShortExpression) value else object : ShortBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Short {
                    val v: T = value.value
                    return v?.toShort() ?: 0
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

    }

}