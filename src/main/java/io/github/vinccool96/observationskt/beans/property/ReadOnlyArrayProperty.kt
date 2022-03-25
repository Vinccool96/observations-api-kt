package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.ArrayExpression
import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
import io.github.vinccool96.observationskt.collections.ObservableArray

/**
 * Super class for all readonly properties wrapping an [ObservableArray].
 *
 * @param T the type of the `Array` elements
 *
 * @see ObservableArray
 * @see io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
 * @see ArrayExpression
 * @see ReadOnlyProperty
 *
 * @constructor The constructor needs a base array that [baseArray] will return when [ObservableArrayValue.get] returns
 * `null`. Therefore, `baseArray` will return the equivalent of `get()?.baseArray ?: baseArrayOfNull`.
 *
 * @param baseArrayOfNull the base array when the value is `null`
 */
@Suppress("UNCHECKED_CAST")
abstract class ReadOnlyArrayProperty<T>(baseArrayOfNull: Array<T>) : ArrayExpression<T>(baseArrayOfNull),
        ReadOnlyProperty<ObservableArray<T>?> {

    /**
     * Creates a bidirectional content binding of the [ObservableArray], that is wrapped in this
     * `ReadOnlyArrayProperty`, and another `ObservableArray`.
     *
     * A bidirectional content binding ensures that the content of two `ObservableArrays` is the same. If the content of
     * one of the arrays changes, the other one will be updated automatically.
     *
     * @param array the `ObservableArray` this property should be bound to
     *
     * @throws IllegalArgumentException if `array` is the same array that this `ReadOnlyArrayProperty` points to
     */
    fun bindContentBidirectional(array: ObservableArray<T>) {
        Bindings.bindContentBidirectional(this, array)
    }

    /**
     * Deletes a bidirectional content binding between the [ObservableArray], that is wrapped in this
     * `ReadOnlyArrayProperty`, and another `Object`.
     *
     * @param obj the `Object` to which the bidirectional binding should be removed
     *
     * @throws IllegalArgumentException if `obj` is the same list that this `ReadOnlyArrayProperty` points to
     */
    fun unbindContentBidirectional(obj: Any) {
        Bindings.unbindContentBidirectional(this, obj)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is ObservableArray<*>) {
            return false
        }
        val array: ObservableArray<T> = other as ObservableArray<T>

        if (size != array.size) {
            return false
        }

        val e1: Iterator<T> = iterator()
        val e2 = array.iterator()

        while (e1.hasNext() && e2.hasNext()) {
            val o1 = e1.next()
            val o2 = e2.next()
            if (o1 != o2) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var hashCode = 1
        for (e in this) {
            hashCode = 31 * hashCode + (e?.hashCode() ?: 0)
        }
        return hashCode
    }

    /**
     * Returns a string representation of this `ReadOnlyArrayProperty` object.
     *
     * @return a string representation of this `ReadOnlyArrayProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyArrayProperty [")
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ")
        }
        if (name != null && name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        result.append("value: ").append(get()).append("]")
        return result.toString()
    }

}