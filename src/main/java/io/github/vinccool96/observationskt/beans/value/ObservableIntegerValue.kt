package io.github.vinccool96.observationskt.beans.value

/**
 * An observable integer value.
 *
 * @see ObservableValue
 *
 * @see ObservableNumberValue
 *
 * @since JavaFX 2.0
 */
interface ObservableIntegerValue : ObservableNumberValue {

    /**
     * Returns the current value of this `ObservableIntegerValue`.
     *
     * @return The current value
     */
    fun get(): Int
}