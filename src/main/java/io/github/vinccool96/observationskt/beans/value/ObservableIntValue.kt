package io.github.vinccool96.observationskt.beans.value

/**
 * An observable int value.
 *
 * @see ObservableValue
 *
 * @see ObservableNumberValue
 *
 * @since JavaFX 2.0
 */
interface ObservableIntValue : ObservableNumberValue {

    /**
     * Returns the current value of this `ObservableIntValue`.
     *
     * @return The current value
     */
    fun get(): Int
}