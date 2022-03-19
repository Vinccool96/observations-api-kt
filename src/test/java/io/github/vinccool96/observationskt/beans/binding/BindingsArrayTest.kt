package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.IntProperty
import io.github.vinccool96.observationskt.beans.property.SimpleIntProperty
import io.github.vinccool96.observationskt.collections.*
import io.github.vinccool96.observationskt.sun.binding.ErrorLoggingUtility
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.*

@Suppress("RedundantNullableReturnType", "UNCHECKED_CAST")
class BindingsArrayTest {

    private lateinit var array: ObservableObjectArray<Any>

    private lateinit var array1: ObservableObjectArray<Any>

    private lateinit var array2: ObservableObjectArray<Any>

    private lateinit var index: IntProperty

    @BeforeTest
    fun setUp() {
        this.array = ObservableCollections.observableObjectArray(arrayOf(Any()))
        this.array1 = ObservableCollections.observableObjectArray(arrayOf(Any()), data1, data2)
        this.array2 = ObservableCollections.observableObjectArray(arrayOf(Any()))
        this.index = SimpleIntProperty()
    }

    private fun <T> setAs(array: ObservableArray<T>, value: ObservableArray<T>?) {
        array.clear()
        if (value != null) {
            when {
                array is ObservableObjectArray<*> && value is ObservableObjectArray<*> -> {
                    try {
                        array as ObservableObjectArray<String?> += value as ObservableObjectArray<String?>
                    } catch (ex: ClassCastException) {
                        array as ObservableObjectArray<Any> += value as ObservableObjectArray<Any>
                    }
                }
                array is ObservableBooleanArray && value is ObservableBooleanArray -> array += value
                array is ObservableDoubleArray && value is ObservableDoubleArray -> array += value
                array is ObservableFloatArray && value is ObservableFloatArray -> array += value
                array is ObservableIntArray && value is ObservableIntArray -> array += value
                array is ObservableLongArray && value is ObservableLongArray -> array += value
                array is ObservableShortArray && value is ObservableShortArray -> array += value
                array is ObservableByteArray && value is ObservableByteArray -> array += value
            }
        }
    }

    @Test
    fun testSize() {
        val size = Bindings.size(this.array)
        DependencyUtils.checkDependencies(size.dependencies, this.array)

        assertEquals(0, size.get())
        setAs(this.array, this.array1)
        assertEquals(2, size.get())
        this.array.resize(1)
        assertEquals(1, size.get())
        setAs(this.array, this.array2)
        assertEquals(0, size.get())
        this.array.addAll(data2, data2)
        assertEquals(2, size.get())
        setAs(this.array, null)
        assertEquals(0, size.get())
        size.dispose()
    }

    @Test
    fun testIsEmpty() {
        val empty = Bindings.isEmpty(this.array)
        DependencyUtils.checkDependencies(empty.dependencies, this.array)

        assertTrue(empty.get())
        setAs(this.array, this.array1)
        assertFalse(empty.get())
        this.array.resize(1)
        assertFalse(empty.get())
        setAs(this.array, this.array2)
        assertTrue(empty.get())
        this.array.addAll(data2, data2)
        assertFalse(empty.get())
        setAs(this.array, null)
        assertTrue(empty.get())
        empty.dispose()
    }

    @Test
    fun testIsNotEmpty() {
        val notEmpty = Bindings.isNotEmpty(this.array)
        DependencyUtils.checkDependencies(notEmpty.dependencies, this.array)

        assertFalse(notEmpty.get())
        setAs(this.array, this.array1)
        assertTrue(notEmpty.get())
        this.array.resize(1)
        assertTrue(notEmpty.get())
        setAs(this.array, this.array2)
        assertFalse(notEmpty.get())
        this.array.addAll(data2, data2)
        assertTrue(notEmpty.get())
        setAs(this.array, null)
        assertFalse(notEmpty.get())
        notEmpty.dispose()
    }

    @Test
    fun testValueAt_Constant() {
        val binding0 = Bindings.valueAt(this.array, 0)
        val binding1 = Bindings.valueAt(this.array, 1)
        val binding2 = Bindings.valueAt(this.array, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, this.array)
        DependencyUtils.checkDependencies(binding1.dependencies, this.array)
        DependencyUtils.checkDependencies(binding2.dependencies, this.array)

        assertNull(binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(this.array, this.array1)
        assertEquals(data1, binding0.get())
        assertEquals(data2, binding1.get())
        assertNull(binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        this.array.resize(1)
        assertEquals(data1, binding0.get())
        assertNull(binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(this.array, this.array2)
        assertNull(binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        this.array.addAll(data2, data2)
        assertEquals(data2, binding0.get())
        assertEquals(data2, binding1.get())
        assertNull(binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(this.array, null)
        assertNull(binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testValueAt_Constant_NegativeIndex() {
        Bindings.valueAt(this.array, -1)
    }

    @Test
    fun testValueAt_Variable() {
        val binding = Bindings.valueAt(this.array, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, this.array, this.index)

        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(this.array, this.array1)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(data1, binding.get())
        this.index.set(1)
        assertEquals(data2, binding.get())
        this.index.set(2)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        this.array.resize(1)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(data1, binding.get())
        this.index.set(1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(this.array, this.array2)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        this.array.addAll(data2, data2)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(data2, binding.get())
        this.index.set(1)
        assertEquals(data2, binding.get())
        this.index.set(2)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(this.array, null)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testBooleanValueAt_Constant() {
        val defaultData = false
        val localData1 = false
        val localData2 = true
        val localProperty: ObservableBooleanArray = ObservableCollections.observableBooleanArray()
        val localArray1: ObservableBooleanArray = ObservableCollections.observableBooleanArray(localData1, localData2)
        val localArray2: ObservableBooleanArray = ObservableCollections.observableBooleanArray()

        val binding0 = Bindings.booleanValueAt(localProperty, 0)
        val binding1 = Bindings.booleanValueAt(localProperty, 1)
        val binding2 = Bindings.booleanValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = booleanArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testBooleanValueAt_Constant_NegativeIndex() {
        val localProperty: ObservableBooleanArray = ObservableCollections.observableBooleanArray()
        Bindings.booleanValueAt(localProperty, -1)
    }

    @Test
    fun testBooleanValueAt_Variable() {
        val defaultData = false
        val localData1 = false
        val localData2 = true
        val localProperty: ObservableBooleanArray = ObservableCollections.observableBooleanArray()
        val localArray1: ObservableBooleanArray = ObservableCollections.observableBooleanArray(localData1, localData2)
        val localArray2: ObservableBooleanArray = ObservableCollections.observableBooleanArray()

        val binding = Bindings.booleanValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = booleanArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testDoubleValueAt_Constant() {
        val defaultData = 0.0
        val localData1 = PI
        val localData2 = -E
        val localProperty: ObservableDoubleArray = ObservableCollections.observableDoubleArray()
        val localArray1: ObservableDoubleArray = ObservableCollections.observableDoubleArray(localData1, localData2)
        val localArray2: ObservableDoubleArray = ObservableCollections.observableDoubleArray()

        val binding0 = Bindings.doubleValueAt(localProperty, 0)
        val binding1 = Bindings.doubleValueAt(localProperty, 1)
        val binding2 = Bindings.doubleValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        assertEquals(localData1, binding0.get(), EPSILON_DOUBLE)
        assertEquals(localData2, binding1.get(), EPSILON_DOUBLE)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        assertEquals(localData1, binding0.get(), EPSILON_DOUBLE)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = doubleArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        assertEquals(localData2, binding0.get(), EPSILON_DOUBLE)
        assertEquals(localData2, binding1.get(), EPSILON_DOUBLE)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDoubleValueAt_Constant_NegativeIndex() {
        val localProperty: ObservableDoubleArray = ObservableCollections.observableDoubleArray()
        Bindings.doubleValueAt(localProperty, -1)
    }

    @Test
    fun testDoubleValueAt_Variable() {
        val defaultData = 0.0
        val localData1 = PI
        val localData2 = -E
        val localProperty: ObservableDoubleArray = ObservableCollections.observableDoubleArray()
        val localArray1: ObservableDoubleArray = ObservableCollections.observableDoubleArray(localData1, localData2)
        val localArray2: ObservableDoubleArray = ObservableCollections.observableDoubleArray()

        val binding = Bindings.doubleValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get(), EPSILON_DOUBLE)
        this.index.set(1)
        assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(2)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get(), EPSILON_DOUBLE)
        this.index.set(1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = doubleArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(1)
        assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(2)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testFloatValueAt_Constant() {
        val defaultData = 0.0f
        val localData1 = PI.toFloat()
        val localData2 = -E.toFloat()
        val localProperty: ObservableFloatArray = ObservableCollections.observableFloatArray()
        val localArray1: ObservableFloatArray = ObservableCollections.observableFloatArray(localData1, localData2)
        val localArray2: ObservableFloatArray = ObservableCollections.observableFloatArray()

        val binding0 = Bindings.floatValueAt(localProperty, 0)
        val binding1 = Bindings.floatValueAt(localProperty, 1)
        val binding2 = Bindings.floatValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        assertEquals(localData1, binding0.get(), EPSILON_FLOAT)
        assertEquals(localData2, binding1.get(), EPSILON_FLOAT)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        assertEquals(localData1, binding0.get(), EPSILON_FLOAT)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = floatArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        assertEquals(localData2, binding0.get(), EPSILON_FLOAT)
        assertEquals(localData2, binding1.get(), EPSILON_FLOAT)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFloatValueAt_Constant_NegativeIndex() {
        val localProperty: ObservableFloatArray = ObservableCollections.observableFloatArray()
        Bindings.floatValueAt(localProperty, -1)
    }

    @Test
    fun testFloatValueAt_Variable() {
        val defaultData = 0.0f
        val localData1 = PI.toFloat()
        val localData2 = -E.toFloat()
        val localProperty: ObservableFloatArray = ObservableCollections.observableFloatArray()
        val localArray1: ObservableFloatArray = ObservableCollections.observableFloatArray(localData1, localData2)
        val localArray2: ObservableFloatArray = ObservableCollections.observableFloatArray()

        val binding = Bindings.floatValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get(), EPSILON_FLOAT)
        this.index.set(1)
        assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(2)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get(), EPSILON_FLOAT)
        this.index.set(1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = floatArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(1)
        assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(2)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testIntValueAt_Constant() {
        val defaultData = 0
        val localData1 = 42
        val localData2 = -7
        val localProperty: ObservableIntArray = ObservableCollections.observableIntArray()
        val localArray1: ObservableIntArray = ObservableCollections.observableIntArray(localData1, localData2)
        val localArray2: ObservableIntArray = ObservableCollections.observableIntArray()

        val binding0 = Bindings.intValueAt(localProperty, 0)
        val binding1 = Bindings.intValueAt(localProperty, 1)
        val binding2 = Bindings.intValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = intArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testIntValueAt_Constant_NegativeIndex() {
        val localProperty: ObservableIntArray = ObservableCollections.observableIntArray()
        Bindings.intValueAt(localProperty, -1)
    }

    @Test
    fun testIntValueAt_Variable() {
        val defaultData = 0
        val localData1 = 42
        val localData2 = -7
        val localProperty: ObservableIntArray = ObservableCollections.observableIntArray()
        val localArray1: ObservableIntArray = ObservableCollections.observableIntArray(localData1, localData2)
        val localArray2: ObservableIntArray = ObservableCollections.observableIntArray()

        val binding = Bindings.intValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = intArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testLongValueAt_Constant() {
        val defaultData = 0L
        val localData1 = 1234567890987654321L
        val localData2 = -987654321987654321L
        val localProperty: ObservableLongArray = ObservableCollections.observableLongArray()
        val localArray1: ObservableLongArray = ObservableCollections.observableLongArray(localData1, localData2)
        val localArray2: ObservableLongArray = ObservableCollections.observableLongArray()

        val binding0 = Bindings.longValueAt(localProperty, 0)
        val binding1 = Bindings.longValueAt(localProperty, 1)
        val binding2 = Bindings.longValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = longArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testLongValueAt_Constant_NegativeIndex() {
        val localProperty: ObservableLongArray = ObservableCollections.observableLongArray()
        Bindings.longValueAt(localProperty, -1)
    }

    @Test
    fun testLongValueAt_Variable() {
        val defaultData = 0L
        val localData1 = 1234567890987654321L
        val localData2 = -987654321987654321L
        val localProperty: ObservableLongArray = ObservableCollections.observableLongArray()
        val localArray1: ObservableLongArray = ObservableCollections.observableLongArray(localData1, localData2)
        val localArray2: ObservableLongArray = ObservableCollections.observableLongArray()

        val binding = Bindings.longValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = longArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testShortValueAt_Constant() {
        val defaultData: Short = 0
        val localData1: Short = 12345
        val localData2: Short = -9876
        val localProperty: ObservableShortArray = ObservableCollections.observableShortArray()
        val localArray1: ObservableShortArray = ObservableCollections.observableShortArray(localData1, localData2)
        val localArray2: ObservableShortArray = ObservableCollections.observableShortArray()

        val binding0 = Bindings.shortValueAt(localProperty, 0)
        val binding1 = Bindings.shortValueAt(localProperty, 1)
        val binding2 = Bindings.shortValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = shortArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testShortValueAt_Constant_NegativeIndex() {
        val localProperty: ObservableShortArray = ObservableCollections.observableShortArray()
        Bindings.shortValueAt(localProperty, -1)
    }

    @Test
    fun testShortValueAt_Variable() {
        val defaultData: Short = 0
        val localData1: Short = 12345
        val localData2: Short = -9876
        val localProperty: ObservableShortArray = ObservableCollections.observableShortArray()
        val localArray1: ObservableShortArray = ObservableCollections.observableShortArray(localData1, localData2)
        val localArray2: ObservableShortArray = ObservableCollections.observableShortArray()

        val binding = Bindings.shortValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = shortArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testByteValueAt_Constant() {
        val defaultData: Byte = 0
        val localData1: Byte = 123
        val localData2: Byte = -98
        val localProperty: ObservableByteArray = ObservableCollections.observableByteArray()
        val localArray1: ObservableByteArray = ObservableCollections.observableByteArray(localData1, localData2)
        val localArray2: ObservableByteArray = ObservableCollections.observableByteArray()

        val binding0 = Bindings.byteValueAt(localProperty, 0)
        val binding1 = Bindings.byteValueAt(localProperty, 1)
        val binding2 = Bindings.byteValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = byteArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testByteValueAt_Constant_NegativeIndex() {
        val localProperty: ObservableByteArray = ObservableCollections.observableByteArray()
        Bindings.byteValueAt(localProperty, -1)
    }

    @Test
    fun testByteValueAt_Variable() {
        val defaultData: Byte = 0
        val localData1: Byte = 123
        val localData2: Byte = -98
        val localProperty: ObservableByteArray = ObservableCollections.observableByteArray()
        val localArray1: ObservableByteArray = ObservableCollections.observableByteArray(localData1, localData2)
        val localArray2: ObservableByteArray = ObservableCollections.observableByteArray()

        val binding = Bindings.byteValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        val data = byteArrayOf(localData2, localData2)
        localProperty.addAll(*data)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testStringValueAt_Constant() {
        val defaultData: String? = null
        val localData1: String? = "Hello World"
        val localData2: String? = "Goodbye World"
        val localProperty: ObservableObjectArray<String?> = ObservableCollections.observableObjectArray(arrayOf(""))
        val localArray1: ObservableObjectArray<String?> =
                ObservableCollections.observableObjectArray(arrayOf(""), localData1, localData2)
        val localArray2: ObservableObjectArray<String?> = ObservableCollections.observableObjectArray(arrayOf(""))

        val binding0 = Bindings.stringValueAt(localProperty, 0)
        val binding1 = Bindings.stringValueAt(localProperty, 1)
        val binding2 = Bindings.stringValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStringValueAt_Constant_NegativeIndex() {
        val localProperty: ObservableObjectArray<String?> = ObservableCollections.observableObjectArray(arrayOf(""))
        Bindings.stringValueAt(localProperty, -1)
    }

    @Test
    fun testStringValueAt_Variable() {
        val defaultData: String? = null
        val localData1: String? = "Hello World"
        val localData2: String? = "Goodbye World"
        val localProperty: ObservableObjectArray<String?> = ObservableCollections.observableObjectArray(arrayOf(""))
        val localArray1: ObservableObjectArray<String?> =
                ObservableCollections.observableObjectArray(arrayOf(""), localData1, localData2)
        val localArray2: ObservableObjectArray<String?> = ObservableCollections.observableObjectArray(arrayOf(""))

        val binding = Bindings.stringValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.resize(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, localArray2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        setAs(localProperty, null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    companion object {

        private const val EPSILON_DOUBLE = 1e-12

        private const val EPSILON_FLOAT = 1e-5f

        private val data1 = Any()

        private val data2 = Any()

        private val log = ErrorLoggingUtility()

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            log.start()
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            log.stop()
        }

    }

}