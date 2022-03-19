package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableByteValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableObjectValueStub
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("BooleanLiteralArgument")
class BytePropertyBaseTest {

    private lateinit var property: BytePropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<Number?>

    @BeforeTest
    fun setUp() {
        this.property = BytePropertyMock()
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
        val p1: ByteProperty = SimpleByteProperty()
        assertEquals(0, p1.get())
        assertEquals(0, p1.value!!.toByte())
        assertFalse(p1.bound)

        val p2: ByteProperty = SimpleByteProperty(VALUE_1)
        assertEquals(VALUE_1, p2.get())
        assertEquals(VALUE_1, p2.value!!.toByte())
        assertFalse(p2.bound)
    }

    @Test
    fun testInvalidationListener() {
        attachInvalidationListener()
        this.property.set(VALUE_1)
        this.invalidationListener.check(this.property, 1)
        this.property.removeListener(this.invalidationListener)
        this.invalidationListener.reset()
        this.property.set(VALUE_2)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testChangeListener() {
        attachChangeListener()
        this.property.set(VALUE_1)
        this.changeListener.check(this.property, DEFAULT_VALUE, VALUE_1, 1)
        this.property.removeListener(this.changeListener)
        this.changeListener.reset()
        this.property.set(VALUE_2)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
    }

    @Test
    fun testLazySet() {
        attachInvalidationListener()

        // set value once
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set same value again
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value twice without reading
        this.property.set(VALUE_2)
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerSet() {
        attachChangeListener()

        // set value once
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, DEFAULT_VALUE, VALUE_1, 1)

        // set same value again
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.set(VALUE_2)
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_2, VALUE_1, 2)
    }

    @Test
    fun testLazyValueSet() {
        attachInvalidationListener()

        // set value once
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.value)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set same value again
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.value)
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value twice without reading
        this.property.value = VALUE_2
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.value)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerValueSet() {
        attachChangeListener()

        // set value once
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.value)
        this.property.check(1)
        this.changeListener.check(this.property, DEFAULT_VALUE, VALUE_1, 1)

        // set same value again
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.value)
        this.property.check(0)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.value = VALUE_2
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.value)
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_2, VALUE_1, 2)
    }

    @Test(expected = RuntimeException::class)
    fun testSetBound() {
        val v: ByteProperty = SimpleByteProperty(VALUE_1)
        this.property.bind(v)
        this.property.set(VALUE_1)
    }

    @Test(expected = RuntimeException::class)
    fun testValueSetBound() {
        val v: ByteProperty = SimpleByteProperty(VALUE_1)
        this.property.bind(v)
        this.property.value = VALUE_1
    }

    @Test
    fun testLazyBind_primitive() {
        attachInvalidationListener()
        val v = ObservableByteValueStub(VALUE_1)
        this.property.bind(v)

        assertEquals(VALUE_1, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding once
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice without reading
        v.set(VALUE_1)
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice to same value
        v.set(VALUE_1)
        v.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerBind_primitive() {
        attachChangeListener()
        val v = ObservableByteValueStub(VALUE_1)
        this.property.bind(v)

        assertEquals(VALUE_1, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.changeListener.check(this.property, DEFAULT_VALUE, VALUE_1, 1)

        // change binding once
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, VALUE_1, VALUE_2, 1)

        // change binding twice without reading
        v.set(VALUE_1)
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_1, VALUE_2, 2)

        // change binding twice to same value
        v.set(VALUE_1)
        v.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_2, VALUE_1, 1)
    }

    @Test
    fun testLazyBind_generic() {
        attachInvalidationListener()
        val v: ObservableObjectValueStub<Byte> = ObservableObjectValueStub(VALUE_1)
        this.property.bind(v)

        assertEquals(VALUE_1, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding once
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice without reading
        v.set(VALUE_1)
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice to same value
        v.set(VALUE_1)
        v.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerBind_generic() {
        attachChangeListener()
        val v: ObservableObjectValueStub<Byte> = ObservableObjectValueStub(VALUE_1)
        this.property.bind(v)

        assertEquals(VALUE_1, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.changeListener.check(this.property, DEFAULT_VALUE, VALUE_1, 1)

        // change binding once
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, VALUE_1, VALUE_2, 1)

        // change binding twice without reading
        v.set(VALUE_1)
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_1, VALUE_2, 2)

        // change binding twice to same value
        v.set(VALUE_1)
        v.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_2, VALUE_1, 1)
    }

    @Test
    fun testRebind() {
        attachInvalidationListener()
        val v1 = ObservableByteValueStub(VALUE_2)
        val v2 = ObservableByteValueStub(VALUE_1)
        this.property.bind(v1)
        this.property.get()
        this.property.reset()
        this.invalidationListener.reset()
        assertTrue(this.property.bound)

        // rebind causes invalidation event
        this.property.bind(v2)
        assertEquals(VALUE_1, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change old binding
        v1.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // change new binding
        v2.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // rebind to same observable should have no effect
        this.property.bind(v2)
        assertEquals(VALUE_1, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(0)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testUnbind() {
        attachInvalidationListener()
        val v = ObservableByteValueStub(VALUE_2)
        this.property.bind(v)
        this.property.unbind()
        assertEquals(VALUE_2, this.property.get())
        assertFalse(this.property.bound)
        this.property.reset()
        this.invalidationListener.reset()

        // change binding
        v.set(VALUE_1)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    @Suppress("UNUSED_VALUE")
    fun testBindNull() {
        var property: BytePropertyMock? = BytePropertyMock()
        val v = ObservableByteValueStub(VALUE_1)
        val publicListener = InvalidationListenerMock()
        val privateListener = InvalidationListenerMock()
        property!!.addListener(publicListener)
        v.addListener(privateListener)
        property.bind(v)
        assertEquals(VALUE_1, property.get())
        assertTrue(property.bound)
        property.reset()
        publicListener.reset()
        privateListener.reset()

        // GC-ed call
        property = null
        System.gc()
        publicListener.reset()
        privateListener.reset()
        v.set(VALUE_2)
        v.get()
        publicListener.check(null, 0)
        privateListener.check(v, 1)
    }

    @Test
    fun testAddingListenerWillAlwaysReceiveInvalidationEvent() {
        val v = ObservableByteValueStub(VALUE_2)
        val listener2 = InvalidationListenerMock()
        val listener3 = InvalidationListenerMock()

        // setting the property
        this.property.set(VALUE_2)
        this.property.addListener(listener2)
        listener2.reset()
        this.property.set(VALUE_1)
        listener2.check(this.property, 1)

        // binding the property
        this.property.bind(v)
        v.set(VALUE_1)
        this.property.addListener(listener3)
        v.get()
        listener3.reset()
        v.set(VALUE_1)
        listener3.check(this.property, 1)
    }

    @Test
    fun testToString() {
        val v: ByteProperty = SimpleByteProperty(VALUE_1)

        this.property.set(VALUE_1)
        assertEquals("ByteProperty [value: $VALUE_1]", this.property.toString())

        this.property.bind(v)
        assertEquals("ByteProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("ByteProperty [bound, value: $VALUE_1]", this.property.toString())
        v.set(VALUE_2)
        assertEquals("ByteProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("ByteProperty [bound, value: ${VALUE_2}]", this.property.toString())

        val bean = Any()
        val name = "My name"
        val v1: BytePropertyBase = BytePropertyMock(bean, name)
        assertEquals("ByteProperty [bean: $bean, name: My name, value: 0]", v1.toString())
        v1.set(VALUE_1)
        assertEquals("ByteProperty [bean: $bean, name: My name, value: $VALUE_1]", v1.toString())

        val v2: BytePropertyBase = BytePropertyMock(bean, NO_NAME_1)
        assertEquals("ByteProperty [bean: $bean, value: 0]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("ByteProperty [bean: $bean, value: $VALUE_1]", v2.toString())

        val v3: BytePropertyBase = BytePropertyMock(bean, NO_NAME_2)
        assertEquals("ByteProperty [bean: $bean, value: 0]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("ByteProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: BytePropertyBase = BytePropertyMock(NO_BEAN, name)
        assertEquals("ByteProperty [name: My name, value: 0]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("ByteProperty [name: My name, value: $VALUE_1]", v4.toString())
    }

    private class BytePropertyMock(override val bean: Any?, override val name: String?) : BytePropertyBase(0) {

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

        private const val UNDEFINED: Byte = Byte.MAX_VALUE

        private const val DEFAULT_VALUE: Byte = 0

        private const val VALUE_1: Byte = 42

        private const val VALUE_2: Byte = 123

    }

}