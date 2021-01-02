package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ObservableMap

/**
 * This class provides a full implementation of a [Property] wrapping an `ObservableMap`.
 *
 * @param K the type of the key elements of the `Map`
 * @param V the type of the value elements of the `Map`
 *
 * @see MapPropertyBase
 *
 * @constructor The constructor of `SimpleMapProperty`
 *
 * @param bean the bean of this `MapProperty`
 * @param name the name of this `MapProperty`
 * @param initialValue the initial value of the wrapped value
 */
open class SimpleMapProperty<K, V>(override val bean: Any?, override val name: String?,
        initialValue: ObservableMap<K, V>?) : MapPropertyBase<K, V>(initialValue) {

    /**
     * The constructor of `SimpleMapProperty`
     *
     * @param bean the bean of this `MapProperty`
     * @param name the name of this `MapProperty`
     */
    constructor(bean: Any?, name: String?) : this(bean, name ?: DEFAULT_NAME, null)

    /**
     * The constructor of `SimpleMapProperty`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: ObservableMap<K, V>?) : this(DEFAULT_BEAN, DEFAULT_NAME, initialValue)

    /**
     * The constructor of `SimpleMapProperty`
     */
    constructor() : this(DEFAULT_BEAN, DEFAULT_NAME)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}