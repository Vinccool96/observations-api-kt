package io.github.vinccool96.observationskt.beans.value

/**
 * A common interface of all sub-interfaces of [ObservableValue] that wrap a number.
 *
 * For the `<T>` of [ObservableValue], it is the type of the wrapped value (a `Number`).
 *
 * @see ObservableValue
 *
 * @see ObservableDoubleValue
 *
 * @see ObservableFloatValue
 *
 * @see ObservableIntegerValue
 *
 * @see ObservableLongValue
 *
 * @since JavaFX 2.0
 */
interface ObservableNumberValue : ObservableValue<Number> {

    /**
     * Returns the value of this `ObservableNumberValue` as an `int` . If the value is not an `int`, a standard cast is
     * performed.
     *
     * @return The value of this `ObservableNumberValue` as an `int`
     */
    val intValue: Int

    /**
     * Returns the value of this `ObservableNumberValue` as a `long` . If the value is not a `long`, a standard cast is
     * performed.
     *
     * @return The value of this `ObservableNumberValue` as a `long`
     */
    val longValue: Long

    /**
     * Returns the value of this `ObservableNumberValue` as a `float`. If the value is not a `float`, a standard cast is
     * performed.
     *
     * @return The value of this `ObservableNumberValue` as a `float`
     */
    val floatValue: Float

    /**
     * Returns the value of this `ObservableNumberValue` as a `double`. If the value is not a `double`, a standard cast
     * is performed.
     *
     * @return The value of this `ObservableNumberValue` as a `double`
     */
    val doubleValue: Double
}