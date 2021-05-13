package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ObservableSet

/**
 * This class provides a full implementation of a [Property] wrapping an `ObservableSet`.
 *
 * @param E the type of the `MutableSet` elements
 *
 * @see SetPropertyBase
 *
 * @constructor The constructor of `SimpleSetProperty`
 *
 * @param bean the bean of this `SetProperty`
 * @param name the name of this `SetProperty`
 * @param initialValue the initial value of the wrapped value
 */
open class SimpleSetProperty<E>(override val bean: Any?, override val name: String?, initialValue: ObservableSet<E>?) :
        SetPropertyBase<E>(initialValue) {

    /**
     * The constructor of `SimpleSetProperty`
     *
     * @param bean the bean of this `SetProperty`
     * @param name the name of this `SetProperty`
     */
    constructor(bean: Any?, name: String?) : this(bean, name, null)

    /**
     * The constructor of `SimpleSetProperty`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: ObservableSet<E>?) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `SimpleSetProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}