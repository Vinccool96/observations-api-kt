package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a full implementation of a [Property] wrapping a `Short` value.
 *
 * @see ShortPropertyBase
 *
 * @constructor The constructor of `ShortProperty`
 *
 * @param bean the bean of this `ShortProperty`
 * @param name the name of this `ShortProperty`
 * @param initialValue the initial value of the wrapped value
 */
open class SimpleShortProperty(override val bean: Any?, override val name: String?, initialValue: Short) :
        ShortPropertyBase(initialValue) {

    /**
     * The constructor of `ShortProperty`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Short) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `ShortProperty`
     *
     * @param bean the bean of this `ShortProperty`
     * @param name the name of this `ShortProperty`
     */
    constructor(bean: Any?, name: String?) : this(bean, name, 0)

    /**
     * The constructor of `ShortProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}