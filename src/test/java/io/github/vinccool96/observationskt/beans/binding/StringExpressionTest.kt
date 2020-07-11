package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.SimpleStringProperty
import io.github.vinccool96.observationskt.beans.property.StringProperty
import io.github.vinccool96.observationskt.beans.value.ObservableIntValueStub
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class StringExpressionTest {

    private lateinit var data1: String

    private lateinit var data1Ic: String

    private lateinit var data2: String

    private lateinit var op1: StringProperty

    private lateinit var op2: StringProperty

    @Before
    fun setUp() {
        this.data1 = "Hello"
        this.data1Ic = "HeLlO"
        this.data2 = "Goodbye"
        this.op1 = SimpleStringProperty(this.data1)
        this.op2 = SimpleStringProperty(this.data2)
    }

    @Test
    fun testConcat() {
        var expression = this.op1.concat(this.op2)
        assertEquals(this.data1 + this.data2, expression.get())
        expression = this.op1.concat(this.data1)
        assertEquals(this.data1 + this.data1, expression.get())
    }

    @Test
    fun testEquals() {
        var binding = this.op1.isEqualTo(this.op1)
        assertEquals(true, binding.get())
        binding = this.op1.isEqualTo(this.op2)
        assertEquals(false, binding.get())
        binding = this.op1.isEqualTo(this.data1)
        assertEquals(true, binding.get())
        binding = this.op1.isEqualTo(this.data1Ic)
        assertEquals(false, binding.get())
        binding = this.op1.isEqualTo(this.data2)
        assertEquals(false, binding.get())
    }

    @Test
    fun testEqualsIgnoringCase() {
        var binding = this.op1.isEqualToIgnoreCase(this.op1)
        assertEquals(true, binding.get())
        binding = this.op1.isEqualToIgnoreCase(this.op2)
        assertEquals(false, binding.get())
        binding = this.op1.isEqualToIgnoreCase(this.data1)
        assertEquals(true, binding.get())
        binding = this.op1.isEqualToIgnoreCase(this.data1Ic)
        assertEquals(true, binding.get())
        binding = this.op1.isEqualToIgnoreCase(this.data2)
        assertEquals(false, binding.get())
    }

    @Test
    fun testNotEquals() {
        var binding = this.op1.isNotEqualTo(this.op1)
        assertEquals(false, binding.get())
        binding = this.op1.isNotEqualTo(this.op2)
        assertEquals(true, binding.get())
        binding = this.op1.isNotEqualTo(this.data1)
        assertEquals(false, binding.get())
        binding = this.op1.isNotEqualTo(this.data1Ic)
        assertEquals(true, binding.get())
        binding = this.op1.isNotEqualTo(this.data2)
        assertEquals(true, binding.get())
    }

    @Test
    fun testNotEqualsIgnoringCase() {
        var binding = this.op1.isNotEqualToIgnoreCase(this.op1)
        assertEquals(false, binding.get())
        binding = this.op1.isNotEqualToIgnoreCase(this.op2)
        assertEquals(true, binding.get())
        binding = this.op1.isNotEqualToIgnoreCase(this.data1)
        assertEquals(false, binding.get())
        binding = this.op1.isNotEqualToIgnoreCase(this.data1Ic)
        assertEquals(false, binding.get())
        binding = this.op1.isNotEqualToIgnoreCase(this.data2)
        assertEquals(true, binding.get())
    }

    @Test
    fun testIsNull() {
        var binding = this.op1.isNull()
        assertEquals(false, binding.get())
        val op3: StringProperty = SimpleStringProperty(null)
        binding = op3.isNull()
        assertEquals(true, binding.get())
    }

    @Test
    fun testIsNotNull() {
        var binding = this.op1.isNotNull()
        assertEquals(true, binding.get())
        val op3: StringProperty = SimpleStringProperty(null)
        binding = op3.isNotNull()
        assertEquals(false, binding.get())
    }

    @Test
    fun testGreater() {
        var binding = this.op1.greaterThan(this.op1)
        assertEquals(this.data1 > this.data1, binding.get())
        binding = this.op1.greaterThan(this.op2)
        assertEquals(this.data1 > this.data2, binding.get())
        binding = this.op1.greaterThan(this.data1)
        assertEquals(this.data1 > this.data1, binding.get())
        binding = this.op1.greaterThan(this.data1Ic)
        assertEquals(this.data1 > this.data1Ic, binding.get())
        binding = this.op1.greaterThan(this.data2)
        assertEquals(this.data1 > this.data2, binding.get())
    }

    @Test
    fun testLesser() {
        var binding = this.op1.lessThan(this.op1)
        assertEquals(this.data1 < this.data1, binding.get())
        binding = this.op1.lessThan(this.op2)
        assertEquals(this.data1 < this.data2, binding.get())
        binding = this.op1.lessThan(this.data1)
        assertEquals(this.data1 < this.data1, binding.get())
        binding = this.op1.lessThan(this.data1Ic)
        assertEquals(this.data1 < this.data1Ic, binding.get())
        binding = this.op1.lessThan(this.data2)
        assertEquals(this.data1 < this.data2, binding.get())
    }

    @Test
    fun testGreaterOrEqual() {
        var binding = this.op1.greaterThanOrEqualTo(this.op1)
        assertEquals(this.data1 >= this.data1, binding.get())
        binding = this.op1.greaterThanOrEqualTo(this.op2)
        assertEquals(this.data1 >= this.data2, binding.get())
        binding = this.op1.greaterThanOrEqualTo(this.data1)
        assertEquals(this.data1 >= this.data1, binding.get())
        binding = this.op1.greaterThanOrEqualTo(this.data1Ic)
        assertEquals(this.data1 >= this.data1Ic, binding.get())
        binding = this.op1.greaterThanOrEqualTo(this.data2)
        assertEquals(this.data1 >= this.data2, binding.get())
    }

    @Test
    fun testLesserOrEqual() {
        var binding = this.op1.lessThanOrEqualTo(this.op1)
        assertEquals(this.data1 <= this.data1, binding.get())
        binding = this.op1.lessThanOrEqualTo(this.op2)
        assertEquals(this.data1 <= this.data2, binding.get())
        binding = this.op1.lessThanOrEqualTo(this.data1)
        assertEquals(this.data1 <= this.data1, binding.get())
        binding = this.op1.lessThanOrEqualTo(this.data1Ic)
        assertEquals(this.data1 <= this.data1Ic, binding.get())
        binding = this.op1.lessThanOrEqualTo(this.data2)
        assertEquals(this.data1 <= this.data2, binding.get())
    }

    @Test
    fun testLength() {
        var binding: IntBinding = this.op1.length()
        assertEquals(this.data1.length, binding.get())
        val op3: StringProperty = SimpleStringProperty(null)
        binding = op3.length()
        assertEquals(0, binding.get())
    }

    @Test
    fun testIsEmpty() {
        var binding = this.op1.isEmpty()
        assertEquals(this.data1.isEmpty(), binding.get())
        val op3: StringProperty = SimpleStringProperty(null)
        binding = op3.isEmpty()
        assertEquals(true, binding.get())
    }

    @Test
    fun testIsNotEmpty() {
        var binding = this.op1.isNotEmpty()
        assertEquals(this.data1.isNotEmpty(), binding.get())
        val op3: StringProperty = SimpleStringProperty(null)
        binding = op3.isNotEmpty()
        assertEquals(false, binding.get())
    }

    @Test
    fun testGetValueSafe() {
        assertEquals(this.data1, this.op1.get())
        assertEquals(this.data1, this.op1.valueSafe)
        this.op1.set(null)
        Assert.assertNull(this.op1.get())
        assertEquals("", this.op1.valueSafe)
    }

    @Test
    fun testFactory() {
        val valueModel = ObservableIntValueStub()
        val exp = StringExpression.stringExpression(valueModel)
        Assert.assertTrue(exp is StringBinding)
        DependencyUtils.checkDependencies((exp as StringBinding).dependencies, valueModel)
        assertEquals("0", exp.get())
        valueModel.set(42)
        assertEquals("42", exp.get())
        valueModel.set(7)
        assertEquals("7", exp.get())

        // make sure we do not create unnecessary bindings
        assertEquals(this.op1, StringExpression.stringExpression(this.op1))
    }

}