package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableDoubleValue
import io.github.vinccool96.observationskt.sun.binding.BidirectionalBinding
import java.security.AccessControlContext
import java.security.AccessController
import java.security.PrivilegedAction

/**
 * This class defines a [Property] wrapping a `Double` value.
 *
 * The value of a `DoubleProperty` can be get and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `ObjectProperty` can be read with [bean] and [name].
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableDoubleValue
 * @see WritableDoubleValue
 * @see ReadOnlyDoubleProperty
 * @see Property
 * @since JavaFX 2.0
 */
abstract class DoubleProperty : ReadOnlyDoubleProperty(), Property<Number>, WritableDoubleValue {

    override var value: Number
        get() = this.get()
        set(value) = this.set(value.toDouble())

    override fun bindBidirectional(other: Property<Number>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<Number>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `DoubleProperty` object.
     *
     * @return a string representation of this `DoubleProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("DoubleProperty [")
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
     * Creates an [ObjectProperty] that holds the value of this `DoubleProperty`. If the value of this
     * `DoubleProperty` changes, the value of the `ObjectProperty` will be updated automatically.
     *
     * @return the new `ObjectProperty`
     *
     * @since JavaFX 8.0
     */
    override fun asObject(): ObjectProperty<Double> {
        return object : ObjectPropertyBase<Double>(this@DoubleProperty.doubleValue) {

            private val acc: AccessControlContext = AccessController.getContext()

            init {
                BidirectionalBinding.bind(this as Property<Number>, this@DoubleProperty)
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String
                get() = this@DoubleProperty.name

            @Throws(Throwable::class)
            protected fun finalize() {
                try {
                    AccessController.doPrivileged(PrivilegedAction {
                        BidirectionalBinding.unbind(this, this@DoubleProperty)
                    }, this.acc)
                } finally {
                }
            }

        }
    }

    companion object {

        /**
         * Returns a `DoubleProperty` that wraps a [Property]. If the `Property` is already a `DoubleProperty`, it
         * will be returned. Otherwise a new `DoubleProperty` is created that is bound to the `Property`.
         *
         * Note: null values in the source property will be interpreted as `false`
         *
         * @param property
         *         The source `Property`
         *
         * @return A `DoubleProperty` that wraps the `Property` if necessary
         *
         * @since JavaFX 8.0
         */
        fun doubleProperty(property: Property<Double>): DoubleProperty {
            return if (property is DoubleProperty) property else object : DoublePropertyBase() {

                private val acc: AccessControlContext = AccessController.getContext()

                init {
                    BidirectionalBinding.bind(this, property as Property<Number>)
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