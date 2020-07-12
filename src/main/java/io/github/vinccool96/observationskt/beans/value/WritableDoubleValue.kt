package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `Double` value.
 *
 * @see WritableValue
 * @see WritableNumberValue
 */
interface WritableDoubleValue : WritableNumberValue {

    /**
     * Get the wrapped value. Unlike [value], this method returns not-nullable `Double`. Needs to be identical to
     * [value].
     *
     * @return The current value
     */
    fun get(): Double

    /**
     * Set the wrapped value. Unlike [WritableDoubleValue.value], this method uses not-nullable `Double`.
     *
     * @param value The new value
     */
    fun set(value: Double)

}