package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableIntegerValue
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `IntegerExpression` is a [ObservableIntegerValue] plus additional convenience methods to generate bindings in a
 * fluent style.
 *
 * A concrete sub-class of `IntegerExpression` has to implement the method [get], which provides the actual value of
 * this expression.
 *
 * @since JavaFX 2.0
 */
abstract class IntegerExpression : NumberExpressionBase(), ObservableIntegerValue {

    override val intValue: Int
        get() = this.get()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val value: Number
        get() = this.get()

    override fun negate(): IntegerBinding {
        return Bindings.negate(this) as IntegerBinding
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

    override fun plus(other: Int): IntegerBinding {
        return Bindings.add(this, other) as IntegerBinding
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

    override fun minus(other: Int): IntegerBinding {
        return Bindings.subtract(this, other) as IntegerBinding
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

    override fun times(other: Int): IntegerBinding {
        return Bindings.multiply(this, other) as IntegerBinding
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

    override fun div(other: Int): IntegerBinding {
        return Bindings.divide(this, other) as IntegerBinding
    }

    /**
     * Creates an [ObjectExpression] that holds the value of this `IntegerExpression`. If the value of this
     * `IntegerExpression` changes, the value of the `ObjectExpression` will be updated automatically.
     *
     * @return the new `ObjectExpression`
     *
     * @since JavaFX 8.0
     */
    open fun asObject(): ObjectExpression<Int> {
        return object : ObjectBinding<Int>() {

            init {
                super.bind(this@IntegerExpression)
            }

            override fun dispose() {
                super.unbind(this@IntegerExpression)
            }

            override fun computeValue(): Int {
                return this@IntegerExpression.intValue
            }

        }
    }

    companion object {

        /**
         * Returns a `IntegerExpression` that wraps a [ObservableIntegerValue]. If the `ObservableIntegerValue` is
         * already a `IntegerExpression`, it will be returned. Otherwise a new [IntegerBinding] is created that is bound
         * to the `ObservableIntegerValue`.
         *
         * @param value The source `ObservableIntegerValue`
         *
         * @return A `IntegerExpression` that wraps the `ObservableIntegerValue` if necessary
         */
        fun integerExpression(value: ObservableIntegerValue): IntegerExpression {
            return if (value is IntegerExpression) value else object : IntegerBinding() {

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
         * Returns an `IntegerExpression` that wraps an [ObservableValue]. If the `ObservableValue` is already an
         * `IntegerExpression`, it will be returned. Otherwise a new [IntegerBinding] is created that is bound to the
         * `ObservableValue`.
         *
         * Note: this method can be used to convert an [ObjectExpression] or
         * [io.github.vinccool96.observationskt.beans.property.ObjectProperty] of specific number type to
         * `IntegerExpression`, which is essentially an `ObservableValue<Number>`. See sample below.
         *
         * ```
         * val intProperty: IntegerProperty = SimpleIntegerProperty(1)
         * val objectProperty: ObjectProperty<Int> = new SimpleObjectProperty(2)
         * val binding: BooleanBinding = intProperty.greaterThan(IntegerExpression.integerExpression(objectProperty))
         * ```
         *
         * Note: null values will be interpreted as `0`
         *
         * @param value
         *         The source `ObservableValue`
         * @param T
         *         The type of the wrapped number
         *
         * @return An `IntegerExpression` that wraps the `ObservableValue` if necessary
         *
         * @since JavaFX 8.0
         */
        fun <T : Number?> integerExpression(value: ObservableValue<T>): IntegerExpression {
            return if (value is IntegerExpression) value else object : IntegerBinding() {

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