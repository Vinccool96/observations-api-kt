package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.FloatProperty
import io.github.vinccool96.observationskt.beans.property.SimpleFloatProperty
import io.github.vinccool96.observationskt.beans.value.ObservableFloatValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FloatExpressionTest {

    private var data = 0.0f

    private lateinit var op1: FloatProperty

    private var double1 = 0.0

    private var float1 = 0f

    private var long1: Long = 0L

    private var int1 = 0

    private var short1: Short = 0

    @Before
    fun setUp() {
        this.data = 2.1f
        this.op1 = SimpleFloatProperty(this.data)
        this.double1 = -234.234
        this.float1 = 111.9f
        this.long1 = 2009234L
        this.int1 = -234734
        this.short1 = 9824
    }

    @Test
    fun testGetters() {
        assertEquals(this.data.toDouble(), this.op1.doubleValue, EPSILON.toDouble())
        assertEquals(this.data, this.op1.floatValue, EPSILON)
        assertEquals(this.data.toLong(), this.op1.longValue)
        assertEquals(this.data.toInt(), this.op1.intValue)
        assertEquals(this.data.toInt().toShort(), this.op1.shortValue)
        assertEquals(this.data.toInt().toByte(), this.op1.byteValue)
    }

    @Test
    fun testNegation() {
        val binding: FloatBinding = -this.op1
        assertEquals(-this.data, binding.floatValue, EPSILON)
    }

    @Test
    fun testPlus() {
        val binding1: DoubleBinding = this.op1 + this.double1
        assertEquals(this.data + this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 + this.float1
        assertEquals(this.data + this.float1, binding2.floatValue, EPSILON)

        val binding3: FloatBinding = this.op1 + this.long1
        assertEquals(this.data + this.long1, binding3.floatValue, EPSILON)

        val binding4: FloatBinding = this.op1 + this.int1
        assertEquals(this.data + this.int1, binding4.floatValue, EPSILON)

        val binding5: FloatBinding = this.op1 + this.short1
        assertEquals(this.data + this.short1, binding5.floatValue, EPSILON)
    }

    @Test
    fun testMinus() {
        val binding1: DoubleBinding = this.op1 - this.double1
        assertEquals(this.data - this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 - this.float1
        assertEquals(this.data - this.float1, binding2.floatValue, EPSILON)

        val binding3: FloatBinding = this.op1 - this.long1
        assertEquals(this.data - this.long1, binding3.floatValue, EPSILON)

        val binding4: FloatBinding = this.op1 - this.int1
        assertEquals(this.data - this.int1, binding4.floatValue, EPSILON)

        val binding5: FloatBinding = this.op1 - this.short1
        assertEquals(this.data - this.short1, binding5.floatValue, EPSILON)
    }

    @Test
    fun testTimes() {
        val binding1: DoubleBinding = this.op1 * this.double1
        assertEquals(this.data * this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 * this.float1
        assertEquals(this.data * this.float1, binding2.floatValue, EPSILON)

        val binding3: FloatBinding = this.op1 * this.long1
        assertEquals(this.data * this.long1, binding3.floatValue, EPSILON)

        val binding4: FloatBinding = this.op1 * this.int1
        assertEquals(this.data * this.int1, binding4.floatValue, EPSILON)

        val binding5: FloatBinding = this.op1 * this.short1
        assertEquals(this.data * this.short1, binding5.floatValue, EPSILON)
    }

    @Test
    fun testDividedBy() {
        val binding1: DoubleBinding = this.op1 / this.double1
        assertEquals(this.data / this.double1, binding1.doubleValue, EPSILON.toDouble())

        val binding2: FloatBinding = this.op1 / this.float1
        assertEquals(this.data / this.float1, binding2.floatValue, EPSILON)

        val binding3: FloatBinding = this.op1 / this.long1
        assertEquals(this.data / this.long1, binding3.floatValue, EPSILON)

        val binding4: FloatBinding = this.op1 / this.int1
        assertEquals(this.data / this.int1, binding4.floatValue, EPSILON)

        val binding5: FloatBinding = this.op1 / this.short1
        assertEquals(this.data / this.short1, binding5.floatValue, EPSILON)
    }

    @Test
    fun testAsObject() {
        val valueModel = ObservableFloatValueStub()
        val exp: ObjectExpression<Float> = FloatExpression.floatExpression(valueModel).asObject()

        assertEquals(0.0f, exp.value, EPSILON)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value, EPSILON)
        valueModel.set(this.float1)
        assertEquals(this.float1, exp.value, EPSILON)
        (exp as ObjectBinding<Float>).dispose()
    }

    @Test
    fun testFactory() {
        val valueModel = ObservableFloatValueStub()
        val exp: FloatExpression = FloatExpression.floatExpression(valueModel)

        assertTrue(exp is FloatBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(0.0f, exp.floatValue, EPSILON)
        valueModel.set(this.data)
        assertEquals(this.data, exp.floatValue, EPSILON)
        valueModel.set(this.float1)
        assertEquals(this.float1, exp.floatValue, EPSILON)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, FloatExpression.floatExpression(this.op1))
        exp.dispose()
    }

    @Test
    fun testObjectToFloat() {
        val valueModel: ObservableValueStub<Float?> = ObservableValueStub(null)
        val exp: FloatExpression = FloatExpression.floatExpression(valueModel)

        assertTrue(exp is FloatBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(0.0f, exp.floatValue, EPSILON)
        valueModel.set(this.data)
        assertEquals(this.data, exp.floatValue, EPSILON)
        valueModel.set(this.float1)
        assertEquals(this.float1, exp.floatValue, EPSILON)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, FloatExpression.floatExpression(this.op1))
        exp.dispose()
    }

    companion object {

        private const val EPSILON: Float = 1e-6f

    }

}