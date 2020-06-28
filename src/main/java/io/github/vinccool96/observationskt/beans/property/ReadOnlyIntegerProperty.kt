package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.WeakInvalidationListener
import io.github.vinccool96.observationskt.beans.binding.IntegerExpression

/**
 * Super class for all readonly properties wrapping a `Integer`.
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableIntegerValue
 * @see IntegerExpression
 * @see ReadOnlyProperty
 * @since JavaFX 2.0
 */
abstract class ReadOnlyIntegerProperty : IntegerExpression(), ReadOnlyProperty<Number> {

    /**
     * Returns a string representation of this `ReadOnlyIntegerProperty` object.
     *
     * @return a string representation of this `ReadOnlyIntegerProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyIntegerProperty [")
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
     * Creates a [ReadOnlyObjectProperty] that holds the value of this `ReadOnlyIntegerProperty`. If the value of this
     * `ReadOnlyIntegerProperty` changes, the value of the `ReadOnlyObjectProperty` will be updated automatically.
     *
     * @return the new `ReadOnlyObjectProperty`
     *
     * @since JavaFX 8.0
     */
    override fun asObject(): ReadOnlyObjectProperty<Int> {
        return object : ReadOnlyObjectPropertyBase<Int>() {

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
                this@ReadOnlyIntegerProperty.addListener(WeakInvalidationListener(this.listener))
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String
                get() = this@ReadOnlyIntegerProperty.name

            override fun get(): Int {
                return this@ReadOnlyIntegerProperty.get()
            }

        }
    }

    companion object {

        /**
         * Returns a `ReadOnlyIntegerProperty` that wraps a [ReadOnlyProperty]. If the `ReadOnlyProperty` is already a
         * `ReadOnlyIntegerProperty`, it will be returned. Otherwise a new `ReadOnlyIntegerProperty` is created that is
         * bound to the `ReadOnlyProperty`.
         *
         * Note: null values will be interpreted as `0`
         *
         * @param property
         *         The source `ReadOnlyProperty`
         * @param T
         *         The type of the wrapped number
         *
         * @return A `ReadOnlyIntegerProperty` that wraps the `ReadOnlyProperty` if necessary
         *
         * @since JavaFX 8.0
         */
        fun <T : Number?> readOnlyIntegerProperty(property: ReadOnlyProperty<T>): ReadOnlyIntegerProperty {
            return if (property is ReadOnlyIntegerProperty) property else object : ReadOnlyIntegerPropertyBase() {

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

                override fun get(): Int {
                    this.valid = true
                    val value: T = property.value
                    return value?.toInt() ?: 0
                }

                override val bean: Any?
                    get() = null // Virtual property, no bean

                override val name: String
                    get() = property.name

            }
        }

    }

}