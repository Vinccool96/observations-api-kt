package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableFloatValue

/**
 * A simple FloatExpression that represents a single constant value.
 */
class FloatConstant private constructor(override val value: Float) : ObservableFloatValue {

    override fun get(): Float {
        return this.value
    }

    override fun addListener(listener: InvalidationListener) {
        // no-op
    }

    override fun removeListener(listener: InvalidationListener) {
        // no-op
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        // no-op
        return false
    }

    override fun addListener(listener: ChangeListener<in Number?>) {
        // no-op
    }

    override fun removeListener(listener: ChangeListener<in Number?>) {
        // no-op
    }

    override fun hasListener(listener: ChangeListener<in Number?>): Boolean {
        // no-op
        return false
    }

    override val intValue: Int
        get() = this.value.toInt()

    override val longValue: Long
        get() = this.value.toLong()

    override val floatValue: Float
        get() = this.value

    override val doubleValue: Double
        get() = this.value.toDouble()

    override val shortValue: Short
        get() = this.intValue.toShort()

    override val byteValue: Byte
        get() = this.intValue.toByte()

    companion object {

        fun valueOf(value: Float): FloatConstant {
            return FloatConstant(value)
        }

    }

}