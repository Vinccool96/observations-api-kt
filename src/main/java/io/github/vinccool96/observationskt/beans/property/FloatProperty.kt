package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableFloatValue
import io.github.vinccool96.observationskt.sun.binding.BidirectionalBinding
import java.security.AccessControlContext
import java.security.AccessController
import java.security.PrivilegedAction

/**
 * This class defines a [Property] wrapping a `Float` value.
 *
 * The value of a `FloatProperty` can be get and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `ObjectProperty` can be read with [bean] and [name].
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableFloatValue
 * @see WritableFloatValue
 * @see ReadOnlyFloatProperty
 * @see Property
 */
@Suppress("UNCHECKED_CAST")
abstract class FloatProperty : ReadOnlyFloatProperty(), Property<Number>, WritableFloatValue {

    override var value: Number
        get() = this.get()
        set(value) = this.set(value.toFloat())

    override fun bindBidirectional(other: Property<Number>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<Number>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `FloatProperty` object.
     *
     * @return a string representation of this `FloatProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("FloatProperty [")
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
     * Creates an [ObjectProperty] that bidirectionally bound to this `FloatProperty`. If the value of this
     * `FloatProperty` changes, the value of the `ObjectProperty` will be updated automatically and vice-versa.
     *
     * Can be used for binding an ObjectProperty to FloatProperty.
     *
     * ```
     * val floatProperty: FloatProperty = SimpleFloatProperty(1.0F)
     * val objectProperty: ObjectProperty<Float> = SimpleObjectProperty(2.0F)
     *
     * objectProperty.bind(floatProperty.asObject())
     * ```
     *
     * @return the new `ObjectProperty`
     */
    override fun asObject(): ObjectProperty<Float> {
        return object : ObjectPropertyBase<Float>(this@FloatProperty.floatValue) {

            private val acc: AccessControlContext = AccessController.getContext()

            init {
                BidirectionalBinding.bind(this as Property<Number>, this@FloatProperty)
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String
                get() = this@FloatProperty.name

            @Throws(Throwable::class)
            protected fun finalize() {
                try {
                    AccessController.doPrivileged(PrivilegedAction {
                        BidirectionalBinding.unbind(this, this@FloatProperty)
                    }, this.acc)
                } finally {
                }
            }

        }
    }

    companion object {

        /**
         * Returns a `FloatProperty` that wraps a [Property]. If the `Property` is already a `FloatProperty`, it will be
         * returned. Otherwise a new `FloatProperty` is created that is bound to the `Property`.
         *
         * This is very useful when bidirectionally binding an ObjectProperty<Float> and a FloatProperty.
         * ```
         * val floatProperty: FloatProperty = SimpleFloatProperty(1.0F)
         * val objectProperty: ObjectProperty<Double> = SimpleObjectProperty(2.0F)
         *
         * // Need to keep the reference as bidirectional binding uses weak references
         * val objectAsFloat: FloatProperty = FloatProperty.floatProperty(objectProperty)
         *
         * doubleProperty.bindBidirectional(objectAsFloat)
         * ```
         *
         * Another approach is to convert the FloatProperty to ObjectProperty using [asObject] method.
         *
         * @param property The source `Property`
         *
         * @return A `FloatProperty` that wraps the `Property` if necessary
         */
        fun floatProperty(property: Property<Float>): FloatProperty {
            return if (property is FloatProperty) property else object : FloatPropertyBase() {

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