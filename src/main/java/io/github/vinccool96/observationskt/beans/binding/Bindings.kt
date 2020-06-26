package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.property.Property
import io.github.vinccool96.observationskt.beans.value.*
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.BidirectionalBinding
import io.github.vinccool96.observationskt.sun.binding.ObjectConstant
import io.github.vinccool96.observationskt.sun.binding.StringConstant
import io.github.vinccool96.observationskt.sun.binding.StringFormatter
import io.github.vinccool96.observationskt.sun.collections.ImmutableObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection
import java.lang.ref.WeakReference
import java.util.*

object Bindings {

    // Bidirectional Bindings
    // =================================================================================================================

    /**
     * Generates a bidirectional binding (or "bind with inverse") between two instances of [Property].
     *
     * A bidirectional binding is a binding that works in both directions. If two properties `a` and `b` are linked with
     * a bidirectional binding and the value of `a` changes, `b` is set to the same value automatically. And vice versa,
     * if `b` changes, `a` is set to the same value.
     *
     * A bidirectional binding can be removed with [unbindBidirectional].
     *
     * Note: this implementation of a bidirectional binding behaves differently from all other bindings here in two
     * important aspects. A property that is linked to another property with a bidirectional binding can still be set
     * (usually bindings would throw an exception). Secondly bidirectional bindings are calculated eagerly, i.e. a bound
     * property is updated immediately.
     *
     * @param T
     *         the types of the properties
     * @param property1
     *         the first `Property<T>`
     * @param property2
     *         the second `Property<T>`
     *
     * @throws IllegalArgumentException
     *         if both properties are equal
     */
    fun <T> bindBidirectional(property1: Property<T>, property2: Property<T>) {
        BidirectionalBinding.bind(property1, property2)
    }

    /**
     * Delete a bidirectional binding that was previously defined with [bindBidirectional].
     *
     * @param T
     *         the types of the properties
     * @param property1
     *         the first `Property<T>`
     * @param property2
     *         the second `Property<T>`
     *
     * @throws IllegalArgumentException
     *         if both properties are equal
     */
    fun <T> unbindBidirectional(property1: Property<T>, property2: Property<T>) {
        BidirectionalBinding.unbind(property1, property2)
    }

    // Numbers
    // =================================================================================================================

    // =================================================================================================================
    // Negation

    /**
     * Creates a new {@link NumberBinding} that calculates the negation of a {@link ObservableNumberValue}.
     *
     * @param value the operand
     *
     * @return the new {@code NumberBinding}
     */
    fun negate(value: ObservableNumberValue): NumberBinding {
        return when (value) {
            is ObservableDoubleValue -> object : DoubleBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Double {
                    return -value.doubleValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
            is ObservableFloatValue -> object : FloatBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Float {
                    return -value.floatValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
            is ObservableLongValue -> object : LongBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Long {
                    return -value.longValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
            else -> object : IntegerBinding() {

                init {
                    super.bind(value)
                }

                override fun dispose() {
                    super.unbind(value)
                }

                override fun computeValue(): Int {
                    return -value.intValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = ObservableCollections.singletonObservableList(value)

            }
        }
    }

    // =================================================================================================================
    // Sum

    private fun add(op1: ObservableNumberValue, op2: ObservableNumberValue,
            vararg dependencies: Observable): NumberBinding {
        require(dependencies.isNotEmpty())
        return when {
            op1 is ObservableDoubleValue || op2 is ObservableDoubleValue -> object : DoubleBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Double {
                    return op1.doubleValue + op2.doubleValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableFloatValue || op2 is ObservableFloatValue -> object : FloatBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Float {
                    return op1.floatValue + op2.floatValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableLongValue || op2 is ObservableLongValue -> object : LongBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Long {
                    return op1.longValue + op2.longValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : IntegerBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Int {
                    return op1.intValue + op2.intValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }
    }

    fun add(op1: ObservableNumberValue, op2: ObservableNumberValue): NumberBinding {
        return add(op1, op2, op1, op2)
    }

    // boolean
    // =================================================================================================================

    class BooleanAndBinding(internal val op1: ObservableBooleanValue, private val op2: ObservableBooleanValue) :
            BooleanBinding() {

        private val observer: InvalidationListener

        init {
            this.observer = ShortCircuitAndInvalidator(this)

            this.op1.addListener(this.observer)
            this.op2.addListener(this.observer)
        }

        override fun dispose() {
            this.op1.removeListener(this.observer)
            this.op2.removeListener(this.observer)
        }

        override fun computeValue(): Boolean {
            return this.op1.get() && this.op2.get()
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = ImmutableObservableList(this.op1, this.op2)

    }

    private class ShortCircuitAndInvalidator(binding: BooleanAndBinding) : InvalidationListener {

        private val ref: WeakReference<BooleanAndBinding> = WeakReference(binding)

        override fun invalidated(observable: Observable) {
            val binding = this.ref.get()
            if (binding == null) {
                observable.removeListener(this)
            } else {
                // short-circuit invalidation. This BooleanBinding becomes only invalid if the first operator changes or
                // the first parameter is true.
                if (binding.op1 == observable || (binding.valid && binding.op1.get())) {
                    binding.invalidate()
                }
            }
        }

    }

    /**
     * Creates a [BooleanBinding] that calculates the conditional-AND operation on the value of two instance of
     * [ObservableBooleanValue].
     *
     * @param op1 first `ObservableBooleanValue`
     * @param op2 second `ObservableBooleanValue`
     *
     * @return the new `BooleanBinding`
     */
    fun and(op1: ObservableBooleanValue, op2: ObservableBooleanValue): BooleanBinding {
        return BooleanAndBinding(op1, op2)
    }

    class BooleanOrBinding(internal val op1: ObservableBooleanValue, private val op2: ObservableBooleanValue) :
            BooleanBinding() {

        private val observer: InvalidationListener

        init {
            this.observer = ShortCircuitOrInvalidator(this)

            this.op1.addListener(this.observer)
            this.op2.addListener(this.observer)
        }

        override fun dispose() {
            this.op1.removeListener(this.observer)
            this.op2.removeListener(this.observer)
        }

        override fun computeValue(): Boolean {
            return this.op1.get() || this.op2.get()
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = ImmutableObservableList(this.op1, this.op2)

    }

    private class ShortCircuitOrInvalidator(binding: BooleanOrBinding) : InvalidationListener {

        private val ref: WeakReference<BooleanOrBinding> = WeakReference(binding)

        override fun invalidated(observable: Observable) {
            val binding = this.ref.get()
            if (binding == null) {
                observable.removeListener(this)
            } else {
                // short circuit invalidation. This BooleanBinding becomes only invalid if the first operator changes or
                // the first parameter is false.
                if (binding.op1 == observable || (binding.valid && !binding.op1.get())) {
                    binding.invalidate()
                }
            }
        }

    }

    /**
     * Creates a [BooleanBinding] that calculates the conditional-OR operation on the value of two instance of
     * [ObservableBooleanValue].
     *
     * @param op1 first `ObservableBooleanValue`
     * @param op2 second `ObservableBooleanValue`
     *
     * @return the new `BooleanBinding`
     */
    fun or(op1: ObservableBooleanValue, op2: ObservableBooleanValue): BooleanBinding {
        return BooleanOrBinding(op1, op2)
    }

    /**
     * Creates a [BooleanBinding] that calculates the inverse of the value of a [ObservableBooleanValue].
     *
     * @param op the `ObservableBooleanValue`
     *
     * @return the new `BooleanBinding`
     */
    fun not(op: ObservableBooleanValue): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return !op.get()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableBooleanValue] are
     * equal.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableBooleanValue, op2: ObservableBooleanValue): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op1, op2)
            }

            override fun dispose() {
                super.unbind(op1, op2)
            }

            override fun computeValue(): Boolean {
                return op1.get() == op2.get()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op1, op2)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableBooleanValue] are
     * not equal.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableBooleanValue, op2: ObservableBooleanValue): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op1, op2)
            }

            override fun dispose() {
                super.unbind(op1, op2)
            }

            override fun computeValue(): Boolean {
                return op1.get() != op2.get()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op1, op2)

        }
    }

    // String
    // =================================================================================================================

    /**
     * Returns a [StringExpression] that wraps a [ObservableValue]. If the `ObservableValue` is already a
     * `StringExpression`, it will be returned. Otherwise a new [StringBinding] is created that holds the value of the
     * `ObservableValue` converted to a `String`.
     *
     * @param observableValue The source `ObservableValue`
     *
     * @return A `StringExpression` that wraps the `ObservableValue` if necessary
     */
    fun convert(observableValue: ObservableValue<*>): StringExpression {
        return StringFormatter.convert(observableValue)
    }

    /**
     * Returns a [StringExpression] that holds the value of the concatenation of multiple `Objects`.
     *
     * If one of the arguments implements [ObservableValue] and the value of this `ObservableValue` changes, the change
     * is automatically reflected in the `StringExpression`.
     *
     * If `null` or an empty array is passed to this method, a `StringExpression` that contains an empty `String` is
     * returned
     *
     * @param args the `Objects` that should be concatenated
     *
     * @return the new `StringExpression`
     */
    fun concat(vararg args: Any): StringExpression {
        return StringFormatter.concat(*args)
    }

    /**
     * Creates a [StringExpression] that holds the value of multiple `Objects` formatted according to a format `String`.
     *
     * If one of the arguments implements [ObservableValue] and the value of this `ObservableValue` changes, the change
     * is automatically reflected in the `StringExpression`.
     *
     * See [java.util.Formatter] for formatting rules.
     *
     * @param format the formatting `String`
     * @param args the `Objects` that should be inserted in the formatting `String`
     *
     * @return the new `StringExpression`
     */
    fun format(format: String, vararg args: Any): StringExpression {
        return StringFormatter.format(format, *args)
    }

    /**
     * Creates a [StringExpression] that holds the value of multiple `Objects` formatted according to a format `String`
     * and a specified `Locale`
     *
     * If one of the arguments implements [ObservableValue] and the value of this `ObservableValue` changes, the change
     * is automatically reflected in the `StringExpression`.
     *
     * See [java.util.Formatter] for formatting rules. See [Locale] for details on `Locale`.
     *
     * @param locale the `Locale` to use during formatting
     * @param format the formatting `String`
     * @param args the `Objects` that should be inserted in the formatting `String`
     *
     * @return the new `StringExpression`
     */
    fun format(locale: Locale, format: String, vararg args: Any): StringExpression {
        return StringFormatter.format(locale, format, *args)
    }

    private fun getStringSafe(value: String?): String {
        return value ?: ""
    }

    private fun equal(op1: ObservableStringValue, op2: ObservableStringValue,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())

        return object : BooleanBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Boolean {
                val s1: String = getStringSafe(op1.get())
                val s2: String = getStringSafe(op2.get())
                return s1 == s2
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0]) else
                    ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableStringValue] are
     * equal.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableStringValue, op2: ObservableStringValue): BooleanBinding {
        return equal(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is equal to a constant
     * value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the `ObservableStringValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableStringValue, op2: String?): BooleanBinding {
        return equal(op1, StringConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is equal to a constant
     * value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: String?, op2: ObservableStringValue): BooleanBinding {
        return equal(StringConstant.valueOf(op1), op2, op2)
    }

    private fun notEqual(op1: ObservableStringValue, op2: ObservableStringValue,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())

        return object : BooleanBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Boolean {
                val s1: String = getStringSafe(op1.get())
                val s2: String = getStringSafe(op2.get())
                return s1 != s2
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0]) else
                    ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableStringValue] are
     * not equal.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableStringValue, op2: ObservableStringValue): BooleanBinding {
        return notEqual(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is not equal to a
     * constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the `ObservableStringValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableStringValue, op2: String?): BooleanBinding {
        return notEqual(op1, StringConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is not equal to a
     * constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: String?, op2: ObservableStringValue): BooleanBinding {
        return notEqual(StringConstant.valueOf(op1), op2, op2)
    }

    private fun equalIgnoreCase(op1: ObservableStringValue, op2: ObservableStringValue,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())

        return object : BooleanBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Boolean {
                val s1: String = getStringSafe(op1.get())
                val s2: String = getStringSafe(op2.get())
                return s1.equals(s2, true)
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0]) else
                    ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableStringValue] are
     * equal ignoring case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun equalIgnoreCase(op1: ObservableStringValue, op2: ObservableStringValue): BooleanBinding {
        return equalIgnoreCase(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is equal to a constant
     * value ignoring case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the `ObservableStringValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun equalIgnoreCase(op1: ObservableStringValue, op2: String?): BooleanBinding {
        return equalIgnoreCase(op1, StringConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is equal to a constant
     * value ignoring case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun equalIgnoreCase(op1: String?, op2: ObservableStringValue): BooleanBinding {
        return equalIgnoreCase(StringConstant.valueOf(op1), op2, op2)
    }

    private fun notEqualIgnoreCase(op1: ObservableStringValue, op2: ObservableStringValue,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())

        return object : BooleanBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Boolean {
                val s1: String = getStringSafe(op1.get())
                val s2: String = getStringSafe(op2.get())
                return !s1.equals(s2, true)
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0]) else
                    ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableStringValue] are
     * not equal ignoring case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun notEqualIgnoreCase(op1: ObservableStringValue, op2: ObservableStringValue): BooleanBinding {
        return notEqualIgnoreCase(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is not equal to a
     * constant value ignoring case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the `ObservableStringValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun notEqualIgnoreCase(op1: ObservableStringValue, op2: String?): BooleanBinding {
        return notEqualIgnoreCase(op1, StringConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is not equal to a
     * constant value ignoring case.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun notEqualIgnoreCase(op1: String?, op2: ObservableStringValue): BooleanBinding {
        return notEqualIgnoreCase(StringConstant.valueOf(op1), op2, op2)
    }

    private fun greaterThan(op1: ObservableStringValue, op2: ObservableStringValue,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())

        return object : BooleanBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Boolean {
                val s1: String = getStringSafe(op1.get())
                val s2: String = getStringSafe(op2.get())
                return s1 > s2
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0]) else
                    ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of the first [ObservableStringValue] is greater
     * than the value of the second.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: ObservableStringValue, op2: ObservableStringValue): BooleanBinding {
        return greaterThan(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is greater than a
     * constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the `ObservableStringValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: ObservableStringValue, op2: String?): BooleanBinding {
        return greaterThan(op1, StringConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a constant value is greater than the value of a
     * [ObservableStringValue].
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: String?, op2: ObservableStringValue): BooleanBinding {
        return greaterThan(StringConstant.valueOf(op1), op2, op2)
    }

    private fun lessThan(op1: ObservableStringValue, op2: ObservableStringValue,
            vararg dependencies: Observable): BooleanBinding {
        return greaterThan(op2, op1, *dependencies)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of the first [ObservableStringValue] is less than
     * the value of the second.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: ObservableStringValue, op2: ObservableStringValue): BooleanBinding {
        return lessThan(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is less than a
     * constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the `ObservableStringValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: ObservableStringValue, op2: String?): BooleanBinding {
        return lessThan(op1, StringConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a constant value is less than the value of a
     * [ObservableStringValue].
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: String?, op2: ObservableStringValue): BooleanBinding {
        return lessThan(StringConstant.valueOf(op1), op2, op2)
    }

    private fun greaterThanOrEqual(op1: ObservableStringValue, op2: ObservableStringValue,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())

        return object : BooleanBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Boolean {
                val s1: String = getStringSafe(op1.get())
                val s2: String = getStringSafe(op2.get())
                return s1 >= s2
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0]) else
                    ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of the first [ObservableStringValue] is greater
     * than or equal to the value of the second.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: ObservableStringValue, op2: ObservableStringValue): BooleanBinding {
        return greaterThanOrEqual(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is greater than or
     * equal to a constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the `ObservableStringValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: ObservableStringValue, op2: String?): BooleanBinding {
        return greaterThanOrEqual(op1, StringConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a constant value is greater than or equal to the
     * value of a [ObservableStringValue].
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: String?, op2: ObservableStringValue): BooleanBinding {
        return greaterThanOrEqual(StringConstant.valueOf(op1), op2, op2)
    }

    private fun lessThanOrEqual(op1: ObservableStringValue, op2: ObservableStringValue,
            vararg dependencies: Observable): BooleanBinding {
        return greaterThanOrEqual(op2, op1, *dependencies)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of the first [ObservableStringValue] is less than
     * or equal to the value of the second.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: ObservableStringValue, op2: ObservableStringValue): BooleanBinding {
        return lessThan(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is less than or equal
     * to a constant value.
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the `ObservableStringValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: ObservableStringValue, op2: String?): BooleanBinding {
        return lessThanOrEqual(op1, StringConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a constant value is less than or equal to the
     * value of a [ObservableStringValue].
     *
     * Note: In this comparison a `String` that is `null` is considered equal to an empty `String`.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: String?, op2: ObservableStringValue): BooleanBinding {
        return lessThanOrEqual(StringConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [IntegerBinding] that holds the length of a `ObservableStringValue`.
     *
     * Note: In this comparison a `String` that is `null` is considered to have a length of `0`
     *
     * @param op the `ObservableStringValue`
     *
     * @return the new `IntegerBinding`
     *
     * @since JavaFX 8.0
     */
    fun length(op: ObservableStringValue): IntegerBinding {
        return object : IntegerBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Int {
                return getStringSafe(op.get()).length
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is empty.
     *
     * Note: In this comparison a `String` that is `null` is considered to be empty.
     *
     * @param op the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     *
     * @since JavaFX 8.0
     */
    fun isEmpty(op: ObservableStringValue): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return getStringSafe(op.get()).isEmpty()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableStringValue] is not empty.
     *
     * Note: In this comparison a `String` that is `null` is considered to be empty.
     *
     * @param op the `ObservableStringValue`
     *
     * @return the new `BooleanBinding`
     *
     * @since JavaFX 8.0
     */
    fun isNotEmpty(op: ObservableStringValue): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return getStringSafe(op.get()).isNotEmpty()
            }

            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    // Object
    // =================================================================================================================

    private fun equal(op1: ObservableObjectValue<*>, op2: ObservableObjectValue<*>,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())
        return object : BooleanBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Boolean {
                val obj1 = op1.get()
                val obj2 = op2.get()
                return obj1 == obj2
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() {
                    return if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)
                }

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableObjectValue] are
     * equal.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableObjectValue<*>, op2: ObservableObjectValue<*>): BooleanBinding {
        return equal(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of an [ObservableObjectValue] is equal to a
     * constant value.
     *
     * @param op1 the `ObservableCharacterValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableObjectValue<*>, op2: Any?): BooleanBinding {
        return equal(op1, ObjectConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of an [ObservableObjectValue] is equal to a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableCharacterValue`
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: Any?, op2: ObservableObjectValue<*>): BooleanBinding {
        return equal(ObjectConstant.valueOf(op1), op2, op2)
    }

    private fun notEqual(op1: ObservableObjectValue<*>, op2: ObservableObjectValue<*>,
            vararg dependencies: Observable): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Boolean {
                val obj1 = op1.get()
                val obj2 = op2.get()
                return obj1 != obj2
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() {
                    return if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)
                }

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableObjectValue] are
     * not equal.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableObjectValue<*>, op2: ObservableObjectValue<*>): BooleanBinding {
        return notEqual(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of an [ObservableObjectValue] is not equal to a
     * constant value.
     *
     * @param op1 the `ObservableObjectValue`
     * @param op2 the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableObjectValue<*>, op2: Any?): BooleanBinding {
        return notEqual(op1, ObjectConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of an [ObservableObjectValue] is not equal to a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableObjectValue`
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: Any, op2: ObservableObjectValue<*>): BooleanBinding {
        return notEqual(ObjectConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of an [ObservableObjectValue] is `null`.
     *
     * @param op the `ObservableObjectValue`
     *
     * @return the new `BooleanBinding`
     */
    fun isNull(op: ObservableObjectValue<*>): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.get() == null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of an [ObservableObjectValue] is not `null`.
     *
     * @param op the `ObservableObjectValue`
     *
     * @return the new `BooleanBinding`
     */
    fun isNotNull(op: ObservableObjectValue<*>): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.get() != null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

}