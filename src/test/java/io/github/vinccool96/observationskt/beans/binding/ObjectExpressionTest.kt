package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.ObjectProperty
import io.github.vinccool96.observationskt.beans.property.SimpleObjectProperty
import io.github.vinccool96.observationskt.beans.value.ObservableObjectValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ObjectExpressionTest {

    private lateinit var data1: Any

    private lateinit var data2: Any

    private lateinit var op1: ObjectProperty<Any>

    private lateinit var op2: ObjectProperty<Any>

    @Before
    fun setUp() {
        this.data1 = Any()
        this.data2 = Any()
        this.op1 = SimpleObjectProperty(this.data1)
        this.op2 = SimpleObjectProperty(this.data2)
    }

    @Test
    fun testEquals() {
        var binding: BooleanBinding = this.op1.isEqualTo(this.op1)
        assertEquals(true, binding.get())

        binding = this.op1.isEqualTo(this.op2)
        assertEquals(false, binding.get())

        binding = this.op1.isEqualTo(this.data1)
        assertEquals(true, binding.get())

        binding = this.op1.isEqualTo(this.data2)
        assertEquals(false, binding.get())
    }

    @Test
    fun testNotEquals() {
        var binding: BooleanBinding = this.op1.isNotEqualTo(this.op1)
        assertEquals(false, binding.get())

        binding = this.op1.isNotEqualTo(this.op2)
        assertEquals(true, binding.get())

        binding = this.op1.isNotEqualTo(this.data1)
        assertEquals(false, binding.get())

        binding = this.op1.isNotEqualTo(this.data2)
        assertEquals(true, binding.get())
    }

    @Test
    fun testIsNull() {
        var binding: BooleanBinding = this.op1.isNull()
        assertEquals(false, binding.get())

        val op3: ObjectExpression<Any?> = SimpleObjectProperty(null)
        binding = op3.isNull()
        assertEquals(true, binding.get())
    }

    @Test
    fun testIsNotNull() {
        var binding: BooleanBinding = this.op1.isNotNull()
        assertEquals(true, binding.get())

        val op3: ObjectExpression<Any?> = SimpleObjectProperty(null)
        binding = op3.isNotNull()
        assertEquals(false, binding.get())
    }

    @Test
    fun testFactory() {
        val valueModel: ObservableObjectValueStub<Any?> = ObservableObjectValueStub(null)
        val exp: ObjectExpression<Any?> = ObjectExpression.objectExpression(valueModel)

        assertTrue(exp is ObjectBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(null, exp.get())
        valueModel.set(this.data1)
        assertEquals(this.data1, exp.get())
        valueModel.set(this.data2)
        assertEquals(this.data2, exp.get())

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, ObjectExpression.objectExpression(this.op1))
    }

    @Test
    fun testAsString() {
        val binding: StringBinding = this.op1.asString()
        DependencyUtils.checkDependencies(binding.dependencies, this.op1)

        assertEquals(this.op1.get().toString(), binding.get())

        this.op1.set(object : Any() {

            override fun toString(): String {
                return "foo"
            }

        })
        assertEquals("foo", binding.get())
    }

    @Test
    fun testAsString_Format() {
        val binding: StringBinding = this.op1.asString("%h")
        DependencyUtils.checkDependencies(binding.dependencies, this.op1)
        this.op1.set(object : Any() {

            override fun toString(): String {
                return "foo"
            }

        })
        assertEquals(Integer.toHexString(this.op1.get().hashCode()), binding.get())
    }

}