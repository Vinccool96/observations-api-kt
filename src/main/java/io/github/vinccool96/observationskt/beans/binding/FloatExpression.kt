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