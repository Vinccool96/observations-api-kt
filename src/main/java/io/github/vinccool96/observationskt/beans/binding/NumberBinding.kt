package io.github.vinccool96.observationskt.beans.binding

/**
 * A tagging interface to mark all Bindings that wrap a number-value.
 *
 * @see Binding
 * @see NumberExpression
 * @since JavaFX 2.0
 */
interface NumberBinding : Binding<Number>, NumberExpression
