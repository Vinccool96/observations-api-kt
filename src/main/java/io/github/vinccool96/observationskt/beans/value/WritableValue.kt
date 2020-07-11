package io.github.vinccool96.observationskt.beans.value

/**
 * A `WritableValue` is an entity that wraps a value that can be read and set. In general this interface should
 * not be implemented directly but one of its sub-interfaces (`WritableBooleanValue` etc.).
 *
 * @param T The type of the wrapped value
 *
 * @see WritableBooleanValue
 * @see WritableDoubleValue
 * @see WritableFloatValue
 * @see WritableIntValue
 * @see WritableLongValue
 * @see WritableNumberValue
 * @see WritableObjectValue
 * @see WritableStringValue
 */
interface WritableValue<T> {

    var value: T

}