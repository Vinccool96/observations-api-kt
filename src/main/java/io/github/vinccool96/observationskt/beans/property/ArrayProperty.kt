package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
import io.github.vinccool96.observationskt.beans.value.WritableArrayValue
import io.github.vinccool96.observationskt.collections.ObservableArray

/**
 * This class provides a full implementation of a [Property] wrapping a [ObservableArray].
 *
 * The value of a `ListProperty` can be got and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `ArrayProperty` can be read with [bean] and [name].
 *
 * @param T the type of the `Array` elements
 *
 * @see ObservableArray
 * @see io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
 * @see WritableArrayValue
 * @see ReadOnlyArrayProperty
 * @see Property
 *
 * @constructor The constructor needs a base array that [baseArray] will return when [ObservableArrayValue.get] returns
 * `null`. Therefore, `baseArray` will return the equivalent of `get()?.baseArray ?: baseArrayOfNull`.
 *
 * @param baseArrayOfNull the base array when the value is `null`
 */
abstract class ArrayProperty<T>(baseArrayOfNull: Array<T>) : ReadOnlyArrayProperty<T>(baseArrayOfNull),
        Property<ObservableArray<T>?>, WritableArrayValue<T> {

    override var value: ObservableArray<T>?
        get() = this.get()
        set(value) = this.set(value)

    override fun bindBidirectional(other: Property<ObservableArray<T>?>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<ObservableArray<T>?>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `ArrayProperty` object.
     *
     * @return a string representation of this `ArrayProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ArrayProperty [")
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