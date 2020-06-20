package io.github.vinccool96.observationskt.beans.value

/**
 * An observable long value.
 *
 * @see ObservableValue
 * @see ObservableNumberValue
 * @since JavaFX 2.0
 */
interface ObservableLongValue : ObservableNumberValue {

    /**
     * Returns the current value of this `ObservableLongValue`.
     *
     * @return The current value
     */
    fun get(): Long

}