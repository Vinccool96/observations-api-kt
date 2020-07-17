package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.WeakInvalidationListener
import io.github.vinccool96.observationskt.beans.binding.BooleanExpression

/**
 * Super class for all readonly properties wrapping a `Boolean`.
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableBooleanValue
 * @see BooleanExpression
 * @see ReadOnlyProperty
 */
abstract class ReadOnlyBooleanProperty : BooleanExpression(), ReadOnlyProperty<Boolean?> {

    /**
     * Returns a string representation of this `ReadOnlyBooleanProperty` object.
     *
     * @return a string representation of this `ReadOnlyBooleanProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyBooleanProperty [")
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
     * Creates a [ReadOnlyObjectProperty] that holds the value of this `ReadOnlyBooleanProperty`. If the value of this
     * `ReadOnlyBooleanProperty` changes, the value of the `ReadOnlyObjectProperty` will be updated automatically.
     *
     * @return the new `ReadOnlyObjectProperty`
     */
    override fun asObject(): ReadOnlyObjectProperty<Boolean> {
        return object : ReadOnlyObjectPropertyBase<Boolean>() {

            private var valid: Boolean = true

            private val listener: InvalidationListener = InvalidationListener {
                if (valid) {
                    valid = false
                    fireValueChangedEvent()
                }
            }

            init {
                this@ReadOnlyBooleanProperty.addListener(WeakInvalidationListener(this.listener))
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String?
                get() = this@ReadOnlyBooleanProperty.name

            override fun get(): Boolean {
                return this@ReadOnlyBooleanProperty.get()
            }

        }
    }

    companion object {

        /**
         * Returns a `ReadOnlyBooleanProperty` that wraps a [ReadOnlyProperty]. If the `ReadOnlyProperty` is already a
         * `ReadOnlyBooleanProperty`, it will be returned. Otherwise a new `ReadOnlyBooleanProperty` is created that is
         * bound to the `ReadOnlyProperty`.
         *
         * Note: null values will be interpreted as "false"
         *
         * @param property The source `ReadOnlyProperty`
         *
         * @return A `ReadOnlyBooleanProperty` that wraps the `ReadOnlyProperty` if necessary
         */
        fun readOnlyBooleanProperty(property: ReadOnlyProperty<Boolean?>): ReadOnlyBooleanProperty {
            return if (property is ReadOnlyBooleanProperty) property else object : ReadOnlyBooleanPropertyBase() {

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

                override fun get(): Boolean {
                    this.valid = true
                    val value: Boolean? = property.value
                    return value ?: false
                }

                override val bean: Any?
                    get() = null // Virtual property, no bean

                override val name: String?
                    get() = property.name

            }
        }

    }

}