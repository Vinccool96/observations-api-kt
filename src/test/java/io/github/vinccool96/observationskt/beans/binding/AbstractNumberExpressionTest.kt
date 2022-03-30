package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.property.*
import io.github.vinccool96.observationskt.beans.value.*
import io.github.vinccool96.observationskt.collections.ObservableCollections
import java.util.*
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.*

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

    @BeforeTest
    fun setUp() {
        this.data1 = 90224.8923
        this.data2 = -13
        this.op1 = SimpleDoubleProperty(this.data1)
        this.op2 = SimpleIntProperty(this.data2)
        this.double1 = -234.234
        this.float1 = 111.9F
        this.long1 = 2009234L
        this.int1 = -234734
        this.short1 = 9824
        this.byte1 = -123
    }

    private fun testArithmetic(op1: NumberExpressionBase, op2: NumberExpressionBase, binding: NumberBinding, op: OP) {
        val expected = when (op) {
            OP.PL -> when (op1) {
                is SimpleDoubleProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.double1 + this.double1
                    is SimpleFloatProperty -> this.double1 + this.float1
                    is SimpleIntProperty -> this.double1 + this.int1
                    is SimpleLongProperty -> this.double1 + this.long1
                    is SimpleShortProperty -> this.double1 + this.short1
                    else -> 0.0
                }
                is SimpleFloatProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.float1 + this.double1
                    is SimpleFloatProperty -> this.float1 + this.float1
                    is SimpleIntProperty -> this.float1 + this.int1
                    is SimpleLongProperty -> this.float1 + this.long1
                    is SimpleShortProperty -> this.float1 + this.short1
                    else -> 0.0
                }
                is SimpleIntProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.int1 + this.double1
                    is SimpleFloatProperty -> this.int1 + this.float1
                    is SimpleIntProperty -> this.int1 + this.int1
                    is SimpleLongProperty -> this.int1 + this.long1
                    is SimpleShortProperty -> this.int1 + this.short1
                    else -> 0.0
                }
                is SimpleLongProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.long1 + this.double1
                    is SimpleFloatProperty -> this.long1 + this.float1
                    is SimpleIntProperty -> this.long1 + this.int1
                    is SimpleLongProperty -> this.long1 + this.long1
                    is SimpleShortProperty -> this.long1 + this.short1
                    else -> 0.0
                }
                is SimpleShortProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.short1 + this.double1
                    is SimpleFloatProperty -> this.short1 + this.float1
                    is SimpleIntProperty -> this.short1 + this.int1
                    is SimpleLongProperty -> this.short1 + this.long1
                    is SimpleShortProperty -> this.short1 + this.short1
                    else -> 0.0
                }
                else -> 0.0
            }
            OP.MI -> when (op1) {
                is SimpleDoubleProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.double1 - this.double1
                    is SimpleFloatProperty -> this.double1 - this.float1
                    is SimpleIntProperty -> this.double1 - this.int1
                    is SimpleLongProperty -> this.double1 - this.long1
                    is SimpleShortProperty -> this.double1 - this.short1
                    else -> 0.0
                }
                is SimpleFloatProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.float1 - this.double1
                    is SimpleFloatProperty -> this.float1 - this.float1
                    is SimpleIntProperty -> this.float1 - this.int1
                    is SimpleLongProperty -> this.float1 - this.long1
                    is SimpleShortProperty -> this.float1 - this.short1
                    else -> 0.0
                }
                is SimpleIntProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.int1 - this.double1
                    is SimpleFloatProperty -> this.int1 - this.float1
                    is SimpleIntProperty -> this.int1 - this.int1
                    is SimpleLongProperty -> this.int1 - this.long1
                    is SimpleShortProperty -> this.int1 - this.short1
                    else -> 0.0
                }
                is SimpleLongProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.long1 - this.double1
                    is SimpleFloatProperty -> this.long1 - this.float1
                    is SimpleIntProperty -> this.long1 - this.int1
                    is SimpleLongProperty -> this.long1 - this.long1
                    is SimpleShortProperty -> this.long1 - this.short1
                    else -> 0.0
                }
                is SimpleShortProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.short1 - this.double1
                    is SimpleFloatProperty -> this.short1 - this.float1
                    is SimpleIntProperty -> this.short1 - this.int1
                    is SimpleLongProperty -> this.short1 - this.long1
                    is SimpleShortProperty -> this.short1 - this.short1
                    else -> 0.0
                }
                else -> 0.0
            }
            OP.MU -> when (op1) {
                is SimpleDoubleProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.double1 * this.double1
                    is SimpleFloatProperty -> this.double1 * this.float1
                    is SimpleIntProperty -> this.double1 * this.int1
                    is SimpleLongProperty -> this.double1 * this.long1
                    is SimpleShortProperty -> this.double1 * this.short1
                    else -> 0.0
                }
                is SimpleFloatProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.float1 * this.double1
                    is SimpleFloatProperty -> this.float1 * this.float1
                    is SimpleIntProperty -> this.float1 * this.int1
                    is SimpleLongProperty -> this.float1 * this.long1
                    is SimpleShortProperty -> this.float1 * this.short1
                    else -> 0.0
                }
                is SimpleIntProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.int1 * this.double1
                    is SimpleFloatProperty -> this.int1 * this.float1
                    is SimpleIntProperty -> this.int1 * this.int1
                    is SimpleLongProperty -> this.int1 * this.long1
                    is SimpleShortProperty -> this.int1 * this.short1
                    else -> 0.0
                }
                is SimpleLongProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.long1 * this.double1
                    is SimpleFloatProperty -> this.long1 * this.float1
                    is SimpleIntProperty -> this.long1 * this.int1
                    is SimpleLongProperty -> this.long1 * this.long1
                    is SimpleShortProperty -> this.long1 * this.short1
                    else -> 0.0
                }
                is SimpleShortProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.short1 * this.double1
                    is SimpleFloatProperty -> this.short1 * this.float1
                    is SimpleIntProperty -> this.short1 * this.int1
                    is SimpleLongProperty -> this.short1 * this.long1
                    is SimpleShortProperty -> this.short1 * this.short1
                    else -> 0.0
                }
                else -> 0.0
            }
            OP.DI -> when (op1) {
                is SimpleDoubleProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.double1 / this.double1
                    is SimpleFloatProperty -> this.double1 / this.float1
                    is SimpleIntProperty -> this.double1 / this.int1
                    is SimpleLongProperty -> this.double1 / this.long1
                    is SimpleShortProperty -> this.double1 / this.short1
                    else -> 0.0
                }
                is SimpleFloatProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.float1 / this.double1
                    is SimpleFloatProperty -> this.float1 / this.float1
                    is SimpleIntProperty -> this.float1 / this.int1
                    is SimpleLongProperty -> this.float1 / this.long1
                    is SimpleShortProperty -> this.float1 / this.short1
                    else -> 0.0
                }
                is SimpleIntProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.int1 / this.double1
                    is SimpleFloatProperty -> this.int1 / this.float1
                    is SimpleIntProperty -> this.int1 / this.int1
                    is SimpleLongProperty -> this.int1 / this.long1
                    is SimpleShortProperty -> this.int1 / this.short1
                    else -> 0.0
                }
                is SimpleLongProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.long1 / this.double1
                    is SimpleFloatProperty -> this.long1 / this.float1
                    is SimpleIntProperty -> this.long1 / this.int1
                    is SimpleLongProperty -> this.long1 / this.long1
                    is SimpleShortProperty -> this.long1 / this.short1
                    else -> 0.0
                }
                is SimpleShortProperty -> when (op2) {
                    is SimpleDoubleProperty -> this.short1 / this.double1
                    is SimpleFloatProperty -> this.short1 / this.float1
                    is SimpleIntProperty -> this.short1 / this.int1
                    is SimpleLongProperty -> this.short1 / this.long1
                    is SimpleShortProperty -> this.short1 / this.short1
                    else -> 0.0
                }
                else -> 0.0
            }
        }
        when (binding) {
            is DoubleBinding -> assertEquals(expected.toDouble(), binding.get(), EPSILON)
            is FloatBinding -> assertEquals(expected.toFloat(), binding.get(), EPSILON_FLOAT)
            is IntBinding -> assertEquals(expected.toInt(), binding.get())
            is LongBinding -> assertEquals(expected.toLong(), binding.get())
        }
    }

    @Test
    fun testArithmetic() {
        val ops = listOf(SimpleDoubleProperty(this.double1), SimpleFloatProperty(this.float1),
                SimpleIntProperty(this.int1), SimpleLongProperty(this.long1), SimpleShortProperty(this.short1))
        for (op1 in ops) {
            for (op2 in ops) {
                testArithmetic(op1, op2, op1 + op2, OP.PL)

                testArithmetic(op1, op2, op1 - op2, OP.MI)

                testArithmetic(op1, op2, op1 * op2, OP.MU)

                testArithmetic(op1, op2, op1 / op2, OP.DI)
            }
        }
    }

    @Test
    fun testEquals() {
        var binding: BooleanBinding = this.op1.isEqualTo(this.op1, EPSILON)
        assertTrue(binding.get())

        binding = this.op2.isEqualTo(this.op2)
        assertTrue(binding.get())

        binding = this.op1.isEqualTo(this.op2, EPSILON)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.data1, EPSILON)
        assertTrue(binding.get())

        binding = this.op1.isEqualTo(this.data2, EPSILON)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.double1, EPSILON)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.float1, EPSILON)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.long1, EPSILON)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.long1)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.int1, EPSILON)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.int1)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.short1, EPSILON)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.short1)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.byte1, EPSILON)
        assertFalse(binding.get())

        binding = this.op1.isEqualTo(this.byte1)
        assertFalse(binding.get())
    }

    @Test
    fun testNotEquals() {
        var binding: BooleanBinding = this.op1.isNotEqualTo(this.op1, EPSILON)
        assertFalse(binding.get())

        binding = this.op2.isNotEqualTo(this.op2)
        assertFalse(binding.get())

        binding = this.op1.isNotEqualTo(this.op2, EPSILON)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.data1, EPSILON)
        assertFalse(binding.get())

        binding = this.op1.isNotEqualTo(this.data2, EPSILON)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.double1, EPSILON)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.float1, EPSILON)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.long1, EPSILON)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.long1)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.int1, EPSILON)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.int1)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.short1, EPSILON)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.short1)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.byte1, EPSILON)
        assertTrue(binding.get())

        binding = this.op1.isNotEqualTo(this.byte1)
        assertTrue(binding.get())
    }

    @Test
    fun testGreater() {
        var binding: BooleanBinding = this.op1.greaterThan(this.op1)
        assertEquals(this.data1 > this.data1, binding.get())

        binding = this.op1.greaterThan(this.op2)
        assertEquals(this.data1 > this.data2, binding.get())

        binding = this.op2.greaterThan(this.op1)
        assertEquals(this.data2 > this.data1, binding.get())

        binding = this.op2.greaterThan(this.op2)
        assertEquals(this.data2 > this.data2, binding.get())

        binding = this.op1.greaterThan(this.data1)
        assertEquals(this.data1 > this.data1, binding.get())

        binding = this.op1.greaterThan(this.data2)
        assertEquals(this.data1 > this.data2, binding.get())

        binding = this.op2.greaterThan(this.data1)
        assertEquals(this.data2 > this.data1, binding.get())

        binding = this.op2.greaterThan(this.data2)
        assertEquals(this.data2 > this.data2, binding.get())

        binding = this.op1.greaterThan(this.double1)
        assertEquals(this.data1 > this.double1, binding.get())

        binding = this.op1.greaterThan(this.float1)
        assertEquals(this.data1 > this.float1, binding.get())

        binding = this.op1.greaterThan(this.long1)
        assertEquals(this.data1 > this.long1, binding.get())

        binding = this.op1.greaterThan(this.int1)
        assertEquals(this.data1 > this.int1, binding.get())

        binding = this.op1.greaterThan(this.short1)
        assertEquals(this.data1 > this.short1, binding.get())

        binding = this.op1.greaterThan(this.byte1)
        assertEquals(this.data1 > this.byte1, binding.get())
    }

    @Test
    fun testLesser() {
        var binding: BooleanBinding = this.op1.lessThan(this.op1)
        assertEquals(this.data1 < this.data1, binding.get())

        binding = this.op1.lessThan(this.op2)
        assertEquals(this.data1 < this.data2, binding.get())

        binding = this.op2.lessThan(this.op1)
        assertEquals(this.data2 < this.data1, binding.get())

        binding = this.op2.lessThan(this.op2)
        assertEquals(this.data2 < this.data2, binding.get())

        binding = this.op1.lessThan(this.data1)
        assertEquals(this.data1 < this.data1, binding.get())

        binding = this.op1.lessThan(this.data2)
        assertEquals(this.data1 < this.data2, binding.get())

        binding = this.op2.lessThan(this.data1)
        assertEquals(this.data2 < this.data1, binding.get())

        binding = this.op2.lessThan(this.data2)
        assertEquals(this.data2 < this.data2, binding.get())

        binding = this.op1.lessThan(this.double1)
        assertEquals(this.data1 < this.double1, binding.get())

        binding = this.op1.lessThan(this.float1)
        assertEquals(this.data1 < this.float1, binding.get())

        binding = this.op1.lessThan(this.long1)
        assertEquals(this.data1 < this.long1, binding.get())

        binding = this.op1.lessThan(this.int1)
        assertEquals(this.data1 < this.int1, binding.get())

        binding = this.op1.lessThan(this.short1)
        assertEquals(this.data1 < this.short1, binding.get())

        binding = this.op1.lessThan(this.byte1)
        assertEquals(this.data1 < this.byte1, binding.get())
    }

    @Test
    fun testGreaterOrEqual() {
        var binding: BooleanBinding = this.op1.greaterThanOrEqualTo(this.op1)
        assertEquals(this.data1 >= this.data1, binding.get())

        binding = this.op1.greaterThanOrEqualTo(this.op2)
        assertEquals(this.data1 >= this.data2, binding.get())

        binding = this.op2.greaterThanOrEqualTo(this.op1)
        assertEquals(this.data2 >= this.data1, binding.get())

        binding = this.op2.greaterThanOrEqualTo(this.op2)
        assertEquals(this.data2 >= this.data2, binding.get())

        binding = this.op1.greaterThanOrEqualTo(this.data1)
        assertEquals(this.data1 >= this.data1, binding.get())

        binding = this.op1.greaterThanOrEqualTo(this.data2)
        assertEquals(this.data1 >= this.data2, binding.get())

        binding = this.op2.greaterThanOrEqualTo(this.data1)
        assertEquals(this.data2 >= this.data1, binding.get())

        binding = this.op2.greaterThanOrEqualTo(this.data2)
        assertEquals(this.data2 >= this.data2, binding.get())

        binding = this.op1.greaterThanOrEqualTo(this.double1)
        assertEquals(this.data1 >= this.double1, binding.get())

        binding = this.op1.greaterThanOrEqualTo(this.float1)
        assertEquals(this.data1 >= this.float1, binding.get())

        binding = this.op1.greaterThanOrEqualTo(this.long1)
        assertEquals(this.data1 >= this.long1, binding.get())

        binding = this.op1.greaterThanOrEqualTo(this.int1)
        assertEquals(this.data1 >= this.int1, binding.get())

        binding = this.op1.greaterThanOrEqualTo(this.short1)
        assertEquals(this.data1 >= this.short1, binding.get())

        binding = this.op1.greaterThanOrEqualTo(this.byte1)
        assertEquals(this.data1 >= this.byte1, binding.get())
    }

    @Test
    fun testLesserOrEqual() {
        var binding: BooleanBinding = this.op1.lessThanOrEqualTo(this.op1)
        assertEquals(this.data1 <= this.data1, binding.get())

        binding = this.op1.lessThanOrEqualTo(this.op2)
        assertEquals(this.data1 <= this.data2, binding.get())

        binding = this.op2.lessThanOrEqualTo(this.op1)
        assertEquals(this.data2 <= this.data1, binding.get())

        binding = this.op2.lessThanOrEqualTo(this.op2)
        assertEquals(this.data2 <= this.data2, binding.get())

        binding = this.op1.lessThanOrEqualTo(this.data1)
        assertEquals(this.data1 <= this.data1, binding.get())

        binding = this.op1.lessThanOrEqualTo(this.data2)
        assertEquals(this.data1 <= this.data2, binding.get())

        binding = this.op2.lessThanOrEqualTo(this.data1)
        assertEquals(this.data2 <= this.data1, binding.get())

        binding = this.op2.lessThanOrEqualTo(this.data2)
        assertEquals(this.data2 <= this.data2, binding.get())

        binding = this.op1.lessThanOrEqualTo(this.double1)
        assertEquals(this.data1 <= this.double1, binding.get())

        binding = this.op1.lessThanOrEqualTo(this.float1)
        assertEquals(this.data1 <= this.float1, binding.get())

        binding = this.op1.lessThanOrEqualTo(this.long1)
        assertEquals(this.data1 <= this.long1, binding.get())

        binding = this.op1.lessThanOrEqualTo(this.int1)
        assertEquals(this.data1 <= this.int1, binding.get())

        binding = this.op1.lessThanOrEqualTo(this.short1)
        assertEquals(this.data1 <= this.short1, binding.get())

        binding = this.op1.lessThanOrEqualTo(this.byte1)
        assertEquals(this.data1 <= this.byte1, binding.get())
    }

    @Test
    fun testFactory() {
        assertEquals(this.op1, NumberExpressionBase.numberExpression(this.op1))

        val double2 = ObservableDoubleValueStub()
        double2.set(this.double1)
        var exp: NumberExpression = NumberExpressionBase.numberExpression(double2)
        assertTrue(exp is DoubleBinding)
        assertEquals(ObservableCollections.singletonObservableList(double2), exp.dependencies)
        assertEquals(this.double1, exp.doubleValue, EPSILON)
        double2.set(0.0)
        assertEquals(0.0, exp.doubleValue, EPSILON)

        val float2 = ObservableFloatValueStub()
        float2.set(this.float1)
        exp = NumberExpressionBase.numberExpression(float2)
        assertTrue(exp is FloatBinding)
        assertEquals(ObservableCollections.singletonObservableList(float2), exp.dependencies)
        assertEquals(this.float1, exp.floatValue, EPSILON.toFloat())
        float2.set(0.0F)
        assertEquals(0.0F, exp.floatValue, EPSILON.toFloat())

        val long2 = ObservableLongValueStub()
        long2.set(this.long1)
        exp = NumberExpressionBase.numberExpression(long2)
        assertTrue(exp is LongBinding)
        assertEquals(ObservableCollections.singletonObservableList(long2), exp.dependencies)
        assertEquals(this.long1, exp.longValue)
        long2.set(0L)
        assertEquals(0L, exp.longValue)

        val int2 = ObservableIntValueStub()
        int2.set(this.int1)
        exp = NumberExpressionBase.numberExpression(int2)
        assertTrue(exp is IntBinding)
        assertEquals(ObservableCollections.singletonObservableList(int2), exp.dependencies)
        assertEquals(this.int1, exp.intValue)
        int2.set(0)
        assertEquals(0, exp.intValue)

        val short2 = ObservableShortValueStub()
        short2.set(this.short1)
        exp = NumberExpressionBase.numberExpression(short2)
        assertTrue(exp is ShortBinding)
        assertEquals(ObservableCollections.singletonObservableList(short2), exp.dependencies)
        assertEquals(this.short1, exp.shortValue)
        short2.set(0)
        assertEquals(0, exp.shortValue)
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

    @Test
    fun testFactoryOther() {
        assertFailsWith<IllegalArgumentException> {
            NumberExpressionBase.numberExpression(OtherNumberValue())
        }
    }

    private enum class OP {

        PL,

        MI,

        MU,

        DI

    }

    private class OtherNumberValue : ObservableNumberValue {

        override val doubleValue: Double
            get() = fail("Not in use")

        override val floatValue: Float
            get() = fail("Not in use")

        override val intValue: Int
            get() = fail("Not in use")

        override val longValue: Long
            get() = fail("Not in use")

        override val shortValue: Short
            get() = fail("Not in use")

        override val byteValue: Byte
            get() = fail("Not in use")

        override val value: Number
            get() = fail("Not in use")

        override fun addListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun removeListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun hasListener(listener: InvalidationListener): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: ChangeListener<in Number?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in Number?>) {
            fail("Not in use")
        }

        override fun hasListener(listener: ChangeListener<in Number?>): Boolean {
            fail("Not in use")
        }

    }

    companion object {

        private const val EPSILON: Double = 1e-6

        private const val EPSILON_FLOAT: Float = 1e-6f

    }

}