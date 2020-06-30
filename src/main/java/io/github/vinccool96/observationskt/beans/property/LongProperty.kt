package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableLongValue
import io.github.vinccool96.observationskt.sun.binding.BidirectionalBinding
import java.security.AccessControlContext
import java.security.AccessController
import java.security.PrivilegedAction

/**
 * This class defines a [Property] wrapping a `Long` value.
 *
 * The value of a `LongProperty` can be get and set with [get], [set], and [value].
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
 * @since JavaFX 2.0
 */
@Suppress("UNCHECKED_CAST")
abstract class LongProperty : ReadOnlyLongProperty(), Property<Number>, WritableLongValue {

    override var value: Number
        get() = this.get()
        set(value) = this.set(value.toLong())

    override fun bindBidirectional(other: Property<Number>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<Number>) {
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
        if (name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        result.append("value: ").append(get()).append("]")
        return result.toString()
    }

    /**
     * Creates an [ObjectProperty] that holds the value of this `LongProperty`. If the value of this
     * `LongProperty` changes, the value of the `ObjectProperty` will be updated automatically.
     *
     * @return the new `ObjectProperty`
     *
     * @since JavaFX 8.0
     */
    override fun asObject(): ObjectProperty<Long> {
        return object : ObjectPropertyBase<Long>(this@LongProperty.longValue) {

            private val acc: AccessControlContext = AccessController.getContext()

            init {
                BidirectionalBinding.bind(this as Property<Number>, this@LongProperty)
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String
                get() = this@LongProperty.name

            @Throws(Throwable::class)
            protected fun finalize() {
                try {
                    AccessController.doPrivileged(PrivilegedAction {
                        BidirectionalBinding.unbind(this, this@LongProperty)
                    }, this.acc)
                } finally {
                }
            }

        }
    }

    companion object {

        /**
         * Returns a `LongProperty` that wraps a [Property]. If the `Property` is already a `LongProperty`, it will be
         * returned. Otherwise a new `LongProperty` is created that is bound to the `Property`.
         *
         * This is very useful when bidirectionally binding an ObjectProperty<Long> and a LongProperty.
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
         * @param property
         *         The source `Property`
         *
         * @return A `LongProperty` that wraps the `Property` if necessary
         *
         * @since JavaFX 8.0
         */
        fun longProperty(property: Property<Number>): LongProperty {
            return if (property is LongProperty) property else object : LongPropertyBase() {

                private val acc: AccessControlContext = AccessController.getContext()

                init {
                    BidirectionalBinding.bind(this, property)
                }

                override val bean: Any?
                    get() = null // Virtual property, does not exist on a bean

                override val name: String
                    get() = property.name

                @Throws(Throwable::class)
                protected fun finalize() {
                    try {
                        AccessController.doPrivileged(PrivilegedAction {
                            BidirectionalBinding.unbind(property, this)
                        }, this.acc)
                    } finally {
                    }
                }

            }
        }

    }

}