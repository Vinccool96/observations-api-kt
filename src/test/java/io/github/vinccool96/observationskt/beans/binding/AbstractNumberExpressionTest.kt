package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.DoubleProperty
import io.github.vinccool96.observationskt.beans.property.IntProperty
import io.github.vinccool96.observationskt.beans.property.SimpleDoubleProperty
import io.github.vinccool96.observationskt.beans.property.SimpleIntProperty
import io.github.vinccool96.observationskt.beans.value.ObservableDoubleValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableFloatValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableIntValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableLongValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AbstractNumberExpressionTest {

    private var data1: Double = 0.0

    private var data2: Int = 0

    private lateinit var op1: DoubleProperty

    private lateinit var op2: IntProperty

    private var double1 = 0.0

    private var float1 = 0F

    private var long1: Long = 0

    private var int1 = 0

    private var short1: Short = 0

    private var byte1: Byte = 0

    @Before
    fun setUp() {
        data1 = 90224.8923
        data2 = -13
        op1 = SimpleDoubleProperty(data1)
        op2 = SimpleIntProperty(data2)
        double1 = -234.234
        float1 = 111.9F
        long1 = 2009234L
        int1 = -234734
        short1 = 9824
        byte1 = -123
    }

    @Test
    fun testArithmetic() {
        var binding: NumberBinding = op1 + op2
        org.junit.Assert.assertEquals(data1 + data2, binding.doubleValue, EPSILON)

        binding = op1 - op2
        org.junit.Assert.assertEquals(data1 - data2, binding.doubleValue, EPSILON)

        binding = op1 * op2
        org.junit.Assert.assertEquals(data1 * data2, binding.doubleValue, EPSILON)

        binding = op1 / op2
        org.junit.Assert.assertEquals(data1 / data2, binding.doubleValue, EPSILON)
    }

    @Test
    fun testEquals() {
        var binding: BooleanBinding = op1.isEqualTo(op1, EPSILON)
        assertTrue(binding.get())

        binding = op2.isEqualTo(op2)
        assertTrue(binding.get())

        binding = op1.isEqualTo(op2, EPSILON)
        assertFalse(binding.get())

        binding = op1.isEqualTo(data1, EPSILON)
        assertTrue(binding.get())

        binding = op1.isEqualTo(data2, EPSILON)
        assertFalse(binding.get())

        binding = op1.isEqualTo(double1, EPSILON)
        assertFalse(binding.get())

        binding = op1.isEqualTo(float1, EPSILON)
        assertFalse(binding.get())

        binding = op1.isEqualTo(long1, EPSILON)
        assertFalse(binding.get())

        binding = op1.isEqualTo(long1)
        assertFalse(binding.get())

        binding = op1.isEqualTo(int1, EPSILON)
        assertFalse(binding.get())

        binding = op1.isEqualTo(int1)
        assertFalse(binding.get())

        binding = op1.isEqualTo(short1.toInt(), EPSILON)
        assertFalse(binding.get())

        binding = op1.isEqualTo(short1.toInt())
        assertFalse(binding.get())

        binding = op1.isEqualTo(byte1.toInt(), EPSILON)
        assertFalse(binding.get())

        binding = op1.isEqualTo(byte1.toInt())
        assertFalse(binding.get())
    }

    @Test
    fun testNotEquals() {
        var binding: BooleanBinding = op1.isNotEqualTo(op1, EPSILON)
        assertFalse(binding.get())

        binding = op2.isNotEqualTo(op2)
        assertFalse(binding.get())

        binding = op1.isNotEqualTo(op2, EPSILON)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(data1, EPSILON)
        assertFalse(binding.get())

        binding = op1.isNotEqualTo(data2, EPSILON)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(double1, EPSILON)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(float1, EPSILON)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(long1, EPSILON)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(long1)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(int1, EPSILON)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(int1)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(short1.toInt(), EPSILON)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(short1.toInt())
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(byte1.toInt(), EPSILON)
        assertTrue(binding.get())

        binding = op1.isNotEqualTo(byte1.toInt())
        assertTrue(binding.get())
    }

    @Test
    fun testGreater() {
        var binding: BooleanBinding = op1.greaterThan(op1)
        assertEquals(data1 > data1, binding.get())

        binding = op1.greaterThan(op2)
        assertEquals(data1 > data2, binding.get())

        binding = op2.greaterThan(op1)
        assertEquals(data2 > data1, binding.get())

        binding = op2.greaterThan(op2)
        assertEquals(data2 > data2, binding.get())

        binding = op1.greaterThan(data1)
        assertEquals(data1 > data1, binding.get())

        binding = op1.greaterThan(data2)
        assertEquals(data1 > data2, binding.get())

        binding = op2.greaterThan(data1)
        assertEquals(data2 > data1, binding.get())

        binding = op2.greaterThan(data2)
        assertEquals(data2 > data2, binding.get())

        binding = op1.greaterThan(double1)
        assertEquals(data1 > double1, binding.get())

        binding = op1.greaterThan(float1)
        assertEquals(data1 > float1, binding.get())

        binding = op1.greaterThan(long1)
        assertEquals(data1 > long1, binding.get())

        binding = op1.greaterThan(int1)
        assertEquals(data1 > int1, binding.get())

        binding = op1.greaterThan(short1.toInt())
        assertEquals(data1 > short1, binding.get())

        binding = op1.greaterThan(byte1.toInt())
        assertEquals(data1 > byte1, binding.get())
    }

    @Test
    fun testLesser() {
        var binding: BooleanBinding = op1.lessThan(op1)
        assertEquals(data1 < data1, binding.get())

        binding = op1.lessThan(op2)
        assertEquals(data1 < data2, binding.get())

        binding = op2.lessThan(op1)
        assertEquals(data2 < data1, binding.get())

        binding = op2.lessThan(op2)
        assertEquals(data2 < data2, binding.get())

        binding = op1.lessThan(data1)
        assertEquals(data1 < data1, binding.get())

        binding = op1.lessThan(data2)
        assertEquals(data1 < data2, binding.get())

        binding = op2.lessThan(data1)
        assertEquals(data2 < data1, binding.get())

        binding = op2.lessThan(data2)
        assertEquals(data2 < data2, binding.get())

        binding = op1.lessThan(double1)
        assertEquals(data1 < double1, binding.get())

        binding = op1.lessThan(float1)
        assertEquals(data1 < float1, binding.get())

        binding = op1.lessThan(long1)
        assertEquals(data1 < long1, binding.get())

        binding = op1.lessThan(int1)
        assertEquals(data1 < int1, binding.get())

        binding = op1.lessThan(short1.toInt())
        assertEquals(data1 < short1, binding.get())

        binding = op1.lessThan(byte1.toInt())
        assertEquals(data1 < byte1, binding.get())
    }

    @Test
    fun testGreaterOrEqual() {
        var binding: BooleanBinding = op1.greaterThanOrEqualTo(op1)
        assertEquals(data1 >= data1, binding.get())

        binding = op1.greaterThanOrEqualTo(op2)
        assertEquals(data1 >= data2, binding.get())

        binding = op2.greaterThanOrEqualTo(op1)
        assertEquals(data2 >= data1, binding.get())

        binding = op2.greaterThanOrEqualTo(op2)
        assertEquals(data2 >= data2, binding.get())

        binding = op1.greaterThanOrEqualTo(data1)
        assertEquals(data1 >= data1, binding.get())

        binding = op1.greaterThanOrEqualTo(data2)
        assertEquals(data1 >= data2, binding.get())

        binding = op2.greaterThanOrEqualTo(data1)
        assertEquals(data2 >= data1, binding.get())

        binding = op2.greaterThanOrEqualTo(data2)
        assertEquals(data2 >= data2, binding.get())

        binding = op1.greaterThanOrEqualTo(double1)
        assertEquals(data1 >= double1, binding.get())

        binding = op1.greaterThanOrEqualTo(float1)
        assertEquals(data1 >= float1, binding.get())

        binding = op1.greaterThanOrEqualTo(long1)
        assertEquals(data1 >= long1, binding.get())

        binding = op1.greaterThanOrEqualTo(int1)
        assertEquals(data1 >= int1, binding.get())

        binding = op1.greaterThanOrEqualTo(short1.toInt())
        assertEquals(data1 >= short1, binding.get())

        binding = op1.greaterThanOrEqualTo(byte1.toInt())
        assertEquals(data1 >= byte1, binding.get())
    }

    @Test
    fun testLesserOrEqual() {
        var binding: BooleanBinding = op1.lessThanOrEqualTo(op1)
        assertEquals(data1 <= data1, binding.get())

        binding = op1.lessThanOrEqualTo(op2)
        assertEquals(data1 <= data2, binding.get())

        binding = op2.lessThanOrEqualTo(op1)
        assertEquals(data2 <= data1, binding.get())

        binding = op2.lessThanOrEqualTo(op2)
        assertEquals(data2 <= data2, binding.get())

        binding = op1.lessThanOrEqualTo(data1)
        assertEquals(data1 <= data1, binding.get())

        binding = op1.lessThanOrEqualTo(data2)
        assertEquals(data1 <= data2, binding.get())

        binding = op2.lessThanOrEqualTo(data1)
        assertEquals(data2 <= data1, binding.get())

        binding = op2.lessThanOrEqualTo(data2)
        assertEquals(data2 <= data2, binding.get())

        binding = op1.lessThanOrEqualTo(double1)
        assertEquals(data1 <= double1, binding.get())

        binding = op1.lessThanOrEqualTo(float1)
        assertEquals(data1 <= float1, binding.get())

        binding = op1.lessThanOrEqualTo(long1)
        assertEquals(data1 <= long1, binding.get())

        binding = op1.lessThanOrEqualTo(int1)
        assertEquals(data1 <= int1, binding.get())

        binding = op1.lessThanOrEqualTo(short1.toInt())
        assertEquals(data1 <= short1, binding.get())

        binding = op1.lessThanOrEqualTo(byte1.toInt())
        assertEquals(data1 <= byte1, binding.get())
    }

    @Test
    fun testFactory() {
        assertEquals(op1, NumberExpressionBase.numberExpression(op1))

        val double2 = ObservableDoubleValueStub()
        double2.set(double1)
        var exp: NumberExpression = NumberExpressionBase.numberExpression(double2)
        assertTrue(exp is DoubleBinding)
        assertEquals(ObservableCollections.singletonObservableList(double2), exp.dependencies)
        org.junit.Assert.assertEquals(double1, exp.doubleValue, EPSILON)
        double2.set(0.0)
        org.junit.Assert.assertEquals(0.0, exp.doubleValue, EPSILON)

        val float2 = ObservableFloatValueStub()
        float2.set(float1)
        exp = NumberExpressionBase.numberExpression(float2)
        assertTrue(exp is FloatBinding)
        assertEquals(ObservableCollections.singletonObservableList(float2), exp.dependencies)
        org.junit.Assert.assertEquals(float1, exp.floatValue, EPSILON.toFloat())
        float2.set(0.0F)
        org.junit.Assert.assertEquals(0.0F, exp.floatValue, EPSILON.toFloat())

        val long2 = ObservableLongValueStub()
        long2.set(long1)
        exp = NumberExpressionBase.numberExpression(long2)
        assertTrue(exp is LongBinding)
        assertEquals(ObservableCollections.singletonObservableList(long2), exp.dependencies)
        assertEquals(long1, exp.longValue)
        long2.set(0L)
        assertEquals(0L, exp.longValue)

        val int2 = ObservableIntValueStub()
        int2.set(int1)
        exp = NumberExpressionBase.numberExpression(int2)
        assertTrue(exp is IntBinding)
        assertEquals(ObservableCollections.singletonObservableList(int2), exp.dependencies)
        assertEquals(int1, exp.intValue)
        int2.set(0)
        assertEquals(0, exp.intValue)
    }

    @Test
    fun testAsString() {
        val i: IntProperty = SimpleIntProperty()
        val s: StringBinding = i.asString()
        DependencyUtils.checkDependencies(s.dependencies, i)
        assertEquals("0", s.get())
        i.set(42)
        assertEquals("42", s.get())
    }

    @Test
    fun testAsString_Format() {
        val defaultLocale: Locale = Locale.getDefault()
        try {
            // checking German default
            Locale.setDefault(Locale.GERMAN)
            val d: DoubleProperty = SimpleDoubleProperty(PI)
            val s: StringBinding = d.asString("%.4f")
            DependencyUtils.checkDependencies(s.dependencies, d)
            assertEquals("3,1416", s.get())
            d.set(E)
            assertEquals("2,7183", s.get())

            // checking US default
            Locale.setDefault(Locale.US)
            d.set(PI)
            assertEquals("3.1416", s.get())
            d.set(E)
            assertEquals("2.7183", s.get())
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    @Test
    fun testAsString_LocaleFormat() {
        // checking German default
        val d: DoubleProperty = SimpleDoubleProperty(PI)
        var s: StringBinding = d.asString(Locale.GERMAN, "%.4f")
        DependencyUtils.checkDependencies(s.dependencies, d)
        assertEquals("3,1416", s.get())
        d.set(E)
        assertEquals("2,7183", s.get())

        // checking US default
        s = d.asString(Locale.US, "%.4f")
        DependencyUtils.checkDependencies(s.dependencies, d)
        d.set(PI)
        assertEquals("3.1416", s.get())
        d.set(E)
        assertEquals("2.7183", s.get())
    }

    companion object {

        private const val EPSILON: Double = 1e-6

    }

}