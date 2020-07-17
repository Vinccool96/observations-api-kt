package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.WeakInvalidationListener
import io.github.vinccool96.observationskt.beans.binding.DoubleExpression

/**
 * Super class for all readonly properties wrapping a `Double`.
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableDoubleValue
 * @see DoubleExpression
 * @see ReadOnlyProperty
 */
abstract class ReadOnlyDoubleProperty : DoubleExpression(), ReadOnlyProperty<Number?> {

    /**
     * Returns a string representation of this `ReadOnlyDoubleProperty` object.
     *
     * @return a string representation of this `ReadOnlyDoubleProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyDoubleProperty [")
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
     * Creates a [ReadOnlyObjectProperty] that holds the value of this `ReadOnlyDoubleProperty`. If the value of this
     * `ReadOnlyDoubleProperty` changes, the value of the `ReadOnlyObjectProperty` will be updated automatically.
     *
     * @return the new `ReadOnlyObjectProperty`
     */
    override fun asObject(): ReadOnlyObjectProperty<Double> {
        return object : ReadOnlyObjectPropertyBase<Double>() {

            private var valid: Boolean = true

            private val listener: InvalidationListener = InvalidationListener {
                if (valid) {
                    valid = false
                    fireValueChangedEvent()
                }
            }

            init {
                this@ReadOnlyDoubleProperty.addListener(WeakInvalidationListener(this.listener))
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String?
                get() = this@ReadOnlyDoubleProperty.name

            override fun get(): Double {
                return this@ReadOnlyDoubleProperty.get()
            }

        }
    }

    companion object {

        /**
         * Returns a `ReadOnlyDoubleProperty` that wraps a [ReadOnlyProperty]. If the `ReadOnlyProperty` is already a
         * `ReadOnlyDoubleProperty`, it will be returned. Otherwise a new `ReadOnlyDoubleProperty` is created that is
         * bound to the `ReadOnlyProperty`.
         *
         * Note: null values will be interpreted as `0.0`
         *
         * @param property The source `ReadOnlyProperty`
         * @param T The type of the wrapped number
         *
         * @return A `ReadOnlyDoubleProperty` that wraps the `ReadOnlyProperty` if necessary
         */
        fun <T : Number?> readOnlyDoubleProperty(property: ReadOnlyProperty<T>): ReadOnlyDoubleProperty {
            return if (property is ReadOnlyDoubleProperty) property else object : ReadOnlyDoublePropertyBase() {

                private var valid: Boolean = true

                private val listener: InvalidationListener = InvalidationListener {
                    if (valid) {
                        valid = false
                        fireValueChangedEvent()
                    }
                }

                init {
                    property.addListener(WeakInvalidationListener(this.listener))
                }

                override fun get(): Double {
                    this.valid = true
                    val value: T = property.value
                    return value?.toDouble() ?: 0.0
                }

                override val bean: Any?
                    get() = null // Virtual property, no bean

                override val name: String?
                    get() = property.name

            }
        }

    }

}