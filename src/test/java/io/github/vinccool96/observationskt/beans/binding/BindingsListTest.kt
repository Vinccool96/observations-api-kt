package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.IntProperty
import io.github.vinccool96.observationskt.beans.property.ListProperty
import io.github.vinccool96.observationskt.beans.property.SimpleIntProperty
import io.github.vinccool96.observationskt.beans.property.SimpleListProperty
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.ErrorLoggingUtility
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("RedundantNullableReturnType")
class BindingsListTest {

    private lateinit var property: ListProperty<Any>

    private lateinit var list1: ObservableList<Any>

    private lateinit var list2: ObservableList<Any>

    private lateinit var index: IntProperty

    @Before
    fun setUp() {
        this.property = SimpleListProperty()
        this.list1 = ObservableCollections.observableArrayList(data1, data2)
        this.list2 = ObservableCollections.observableArrayList()
        this.index = SimpleIntProperty()
    }

    @Test
    fun testSize() {
        val size = Bindings.size(this.property)
        DependencyUtils.checkDependencies(size.dependencies, this.property)

        assertEquals(0, size.get())
        this.property.set(this.list1)
        assertEquals(2, size.get())
        this.list1.remove(data2)
        assertEquals(1, size.get())
        this.property.set(this.list2)
        assertEquals(0, size.get())
        this.property.addAll(data2, data2)
        assertEquals(2, size.get())
        this.property.set(null)
        assertEquals(0, size.get())
        size.dispose()
    }

    @Test
    fun testIsEmpty() {
        val empty = Bindings.isEmpty(this.property)
        DependencyUtils.checkDependencies(empty.dependencies, this.property)

        assertTrue(empty.get())
        this.property.set(this.list1)
        assertFalse(empty.get())
        this.list1.remove(data2)
        assertFalse(empty.get())
        this.property.set(this.list2)
        assertTrue(empty.get())
        this.property.addAll(data2, data2)
        assertFalse(empty.get())
        this.property.set(null)
        assertTrue(empty.get())
        empty.dispose()
    }

    @Test
    fun testIsNotEmpty() {
        val notEmpty = Bindings.isNotEmpty(this.property)
        DependencyUtils.checkDependencies(notEmpty.dependencies, this.property)

        assertFalse(notEmpty.get())
        this.property.set(this.list1)
        assertTrue(notEmpty.get())
        this.list1.remove(data2)
        assertTrue(notEmpty.get())
        this.property.set(this.list2)
        assertFalse(notEmpty.get())
        this.property.addAll(data2, data2)
        assertTrue(notEmpty.get())
        this.property.set(null)
        assertFalse(notEmpty.get())
        notEmpty.dispose()
    }

    @Test
    fun testValueAt_Constant() {
        val binding0 = Bindings.valueAt(this.property, 0)
        val binding1 = Bindings.valueAt(this.property, 1)
        val binding2 = Bindings.valueAt(this.property, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, this.property)
        DependencyUtils.checkDependencies(binding1.dependencies, this.property)
        DependencyUtils.checkDependencies(binding2.dependencies, this.property)

        assertNull(binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.set(this.list1)
        assertEquals(data1, binding0.get())
        assertEquals(data2, binding1.get())
        assertNull(binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.remove(data2)
        assertEquals(data1, binding0.get())
        assertNull(binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.set(this.list2)
        assertNull(binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.addAll(data2, data2)
        assertEquals(data2, binding0.get())
        assertEquals(data2, binding1.get())
        assertNull(binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.set(null)
        assertNull(binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testValueAt_Constant_NegativeIndex() {
        Bindings.valueAt(this.property, -1)
    }

    @Test
    fun testValueAt_Variable() {
        val binding = Bindings.valueAt(this.property, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, this.property, this.index)

        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertNull(binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.set(this.list1)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(data1, binding.get())
        this.index.set(1)
        assertEquals(data2, binding.get())
        this.index.set(2)
        assertNull(binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.remove(data2)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(data1, binding.get())
        this.index.set(1)
        assertNull(binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.set(this.list2)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertNull(binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.addAll(data2, data2)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(data2, binding.get())
        this.index.set(1)
        assertEquals(data2, binding.get())
        this.index.set(2)
        assertNull(binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        this.property.set(null)
        this.index.set(-1)
        assertNull(binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertNull(binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testBooleanValueAt_Constant() {
        val defaultData = false
        val localData1 = false
        val localData2 = true
        val localProperty: ListProperty<Boolean?> = SimpleListProperty()
        val localList1: ObservableList<Boolean?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Boolean?> = ObservableCollections.observableArrayList()

        val binding0 = Bindings.booleanValueAt(localProperty, 0)
        val binding1 = Bindings.booleanValueAt(localProperty, 1)
        val binding2 = Bindings.booleanValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localList2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testBooleanValueAt_Constant_NegativeIndex() {
        val localProperty: ListProperty<Boolean?> = SimpleListProperty()
        Bindings.booleanValueAt(localProperty, -1)
    }

    @Test
    fun testBooleanValueAt_Variable() {
        val defaultData = false
        val localData1 = false
        val localData2 = true
        val localProperty: ListProperty<Boolean?> = SimpleListProperty()
        val localList1: ObservableList<Boolean?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Boolean?> = ObservableCollections.observableArrayList()

        val binding = Bindings.booleanValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

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
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testDoubleValueAt_Constant() {
        val defaultData = 0.0
        val localData1 = PI
        val localData2 = -E
        val localProperty: ListProperty<Double?> = SimpleListProperty()
        val localList1: ObservableList<Double?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Double?> = ObservableCollections.observableArrayList()

        val binding0 = Bindings.doubleValueAt(localProperty, 0)
        val binding1 = Bindings.doubleValueAt(localProperty, 1)
        val binding2 = Bindings.doubleValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        assertEquals(localData1, binding0.get(), EPSILON_DOUBLE)
        assertEquals(localData2, binding1.get(), EPSILON_DOUBLE)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        assertEquals(localData1, binding0.get(), EPSILON_DOUBLE)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localList2)
        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        assertEquals(localData2, binding0.get(), EPSILON_DOUBLE)
        assertEquals(localData2, binding1.get(), EPSILON_DOUBLE)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDoubleValueAt_Constant_NegativeIndex() {
        val localProperty: ListProperty<Boolean?> = SimpleListProperty()
        Bindings.booleanValueAt(localProperty, -1)
    }

    @Test
    fun testDoubleValueAt_Variable() {
        val defaultData = 0.0
        val localData1 = PI
        val localData2 = -E
        val localProperty: ListProperty<Double?> = SimpleListProperty()
        val localList1: ObservableList<Double?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Double?> = ObservableCollections.observableArrayList()

        val binding = Bindings.doubleValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get(), EPSILON_DOUBLE)
        this.index.set(1)
        assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(2)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get(), EPSILON_DOUBLE)
        this.index.set(1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(1)
        assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(2)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testFloatValueAt_Constant() {
        val defaultData = 0.0f
        val localData1 = PI.toFloat()
        val localData2 = -E.toFloat()
        val localProperty: ListProperty<Float?> = SimpleListProperty()
        val localList1: ObservableList<Float?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Float?> = ObservableCollections.observableArrayList()

        val binding0 = Bindings.floatValueAt(localProperty, 0)
        val binding1 = Bindings.floatValueAt(localProperty, 1)
        val binding2 = Bindings.floatValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        assertEquals(localData1, binding0.get(), EPSILON_FLOAT)
        assertEquals(localData2, binding1.get(), EPSILON_FLOAT)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        assertEquals(localData1, binding0.get(), EPSILON_FLOAT)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localList2)
        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        assertEquals(localData2, binding0.get(), EPSILON_FLOAT)
        assertEquals(localData2, binding1.get(), EPSILON_FLOAT)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFloatValueAt_Constant_NegativeIndex() {
        val localProperty: ListProperty<Boolean?> = SimpleListProperty()
        Bindings.booleanValueAt(localProperty, -1)
    }

    @Test
    fun testFloatValueAt_Variable() {
        val defaultData = 0.0f
        val localData1 = PI.toFloat()
        val localData2 = -E.toFloat()
        val localProperty: ListProperty<Float?> = SimpleListProperty()
        val localList1: ObservableList<Float?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Float?> = ObservableCollections.observableArrayList()

        val binding = Bindings.floatValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get(), EPSILON_FLOAT)
        this.index.set(1)
        assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(2)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get(), EPSILON_FLOAT)
        this.index.set(1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(1)
        assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(2)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testIntValueAt_Constant() {
        val defaultData = 0
        val localData1 = 42
        val localData2 = -7
        val localProperty: ListProperty<Int?> = SimpleListProperty()
        val localList1: ObservableList<Int?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Int?> = ObservableCollections.observableArrayList()

        val binding0 = Bindings.intValueAt(localProperty, 0)
        val binding1 = Bindings.intValueAt(localProperty, 1)
        val binding2 = Bindings.intValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localList2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testIntValueAt_Constant_NegativeIndex() {
        val localProperty: ListProperty<Int?> = SimpleListProperty()
        Bindings.intValueAt(localProperty, -1)
    }

    @Test
    fun testIntValueAt_Variable() {
        val defaultData = 0
        val localData1 = 42
        val localData2 = -7
        val localProperty: ListProperty<Int?> = SimpleListProperty()
        val localList1: ObservableList<Int?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Int?> = ObservableCollections.observableArrayList()

        val binding = Bindings.intValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

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
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testLongValueAt_Constant() {
        val defaultData = 0L
        val localData1 = 1234567890987654321L
        val localData2 = -987654321987654321L
        val localProperty: ListProperty<Long?> = SimpleListProperty()
        val localList1: ObservableList<Long?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Long?> = ObservableCollections.observableArrayList()

        val binding0 = Bindings.longValueAt(localProperty, 0)
        val binding1 = Bindings.longValueAt(localProperty, 1)
        val binding2 = Bindings.longValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localList2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testLongValueAt_Constant_NegativeIndex() {
        val localProperty: ListProperty<Long?> = SimpleListProperty()
        Bindings.longValueAt(localProperty, -1)
    }

    @Test
    fun testLongValueAt_Variable() {
        val defaultData = 0L
        val localData1 = 1234567890987654321L
        val localData2 = -987654321987654321L
        val localProperty: ListProperty<Long?> = SimpleListProperty()
        val localList1: ObservableList<Long?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Long?> = ObservableCollections.observableArrayList()

        val binding = Bindings.longValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

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
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testShortValueAt_Constant() {
        val defaultData: Short = 0
        val localData1: Short = 12345
        val localData2: Short = -9876
        val localProperty: ListProperty<Short?> = SimpleListProperty()
        val localList1: ObservableList<Short?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Short?> = ObservableCollections.observableArrayList()

        val binding0 = Bindings.shortValueAt(localProperty, 0)
        val binding1 = Bindings.shortValueAt(localProperty, 1)
        val binding2 = Bindings.shortValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localList2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testShortValueAt_Constant_NegativeIndex() {
        val localProperty: ListProperty<Short?> = SimpleListProperty()
        Bindings.shortValueAt(localProperty, -1)
    }

    @Test
    fun testShortValueAt_Variable() {
        val defaultData: Short = 0
        val localData1: Short = 12345
        val localData2: Short = -9876
        val localProperty: ListProperty<Short?> = SimpleListProperty()
        val localList1: ObservableList<Short?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<Short?> = ObservableCollections.observableArrayList()

        val binding = Bindings.shortValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty[0] = null
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

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
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding.dispose()
    }

    @Test
    fun testStringValueAt_Constant() {
        val defaultData: String? = null
        val localData1: String? = "Hello World"
        val localData2: String? = "Goodbye World"
        val localProperty: ListProperty<String?> = SimpleListProperty()
        val localList1: ObservableList<String?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<String?> = ObservableCollections.observableArrayList()

        val binding0 = Bindings.stringValueAt(localProperty, 0)
        val binding1 = Bindings.stringValueAt(localProperty, 1)
        val binding2 = Bindings.stringValueAt(localProperty, 2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.addAll(localData2, localData2)
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStringValueAt_Constant_NegativeIndex() {
        val localProperty: ListProperty<String?> = SimpleListProperty()
        Bindings.stringValueAt(localProperty, -1)
    }

    @Test
    fun testStringValueAt_Variable() {
        val defaultData: String? = null
        val localData1: String? = "Hello World"
        val localData2: String? = "Goodbye World"
        val localProperty: ListProperty<String?> = SimpleListProperty()
        val localList1: ObservableList<String?> = ObservableCollections.observableArrayList(localData1, localData2)
        val localList2: ObservableList<String?> = ObservableCollections.observableArrayList()

        val binding = Bindings.stringValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(localData2, binding.get())
        this.index.set(2)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.removeAt(1)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(localData1, binding.get())
        this.index.set(1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(localList2)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)

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
        log.checkFine(IndexOutOfBoundsException::class.java)

        localProperty.set(null)
        this.index.set(-1)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        this.index.set(0)
        assertEquals(defaultData, binding.get())
        log.checkFine(IndexOutOfBoundsException::class.java)
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