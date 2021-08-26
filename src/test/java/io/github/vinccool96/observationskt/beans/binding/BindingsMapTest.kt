package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.MapProperty
import io.github.vinccool96.observationskt.beans.property.SimpleMapProperty
import io.github.vinccool96.observationskt.beans.property.SimpleStringProperty
import io.github.vinccool96.observationskt.beans.property.StringProperty
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.sun.binding.ErrorLoggingUtility
import io.github.vinccool96.observationskt.sun.collections.ObservableMapWrapper
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.logging.Level
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
        size.dispose()
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
        empty.dispose()
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
        notEmpty.dispose()
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
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test
    fun testValueAt_Constant_Exception() {
        val localMap1: ObservableMap<String?, Any> = MapClassCastException()
        val localMap2: ObservableMap<String?, Any> = MapNullPointerException()

        val binding = Bindings.valueAt(this.property, key1)
        DependencyUtils.checkDependencies(binding.dependencies, this.property)

        this.property.set(localMap1)
        assertNull(binding.get())
        log.check(Level.WARNING, ClassCastException::class.java)

        this.property.set(localMap2)
        assertNull(binding.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
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
        binding.dispose()
    }

    @Test
    fun testValueAt_Variable_Exception() {
        val localMap1: ObservableMap<String?, Any> = MapClassCastException()
        val localMap2: ObservableMap<String?, Any> = MapNullPointerException()

        val binding = Bindings.valueAt(this.property, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, this.property, this.index)

        this.property.set(localMap1)
        assertNull(binding.get())
        log.check(Level.WARNING, ClassCastException::class.java)

        this.property.set(localMap2)
        assertNull(binding.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
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
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test
    fun testBooleanValueAt_Constant_Exception() {
        val defaultData = false
        val localProperty: MapProperty<String?, Boolean> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Boolean> = MapClassCastException()
        val localMap2: ObservableMap<String?, Boolean> = MapNullPointerException()

        val binding = Bindings.booleanValueAt(localProperty, key1)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty)

        localProperty.set(localMap1)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, ClassCastException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
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
        binding.dispose()
    }

    @Test
    fun testBooleanValueAt_Variable_Exception() {
        val defaultData = false
        val localProperty: MapProperty<String?, Boolean> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Boolean> = MapClassCastException()
        val localMap2: ObservableMap<String?, Boolean> = MapNullPointerException()

        val binding = Bindings.booleanValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        localProperty.set(localMap1)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, ClassCastException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
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
        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        assertEquals(localData1, binding0.get(), EPSILON_DOUBLE)
        assertEquals(localData2, binding1.get(), EPSILON_DOUBLE)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        assertEquals(localData1, binding0.get(), EPSILON_DOUBLE)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        assertEquals(localData2, binding0.get(), EPSILON_DOUBLE)
        assertEquals(localData2, binding1.get(), EPSILON_DOUBLE)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test
    fun testDoubleValueAt_Constant_Exception() {
        val defaultData = 0.0
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding0 = Bindings.doubleValueAt(localProperty, key1)
        val binding1 = Bindings.doubleValueAt(localProperty, key2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)

        assertEquals(defaultData, binding0.get(), EPSILON_DOUBLE)
        log.check(Level.WARNING, ClassCastException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_DOUBLE)
        log.check(Level.WARNING, NullPointerException::class.java)
        binding0.dispose()
        binding1.dispose()
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
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get(), EPSILON_DOUBLE)
        this.index.set(key2)
        assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(key3)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get(), EPSILON_DOUBLE)
        this.index.set(key2)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(key2)
        assertEquals(localData2, binding.get(), EPSILON_DOUBLE)
        this.index.set(key3)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.checkFine(NullPointerException::class.java)
        binding.dispose()
    }

    @Test
    fun testDoubleValueAt_Variable_Exception() {
        val defaultData = 0.0
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding = Bindings.doubleValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(key1)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.check(Level.WARNING, ClassCastException::class.java)
        this.index.set(key2)
        assertEquals(defaultData, binding.get(), EPSILON_DOUBLE)
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
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
        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        assertEquals(localData1, binding0.get(), EPSILON_FLOAT)
        assertEquals(localData2, binding1.get(), EPSILON_FLOAT)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        assertEquals(localData1, binding0.get(), EPSILON_FLOAT)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        assertEquals(localData2, binding0.get(), EPSILON_FLOAT)
        assertEquals(localData2, binding1.get(), EPSILON_FLOAT)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        assertEquals(defaultData, binding2.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test
    fun testFloatValueAt_Constant_Exception() {
        val defaultData = 0.0f
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding0 = Bindings.floatValueAt(localProperty, key1)
        val binding1 = Bindings.floatValueAt(localProperty, key2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)

        assertEquals(defaultData, binding0.get(), EPSILON_FLOAT)
        log.check(Level.WARNING, ClassCastException::class.java)
        assertEquals(defaultData, binding1.get(), EPSILON_FLOAT)
        log.check(Level.WARNING, NullPointerException::class.java)
        binding0.dispose()
        binding1.dispose()
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
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap1)
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get(), EPSILON_FLOAT)
        this.index.set(key2)
        assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(key3)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.remove(key2)
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData1, binding.get(), EPSILON_FLOAT)
        this.index.set(key2)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(localMap2)
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty[key1] = localData2
        localProperty[key2] = localData2
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(key2)
        assertEquals(localData2, binding.get(), EPSILON_FLOAT)
        this.index.set(key3)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)

        localProperty.set(null)
        this.index.set(null)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        this.index.set(key1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.checkFine(NullPointerException::class.java)
        binding.dispose()
    }

    @Test
    fun testFloatValueAt_Variable_Exception() {
        val defaultData = 0.0f
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding = Bindings.floatValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(key1)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.check(Level.WARNING, ClassCastException::class.java)
        this.index.set(key2)
        assertEquals(defaultData, binding.get(), EPSILON_FLOAT)
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
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
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test
    fun testIntValueAt_Constant_Exception() {
        val defaultData = 0
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding0 = Bindings.intValueAt(localProperty, key1)
        val binding1 = Bindings.intValueAt(localProperty, key2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.check(Level.WARNING, ClassCastException::class.java)
        assertEquals(defaultData, binding1.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding0.dispose()
        binding1.dispose()
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
        binding.dispose()
    }

    @Test
    fun testIntValueAt_Variable_Exception() {
        val defaultData = 0
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding = Bindings.intValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, ClassCastException::class.java)
        this.index.set(key2)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
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
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test
    fun testLongValueAt_Constant_Exception() {
        val defaultData = 0L
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding0 = Bindings.longValueAt(localProperty, key1)
        val binding1 = Bindings.longValueAt(localProperty, key2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.check(Level.WARNING, ClassCastException::class.java)
        assertEquals(defaultData, binding1.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding0.dispose()
        binding1.dispose()
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
        binding.dispose()
    }

    @Test
    fun testLongValueAt_Variable_Exception() {
        val defaultData = 0L
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding = Bindings.longValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, ClassCastException::class.java)
        this.index.set(key2)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
    }

    @Test
    fun testShortValueAt_Constant() {
        val defaultData: Short = 0
        val localData1: Short = 12345
        val localData2: Short = -9876
        val localProperty: MapProperty<String?, Short> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Short> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Short> = ObservableCollections.observableHashMap()

        val binding0 = Bindings.shortValueAt(localProperty, key1)
        val binding1 = Bindings.shortValueAt(localProperty, key2)
        val binding2 = Bindings.shortValueAt(localProperty, key3)
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
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test
    fun testShortValueAt_Constant_Exception() {
        val defaultData: Short = 0
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding0 = Bindings.shortValueAt(localProperty, key1)
        val binding1 = Bindings.shortValueAt(localProperty, key2)
        DependencyUtils.checkDependencies(binding0.dependencies, localProperty)
        DependencyUtils.checkDependencies(binding1.dependencies, localProperty)

        assertEquals(defaultData, binding0.get())
        log.check(Level.WARNING, ClassCastException::class.java)
        assertEquals(defaultData, binding1.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding0.dispose()
        binding1.dispose()
    }

    @Test
    fun testShortValueAt_Variable() {
        val defaultData: Short = 0
        val localData1: Short = 12345
        val localData2: Short = -9876
        val localProperty: MapProperty<String?, Short> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, Short> = ObservableCollections.observableHashMap()
        localMap1[key1] = localData1
        localMap1[key2] = localData2
        val localMap2: ObservableMap<String?, Short> = ObservableCollections.observableHashMap()

        val binding = Bindings.shortValueAt(localProperty, this.index)
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
        binding.dispose()
    }

    @Test
    fun testShortValueAt_Variable_Exception() {
        val defaultData: Short = 0
        val localProperty: MapProperty<String?, Number> = SimpleMapProperty()
        val localMap: ObservableMap<String?, Number> = ObservableCollections.observableHashMap()
        localMap[key1] = NumberClassCastException()
        localMap[key2] = NumberNullPointerException()
        localProperty.set(localMap)

        val binding = Bindings.shortValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        this.index.set(key1)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, ClassCastException::class.java)
        this.index.set(key2)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
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
        binding0.dispose()
        binding1.dispose()
        binding2.dispose()
    }

    @Test
    fun testStringValueAt_Constant_Exception() {
        val defaultData: String? = null
        val localProperty: MapProperty<String?, String?> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, String?> = MapClassCastException()
        val localMap2: ObservableMap<String?, String?> = MapNullPointerException()

        val binding = Bindings.stringValueAt(localProperty, key1)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty)

        localProperty.set(localMap1)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, ClassCastException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
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
        binding.dispose()
    }

    @Test
    fun testStringValueAt_Variable_Exception() {
        val defaultData: String? = null
        val localProperty: MapProperty<String?, String?> = SimpleMapProperty()
        val localMap1: ObservableMap<String?, String?> = MapClassCastException()
        val localMap2: ObservableMap<String?, String?> = MapNullPointerException()

        val binding = Bindings.stringValueAt(localProperty, this.index)
        DependencyUtils.checkDependencies(binding.dependencies, localProperty, this.index)

        localProperty.set(localMap1)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, ClassCastException::class.java)

        localProperty.set(localMap2)
        assertEquals(defaultData, binding.get())
        log.check(Level.WARNING, NullPointerException::class.java)
        binding.dispose()
    }

    private class NumberClassCastException : Number() {

        override fun toDouble(): Double {
            throw ClassCastException("For the test")
        }

        override fun toFloat(): Float {
            throw ClassCastException("For the test")
        }

        override fun toInt(): Int {
            throw ClassCastException("For the test")
        }

        override fun toLong(): Long {
            throw ClassCastException("For the test")
        }

        override fun toShort(): Short {
            throw ClassCastException("For the test")
        }

        override fun toByte(): Byte {
            throw ClassCastException("For the test")
        }

        override fun toChar(): Char {
            throw ClassCastException("For the test") // unused
        }

    }

    private class NumberNullPointerException : Number() {

        override fun toDouble(): Double {
            throw NullPointerException("For the test")
        }

        override fun toFloat(): Float {
            throw NullPointerException("For the test")
        }

        override fun toInt(): Int {
            throw NullPointerException("For the test")
        }

        override fun toLong(): Long {
            throw NullPointerException("For the test")
        }

        override fun toShort(): Short {
            throw NullPointerException("For the test")
        }

        override fun toByte(): Byte {
            throw NullPointerException("For the test")
        }

        override fun toChar(): Char {
            throw NullPointerException("For the test") // unused
        }

    }

    private class MapClassCastException<V> : ObservableMapWrapper<String?, V>(HashMap()) {

        override fun get(key: String?): V? {
            throw ClassCastException("For the test")
        }

    }

    private class MapNullPointerException<V> : ObservableMapWrapper<String?, V>(HashMap()) {

        override fun get(key: String?): V? {
            throw NullPointerException("For the test")
        }

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