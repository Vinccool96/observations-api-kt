package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.IntProperty
import io.github.vinccool96.observationskt.beans.property.MapProperty
import io.github.vinccool96.observationskt.beans.property.SimpleIntProperty
import io.github.vinccool96.observationskt.beans.property.SimpleMapProperty
import io.github.vinccool96.observationskt.beans.value.ObservableMapValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableMap
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.*

@Suppress("RedundantNullableReturnType")
class MapExpressionTest {

    private lateinit var opNull: MapProperty<Number?, Int>

    private lateinit var opEmpty: MapProperty<Number?, Int>

    private lateinit var op1: MapProperty<Number?, Int>

    private lateinit var op2: MapProperty<Number?, Int>

    @Before
    fun setUp() {
        this.opNull = SimpleMapProperty()
        this.opEmpty = SimpleMapProperty(ObservableCollections.observableMap(Collections.emptyMap()))
        this.op1 = SimpleMapProperty(ObservableCollections.singletonObservableMap(key1_0, data1_0))
        this.op2 = SimpleMapProperty(ObservableCollections.observableHashMap(key2_0 to data2_0, key2_1 to data2_1))
    }

    @Test
    fun testSize() {
        assertEquals(0, this.opNull.size)
        assertEquals(0, this.opEmpty.size)
        assertEquals(1, this.op1.size)
        assertEquals(2, this.op2.size)
    }

    @Test
    fun testValueAt_Constant() {
        assertNull(this.opNull.valueAt(0).get())
        assertNull(this.opEmpty.valueAt(0).get())

        assertEquals(data1_0, this.op1.valueAt(key1_0).get())
        assertNull(this.op1.valueAt(keyx).get())

        assertEquals(data2_0, this.op2.valueAt(key2_0).get())
        assertEquals(data2_1, this.op2.valueAt(key2_1).get())
        assertNull(this.op1.valueAt(keyx).get())
    }

    @Test
    fun testValueAt_Variable() {
        val index: IntProperty = SimpleIntProperty(keyx.toInt())

        assertNull(this.opNull.valueAt(index).get())
        assertNull(this.opEmpty.valueAt(index).get())
        assertNull(this.op1.valueAt(index).get())
        assertNull(this.op2.valueAt(index).get())

        index.set(key1_0.toInt())
        assertNull(this.opNull.valueAt(index).get())
        assertNull(this.opEmpty.valueAt(index).get())
        assertEquals(data1_0, this.op1.valueAt(index).get())
        assertEquals(data2_0, this.op2.valueAt(index).get())

        index.set(key2_1.toInt())
        assertNull(this.opNull.valueAt(index).get())
        assertNull(this.opEmpty.valueAt(index).get())
        assertNull(this.op1.valueAt(index).get())
        assertEquals(data2_1, this.op2.valueAt(index).get())
    }

    @Test
    fun testIsEqualTo() {
        val emptyMap: ObservableMap<Number?, Int> = ObservableCollections.observableMap(Collections.emptyMap())
        val map1: ObservableMap<Number?, Int> = ObservableCollections.singletonObservableMap(key1_0, data1_0)
        val map2: ObservableMap<Number?, Int> = ObservableCollections.observableHashMap(key2_0 to data2_0,
                key2_1 to data2_1)

        var binding: BooleanBinding = this.opNull.isEqualTo(emptyMap)
        assertEquals(false, binding.get())
        binding = this.opNull.isEqualTo(map1)
        assertEquals(false, binding.get())
        binding = this.opNull.isEqualTo(map2)
        assertEquals(false, binding.get())

        binding = this.opEmpty.isEqualTo(emptyMap)
        assertEquals(true, binding.get())
        binding = this.opEmpty.isEqualTo(map1)
        assertEquals(false, binding.get())
        binding = this.opEmpty.isEqualTo(map2)
        assertEquals(false, binding.get())

        binding = this.op1.isEqualTo(emptyMap)
        assertEquals(false, binding.get())
        binding = this.op1.isEqualTo(map1)
        assertEquals(true, binding.get())
        binding = this.op1.isEqualTo(map2)
        assertEquals(false, binding.get())

        binding = this.op2.isEqualTo(emptyMap)
        assertEquals(false, binding.get())
        binding = this.op2.isEqualTo(map1)
        assertEquals(false, binding.get())
        binding = this.op2.isEqualTo(map2)
        assertEquals(true, binding.get())
    }

    @Test
    fun testNotIsEqualTo() {
        val emptyMap: ObservableMap<Number?, Int> = ObservableCollections.observableMap(Collections.emptyMap())
        val map1: ObservableMap<Number?, Int> = ObservableCollections.singletonObservableMap(key1_0, data1_0)
        val map2: ObservableMap<Number?, Int> = ObservableCollections.observableHashMap(key2_0 to data2_0,
                key2_1 to data2_1)

        var binding: BooleanBinding = this.opNull.isNotEqualTo(emptyMap)
        assertEquals(true, binding.get())
        binding = this.opNull.isNotEqualTo(map1)
        assertEquals(true, binding.get())
        binding = this.opNull.isNotEqualTo(map2)
        assertEquals(true, binding.get())

        binding = this.opEmpty.isNotEqualTo(emptyMap)
        assertEquals(false, binding.get())
        binding = this.opEmpty.isNotEqualTo(map1)
        assertEquals(true, binding.get())
        binding = this.opEmpty.isNotEqualTo(map2)
        assertEquals(true, binding.get())

        binding = this.op1.isNotEqualTo(emptyMap)
        assertEquals(true, binding.get())
        binding = this.op1.isNotEqualTo(map1)
        assertEquals(false, binding.get())
        binding = this.op1.isNotEqualTo(map2)
        assertEquals(true, binding.get())

        binding = this.op2.isNotEqualTo(emptyMap)
        assertEquals(true, binding.get())
        binding = this.op2.isNotEqualTo(map1)
        assertEquals(true, binding.get())
        binding = this.op2.isNotEqualTo(map2)
        assertEquals(false, binding.get())
    }

    @Test
    fun testIsNull() {
        assertTrue(this.opNull.isNull().get())
        assertFalse(this.opEmpty.isNull().get())
        assertFalse(this.op1.isNull().get())
        assertFalse(this.op2.isNull().get())
    }

    @Test
    fun testIsNotNull() {
        assertFalse(this.opNull.isNotNull().get())
        assertTrue(this.opEmpty.isNotNull().get())
        assertTrue(this.op1.isNotNull().get())
        assertTrue(this.op2.isNotNull().get())
    }

    @Test
    fun testAsString() {
        assertEquals("null", this.opNull.asString().get())
        assertEquals(emptyMap<Number?, Int>().toString(), this.opEmpty.asString().get())
        assertEquals(mapOf(key1_0 to data1_0).toString(), this.op1.asString().get())
    }

    @Test
    fun testIsEmpty() {
        assertTrue(this.opNull.isEmpty())
        assertTrue(this.opEmpty.isEmpty())
        assertFalse(this.op1.isEmpty())
        assertFalse(this.op2.isEmpty())
    }

    @Test
    fun testContainsKey() {
        assertFalse(this.opNull.containsKey(key1_0))
        assertFalse(this.opNull.containsKey(key2_0))
        assertFalse(this.opNull.containsKey(key2_1))

        assertFalse(this.opEmpty.containsKey(key1_0))
        assertFalse(this.opEmpty.containsKey(key2_0))
        assertFalse(this.opEmpty.containsKey(key2_1))

        assertTrue(this.op1.containsKey(key1_0))
        assertFalse(this.op1.containsKey(key2_1))

        assertTrue(this.op2.containsKey(key2_0))
        assertTrue(this.op2.containsKey(key2_1))
    }

    @Test
    fun testContainsValue() {
        assertFalse(this.opNull.containsValue(data1_0))
        assertFalse(this.opNull.containsValue(data2_0))
        assertFalse(this.opNull.containsValue(data2_1))

        assertFalse(this.opEmpty.containsValue(data1_0))
        assertFalse(this.opEmpty.containsValue(data2_0))
        assertFalse(this.opEmpty.containsValue(data2_1))

        assertTrue(this.op1.containsValue(data1_0))
        assertFalse(this.op1.containsValue(data2_0))
        assertFalse(this.op1.containsValue(data2_1))

        assertFalse(this.op2.containsValue(data1_0))
        assertTrue(this.op2.containsValue(data2_0))
        assertTrue(this.op2.containsValue(data2_1))
    }

    @Test
    fun testObservableMapValueToExpression() {
        val valueModel = ObservableMapValueStub<Number?, Int>()
        val exp: MapExpression<Number?, Int> = MapExpression.mapExpression(valueModel)
        val k1: Number? = 1.0
        val k2: Number? = 2.0f
        val k3: Number? = 3L
        val v1 = 4
        val v2 = 5
        val v3 = 6

        assertTrue(exp is MapBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(null, exp.get())
        valueModel.set(ObservableCollections.observableHashMap(k1 to v1))
        assertEquals(ObservableCollections.singletonObservableMap(k1, v1), exp.get())
        valueModel[k2] = v2
        assertEquals(ObservableCollections.observableHashMap(k1 to v1, k2 to v2), exp.get())
        exp[k3] = v3
        assertEquals(ObservableCollections.observableHashMap(k1 to v1, k2 to v2, k3 to v3), exp.get())

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, MapExpression.mapExpression(this.op1))
        exp.dispose()
    }

    companion object {

        private val key1_0: Number = 4711

        private val key2_0: Number = 4711

        private val key2_1: Number = 4712

        private val keyx: Number = 4710

        private const val data1_0: Int = 7

        private const val data2_0: Int = 42

        private const val data2_1: Int = -3

    }

}