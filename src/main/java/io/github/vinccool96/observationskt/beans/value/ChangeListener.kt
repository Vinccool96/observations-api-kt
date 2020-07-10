package io.github.vinccool96.observationskt.beans.value

/**
 * A `ChangeListener` is notified whenever the value of an [ObservableValue] changes. It can be registered and
 * unregistered with [ObservableValue.addListener] and [ObservableValue.removeListener] respectively.
 *
 * For an in-depth explanation of change events and how they differ from invalidation events, see the documentation of
 * `ObservableValue`.
 *
 * The same instance of `ChangeListener` can be registered to listen to multiple `ObservableValues`.
 *
 * @see ObservableValue
 */
@FunctionalInterface
interface ChangeListener<T> {

    /**
     * This method needs to be provided by an implementation of `ChangeListener`. It is called if the value of an
     * [ObservableValue] changes.
     *
     * In general is is considered bad practice to modify the observed value in this method.
     *
     * @param observable
     *         The `ObservableValue` which value changed
     * @param oldValue
     *         The old value
     * @param newValue
     *         The new value
     */
    fun changed(observable: ObservableValue<out T>, oldValue: T, newValue: T)

}