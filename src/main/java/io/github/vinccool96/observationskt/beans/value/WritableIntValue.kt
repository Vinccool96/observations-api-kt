package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `int` value.
 *
 * @see WritableValue
 * @see WritableNumberValue
 */
interface WritableIntValue : WritableNumberValue {

    /**
     * Get the wrapped value. Unlike [value], this method returns primitive `int`. Needs to be identical to [value].
     *
     * @return The current value
     */
    fun get(): Int

    /**
     * Set the wrapped value. Unlike [WritableIntValue.value], this method uses primitive `int`.
     *
     * @param value The new value
     */
    fun set(value: Int)

}