package io.github.vinccool96.observationskt.beans.value

/**
 * An observable double value.
 *
 * @see ObservableValue
 *
 * @see ObservableNumberValue
 */
interface ObservableDoubleValue : ObservableNumberValue {

    /**
     * Returns the current value of this `ObservableDoubleValue`.
     *
     * @return The current value
     */
    fun get(): Double

}