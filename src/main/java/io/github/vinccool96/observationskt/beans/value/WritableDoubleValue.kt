package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `double` value.
 *
 * @see WritableValue
 * @see WritableNumberValue
 * @since JavaFX 2.0
 */
interface WritableDoubleValue : WritableNumberValue {

    /**
     * Get the wrapped value. Unlike [value], this method returns primitive `double`. Needs to be
     * identical to [value].
     *
     * @return The current value
     */
    fun get(): Double

    /**
     * Set the wrapped value. Unlike [WritableDoubleValue.value], this method uses primitive `double`.
     *
     * @param value
     *         The new value
     */
    fun set(value: Double)

}