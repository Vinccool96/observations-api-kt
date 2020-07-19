package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.property.Property
import io.github.vinccool96.observationskt.beans.value.*
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.*
import io.github.vinccool96.observationskt.sun.collections.ImmutableObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection
import io.github.vinccool96.observationskt.util.StringConverter
import java.lang.ref.WeakReference
import java.text.Format
import java.util.*
import java.util.concurrent.Callable
import kotlin.math.abs

@Suppress("DuplicatedCode")
object Bindings {

    // =================================================================================================================
    // Helper functions to create custom bindings

    /**
     * Helper function to create a custom [BooleanBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun createBooleanBinding(func: Callable<Boolean>, vararg dependencies: Observable): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Boolean {
                return try {
                    func.call() ?: false
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    false
                }
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                else ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Helper function to create a custom [DoubleBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun createDoubleBinding(func: Callable<Double>, vararg dependencies: Observable): DoubleBinding {
        return object : DoubleBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Double {
                return try {
                    func.call() ?: 0.0
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    0.0
                }
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                else ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Helper function to create a custom [FloatBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun createFloatBinding(func: Callable<Float>, vararg dependencies: Observable): FloatBinding {
        return object : FloatBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Float {
                return try {
                    func.call()
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    0.0f
                }
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                else ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Helper function to create a custom [IntBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun createIntBinding(func: Callable<Int>, vararg dependencies: Observable): IntBinding {
        return object : IntBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Int {
                return try {
                    func.call()
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    0
                }
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                else ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Helper function to create a custom [LongBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun createLongBinding(func: Callable<Long>, vararg dependencies: Observable): LongBinding {
        return object : LongBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): Long {
                return try {
                    func.call()
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    0L
                }
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                else ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Helper function to create a custom [ObjectBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun <T> createObjectBinding(func: Callable<T>, default: T, vararg dependencies: Observable): ObjectBinding<T> {
        return object : ObjectBinding<T>() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): T {
                return try {
                    func.call()
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    default
                }
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                else ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Helper function to create a custom [StringBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun createStringBinding(func: Callable<String?>, vararg dependencies: Observable): StringBinding {
        return object : StringBinding() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): String? {
                return try {
                    func.call()
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    ""
                }
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                else ImmutableObservableList(*dependencies)

        }
    }

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
     * @param T the types of the properties
     * @param property1 the first `Property<T>`
     * @param property2 the second `Property<T>`
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
     * @param T the types of the properties
     * @param property1 the first `Property<T>`
     * @param property2 the second `Property<T>`
     *
     * @throws IllegalArgumentException
     *         if both properties are equal
     */
    fun <T> unbindBidirectional(property1: Property<T>, property2: Property<T>) {
        BidirectionalBinding.unbind(property1, property2)
    }

    /**
     * Delete a bidirectional binding that was previously defined with [bindBidirectional].
     *
     * @param property1 the first `Property<T>`
     * @param property2 the second `Property<T>`
     *
     * @throws IllegalArgumentException
     *         if both properties are equal
     */
    fun unbindBidirectional(property1: Any, property2: Any) {
        BidirectionalBinding.unbind(property1, property2)
    }

    /**
     * Generates a bidirectional binding (or "bind with inverse") between a `String`-[Property] and another `Property`
     * using the specified `Format` for conversion.
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
     * @param stringProperty the `String` `Property`
     * @param otherProperty the other (non-`String`) `Property`
     * @param format the `Format` used to convert between the properties
     * @param T the type of the wrapped value
     *
     * @throws IllegalArgumentException if both properties are equal
     */
    fun <T> bindBidirectional(stringProperty: Property<String?>, otherProperty: Property<T>, format: Format) {
        BidirectionalBinding.bind(stringProperty, otherProperty, format)
    }

    /**
     * Generates a bidirectional binding (or "bind with inverse") between a `String`-[Property] and another `Property`
     * using the specified [StringConverter] for conversion.
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
     * @param stringProperty the `String` `Property`
     * @param otherProperty the other (non-`String`) `Property`
     * @param converter the `StringConverter` used to convert between the properties
     * @param T the type of the wrapped value
     *
     * @throws IllegalArgumentException if both properties are equal
     */
    fun <T> bindBidirectional(stringProperty: Property<String?>, otherProperty: Property<T>,
            converter: StringConverter<T>) {
        BidirectionalBinding.bind(stringProperty, otherProperty, converter)
    }

    // Numbers
    // =================================================================================================================

    // =================================================================================================================
    // Negation

    /**
     * Creates a new [NumberBinding] that calculates the negation of a [ObservableNumberValue].
     *
     * @param value the operand
     *
     * @return the new `NumberBinding`
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
            else -> object : IntBinding() {

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
            else -> object : IntBinding() {

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

    /**
     * Creates a new [NumberBinding] that calculates the sum of the values of two instances of [ObservableNumberValue].
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `NumberBinding`
     */
    fun add(op1: ObservableNumberValue, op2: ObservableNumberValue): NumberBinding {
        return add(op1, op2, op1, op2)
    }

    /**
     * Creates a new [DoubleBinding] that calculates the sum of the value of a [ObservableNumberValue] and a constant
     * value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `DoubleBinding`
     */
    fun add(op1: ObservableNumberValue, op2: Double): DoubleBinding {
        return add(op1, DoubleConstant.valueOf(op2), op1) as DoubleBinding
    }

    /**
     * Creates a new [DoubleBinding] that calculates the sum of the value of a [ObservableNumberValue] and a constant
     * value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `DoubleBinding`
     */
    fun add(op1: Double, op2: ObservableNumberValue): DoubleBinding {
        return add(DoubleConstant.valueOf(op1), op2, op2) as DoubleBinding
    }

    /**
     * Creates a new [NumberBinding] that calculates the sum of the value of a [ObservableNumberValue] and a constant
     * value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun add(op1: ObservableNumberValue, op2: Float): NumberBinding {
        return add(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the sum of the value of a [ObservableNumberValue] and a constant
     * value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun add(op1: Float, op2: ObservableNumberValue): NumberBinding {
        return add(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the sum of the value of a [ObservableNumberValue] and a constant
     * value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun add(op1: ObservableNumberValue, op2: Long): NumberBinding {
        return add(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the sum of the value of a [ObservableNumberValue] and a constant
     * value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun add(op1: Long, op2: ObservableNumberValue): NumberBinding {
        return add(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the sum of the value of a [ObservableNumberValue] and a constant
     * value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun add(op1: ObservableNumberValue, op2: Int): NumberBinding {
        return add(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the sum of the value of a [ObservableNumberValue] and a constant
     * value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun add(op1: Int, op2: ObservableNumberValue): NumberBinding {
        return add(IntConstant.valueOf(op1), op2, op2)
    }

    // =================================================================================================================
    // Diff

    private fun subtract(op1: ObservableNumberValue, op2: ObservableNumberValue,
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
                    return op1.doubleValue - op2.doubleValue
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
                    return op1.floatValue - op2.floatValue
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
                    return op1.longValue - op2.longValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : IntBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Int {
                    return op1.intValue - op2.intValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }
    }

    /**
     * Creates a new [NumberBinding] that calculates the difference of the values of two instances of
     * [ObservableNumberValue].
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `NumberBinding`
     */
    fun subtract(op1: ObservableNumberValue, op2: ObservableNumberValue): NumberBinding {
        return subtract(op1, op2, op1, op2)
    }

    /**
     * Creates a new [DoubleBinding] that calculates the difference of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `DoubleBinding`
     */
    fun subtract(op1: ObservableNumberValue, op2: Double): DoubleBinding {
        return subtract(op1, DoubleConstant.valueOf(op2), op1) as DoubleBinding
    }

    /**
     * Creates a new [DoubleBinding] that calculates the difference of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `DoubleBinding`
     */
    fun subtract(op1: Double, op2: ObservableNumberValue): DoubleBinding {
        return subtract(DoubleConstant.valueOf(op1), op2, op2) as DoubleBinding
    }

    /**
     * Creates a new [NumberBinding] that calculates the difference of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun subtract(op1: ObservableNumberValue, op2: Float): NumberBinding {
        return subtract(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the difference of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun subtract(op1: Float, op2: ObservableNumberValue): NumberBinding {
        return subtract(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the difference of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun subtract(op1: ObservableNumberValue, op2: Long): NumberBinding {
        return subtract(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the difference of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun subtract(op1: Long, op2: ObservableNumberValue): NumberBinding {
        return subtract(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the difference of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun subtract(op1: ObservableNumberValue, op2: Int): NumberBinding {
        return subtract(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the difference of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun subtract(op1: Int, op2: ObservableNumberValue): NumberBinding {
        return subtract(IntConstant.valueOf(op1), op2, op2)
    }

    // =================================================================================================================
    // Multiply

    private fun multiply(op1: ObservableNumberValue, op2: ObservableNumberValue,
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
                    return op1.doubleValue * op2.doubleValue
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
                    return op1.floatValue * op2.floatValue
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
                    return op1.longValue * op2.longValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : IntBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Int {
                    return op1.intValue * op2.intValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }
    }

    /**
     * Creates a new [NumberBinding] that calculates the product of the values of two instances of
     * [ObservableNumberValue].
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `NumberBinding`
     */
    fun multiply(op1: ObservableNumberValue, op2: ObservableNumberValue): NumberBinding {
        return multiply(op1, op2, op1, op2)
    }

    /**
     * Creates a new [DoubleBinding] that calculates the product of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `DoubleBinding`
     */
    fun multiply(op1: ObservableNumberValue, op2: Double): DoubleBinding {
        return multiply(op1, DoubleConstant.valueOf(op2), op1) as DoubleBinding
    }

    /**
     * Creates a new [DoubleBinding] that calculates the product of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `DoubleBinding`
     */
    fun multiply(op1: Double, op2: ObservableNumberValue): DoubleBinding {
        return multiply(DoubleConstant.valueOf(op1), op2, op2) as DoubleBinding
    }

    /**
     * Creates a new [NumberBinding] that calculates the product of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun multiply(op1: ObservableNumberValue, op2: Float): NumberBinding {
        return multiply(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the product of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun multiply(op1: Float, op2: ObservableNumberValue): NumberBinding {
        return multiply(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the product of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun multiply(op1: ObservableNumberValue, op2: Long): NumberBinding {
        return multiply(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the product of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun multiply(op1: Long, op2: ObservableNumberValue): NumberBinding {
        return multiply(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the product of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun multiply(op1: ObservableNumberValue, op2: Int): NumberBinding {
        return multiply(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the product of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun multiply(op1: Int, op2: ObservableNumberValue): NumberBinding {
        return multiply(IntConstant.valueOf(op1), op2, op2)
    }

    // =================================================================================================================
    // Divide

    private fun divide(op1: ObservableNumberValue, op2: ObservableNumberValue,
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
                    return op1.doubleValue / op2.doubleValue
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
                    return op1.floatValue / op2.floatValue
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
                    return op1.longValue / op2.longValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : IntBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Int {
                    return op1.intValue / op2.intValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }
    }

    /**
     * Creates a new [NumberBinding] that calculates the division of the values of two instances of
     * [ObservableNumberValue].
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `NumberBinding`
     */
    fun divide(op1: ObservableNumberValue, op2: ObservableNumberValue): NumberBinding {
        return divide(op1, op2, op1, op2)
    }

    /**
     * Creates a new [DoubleBinding] that calculates the division of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `DoubleBinding`
     */
    fun divide(op1: ObservableNumberValue, op2: Double): DoubleBinding {
        return divide(op1, DoubleConstant.valueOf(op2), op1) as DoubleBinding
    }

    /**
     * Creates a new [DoubleBinding] that calculates the division of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `DoubleBinding`
     */
    fun divide(op1: Double, op2: ObservableNumberValue): DoubleBinding {
        return divide(DoubleConstant.valueOf(op1), op2, op2) as DoubleBinding
    }

    /**
     * Creates a new [NumberBinding] that calculates the division of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun divide(op1: ObservableNumberValue, op2: Float): NumberBinding {
        return divide(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the division of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun divide(op1: Float, op2: ObservableNumberValue): NumberBinding {
        return divide(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the division of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun divide(op1: ObservableNumberValue, op2: Long): NumberBinding {
        return divide(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the division of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun divide(op1: Long, op2: ObservableNumberValue): NumberBinding {
        return divide(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the division of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     *
     * @return the new `NumberBinding`
     */
    fun divide(op1: ObservableNumberValue, op2: Int): NumberBinding {
        return divide(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the division of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun divide(op1: Int, op2: ObservableNumberValue): NumberBinding {
        return divide(IntConstant.valueOf(op1), op2, op2)
    }

    // =================================================================================================================
    // Equals

    private fun equal(op1: ObservableNumberValue, op2: ObservableNumberValue, epsilon: Double,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())
        return when {
            op1 is ObservableDoubleValue || op2 is ObservableDoubleValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return abs(op1.doubleValue - op2.doubleValue) <= epsilon
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableFloatValue || op2 is ObservableFloatValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return abs(op1.floatValue - op2.floatValue) <= epsilon
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableLongValue || op2 is ObservableLongValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return abs(op1.longValue - op2.longValue) <= epsilon
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return abs(op1.intValue - op2.intValue) <= epsilon
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableNumberValue] are
     * equal (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableNumberValue, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return equal(op1, op2, epsilon, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableNumberValue] are
     * equal.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableNumberValue, op2: ObservableNumberValue): BooleanBinding {
        return equal(op1, op2, 0.0, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableNumberValue, op2: Double, epsilon: Double): BooleanBinding {
        return equal(op1, DoubleConstant.valueOf(op2), epsilon, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: Double, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return equal(DoubleConstant.valueOf(op1), op2, epsilon, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableNumberValue, op2: Float, epsilon: Double): BooleanBinding {
        return equal(op1, FloatConstant.valueOf(op2), epsilon, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: Float, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return equal(FloatConstant.valueOf(op1), op2, epsilon, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableNumberValue, op2: Long, epsilon: Double): BooleanBinding {
        return equal(op1, LongConstant.valueOf(op2), epsilon, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     * @param epsilon the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: Long, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return equal(LongConstant.valueOf(op1), op2, epsilon, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableNumberValue, op2: Long): BooleanBinding {
        return equal(op1, LongConstant.valueOf(op2), 0.0, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: Long, op2: ObservableNumberValue): BooleanBinding {
        return equal(LongConstant.valueOf(op1), op2, 0.0, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableNumberValue, op2: Int, epsilon: Double): BooleanBinding {
        return equal(op1, IntConstant.valueOf(op2), epsilon, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: Int, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return equal(IntConstant.valueOf(op1), op2, epsilon, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: ObservableNumberValue, op2: Int): BooleanBinding {
        return equal(op1, IntConstant.valueOf(op2), 0.0, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is equal to a constant
     * value.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun equal(op1: Int, op2: ObservableNumberValue): BooleanBinding {
        return equal(IntConstant.valueOf(op1), op2, 0.0, op2)
    }

    // =================================================================================================================
    // Not Equal

    private fun notEqual(op1: ObservableNumberValue, op2: ObservableNumberValue, epsilon: Double,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())
        return when {
            op1 is ObservableDoubleValue || op2 is ObservableDoubleValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return abs(op1.doubleValue - op2.doubleValue) > epsilon
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableFloatValue || op2 is ObservableFloatValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return abs(op1.floatValue - op2.floatValue) > epsilon
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableLongValue || op2 is ObservableLongValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return abs(op1.longValue - op2.longValue) > epsilon
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return abs(op1.intValue - op2.intValue) > epsilon
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableNumberValue] are
     * not equal (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the first operand
     * @param op2
     *         the second operand
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableNumberValue, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return notEqual(op1, op2, epsilon, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the values of two instances of [ObservableNumberValue] are
     * not equal.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the first operand
     * @param op2
     *         the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableNumberValue, op2: ObservableNumberValue): BooleanBinding {
        return notEqual(op1, op2, 0.0, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableNumberValue, op2: Double, epsilon: Double): BooleanBinding {
        return notEqual(op1, DoubleConstant.valueOf(op2), epsilon, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: Double, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return notEqual(DoubleConstant.valueOf(op1), op2, epsilon, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableNumberValue, op2: Float, epsilon: Double): BooleanBinding {
        return notEqual(op1, FloatConstant.valueOf(op2), epsilon, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: Float, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return notEqual(FloatConstant.valueOf(op1), op2, epsilon, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableNumberValue, op2: Long, epsilon: Double): BooleanBinding {
        return notEqual(op1, LongConstant.valueOf(op2), epsilon, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: Long, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return notEqual(LongConstant.valueOf(op1), op2, epsilon, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableNumberValue, op2: Long): BooleanBinding {
        return notEqual(op1, LongConstant.valueOf(op2), 0.0, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: Long, op2: ObservableNumberValue): BooleanBinding {
        return notEqual(LongConstant.valueOf(op1), op2, 0.0, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableNumberValue, op2: Int, epsilon: Double): BooleanBinding {
        return notEqual(op1, IntConstant.valueOf(op2), epsilon, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value (with a tolerance).
     *
     * Two operands `a` and `b` are considered equal if `abs(a-b) <= epsilon`.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     * @param epsilon
     *         the permitted tolerance
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: Int, op2: ObservableNumberValue, epsilon: Double): BooleanBinding {
        return notEqual(IntConstant.valueOf(op1), op2, epsilon, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: ObservableNumberValue, op2: Int): BooleanBinding {
        return notEqual(op1, IntConstant.valueOf(op2), 0.0, op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is not equal to a
     * constant value.
     *
     * Allowing a small tolerance is recommended when comparing floating-point numbers because of rounding-errors.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun notEqual(op1: Int, op2: ObservableNumberValue): BooleanBinding {
        return notEqual(IntConstant.valueOf(op1), op2, 0.0, op2)
    }

    // =================================================================================================================
    // Greater Than

    private fun greaterThan(op1: ObservableNumberValue, op2: ObservableNumberValue,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())
        return when {
            op1 is ObservableDoubleValue || op2 is ObservableDoubleValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return op1.doubleValue > op2.doubleValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableFloatValue || op2 is ObservableFloatValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return op1.floatValue > op2.floatValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableLongValue || op2 is ObservableLongValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return op1.longValue > op2.longValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return op1.intValue > op2.intValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of the first [ObservableNumberValue] is greater
     * than the value of the second.
     *
     * @param op1
     *         the first operand
     * @param op2
     *         the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: ObservableNumberValue, op2: ObservableNumberValue): BooleanBinding {
        return greaterThan(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is greater than a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: ObservableNumberValue, op2: Double): BooleanBinding {
        return greaterThan(op1, DoubleConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is greater than the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: Double, op2: ObservableNumberValue): BooleanBinding {
        return greaterThan(DoubleConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is greater than a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: ObservableNumberValue, op2: Float): BooleanBinding {
        return greaterThan(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is greater than the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: Float, op2: ObservableNumberValue): BooleanBinding {
        return greaterThan(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is greater than a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: ObservableNumberValue, op2: Long): BooleanBinding {
        return greaterThan(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is greater than the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: Long, op2: ObservableNumberValue): BooleanBinding {
        return greaterThan(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is greater than a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: ObservableNumberValue, op2: Int): BooleanBinding {
        return greaterThan(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is greater than the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThan(op1: Int, op2: ObservableNumberValue): BooleanBinding {
        return greaterThan(IntConstant.valueOf(op1), op2, op2)
    }

    // =================================================================================================================
    // Less Than

    private fun lessThan(op1: ObservableNumberValue, op2: ObservableNumberValue,
            vararg dependencies: Observable): BooleanBinding {
        return greaterThan(op2, op1, *dependencies)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of the first [ObservableNumberValue] is less than
     * the value of the second.
     *
     * @param op1
     *         the first operand
     * @param op2
     *         the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: ObservableNumberValue, op2: ObservableNumberValue): BooleanBinding {
        return lessThan(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is less than a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: ObservableNumberValue, op2: Double): BooleanBinding {
        return lessThan(op1, DoubleConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is less than the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: Double, op2: ObservableNumberValue): BooleanBinding {
        return lessThan(DoubleConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is less than a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: ObservableNumberValue, op2: Float): BooleanBinding {
        return lessThan(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is less than the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: Float, op2: ObservableNumberValue): BooleanBinding {
        return lessThan(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is less than a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: ObservableNumberValue, op2: Long): BooleanBinding {
        return lessThan(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is less than the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: Long, op2: ObservableNumberValue): BooleanBinding {
        return lessThan(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is less than a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: ObservableNumberValue, op2: Int): BooleanBinding {
        return lessThan(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is less than the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThan(op1: Int, op2: ObservableNumberValue): BooleanBinding {
        return lessThan(IntConstant.valueOf(op1), op2, op2)
    }

    // =================================================================================================================
    // Greater Than or Equal

    private fun greaterThanOrEqual(op1: ObservableNumberValue, op2: ObservableNumberValue,
            vararg dependencies: Observable): BooleanBinding {
        require(dependencies.isNotEmpty())
        return when {
            op1 is ObservableDoubleValue || op2 is ObservableDoubleValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return op1.doubleValue >= op2.doubleValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableFloatValue || op2 is ObservableFloatValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return op1.floatValue >= op2.floatValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            op1 is ObservableLongValue || op2 is ObservableLongValue -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return op1.longValue >= op2.longValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : BooleanBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Boolean {
                    return op1.intValue >= op2.intValue
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of the first [ObservableNumberValue] is greater
     * than or equal to the value of the second.
     *
     * @param op1
     *         the first operand
     * @param op2
     *         the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: ObservableNumberValue, op2: ObservableNumberValue): BooleanBinding {
        return greaterThanOrEqual(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is greater than or
     * equal to a constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: ObservableNumberValue, op2: Double): BooleanBinding {
        return greaterThanOrEqual(op1, DoubleConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is greater than or equal to the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: Double, op2: ObservableNumberValue): BooleanBinding {
        return greaterThanOrEqual(DoubleConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is greater than or
     * equal to a constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: ObservableNumberValue, op2: Float): BooleanBinding {
        return greaterThanOrEqual(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is greater than or equal to the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: Float, op2: ObservableNumberValue): BooleanBinding {
        return greaterThanOrEqual(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is greater than or
     * equal to a constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: ObservableNumberValue, op2: Long): BooleanBinding {
        return greaterThanOrEqual(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is greater than or equal to the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: Long, op2: ObservableNumberValue): BooleanBinding {
        return greaterThanOrEqual(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is greater than or
     * equal to a constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: ObservableNumberValue, op2: Int): BooleanBinding {
        return greaterThanOrEqual(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is greater than or equal to the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun greaterThanOrEqual(op1: Int, op2: ObservableNumberValue): BooleanBinding {
        return greaterThanOrEqual(IntConstant.valueOf(op1), op2, op2)
    }

    // =================================================================================================================
    // Less Than or Equal

    private fun lessThanOrEqual(op1: ObservableNumberValue, op2: ObservableNumberValue,
            vararg dependencies: Observable): BooleanBinding {
        return greaterThanOrEqual(op2, op1, *dependencies)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of the first [ObservableNumberValue] is less than
     * or equal to the value of the second.
     *
     * @param op1
     *         the first operand
     * @param op2
     *         the second operand
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: ObservableNumberValue, op2: ObservableNumberValue): BooleanBinding {
        return lessThanOrEqual(op1, op2, op1, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is less than or equal
     * to a constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: ObservableNumberValue, op2: Double): BooleanBinding {
        return lessThanOrEqual(op1, DoubleConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is less than or equal to the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: Double, op2: ObservableNumberValue): BooleanBinding {
        return lessThanOrEqual(DoubleConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is less than or equal
     * to a constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: ObservableNumberValue, op2: Float): BooleanBinding {
        return lessThanOrEqual(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is less than or equal to the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: Float, op2: ObservableNumberValue): BooleanBinding {
        return lessThanOrEqual(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is less than or equal
     * to a constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: ObservableNumberValue, op2: Long): BooleanBinding {
        return lessThanOrEqual(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is less than or equal to the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: Long, op2: ObservableNumberValue): BooleanBinding {
        return lessThanOrEqual(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if the value of a [ObservableNumberValue] is less than or equal
     * to a constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: ObservableNumberValue, op2: Int): BooleanBinding {
        return lessThanOrEqual(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a constant value is less than or equal to the value of a
     * [ObservableNumberValue].
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `BooleanBinding`
     */
    fun lessThanOrEqual(op1: Int, op2: ObservableNumberValue): BooleanBinding {
        return lessThanOrEqual(IntConstant.valueOf(op1), op2, op2)
    }

    // =================================================================================================================
    // Minimum

    private fun min(op1: ObservableNumberValue, op2: ObservableNumberValue,
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
                    return kotlin.math.min(op1.doubleValue, op2.doubleValue)
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
                    return kotlin.math.min(op1.floatValue, op2.floatValue)
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
                    return kotlin.math.min(op1.longValue, op2.longValue)
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : IntBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Int {
                    return kotlin.math.min(op1.intValue, op2.intValue)
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }

    }

    /**
     * Creates a new [NumberBinding] that calculates the minimum of the values of two instances of
     * [ObservableNumberValue].
     *
     * @param op1
     *         the first operand
     * @param op2
     *         the second operand
     *
     * @return the new `NumberBinding`
     */
    fun min(op1: ObservableNumberValue, op2: ObservableNumberValue): NumberBinding {
        return min(op1, op2, op1, op2)
    }

    /**
     * Creates a new [DoubleBinding] that calculates the minimum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `DoubleBinding`
     */
    fun min(op1: ObservableNumberValue, op2: Double): DoubleBinding {
        return min(op1, DoubleConstant.valueOf(op2), op1) as DoubleBinding
    }

    /**
     * Creates a new [DoubleBinding] that calculates the minimum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `DoubleBinding`
     */
    fun min(op1: Double, op2: ObservableNumberValue): DoubleBinding {
        return min(DoubleConstant.valueOf(op1), op2, op2) as DoubleBinding
    }

    /**
     * Creates a new [NumberBinding] that calculates the minimum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `NumberBinding`
     */
    fun min(op1: ObservableNumberValue, op2: Float): NumberBinding {
        return min(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the minimum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun min(op1: Float, op2: ObservableNumberValue): NumberBinding {
        return min(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the minimum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `NumberBinding`
     */
    fun min(op1: ObservableNumberValue, op2: Long): NumberBinding {
        return min(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the minimum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun min(op1: Long, op2: ObservableNumberValue): NumberBinding {
        return min(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the minimum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `NumberBinding`
     */
    fun min(op1: ObservableNumberValue, op2: Int): NumberBinding {
        return min(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the minimum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun min(op1: Int, op2: ObservableNumberValue): NumberBinding {
        return min(IntConstant.valueOf(op1), op2, op2)
    }

    // =================================================================================================================
    // Maximum

    private fun max(op1: ObservableNumberValue, op2: ObservableNumberValue,
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
                    return kotlin.math.max(op1.doubleValue, op2.doubleValue)
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
                    return kotlin.math.max(op1.floatValue, op2.floatValue)
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
                    return kotlin.math.max(op1.longValue, op2.longValue)
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
            else -> object : IntBinding() {

                init {
                    super.bind(*dependencies)
                }

                override fun dispose() {
                    super.unbind(*dependencies)
                }

                override fun computeValue(): Int {
                    return kotlin.math.max(op1.intValue, op2.intValue)
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                    else ImmutableObservableList(*dependencies)

            }
        }

    }

    /**
     * Creates a new [NumberBinding] that calculates the maximum of the values of two instances of
     * [ObservableNumberValue].
     *
     * @param op1
     *         the first operand
     * @param op2
     *         the second operand
     *
     * @return the new `NumberBinding`
     */
    fun max(op1: ObservableNumberValue, op2: ObservableNumberValue): NumberBinding {
        return max(op1, op2, op1, op2)
    }

    /**
     * Creates a new [DoubleBinding] that calculates the maximum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `DoubleBinding`
     */
    fun max(op1: ObservableNumberValue, op2: Double): DoubleBinding {
        return max(op1, DoubleConstant.valueOf(op2), op1) as DoubleBinding
    }

    /**
     * Creates a new [DoubleBinding] that calculates the maximum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `DoubleBinding`
     */
    fun max(op1: Double, op2: ObservableNumberValue): DoubleBinding {
        return max(DoubleConstant.valueOf(op1), op2, op2) as DoubleBinding
    }

    /**
     * Creates a new [NumberBinding] that calculates the maximum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `NumberBinding`
     */
    fun max(op1: ObservableNumberValue, op2: Float): NumberBinding {
        return max(op1, FloatConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the maximum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun max(op1: Float, op2: ObservableNumberValue): NumberBinding {
        return max(FloatConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the maximum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `NumberBinding`
     */
    fun max(op1: ObservableNumberValue, op2: Long): NumberBinding {
        return max(op1, LongConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the maximum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun max(op1: Long, op2: ObservableNumberValue): NumberBinding {
        return max(LongConstant.valueOf(op1), op2, op2)
    }

    /**
     * Creates a new [NumberBinding] that calculates the maximum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the `ObservableNumberValue`
     * @param op2
     *         the constant value
     *
     * @return the new `NumberBinding`
     */
    fun max(op1: ObservableNumberValue, op2: Int): NumberBinding {
        return max(op1, IntConstant.valueOf(op2), op1)
    }

    /**
     * Creates a new [NumberBinding] that calculates the maximum of the value of a [ObservableNumberValue] and a
     * constant value.
     *
     * @param op1
     *         the constant value
     * @param op2
     *         the `ObservableNumberValue`
     *
     * @return the new `NumberBinding`
     */
    fun max(op1: Int, op2: ObservableNumberValue): NumberBinding {
        return max(IntConstant.valueOf(op1), op2, op2)
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
    fun concat(vararg args: Any?): StringExpression {
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
        return lessThanOrEqual(op1, op2, op1, op2)
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
     * Creates a new [IntBinding] that holds the length of a `ObservableStringValue`.
     *
     * Note: In this comparison a `String` that is `null` is considered to have a length of `0`
     *
     * @param op the `ObservableStringValue`
     *
     * @return the new `IntBinding`
     */
    fun length(op: ObservableStringValue): IntBinding {
        return object : IntBinding() {

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