package io.github.vinccool96.observationskt.beans.value

/**
 * A writable typed value.
 *
 * @param T The type of the wrapped value
 *
 * @see WritableValue
 */
interface WritableObjectValue<T> : WritableValue<T> {

    /**
     * Get the wrapped value. This must be identical to the value returned from [getValue].
     *
     * This method exists only to align `WritableObjectValue` API with [WritableBooleanValue] and subclasses of
     * [WritableNumberValue]
     *
     * @return The current value
     */
    fun get(): T

    /**
     * Set the wrapped value. Should be equivalent to [setValue]
     *
     * @param value The new value
     *
     * @see get
     */
    fun set(value: T)

}