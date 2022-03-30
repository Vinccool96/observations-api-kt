package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableByteValue
import io.github.vinccool96.observationskt.sun.binding.BidirectionalBinding
import io.github.vinccool96.observationskt.sun.binding.Logging

/**
 * This class defines a [Property] wrapping a `Byte` value.
 *
 * The value of a `ByteProperty` can be got and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `ObjectProperty` can be read with [bean] and [name].
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableByteValue
 * @see WritableByteValue
 * @see ReadOnlyByteProperty
 * @see Property
 */
@Suppress("UNCHECKED_CAST")
abstract class ByteProperty : ReadOnlyByteProperty(), Property<Number?>, WritableByteValue {

    override var value: Number?
        get() = super.value
        set(value) {
            if (value == null) {
                Logging.getLogger().fine("Attempt to set byte property to null, using default value instead.",
                        NullPointerException())
            }
            this.set(value?.toByte() ?: 0)
        }

    override fun bindBidirectional(other: Property<Number?>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<Number?>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `ByteProperty` object.
     *
     * @return a string representation of this `ByteProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ByteProperty [")
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
     * Creates an [ObjectProperty] that is bidirectionally bound to this `ByteProperty`. If the value of this
     * `ByteProperty` changes, the value of the `ObjectProperty` will be updated automatically and vice-versa.
     *
     * Can be used for binding an ObjectProperty to ByteProperty.
     *
     * ```
     * val byteProperty: ByteProperty = SimpleByteProperty(1)
     * val objectProperty: ObjectProperty<Byte> = SimpleObjectProperty(2)
     *
     * objectProperty.bind(byteProperty.asObject())
     * ```
     *
     * @return the new `ObjectProperty`
     */
    override fun asObject(): ObjectProperty<Byte> {
        return object : ObjectPropertyBase<Byte>(this@ByteProperty.byteValue) {

            init {
                BidirectionalBinding.bind(this as Property<Number?>, this@ByteProperty)
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String?
                get() = this@ByteProperty.name

        }
    }

    companion object {

        /**
         * Returns a `ByteProperty` that wraps a [Property]. If the `Property` is already a `ByteProperty`, it
         * will be returned. Otherwise, a new `ByteProperty` is created that is bound to the `Property`.
         *
         * This is very useful when bidirectionally binding an ObjectProperty<Byte> and an ByteProperty.
         * ```
         * val byteProperty: ByteProperty = SimpleByteProperty(1)
         * val objectProperty: ObjectProperty<Byte> = SimpleObjectProperty(2)
         *
         * // Need to keep the reference as bidirectional binding uses weak references
         * val objectAsByte: ByteProperty = ByteProperty.byteProperty(objectProperty)
         *
         * byteProperty.bindBidirectional(objectAsByte)
         * ```
         *
         * Another approach is to convert the ByteProperty to ObjectProperty using [asObject] method.
         *
         * @param property The source `Property`
         *
         * @return A `ByteProperty` that wraps the `Property` if necessary
         */
        fun byteProperty(property: Property<Byte?>): ByteProperty {
            return if (property is ByteProperty) property else object : BytePropertyBase() {

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