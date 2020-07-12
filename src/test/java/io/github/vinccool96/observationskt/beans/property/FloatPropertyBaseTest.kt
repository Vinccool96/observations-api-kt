package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableFloatValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableObjectValueStub
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("BooleanLiteralArgument")
class FloatPropertyBaseTest {

    private lateinit var property: FloatPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<Number?>

    @Before
    fun setUp() {
        this.property = FloatPropertyMock()
        this.invalidationListener = InvalidationListenerMock()
        this.changeListener = ChangeListenerMock(UNDEFINED)
    }

    private fun attachInvalidationListener() {
        this.property.addListener(this.invalidationListener)
        this.property.get()
        this.invalidationListener.reset()
    }

    private fun attachChangeListener() {
        this.property.addListener(this.changeListener)
        this.property.get()
        this.changeListener.reset()
    }

    @Test
    fun testConstructor() {
        val p1: FloatProperty = SimpleFloatProperty()
        assertEquals(0.0f, p1.get())
        assertEquals(0.0f, p1.value)
        assertFalse(p1.bound)

        val p2: FloatProperty = SimpleFloatProperty(PI)
        assertEquals(PI, p2.get())
        assertEquals(PI, p2.value)
        assertFalse(p2.bound)
    }

    @Test
    fun testInvalidationListener() {
        attachInvalidationListener()
        this.property.set(PI)
        this.invalidationListener.check(this.property, 1)
        this.property.removeListener(this.invalidationListener)
        this.invalidationListener.reset()
        this.property.set(-E)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testChangeListener() {
        attachChangeListener()
        this.property.set(PI)
        this.changeListener.check(this.property, 0.0f, PI, 1)
        this.property.removeListener(this.changeListener)
        this.changeListener.reset()
        this.property.set(-E)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
    }

    @Test
    fun testLazySet() {
        attachInvalidationListener()

        // set value once
        this.property.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set same value again
        this.property.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value twice without reading
        this.property.set(-E)
        this.property.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerSet() {
        attachChangeListener()

        // set value once
        this.property.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, 0.0f, PI, 1)

        // set same value again
        this.property.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(0)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.set(-E)
        this.property.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, -E, PI, 2)
    }

    @Test
    fun testLazyValueSet() {
        attachInvalidationListener()

        // set value once
        this.property.value = PI
        assertEquals(PI, this.property.value)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set same value again
        this.property.value = PI
        assertEquals(PI, this.property.value)
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value twice without reading
        this.property.value = -E
        this.property.value = PI
        assertEquals(PI, this.property.value)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerValueSet() {
        attachChangeListener()

        // set value once
        this.property.value = PI
        assertEquals(PI, this.property.value)
        this.property.check(1)
        this.changeListener.check(this.property, 0.0f, PI, 1)

        // set same value again
        this.property.value = PI
        assertEquals(PI, this.property.value)
        this.property.check(0)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.value = -E
        this.property.value = PI
        assertEquals(PI, this.property.value)
        this.property.check(2)
        this.changeListener.check(this.property, -E, PI, 2)
    }

    @Test(expected = RuntimeException::class)
    fun testSetBound() {
        val v: FloatProperty = SimpleFloatProperty(PI)
        this.property.bind(v)
        this.property.set(PI)
    }

    @Test(expected = RuntimeException::class)
    fun testValueSetBound() {
        val v: FloatProperty = SimpleFloatProperty(PI)
        this.property.bind(v)
        this.property.value = PI
    }

    @Test
    fun testLazyBind_primitive() {
        attachInvalidationListener()
        val v = ObservableFloatValueStub(PI)
        this.property.bind(v)

        assertEquals(PI, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding once
        v.set(-E)
        assertEquals(-E, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice without reading
        v.set(PI)
        v.set(-E)
        assertEquals(-E, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice to same value
        v.set(PI)
        v.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerBind_primitive() {
        attachChangeListener()
        val v = ObservableFloatValueStub(PI)
        this.property.bind(v)

        assertEquals(PI, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.changeListener.check(this.property, 0.0f, PI, 1)

        // change binding once
        v.set(-E)
        assertEquals(-E, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, PI, -E, 1)

        // change binding twice without reading
        v.set(PI)
        v.set(-E)
        assertEquals(-E, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, PI, -E, 2)

        // change binding twice to same value
        v.set(PI)
        v.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, -E, PI, 1)
    }

    @Test
    fun testLazyBind_generic() {
        attachInvalidationListener()
        val v: ObservableObjectValueStub<Float> = ObservableObjectValueStub(PI)
        this.property.bind(v)

        assertEquals(PI, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding once
        v.set(-E)
        assertEquals(-E, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice without reading
        v.set(PI)
        v.set(-E)
        assertEquals(-E, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice to same value
        v.set(PI)
        v.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerBind_generic() {
        attachChangeListener()
        val v: ObservableObjectValueStub<Float> = ObservableObjectValueStub(PI)
        this.property.bind(v)

        assertEquals(PI, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.changeListener.check(this.property, 0.0f, PI, 1)

        // change binding once
        v.set(-E)
        assertEquals(-E, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, PI, -E, 1)

        // change binding twice without reading
        v.set(PI)
        v.set(-E)
        assertEquals(-E, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, PI, -E, 2)

        // change binding twice to same value
        v.set(PI)
        v.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, -E, PI, 1)
    }

    @Test
    fun testRebind() {
        attachInvalidationListener()
        val v1 = ObservableFloatValueStub(-E)
        val v2 = ObservableFloatValueStub(PI)
        this.property.bind(v1)
        this.property.get()
        this.property.reset()
        this.invalidationListener.reset()
        assertTrue(this.property.bound)

        // rebind causes invalidation event
        this.property.bind(v2)
        assertEquals(PI, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change old binding
        v1.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // change new binding
        v2.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // rebind to same observable should have no effect
        this.property.bind(v2)
        assertEquals(PI, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(0)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testUnbind() {
        attachInvalidationListener()
        val v = ObservableFloatValueStub(-E)
        this.property.bind(v)
        this.property.unbind()
        assertEquals(-E, this.property.get())
        assertFalse(this.property.bound)
        this.property.reset()
        this.invalidationListener.reset()

        // change binding
        v.set(PI)
        assertEquals(-E, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value
        this.property.set(PI)
        assertEquals(PI, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testAddingListenerWillAlwaysReceiveInvalidationEvent() {
        val v = ObservableFloatValueStub(-E)
        val listener2 = InvalidationListenerMock()
        val listener3 = InvalidationListenerMock()

        // setting the property
        this.property.set(-E)
        this.property.addListener(listener2)
        listener2.reset()
        this.property.set(PI)
        listener2.check(this.property, 1)

        // binding the property
        this.property.bind(v)
        v.set(PI)
        this.property.addListener(listener3)
        v.get()
        listener3.reset()
        v.set(PI)
        listener3.check(this.property, 1)
    }

    @Test
    fun testToString() {
        val v: FloatProperty = SimpleFloatProperty(PI)

        this.property.set(PI)
        assertEquals("FloatProperty [value: $PI]", this.property.toString())

        this.property.bind(v)
        assertEquals("FloatProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("FloatProperty [bound, value: $PI]", this.property.toString())
        v.set(-E)
        assertEquals("FloatProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("FloatProperty [bound, value: ${-E}]", this.property.toString())

        val bean = Any()
        val name = "My name"
        val v1: FloatPropertyBase = FloatPropertyMock(bean, name)
        assertEquals("FloatProperty [bean: $bean, name: My name, value: 0.0]", v1.toString())
        v1.set(PI)
        assertEquals("FloatProperty [bean: $bean, name: My name, value: $PI]", v1.toString())

        val v2: FloatPropertyBase = FloatPropertyMock(bean, NO_NAME_1)
        assertEquals("FloatProperty [bean: $bean, value: 0.0]", v2.toString())
        v2.set(PI)
        assertEquals("FloatProperty [bean: $bean, value: $PI]", v2.toString())

        val v3: FloatPropertyBase = FloatPropertyMock(bean, NO_NAME_2)
        assertEquals("FloatProperty [bean: $bean, value: 0.0]", v3.toString())
        v3.set(PI)
        assertEquals("FloatProperty [bean: $bean, value: $PI]", v3.toString())

        val v4: FloatPropertyBase = FloatPropertyMock(NO_BEAN, name)
        assertEquals("FloatProperty [name: My name, value: 0.0]", v4.toString())
        v4.set(PI)
        assertEquals("FloatProperty [name: My name, value: $PI]", v4.toString())
    }

    private class FloatPropertyMock(override val bean: Any?, override val name: String?) : FloatPropertyBase(0.0f) {

        private var counter: Int = 0

        constructor() : this(NO_BEAN, NO_NAME_1)

        fun check(expected: Int) {
            assertEquals(expected, this.counter)
            reset()
        }

        fun reset() {
            this.counter = 0
        }

        override fun invalidated() {
            this.counter++
        }

    }

    companion object {

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

        private val UNDEFINED: Double? = Double.MAX_VALUE

        private const val PI: Float = kotlin.math.PI.toFloat()

        private const val E: Float = kotlin.math.E.toFloat()

    }

}