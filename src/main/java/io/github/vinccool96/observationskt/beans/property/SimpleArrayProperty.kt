package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ObservableArray

/**
 * This class provides a full implementation of a [Property] wrapping an `ObservableArray`.
 *
 * @param T the type of the `Array` elements
 *
 * @see ArrayPropertyBase
 *
 * @constructor The constructor of `SimpleArrayProperty`
 *
 * @param bean the bean of this `ArrayProperty`
 * @param name the name of this `ArrayProperty`
 * @param initialValue the initial value of the wrapped value
 * @param baseArrayOfNull the base array when the value is `null`
 */
open class SimpleArrayProperty<T>(override val bean: Any?, override val name: String?,
        initialValue: ObservableArray<T>?, baseArrayOfNull: Array<T>) : ArrayPropertyBase<T>(initialValue,
        baseArrayOfNull) {

    /**
     * The constructor of `SimpleArrayProperty`
     *
     * @param bean the bean of this `ArrayProperty`
     * @param name the name of this `ArrayProperty`
     * @param baseArrayOfNull the base array when the value is `null`
     */
    constructor(bean: Any?, name: String?, baseArrayOfNull: Array<T>) : this(bean, name, null, baseArrayOfNull)

    /**
     * The constructor of `SimpleArrayProperty`
     *
     * @param initialValue the initial value of the wrapped value
     * @param baseArrayOfNull the base array when the value is `null`
     */
    constructor(initialValue: ObservableArray<T>?, baseArrayOfNull: Array<T>) : this(DEFAULT_BEAN, DEFAULT_NAME,
            initialValue, baseArrayOfNull)

    /**
     * The constructor of `SimpleArrayProperty`
     *
     * @param baseArrayOfNull the base array when the value is `null`
     */
    constructor(baseArrayOfNull: Array<T>) : this(DEFAULT_BEAN, DEFAULT_NAME, baseArrayOfNull)

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

    }

}