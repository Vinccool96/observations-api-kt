package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.IntProperty
import io.github.vinccool96.observationskt.beans.property.SimpleIntProperty
import io.github.vinccool96.observationskt.beans.value.ObservableIntValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class IntExpressionTest {

    private var data = 0

    private lateinit var op1: IntProperty

    private var double1 = 0.0

    private var float1 = 0f

    private var long1: Long = 0L

    private var int1 = 0

    @Before
    fun setUp() {
        this.data = 3425
        this.op1 = SimpleIntProperty(this.data)
        this.double1 = -234.234
        this.float1 = 111.9f
        this.long1 = 2009234L
        this.int1 = -234734
    }

    @Test
    fun testGetters() {
        assertEquals(this.data.toDouble(), this.op1.doubleValue, EPSILON.toDouble())
        assertEquals(this.data.toFloat(), this.op1.floatValue, EPSILON)
        assertEquals(this.data.toLong(), this.op1.longValue)
        assertEquals(this.data, this.op1.intValue)
    }

    @Test
    fun testNegation() {
        val binding: IntBinding = -this.op1
        assertEquals(-this.data, binding.intValue)
    }

    @Test
    fun testPlus() {
        val binding1: DoubleBinding = this.op1 + this.double1
        assertEquals(this.data + this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 + this.float1
        assertEquals(this.data + this.float1, binding2.floatValue, EPSILON)

        val binding3: LongBinding = this.op1 + this.long1
        assertEquals(this.data + this.long1, binding3.longValue)

        val binding4: IntBinding = this.op1 + this.int1
        assertEquals(this.data + this.int1, binding4.intValue)
    }

    @Test
    fun testMinus() {
        val binding1: DoubleBinding = this.op1 - this.double1
        assertEquals(this.data - this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 - this.float1
        assertEquals(this.data - this.float1, binding2.floatValue, EPSILON)

        val binding3: LongBinding = this.op1 - this.long1
        assertEquals(this.data - this.long1, binding3.longValue)

        val binding4: IntBinding = this.op1 - this.int1
        assertEquals(this.data - this.int1, binding4.intValue)
    }

    @Test
    fun testTimes() {
        val binding1: DoubleBinding = this.op1 * this.double1
        assertEquals(this.data * this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 * this.float1
        assertEquals(this.data * this.float1, binding2.floatValue, EPSILON)

        val binding3: LongBinding = this.op1 * this.long1
        assertEquals(this.data * this.long1, binding3.longValue)

        val binding4: IntBinding = this.op1 * this.int1
        assertEquals(this.data * this.int1, binding4.intValue)
    }

    @Test
    fun testDividedBy() {
        val binding1: DoubleBinding = this.op1 / this.double1
        assertEquals(this.data / this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 / this.float1
        assertEquals(this.data / this.float1, binding2.floatValue, EPSILON)

        val binding3: LongBinding = this.op1 / this.long1
        assertEquals(this.data / this.long1, binding3.longValue)

        val binding4: IntBinding = this.op1 / this.int1
        assertEquals(this.data / this.int1, binding4.intValue)
    }

    @Test
    fun testAsObject() {
        val valueModel = ObservableIntValueStub()
        val exp: ObjectExpression<Int> = IntExpression.intExpression(valueModel).asObject()

        assertEquals(0, exp.value)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value)
        valueModel.set(this.int1)
        assertEquals(this.int1, exp.value)
        (exp as ObjectBinding<Int>).dispose()
    }

    @Test
    fun testFactory() {
        val valueModel = ObservableIntValueStub()
        val exp: IntExpression = IntExpression.intExpression(valueModel)

        assertTrue(exp is IntBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(0, exp.value)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value)
        valueModel.set(this.int1)
        assertEquals(this.int1, exp.value)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, IntExpression.intExpression(this.op1))
        exp.dispose()
    }

    @Test
    fun testObjectToFloat() {
        val valueModel: ObservableValueStub<Int?> = ObservableValueStub(null)
        val exp: IntExpression = IntExpression.intExpression(valueModel)

        assertTrue(exp is IntBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(0, exp.value)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value)
        valueModel.set(this.int1)
        assertEquals(this.int1, exp.value)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, IntExpression.intExpression(this.op1))
        exp.dispose()
    }

    companion object {

        private const val EPSILON: Float = 1e-6f

    }

}