package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.DoubleProperty
import io.github.vinccool96.observationskt.beans.property.SimpleDoubleProperty
import io.github.vinccool96.observationskt.beans.value.ObservableDoubleValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DoubleExpressionTest {

    private var data = 0.0

    private lateinit var op1: DoubleProperty

    private var double1 = 0.0

    private var float1 = 0f

    private var long1: Long = 0L

    private var int1 = 0

    private var short1: Short = 0

    private var byte1: Byte = 0

    @BeforeTest
    fun setUp() {
        this.data = -67.0975
        this.op1 = SimpleDoubleProperty(this.data)
        this.double1 = -234.234
        this.float1 = 111.9f
        this.long1 = 2009234L
        this.int1 = -234734
        this.short1 = 9824
        this.byte1 = 42
    }

    @Test
    fun testGetters() {
        assertEquals(this.data, this.op1.doubleValue, EPSILON)
        assertEquals(this.data.toFloat(), this.op1.floatValue, EPSILON.toFloat())
        assertEquals(this.data.toLong(), this.op1.longValue)
        assertEquals(this.data.toInt(), this.op1.intValue)
        assertEquals(this.data.toLong().toShort(), this.op1.shortValue)
        assertEquals(this.data.toLong().toByte(), this.op1.byteValue)
    }

    @Test
    fun testNegation() {
        val binding: DoubleBinding = -this.op1
        assertEquals(-this.data, binding.doubleValue, EPSILON)
    }

    @Test
    fun testPlus() {
        val binding1: DoubleBinding = this.op1 + this.double1
        assertEquals(this.data + this.double1, binding1.doubleValue, EPSILON)

        val binding2: DoubleBinding = this.op1 + this.float1
        assertEquals(this.data + this.float1, binding2.doubleValue, EPSILON)

        val binding3: DoubleBinding = this.op1 + this.long1
        assertEquals(this.data + this.long1, binding3.doubleValue, EPSILON)

        val binding4: DoubleBinding = this.op1 + this.int1
        assertEquals(this.data + this.int1, binding4.doubleValue, EPSILON)

        val binding5: DoubleBinding = this.op1 + this.short1
        assertEquals(this.data + this.short1, binding5.doubleValue, EPSILON)

        val binding6: DoubleBinding = this.op1 + this.byte1
        assertEquals(this.data + this.byte1, binding6.doubleValue, EPSILON)
    }

    @Test
    fun testMinus() {
        val binding1: DoubleBinding = this.op1 - this.double1
        assertEquals(this.data - this.double1, binding1.doubleValue, EPSILON)

        val binding2: DoubleBinding = this.op1 - this.float1
        assertEquals(this.data - this.float1, binding2.doubleValue, EPSILON)

        val binding3: DoubleBinding = this.op1 - this.long1
        assertEquals(this.data - this.long1, binding3.doubleValue, EPSILON)

        val binding4: DoubleBinding = this.op1 - this.int1
        assertEquals(this.data - this.int1, binding4.doubleValue, EPSILON)

        val binding5: DoubleBinding = this.op1 - this.short1
        assertEquals(this.data - this.short1, binding5.doubleValue, EPSILON)

        val binding6: DoubleBinding = this.op1 - this.byte1
        assertEquals(this.data - this.byte1, binding6.doubleValue, EPSILON)
    }

    @Test
    fun testTimes() {
        val binding1: DoubleBinding = this.op1 * this.double1
        assertEquals(this.data * this.double1, binding1.doubleValue, EPSILON)

        val binding2: DoubleBinding = this.op1 * this.float1
        assertEquals(this.data * this.float1, binding2.doubleValue, EPSILON)

        val binding3: DoubleBinding = this.op1 * this.long1
        assertEquals(this.data * this.long1, binding3.doubleValue, EPSILON)

        val binding4: DoubleBinding = this.op1 * this.int1
        assertEquals(this.data * this.int1, binding4.doubleValue, EPSILON)

        val binding5: DoubleBinding = this.op1 * this.short1
        assertEquals(this.data * this.short1, binding5.doubleValue, EPSILON)

        val binding6: DoubleBinding = this.op1 * this.byte1
        assertEquals(this.data * this.byte1, binding6.doubleValue, EPSILON)
    }

    @Test
    fun testDividedBy() {
        val binding1: DoubleBinding = this.op1 / this.double1
        assertEquals(this.data / this.double1, binding1.doubleValue, EPSILON)

        val binding2: DoubleBinding = this.op1 / this.float1
        assertEquals(this.data / this.float1, binding2.doubleValue, EPSILON)

        val binding3: DoubleBinding = this.op1 / this.long1
        assertEquals(this.data / this.long1, binding3.doubleValue, EPSILON)

        val binding4: DoubleBinding = this.op1 / this.int1
        assertEquals(this.data / this.int1, binding4.doubleValue, EPSILON)

        val binding5: DoubleBinding = this.op1 / this.short1
        assertEquals(this.data / this.short1, binding5.doubleValue, EPSILON)

        val binding6: DoubleBinding = this.op1 / this.byte1
        assertEquals(this.data / this.byte1, binding6.doubleValue, EPSILON)
    }

    @Test
    fun testAsObject() {
        val valueModel = ObservableDoubleValueStub()
        val exp: ObjectExpression<Double> = DoubleExpression.doubleExpression(valueModel).asObject()

        assertEquals(0.0, exp.value, EPSILON)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value, EPSILON)
        valueModel.set(this.double1)
        assertEquals(this.double1, exp.value, EPSILON)
        (exp as ObjectBinding<Double>).dispose()
    }

    @Test
    fun testFactory() {
        val valueModel = ObservableDoubleValueStub()
        val exp: DoubleExpression = DoubleExpression.doubleExpression(valueModel)

        assertTrue(exp is DoubleBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(0.0, exp.doubleValue, EPSILON)
        valueModel.set(this.data)
        assertEquals(this.data, exp.doubleValue, EPSILON)
        valueModel.set(this.double1)
        assertEquals(this.double1, exp.doubleValue, EPSILON)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, DoubleExpression.doubleExpression(this.op1))
        exp.dispose()
    }

    @Test
    fun testObjectToDouble() {
        val valueModel: ObservableValueStub<Double?> = ObservableValueStub(null)
        val exp: DoubleExpression = DoubleExpression.doubleExpression(valueModel)

        assertTrue(exp is DoubleBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(0.0, exp.doubleValue, EPSILON)
        valueModel.set(this.data)
        assertEquals(this.data, exp.doubleValue, EPSILON)
        valueModel.set(this.double1)
        assertEquals(this.double1, exp.doubleValue, EPSILON)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, DoubleExpression.doubleExpression(this.op1))
        exp.dispose()
    }

    companion object {

        private const val EPSILON: Double = 1e-6

    }

}