package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableIntegerValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A {@code IntegerExpression} is a {@link ObservableIntegerValue} plus additional convenience methods to generate
 * bindings in a fluent style.
 *
 * A concrete sub-class of {@code IntegerExpression} has to implement the method [get],
 * which provides the actual value of this expression.
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

    override val value: Int
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

    companion object {

        /**
         * Returns a {@code IntegerExpression} that wraps a {@link ObservableIntegerValue}. If the {@code
         * ObservableIntegerValue} is already a {@code IntegerExpression}, it will be returned. Otherwise a new {@link
         * IntegerBinding} is created that is bound to the {@code ObservableIntegerValue}.
         *
         * @param value The source {@code ObservableIntegerValue}
         *
         * @return A {@code IntegerExpression} that wraps the {@code ObservableIntegerValue} if necessary
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

    }

}