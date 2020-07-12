package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableObjectValueStub
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObjectPropertyBaseTest {

    private lateinit var property: ObjectPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<Any?>

    @Before
    fun setUp() {
        this.property = ObjectPropertyMock()
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
        val p1: ObjectProperty<Any?> = SimpleObjectProperty(null)
        assertEquals(null, p1.get())
        assertEquals(null, p1.value)
        assertFalse(p1.bound)

        val p2: ObjectProperty<Any?> = SimpleObjectProperty(VALUE_1b)
        assertEquals(VALUE_1b, p2.get())
        assertEquals(VALUE_1b, p2.value)
        assertFalse(p2.bound)
    }

    @Test
    fun testInvalidationListener() {
        attachInvalidationListener()
        this.property.set(VALUE_2a)
        this.invalidationListener.check(this.property, 1)
        this.property.removeListener(this.invalidationListener)
        this.invalidationListener.reset()
        this.property.set(VALUE_1a)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testChangeListener() {
        attachChangeListener()
        this.property.set(VALUE_2a)
        this.changeListener.check(this.property, null, VALUE_2a, 1)
        this.property.removeListener(this.changeListener)
        this.changeListener.reset()
        this.property.set(VALUE_1a)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
    }

    @Test
    fun testLazySet() {
        attachInvalidationListener()

        // set value once
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set same value again
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value twice without reading
        this.property.set(VALUE_1a)
        this.property.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerSet() {
        attachChangeListener()

        // set value once
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, null, VALUE_2a, 1)

        // set same value again
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.set(VALUE_1a)
        this.property.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_1a, VALUE_1b, 2)
    }

    @Test
    fun testLazyValueSet() {
        attachInvalidationListener()

        // set value once
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.value)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set same value again
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.value)
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value twice without reading
        this.property.value = VALUE_1a
        this.property.value = VALUE_1b
        assertEquals(VALUE_1b, this.property.value)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerValueSet() {
        attachChangeListener()

        // set value once
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.value)
        this.property.check(1)
        this.changeListener.check(this.property, null, VALUE_2a, 1)

        // set same value again
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.value)
        this.property.check(0)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.value = VALUE_1a
        this.property.value = VALUE_1b
        assertEquals(VALUE_1b, this.property.value)
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_1a, VALUE_1b, 2)
    }

    @Test(expected = RuntimeException::class)
    fun testSetBound() {
        val v: ObjectProperty<Any?> = SimpleObjectProperty(VALUE_1a)
        this.property.bind(v)
        this.property.set(VALUE_2b)
    }

    @Test(expected = RuntimeException::class)
    fun testValueSetBound() {
        val v: ObjectProperty<Any?> = SimpleObjectProperty(VALUE_1a)
        this.property.bind(v)
        this.property.value = VALUE_2b
    }

    @Test
    fun testLazyBind() {
        attachInvalidationListener()
        val v: ObservableObjectValueStub<Any?> = ObservableObjectValueStub(VALUE_1a)
        this.property.bind(v)

        assertEquals(VALUE_1a, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding once
        v.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice without reading
        v.set(VALUE_1a)
        v.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice to same value
        v.set(VALUE_1a)
        v.set(VALUE_1a)
        assertEquals(VALUE_1a, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerBind() {
        attachChangeListener()
        val v: ObservableObjectValueStub<Any?> = ObservableObjectValueStub(VALUE_1a)
        this.property.bind(v)

        assertEquals(VALUE_1a, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.changeListener.check(this.property, null, VALUE_1a, 1)

        // change binding once
        v.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, VALUE_1a, VALUE_2a, 1)

        // change binding twice without reading
        v.set(VALUE_1a)
        v.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_1a, VALUE_1b, 2)

        // change binding twice to same value
        v.set(VALUE_1a)
        v.set(VALUE_1a)
        assertEquals(VALUE_1a, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_1b, VALUE_1a, 1)
    }

    @Test
    fun testRebind() {
        attachInvalidationListener()
        val v1: ObservableObjectValueStub<Any?> = ObservableObjectValueStub(VALUE_1a)
        val v2: ObservableObjectValueStub<Any?> = ObservableObjectValueStub(VALUE_2a)
        this.property.bind(v1)
        this.property.get()
        this.property.reset()
        this.invalidationListener.reset()
        assertTrue(this.property.bound)

        // rebind causes invalidation event
        this.property.bind(v2)
        assertEquals(VALUE_2a, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change old binding
        v1.set(VALUE_1b)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // change new binding
        v2.set(VALUE_2b)
        assertEquals(VALUE_2b, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // rebind to same observable should have no effect
        this.property.bind(v2)
        assertEquals(VALUE_2b, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(0)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testUnbind() {
        attachInvalidationListener()
        val v: ObservableObjectValueStub<Any?> = ObservableObjectValueStub(VALUE_1a)
        this.property.bind(v)
        this.property.unbind()
        assertEquals(VALUE_1a, this.property.get())
        assertFalse(this.property.bound)
        this.property.reset()
        this.invalidationListener.reset()

        // change binding
        v.set(VALUE_1b)
        assertEquals(VALUE_1a, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testAddingListenerWillAlwaysReceiveInvalidationEvent() {
        val v: ObservableObjectValueStub<Any?> = ObservableObjectValueStub(VALUE_1a)
        val listener2 = InvalidationListenerMock()
        val listener3 = InvalidationListenerMock()

        // setting the property
        this.property.set(VALUE_1a)
        this.property.addListener(listener2)
        listener2.reset()
        this.property.set(VALUE_1b)
        listener2.check(this.property, 1)

        // binding the property
        this.property.bind(v)
        v.set(VALUE_2a)
        this.property.addListener(listener3)
        v.get()
        listener3.reset()
        v.set(VALUE_2b)
        listener3.check(this.property, 1)
    }

    @Test
    fun testToString() {
        val value1: Any? = Any()
        val value2: Any? = Any()
        val v: ObjectProperty<Any?> = SimpleObjectProperty(value2)

        this.property.set(value1)
        assertEquals("ObjectProperty [value: $value1]", this.property.toString())

        this.property.bind(v)
        assertEquals("ObjectProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("ObjectProperty [bound, value: $value2]", this.property.toString())
        v.set(value1)
        assertEquals("ObjectProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("ObjectProperty [bound, value: $value1]", this.property.toString())

        val bean = Any()
        val name = "My name"
        val v1: ObjectPropertyBase<Any?> = ObjectPropertyMock(bean, name)
        assertEquals("ObjectProperty [bean: $bean, name: My name, value: ${null}]", v1.toString())
        v1.set(value1)
        assertEquals("ObjectProperty [bean: $bean, name: My name, value: $value1]", v1.toString())

        val v2: ObjectPropertyBase<Any?> = ObjectPropertyMock(bean, NO_NAME_1)
        assertEquals("ObjectProperty [bean: $bean, value: ${null}]", v2.toString())
        v2.set(value1)
        assertEquals("ObjectProperty [bean: $bean, value: $value1]", v2.toString())

        val v3: ObjectPropertyBase<Any?> = ObjectPropertyMock(bean, NO_NAME_2)
        assertEquals("ObjectProperty [bean: $bean, value: ${null}]", v3.toString())
        v3.set(value1)
        assertEquals("ObjectProperty [bean: $bean, value: $value1]", v3.toString())

        val v4: ObjectPropertyBase<Any?> = ObjectPropertyMock(NO_BEAN, name)
        assertEquals("ObjectProperty [name: My name, value: ${null}]", v4.toString())
        v4.set(value1)
        assertEquals("ObjectProperty [name: My name, value: $value1]", v4.toString())
    }

    private class ObjectPropertyMock(override val bean: Any?, override val name: String?) :
            ObjectPropertyBase<Any?>(null) {

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

        private val UNDEFINED: Any? = Any()

        private val VALUE_1a: Any? = Any()

        private val VALUE_1b: Any? = Any()

        private val VALUE_2a: Any? = Any()

        private val VALUE_2b: Any? = Any()

    }

}