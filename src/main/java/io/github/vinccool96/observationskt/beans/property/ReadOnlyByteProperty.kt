package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.WeakInvalidationListener
import io.github.vinccool96.observationskt.beans.binding.ByteExpression

/**
 * Super class for all readonly properties wrapping a `Byte`.
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableByteValue
 * @see ByteExpression
 * @see ReadOnlyProperty
 */
abstract class ReadOnlyByteProperty : ByteExpression(), ReadOnlyProperty<Number?> {

    /**
     * Returns a string representation of this `ReadOnlyByteProperty` object.
     *
     * @return a string representation of this `ReadOnlyByteProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyByteProperty [")
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
     * Creates a [ReadOnlyObjectProperty] that holds the value of this `ReadOnlyByteProperty`. If the value of this
     * `ReadOnlyByteProperty` changes, the value of the `ReadOnlyObjectProperty` will be updated automatically.
     *
     * @return the new `ReadOnlyObjectProperty`
     */
    override fun asObject(): ReadOnlyObjectProperty<Byte> {
        return object : ReadOnlyObjectPropertyBase<Byte>() {

            private var valid: Boolean = true

            private val listener: InvalidationListener = InvalidationListener {
                if (valid) {
                    valid = false
                    fireValueChangedEvent()
                }
            }

            init {
                this@ReadOnlyByteProperty.addListener(WeakInvalidationListener(this.listener))
            }

            override val bean: Any?
                get() = null // Virtual property, does not exist on a bean

            override val name: String?
                get() = this@ReadOnlyByteProperty.name

            override fun get(): Byte {
                return this@ReadOnlyByteProperty.get()
            }

        }
    }

    companion object {

        /**
         * Returns a `ReadOnlyByteProperty` that wraps a [ReadOnlyProperty]. If the `ReadOnlyProperty` is already a
         * `ReadOnlyByteProperty`, it will be returned. Otherwise, a new `ReadOnlyByteProperty` is created that is
         * bound to the `ReadOnlyProperty`.
         *
         * Note: null values will be interpreted as `0`
         *
         * @param property The source `ReadOnlyProperty`
         * @param T The type of the wrapped number
         *
         * @return A `ReadOnlyByteProperty` that wraps the `ReadOnlyProperty` if necessary
         */
        fun <T : Number?> readOnlyByteProperty(property: ReadOnlyProperty<T>): ReadOnlyByteProperty {
            return if (property is ReadOnlyByteProperty) property else object : ReadOnlyBytePropertyBase() {

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

                override fun get(): Byte {
                    this.valid = true
                    val value: T = property.value
                    return value?.toByte() ?: 0
                }

                override val bean: Any?
                    get() = null // Virtual property, no bean

                override val name: String?
                    get() = property.name

            }
        }

    }

}