package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableStringValueStub
import kotlin.test.*

class StringPropertyBaseTest {

    private lateinit var property: StringPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<String?>

    @BeforeTest
    fun setUp() {
        this.property = StringPropertyMock()
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
        val p1: StringProperty = SimpleStringProperty(null)
        assertEquals(null, p1.get())
        assertEquals(null, p1.value)
        assertFalse(p1.bound)

        val p2: StringProperty = SimpleStringProperty(VALUE_1b)
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

    @Test
    fun testSetBound() {
        assertFailsWith<RuntimeException> {
            val v: StringProperty = SimpleStringProperty(VALUE_1a)
            this.property.bind(v)
            this.property.set(VALUE_2b)
        }
    }

    @Test
    fun testValueSetBound() {
        assertFailsWith<RuntimeException> {
            val v: StringProperty = SimpleStringProperty(VALUE_1a)
            this.property.bind(v)
            this.property.value = VALUE_2b
        }
    }

    @Test
    fun testLazyBind() {
        attachInvalidationListener()
        val v = ObservableStringValueStub(VALUE_1a)
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
        val v = ObservableStringValueStub(VALUE_1a)
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
        val v1 = ObservableStringValueStub(VALUE_1a)
        val v2 = ObservableStringValueStub(VALUE_2a)
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
        val v = ObservableStringValueStub(VALUE_1a)
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
    @Suppress("UNUSED_VALUE")
    fun testBindNull() {
        var property: StringPropertyMock? = StringPropertyMock()
        val v = ObservableStringValueStub(VALUE_1a)
        val publicListener = InvalidationListenerMock()
        val privateListener = InvalidationListenerMock()
        property!!.addListener(publicListener)
        v.addListener(privateListener)
        property.bind(v)
        assertEquals(VALUE_1a, property.get())
        assertTrue(property.bound)
        property.reset()
        publicListener.reset()
        privateListener.reset()

        // GC-ed call
        property = null
        System.gc()
        publicListener.reset()
        privateListener.reset()
        v.set(VALUE_2b)
        v.get()
        publicListener.check(null, 0)
        privateListener.check(v, 1)
    }

    @Test
    fun testAddingListenerWillAlwaysReceiveInvalidationEvent() {
        val v = ObservableStringValueStub(VALUE_1a)
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
        val value1 = "Hello there"
        val value2 = "Bye"
        val v: StringProperty = SimpleStringProperty(value2)

        this.property.set(value1)
        assertEquals("StringProperty [value: $value1]", this.property.toString())

        this.property.bind(v)
        assertEquals("StringProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("StringProperty [bound, value: $value2]", this.property.toString())
        v.set(value1)
        assertEquals("StringProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("StringProperty [bound, value: $value1]", this.property.toString())

        val bean = String()
        val name = "My name"
        val v1: StringPropertyBase = StringPropertyMock(bean, name)
        assertEquals("StringProperty [bean: $bean, name: My name, value: ${null}]", v1.toString())
        v1.set(value1)
        assertEquals("StringProperty [bean: $bean, name: My name, value: $value1]", v1.toString())

        val v2: StringPropertyBase = StringPropertyMock(bean, NO_NAME_1)
        assertEquals("StringProperty [bean: $bean, value: ${null}]", v2.toString())
        v2.set(value1)
        assertEquals("StringProperty [bean: $bean, value: $value1]", v2.toString())

        val v3: StringPropertyBase = StringPropertyMock(bean, NO_NAME_2)
        assertEquals("StringProperty [bean: $bean, value: ${null}]", v3.toString())
        v3.set(value1)
        assertEquals("StringProperty [bean: $bean, value: $value1]", v3.toString())

        val v4: StringPropertyBase = StringPropertyMock(NO_BEAN, name)
        assertEquals("StringProperty [name: My name, value: ${null}]", v4.toString())
        v4.set(value1)
        assertEquals("StringProperty [name: My name, value: $value1]", v4.toString())
    }

    private class StringPropertyMock :
            StringPropertyBase {

        override val bean: Any?

        override val name: String?

        var counter: Int = 0

        constructor(bean: Any?, name: String?) : super() {
            this.bean = bean
            this.name = name
        }

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

        private val NO_BEAN: String? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

        private const val UNDEFINED: String = "UNDEFINED"

        private const val VALUE_1a: String = "Hello World"

        private const val VALUE_1b: String = "HELLO WORLD"

        private const val VALUE_2a: String = "Goodbye"

        private const val VALUE_2b: String = "GOODBYE"

    }

}