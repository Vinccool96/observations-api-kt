package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a full implementation of a [Property] wrapping a `Long` value.
 *
 * @see LongPropertyBase
 *
 * @constructor The constructor of `LongProperty`
 *
 * @param bean the bean of this `LongProperty`
 * @param name the name of this `LongProperty`
 * @param initialValue the initial value of the wrapped value
 */
class SimpleLongProperty(override val bean: Any?, override val name: String?, initialValue: Long) :
        LongPropertyBase(initialValue) {

    /**
     * The constructor of `LongProperty`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Long) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `LongProperty`
     *
     * @param bean the bean of this `LongProperty`
     * @param name the name of this `LongProperty`
     */
    constructor(bean: Any?, name: String) : this(bean, name, 0L)

    /**
     * The constructor of `LongProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}