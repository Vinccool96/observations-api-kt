package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableBooleanValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableObjectValueStub
import kotlin.test.*

@Suppress("BooleanLiteralArgument")
class BooleanPropertyBaseTest {

    private lateinit var property: BooleanPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<Boolean?>

    @BeforeTest
    fun setUp() {
        this.property = BooleanPropertyMock()
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
        val p1: BooleanProperty = SimpleBooleanProperty()
        assertEquals(false, p1.get())
        assertEquals(false, p1.value)
        assertFalse(p1.bound)

        val p2: BooleanProperty = SimpleBooleanProperty(true)
        assertEquals(true, p2.get())
        assertEquals(true, p2.value)
        assertFalse(p2.bound)
    }

    @Test
    fun testInvalidationListener() {
        attachInvalidationListener()
        this.property.set(true)
        this.invalidationListener.check(this.property, 1)
        this.property.removeListener(this.invalidationListener)
        this.invalidationListener.reset()
        this.property.set(false)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testChangeListener() {
        attachChangeListener()
        this.property.set(true)
        this.changeListener.check(this.property, false, true, 1)
        this.property.removeListener(this.changeListener)
        this.changeListener.reset()
        this.property.set(false)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
    }

    @Test
    fun testLazySet() {
        attachInvalidationListener()

        // set value once
        this.property.set(true)
        assertEquals(true, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set same value again
        this.property.set(true)
        assertEquals(true, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value twice without reading
        this.property.set(false)
        this.property.set(true)
        assertEquals(true, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerSet() {
        attachChangeListener()

        // set value once
        this.property.set(true)
        assertEquals(true, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, false, true, 1)

        // set same value again
        this.property.set(true)
        assertEquals(true, this.property.get())
        this.property.check(0)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.set(false)
        this.property.set(true)
        assertEquals(true, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, false, true, 2)
    }

    @Test
    fun testLazyValueSet() {
        attachInvalidationListener()

        // set value once
        this.property.value = true
        assertEquals(true, this.property.value)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set same value again
        this.property.value = true
        assertEquals(true, this.property.value)
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value twice without reading
        this.property.value = false
        this.property.value = true
        assertEquals(true, this.property.value)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerValueSet() {
        attachChangeListener()

        // set value once
        this.property.value = true
        assertEquals(true, this.property.value)
        this.property.check(1)
        this.changeListener.check(this.property, false, true, 1)

        // set same value again
        this.property.value = true
        assertEquals(true, this.property.value)
        this.property.check(0)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.value = false
        this.property.value = true
        assertEquals(true, this.property.value)
        this.property.check(2)
        this.changeListener.check(this.property, false, true, 2)
    }

    @Test
    fun testSetBound() {
        assertFailsWith<RuntimeException> {
            val v: BooleanProperty = SimpleBooleanProperty(true)
            this.property.bind(v)
            this.property.set(true)
        }
    }

    @Test
    fun testValueSetBound() {
        assertFailsWith<RuntimeException> {
            val v: BooleanProperty = SimpleBooleanProperty(true)
            this.property.bind(v)
            this.property.value = true
        }
    }

    @Test
    fun testLazyBind_primitive() {
        attachInvalidationListener()
        val v = ObservableBooleanValueStub(true)
        this.property.bind(v)

        assertEquals(true, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding once
        v.set(false)
        assertEquals(false, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice without reading
        v.set(true)
        v.set(false)
        assertEquals(false, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice to same value
        v.set(true)
        v.set(true)
        assertEquals(true, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerBind_primitive() {
        attachChangeListener()
        val v = ObservableBooleanValueStub(true)
        this.property.bind(v)

        assertEquals(true, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.changeListener.check(this.property, false, true, 1)

        // change binding once
        v.set(false)
        assertEquals(false, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, true, false, 1)

        // change binding twice without reading
        v.set(true)
        v.set(false)
        assertEquals(false, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, true, false, 2)

        // change binding twice to same value
        v.set(true)
        v.set(true)
        assertEquals(true, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, false, true, 1)
    }

    @Test
    fun testLazyBind_generic() {
        attachInvalidationListener()
        val v: ObservableObjectValueStub<Boolean> = ObservableObjectValueStub(true)
        this.property.bind(v)

        assertEquals(true, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding once
        v.set(false)
        assertEquals(false, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice without reading
        v.set(true)
        v.set(false)
        assertEquals(false, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change binding twice to same value
        v.set(true)
        v.set(true)
        assertEquals(true, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testEagerBind_generic() {
        attachChangeListener()
        val v: ObservableObjectValueStub<Boolean> = ObservableObjectValueStub(true)
        this.property.bind(v)

        assertEquals(true, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.changeListener.check(this.property, false, true, 1)

        // change binding once
        v.set(false)
        assertEquals(false, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, true, false, 1)

        // change binding twice without reading
        v.set(true)
        v.set(false)
        assertEquals(false, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, true, false, 2)

        // change binding twice to same value
        v.set(true)
        v.set(true)
        assertEquals(true, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, false, true, 1)
    }

    @Test
    fun testRebind() {
        attachInvalidationListener()
        val v1 = ObservableBooleanValueStub(false)
        val v2 = ObservableBooleanValueStub(true)
        this.property.bind(v1)
        this.property.get()
        this.property.reset()
        this.invalidationListener.reset()
        assertTrue(this.property.bound)

        // rebind causes invalidation event
        this.property.bind(v2)
        assertEquals(true, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // change old binding
        v1.set(true)
        assertEquals(true, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // change new binding
        v2.set(true)
        assertEquals(true, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // rebind to same observable should have no effect
        this.property.bind(v2)
        assertEquals(true, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(0)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testUnbind() {
        attachInvalidationListener()
        val v = ObservableBooleanValueStub(false)
        this.property.bind(v)
        this.property.unbind()
        assertEquals(false, this.property.get())
        assertFalse(this.property.bound)
        this.property.reset()
        this.invalidationListener.reset()

        // change binding
        v.set(true)
        assertEquals(false, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value
        this.property.set(true)
        assertEquals(true, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    @Suppress("UNUSED_VALUE")
    fun testBindNull() {
        var property: BooleanPropertyMock? = BooleanPropertyMock()
        val v = ObservableBooleanValueStub(false)
        val publicListener = InvalidationListenerMock()
        val privateListener = InvalidationListenerMock()
        property!!.addListener(publicListener)
        v.addListener(privateListener)
        property.bind(v)
        assertEquals(false, property.get())
        assertTrue(property.bound)
        property.reset()
        publicListener.reset()
        privateListener.reset()

        // GC-ed call
        property = null
        System.gc()
        publicListener.reset()
        privateListener.reset()
        v.set(true)
        v.get()
        publicListener.check(null, 0)
        privateListener.check(v, 1)
    }

    @Test
    fun testAddingListenerWillAlwaysReceiveInvalidationEvent() {
        val v = ObservableBooleanValueStub(false)
        val listener2 = InvalidationListenerMock()
        val listener3 = InvalidationListenerMock()

        // setting the property
        this.property.set(false)
        this.property.addListener(listener2)
        listener2.reset()
        this.property.set(true)
        listener2.check(this.property, 1)

        // binding the property
        this.property.bind(v)
        v.set(true)
        this.property.addListener(listener3)
        v.get()
        listener3.reset()
        v.set(true)
        listener3.check(this.property, 1)
    }

    @Test
    fun testToString() {
        val v: BooleanProperty = SimpleBooleanProperty(true)

        this.property.set(true)
        assertEquals("BooleanProperty [value: true]", this.property.toString())

        this.property.bind(v)
        assertEquals("BooleanProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("BooleanProperty [bound, value: true]", this.property.toString())
        v.set(false)
        assertEquals("BooleanProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("BooleanProperty [bound, value: false]", this.property.toString())

        val bean = Any()
        val name = "My name"
        val v1: BooleanPropertyBase = BooleanPropertyMock(bean, name)
        assertEquals("BooleanProperty [bean: $bean, name: My name, value: false]", v1.toString())
        v1.set(true)
        assertEquals("BooleanProperty [bean: $bean, name: My name, value: true]", v1.toString())

        val v2: BooleanPropertyBase = BooleanPropertyMock(bean, NO_NAME_1)
        assertEquals("BooleanProperty [bean: $bean, value: false]", v2.toString())
        v2.set(true)
        assertEquals("BooleanProperty [bean: $bean, value: true]", v2.toString())

        val v3: BooleanPropertyBase = BooleanPropertyMock(bean, NO_NAME_2)
        assertEquals("BooleanProperty [bean: $bean, value: false]", v3.toString())
        v3.set(true)
        assertEquals("BooleanProperty [bean: $bean, value: true]", v3.toString())

        val v4: BooleanPropertyBase = BooleanPropertyMock(NO_BEAN, name)
        assertEquals("BooleanProperty [name: My name, value: false]", v4.toString())
        v4.set(true)
        assertEquals("BooleanProperty [name: My name, value: true]", v4.toString())
    }

    private class BooleanPropertyMock(override val bean: Any?, override val name: String?) :
            BooleanPropertyBase(false) {

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

        private val UNDEFINED: Boolean? = null

    }

}