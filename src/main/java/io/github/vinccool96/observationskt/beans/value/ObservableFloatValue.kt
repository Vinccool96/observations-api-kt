package io.github.vinccool96.observationskt.beans.value

/**
 * An observable float value.
 *
 * @see ObservableValue
 *
 * @see ObservableNumberValue
 *
 * @since JavaFX 2.0
 */
interface ObservableFloatValue : ObservableNumberValue {

    /**
     * Returns the current value of this `ObservableFloatValue`.
     *
     * @return The current value
     */
    fun get(): Float
}