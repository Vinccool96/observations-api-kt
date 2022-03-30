package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.value.WritableIntValue
import io.github.vinccool96.observationskt.sun.binding.BidirectionalBinding
import io.github.vinccool96.observationskt.sun.binding.Logging

/**
 * This class defines a [Property] wrapping a `Int` value.
 *
 * The value of a `IntProperty` can be got and set with [get], [set], and [value].
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
 */
@Suppress("UNCHECKED_CAST")
abstract class IntProperty : ReadOnlyIntProperty(), Property<Number?>, WritableIntValue {

    override var value: Number?
        get() = super.value
        set(value) {
            if (value == null) {
                Logging.getLogger().fine("Attempt to set int property to null, using default value instead.",
                        NullPointerException())
            }
            this.set(value?.toInt() ?: 0)
        }

    override fun bindBidirectional(other: Property<Number?>) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<Number?>) {
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
        if (name != null && name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        result.append("value: ").append(get()).append("]")
        return result.toString()
    }

    /**
     * Creates an [ObjectProperty] that is bidirectionally bound to this `IntProperty`. If the value of this
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
     */
    override fun asObject(): ObjectProperty<Int> {
        return object : ObjectPropertyBase<Int>(this@IntProperty.intValue) {

            init {
                BidirectionalBinding.bind(this as Property<Number?>, this@IntProperty)
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String?
                get() = this@IntProperty.name

        }
    }

    companion object {

        /**
         * Returns a `IntProperty` that wraps a [Property]. If the `Property` is already a `IntProperty`, it
         * will be returned. Otherwise, a new `IntProperty` is created that is bound to the `Property`.
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
         * Another approach is to convert the IntProperty to ObjectProperty using [asObject] method.
         *
         * @param property The source `Property`
         *
         * @return A `IntProperty` that wraps the `Property` if necessary
         */
        fun intProperty(property: Property<Int?>): IntProperty {
            return if (property is IntProperty) property else object : IntPropertyBase() {

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