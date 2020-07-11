package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.WeakInvalidationListener
import io.github.vinccool96.observationskt.beans.binding.LongExpression

/**
 * Super class for all readonly properties wrapping a `Long`.
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableLongValue
 * @see LongExpression
 * @see ReadOnlyProperty
 */
abstract class ReadOnlyLongProperty : LongExpression(), ReadOnlyProperty<Number> {

    /**
     * Returns a string representation of this `ReadOnlyLongProperty` object.
     *
     * @return a string representation of this `ReadOnlyLongProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyLongProperty [")
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
     * Creates a [ReadOnlyObjectProperty] that holds the value of this `ReadOnlyLongProperty`. If the value of this
     * `ReadOnlyLongProperty` changes, the value of the `ReadOnlyObjectProperty` will be updated automatically.
     *
     * @return the new `ReadOnlyObjectProperty`
     */
    override fun asObject(): ReadOnlyObjectProperty<Long> {
        return object : ReadOnlyObjectPropertyBase<Long>() {

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
                this@ReadOnlyLongProperty.addListener(WeakInvalidationListener(this.listener))
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String
                get() = this@ReadOnlyLongProperty.name

            override fun get(): Long {
                return this@ReadOnlyLongProperty.get()
            }

        }
    }

    companion object {

        /**
         * Returns a `ReadOnlyLongProperty` that wraps a [ReadOnlyProperty]. If the `ReadOnlyProperty` is already a
         * `ReadOnlyLongProperty`, it will be returned. Otherwise a new `ReadOnlyLongProperty` is created that is
         * bound to the `ReadOnlyProperty`.
         *
         * Note: null values will be interpreted as `0L`
         *
         * @param property The source `ReadOnlyProperty`
         * @param T The type of the wrapped number
         *
         * @return A `ReadOnlyLongProperty` that wraps the `ReadOnlyProperty` if necessary
         */
        fun <T : Number?> readOnlyLongProperty(property: ReadOnlyProperty<T>): ReadOnlyLongProperty {
            return if (property is ReadOnlyLongProperty) property else object : ReadOnlyLongPropertyBase() {

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

                override fun get(): Long {
                    this.valid = true
                    val value: T = property.value
                    return value?.toLong() ?: 0L
                }

                override val bean: Any?
                    get() = null // Virtual property, no bean

                override val name: String
                    get() = property.name

            }
        }

    }

}