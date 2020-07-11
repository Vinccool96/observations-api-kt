package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `boolean` value.
 *
 * @see WritableValue
 * @see WritableNumberValue
 */
interface WritableBooleanValue : WritableValue<Boolean> {

    /**
     * Get the wrapped value. Unlike [value], this method returns primitive `boolean`. Needs to be
     * identical to [value].
     *
     * @return The current value
     */
    fun get(): Boolean

    /**
     * Set the wrapped value. Unlike [WritableBooleanValue.value], this method uses primitive `boolean`.
     *
     * @param value The new value
     */
    fun set(value: Boolean)

}