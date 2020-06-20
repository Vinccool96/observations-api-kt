package io.github.vinccool96.observationskt.beans.value

/**
 * An observable double value.
 *
 * @see ObservableValue
 *
 * @see ObservableNumberValue
 *
 * @since JavaFX 2.0
 */
interface ObservableDoubleValue : ObservableNumberValue {

    /**
     * Returns the current value of this `ObservableDoubleValue`.
     *
     * @return The current value
     */
    fun get(): Double
}