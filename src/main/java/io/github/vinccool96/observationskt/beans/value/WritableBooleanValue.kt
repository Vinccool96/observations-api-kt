package io.github.vinccool96.observationskt.beans.value

/**
 * A writable `Boolean` value.
 *
 * @see WritableValue
 */
interface WritableBooleanValue : WritableValue<Boolean?> {

    /**
     * Get the wrapped value. Unlike [value], this method returns not-nullable `Boolean`. Needs to be identical to
     * [value].
     *
     * @return The current value
     */
    fun get(): Boolean

    /**
     * Set the wrapped value. Unlike [WritableBooleanValue.value], this method uses not-nullable `Boolean`.
     *
     * @param value The new value
     */
    fun set(value: Boolean)

}