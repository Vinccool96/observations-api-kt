package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableLongValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

abstract class LongExpression : NumberExpressionBase(), ObservableLongValue {

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val value: Long
        get() = this.get()

    override fun negate(): LongBinding {
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

    companion object {

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

    }

}