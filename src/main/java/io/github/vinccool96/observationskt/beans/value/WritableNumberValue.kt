package io.github.vinccool96.observationskt.beans.value

/**
 * A tagging interface that marks all sub-interfaces of [WritableValue] that wrap a number.
 *
 * @see WritableValue
 * @see WritableDoubleValue
 * @see WritableFloatValue
 * @see WritableIntegerValue
 * @see WritableLongValue
 * @since JavaFX 2.0
 */
interface WritableNumberValue : WritableValue<Number>
