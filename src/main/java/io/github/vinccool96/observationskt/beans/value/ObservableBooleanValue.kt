package io.github.vinccool96.observationskt.beans.value

/**
 * An observable boolean value.
 *
 * @see ObservableValue
 * @since JavaFX 2.0
 */
interface ObservableBooleanValue : ObservableValue<Boolean> {

    /**
     * Returns the current value of this `ObservableBooleanValue`.
     *
     * @return The current value
     */
    fun get(): Boolean
}