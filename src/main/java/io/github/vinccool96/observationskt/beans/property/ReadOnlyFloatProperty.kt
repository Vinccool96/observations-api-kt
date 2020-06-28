package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.WeakInvalidationListener
import io.github.vinccool96.observationskt.beans.binding.FloatExpression

/**
 * Super class for all readonly properties wrapping a `Float`.
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableFloatValue
 * @see FloatExpression
 * @see ReadOnlyProperty
 * @since JavaFX 2.0
 */
abstract class ReadOnlyFloatProperty : FloatExpression(), ReadOnlyProperty<Number> {

    /**
     * Returns a string representation of this `ReadOnlyFloatProperty` object.
     *
     * @return a string representation of this `ReadOnlyFloatProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyFloatProperty [")
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
     * Creates a [ReadOnlyObjectProperty] that holds the value of this `ReadOnlyFloatProperty`. If the value of this
     * `ReadOnlyFloatProperty` changes, the value of the `ReadOnlyObjectProperty` will be updated automatically.
     *
     * @return the new `ReadOnlyObjectProperty`
     *
     * @since JavaFX 8.0
     */
    override fun asObject(): ReadOnlyObjectProperty<Float> {
        return object : ReadOnlyObjectPropertyBase<Float>() {

            private var valid: Boolean = true

            private val listener: InvalidationListener = object : InvalidationListener {

                override fun invalidated(observable: Observable) {
                    if (valid) {
                        valid = false
                        fireValueChangedEvent()
                    }
                }

            }

            init {
                this@ReadOnlyFloatProperty.addListener(WeakInvalidationListener(this.listener))
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String
                get() = this@ReadOnlyFloatProperty.name

            override fun get(): Float {
                return this@ReadOnlyFloatProperty.get()
            }

        }
    }

    companion object {

        /**
         * Returns a `ReadOnlyFloatProperty` that wraps a [ReadOnlyProperty]. If the `ReadOnlyProperty` is already a
         * `ReadOnlyFloatProperty`, it will be returned. Otherwise a new `ReadOnlyFloatProperty` is created that is
         * bound to the `ReadOnlyProperty`.
         *
         * Note: null values will be interpreted as `0.0`
         *
         * @param property
         *         The source `ReadOnlyProperty`
         * @param T
         *         The type of the wrapped number
         *
         * @return A `ReadOnlyFloatProperty` that wraps the `ReadOnlyProperty` if necessary
         *
         * @since JavaFX 8.0
         */
        fun <T : Number?> readOnlyFloatProperty(property: ReadOnlyProperty<T>): ReadOnlyFloatProperty {
            return if (property is ReadOnlyFloatProperty) property else object : ReadOnlyFloatPropertyBase() {

                private var valid: Boolean = true

                private val listener: InvalidationListener = object : InvalidationListener {

                    override fun invalidated(observable: Observable) {
                        if (valid) {
                            valid = false
                            fireValueChangedEvent()
                        }
                    }

                }

                init {
                    property.addListener(WeakInvalidationListener(this.listener))
                }

                override fun get(): Float {
                    this.valid = true
                    val value: T = property.value
                    return value?.toFloat() ?: 0.0F
                }

                override val bean: Any?
                    get() = null // Virtual property, no bean

                override val name: String
                    get() = property.name

            }
        }

    }

}