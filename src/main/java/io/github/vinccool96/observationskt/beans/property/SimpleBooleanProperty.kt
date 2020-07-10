package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a full implementation of a [Property] wrapping a `Boolean` value.
 *
 * @see BooleanPropertyBase
 *
 * @constructor The constructor of `BooleanProperty`
 *
 * @param bean
 *         the bean of this `BooleanProperty`
 * @param name
 *         the name of this `BooleanProperty`
 * @param initialValue
 *         the initial value of the wrapped value
 */
class SimpleBooleanProperty(override val bean: Any?, override val name: String, initialValue: Boolean) :
        BooleanPropertyBase(initialValue) {

    /**
     * The constructor of `BooleanProperty`
     *
     * @param initialValue
     *         the initial value of the wrapped value
     */
    constructor(initialValue: Boolean) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `BooleanProperty`
     *
     * @param bean
     *         the bean of this `BooleanProperty`
     * @param name
     *         the name of this `BooleanProperty`
     */
    constructor(bean: Any?, name: String) : this(bean, name, false)

    /**
     * The constructor of `BooleanProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}