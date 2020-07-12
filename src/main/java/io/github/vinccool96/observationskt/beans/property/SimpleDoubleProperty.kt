package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a full implementation of a [Property] wrapping a `Double` value.
 *
 * @see DoublePropertyBase
 *
 * @constructor The constructor of `DoubleProperty`
 *
 * @param bean the bean of this `DoubleProperty`
 * @param name the name of this `DoubleProperty`
 * @param initialValue the initial value of the wrapped value
 */
class SimpleDoubleProperty(override val bean: Any?, override val name: String?, initialValue: Double) :
        DoublePropertyBase(initialValue) {

    /**
     * The constructor of `DoubleProperty`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Double) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `DoubleProperty`
     *
     * @param bean the bean of this `DoubleProperty`
     * @param name the name of this `DoubleProperty`
     */
    constructor(bean: Any?, name: String) : this(bean, name, 0.0)

    /**
     * The constructor of `DoubleProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}