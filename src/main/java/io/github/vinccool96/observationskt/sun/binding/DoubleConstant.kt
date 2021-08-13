package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableDoubleValue

/**
 * A simple DoubleExpression that represents a single constant value.
 */
class DoubleConstant private constructor(override val value: Double) : ObservableDoubleValue {

    override fun get(): Double {
        return this.value
    }

    override fun addListener(listener: InvalidationListener) {
        // no-op
    }

    override fun removeListener(listener: InvalidationListener) {
        // no-op
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        // no-op
        return false
    }

    override fun addListener(listener: ChangeListener<in Number?>) {
        // no-op
    }

    override fun removeListener(listener: ChangeListener<in Number?>) {
        // no-op
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Number?>): Boolean {
        // no-op
        return false
    }

    override val intValue: Int
        get() = this.value.toInt()

    override val longValue: Long
        get() = this.value.toLong()

    override val floatValue: Float
        get() = this.value.toFloat()

    override val doubleValue: Double
        get() = this.value

    override val shortValue: Short
        get() = this.longValue.toShort()

    override val byteValue: Byte
        get() = this.longValue.toByte()

    companion object {

        fun valueOf(value: Double): DoubleConstant {
            return DoubleConstant(value)
        }

    }

}