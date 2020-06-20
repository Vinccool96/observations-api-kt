package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.value.ObservableBooleanValue
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.StringFormatter
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * A `BooleanExpression` is a [ObservableBooleanValue] plus additional convenience methods to generate
 * bindings in a fluent style.
 *
 * A concrete sub-class of `BooleanExpression` has to implement the method [ObservableBooleanValue.get],
 * which provides the actual value of this expression.
 *
 * @since JavaFX 2.0
 *
 * @constructor Sole constructor
 */
abstract class BooleanExpression() : ObservableBooleanValue {

    override val value: Boolean
        get() = this.get()

    /**
     * Creates a new `BooleanExpression` that performs the conditional AND-operation on this `BooleanExpression` and a
     * [ObservableBooleanValue].
     *
     * @param other
     *         the other `ObservableBooleanValue`
     *
     * @return the new `BooleanExpression`
     */
    fun and(other: ObservableBooleanValue): BooleanBinding {
        return Bindings.and(this, other)
    }

    /**
     * Creates a new `BooleanExpression` that performs the conditional OR-operation on this `BooleanExpression` and a
     * [ObservableBooleanValue].
     *
     * @param other
     *         the other `ObservableBooleanValue`
     *
     * @return the new `BooleanExpression`
     */
    fun or(other: ObservableBooleanValue): BooleanBinding {
        return Bindings.or(this, other)
    }

    /**
     * Creates a new `BooleanExpression` that calculates the negation of this `BooleanExpression`.
     *
     * @return the new `BooleanExpression`
     */
    fun not(): BooleanBinding {
        return Bindings.not(this)
    }

    /**
     * Creates a new `BooleanExpression` that holds `true` if this and another [ObservableBooleanValue] are equal.
     *
     * @param other
     *         the other `ObservableBooleanValue`
     *
     * @return the new `BooleanExpression`
     */
    fun isEqualTo(other: ObservableBooleanValue): BooleanBinding {
        return Bindings.equal(this, other)
    }

    /**
     * Creates a new `BooleanExpression` that holds `true` if this and another [ObservableBooleanValue] are equal.
     *
     * @param other
     *         the other `ObservableBooleanValue`
     *
     * @return the new `BooleanExpression`
     */
    fun isNotEqualTo(other: ObservableBooleanValue): BooleanBinding {
        return Bindings.notEqual(this, other)
    }

    /**
     * Creates a [StringBinding] that holds the value of this `BooleanExpression` turned into a `String`. If the value
     * of this `BooleanExpression` changes, the value of the `StringBinding` will be updated automatically.
     *
     * @return the new `StringBinding`
     */
    fun asString(): StringBinding {
        return StringFormatter.convert(this) as StringBinding
    }

    /**
     * Creates an [ObjectExpression] that holds the value of this `BooleanExpression`. If the value of this
     * `BooleanExpression` changes, the value of the `ObjectExpression` will be updated automatically.
     *
     * @return the new `ObjectExpression`
     *
     * @since JavaFX 8.0
     */
    open fun asObject(): ObjectExpression<Boolean> {
        return object : ObjectBinding<Boolean>() {
            override fun dispose() {
                unbindOB(this@BooleanExpression)
            }

            override fun computeValue(): Boolean {
                return this@BooleanExpression.value
            }

            init {
                bindOB(this@BooleanExpression)
            }
        }
    }

    companion object {

        /**
         * Returns a `BooleanExpression` that wraps a [ObservableBooleanValue]. If the `ObservableBooleanValue` is
         * already a `BooleanExpression`, it will be returned. Otherwise a new [BooleanBinding] is created that is bound
         * to the `ObservableBooleanValue`.
         *
         * @param value The source `ObservableBooleanValue`
         *
         * @return A `BooleanExpression` that wraps the `ObservableBooleanValue` if necessary
         */
        fun booleanExpression(value: ObservableBooleanValue?): BooleanExpression {
            if (value == null) {
                throw NullPointerException("Value must be specified.")
            }
            return if (value is BooleanExpression) value else object : BooleanBinding() {
                override fun dispose() {
                    super.unbindBB(value)
                }

                override fun computeValue(): Boolean {
                    return value.get()
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<ObservableBooleanValue>
                    get() = ObservableCollections.singletonObservableList(value)

                init {
                    super.bindBB(value)
                }
            }
        }

        /**
         * Returns a `BooleanExpression` that wraps an [ObservableValue]. If the `ObservableValue` is already a
         * `BooleanExpression`, it will be returned. Otherwise a new [BooleanBinding] is created that is bound to the
         * `ObservableValue`.
         *
         * Note: null values will be interpreted as "false".
         *
         * @param value The source `ObservableValue`
         *
         * @return A `BooleanExpression` that wraps the `ObservableValue` if necessary
         *
         * @throws NullPointerException if `value` is `null`
         * @since JavaFX 8.0
         */
        fun booleanExpression(value: ObservableValue<Boolean>?): BooleanExpression {
            if (value == null) {
                throw NullPointerException("Value must be specified.")
            }
            return if (value is BooleanExpression) value else object : BooleanBinding() {
                override fun dispose() {
                    super.unbindBB(value)
                }

                override fun computeValue(): Boolean {
                    return value.value
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<ObservableValue<Boolean>>
                    get() = ObservableCollections.singletonObservableList(value)

                init {
                    super.bindBB(value)
                }
            }
        }

    }

}