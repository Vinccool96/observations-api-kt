package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.WeakInvalidationListener
import io.github.vinccool96.observationskt.beans.binding.ShortExpression

/**
 * Super class for all readonly properties wrapping a `Short`.
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableShortValue
 * @see ShortExpression
 * @see ReadOnlyProperty
 */
abstract class ReadOnlyShortProperty : ShortExpression(), ReadOnlyProperty<Number?> {

    /**
     * Returns a string representation of this `ReadOnlyShortProperty` object.
     *
     * @return a string representation of this `ReadOnlyShortProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyShortProperty [")
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
     * Creates a [ReadOnlyObjectProperty] that holds the value of this `ReadOnlyShortProperty`. If the value of this
     * `ReadOnlyShortProperty` changes, the value of the `ReadOnlyObjectProperty` will be updated automatically.
     *
     * @return the new `ReadOnlyObjectProperty`
     */
    override fun asObject(): ReadOnlyObjectProperty<Short> {
        return object : ReadOnlyObjectPropertyBase<Short>() {

            private var valid: Boolean = true

            private val listener: InvalidationListener = InvalidationListener {
                if (valid) {
                    valid = false
                    fireValueChangedEvent()
                }
            }

            init {
                this@ReadOnlyShortProperty.addListener(WeakInvalidationListener(this.listener))
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String?
                get() = this@ReadOnlyShortProperty.name

            override fun get(): Short {
                return this@ReadOnlyShortProperty.get()
            }

        }
    }

    companion object {

        /**
         * Returns a `ReadOnlyShortProperty` that wraps a [ReadOnlyProperty]. If the `ReadOnlyProperty` is already a
         * `ReadOnlyShortProperty`, it will be returned. Otherwise, a new `ReadOnlyShortProperty` is created that is
         * bound to the `ReadOnlyProperty`.
         *
         * Note: null values will be interpreted as `0`
         *
         * @param property The source `ReadOnlyProperty`
         * @param T The type of the wrapped number
         *
         * @return A `ReadOnlyShortProperty` that wraps the `ReadOnlyProperty` if necessary
         */
        fun <T : Number?> readOnlyShortProperty(property: ReadOnlyProperty<T>): ReadOnlyShortProperty {
            return if (property is ReadOnlyShortProperty) property else object : ReadOnlyShortPropertyBase() {

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

                override fun get(): Short {
                    this.valid = true
                    val value: T = property.value
                    return value?.toShort() ?: 0
                }

                override val bean: Any?
                    get() = null // Virtual property, no bean

                override val name: String?
                    get() = property.name

            }
        }

    }

}