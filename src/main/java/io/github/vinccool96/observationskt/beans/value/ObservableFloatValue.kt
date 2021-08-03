package io.github.vinccool96.observationskt.beans.value

/**
 * An observable float value.
 *
 * @see ObservableValue
 *
 * @see ObservableNumberValue
 */
interface ObservableFloatValue : ObservableNumberValue {

    /**
     * Returns the current value of this `ObservableFloatValue`.
     *
     * @return The current value
     */
    fun get(): Float

}