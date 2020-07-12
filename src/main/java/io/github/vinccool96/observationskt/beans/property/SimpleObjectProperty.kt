package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a full implementation of a [Property] wrapping an arbitrary `Object`.
 *
 * @param T the type of the wrapped `Object`
 *
 * @see ObjectPropertyBase
 *
 * @constructor The constructor of `ObjectProperty`
 *
 * @param bean the bean of this `ObjectProperty`
 * @param name the name of this `ObjectProperty`
 * @param initialValue the initial value of the wrapped value
 */
class SimpleObjectProperty<T>(override val bean: Any?, override val name: String?, initialValue: T) :
        ObjectPropertyBase<T>(initialValue) {

    /**
     * The constructor of `ObjectProperty`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: T) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}