package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `byte` value.
 *
 * @see WritableValue
 * @see WritableNumberValue
 */
interface WritableByteValue : WritableNumberValue {

    /**
     * Get the wrapped value. Unlike [value], this method returns primitive `byte`. Needs to be identical to [value].
     *
     * @return The current value
     */
    fun get(): Byte

    /**
     * Set the wrapped value. Unlike [WritableByteValue.value], this method uses primitive `byte`.
     *
     * @param value The new value
     */
    fun set(value: Byte)

}