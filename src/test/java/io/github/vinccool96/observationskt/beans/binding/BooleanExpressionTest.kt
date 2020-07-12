package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.BooleanProperty
import io.github.vinccool96.observationskt.beans.property.SimpleBooleanProperty
import io.github.vinccool96.observationskt.beans.value.ObservableBooleanValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.beans.value.ObservableValueStub
import io.github.vinccool96.observationskt.collections.ObservableCollections
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@Suppress("SimplifyBooleanWithConstants")
class BooleanExpressionTest {

    private lateinit var op1: BooleanProperty

    private lateinit var op2: BooleanProperty

    @Before
    fun setUp() {
        this.op1 = SimpleBooleanProperty(true)
        this.op2 = SimpleBooleanProperty(false)
    }

    @Test
    fun testGetters() {
        assertEquals(true, this.op1.get())
        assertEquals(true, this.op1.value)

        assertEquals(false, this.op2.get())
        assertEquals(false, this.op2.value)
    }

    @Test
    fun testAND() {
        val exp: BooleanExpression = this.op1.and(this.op2)
        assertEquals(true && false, exp.get())

        this.op1.set(false)
        assertEquals(false && false, exp.get())

        this.op2.set(true)
        assertEquals(false && true, exp.get())

        this.op1.set(true)
        assertEquals(true && true, exp.get())
    }

    @Test
    fun testOR() {
        val exp: BooleanExpression = this.op1.or(this.op2)
        assertEquals(true || false, exp.get())

        this.op1.set(false)
        assertEquals(false || false, exp.get())

        this.op2.set(true)
        assertEquals(false || true, exp.get())

        this.op1.set(true)
        assertEquals(true || true, exp.get())
    }

    @Test
    fun testNOT() {
        val exp: BooleanExpression = this.op1.not()
        assertEquals(false, exp.get())

        this.op1.set(false)
        assertEquals(true, exp.get())

        this.op1.set(true)
        assertEquals(false, exp.get())
    }

    @Test
    fun testEquals() {
        val exp: BooleanExpression = this.op1.isEqualTo(this.op2)
        assertEquals(true == false, exp.get())

        this.op1.set(false)
        assertEquals(false == false, exp.get())

        this.op2.set(true)
        assertEquals(false == true, exp.get())

        this.op1.set(true)
        assertEquals(true == true, exp.get())
    }

    @Test
    fun testNotEquals() {
        val exp: BooleanExpression = this.op1.isNotEqualTo(this.op2)
        assertEquals(true != false, exp.get())

        this.op1.set(false)
        assertEquals(false != false, exp.get())

        this.op2.set(true)
        assertEquals(false != true, exp.get())

        this.op1.set(true)
        assertEquals(true != true, exp.get())
    }

    @Test
    fun testAsString() {
        val binding: StringBinding = this.op1.asString()
        DependencyUtils.checkDependencies(binding.dependencies, this.op1)
        assertEquals("true", binding.get())

        this.op1.set(false)
        assertEquals("false", binding.get())

        this.op1.set(true)
        assertEquals("true", binding.get())
    }

    @Test
    fun testAsObject() {
        val valueModel = ObservableBooleanValueStub()
        val exp: ObjectExpression<Boolean> =
                BooleanExpression.booleanExpression(valueModel as ObservableValue<Boolean?>).asObject()

        assertEquals(false, exp.get())
        valueModel.set(true)
        assertEquals(true, exp.get())
        valueModel.set(false)
        assertEquals(false, exp.get())
    }

    @Test
    fun testFactory() {
        val valueModel = ObservableBooleanValueStub()
        val exp: BooleanExpression = BooleanExpression.booleanExpression(valueModel as ObservableValue<Boolean?>)

        assertTrue(exp is BooleanBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(false, exp.get())
        valueModel.set(true)
        assertEquals(true, exp.get())
        valueModel.set(false)
        assertEquals(false, exp.get())

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, BooleanExpression.booleanExpression(this.op1 as ObservableValue<Boolean?>))
    }

    @Test
    fun testObjectToBoolean() {
        val valueModel: ObservableValueStub<Boolean?> = ObservableValueStub(null)
        val exp: BooleanExpression = BooleanExpression.booleanExpression(valueModel)

        assertTrue(exp is BooleanBinding)
        assertEquals(ObservableCollections.singletonObservableList(valueModel), exp.dependencies)

        assertEquals(false, exp.get())
        valueModel.set(true)
        assertEquals(true, exp.get())
        valueModel.set(false)
        assertEquals(false, exp.get())

        // make sure we do not create unnecessary bindings
        assertSame(this.op1, BooleanExpression.booleanExpression(this.op1 as ObservableValue<Boolean?>))
    }

}