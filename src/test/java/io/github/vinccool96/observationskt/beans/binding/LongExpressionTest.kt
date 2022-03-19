package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.LongProperty
import io.github.vinccool96.observationskt.beans.property.SimpleLongProperty
import io.github.vinccool96.observationskt.beans.value.ObservableLongValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LongExpressionTest {

    private var data = 0L

    private lateinit var op1: LongProperty

    private var double1 = 0.0

    private var float1 = 0f

    private var long1: Long = 0L

    private var int1 = 0

    private var short1: Short = 0

    private var byte1: Byte = 0

    @BeforeTest
    fun setUp() {
        this.data = 34258L
        this.op1 = SimpleLongProperty(this.data)
        this.double1 = -234.234
        this.float1 = 111.9f
        this.long1 = 2009234L
        this.int1 = -234734
        this.short1 = 9824
        this.byte1 = 42
    }

    @Test
    fun testGetters() {
        assertEquals(this.data.toDouble(), this.op1.doubleValue, EPSILON.toDouble())
        assertEquals(this.data.toFloat(), this.op1.floatValue, EPSILON)
        assertEquals(this.data, this.op1.longValue)
        assertEquals(this.data.toInt(), this.op1.intValue)
        assertEquals(this.data.toShort(), this.op1.shortValue)
        assertEquals(this.data.toByte(), this.op1.byteValue)
    }

    @Test
    fun testNegation() {
        val binding: LongBinding = -this.op1
        assertEquals(-this.data, binding.longValue)
    }

    @Test
    fun testPlus() {
        val binding1: DoubleBinding = this.op1 + this.double1
        assertEquals(this.data + this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 + this.float1
        assertEquals(this.data + this.float1, binding2.floatValue, EPSILON)

        val binding3: LongBinding = this.op1 + this.long1
        assertEquals(this.data + this.long1, binding3.longValue)

        val binding4: LongBinding = this.op1 + this.int1
        assertEquals(this.data + this.int1, binding4.longValue)

        val binding5: LongBinding = this.op1 + this.short1
        assertEquals(this.data + this.short1, binding5.longValue)

        val binding6: LongBinding = this.op1 + this.byte1
        assertEquals(this.data + this.byte1, binding6.longValue)
    }

    @Test
    fun testMinus() {
        val binding1: DoubleBinding = this.op1 - this.double1
        assertEquals(this.data - this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 - this.float1
        assertEquals(this.data - this.float1, binding2.floatValue, EPSILON)

        val binding3: LongBinding = this.op1 - this.long1
        assertEquals(this.data - this.long1, binding3.longValue)

        val binding4: LongBinding = this.op1 - this.int1
        assertEquals(this.data - this.int1, binding4.longValue)

        val binding5: LongBinding = this.op1 - this.short1
        assertEquals(this.data - this.short1, binding5.longValue)

        val binding6: LongBinding = this.op1 - this.byte1
        assertEquals(this.data - this.byte1, binding6.longValue)
    }

    @Test
    fun testTimes() {
        val binding1: DoubleBinding = this.op1 * this.double1
        assertEquals(this.data * this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 * this.float1
        assertEquals(this.data * this.float1, binding2.floatValue, EPSILON)

        val binding3: LongBinding = this.op1 * this.long1
        assertEquals(this.data * this.long1, binding3.longValue)

        val binding4: LongBinding = this.op1 * this.int1
        assertEquals(this.data * this.int1, binding4.longValue)

        val binding5: LongBinding = this.op1 * this.short1
        assertEquals(this.data * this.short1, binding5.longValue)

        val binding6: LongBinding = this.op1 * this.byte1
        assertEquals(this.data * this.byte1, binding6.longValue)
    }

    @Test
    fun testDividedBy() {
        val binding1: DoubleBinding = this.op1 / this.double1
        assertEquals(this.data / this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 / this.float1
        assertEquals(this.data / this.float1, binding2.floatValue, EPSILON)

        val binding3: LongBinding = this.op1 / this.long1
        assertEquals(this.data / this.long1, binding3.longValue)

        val binding4: LongBinding = this.op1 / this.int1
        assertEquals(this.data / this.int1, binding4.longValue)

        val binding5: LongBinding = this.op1 / this.short1
        assertEquals(this.data / this.short1, binding5.longValue)

        val binding6: LongBinding = this.op1 / this.byte1
        assertEquals(this.data / this.byte1, binding6.longValue)
    }

    @Test
    fun testAsObject() {
        val valueModel = ObservableLongValueStub()
        val exp: ObjectExpression<Long> = LongExpression.longExpression(valueModel).asObject()

        assertEquals(0L, exp.value)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value)
        valueModel.set(this.long1)
        assertEquals(this.long1, exp.value)
        (exp as ObjectBinding<Long>).dispose()
    }

    @Test
    fun testFactory() {
        val valueModel = ObservableLongValueStub()
        val exp: LongExpression = LongExpression.longExpression(valueModel)

        assertTrue(exp is LongBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(0L, exp.value)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value)
        valueModel.set(this.long1)
        assertEquals(this.long1, exp.value)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, LongExpression.longExpression(this.op1))
        exp.dispose()
    }

    @Test
    fun testObjectToLong() {
        val valueModel: ObservableValueStub<Long?> = ObservableValueStub(null)
        val exp: LongExpression = LongExpression.longExpression(valueModel)

        assertTrue(exp is LongBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(0L, exp.value)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value)
        valueModel.set(this.long1)
        assertEquals(this.long1, exp.value)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, LongExpression.longExpression(this.op1))
        exp.dispose()
    }

    companion object {

        private const val EPSILON: Float = 1e-6f

    }

}