package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.ShortProperty
import io.github.vinccool96.observationskt.beans.property.SimpleShortProperty
import io.github.vinccool96.observationskt.beans.value.ObservableShortValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ShortExpressionTest {

    private var data: Short = 0

    private lateinit var op1: ShortProperty

    private var double1 = 0.0

    private var float1 = 0f

    private var long1: Long = 0L

    private var int1 = 0

    private var short1: Short = 0

    @Before
    fun setUp() {
        this.data = 3425
        this.op1 = SimpleShortProperty(this.data)
        this.double1 = -234.234
        this.float1 = 111.9f
        this.long1 = 2009234L
        this.int1 = -234734
        this.short1 = 23432
    }

    @Test
    fun testGetters() {
        assertEquals(this.data.toDouble(), this.op1.doubleValue, EPSILON.toDouble())
        assertEquals(this.data.toFloat(), this.op1.floatValue, EPSILON)
        assertEquals(this.data.toLong(), this.op1.longValue)
        assertEquals(this.data.toInt(), this.op1.intValue)
        assertEquals(this.data, this.op1.shortValue)
        assertEquals(this.data.toByte(), this.op1.byteValue)
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

        val binding5: IntBinding = this.op1 + this.short1
        assertEquals(this.data + this.short1, binding5.intValue)
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

        val binding5: IntBinding = this.op1 - this.short1
        assertEquals(this.data - this.short1, binding5.intValue)
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

        val binding5: IntBinding = this.op1 * this.short1
        assertEquals(this.data * this.short1, binding5.intValue)
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

        val binding5: IntBinding = this.op1 / this.short1
        assertEquals(this.data / this.short1, binding5.intValue)
    }

    @Test
    fun testAsObject() {
        val valueModel = ObservableShortValueStub()
        val exp: ObjectExpression<Short> = ShortExpression.shortExpression(valueModel).asObject()

        assertEquals(0, exp.value)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value)
        valueModel.set(this.short1)
        assertEquals(this.short1, exp.value)
        (exp as ObjectBinding<Short>).dispose()
    }

    @Test
    fun testFactory() {
        val valueModel = ObservableShortValueStub()
        val exp: ShortExpression = ShortExpression.shortExpression(valueModel)

        assertTrue(exp is ShortBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals((0).toShort(), exp.value)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value)
        valueModel.set(this.short1)
        assertEquals(this.short1, exp.value)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, ShortExpression.shortExpression(this.op1))
        exp.dispose()
    }

    @Test
    fun testObjectToFloat() {
        val valueModel: ObservableValueStub<Short?> = ObservableValueStub(null)
        val exp: ShortExpression = ShortExpression.shortExpression(valueModel)

        assertTrue(exp is ShortBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals((0).toShort(), exp.value)
        valueModel.set(this.data)
        assertEquals(this.data, exp.value)
        valueModel.set(this.short1)
        assertEquals(this.short1, exp.value)

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, ShortExpression.shortExpression(this.op1))
        exp.dispose()
    }

    companion object {

        private const val EPSILON: Float = 1e-6f

    }

}