package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableSetValue
import io.github.vinccool96.observationskt.collections.ObservableSet

/**
 * This class provides a full implementation of a [Property] wrapping a [ObservableSet].
 *
 * The value of a `SetProperty` can be got and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `SetProperty` can be read with [bean] and [name].
 *
 * @param E the type of the `MutableSet` elements
 *
 * @see ObservableSet
 * @see io.github.vinccool96.observationskt.beans.value.ObservableSetValue
 * @see WritableSetValue
 * @see ReadOnlySetProperty
 * @see Property
 */
abstract class SetProperty<E> : ReadOnlySetProperty<E>(), Property<ObservableSet<E>?>, WritableSetValue<E> {

    override var value
        get() = super.value
        set(value) = set(value)

    override fun bindBidirectional(other: Property<ObservableSet<E>?>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<ObservableSet<E>?>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `SetProperty` object.
     *
     * @return a string representation of this `SetProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("SetProperty [")
        if (bean != null) {
            result.append("bean: $bean, ")
        }
        if (name != null && name != "") {
            result.append("name: $name, ")
        }
        result.append("value: ${get()}]")
        return result.toString()
    }

}