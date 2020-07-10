package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableStringValue

/**
 * This class provides a full implementation of a {@link Property} wrapping a {@code String} value.
 * <p>
 * The value of a {@code StringProperty} can be get and set with {@link #get()}, {@link #getValue()}, {@link
 * #set(Object)}, and {@link #setValue(String)}.
 * <p>
 * A property can be bound and unbound unidirectional with {@link #bind(ObservableValue)} and {@link #unbind()}.
 * Bidirectional bindings can be created and removed with {@link #bindBidirectional(Property)} and {@link
 * #unbindBidirectional(Property)}.
 * <p>
 * The context of a {@code StringProperty} can be read with {@link #getBean()} and {@link #getName()}.
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
        if (name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        result.append("value: ").append(get()).append("]")
        return result.toString()
    }

}