package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableDoubleValue
import io.github.vinccool96.observationskt.beans.value.ObservableNumberValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

abstract class DoubleExpression : NumberExpressionBase(), ObservableDoubleValue {

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get()

    override val value: Double
        get() = this.get()

    override fun negate(): DoubleBinding {
        return Bindings.negate(this) as DoubleBinding
    }

    override fun plus(other: ObservableNumberValue): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override fun plus(other: Double): DoubleBinding {
        return Bindings.add(this, other)
    }

    override fun plus(other: Float): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override fun plus(other: Long): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override fun plus(other: Int): DoubleBinding {
        return Bindings.add(this, other) as DoubleBinding
    }

    override fun minus(other: ObservableNumberValue): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override fun minus(other: Double): DoubleBinding {
        return Bindings.subtract(this, other)
    }

    override fun minus(other: Float): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override fun minus(other: Long): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override fun minus(other: Int): DoubleBinding {
        return Bindings.subtract(this, other) as DoubleBinding
    }

    override fun times(other: ObservableNumberValue): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override fun times(other: Double): DoubleBinding {
        return Bindings.multiply(this, other)
    }

    override fun times(other: Float): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override fun times(other: Long): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override fun times(other: Int): DoubleBinding {
        return Bindings.multiply(this, other) as DoubleBinding
    }

    override fun div(other: ObservableNumberValue): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    override fun div(other: Double): DoubleBinding {
        return Bindings.divide(this, other)
    }

    override fun div(other: Float): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    override fun div(other: Long): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    override fun div(other: Int): DoubleBinding {
        return Bindings.divide(this, other) as DoubleBinding
    }

    companion object {

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

    }

}