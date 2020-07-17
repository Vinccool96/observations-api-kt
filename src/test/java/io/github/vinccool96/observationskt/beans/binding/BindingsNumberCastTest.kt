package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.*
import io.github.vinccool96.observationskt.beans.value.ObservableNumberValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@RunWith(Parameterized::class)
class BindingsNumberCastTest(private val func: Functions) {

    private var double0: Double = 0.0

    private var float0: Float = 0.0f

    private var long0: Long = 0L

    private var int0: Int = 0

    private lateinit var double1: DoubleProperty

    private lateinit var float1: FloatProperty

    private lateinit var long1: LongProperty

    private lateinit var int1: IntProperty

    interface Functions {

        fun generateExpression(op1: ObservableNumberValue, op2: ObservableNumberValue): Binding<*>

        fun check(op1: Double, op2: Double, binding: Binding<*>)

    }

    @Before
    fun setUp() {
        this.double0 = 3.1415
        this.float0 = 2.71f
        this.long0 = 111L
        this.int0 = 42

        this.double1 = SimpleDoubleProperty(this.double0)
        this.float1 = SimpleFloatProperty(this.float0)
        this.long1 = SimpleLongProperty(this.long0)
        this.int1 = SimpleIntProperty(this.int0)
    }

    @Test
    fun testDouble() {
        var binding = this.func.generateExpression(this.double1, this.double1)
        assertTrue(binding is DoubleExpression || binding is BooleanExpression)
        this.func.check(this.double0, this.double0, binding)
        binding = this.func.generateExpression(this.double1, this.float1)
        assertTrue(binding is DoubleExpression || binding is BooleanExpression)
        this.func.check(this.double0, this.float0.toDouble(), binding)
        binding = this.func.generateExpression(this.double1, this.long1)
        assertTrue(binding is DoubleExpression || binding is BooleanExpression)
        this.func.check(this.double0, this.long0.toDouble(), binding)
        binding = this.func.generateExpression(this.double1, this.int1)
        assertTrue(binding is DoubleExpression || binding is BooleanExpression)
        this.func.check(this.double0, this.int0.toDouble(), binding)
    }

    @Test
    fun testFloat() {
        var binding = this.func.generateExpression(this.float1, this.double1)
        assertTrue(binding is DoubleExpression || binding is BooleanExpression)
        this.func.check(this.float0.toDouble(), this.double0, binding)
        binding = this.func.generateExpression(this.float1, this.float1)
        assertTrue(binding is FloatExpression || binding is BooleanExpression)
        this.func.check(this.float0.toDouble(), this.float0.toDouble(), binding)
        binding = this.func.generateExpression(this.float1, this.long1)
        assertTrue(binding is FloatExpression || binding is BooleanExpression)
        this.func.check(this.float0.toDouble(), this.long0.toDouble(), binding)
        binding = this.func.generateExpression(this.float1, this.int1)
        assertTrue(binding is FloatExpression || binding is BooleanExpression)
        this.func.check(this.float0.toDouble(), this.int0.toDouble(), binding)
    }

    @Test
    fun testLong() {
        var binding = this.func.generateExpression(this.long1, this.double1)
        assertTrue(binding is DoubleExpression || binding is BooleanExpression)
        this.func.check(this.long0.toDouble(), this.double0, binding)
        binding = this.func.generateExpression(this.long1, this.float1)
        assertTrue(binding is FloatExpression || binding is BooleanExpression)
        this.func.check(this.long0.toDouble(), this.float0.toDouble(), binding)
        binding = this.func.generateExpression(this.long1, this.long1)
        assertTrue(binding is LongExpression || binding is BooleanExpression)
        this.func.check(this.long0.toDouble(), this.long0.toDouble(), binding)
        binding = this.func.generateExpression(this.long1, this.int1)
        assertTrue(binding is LongExpression || binding is BooleanExpression)
        this.func.check(this.long0.toDouble(), this.int0.toDouble(), binding)
    }

    @Test
    fun testInt() {
        var binding = this.func.generateExpression(this.int1, this.double1)
        assertTrue(binding is DoubleExpression || binding is BooleanExpression)
        this.func.check(this.int0.toDouble(), this.double0, binding)
        binding = this.func.generateExpression(this.int1, this.float1)
        assertTrue(binding is FloatExpression || binding is BooleanExpression)
        this.func.check(this.int0.toDouble(), this.float0.toDouble(), binding)
        binding = this.func.generateExpression(this.int1, this.long1)
        assertTrue(binding is LongExpression || binding is BooleanExpression)
        this.func.check(this.int0.toDouble(), this.long0.toDouble(), binding)
        binding = this.func.generateExpression(this.int1, this.int1)
        assertTrue(binding is IntExpression || binding is BooleanExpression)
        this.func.check(this.int0.toDouble(), this.int0.toDouble(), binding)
    }

    companion object {

        private const val EPSILON: Double = 1e-5

        @Parameterized.Parameters
        @JvmStatic
        fun parameters(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(
                            object : Functions {
                                override fun generateExpression(op1: ObservableNumberValue,
                                        op2: ObservableNumberValue): Binding<*> {
                                    return Bindings.add(op1, op2)
                                }

                                override fun check(op1: Double, op2: Double, binding: Binding<*>) {
                                    assertTrue(binding is NumberExpression)
                                    assertEquals(op1 + op2, (binding as NumberExpression).doubleValue, EPSILON)
                                }
                            }
                    ),
                    arrayOf(
                            object : Functions {
                                override fun generateExpression(op1: ObservableNumberValue,
                                        op2: ObservableNumberValue): Binding<*> {
                                    return Bindings.multiply(op1, op2)
                                }

                                override fun check(op1: Double, op2: Double, binding: Binding<*>) {
                                    assertTrue(binding is NumberExpression)
                                    assertEquals(op1 * op2, (binding as NumberExpression).doubleValue, EPSILON)
                                }
                            }
                    ),
                    arrayOf(
                            object : Functions {
                                override fun generateExpression(op1: ObservableNumberValue,
                                        op2: ObservableNumberValue): Binding<*> {
                                    return Bindings.divide(op1, op2)
                                }

                                override fun check(op1: Double, op2: Double, binding: Binding<*>) {
                                    assertTrue(binding is NumberExpression)
                                    if (binding is DoubleExpression || binding is FloatExpression) {
                                        assertEquals(op1 / op2, (binding as NumberExpression).doubleValue,
                                                EPSILON)
                                    } else {
                                        assertEquals(op1.toLong() / op2.toLong(),
                                                (binding as NumberExpression).longValue)
                                    }
                                }
                            }
                    ),
                    arrayOf(
                            object : Functions {
                                override fun generateExpression(op1: ObservableNumberValue,
                                        op2: ObservableNumberValue): Binding<*> {
                                    return Bindings.min(op1, op2)
                                }

                                override fun check(op1: Double, op2: Double, binding: Binding<*>) {
                                    assertTrue(binding is NumberExpression)
                                    assertEquals(min(op1, op2), (binding as NumberExpression).doubleValue, EPSILON)
                                }
                            }
                    ),
                    arrayOf(
                            object : Functions {
                                override fun generateExpression(op1: ObservableNumberValue,
                                        op2: ObservableNumberValue): Binding<*> {
                                    return Bindings.max(op1, op2)
                                }

                                override fun check(op1: Double, op2: Double, binding: Binding<*>) {
                                    assertTrue(binding is NumberExpression)
                                    assertEquals(max(op1, op2), (binding as NumberExpression).doubleValue, EPSILON)
                                }
                            }
                    ),
                    arrayOf(
                            object : Functions {
                                override fun generateExpression(op1: ObservableNumberValue,
                                        op2: ObservableNumberValue): Binding<*> {
                                    return Bindings.equal(op1, op2)
                                }

                                override fun check(op1: Double, op2: Double, binding: Binding<*>) {
                                    assertTrue(binding is BooleanExpression)
                                    assertEquals(abs(op1 - op2) < EPSILON, (binding as BooleanExpression).get())
                                }
                            }
                    ),
                    arrayOf(
                            object : Functions {
                                override fun generateExpression(op1: ObservableNumberValue,
                                        op2: ObservableNumberValue): Binding<*> {
                                    return Bindings.greaterThan(op1, op2)
                                }

                                override fun check(op1: Double, op2: Double, binding: Binding<*>) {
                                    assertTrue(binding is BooleanExpression)
                                    assertEquals(op1 > op2, (binding as BooleanExpression).get())
                                }
                            }
                    ))
        }

    }

}