package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableStringValue

/**
 * This class provides a full implementation of a [Property] wrapping a `String` value.
 *
 * The value of a `StringProperty` can be get and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `StringProperty` can be read with [bean] and [name].
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableStringValue
 * @see WritableStringValue
 * @see ReadOnlyStringProperty
 * @see Property
 */
abstract class StringProperty : ReadOnlyStringProperty(), Property<String?>, WritableStringValue {

    override var value: String?
        get() = this.get()
        set(value) = set(value)

    override fun bindBidirectional(other: Property<String?>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<String?>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `StringProperty` object.
     *
     * @return a string representation of this `StringProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("StringProperty [")
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