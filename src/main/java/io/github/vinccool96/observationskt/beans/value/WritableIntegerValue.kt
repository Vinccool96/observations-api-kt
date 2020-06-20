package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `int` value.
 *
 * @see WritableValue
 * @see WritableNumberValue
 * @since JavaFX 2.0
 */
interface WritableIntegerValue : WritableNumberValue {

    /**
     * Get the wrapped value. Unlike [value], this method returns primitive `int`. Needs to be
     * identical to [value].
     *
     * @return The current value
     */
    fun get(): Int

    /**
     * Set the wrapped value. Unlike [WritableIntegerValue.value], this method uses primitive `int`.
     *
     * @param value
     *         The new value
     */
    fun set(value: Int)

}