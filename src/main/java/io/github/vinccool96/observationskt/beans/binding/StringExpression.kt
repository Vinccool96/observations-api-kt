package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableStringValue

/**
 * A {@code StringExpression} is a {@link ObservableStringValue} plus additional convenience methods to generate
 * bindings in a fluent style.
 *
 * A concrete sub-class of {@code StringExpression} has to implement the method [ObservableStringValue.get], which
 * provides the actual value of this expression.
 *
 * Note: all implementation of {@link BooleanBinding} returned by the comparisons in this class consider a {@code String} that is {@code null} equal to an empty {@code String}.
 *
 * @since JavaFX 2.0
 */
abstract class StringExpression : ObservableStringValue {

    override val value: String?
        get() = this.get()

    /**
     * Returns usually the value of this {@code StringExpression}. Only if the value is {@code null} an empty {@code String} is returned instead.
     *
     * @return the value of this {@code StringExpression} or the empty {@code String}
     */
    val valueSafe: String
        get() = this.get() ?: ""

}