package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a full implementation of a [Property] wrapping a `Float` value.
 *
 * @see FloatPropertyBase
 *
 * @constructor The constructor of `FloatProperty`
 *
 * @param bean
 *         the bean of this `FloatProperty`
 * @param name
 *         the name of this `FloatProperty`
 * @param initialValue
 *         the initial value of the wrapped value
 */
class SimpleFloatProperty(override val bean: Any?, override val name: String, initialValue: Float) :
        FloatPropertyBase(initialValue) {

    /**
     * The constructor of `FloatProperty`
     *
     * @param initialValue
     *         the initial value of the wrapped value
     */
    constructor(initialValue: Float) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `FloatProperty`
     *
     * @param bean
     *         the bean of this `FloatProperty`
     * @param name
     *         the name of this `FloatProperty`
     */
    constructor(bean: Any?, name: String) : this(bean, name, 0.0F)

    /**
     * The constructor of `FloatProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}