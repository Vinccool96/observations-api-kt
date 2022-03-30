package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableLongValue
import io.github.vinccool96.observationskt.sun.binding.BidirectionalBinding
import io.github.vinccool96.observationskt.sun.binding.Logging

/**
 * This class defines a [Property] wrapping a `Long` value.
 *
 * The value of a `LongProperty` can be got and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `ObjectProperty` can be read with [bean] and [name].
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableLongValue
 * @see WritableLongValue
 * @see ReadOnlyLongProperty
 * @see Property
 */
@Suppress("UNCHECKED_CAST")
abstract class LongProperty : ReadOnlyLongProperty(), Property<Number?>, WritableLongValue {

    override var value: Number?
        get() = super.value
        set(value) {
            if (value == null) {
                Logging.getLogger().fine("Attempt to set long property to null, using default value instead.",
                        NullPointerException())
            }
            this.set(value?.toLong() ?: 0L)
        }

    override fun bindBidirectional(other: Property<Number?>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<Number?>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `LongProperty` object.
     *
     * @return a string representation of this `LongProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("LongProperty [")
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
     * Creates an [ObjectProperty] that is bidirectionally bound to this `LongProperty`. If the value of this
     * `LongProperty` changes, the value of the `ObjectProperty` will be updated automatically and vice-versa.
     *
     * Can be used for binding an ObjectProperty to LongProperty.
     *
     * ```
     * val longProperty: LongProperty = SimpleLongProperty(1L)
     * val objectProperty: ObjectProperty<Long> = SimpleObjectProperty(2L)
     *
     * objectProperty.bind(longProperty.asObject())
     * ```
     *
     * @return the new `ObjectProperty`
     */
    override fun asObject(): ObjectProperty<Long> {
        return object : ObjectPropertyBase<Long>(this@LongProperty.longValue) {

            init {
                BidirectionalBinding.bind(this as Property<Number?>, this@LongProperty)
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String?
                get() = this@LongProperty.name

        }
    }

    companion object {

        /**
         * Returns a `LongProperty` that wraps a [Property]. If the `Property` is already a `LongProperty`, it
         * will be returned. Otherwise, a new `LongProperty` is created that is bound to the `Property`.
         *
         * This is very useful when bidirectionally binding an ObjectProperty<Long> and an LongProperty.
         * ```
         * val longProperty: LongProperty = SimpleLongProperty(1L)
         * val objectProperty: ObjectProperty<Long> = SimpleObjectProperty(2L)
         *
         * // Need to keep the reference as bidirectional binding uses weak references
         * val objectAsLong: LongProperty = LongProperty.longProperty(objectProperty)
         *
         * longProperty.bindBidirectional(objectAsLong)
         * ```
         *
         * Another approach is to convert the LongProperty to ObjectProperty using [asObject] method.
         *
         * @param property The source `Property`
         *
         * @return A `LongProperty` that wraps the `Property` if necessary
         */
        fun longProperty(property: Property<Long?>): LongProperty {
            return if (property is LongProperty) property else object : LongPropertyBase() {

                init {
                    BidirectionalBinding.bind(this, property as Property<Number?>)
                }

                override val bean: Any?
                    get() = null // Virtual property, does not exist on a bean

                override val name: String?
                    get() = property.name

            }
        }

    }

}