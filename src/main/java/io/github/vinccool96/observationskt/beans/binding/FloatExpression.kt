package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableFloatValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

abstract class FloatExpression : NumberExpressionBase(), ObservableFloatValue {

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val value: Float
        get() = this.get()

    override fun negate(): FloatBinding {
        return Bindings.negate(this) as FloatBinding
    }

    override fun plus(other: Double): DoubleBinding {
        return Bindings.add(this, other)
    }

    override fun plus(other: Float): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override fun plus(other: Long): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override fun plus(other: Int): FloatBinding {
        return Bindings.add(this, other) as FloatBinding
    }

    override fun minus(other: Double): DoubleBinding {
        return Bindings.subtract(this, other)
    }

    override fun minus(other: Float): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override fun minus(other: Long): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override fun minus(other: Int): FloatBinding {
        return Bindings.subtract(this, other) as FloatBinding
    }

    override fun times(other: Double): DoubleBinding {
        return Bindings.multiply(this, other)
    }

    override fun times(other: Float): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override fun times(other: Long): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override fun times(other: Int): FloatBinding {
        return Bindings.multiply(this, other) as FloatBinding
    }

    override fun div(other: Double): DoubleBinding {
        return Bindings.divide(this, other)
    }

    override fun div(other: Float): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    override fun div(other: Long): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    override fun div(other: Int): FloatBinding {
        return Bindings.divide(this, other) as FloatBinding
    }

    companion object {

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

    }

}