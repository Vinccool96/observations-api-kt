package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a full implementation of a [Property] wrapping a `Byte` value.
 *
 * @see BytePropertyBase
 *
 * @constructor The constructor of `ByteProperty`
 *
 * @param bean the bean of this `ByteProperty`
 * @param name the name of this `ByteProperty`
 * @param initialValue the initial value of the wrapped value
 */
open class SimpleByteProperty(override val bean: Any?, override val name: String?, initialValue: Byte) :
        BytePropertyBase(initialValue) {

    /**
     * The constructor of `ByteProperty`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Byte) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `ByteProperty`
     *
     * @param bean the bean of this `ByteProperty`
     * @param name the name of this `ByteProperty`
     */
    constructor(bean: Any?, name: String?) : this(bean, name, 0)

    /**
     * The constructor of `ByteProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}