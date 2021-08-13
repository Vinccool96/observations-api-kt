package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableIntValue
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `IntExpression` is a [ObservableIntValue] plus additional convenience methods to generate bindings in a fluent
 * style.
 *
 * A concrete sub-class of `IntExpression` has to implement the method [get], which provides the actual value of this
 * expression.
 */
abstract class IntExpression : NumberExpressionBase(), ObservableIntValue {

    override val intValue: Int
        get() = this.get()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val value: Number?
        get() = this.get()

    override val shortValue: Short
        get() = this.get().toShort()

    override val byteValue: Byte
        get() = this.get().toByte()

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
     * Creates an [ObjectExpression] that holds the value of this `IntExpression`. If the value of this
     * `IntExpression` changes, the value of the `ObjectExpression` will be updated automatically.
     *
     * @return the new `ObjectExpression`
     */
    open fun asObject(): ObjectExpression<Int> {
        return object : ObjectBinding<Int>() {

            init {
                super.bind(this@IntExpression)
            }

            override fun dispose() {
                super.unbind(this@IntExpression)
            }

            override fun computeValue(): Int {
                return this@IntExpression.intValue
            }

        }
    }

    companion object {

        /**
         * Returns a `IntExpression` that wraps a [ObservableIntValue]. If the `ObservableIntValue` is
         * already a `IntExpression`, it will be returned. Otherwise, a new [IntBinding] is created that is bound
         * to the `ObservableIntValue`.
         *
         * @param value The source `ObservableIntValue`
         *
         * @return A `IntExpression` that wraps the `ObservableIntValue` if necessary
         */
        fun intExpression(value: ObservableIntValue): IntExpression {
            return if (value is IntExpression) value else object : IntBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Int {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

        /**
         * Returns an `IntExpression` that wraps an [ObservableValue]. If the `ObservableValue` is already an
         * `IntExpression`, it will be returned. Otherwise, a new [IntBinding] is created that is bound to the
         * `ObservableValue`.
         *
         * Note: this method can be used to convert an [ObjectExpression] or
         * [io.github.vinccool96.observationskt.beans.property.ObjectProperty] of specific number type to
         * `IntExpression`, which is essentially an `ObservableValue<Number>`. See sample below.
         *
         * ```
         * val intProperty: IntProperty = SimpleIntProperty(1)
         * val objectProperty: ObjectProperty<Int> = SimpleObjectProperty(2)
         * val binding: BooleanBinding = intProperty.greaterThan(IntExpression.intExpression(objectProperty))
         * ```
         *
         * Note: null values will be interpreted as `0`
         *
         * @param value The source `ObservableValue`
         * @param T The type of the wrapped number
         *
         * @return An `IntExpression` that wraps the `ObservableValue` if necessary
         */
        fun <T : Number?> intExpression(value: ObservableValue<T>): IntExpression {
            return if (value is IntExpression) value else object : IntBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Int {
                    val v: T = value.value
                    return v?.toInt() ?: 0
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<ObservableValue<T>>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }

    }

}