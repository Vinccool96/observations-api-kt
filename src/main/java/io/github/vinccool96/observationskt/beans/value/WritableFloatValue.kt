package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `float` value.
 *
 * @see WritableValue
 * @see WritableNumberValue
 */
interface WritableFloatValue : WritableNumberValue {

    /**
     * Get the wrapped value. Unlike [value], this method returns primitive `float`. Needs to be
     * identical to [value].
     *
     * @return The current value
     */
    fun get(): Float

    /**
     * Set the wrapped value. Unlike [WritableFloatValue.value], this method uses primitive `float`.
     *
     * @param value
     *         The new value
     */
    fun set(value: Float)

}