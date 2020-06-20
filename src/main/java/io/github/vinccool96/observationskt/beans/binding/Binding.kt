package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `Binding` calculates a value that depends on one or more sources. The sources are usually called the dependency of
 * a binding. A binding observes its dependencies for changes and updates its value automatically.
 *
 * While a dependency of a binding can be anything, it is almost always an implementation of [ObservableValue].
 * `Binding` implements `ObservableValue` allowing to use it in another binding. With that one can assemble very complex
 * bindings from simple bindings.
 *
 * All bindings in the JavaFX runtime are calculated lazily. That means, if a dependency changes, the result of a
 * binding is not immediately recalculated, but it is marked as invalid. Next time the value of an invalid binding is
 * requested, it is recalculated.
 *
 * It is recommended to use one of the base classes defined in this package (e.g. [DoubleBinding]) to define a custom
 * binding, because these classes already provide most of the needed functionality. See [DoubleBinding] for an example.
 *
 * @see DoubleBinding
 * @since JavaFX 2.0
 */
interface Binding<T> : ObservableValue<T> {

    /**
     * Checks if a binding is valid.
     *
     * @return `true` if the `Binding` is valid, `false` otherwise
     */
    val valid: Boolean

    /**
     * Mark a binding as invalid. This forces the recalculation of the value of the `Binding` next time it is
     * request.
     */
    fun invalidate()

    /**
     * Returns the dependencies of a binding in an unmodifiable [ObservableList]. The implementation is optional. The
     * main purpose of this method is to support developers during development. It allows to explore and monitor
     * dependencies of a binding during runtime.
     *
     * Because this method should not be used in production code, it is recommended to implement this functionality as
     * sparse as possible. For example if the dependencies do not change, each call can generate a new `ObservableList`,
     * avoiding the necessity to store the result.
     *
     * @return an unmodifiable `ObservableList` of the dependencies
     */
    @get:ReturnsUnmodifiableCollection
    val dependencies: ObservableList<*>

    /**
     * Signals to the `Binding` that it will not be used anymore and any references can be removed. A call of this
     * method usually results in the binding stopping to observe its dependencies by unregistering its listener(s). The
     * implementation is optional.
     *
     * All bindings in our implementation use instances of [io.github.vinccool96.observationskt.beans.WeakInvalidationListener],
     * which means usually a binding does not need to be disposed. But if you plan to use your application in
     * environments that do not support `WeakReferences` you have to dispose unused `Bindings` to avoid memory leaks.
     */
    fun dispose()

}