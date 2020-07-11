package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.WeakInvalidationListener
import io.github.vinccool96.observationskt.beans.binding.IntExpression

/**
 * Super class for all readonly properties wrapping a `Int`.
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableIntValue
 * @see IntExpression
 * @see ReadOnlyProperty
 */
abstract class ReadOnlyIntProperty : IntExpression(), ReadOnlyProperty<Number> {

    /**
     * Returns a string representation of this `ReadOnlyIntProperty` object.
     *
     * @return a string representation of this `ReadOnlyIntProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyIntProperty [")
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
     * Creates a [ReadOnlyObjectProperty] that holds the value of this `ReadOnlyIntProperty`. If the value of this
     * `ReadOnlyIntProperty` changes, the value of the `ReadOnlyObjectProperty` will be updated automatically.
     *
     * @return the new `ReadOnlyObjectProperty`
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
                this@ReadOnlyIntProperty.addListener(WeakInvalidationListener(this.listener))
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String
                get() = this@ReadOnlyIntProperty.name

            override fun get(): Int {
                return this@ReadOnlyIntProperty.get()
            }

        }
    }

    companion object {

        /**
         * Returns a `ReadOnlyIntProperty` that wraps a [ReadOnlyProperty]. If the `ReadOnlyProperty` is already a
         * `ReadOnlyIntProperty`, it will be returned. Otherwise a new `ReadOnlyIntProperty` is created that is
         * bound to the `ReadOnlyProperty`.
         *
         * Note: null values will be interpreted as `0`
         *
         * @param property The source `ReadOnlyProperty`
         * @param T The type of the wrapped number
         *
         * @return A `ReadOnlyIntProperty` that wraps the `ReadOnlyProperty` if necessary
         */
        fun <T : Number?> readOnlyIntProperty(property: ReadOnlyProperty<T>): ReadOnlyIntProperty {
            return if (property is ReadOnlyIntProperty) property else object : ReadOnlyIntPropertyBase() {

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