package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.property.*
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(Parameterized::class)
@Suppress("UNUSED_VALUE")
class BidirectionalBindingTest<T>(private val factory: Factory<T>) {

    fun interface PropertyFactory<T> {

        fun createProperty(): Property<T>

    }

    class Factory<T>(private val propertyFactory: PropertyFactory<T>, val values: Array<T>) {

        fun createProperty(): Property<T> {
            return this.propertyFactory.createProperty()
        }

    }

    private lateinit var op1: Property<T>

    private lateinit var op2: Property<T>

    private lateinit var op3: Property<T>

    private lateinit var op4: Property<T>

    private lateinit var v: Array<T>

    @Before
    fun setUp() {
        this.op1 = this.factory.createProperty()
        this.op2 = this.factory.createProperty()
        this.op3 = this.factory.createProperty()
        this.op4 = this.factory.createProperty()
        this.v = this.factory.values
        this.op1.value = this.v[0]
        this.op2.value = this.v[1]
    }

    @Test
    fun testBind() {
        Bindings.bindBidirectional(this.op1, this.op2)
        Bindings.bindBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not not overdo weak references
        assertEquals(this.v[1], this.op1.value)
        assertEquals(this.v[1], this.op2.value)

        this.op1.value = this.v[2]
        assertEquals(this.v[2], this.op1.value)
        assertEquals(this.v[2], this.op2.value)

        this.op2.value = this.v[3]
        assertEquals(this.v[3], this.op1.value)
        assertEquals(this.v[3], this.op2.value)
    }

    @Test
    fun testUnbind() {
        // unbind non-existing binding => no-op
        Bindings.unbindBidirectional(this.op1, this.op2)

        // unbind properties of different beans
        Bindings.bindBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not not overdo weak references
        assertEquals(this.v[1], this.op1.value)
        assertEquals(this.v[1], this.op2.value)

        Bindings.unbindBidirectional(this.op1, this.op2)
        System.gc()
        this.op1.value = this.v[2]
        assertEquals(this.v[2], this.op1.value)
        assertEquals(this.v[1], this.op2.value)

        this.op2.value = this.v[3]
        assertEquals(this.v[2], this.op1.value)
        assertEquals(this.v[3], this.op2.value)
    }

    @Test
    fun testChaining() {
        this.op3.value = this.v[2]
        Bindings.bindBidirectional(this.op1, this.op2)
        Bindings.bindBidirectional(this.op2, this.op3)
        System.gc() // making sure we did not not overdo weak references
        assertEquals(this.v[2], this.op1.value)
        assertEquals(this.v[2], this.op2.value)
        assertEquals(this.v[2], this.op3.value)

        this.op1.value = this.v[3]
        assertEquals(this.v[3], this.op1.value)
        assertEquals(this.v[3], this.op2.value)
        assertEquals(this.v[3], this.op3.value)

        this.op2.value = this.v[0]
        assertEquals(this.v[0], this.op1.value)
        assertEquals(this.v[0], this.op2.value)
        assertEquals(this.v[0], this.op3.value)

        this.op3.value = this.v[1]
        assertEquals(this.v[1], this.op1.value)
        assertEquals(this.v[1], this.op2.value)
        assertEquals(this.v[1], this.op3.value)

        // now unbind
        Bindings.unbindBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not not overdo weak references
        assertEquals(this.v[1], this.op1.value)
        assertEquals(this.v[1], this.op2.value)
        assertEquals(this.v[1], this.op3.value)

        this.op1.value = this.v[2]
        assertEquals(this.v[2], this.op1.value)
        assertEquals(this.v[1], this.op2.value)
        assertEquals(this.v[1], this.op3.value)

        this.op2.value = this.v[3]
        assertEquals(this.v[2], this.op1.value)
        assertEquals(this.v[3], this.op2.value)
        assertEquals(this.v[3], this.op3.value)

        this.op3.value = this.v[0]
        assertEquals(this.v[2], this.op1.value)
        assertEquals(this.v[0], this.op2.value)
        assertEquals(this.v[0], this.op3.value)
    }

    private fun getListenerCount(v: ObservableValue<T>?): Int {
        return if (v == null) -1 else ExpressionHelperUtility.getChangeListeners(v).size
    }

    @Test
    fun testWeakReferencing() {
        var p1: Property<T>? = this.factory.createProperty()
        val p2: Property<T> = this.factory.createProperty()
        var p3: Property<T>? = this.factory.createProperty()
        p1!!.value = this.v[0]
        p2.value = this.v[1]
        p3!!.value = this.v[3]

        Bindings.bindBidirectional(p1, p2)

        assertEquals(1, getListenerCount(p1))
        assertEquals(1, getListenerCount(p2))

        p1 = null
        System.gc()
        p2.value = this.v[2]
        assertEquals(0, getListenerCount(p2))

        Bindings.bindBidirectional(p2, p3)
        assertEquals(1, getListenerCount(p2))
        assertEquals(1, getListenerCount(p3))

        p3 = null
        System.gc()
        p2.value = this.v[0]
        assertEquals(0, getListenerCount(p2))
    }

    @Test
    fun testHashCode() {
        val hc1: Int = BidirectionalBinding.bind(this.op1, this.op2).hashCode()
        val hc2: Int = BidirectionalBinding.bind(this.op2, this.op1).hashCode()
        assertEquals(hc1, hc2)
    }

    @Test
    fun testEquals() {
        val golden: BidirectionalBinding<T> = BidirectionalBinding.bind(this.op1, this.op2)

        assertEquals(golden, golden)
        assertNotEquals(golden as Any, this.op1)
        assertEquals(golden, BidirectionalBinding.bind(this.op1, this.op2))
        assertEquals(golden, BidirectionalBinding.bind(this.op2, this.op1))
        assertNotEquals(golden, BidirectionalBinding.bind(this.op1, this.op3))
        assertNotEquals(golden, BidirectionalBinding.bind(this.op3, this.op1))
        assertNotEquals(golden, BidirectionalBinding.bind(this.op3, this.op2))
        assertNotEquals(golden, BidirectionalBinding.bind(this.op2, this.op3))
    }

    @Test
    fun testEqualsWithGCedProperty() {
        var p1: Property<T>? = this.factory.createProperty()
        val p2: Property<T> = this.factory.createProperty()
        p1!!.value = this.v[0]
        p2.value = this.v[1]

        val binding1 = BidirectionalBinding.bind(p1, p2)
        val binding2 = BidirectionalBinding.bind(p1, p2)
        val binding3 = BidirectionalBinding.bind(p2, p1)
        val binding4 = BidirectionalBinding.bind(p2, p1)
        p1 = null
        System.gc()

        assertEquals(binding1, binding1)
        assertNotEquals(binding1, binding2)
        assertNotEquals(binding1, binding3)

        assertEquals(binding3, binding3)
        assertNotEquals(binding3, binding1)
        assertNotEquals(binding3, binding4)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testBind_X_Self() {
        Bindings.bindBidirectional(this.op1, this.op1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUnbind_X_Self() {
        Bindings.unbindBidirectional(this.op1, this.op1)
    }

    @Test
    fun testBrokenBind() {
        Bindings.bindBidirectional(this.op1, this.op2)
        this.op1.bind(this.op3)
        assertEquals(this.op3.value, this.op1.value)
        assertEquals(this.op2.value, this.op1.value)

        this.op2.value = this.v[2]
        assertEquals(this.op3.value, this.op1.value)
        assertEquals(this.op2.value, this.op1.value)
    }

    @Test
    fun testDoubleBrokenBind() {
        Bindings.bindBidirectional(this.op1, this.op2)
        this.op1.bind(this.op3)
        this.op4.value = this.v[0]

        this.op2.bind(this.op4)
        assertEquals(this.op4.value, this.op2.value)
        assertEquals(this.op3.value, this.op1.value)
        // Test that bidirectional binding was unbound in this case
        this.op3.value = this.v[0]
        this.op4.value = this.v[1]
        assertEquals(this.op4.value, this.op2.value)
        assertEquals(this.op3.value, this.op1.value)
        assertEquals(this.v[0], this.op1.value)
        assertEquals(this.v[1], this.op2.value)
    }

    companion object {

        @Parameterized.Parameters
        @JvmStatic
        fun parameters(): Collection<Array<Any>> {
            val booleanData: Array<Boolean?> = arrayOf(true, false, true, false)
            val doubleData: Array<Number?> = arrayOf(2348.2345, -92.214, -214.0214, -908.214)
            val floatData: Array<Number?> = arrayOf(-3592.9f, 234872.8347f, 3897.274f, 3958.938745f)
            val longData: Array<Number?> = arrayOf(9823984L, 2908934L, -234234L, 9089234L)
            val intData: Array<Number?> = arrayOf(248, -9384, -234, -34)
            val objectData: Array<Any> = arrayOf(Any(), Any(), Any(), Any())
            val stringData: Array<String?> = arrayOf("A", "B", "C", "D")

            return listOf(
                    arrayOf(Factory(SimpleBooleanProperty::class.java::newInstance, booleanData)),
                    arrayOf(Factory(SimpleDoubleProperty::class.java::newInstance, doubleData)),
                    arrayOf(Factory(SimpleFloatProperty::class.java::newInstance, floatData)),
                    arrayOf(Factory(SimpleLongProperty::class.java::newInstance, longData)),
                    arrayOf(Factory(SimpleIntProperty::class.java::newInstance, intData)),
                    arrayOf(Factory({SimpleObjectProperty(Any())}, objectData)),
                    arrayOf(Factory(SimpleStringProperty::class.java::newInstance, stringData)),
                    arrayOf(Factory(ReadOnlyBooleanWrapper::class.java::newInstance, booleanData)),
                    arrayOf(Factory(ReadOnlyDoubleWrapper::class.java::newInstance, doubleData)),
                    arrayOf(Factory(ReadOnlyFloatWrapper::class.java::newInstance, floatData)),
                    arrayOf(Factory(ReadOnlyLongWrapper::class.java::newInstance, longData)),
                    arrayOf(Factory(ReadOnlyIntWrapper::class.java::newInstance, intData)),
                    arrayOf(Factory({ReadOnlyObjectWrapper(Any())}, objectData)),
                    arrayOf(Factory(ReadOnlyStringWrapper::class.java::newInstance, stringData)),
            )
        }

    }

}