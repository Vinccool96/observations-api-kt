package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ObservableList

/**
 * This class provides a full implementation of a [Property] wrapping an `ObservableList`.
 *
 * @param E the type of the `List` elements
 *
 * @see ListPropertyBase
 */
open class SimpleListProperty<E> : ListPropertyBase<E> {

    final override val bean: Any?

    final override val name: String?

    /**
     * The constructor of `SimpleListProperty`
     *
     * @param bean the bean of this `ListProperty`
     * @param name the name of this `ListProperty`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: ObservableList<E>?) : super(initialValue) {
        this.bean = bean
        this.name = name
    }

    /**
     * The constructor of `SimpleListProperty`
     *
     * @param bean the bean of this `ListProperty`
     * @param name the name of this `ListProperty`
     */
    constructor(bean: Any?, name: String?) : super() {
        this.bean = bean
        this.name = name
    }

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

        private val DEFAULT_NAME: String? = ""

    }

}