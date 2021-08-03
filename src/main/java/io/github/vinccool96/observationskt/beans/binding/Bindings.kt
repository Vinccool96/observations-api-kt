package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.property.Property
import io.github.vinccool96.observationskt.beans.value.*
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.sun.binding.*
import io.github.vinccool96.observationskt.sun.collections.ImmutableObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection
import io.github.vinccool96.observationskt.util.StringConverter
import java.lang.ref.WeakReference
import java.text.Format
import java.util.*
import java.util.concurrent.Callable
import kotlin.math.abs

@Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate")
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
     * @param default the default value
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

    /**
     * Helper function to create a custom [ListBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun <E> createListBinding(func: Callable<ObservableList<E>?>, vararg dependencies: Observable): ListBinding<E> {
        return object : ListBinding<E>() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): ObservableList<E>? {
                return try {
                    func.call()
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    null
                }
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                else ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Helper function to create a custom [MapBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun <K, V> createMapBinding(func: Callable<ObservableMap<K, V>?>, vararg dependencies: Observable):
            MapBinding<K, V> {
        return object : MapBinding<K, V>() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): ObservableMap<K, V>? {
                return try {
                    func.call()
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    null
                }
            }

            override val dependencies: ObservableList<*>
                get() = if (dependencies.size == 1) ObservableCollections.singletonObservableList(dependencies[0])
                else ImmutableObservableList(*dependencies)

        }
    }

    /**
     * Helper function to create a custom [SetBinding].
     *
     * @param func The function that calculates the value of this binding
     * @param dependencies The dependencies of this binding
     *
     * @return The generated binding
     */
    fun <E> createSetBinding(func: Callable<ObservableSet<E>?>, vararg dependencies: Observable): SetBinding<E> {
        return object : SetBinding<E>() {

            init {
                super.bind(*dependencies)
            }

            override fun dispose() {
                super.unbind(*dependencies)
            }

            override fun computeValue(): ObservableSet<E>? {
                return try {
                    func.call()
                } catch (e: Exception) {
                    Logging.getLogger().warning("Exception while evaluating binding", e)
                    null
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
     * @throws IllegalArgumentException if both properties are equal
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
     * @throws IllegalArgumentException if both properties are equal
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
     * @throws IllegalArgumentException if both properties are equal
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

    /**
     * Generates a bidirectional binding (or "bind with inverse") between two instances of [ObservableList].
     *
     * A bidirectional binding is a binding that works in both directions. If two properties `a` and `b` are linked with
     * a bidirectional binding and the value of `a` changes, `b` is set to the same value automatically. And vice versa,
     * if `b` changes, `a` is set to the same value.
     *
     * Only the content of the two lists is synchronized, which means that both lists are different, but they contain
     * the same elements.
     *
     * A bidirectional content-binding can be removed with [unbindContentBidirectional].
     *
     * Note: this implementation of a bidirectional binding behaves differently from all other bindings here in two
     * important aspects. A property that is linked to another property with a bidirectional binding can still be set
     * (usually bindings would throw an exception). Secondly bidirectional bindings are calculated eagerly, i.e. a bound
     * property is updated immediately.
     *
     * @param E the type of the list elements
     * @param list1 the first `ObservableList<E>`
     * @param list2 the second `ObservableList<E>`
     *
     * @throws IllegalArgumentException if `list1 === list2`
     */
    fun <E> bindContentBidirectional(list1: ObservableList<E>, list2: ObservableList<E>) {
        BidirectionalContentBinding.bind(list1, list2)
    }

    /**
     * Generates a bidirectional binding (or "bind with inverse") between two instances of [ObservableSet].
     *
     * A bidirectional binding is a binding that works in both directions. If two properties `a` and `b` are linked with
     * a bidirectional binding and the value of `a` changes, `b` is set to the same value automatically. And vice versa,
     * if `b` changes, `a` is set to the same value.
     *
     * Only the content of the two sets is synchronized, which means that both sets are different, but they contain the
     * same elements.
     *
     * A bidirectional content-binding can be removed with [unbindContentBidirectional].
     *
     * Note: this implementation of a bidirectional binding behaves differently from all other bindings here in two
     * important aspects. A property that is linked to another property with a bidirectional binding can still be set
     * (usually bindings would throw an exception). Secondly bidirectional bindings are calculated eagerly, i.e. a bound
     * property is updated immediately.
     *
     * @param E the type of the set elements
     * @param set1 the first `ObservableSet<E>`
     * @param set2 the second `ObservableSet<E>`
     *
     * @throws IllegalArgumentException if `set1 === set2`
     */
    fun <E> bindContentBidirectional(set1: ObservableSet<E>, set2: ObservableSet<E>) {
        BidirectionalContentBinding.bind(set1, set2)
    }

    /**
     * Generates a bidirectional binding (or "bind with inverse") between two instances of [ObservableMap].
     *
     * A bidirectional binding is a binding that works in both directions. If two properties `a` and `b` are linked with
     * a bidirectional binding and the value of `a` changes, `b` is set to the same value automatically. And vice versa,
     * if `b` changes, `a` is set to the same value.
     *
     * Only the content of the two maps is synchronized, which means that both maps are different, but they contain the
     * same elements.
     *
     * A bidirectional content-binding can be removed with [unbindContentBidirectional].
     *
     * Note: this implementation of a bidirectional binding behaves differently from all other bindings here in two
     * important aspects. A property that is linked to another property with a bidirectional binding can still be set
     * (usually bindings would throw an exception). Secondly bidirectional bindings are calculated eagerly, i.e. a bound
     * property is updated immediately.
     *
     * @param K the type of the key elements
     * @param V the type of the value elements
     * @param map1 the first `ObservableMap<K, V>`
     * @param map2 the second `ObservableMap<K, V>`
     *
     * @throws IllegalArgumentException if `map1 === map2`
     */
    fun <K, V> bindContentBidirectional(map1: ObservableMap<K, V>, map2: ObservableMap<K, V>) {
        BidirectionalContentBinding.bind(map1, map2)
    }

    /**
     * Remove a bidirectional content binding.
     *
     * @param obj1 the first `Object`
     * @param obj2 the second `Object`
     */
    fun unbindContentBidirectional(obj1: Any, obj2: Any) {
        BidirectionalContentBinding.unbind(obj1, obj2)
    }

    /**
     * Generates a content binding between an [ObservableList] and a [MutableList].
     *
     * A content binding ensures that the `MutableList` contains the same elements as the `ObservableList`. If the
     * content of the `ObservableList` changes, the `MutableList` will be updated automatically.
     *
     * Once a `MutableList` is bound to an `ObservableList`, the `MutableList` **must not** be changed directly anymore.
     * Doing so would lead to unexpected results.
     *
     * A content-binding can be removed with [unbindContent].
     *
     * @param E the type of the `MutableList` elements
     * @param list1 the `MutableList`
     * @param list2 the `ObservableList`
     */
    fun <E> bindContent(list1: MutableList<E>, list2: ObservableList<out E>) {
        ContentBinding.bind(list1, list2)
    }

    /**
     * Generates a content binding between an [ObservableSet] and a [MutableSet].
     *
     * A content binding ensures that the `MutableSet` contains the same elements as the `ObservableSet`. If the content
     * of the `ObservableSet` changes, the `MutableSet` will be updated automatically.
     *
     * Once a `MutableSet` is bound to an `ObservableSet`, the `MutableSet` must not be changed directly anymore. Doing
     * so would lead to unexpected results.
     *
     * A content-binding can be removed with [unbindContent].
     *
     * @param E the type of the `MutableSet` elements
     * @param set1 the `MutableSet`
     * @param set2 the `ObservableSet`
     *
     * @throws IllegalArgumentException if `set1 == set2`
     */
    fun <E> bindContent(set1: MutableSet<E>, set2: ObservableSet<out E>) {
        ContentBinding.bind(set1, set2)
    }

    /**
     * Generates a content binding between an [ObservableMap] and a [MutableMap].
     *
     * A content binding ensures that the `MutableMap` contains the same elements as the `ObservableMap`. If the content
     * of the `ObservableMap` changes, the `MutableMap` will be updated automatically.
     *
     * Once a `MutableMap` is bound to an `ObservableMap`, the `MutableMap` **must not** be changed directly anymore.
     * Doing so would lead to unexpected results.
     *
     * A content-binding can be removed with [unbindContent].
     *
     * @param K the type of the key elements of the `MutableMap`
     * @param V the type of the value elements of the `MutableMap`
     * @param map1 the `MutableMap`
     * @param map2 the `ObservableMap`
     *
     * @throws IllegalArgumentException if `map1 == map2`
     */
    fun <K, V> bindContent(map1: MutableMap<K, V>, map2: ObservableMap<K, V>) {
        ContentBinding.bind(map1, map2)
    }

    /**
     * Remove a content binding.
     *
     * @param obj1 the first `Object`
     * @param obj2 the second `Object`
     *
     * @throws IllegalArgumentException if `obj1 === obj2`
     */
    fun unbindContent(obj1: Any, obj2: Any) {
        ContentBinding.unbind(obj1, obj2)
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     * @param epsilon the permitted tolerance
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     * @param epsilon the permitted tolerance
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the first operand
     * @param op2 the second operand
     * @param epsilon the permitted tolerance
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
     * @param op1 the first operand
     * @param op2 the second operand
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     * @param epsilon the permitted tolerance
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     * @param epsilon the permitted tolerance
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     * @param epsilon the permitted tolerance
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     * @param epsilon the permitted tolerance
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     * @param epsilon the permitted tolerance
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     * @param epsilon the permitted tolerance
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
     * @param epsilon the permitted tolerance
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
     * @param epsilon the permitted tolerance
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the first operand
     * @param op2 the second operand
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the first operand
     * @param op2 the second operand
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the first operand
     * @param op2 the second operand
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the first operand
     * @param op2 the second operand
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the first operand
     * @param op2 the second operand
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the first operand
     * @param op2 the second operand
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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
     * @param op1 the constant value
     * @param op2 the `ObservableNumberValue`
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
     * @param op1 the `ObservableNumberValue`
     * @param op2 the constant value
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

    private class BooleanAndBinding(val op1: ObservableBooleanValue, private val op2: ObservableBooleanValue) :
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

    private class BooleanOrBinding(val op1: ObservableBooleanValue, private val op2: ObservableBooleanValue) :
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
     * `StringExpression`, it will be returned. Otherwise, a new [StringBinding] is created that holds the value of the
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

    // List
    // =================================================================================================================

    /**
     * Creates a new [IntBinding] that contains the size of an [ObservableList].
     *
     * @param op the `ObservableList`
     * @param E type of the `List` elements
     *
     * @return the new `IntBinding`
     */
    fun <E> size(op: ObservableList<E>): IntBinding {
        return object : IntBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Int {
                return op.size
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a given [ObservableList] is empty.
     *
     * @param op the `ObservableList`
     * @param E type of the `List` elements
     *
     * @return the new `BooleanBinding`
     */
    fun <E> isEmpty(op: ObservableList<E>): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.isEmpty()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a given [ObservableList] is not empty.
     *
     * @param op the `ObservableList`
     * @param E type of the `List` elements
     *
     * @return the new `BooleanBinding`
     */
    fun <E> isNotEmpty(op: ObservableList<E>): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.isNotEmpty()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [ObjectBinding] that contains the element of an [ObservableList] at the specified position. The
     * `ObjectBinding` will contain `null`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     * @param E the type of the `List` elements
     *
     * @return the new `ObjectBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun <E> valueAt(op: ObservableList<E>, index: Int): ObjectBinding<E?> {
        require(index >= 0) { "Index cannot be negative" }

        return object : ObjectBinding<E?>() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): E? {
                try {
                    return op[index]
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [ObjectBinding] that contains the element of an [ObservableList] at the specified position. The
     * `ObjectBinding` will contain `null`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     * @param E the type of the `List` elements
     *
     * @return the new `ObjectBinding`
     *
     * @throws IllegalArgumentException if `index.intValue < 0`
     */
    fun <E> valueAt(op: ObservableList<E>, index: ObservableIntValue): ObjectBinding<E?> {
        return valueAt(op, index as ObservableNumberValue)
    }

    /**
     * Creates a new [ObjectBinding] that contains the element of an [ObservableList] at the specified position. The
     * `ObjectBinding` will contain `null`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`, converted to int
     * @param E the type of the `List` elements
     *
     * @return the new `ObjectBinding`
     *
     * @throws IllegalArgumentException if `index.intValue < 0`
     */
    fun <E> valueAt(op: ObservableList<E>, index: ObservableNumberValue): ObjectBinding<E?> {
        return object : ObjectBinding<E?>() {

            init {
                super.bind(op, index)
            }

            override fun dispose() {
                super.unbind(op, index)
            }

            override fun computeValue(): E? {
                try {
                    return op[index.intValue]
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, index)

        }
    }

    /**
     * Creates a new [BooleanBinding] that contains the element of an [ObservableList] at the specified position. The
     * `BooleanBinding` will hold `false`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `BooleanBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun booleanValueAt(op: ObservableList<Boolean?>, index: Int): BooleanBinding {
        require(index >= 0) { "Index cannot be negative" }

        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                try {
                    val value = op[index]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return false
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that contains the element of an [ObservableList] at the specified position. The
     * `BooleanBinding` will hold `false`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `BooleanBinding`
     */
    fun booleanValueAt(op: ObservableList<Boolean?>, index: ObservableIntValue): BooleanBinding {
        return booleanValueAt(op, index as ObservableNumberValue)
    }

    /**
     * Creates a new [BooleanBinding] that contains the element of an [ObservableList] at the specified position. The
     * `BooleanBinding` will hold `false`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `BooleanBinding`
     */
    fun booleanValueAt(op: ObservableList<Boolean?>, index: ObservableNumberValue): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op, index)
            }

            override fun dispose() {
                super.unbind(op, index)
            }

            override fun computeValue(): Boolean {
                try {
                    val value = op[index.intValue]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return false
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, index)

        }
    }

    /**
     * Creates a new [DoubleBinding] that contains the element of an [ObservableList] at the specified position. The
     * `DoubleBinding` will hold `0.0`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `DoubleBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun doubleValueAt(op: ObservableList<out Number?>, index: Int): DoubleBinding {
        require(index >= 0) { "Index cannot be negative" }

        return object : DoubleBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Double {
                try {
                    val value = op[index]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value.toDouble()
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return 0.0
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [DoubleBinding] that contains the element of an [ObservableList] at the specified position. The
     * `DoubleBinding` will hold `0.0`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `DoubleBinding`
     */
    fun doubleValueAt(op: ObservableList<out Number?>, index: ObservableIntValue): DoubleBinding {
        return doubleValueAt(op, index as ObservableNumberValue)
    }

    /**
     * Creates a new [DoubleBinding] that contains the element of an [ObservableList] at the specified position. The
     * `DoubleBinding` will hold `0.0`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `DoubleBinding`
     */
    fun doubleValueAt(op: ObservableList<out Number?>, index: ObservableNumberValue): DoubleBinding {
        return object : DoubleBinding() {

            init {
                super.bind(op, index)
            }

            override fun dispose() {
                super.unbind(op, index)
            }

            override fun computeValue(): Double {
                try {
                    val value = op[index.intValue]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value.toDouble()
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return 0.0
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, index)

        }
    }

    /**
     * Creates a new [FloatBinding] that contains the element of an [ObservableList] at the specified position. The
     * `FloatBinding` will hold `0.0f`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `FloatBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun floatValueAt(op: ObservableList<out Number?>, index: Int): FloatBinding {
        require(index >= 0) { "Index cannot be negative" }

        return object : FloatBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Float {
                try {
                    val value = op[index]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value.toFloat()
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return 0.0f
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [FloatBinding] that contains the element of an [ObservableList] at the specified position. The
     * `FloatBinding` will hold `0.0f`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `FloatBinding`
     */
    fun floatValueAt(op: ObservableList<out Number?>, index: ObservableIntValue): FloatBinding {
        return floatValueAt(op, index as ObservableNumberValue)
    }

    /**
     * Creates a new [FloatBinding] that contains the element of an [ObservableList] at the specified position. The
     * `FloatBinding` will hold `0.0f`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `FloatBinding`
     */
    fun floatValueAt(op: ObservableList<out Number?>, index: ObservableNumberValue): FloatBinding {
        return object : FloatBinding() {

            init {
                super.bind(op, index)
            }

            override fun dispose() {
                super.unbind(op, index)
            }

            override fun computeValue(): Float {
                try {
                    val value = op[index.intValue]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value.toFloat()
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return 0.0f
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, index)

        }
    }

    /**
     * Creates a new [IntBinding] that contains the element of an [ObservableList] at the specified position. The
     * `IntBinding` will hold `0`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `IntBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun intValueAt(op: ObservableList<out Number?>, index: Int): IntBinding {
        require(index >= 0) { "Index cannot be negative" }

        return object : IntBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Int {
                try {
                    val value = op[index]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value.toInt()
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return 0
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [IntBinding] that contains the element of an [ObservableList] at the specified position. The
     * `IntBinding` will hold `0`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `IntBinding`
     */
    fun intValueAt(op: ObservableList<out Number?>, index: ObservableIntValue): IntBinding {
        return intValueAt(op, index as ObservableNumberValue)
    }

    /**
     * Creates a new [IntBinding] that contains the element of an [ObservableList] at the specified position. The
     * `IntBinding` will hold `0`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `IntBinding`
     */
    fun intValueAt(op: ObservableList<out Number?>, index: ObservableNumberValue): IntBinding {
        return object : IntBinding() {

            init {
                super.bind(op, index)
            }

            override fun dispose() {
                super.unbind(op, index)
            }

            override fun computeValue(): Int {
                try {
                    val value = op[index.intValue]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value.toInt()
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return 0
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, index)

        }
    }

    /**
     * Creates a new [LongBinding] that contains the element of an [ObservableList] at the specified position. The
     * `LongBinding` will hold `0L`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `LongBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun longValueAt(op: ObservableList<out Number?>, index: Int): LongBinding {
        require(index >= 0) { "Index cannot be negative" }

        return object : LongBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Long {
                try {
                    val value = op[index]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value.toLong()
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return 0L
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [LongBinding] that contains the element of an [ObservableList] at the specified position. The
     * `LongBinding` will hold `0L`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `LongBinding`
     */
    fun longValueAt(op: ObservableList<out Number?>, index: ObservableIntValue): LongBinding {
        return longValueAt(op, index as ObservableNumberValue)
    }

    /**
     * Creates a new [LongBinding] that contains the element of an [ObservableList] at the specified position. The
     * `LongBinding` will hold `0L`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `LongBinding`
     */
    fun longValueAt(op: ObservableList<out Number?>, index: ObservableNumberValue): LongBinding {
        return object : LongBinding() {

            init {
                super.bind(op, index)
            }

            override fun dispose() {
                super.unbind(op, index)
            }

            override fun computeValue(): Long {
                try {
                    val value = op[index.intValue]
                    if (value == null) {
                        Logging.getLogger().fine("List element is null, returning default value instead.",
                                NullPointerException())
                    } else {
                        return value.toLong()
                    }
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return 0L
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, index)

        }
    }

    /**
     * Creates a new [StringBinding] that contains the element of an [ObservableList] at the specified position. The
     * `StringBinding` will hold `null`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `StringBinding`
     *
     * @throws IllegalArgumentException if `index < 0`
     */
    fun stringValueAt(op: ObservableList<String?>, index: Int): StringBinding {
        require(index >= 0) { "Index cannot be negative" }

        return object : StringBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): String? {
                try {
                    return op[index]
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [StringBinding] that contains the element of an [ObservableList] at the specified position. The
     * `StringBinding` will hold `null`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `StringBinding`
     */
    fun stringValueAt(op: ObservableList<String?>, index: ObservableIntValue): StringBinding {
        return stringValueAt(op, index as ObservableNumberValue)
    }

    /**
     * Creates a new [StringBinding] that contains the element of an [ObservableList] at the specified position. The
     * `StringBinding` will hold `null`, if the `index` points behind the `ObservableList`.
     *
     * @param op the `ObservableList`
     * @param index the position in the `List`
     *
     * @return the new `StringBinding`
     */
    fun stringValueAt(op: ObservableList<String?>, index: ObservableNumberValue): StringBinding {
        return object : StringBinding() {

            init {
                super.bind(op, index)
            }

            override fun dispose() {
                super.unbind(op, index)
            }

            override fun computeValue(): String? {
                try {
                    return op[index.intValue]
                } catch (ex: IndexOutOfBoundsException) {
                    Logging.getLogger().fine("Exception while evaluating binding", ex)
                }
                return null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, index)

        }
    }

    // Set
    // =================================================================================================================

    /**
     * Creates a new [IntBinding] that contains the size of an [ObservableSet].
     *
     * @param op the `ObservableSet`
     * @param E the type of the `MutableSet` elements
     *
     * @return the new `IntBinding`
     */
    fun <E> size(op: ObservableSet<E>): IntBinding {
        return object : IntBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Int {
                return op.size
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a given [ObservableSet] is empty.
     *
     * @param op the `ObservableSet`
     * @param E the type of the `MutableSet` elements
     *
     * @return the new `BooleanBinding`
     */
    fun <E> isEmpty(op: ObservableSet<E>): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.isEmpty()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a given [ObservableSet] is not empty.
     *
     * @param op the `ObservableSet`
     * @param E the type of the `MutableSet` elements
     *
     * @return the new `BooleanBinding`
     */
    fun <E> isNotEmpty(op: ObservableSet<E>): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.isNotEmpty()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    // Map
    // =================================================================================================================

    /**
     * Creates a new [IntBinding] that contains the size of an [ObservableMap].
     *
     * @param op the `ObservableMap`
     * @param K type of the key elements of the `Map`
     * @param V type of the value elements of the `Map`
     *
     * @return the new `IntBinding`
     */
    fun <K, V> size(op: ObservableMap<K, V>): IntBinding {
        return object : IntBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Int {
                return op.size
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a given [ObservableMap] is empty.
     *
     * @param op the `ObservableMap`
     * @param K type of the key elements of the `Map`
     * @param V type of the value elements of the `Map`
     *
     * @return the new `BooleanBinding`
     */
    fun <K, V> isEmpty(op: ObservableMap<K, V>): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.isEmpty()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that holds `true` if a given [ObservableMap] is not empty.
     *
     * @param op the `ObservableMap`
     * @param K type of the key elements of the `Map`
     * @param V type of the value elements of the `Map`
     *
     * @return the new `BooleanBinding`
     */
    fun <K, V> isNotEmpty(op: ObservableMap<K, V>): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.isNotEmpty()
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [ObjectBinding] that contains the mapping of a specific key in an [ObservableMap].
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     * @param V type of the value elements of the `Map`
     *
     * @return the new `ObjectBinding`
     */
    fun <K, V> valueAt(op: ObservableMap<K, V>, key: K): ObjectBinding<V?> {
        return object : ObjectBinding<V?>() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): V? {
                try {
                    return op[key]
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [ObjectBinding] that contains the mapping of a specific key in an [ObservableMap].
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     * @param V type of the value elements of the `Map`
     *
     * @return the new `ObjectBinding`
     */
    fun <K, V> valueAt(op: ObservableMap<K, V>, key: ObservableValue<K>): ObjectBinding<V?> {
        return object : ObjectBinding<V?>() {

            init {
                super.bind(op, key)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): V? {
                try {
                    return op[key.value]
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, key)

        }
    }

    /**
     * Creates a new [BooleanBinding] that contains the mapping of a specific key in an [ObservableMap]. The
     * `BooleanBinding` will hold `false`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `BooleanBinding`
     */
    fun <K> booleanValueAt(op: ObservableMap<K, Boolean>, key: K): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                try {
                    val value = op[key]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return false
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [BooleanBinding] that contains the mapping of a specific key in an [ObservableMap]. The
     * `BooleanBinding` will hold `false`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `BooleanBinding`
     */
    fun <K> booleanValueAt(op: ObservableMap<K, Boolean>, key: ObservableValue<K>): BooleanBinding {
        return object : BooleanBinding() {

            init {
                super.bind(op, key)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                try {
                    val value = op[key.value]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return false
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, key)

        }
    }

    /**
     * Creates a new [DoubleBinding] that contains the mapping of a specific key in an [ObservableMap]. The
     * `DoubleBinding` will hold `0.0`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `DoubleBinding`
     */
    fun <K> doubleValueAt(op: ObservableMap<K, Double>, key: K): DoubleBinding {
        return object : DoubleBinding() {
            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Double {
                try {
                    val value = op[key]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return 0.0
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)
        }
    }

    /**
     * Creates a new [DoubleBinding] that contains the mapping of a specific key in an [ObservableMap]. The
     * `DoubleBinding` will hold `0.0`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `DoubleBinding`
     */
    fun <K> doubleValueAt(op: ObservableMap<K, Double>, key: ObservableValue<K>): DoubleBinding {
        return object : DoubleBinding() {
            init {
                super.bind(op, key)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Double {
                try {
                    val value = op[key.value]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return 0.0
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, key)
        }
    }

    /**
     * Creates a new [FloatBinding] that contains the mapping of a specific key in an [ObservableMap]. The
     * `FloatBinding` will hold `0.0f`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `FloatBinding`
     */
    fun <K> floatValueAt(op: ObservableMap<K, Float>, key: K): FloatBinding {
        return object : FloatBinding() {
            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Float {
                try {
                    val value = op[key]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return 0.0f
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)
        }
    }

    /**
     * Creates a new [FloatBinding] that contains the mapping of a specific key in an [ObservableMap]. The
     * `FloatBinding` will hold `0.0f`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `FloatBinding`
     */
    fun <K> floatValueAt(op: ObservableMap<K, Float>, key: ObservableValue<K>): FloatBinding {
        return object : FloatBinding() {
            init {
                super.bind(op, key)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Float {
                try {
                    val value = op[key.value]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return 0.0f
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, key)
        }
    }

    /**
     * Creates a new [IntBinding] that contains the mapping of a specific key in an [ObservableMap]. The `IntBinding`
     * will hold `0`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `IntBinding`
     */
    fun <K> intValueAt(op: ObservableMap<K, Int>, key: K): IntBinding {
        return object : IntBinding() {
            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Int {
                try {
                    val value = op[key]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return 0
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)
        }
    }

    /**
     * Creates a new [IntBinding] that contains the mapping of a specific key in an [ObservableMap]. The `IntBinding`
     * will hold `0`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `IntBinding`
     */
    fun <K> intValueAt(op: ObservableMap<K, Int>, key: ObservableValue<K>): IntBinding {
        return object : IntBinding() {
            init {
                super.bind(op, key)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Int {
                try {
                    val value = op[key.value]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return 0
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, key)
        }
    }

    /**
     * Creates a new [LongBinding] that contains the mapping of a specific key in an [ObservableMap]. The `LongBinding`
     * will hold `0L`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `LongBinding`
     */
    fun <K> longValueAt(op: ObservableMap<K, Long>, key: K): LongBinding {
        return object : LongBinding() {
            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Long {
                try {
                    val value = op[key]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return 0L
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)
        }
    }

    /**
     * Creates a new [LongBinding] that contains the mapping of a specific key in an [ObservableMap]. The `LongBinding`
     * will hold `0L`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `LongBinding`
     */
    fun <K> longValueAt(op: ObservableMap<K, Long>, key: ObservableValue<K>): LongBinding {
        return object : LongBinding() {
            init {
                super.bind(op, key)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Long {
                try {
                    val value = op[key.value]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return 0L
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, key)
        }
    }

    /**
     * Creates a new [StringBinding] that contains the mapping of a specific key in an [ObservableMap]. The
     * `StringBinding` will hold `null`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `StringBinding`
     */
    fun <K> stringValueAt(op: ObservableMap<K, String?>, key: K): StringBinding {
        return object : StringBinding() {

            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): String? {
                try {
                    val value = op[key]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ObservableCollections.singletonObservableList(op)

        }
    }

    /**
     * Creates a new [StringBinding] that contains the mapping of a specific key in an [ObservableMap]. The
     * `StringBinding` will hold `null`, if the `key` cannot be found in the `ObservableMap`.
     *
     * @param op the `ObservableMap`
     * @param key the key in the `Map`
     * @param K type of the key elements of the `Map`
     *
     * @return the new `StringBinding`
     */
    fun <K> stringValueAt(op: ObservableMap<K, String?>, key: ObservableValue<K>): StringBinding {
        return object : StringBinding() {

            init {
                super.bind(op, key)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): String? {
                try {
                    val value = op[key.value]
                    if (value != null) {
                        return value
                    } else {
                        Logging.getLogger().fine("Element not found in map, returning default value instead.",
                                NullPointerException())
                    }
                } catch (ex: ClassCastException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                } catch (ex: NullPointerException) {
                    Logging.getLogger().warning("Exception while evaluating binding", ex)
                    // ignore
                }
                return null
            }

            @get:ReturnsUnmodifiableCollection
            override val dependencies: ObservableList<*>
                get() = ImmutableObservableList(op, key)

        }
    }

}