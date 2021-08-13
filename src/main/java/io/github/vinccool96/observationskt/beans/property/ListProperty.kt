package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableListValue
import io.github.vinccool96.observationskt.collections.ObservableList

/**
 * This class provides a full implementation of a [Property] wrapping a [ObservableList].
 *
 * The value of a `ListProperty` can be got and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `ListProperty` can be read with [bean] and [name].
 *
 * @param E the type of the `List` elements
 *
 * @see ObservableList
 * @see io.github.vinccool96.observationskt.beans.value.ObservableListValue
 * @see WritableListValue
 * @see ReadOnlyListProperty
 * @see Property
 */
abstract class ListProperty<E> : ReadOnlyListProperty<E>(), Property<ObservableList<E>?>, WritableListValue<E> {

    override var value: ObservableList<E>?
        get() = this.get()
        set(value) = this.set(value)

    override fun bindBidirectional(other: Property<ObservableList<E>?>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<ObservableList<E>?>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `ListProperty` object.
     *
     * @return a string representation of this `ListProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ListProperty [")
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