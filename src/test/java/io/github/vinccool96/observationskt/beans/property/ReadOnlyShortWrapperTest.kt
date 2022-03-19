package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableShortValueStub
import org.junit.Assert
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReadOnlyShortWrapperTest {

    private lateinit var property: ReadOnlyShortWrapperMock

    private lateinit var readOnlyProperty: ReadOnlyShortProperty

    private lateinit var internalInvalidationListener: InvalidationListenerMock

    private lateinit var publicInvalidationListener: InvalidationListenerMock

    private lateinit var internalChangeListener: ChangeListenerMock<Number?>

    private lateinit var publicChangeListener: ChangeListenerMock<Number?>

    @BeforeTest
    fun setUp() {
        this.property = ReadOnlyShortWrapperMock()
        this.readOnlyProperty = this.property.readOnlyProperty
        this.internalInvalidationListener = InvalidationListenerMock()
        this.publicInvalidationListener = InvalidationListenerMock()
        this.internalChangeListener = ChangeListenerMock(UNDEFINED)
        this.publicChangeListener = ChangeListenerMock(UNDEFINED)
    }

    private fun attachInvalidationListeners() {
        this.property.addListener(this.internalInvalidationListener)
        this.readOnlyProperty.addListener(this.publicInvalidationListener)
        this.property.get()
        this.readOnlyProperty.get()
        this.internalInvalidationListener.reset()
        this.publicChangeListener.reset()
    }

    private fun attachInternalChangeListener() {
        this.property.addListener(this.internalChangeListener)
        this.property.get()
        this.internalChangeListener.reset()
    }

    private fun attachPublicChangeListener() {
        this.readOnlyProperty.addListener(this.publicChangeListener)
        this.readOnlyProperty.get()
        this.publicChangeListener.reset()
    }

    @Test
    fun testConstructor_InitialValue() {
        val p = ReadOnlyShortWrapper(VALUE_1)
        assertEquals(VALUE_1, p.get())
        assertEquals(VALUE_1, p.value)
        assertFalse(p.bound)
        assertEquals(null, p.bean)
        assertEquals("", p.name)
        val r: ReadOnlyShortProperty = p.readOnlyProperty
        assertEquals(VALUE_1, r.get())
        assertEquals(VALUE_1, r.value)
        assertEquals(null, r.bean)
        assertEquals("", r.name)
    }

    @Test
    fun testConstructor_Bean_Name() {
        val bean = Any()
        val name = "My name"
        val p = ReadOnlyShortWrapper(bean, name)
        assertEquals(bean, p.bean)
        assertEquals(name, p.name)
        assertEquals(DEFAULT, p.get())
        assertEquals(DEFAULT, p.value)
        val r: ReadOnlyShortProperty = p.readOnlyProperty
        assertEquals(DEFAULT, r.get())
        assertEquals(DEFAULT, r.value)
        assertEquals(bean, r.bean)
        assertEquals(name, r.name)
    }

    @Test
    fun testConstructor_Bean_Name_InitialValue() {
        val bean = Any()
        val name = "My name"
        val p = ReadOnlyShortWrapper(bean, name, VALUE_1)
        assertEquals(VALUE_1, p.get())
        assertEquals(VALUE_1, p.value)
        assertFalse(p.bound)
        assertEquals(bean, p.bean)
        assertEquals(name, p.name)
        val r: ReadOnlyShortProperty = p.readOnlyProperty
        assertEquals(VALUE_1, r.get())
        assertEquals(VALUE_1, r.value)
        assertEquals(bean, r.bean)
        assertEquals(name, r.name)
    }

    @Test
    fun testLazySet() {
        attachInvalidationListeners()

        // set value once
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)

        // set same value again
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.internalInvalidationListener.check(null, 0)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(null, 0)

        // set value twice without reading
        this.property.set(VALUE_2)
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)
    }

    @Test
    fun testInternalEagerSet() {
        attachInternalChangeListener()

        // set value once
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.internalChangeListener.check(this.property, DEFAULT, VALUE_1, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())

        // set same value again
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.internalChangeListener.check(null, UNDEFINED, UNDEFINED, 0)
        assertEquals(VALUE_1, this.readOnlyProperty.get())

        // set value twice without reading
        this.property.set(VALUE_2)
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(2)
        this.internalChangeListener.check(this.property, VALUE_2, VALUE_1, 2)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
    }

    @Test
    fun testPublicEagerSet() {
        attachPublicChangeListener()

        // set value once
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicChangeListener.check(this.readOnlyProperty, DEFAULT, VALUE_1, 1)

        // set same value again
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicChangeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.set(VALUE_2)
        this.property.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(2)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicChangeListener.check(this.readOnlyProperty, VALUE_2, VALUE_1, 2)
    }

    @Test
    fun testLazyValueSet() {
        attachInvalidationListeners()

        // set value once
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)

        // set same value again
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.internalInvalidationListener.check(null, 0)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(null, 0)

        // set value twice without reading
        this.property.value = VALUE_2
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)
    }

    @Test
    fun testInternalEagerValueSet() {
        attachInternalChangeListener()

        // set value once
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.internalChangeListener.check(this.property, DEFAULT, VALUE_1, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())

        // set same value again
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.internalChangeListener.check(null, UNDEFINED, UNDEFINED, 0)
        assertEquals(VALUE_1, this.readOnlyProperty.get())

        // set value twice without reading
        this.property.value = VALUE_2
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.get())
        this.property.check(2)
        this.internalChangeListener.check(this.property, VALUE_2, VALUE_1, 2)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
    }

    @Test
    fun testPublicEagerValueSet() {
        attachPublicChangeListener()

        // set value once
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicChangeListener.check(this.readOnlyProperty, DEFAULT, VALUE_1, 1)

        // set same value again
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicChangeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.value = VALUE_2
        this.property.value = VALUE_1
        assertEquals(VALUE_1, this.property.get())
        this.property.check(2)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicChangeListener.check(this.readOnlyProperty, VALUE_2, VALUE_1, 2)
    }

    @Test
    fun testLazyBind() {
        attachInvalidationListeners()
        val v = ObservableShortValueStub(VALUE_1)

        this.property.bind(v)
        assertEquals(VALUE_1, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)

        // change binding once
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_2, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)

        // change binding twice without reading
        v.set(VALUE_1)
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_2, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)

        // change binding twice to same value
        v.set(VALUE_1)
        v.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)
    }

    @Test
    fun testInternalEagerBind() {
        attachInternalChangeListener()
        val v = ObservableShortValueStub(VALUE_1)

        this.property.bind(v)
        assertEquals(VALUE_1, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.internalChangeListener.check(this.property, DEFAULT, VALUE_1, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())

        // change binding once
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.internalChangeListener.check(this.property, VALUE_1, VALUE_2, 1)
        assertEquals(VALUE_2, this.readOnlyProperty.get())

        // change binding twice without reading
        v.set(VALUE_1)
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(2)
        this.internalChangeListener.check(this.property, VALUE_1, VALUE_2, 2)
        assertEquals(VALUE_2, this.readOnlyProperty.get())

        // change binding twice to same value
        v.set(VALUE_1)
        v.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(2)
        this.internalChangeListener.check(this.property, VALUE_2, VALUE_1, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
    }

    @Test
    fun testPublicEagerBind() {
        attachPublicChangeListener()
        val v = ObservableShortValueStub(VALUE_1)

        this.property.bind(v)
        assertEquals(VALUE_1, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicChangeListener.check(this.readOnlyProperty, DEFAULT, VALUE_1, 1)

        // change binding once
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        assertEquals(VALUE_2, this.readOnlyProperty.get())
        this.publicChangeListener.check(this.readOnlyProperty, VALUE_1, VALUE_2, 1)

        // change binding twice without reading
        v.set(VALUE_1)
        v.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(2)
        assertEquals(VALUE_2, this.readOnlyProperty.get())
        this.publicChangeListener.check(this.readOnlyProperty, VALUE_1, VALUE_2, 2)

        // change binding twice to same value
        v.set(VALUE_1)
        v.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(2)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicChangeListener.check(this.readOnlyProperty, VALUE_2, VALUE_1, 1)
    }

    @Test
    fun testRebind() {
        attachInvalidationListeners()
        val v1: ShortProperty = SimpleShortProperty(VALUE_1)
        val v2: ShortProperty = SimpleShortProperty(VALUE_2)
        this.property.bind(v1)
        this.property.get()
        this.readOnlyProperty.get()
        this.property.reset()
        this.internalInvalidationListener.reset()
        this.publicInvalidationListener.reset()

        // rebind causes invalidation event
        this.property.bind(v2)
        assertEquals(VALUE_2, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_2, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)

        // change new binding
        v2.set(VALUE_1)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)

        // change old binding
        v1.set(VALUE_2)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.internalInvalidationListener.check(null, 0)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(null, 0)

        // rebind to same observable should have no effect
        this.property.bind(v2)
        assertTrue(this.property.bound)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.internalInvalidationListener.check(null, 0)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(null, 0)
    }

    @Test
    fun testUnbind() {
        attachInvalidationListeners()
        val v: ShortProperty = SimpleShortProperty(VALUE_1)
        this.property.bind(v)
        this.property.unbind()
        assertEquals(VALUE_1, this.property.get())
        assertFalse(this.property.bound)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.property.reset()
        this.internalInvalidationListener.reset()
        this.publicInvalidationListener.reset()

        // change binding
        v.set(VALUE_2)
        assertEquals(VALUE_1, this.property.get())
        this.property.check(0)
        this.internalInvalidationListener.check(null, 0)
        assertEquals(VALUE_1, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(null, 0)

        // set value
        this.property.set(VALUE_2)
        assertEquals(VALUE_2, this.property.get())
        this.property.check(1)
        this.internalInvalidationListener.check(this.property, 1)
        assertEquals(VALUE_2, this.readOnlyProperty.get())
        this.publicInvalidationListener.check(this.readOnlyProperty, 1)
    }

    @Test
    fun testAddingListenerWillAlwaysReceiveInvalidationEvent() {
        val v: ShortProperty = SimpleShortProperty(VALUE_1)
        val internalListener2 = InvalidationListenerMock()
        val internalListener3 = InvalidationListenerMock()
        val publicListener2 = InvalidationListenerMock()
        val publicListener3 = InvalidationListenerMock()

        // setting the property, checking internal
        this.property.set(VALUE_1)
        this.property.addListener(internalListener2)
        internalListener2.reset()
        this.property.set(VALUE_2)
        internalListener2.check(this.property, 1)

        // setting the property, checking public
        this.property.set(VALUE_1)
        this.readOnlyProperty.addListener(publicListener2)
        publicListener2.reset()
        this.property.set(VALUE_2)
        publicListener2.check(this.readOnlyProperty, 1)

        // binding the property, checking internal
        this.property.bind(v)
        v.set(VALUE_2)
        this.property.addListener(internalListener3)
        v.get()
        internalListener3.reset()
        v.set(VALUE_1)
        internalListener3.check(this.property, 1)

        // binding the property, checking public
        this.property.bind(v)
        v.set(VALUE_2)
        this.readOnlyProperty.addListener(publicListener3)
        v.get()
        publicListener3.reset()
        v.set(VALUE_1)
        publicListener3.check(this.readOnlyProperty, 1)
    }

    @Test
    fun testRemoveListeners() {
        attachInvalidationListeners()
        attachInternalChangeListener()
        this.property.removeListener(this.internalInvalidationListener)
        this.property.removeListener(this.internalChangeListener)
        this.property.get()
        this.internalInvalidationListener.reset()
        this.internalChangeListener.reset()

        this.property.set(VALUE_1)
        this.internalInvalidationListener.check(null, 0)
        this.internalChangeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // no read only property created => no-op
        val v = ReadOnlyShortWrapper()
        v.removeListener(this.internalInvalidationListener)
        v.removeListener(this.internalChangeListener)
    }

    @Test
    fun testNoReadOnlyPropertyCreated() {
        val v1: ShortProperty = SimpleShortProperty(VALUE_1)
        val p1 = ReadOnlyShortWrapper()

        p1.set(VALUE_1)
        p1.bind(v1)
        assertEquals(VALUE_1, p1.get())
        v1.set(VALUE_2)
        assertEquals(VALUE_2, p1.get())
    }

    @Test
    fun testToString() {
        val v1: ShortProperty = SimpleShortProperty(VALUE_1)

        this.property.set(VALUE_1)
        Assert.assertEquals("ShortProperty [value: $VALUE_1]", this.property.toString())
        assertEquals("ReadOnlyShortProperty [value: $VALUE_1]", this.readOnlyProperty.toString())

        this.property.bind(v1)
        Assert.assertEquals("ShortProperty [bound, invalid]", this.property.toString())
        assertEquals("ReadOnlyShortProperty [value: $VALUE_1]", this.readOnlyProperty.toString())
        this.property.get()
        Assert.assertEquals("ShortProperty [bound, value: $VALUE_1]", this.property.toString())
        assertEquals("ReadOnlyShortProperty [value: $VALUE_1]", this.readOnlyProperty.toString())
        v1.set(VALUE_2)
        Assert.assertEquals("ShortProperty [bound, invalid]", this.property.toString())
        assertEquals("ReadOnlyShortProperty [value: $VALUE_2]", this.readOnlyProperty.toString())
        this.property.get()
        Assert.assertEquals("ShortProperty [bound, value: $VALUE_2]", this.property.toString())
        assertEquals("ReadOnlyShortProperty [value: $VALUE_2]", this.readOnlyProperty.toString())

        val bean = Any()
        val name = "My name"
        val v2 = ReadOnlyShortWrapper(bean, name, DEFAULT)
        assertEquals("ShortProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        assertEquals("ReadOnlyShortProperty [bean: $bean, name: My name, value: $DEFAULT]",
                v2.readOnlyProperty.toString())

        val v3 = ReadOnlyShortWrapper(bean, "", DEFAULT)
        assertEquals("ShortProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        assertEquals("ReadOnlyShortProperty [bean: $bean, value: $DEFAULT]", v3.readOnlyProperty.toString())

        val v4 = ReadOnlyShortWrapper(null, name, DEFAULT)
        assertEquals("ShortProperty [name: My name, value: $DEFAULT]", v4.toString())
        assertEquals("ReadOnlyShortProperty [name: My name, value: $DEFAULT]", v4.readOnlyProperty.toString())
    }

    private class ReadOnlyShortWrapperMock : ReadOnlyShortWrapper() {

        private var counter: Int = 0

        override fun invalidated() {
            this.counter++
        }

        fun check(expected: Int) {
            assertEquals(expected, this.counter)
            reset()
        }

        fun reset() {
            this.counter = 0
        }

    }

    companion object {

        private val UNDEFINED: Short? = null

        private const val DEFAULT: Short = 0

        private const val VALUE_1: Short = -43

        private const val VALUE_2: Short = 5

    }

}