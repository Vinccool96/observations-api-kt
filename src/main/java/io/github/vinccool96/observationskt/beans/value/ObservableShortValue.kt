package io.github.vinccool96.observationskt.beans.value

/**
 * An observable double value.
 *
 * @see ObservableValue
 *
 * @see ObservableNumberValue
 */
interface ObservableShortValue : ObservableNumberValue {

    /**
     * Returns the current value of this `ObservableShortValue`.
     *
     * @return The current value
     */
    fun get(): Short

}