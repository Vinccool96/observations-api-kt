package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.SetProperty
import io.github.vinccool96.observationskt.beans.property.SimpleSetProperty
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableSet
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class SetExpressionTest {

    private lateinit var opNull: SetProperty<Int>

    private lateinit var opEmpty: SetProperty<Int>

    private lateinit var op1: SetProperty<Int>

    private lateinit var op2: SetProperty<Int>

    @Before
    fun setUp() {
        this.opNull = SimpleSetProperty()
        this.opEmpty = SimpleSetProperty(ObservableCollections.observableSet())
        this.op1 = SimpleSetProperty(ObservableCollections.observableSet(data1_0))
        this.op2 = SimpleSetProperty(ObservableCollections.observableSet(data2_0, data2_1))
    }

    @Test
    fun testGetSize() {
        assertEquals(0, this.opNull.size)
        assertEquals(0, this.opEmpty.size)
        assertEquals(1, this.op1.size)
        assertEquals(2, this.op2.size)
    }

    @Test
    fun testIsEqualTo() {
        val emptySet: ObservableSet<Int> = ObservableCollections.observableSet(Collections.emptySet())
        val set1: ObservableSet<Int> = ObservableCollections.observableSet(data1_0)
        val set2: ObservableSet<Int> = ObservableCollections.observableSet(data2_0, data2_1)

        var binding: BooleanBinding = this.opNull.isEqualTo(emptySet)
        assertEquals(false, binding.get())
        binding = this.opNull.isEqualTo(set1)
        assertEquals(false, binding.get())
        binding = this.opNull.isEqualTo(set2)
        assertEquals(false, binding.get())

        binding = this.opEmpty.isEqualTo(emptySet)
        assertEquals(true, binding.get())
        binding = this.opEmpty.isEqualTo(set1)
        assertEquals(false, binding.get())
        binding = this.opEmpty.isEqualTo(set2)
        assertEquals(false, binding.get())

        binding = this.op1.isEqualTo(emptySet)
        assertEquals(false, binding.get())
        binding = this.op1.isEqualTo(set1)
        assertEquals(true, binding.get())
        binding = this.op1.isEqualTo(set2)
        assertEquals(false, binding.get())

        binding = this.op2.isEqualTo(emptySet)
        assertEquals(false, binding.get())
        binding = this.op2.isEqualTo(set1)
        assertEquals(false, binding.get())
        binding = this.op2.isEqualTo(set2)
        assertEquals(true, binding.get())
    }

    @Test
    fun testIsNotEqualTo() {
        val emptySet: ObservableSet<Int> = ObservableCollections.observableSet(Collections.emptySet())
        val set1: ObservableSet<Int> = ObservableCollections.observableSet(data1_0)
        val set2: ObservableSet<Int> = ObservableCollections.observableSet(data2_0, data2_1)

        var binding: BooleanBinding = this.opNull.isNotEqualTo(emptySet)
        assertEquals(true, binding.get())
        binding = this.opNull.isNotEqualTo(set1)
        assertEquals(true, binding.get())
        binding = this.opNull.isNotEqualTo(set2)
        assertEquals(true, binding.get())

        binding = this.opEmpty.isNotEqualTo(emptySet)
        assertEquals(false, binding.get())
        binding = this.opEmpty.isNotEqualTo(set1)
        assertEquals(true, binding.get())
        binding = this.opEmpty.isNotEqualTo(set2)
        assertEquals(true, binding.get())

        binding = this.op1.isNotEqualTo(emptySet)
        assertEquals(true, binding.get())
        binding = this.op1.isNotEqualTo(set1)
        assertEquals(false, binding.get())
        binding = this.op1.isNotEqualTo(set2)
        assertEquals(true, binding.get())

        binding = this.op2.isNotEqualTo(emptySet)
        assertEquals(true, binding.get())
        binding = this.op2.isNotEqualTo(set1)
        assertEquals(true, binding.get())
        binding = this.op2.isNotEqualTo(set2)
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
        assertEquals(Collections.emptySet<Int>().toString(), this.opEmpty.asString().get())
        assertEquals(Collections.singleton(data1_0).toString(), this.op1.asString().get())
    }

    @Test
    fun testIsEmpty() {
        assertTrue(this.opNull.isEmpty())
        assertTrue(this.opEmpty.isEmpty())
        assertFalse(this.op1.isEmpty())
        assertFalse(this.op2.isEmpty())
    }

    @Test
    fun testContains() {
        assertFalse(this.opNull.contains(data1_0))
        assertFalse(this.opNull.contains(data2_0))
        assertFalse(this.opNull.contains(data2_1))

        assertFalse(this.opEmpty.contains(data1_0))
        assertFalse(this.opEmpty.contains(data2_0))
        assertFalse(this.opEmpty.contains(data2_1))

        assertTrue(this.op1.contains(data1_0))
        assertFalse(this.op1.contains(data2_0))
        assertFalse(this.op1.contains(data2_1))

        assertFalse(this.op2.contains(data1_0))
        assertTrue(this.op2.contains(data2_0))
        assertTrue(this.op2.contains(data2_1))
    }

    @Test
    fun testIterator() {
        assertFalse(this.opNull.iterator().hasNext())
        assertFalse(this.opEmpty.iterator().hasNext())

        var iterator: Iterator<Int> = this.op1.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(data1_0, iterator.next())
        assertFalse(iterator.hasNext())

        iterator = this.op2.iterator()
        assertTrue(iterator.hasNext())
        when (iterator.next()) {
            data2_0 -> {
                assertTrue(iterator.hasNext())
                assertEquals(data2_1, iterator.next())
                assertFalse(iterator.hasNext())
            }
            data2_1 -> {
                assertTrue(iterator.hasNext())
                assertEquals(data2_0, iterator.next())
                assertFalse(iterator.hasNext())
            }
            else -> fail()
        }
    }

    companion object {

        private const val data1_0: Int = 7

        private const val data2_0: Int = 42

        private const val data2_1: Int = -3

    }

}