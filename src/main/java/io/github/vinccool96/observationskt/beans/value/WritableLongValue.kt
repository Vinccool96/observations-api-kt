package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `long` value.
 *
 * @see WritableValue
 * @see WritableNumberValue
 */
interface WritableLongValue : WritableNumberValue {

    /**
     * Get the wrapped value. Unlike [value], this method returns primitive `long`. Needs to be identical to [value].
     *
     * @return The current value
     */
    fun get(): Long

    /**
     * Set the wrapped value. Unlike [WritableLongValue.value], this method uses primitive `long`.
     *
     * @param value The new value
     */
    fun set(value: Long)

}