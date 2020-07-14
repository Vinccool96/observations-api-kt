package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a full implementation of a [Property] wrapping a `Int` value.
 *
 * @see IntPropertyBase
 *
 * @constructor The constructor of `IntProperty`
 *
 * @param bean the bean of this `IntProperty`
 * @param name the name of this `IntProperty`
 * @param initialValue the initial value of the wrapped value
 */
open class SimpleIntProperty(override val bean: Any?, override val name: String?, initialValue: Int) :
        IntPropertyBase(initialValue) {

    /**
     * The constructor of `IntProperty`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Int) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `IntProperty`
     *
     * @param bean the bean of this `IntProperty`
     * @param name the name of this `IntProperty`
     */
    constructor(bean: Any?, name: String) : this(bean, name, 0)

    /**
     * The constructor of `IntProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}