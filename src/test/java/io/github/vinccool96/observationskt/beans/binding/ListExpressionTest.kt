package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.*
import io.github.vinccool96.observationskt.beans.value.ObservableListValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.ErrorLoggingUtility
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.*

class ListExpressionTest {

    private lateinit var opNull: ListProperty<Int>

    private lateinit var opEmpty: ListProperty<Int>

    private lateinit var op1: ListProperty<Int>

    private lateinit var op2: ListProperty<Int>

    @Before
    fun setUp() {
        this.opNull = SimpleListProperty()
        this.opEmpty = SimpleListProperty(ObservableCollections.observableArrayList())
        this.op1 = SimpleListProperty(ObservableCollections.observableArrayList(data1_0))
        this.op2 = SimpleListProperty(ObservableCollections.observableArrayList(data2_0, data2_1))
    }

    @Test
    fun testSizeGet() {
        assertEquals(0, this.opNull.size)
        assertEquals(0, this.opEmpty.size)
        assertEquals(1, this.op1.size)
        assertEquals(2, this.op2.size)
    }

    @Test
    fun testValueAt_Constant() {
        assertNull(this.opNull.valueAt(0).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.opEmpty.valueAt(0).get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        assertEquals(data1_0, this.op1.valueAt(0).get())
        assertNull(this.op1.valueAt(1).get())
        log.checkFine(IndexOutOfBoundsException::class.java)

        assertEquals(data2_0, this.op2.valueAt(0).get())
        assertEquals(data2_1, this.op2.valueAt(1).get())
        assertNull(this.op2.valueAt(2).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testValueAt_Constant_NegativeIndex() {
        this.op1.valueAt(-1)
    }

    @Test
    fun testValueAt_Variable() {
        val index: IntProperty = SimpleIntProperty(-1)

        assertNull(this.opNull.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.opEmpty.valueAt(index).get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(this.op1.valueAt(index).get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(this.op2.valueAt(index).get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        index.set(0)
        assertNull(this.opNull.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.opEmpty.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(data1_0, this.op1.valueAt(index).get())
        assertEquals(data2_0, this.op2.valueAt(index).get())

        index.set(1)
        assertNull(this.opNull.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.opEmpty.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.op1.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(data2_1, this.op2.valueAt(index).get())

        index.set(2)
        assertNull(this.opNull.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.opEmpty.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.op1.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.op2.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
    }

    @Test
    fun testValueAt_Number() {
        val index: DoubleProperty = SimpleDoubleProperty(-1.1)

        assertNull(this.opNull.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.opEmpty.valueAt(index).get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(this.op1.valueAt(index).get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)
        assertNull(this.op2.valueAt(index).get())
        log.checkFine(ArrayIndexOutOfBoundsException::class.java)

        index.set(0.5)
        assertNull(this.opNull.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.opEmpty.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(data1_0, this.op1.valueAt(index).get())
        assertEquals(data2_0, this.op2.valueAt(index).get())

        index.set(1.8)
        assertNull(this.opNull.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.opEmpty.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.op1.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertEquals(data2_1, this.op2.valueAt(index).get())

        index.set(2.0000001)
        assertNull(this.opNull.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.opEmpty.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.op1.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
        assertNull(this.op2.valueAt(index).get())
        log.checkFine(IndexOutOfBoundsException::class.java)
    }

    @Test
    fun testIsEqualTo() {
        val emptyList: ObservableList<Int> = ObservableCollections.emptyObservableList()
        val list1: ObservableList<Int> = ObservableCollections.observableArrayList(data1_0)
        val list2: ObservableList<Int> = ObservableCollections.observableArrayList(data2_0, data2_1)

        var binding: BooleanBinding = this.opNull.isEqualTo(emptyList)
        assertEquals(false, binding.get())
        binding = this.opNull.isEqualTo(list1)
        assertEquals(false, binding.get())
        binding = this.opNull.isEqualTo(list2)
        assertEquals(false, binding.get())

        binding = this.opEmpty.isEqualTo(emptyList)
        assertEquals(true, binding.get())
        binding = this.opEmpty.isEqualTo(list1)
        assertEquals(false, binding.get())
        binding = this.opEmpty.isEqualTo(list2)
        assertEquals(false, binding.get())

        binding = this.op1.isEqualTo(emptyList)
        assertEquals(false, binding.get())
        binding = this.op1.isEqualTo(list1)
        assertEquals(true, binding.get())
        binding = this.op1.isEqualTo(list2)
        assertEquals(false, binding.get())

        binding = this.op2.isEqualTo(emptyList)
        assertEquals(false, binding.get())
        binding = this.op2.isEqualTo(list1)
        assertEquals(false, binding.get())
        binding = this.op2.isEqualTo(list2)
        assertEquals(true, binding.get())
    }

    @Test
    fun testIsNotEqualTo() {
        val emptyList: ObservableList<Int> = ObservableCollections.emptyObservableList()
        val list1: ObservableList<Int> = ObservableCollections.observableArrayList(data1_0)
        val list2: ObservableList<Int> = ObservableCollections.observableArrayList(data2_0, data2_1)

        var binding: BooleanBinding = this.opNull.isNotEqualTo(emptyList)
        assertEquals(true, binding.get())
        binding = this.opNull.isNotEqualTo(list1)
        assertEquals(true, binding.get())
        binding = this.opNull.isNotEqualTo(list2)
        assertEquals(true, binding.get())

        binding = this.opEmpty.isNotEqualTo(emptyList)
        assertEquals(false, binding.get())
        binding = this.opEmpty.isNotEqualTo(list1)
        assertEquals(true, binding.get())
        binding = this.opEmpty.isNotEqualTo(list2)
        assertEquals(true, binding.get())

        binding = this.op1.isNotEqualTo(emptyList)
        assertEquals(true, binding.get())
        binding = this.op1.isNotEqualTo(list1)
        assertEquals(false, binding.get())
        binding = this.op1.isNotEqualTo(list2)
        assertEquals(true, binding.get())

        binding = this.op2.isNotEqualTo(emptyList)
        assertEquals(true, binding.get())
        binding = this.op2.isNotEqualTo(list1)
        assertEquals(true, binding.get())
        binding = this.op2.isNotEqualTo(list2)
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
        assertEquals(emptyList<Int>().toString(), this.opEmpty.asString().get())
        assertEquals(listOf(data1_0).toString(), this.op1.asString().get())
        assertEquals(listOf(data2_0, data2_1).toString(), this.op2.asString().get())
    }

    @Test
    fun testSize() {
        assertEquals(0, this.opNull.size)
        assertEquals(0, this.opEmpty.size)
        assertEquals(1, this.op1.size)
        assertEquals(2, this.op2.size)
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
        assertFalse(data1_0 in this.opNull)
        assertFalse(data2_0 in this.opNull)
        assertFalse(data2_1 in this.opNull)

        assertFalse(data1_0 in this.opEmpty)
        assertFalse(data2_0 in this.opEmpty)
        assertFalse(data2_1 in this.opEmpty)

        assertTrue(data1_0 in this.op1)
        assertFalse(data2_0 in this.op1)
        assertFalse(data2_1 in this.op1)

        assertFalse(data1_0 in this.op2)
        assertTrue(data2_0 in this.op2)
        assertTrue(data2_1 in this.op2)
    }

    @Test
    fun testContainsAll() {
        assertTrue(this.opNull.containsAll(listOf()))
        assertFalse(this.opNull.containsAll(listOf(data1_0)))
        assertFalse(this.opNull.containsAll(setOf(data2_0, data2_1)))
        assertFalse(this.opNull.containsAll(listOf(data1_0, data2_0, data2_1)))

        assertTrue(this.opEmpty.containsAll(listOf()))
        assertFalse(this.opEmpty.containsAll(listOf(data1_0)))
        assertFalse(this.opEmpty.containsAll(setOf(data2_0, data2_1)))
        assertFalse(this.opEmpty.containsAll(listOf(data1_0, data2_0, data2_1)))

        assertTrue(this.op1.containsAll(listOf()))
        assertTrue(this.op1.containsAll(listOf(data1_0)))
        assertFalse(this.op1.containsAll(setOf(data2_0, data2_1)))
        assertFalse(this.op1.containsAll(listOf(data1_0, data2_0, data2_1)))

        assertTrue(this.op2.containsAll(listOf()))
        assertFalse(this.op2.containsAll(listOf(data1_0)))
        assertTrue(this.op2.containsAll(setOf(data2_0, data2_1)))
        assertFalse(this.op2.containsAll(listOf(data1_0, data2_0, data2_1)))
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
        assertEquals(data2_0, iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals(data2_1, iterator.next())
        assertFalse(iterator.hasNext())
    }

    @Test
    fun testToArray_NoArg() {
        assertTrue(emptyArray<Any>().contentDeepEquals(this.opNull.toTypedArray()))
        assertTrue(emptyArray<Any>().contentDeepEquals(this.opEmpty.toTypedArray()))
        assertTrue(arrayOf(data1_0).contentDeepEquals(this.op1.toTypedArray()))
        assertTrue(arrayOf(data2_0, data2_1).contentDeepEquals(this.op2.toTypedArray()))
    }

    @Test
    fun testObservableListValueToExpression() {
        val valueModel: ObservableListValueStub<Any> = ObservableListValueStub()
        val exp: ListExpression<Any> = ListExpression.listExpression(valueModel)
        val o1 = Any()
        val o2 = Any()
        val o3 = Any()

        assertTrue(exp is ListBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(null, exp.get())
        valueModel.set(ObservableCollections.observableArrayList(o1))
        assertEquals(ObservableCollections.singletonObservableList(o1), exp.get())
        valueModel.get()!!.add(o2)
        assertEquals(ObservableCollections.observableList(mutableListOf(o1, o2)), exp.get())
        exp.get()!!.add(o3)
        assertEquals(ObservableCollections.observableList(mutableListOf(o1, o2, o3)), valueModel.get())

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, ListExpression.listExpression(this.op1))
        exp.dispose()
    }

    companion object {

        private const val data1_0: Int = 7

        private const val data2_0: Int = 42

        private const val data2_1: Int = -3

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