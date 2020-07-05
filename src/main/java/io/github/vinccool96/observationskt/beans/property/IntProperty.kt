package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableIntValue
import io.github.vinccool96.observationskt.sun.binding.BidirectionalBinding
import java.security.AccessControlContext
import java.security.AccessController
import java.security.PrivilegedAction

/**
 * This class defines a [Property] wrapping a `Int` value.
 *
 * The value of a `IntProperty` can be get and set with [get], [set], and [value].
 *
 * A property can be bound and unbound unidirectional with [bind] and [unbind]. Bidirectional bindings can be created
 * and removed with [bindBidirectional] and [unbindBidirectional].
 *
 * The context of a `ObjectProperty` can be read with [bean] and [name].
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableIntValue
 * @see WritableIntValue
 * @see ReadOnlyIntProperty
 * @see Property
 * @since JavaFX 2.0
 */
@Suppress("UNCHECKED_CAST")
abstract class IntProperty : ReadOnlyIntProperty(), Property<Number>, WritableIntValue {

    override var value: Number
        get() = this.get()
        set(value) = this.set(value.toInt())

    override fun bindBidirectional(other: Property<Number>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<Number>) {
        Bindings.unbindBidirectional(this, other)
    }

    /**
     * Returns a string representation of this `IntProperty` object.
     *
     * @return a string representation of this `IntProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("IntProperty [")
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
     * Creates an [ObjectProperty] that bidirectionally bound to this `IntProperty`. If the value of this
     * `IntProperty` changes, the value of the `ObjectProperty` will be updated automatically and vice-versa.
     *
     * Can be used for binding an ObjectProperty to IntProperty.
     *
     * ```
     * val intProperty: IntProperty = SimpleIntProperty(1)
     * val objectProperty: ObjectProperty<Int> = SimpleObjectProperty(2)
     *
     * objectProperty.bind(intProperty.asObject())
     * ```
     *
     * @return the new `ObjectProperty`
     *
     * @since JavaFX 8.0
     */
    override fun asObject(): ObjectProperty<Int> {
        return object : ObjectPropertyBase<Int>(this@IntProperty.intValue) {

            private val acc: AccessControlContext = AccessController.getContext()

            init {
                BidirectionalBinding.bind(this as Property<Number>, this@IntProperty)
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String
                get() = this@IntProperty.name

            @Throws(Throwable::class)
            protected fun finalize() {
                try {
                    AccessController.doPrivileged(PrivilegedAction {
                        BidirectionalBinding.unbind(this, this@IntProperty)
                    }, this.acc)
                } finally {
                }
            }

        }
    }

    companion object {

        /**
         * Returns a `IntProperty` that wraps a [Property]. If the `Property` is already a `IntProperty`, it
         * will be returned. Otherwise a new `IntProperty` is created that is bound to the `Property`.
         *
         * This is very useful when bidirectionally binding an ObjectProperty<Int> and an IntProperty.
         * ```
         * val intProperty: IntProperty = SimpleIntProperty(1)
         * val objectProperty: ObjectProperty<Int> = SimpleObjectProperty(2)
         *
         * // Need to keep the reference as bidirectional binding uses weak references
         * val objectAsInt: IntProperty = IntProperty.intProperty(objectProperty)
         *
         * intProperty.bindBidirectional(objectAsInt)
         * ```
         *
         * Another approach is to convert the LongProperty to ObjectProperty using [asObject] method.
         *
         * @param property
         *         The source `Property`
         *
         * @return A `IntProperty` that wraps the `Property` if necessary
         *
         * @since JavaFX 8.0
         */
        fun doubleProperty(property: Property<Int>): IntProperty {
            return if (property is IntProperty) property else object : IntPropertyBase() {

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