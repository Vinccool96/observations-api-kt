package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `short` value.
 *
 * @see WritableValue
 * @see WritableNumberValue
 */
interface WritableShortValue : WritableNumberValue {

    /**
     * Get the wrapped value. Unlike [value], this method returns primitive `short`. Needs to be identical to [value].
     *
     * @return The current value
     */
    fun get(): Short

    /**
     * Set the wrapped value. Unlike [WritableShortValue.value], this method uses primitive `short`.
     *
     * @param value The new value
     */
    fun set(value: Short)

}