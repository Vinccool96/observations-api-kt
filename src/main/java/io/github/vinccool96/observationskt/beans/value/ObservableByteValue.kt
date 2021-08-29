package io.github.vinccool96.observationskt.beans.value

/**
 * An observable byte value.
 *
 * @see ObservableValue
 *
 * @see ObservableNumberValue
 */
interface ObservableByteValue : ObservableNumberValue {

    /**
     * Returns the current value of this `ObservableByteValue`.
     *
     * @return The current value
     */
    fun get(): Byte

}