package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ObservableList

/**
 * This class provides a full implementation of a [Property] wrapping an `ObservableList`.
 *
 * @param E the type of the `List` elements
 *
 * @see ListPropertyBase
 *
 * @constructor The constructor of `SimpleListProperty`
 *
 * @param bean the bean of this `ListProperty`
 * @param name the name of this `ListProperty`
 * @param initialValue the initial value of the wrapped value
 */
open class SimpleListProperty<E>(override val bean: Any?, override val name: String?,
        initialValue: ObservableList<E>?) : ListPropertyBase<E>(initialValue) {

    /**
     * The constructor of `SimpleListProperty`
     *
     * @param bean the bean of this `ListProperty`
     * @param name the name of this `ListProperty`
     */
    constructor(bean: Any?, name: String?) : this(bean, name, null)

    /**
     * The constructor of `SimpleListProperty`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: ObservableList<E>?) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `SimpleListProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}