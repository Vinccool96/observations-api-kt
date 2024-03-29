package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableBooleanValue
import io.github.vinccool96.observationskt.sun.binding.BidirectionalBinding
import io.github.vinccool96.observationskt.sun.binding.Logging

/**
 * This class provides a full implementation of a [Property] wrapping a `Boolean` value.
 *
 * The value of a `BooleanProperty` can be got and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `ObjectProperty` can be read with [bean] and [name].
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableBooleanValue
 * @see WritableBooleanValue
 * @see ReadOnlyBooleanProperty
 * @see Property
 */
@Suppress("UNCHECKED_CAST")
abstract class BooleanProperty : ReadOnlyBooleanProperty(), Property<Boolean?>, WritableBooleanValue {

    override var value: Boolean?
        get() = this.get()
        set(value) {
            if (value == null) {
                Logging.getLogger().fine("Attempt to set boolean property to null, using default value instead.",
                        NullPointerException())
            }
            this.set(value ?: false)
        }

    override fun bindBidirectional(other: Property<Boolean?>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<Boolean?>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `BooleanProperty` object.
     *
     * @return a string representation of this `BooleanProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("BooleanProperty [")
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ")
        }
        if (name != null && name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        result.append("value: ").append(get()).append("]")
        return result.toString()
    }

    /**
     * Creates an [ObjectProperty] that holds the value of this `BooleanProperty`. If the value of this
     * `BooleanProperty` changes, the value of the `ObjectProperty` will be updated automatically.
     *
     * @return the new `ObjectProperty`
     */
    override fun asObject(): ObjectProperty<Boolean> {
        return object : ObjectPropertyBase<Boolean>(this@BooleanProperty.get()) {

            init {
                BidirectionalBinding.bind(this as Property<Boolean?>, this@BooleanProperty)
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String?
                get() = this@BooleanProperty.name

        }
    }

    companion object {

        /**
         * Returns a `BooleanProperty` that wraps a [Property]. If the `Property` is already a `BooleanProperty`, it
         * will be returned. Otherwise, a new `BooleanProperty` is created that is bound to the `Property`.
         *
         * Note: null values in the source property will be interpreted as `false`
         *
         * @param property The source `Property`
         *
         * @return A `BooleanProperty` that wraps the `Property` if necessary
         */
        fun booleanProperty(property: Property<Boolean?>): BooleanProperty {
            return if (property is BooleanProperty) property else object : BooleanPropertyBase() {

                init {
                    BidirectionalBinding.bind(this as Property<Boolean?>, property)
                }

                override val bean: Any?
                    get() = null // Virtual property, does not exist on a bean

                override val name: String?
                    get() = property.name

            }
        }

    }

}