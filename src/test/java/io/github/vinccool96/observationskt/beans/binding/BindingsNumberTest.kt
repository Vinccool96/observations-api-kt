package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.*
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class BindingsNumberTest(private val op: NumberExpression) {

    private lateinit var binding: NumberBinding

    @After
    fun tearDown() {
        if (this::binding.isInitialized) {
            this.binding.dispose()
        }
    }

    @Test
    fun testNegate() {
        this.binding = -this.op
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(negate(this.op.value), this.binding.value)
    }

    @Test
    fun testAdd_Double() {
        this.binding = this.op + DOUBLE
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(add(this.op.value, DOUBLE), this.binding.value)
    }

    @Test
    fun testAdd_Float() {
        this.binding = this.op + FLOAT
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(add(this.op.value, FLOAT), this.binding.value)
    }

    @Test
    fun testAdd_Int() {
        this.binding = this.op + INT
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(add(this.op.value, INT), this.binding.value)
    }

    @Test
    fun testAdd_Long() {
        this.binding = this.op + LONG
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(add(this.op.value, LONG), this.binding.value)
    }

    @Test
    fun testSub_Double() {
        this.binding = this.op - DOUBLE
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(sub(this.op.value, DOUBLE), this.binding.value)
    }

    @Test
    fun testSub_Float() {
        this.binding = this.op - FLOAT
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(sub(this.op.value, FLOAT), this.binding.value)
    }

    @Test
    fun testSub_Int() {
        this.binding = this.op - INT
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(sub(this.op.value, INT), this.binding.value)
    }

    @Test
    fun testSub_Long() {
        this.binding = this.op - LONG
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(sub(this.op.value, LONG), this.binding.value)
    }

    @Test
    fun testMul_Double() {
        this.binding = this.op * DOUBLE
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(mul(this.op.value, DOUBLE), this.binding.value)
    }

    @Test
    fun testMul_Float() {
        this.binding = this.op * FLOAT
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(mul(this.op.value, FLOAT), this.binding.value)
    }

    @Test
    fun testMul_Int() {
        this.binding = this.op * INT
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(mul(this.op.value, INT), this.binding.value)
    }

    @Test
    fun testMul_Long() {
        this.binding = this.op * LONG
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(mul(this.op.value, LONG), this.binding.value)
    }

    @Test
    fun testDiv_Double() {
        this.binding = this.op / DOUBLE
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(div(this.op.value, DOUBLE), this.binding.value)
    }

    @Test
    fun testDiv_Float() {
        this.binding = this.op / FLOAT
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(div(this.op.value, FLOAT), this.binding.value)
    }

    @Test
    fun testDiv_Int() {
        this.binding = this.op / INT
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(div(this.op.value, INT), this.binding.value)
    }

    @Test
    fun testDiv_Long() {
        this.binding = this.op / LONG
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(div(this.op.value, LONG), this.binding.value)
    }

    @Test
    fun testEq_Double() {
        val binding = this.op.isEqualTo(DOUBLE, EPSILON)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(eq(this.op.value, DOUBLE), binding.value)
        binding.dispose()
    }

    @Test
    fun testEq_Float() {
        val binding = this.op.isEqualTo(FLOAT, EPSILON)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(eq(this.op.value, FLOAT), binding.value)
        binding.dispose()
    }

    @Test
    fun testEq_Int() {
        val binding = this.op.isEqualTo(INT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(eq(this.op.value, INT), binding.value)
        binding.dispose()
    }

    @Test
    fun testEq_Long() {
        val binding = this.op.isEqualTo(LONG)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(eq(this.op.value, LONG), binding.value)
        binding.dispose()
    }

    @Test
    fun testNeq_Double() {
        val binding = this.op.isNotEqualTo(DOUBLE, EPSILON)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(neq(this.op.value, DOUBLE), binding.value)
        binding.dispose()
    }

    @Test
    fun testNeq_Float() {
        val binding = this.op.isNotEqualTo(FLOAT, EPSILON)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(neq(this.op.value, FLOAT), binding.value)
        binding.dispose()
    }

    @Test
    fun testNeq_Int() {
        val binding = this.op.isNotEqualTo(INT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(neq(this.op.value, INT), binding.value)
        binding.dispose()
    }

    @Test
    fun testNeq_Long() {
        val binding = this.op.isNotEqualTo(LONG)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(neq(this.op.value, LONG), binding.value)
        binding.dispose()
    }

    @Test
    fun testGt_Double() {
        val binding = this.op.greaterThan(DOUBLE)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(gt(this.op.value, DOUBLE), binding.value)
        binding.dispose()
    }

    @Test
    fun testGt_Float() {
        val binding = this.op.greaterThan(FLOAT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(gt(this.op.value, FLOAT), binding.value)
        binding.dispose()
    }

    @Test
    fun testGt_Int() {
        val binding = this.op.greaterThan(INT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(gt(this.op.value, INT), binding.value)
        binding.dispose()
    }

    @Test
    fun testGt_Long() {
        val binding = this.op.greaterThan(LONG)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(gt(this.op.value, LONG), binding.value)
        binding.dispose()
    }

    @Test
    fun testLt_Double() {
        val binding = this.op.lessThan(DOUBLE)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(lt(this.op.value, DOUBLE), binding.value)
        binding.dispose()
    }

    @Test
    fun testLt_Float() {
        val binding = this.op.lessThan(FLOAT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(lt(this.op.value, FLOAT), binding.value)
        binding.dispose()
    }

    @Test
    fun testLt_Int() {
        val binding = this.op.lessThan(INT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(lt(this.op.value, INT), binding.value)
        binding.dispose()
    }

    @Test
    fun testLt_Long() {
        val binding = this.op.lessThan(LONG)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(lt(this.op.value, LONG), binding.value)
        binding.dispose()
    }

    @Test
    fun testGte_Double() {
        val binding = this.op.greaterThanOrEqualTo(DOUBLE)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(gte(this.op.value, DOUBLE), binding.value)
        binding.dispose()
    }

    @Test
    fun testGte_Float() {
        val binding = this.op.greaterThanOrEqualTo(FLOAT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(gte(this.op.value, FLOAT), binding.value)
        binding.dispose()
    }

    @Test
    fun testGte_Int() {
        val binding = this.op.greaterThanOrEqualTo(INT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(gte(this.op.value, INT), binding.value)
        binding.dispose()
    }

    @Test
    fun testGte_Long() {
        val binding = this.op.greaterThanOrEqualTo(LONG)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(gte(this.op.value, LONG), binding.value)
        binding.dispose()
    }

    @Test
    fun testLte_Double() {
        val binding = this.op.lessThanOrEqualTo(DOUBLE)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(lte(this.op.value, DOUBLE), binding.value)
        binding.dispose()
    }

    @Test
    fun testLte_Float() {
        val binding = this.op.lessThanOrEqualTo(FLOAT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(lte(this.op.value, FLOAT), binding.value)
        binding.dispose()
    }

    @Test
    fun testLte_Int() {
        val binding = this.op.lessThanOrEqualTo(INT)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(lte(this.op.value, INT), binding.value)
        binding.dispose()
    }

    @Test
    fun testLte_Long() {
        val binding = this.op.lessThanOrEqualTo(LONG)
        DependencyUtils.checkDependencies(binding.dependencies, this.op)
        assertEquals(lte(this.op.value, LONG), binding.value)
        binding.dispose()
    }

    @Test
    fun testMin_Double() {
        this.binding = Bindings.min(this.op, DOUBLE)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(min(this.op.value, DOUBLE), this.binding.value)
    }

    @Test
    fun testMin_Float() {
        this.binding = Bindings.min(this.op, FLOAT)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(min(this.op.value, FLOAT), this.binding.value)
    }

    @Test
    fun testMin_Int() {
        this.binding = Bindings.min(this.op, INT)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(min(this.op.value, INT), this.binding.value)
    }

    @Test
    fun testMin_Long() {
        this.binding = Bindings.min(this.op, LONG)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(min(this.op.value, LONG), this.binding.value)
    }

    @Test
    fun testMin_Short() {
        this.binding = Bindings.min(this.op, SHORT)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(min(this.op.value, SHORT), this.binding.value)
    }

    @Test
    fun testMin_Byte() {
        this.binding = Bindings.min(this.op, BYTE)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(min(this.op.value, BYTE), this.binding.value)
    }

    @Test
    fun testMax_Double() {
        this.binding = Bindings.max(this.op, DOUBLE)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(max(this.op.value, DOUBLE), this.binding.value)
    }

    @Test
    fun testMax_Float() {
        this.binding = Bindings.max(this.op, FLOAT)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(max(this.op.value, FLOAT), this.binding.value)
    }

    @Test
    fun testMax_Int() {
        this.binding = Bindings.max(this.op, INT)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(max(this.op.value, INT), this.binding.value)
    }

    @Test
    fun testMax_Long() {
        this.binding = Bindings.max(this.op, LONG)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(max(this.op.value, LONG), this.binding.value)
    }

    @Test
    fun testMax_Short() {
        this.binding = Bindings.max(this.op, SHORT)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(max(this.op.value, SHORT), this.binding.value)
    }

    @Test
    fun testMax_Byte() {
        this.binding = Bindings.max(this.op, BYTE)
        DependencyUtils.checkDependencies(this.binding.dependencies, this.op)
        assertEquals(max(this.op.value, BYTE), this.binding.value)
    }

    private fun negate(value: Number?): Number? {
        return when (value) {
            null -> null
            is Double -> -value
            is Float -> -value
            is Int -> -value
            is Long -> -value
            else -> -value.toInt()
        }
    }

    private fun add(value1: Number?, value2: Number?): Number? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() + value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() + value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() + value2.toLong()
            else -> value1.toInt() + value2.toInt()
        }
    }

    private fun sub(value1: Number?, value2: Number?): Number? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() - value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() - value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() - value2.toLong()
            else -> value1.toInt() - value2.toInt()
        }
    }

    private fun mul(value1: Number?, value2: Number?): Number? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() * value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() * value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() * value2.toLong()
            else -> value1.toInt() * value2.toInt()
        }
    }

    private fun div(value1: Number?, value2: Number?): Number? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() / value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() / value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() / value2.toLong()
            else -> value1.toInt() / value2.toInt()
        }
    }

    private fun eq(value1: Number?, value2: Number?): Boolean? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() == value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() == value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() == value2.toLong()
            else -> value1.toInt() == value2.toInt()
        }
    }

    private fun neq(value1: Number?, value2: Number?): Boolean? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() != value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() != value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() != value2.toLong()
            else -> value1.toInt() != value2.toInt()
        }
    }

    private fun gt(value1: Number?, value2: Number?): Boolean? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() > value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() > value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() > value2.toLong()
            else -> value1.toInt() > value2.toInt()
        }
    }

    private fun lt(value1: Number?, value2: Number?): Boolean? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() < value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() < value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() < value2.toLong()
            else -> value1.toInt() < value2.toInt()
        }
    }

    private fun gte(value1: Number?, value2: Number?): Boolean? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() >= value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() >= value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() >= value2.toLong()
            else -> value1.toInt() >= value2.toInt()
        }
    }

    private fun lte(value1: Number?, value2: Number?): Boolean? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> value1.toDouble() <= value2.toDouble()
            value1 is Float || value2 is Float -> value1.toFloat() <= value2.toFloat()
            value1 is Long || value2 is Long -> value1.toLong() <= value2.toLong()
            else -> value1.toInt() <= value2.toInt()
        }
    }

    private fun min(value1: Number?, value2: Number?): Number? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> kotlin.math.min(value1.toDouble(), value2.toDouble())
            value1 is Float || value2 is Float -> kotlin.math.min(value1.toFloat(), value2.toFloat())
            value1 is Long || value2 is Long -> kotlin.math.min(value1.toLong(), value2.toLong())
            value1 is Int || value2 is Int -> kotlin.math.min(value1.toInt(), value2.toInt())
            value1 is Short || value2 is Short -> minOf(value1.toShort(), value2.toShort())
            else -> minOf(value1.toByte(), value2.toByte())
        }
    }

    private fun max(value1: Number?, value2: Number?): Number? {
        return when {
            value1 == null || value2 == null -> null
            value1 is Double || value2 is Double -> kotlin.math.max(value1.toDouble(), value2.toDouble())
            value1 is Float || value2 is Float -> kotlin.math.max(value1.toFloat(), value2.toFloat())
            value1 is Long || value2 is Long -> kotlin.math.max(value1.toLong(), value2.toLong())
            value1 is Int || value2 is Int -> kotlin.math.max(value1.toInt(), value2.toInt())
            value1 is Short || value2 is Short -> maxOf(value1.toShort(), value2.toShort())
            else -> maxOf(value1.toByte(), value2.toByte())
        }
    }

    companion object {

        private const val DOUBLE: Double = PI

        private const val FLOAT: Float = E.toFloat()

        private const val INT: Int = 13

        private const val LONG: Long = 42L

        private const val SHORT: Short = 13

        private const val BYTE: Byte = 4

        private const val EPSILON: Double = 1e-12

        @Parameters
        @JvmStatic
        fun createParameters(): List<Array<out Any?>> {
            return listOf(
                    arrayOf(SimpleDoubleProperty(DOUBLE)),
                    arrayOf(SimpleFloatProperty(FLOAT)),
                    arrayOf(SimpleIntProperty(INT)),
                    arrayOf(SimpleLongProperty(LONG)),
                    arrayOf(SimpleShortProperty(SHORT)),
                    arrayOf(SimpleByteProperty(BYTE))
            )
        }

    }

}