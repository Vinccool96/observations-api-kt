package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a full implementation of a [Property] wrapping a `String` value.
 *
 * @see StringPropertyBase
 *
 * @constructor The constructor of `StringProperty`
 *
 * @param bean
 *         the bean of this `StringProperty`
 * @param name
 *         the name of this `StringProperty`
 * @param initialValue
 *         the initial value of the wrapped value
 */
class SimpleStringProperty(override val bean: Any?, override val name: String, initialValue: String?) :
        StringPropertyBase(initialValue) {

    /**
     * The constructor of `StringProperty`
     *
     * @param initialValue
     *         the initial value of the wrapped value
     */
    constructor(initialValue: String?) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `StringProperty`
     *
     * @param bean
     *         the bean of this `StringProperty`
     * @param name
     *         the name of this `StringProperty`
     */
    constructor(bean: Any?, name: String) : this(bean, name, null)

    /**
     * The constructor of `StringProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}