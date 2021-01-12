package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.MapProperty
import io.github.vinccool96.observationskt.beans.property.SimpleMapProperty
import io.github.vinccool96.observationskt.beans.property.SimpleStringProperty
import io.github.vinccool96.observationskt.beans.property.StringProperty
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableMap
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

class BindingsMapTest {

    private lateinit var property: MapProperty<String?, Any>

    private lateinit var map1: ObservableMap<String?, Any>

    private lateinit var map2: ObservableMap<String?, Any>

    private lateinit var index: StringProperty

    @Before
    fun setUp() {
        this.property = SimpleMapProperty()
        this.map1 = ObservableCollections.observableHashMap(key1 to data1, key2 to data2)
        this.map2 = ObservableCollections.observableHashMap()
        this.index = SimpleStringProperty()
    }

    @Test
    fun testSize() {
        val size = Bindings.size(this.property)
        DependencyUtils.checkDependencies(size.dependencies, this.property)

        assertEquals(0, size.get())
        this.property.set(this.map1)
        assertEquals(2, size.get())
        this.map1.remove(key1)
        assertEquals(1, size.get())
        this.property.set(this.map2)
        assertEquals(0, size.get())
        this.property[key1] = data1
        this.property[key2] = data2
        assertEquals(2, size.get())
        this.property.set(null)
        assertEquals(0, size.get())
    }

    @Test
    fun testIsEmpty() {
        val empty = Bindings.isEmpty(this.property)
        DependencyUtils.checkDependencies(empty.dependencies, this.property)

        assertTrue(empty.get())
        this.property.set(this.map1)
        assertFalse(empty.get())
        this.map1.remove(key1)
        assertFalse(empty.get())
        this.property.set(this.map2)
        assertTrue(empty.get())
        this.property[key1] = data1
        this.property[key2] = data2
        assertFalse(empty.get())
        this.property.set(null)
        assertTrue(empty.get())
    }

    @Test
    fun testIsNotEmpty() {
        val notEmpty = Bindings.isNotEmpty(this.property)
        DependencyUtils.checkDependencies(notEmpty.dependencies, this.property)

        assertFalse(notEmpty.get())
        this.property.set(this.map1)
        assertTrue(notEmpty.get())
        this.map1.remove(key1)
        assertTrue(notEmpty.get())
        this.property.set(this.map2)
        assertFalse(notEmpty.get())
        this.property[key1] = data1
        this.property[key2] = data2
        assertTrue(notEmpty.get())
        this.property.set(null)
        assertFalse(notEmpty.get())
    }

    @Test
    fun testValueAt_Constant() {
        val binding0 = Bindings.valueAt(this.property, key1)
        val binding1 = Bindings.valueAt(this.property, key2)
        val binding2 = Bindings.valueAt(this.property, key3)
        DependencyUtils.checkDependencies(binding0.dependencies, this.property)
        DependencyUtils.checkDependencies(binding1.dependencies, this.property)
        DependencyUtils.checkDependencies(binding2.dependencies, this.property)
        assertNull(binding0.get())
        assertNull(binding1.get())
        assertNull(binding2.get())

        this.property.set(this.map1)
        assertEquals(data1, binding0.get())
        assertEquals(data2, binding1.get())
        assertNull(binding2.get())

        this.property.remove(key2)
        assertEquals(data1, binding0.get())
        assertNull(binding1.get())
        assertNull(binding2.get())

        this.property.set(this.map2)
        assertNull(binding0.get())
        assertNull(binding1.get())
        assertNull(binding2.get())

        this.property[key1] = data2
        this.property[key2] = data2
        assertEquals(data2, binding0.get())
        assertEquals(data2, binding1.get())
        assertNull(binding2.get())

        this.property.set(null)
        assertNull(binding0.get())
        assertNull(binding1.get())
        assertNull(binding2.get())
    }

    @Test
    fun testValueAt_Variable() {
        val binding = Bindings.valueAt(this.property, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, this.property, this.index)

        this.index.set(null)
        assertNull(binding.get())
        this.index.set(key1)
        assertNull(binding.get())

        this.property.set(this.map1)
        this.index.set(null)
        assertNull(binding.get())
        this.index.set(key1)
        assertEquals(data1, binding.get())
        this.index.set(key2)
        assertEquals(data2, binding.get())
        this.index.set(key3)
        assertNull(binding.get())

        this.property.remove(key2)
        this.index.set(null)
        assertNull(binding.get())
        this.index.set(key1)
        assertEquals(data1, binding.get())
        this.index.set(key2)
        assertNull(binding.get())

        this.property.set(this.map2)
        this.index.set(null)
        assertNull(binding.get())
        this.index.set(key1)
        assertNull(binding.get())

        this.property[key1] = data2
        this.property[key2] = data2
        this.index.set(null)
        assertNull(binding.get())
        this.index.set(key1)
        assertEquals(data2, binding.get())
        this.index.set(key2)
        assertEquals(data2, binding.get())
        this.index.set(key3)
        assertNull(binding.get())

        this.property.set(null)
        this.index.set(null)
        assertNull(binding.get())
        this.index.set(key1)
        assertNull(binding.get())
    }

    @Test
    fun testBooleanValueAt_Constant() {
        val defaultData = false
        val localData1 = false
        val localData2 = true
        val localProperty: MapProperty<String?, Boolean> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Boolean> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Boolean> = ObservableCollections.observableHashMap()

        val binding0 = Bindings.booleanValueAt(localProperty, key1)
        val binding1 = Bindings.booleanValueAt(localProperty, key2)
        val binding2 = Bindings.booleanValueAt(localProperty, key3)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testBooleanValueAt_Variable() {
        val defaultData = false
        val localData1 = false
        val localData2 = true
        val localProperty: MapProperty<String?, Boolean> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Boolean> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Boolean> = ObservableCollections.observableHashMap()

        val binding = Bindings.booleanValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get())
        this.index.set(key2)
        assertEquals(localData2, binding.get())
        this.index.set(key3)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get())
        this.index.set(key2)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData2, binding.get())
        this.index.set(key2)
        assertEquals(localData2, binding.get())
        this.index.set(key3)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testDoubleValueAt_Constant() {
        val defaultData = 0.0
        val localData1 = PI
        val localData2 = -E
        val localProperty: MapProperty<String?, Double> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Double> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Double> = ObservableCollections.observableHashMap()

        val binding0 = Bindings.doubleValueAt(localProperty, key1)
        val binding1 = Bindings.doubleValueAt(localProperty, key2)
        val binding2 = Bindings.doubleValueAt(localProperty, key3)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)
        org.junit.Assert.assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        org.junit.Assert.assertEquals(localData1, binding0.get(), EPSILON_DOUBLE)
        org.junit.Assert.assertEquals(localData2, binding1.get(), EPSILON_DOUBLE)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        org.junit.Assert.assertEquals(localData1, binding0.get(), EPSILON_DOUBLE)
        org.junit.Assert.assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        org.junit.Assert.assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        org.junit.Assert.assertEquals(localData2, binding0.get(), EPSILON_DOUBLE)
        org.junit.Assert.assertEquals(localData2, binding1.get(), EPSILON_DOUBLE)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        org.junit.Assert.assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testDoubleValueAt_Variable() {
        val defaultData = 0.0
        val localData1 = PI
        val localData2 = -E
        val localProperty: MapProperty<String?, Double> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Double> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Double> = ObservableCollections.observableHashMap()

        val binding = Bindings.doubleValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(localData1, binding.get(), EPSILON_DOUBLE)
        this.index.set(key2)
        org.junit.Assert.assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(key3)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(localData1, binding.get(), EPSILON_DOUBLE)
        this.index.set(key2)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(key2)
        org.junit.Assert.assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(key3)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testFloatValueAt_Constant() {
        val defaultData = 0.0f
        val localData1 = PI.toFloat()
        val localData2 = -E.toFloat()
        val localProperty: MapProperty<String?, Float> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Float> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Float> = ObservableCollections.observableHashMap()

        val binding0 = Bindings.floatValueAt(localProperty, key1)
        val binding1 = Bindings.floatValueAt(localProperty, key2)
        val binding2 = Bindings.floatValueAt(localProperty, key3)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)
        org.junit.Assert.assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        org.junit.Assert.assertEquals(localData1, binding0.get(), EPSILON_FLOAT)
        org.junit.Assert.assertEquals(localData2, binding1.get(), EPSILON_FLOAT)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        org.junit.Assert.assertEquals(localData1, binding0.get(), EPSILON_FLOAT)
        org.junit.Assert.assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        org.junit.Assert.assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        org.junit.Assert.assertEquals(localData2, binding0.get(), EPSILON_FLOAT)
        org.junit.Assert.assertEquals(localData2, binding1.get(), EPSILON_FLOAT)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        org.junit.Assert.assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        org.junit.Assert.assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testFloatValueAt_Variable() {
        val defaultData = 0.0f
        val localData1 = PI.toFloat()
        val localData2 = -E.toFloat()
        val localProperty: MapProperty<String?, Float> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Float> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Float> = ObservableCollections.observableHashMap()

        val binding = Bindings.floatValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(localData1, binding.get(), EPSILON_FLOAT)
        this.index.set(key2)
        org.junit.Assert.assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(key3)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(localData1, binding.get(), EPSILON_FLOAT)
        this.index.set(key2)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(key2)
        org.junit.Assert.assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(key3)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        this.index.set(null)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        org.junit.Assert.assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testIntValueAt_Constant() {
        val defaultData = 0
        val localData1 = 42
        val localData2 = -7
        val localProperty: MapProperty<String?, Int> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Int> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Int> = ObservableCollections.observableHashMap()

        val binding0 = Bindings.intValueAt(localProperty, key1)
        val binding1 = Bindings.intValueAt(localProperty, key2)
        val binding2 = Bindings.intValueAt(localProperty, key3)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testIntValueAt_Variable() {
        val defaultData = 0
        val localData1 = 42
        val localData2 = -7
        val localProperty: MapProperty<String?, Int> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Int> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Int> = ObservableCollections.observableHashMap()

        val binding = Bindings.intValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get())
        this.index.set(key2)
        assertEquals(localData2, binding.get())
        this.index.set(key3)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get())
        this.index.set(key2)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData2, binding.get())
        this.index.set(key2)
        assertEquals(localData2, binding.get())
        this.index.set(key3)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testLongValueAt_Constant() {
        val defaultData = 0L
        val localData1 = 1234567890987654321L
        val localData2 = -987654321987654321L
        val localProperty: MapProperty<String?, Long> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Long> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Long> = ObservableCollections.observableHashMap()

        val binding0 = Bindings.longValueAt(localProperty, key1)
        val binding1 = Bindings.longValueAt(localProperty, key2)
        val binding2 = Bindings.longValueAt(localProperty, key3)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testLongValueAt_Variable() {
        val defaultData = 0L
        val localData1 = 1234567890987654321L
        val localData2 = -987654321987654321L
        val localProperty: MapProperty<String?, Long> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Long> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Long> = ObservableCollections.observableHashMap()

        val binding = Bindings.longValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get())
        this.index.set(key2)
        assertEquals(localData2, binding.get())
        this.index.set(key3)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get())
        this.index.set(key2)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData2, binding.get())
        this.index.set(key2)
        assertEquals(localData2, binding.get())
        this.index.set(key3)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testStringValueAt_Constant() {
        val defaultData: String? = null
        val localData1 = "Goodbye"
        val localData2 = "Hello"
        val localProperty: MapProperty<String?, String?> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, String?> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, String?> = ObservableCollections.observableHashMap()

        val binding0 = Bindings.stringValueAt(localProperty, key1)
        val binding1 = Bindings.stringValueAt(localProperty, key2)
        val binding2 = Bindings.stringValueAt(localProperty, key3)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding2.dependencies, localProperty)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        assertEquals(localData1, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        assertEquals(localData1, binding0.get())
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        assertEquals(localData2, binding0.get())
        assertEquals(localData2, binding1.get())
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get())
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testStringValueAt_Variable() {
        val defaultData: String? = null
        val localData1 = "Goodbye"
        val localData2 = "Hello"
        val localProperty: MapProperty<String?, String?> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, String?> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, String?> = ObservableCollections.observableHashMap()

        val binding = Bindings.stringValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get())
        this.index.set(key2)
        assertEquals(localData2, binding.get())
        this.index.set(key3)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get())
        this.index.set(key2)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData2, binding.get())
        this.index.set(key2)
        assertEquals(localData2, binding.get())
        this.index.set(key3)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        this.index.set(null)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.checkFine(NullPointerException::class.java)
    }

    companion object {

        private const val EPSILON_DOUBLE: Double = 1e-12

        private const val EPSILON_FLOAT: Float = 1e-5f

        private const val key1: String = "Key1"

        private const val key2: String = "Key2"

        private const val key3: String = "Key3"

        private val data1: Any = Any()

        private val data2: Any = Any()

        private val log: ErrorLoggingUtility = ErrorLoggingUtility()

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