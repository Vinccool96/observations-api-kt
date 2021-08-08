package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableObjectValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableSetValueStub
import io.github.vinccool96.observationskt.collections.MockSetObserver
import io.github.vinccool96.observationskt.collections.MockSetObserver.Call
import io.github.vinccool96.observationskt.collections.MockSetObserver.Tuple
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableSet
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SetPropertyBaseTest {

    private lateinit var property: SetPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<ObservableSet<Any>?>

    private lateinit var setChangeListener: MockSetObserver<Any>

    @Before
    fun setUp() {
        this.property = SetPropertyMock()
        this.invalidationListener = InvalidationListenerMock()
        this.changeListener = ChangeListenerMock(UNDEFINED)
        this.setChangeListener = MockSetObserver()
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

    private fun attachSetChangeListener() {
        this.property.addListener(this.setChangeListener)
        this.property.get()
        this.setChangeListener.clear()
    }

    @Test
    fun testConstructor() {
        val p1: SetProperty<Any> = SimpleSetProperty()
        assertEquals(null, p1.get())
        assertEquals(null, p1.value)
        assertFalse(p1.bound)

        val p2: SetProperty<Any> = SimpleSetProperty(VALUE_1b)
        assertEquals(VALUE_1b, p2.get())
        assertEquals(VALUE_1b, p2.value)
        assertFalse(p2.bound)
    }

    @Test
    fun testEmptyProperty() {
        assertEquals("empty", this.property.emptyProperty.name)
        assertSame(this.property, this.property.emptyProperty.bean)
        assertTrue(this.property.emptyProperty.get())

        this.property.set(VALUE_2a)
        assertFalse(this.property.emptyProperty.get())
        this.property.set(VALUE_1a)
        assertTrue(this.property.emptyProperty.get())
    }

    @Test
    fun testSizeProperty() {
        assertEquals("size", this.property.sizeProperty.name)
        assertSame(this.property, this.property.sizeProperty.bean)
        assertEquals(0, this.property.sizeProperty.get())

        this.property.set(VALUE_2a)
        assertEquals(2, this.property.sizeProperty.get())
        this.property.set(VALUE_1a)
        assertEquals(0, this.property.sizeProperty.get())
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
    fun testSetChangeListener() {
        attachSetChangeListener()
        this.property.set(VALUE_2a)
        this.setChangeListener.assertMultipleCalls(Call(null, OBJECT_2a_0), Call(null, OBJECT_2a_1))
        this.property.removeListener(this.setChangeListener)
        this.setChangeListener.clear()
        this.property.set(VALUE_1a)
        assertEquals(0, this.setChangeListener.callsNumber)
    }

    @Test
    fun testSourceSet_Invalidation() {
        val source1: ObservableSet<Any> = ObservableCollections.observableSet()
        val source2: ObservableSet<Any> = ObservableCollections.observableSet()
        val value = Any()

        // constructor
        this.property = SetPropertyMock(source1)
        this.property.reset()
        attachInvalidationListener()

        // add element
        source1.add(value)
        assertTrue(this.property.contains(value))
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // remove value
        source1.remove(value)
        assertFalse(this.property.contains(value))
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.invalidationListener.reset()

        // add element
        source2.add(value)
        assertTrue(this.property.contains(value))
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // remove value
        source2.remove(value)
        assertFalse(this.property.contains(value))
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testSourceSet_Change() {
        val source1: ObservableSet<Any> = ObservableCollections.observableSet()
        val source2: ObservableSet<Any> = ObservableCollections.observableSet()
        val value = Any()

        // constructor
        this.property = SetPropertyMock(source1)
        this.property.reset()
        attachChangeListener()

        // add element
        source1.add(value)
        assertTrue(this.property.contains(value))
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // remove value
        source1.remove(value)
        assertFalse(this.property.contains(value))
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.changeListener.reset()

        // add element
        source2.add(value)
        assertTrue(this.property.contains(value))
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)

        // remove value
        source2.remove(value)
        assertFalse(this.property.contains(value))
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)
    }

    @Test
    fun testSourceSet_SetChange() {
        val source1: ObservableSet<Any> = ObservableCollections.observableSet()
        val source2: ObservableSet<Any> = ObservableCollections.observableSet()
        val value = Any()

        // constructor
        this.property = SetPropertyMock(source1)
        this.property.reset()
        attachSetChangeListener()

        // add element
        source1.add(value)
        assertTrue(this.property.contains(value))
        this.property.check(1)
        this.setChangeListener.assertAdded(Tuple.tup(value))
        this.setChangeListener.clear()

        // remove value
        source1.remove(value)
        assertFalse(this.property.contains(value))
        this.property.check(1)
        this.setChangeListener.assertRemoved(Tuple.tup(value))
        this.setChangeListener.clear()

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.setChangeListener.clear()

        // add element
        source2.add(value)
        assertTrue(this.property.contains(value))
        this.property.check(1)
        this.setChangeListener.assertAdded(Tuple.tup(value))
        this.setChangeListener.clear()

        // remove value
        source2.remove(value)
        assertFalse(this.property.contains(value))
        this.property.check(1)
        this.setChangeListener.assertRemoved(Tuple.tup(value))
        this.setChangeListener.clear()
    }

    @Test
    fun testSet_Invalidation() {
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
    fun testSet_Change() {
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
    fun testSet_SetChange() {
        attachSetChangeListener()

        // set value once
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.setChangeListener.assertMultipleCalls(Call(null, OBJECT_2a_0), Call(null, OBJECT_2a_1))

        // set same value again
        this.setChangeListener.clear()
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        assertEquals(0, this.setChangeListener.callsNumber)

        // set value twice without reading
        this.property.set(VALUE_1a)
        this.setChangeListener.clear()
        this.property.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.setChangeListener.assertAdded(Tuple.tup(OBJECT_1b))
    }

    @Test
    fun testValueSet_Invalidation() {
        attachInvalidationListener()

        // set value once
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set same value again
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value twice without reading
        this.property.value = VALUE_1a
        this.property.value = VALUE_1b
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testValueSet_Change() {
        attachChangeListener()

        // set value once
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.changeListener.check(this.property, null, VALUE_2a, 1)

        // set same value again
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)

        // set value twice without reading
        this.property.value = VALUE_1a
        this.property.value = VALUE_1b
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.changeListener.check(this.property, VALUE_1a, VALUE_1b, 2)
    }

    @Test
    fun testValueSet_SetChange() {
        attachSetChangeListener()

        // set value once
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.setChangeListener.assertMultipleCalls(Call(null, OBJECT_2a_0), Call(null, OBJECT_2a_1))

        // set same value again
        this.setChangeListener.clear()
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        assertEquals(0, this.setChangeListener.callsNumber)

        // set value twice without reading
        this.property.value = VALUE_1a
        this.setChangeListener.clear()
        this.property.value = VALUE_1b
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.setChangeListener.assertAdded(Tuple.tup(OBJECT_1b))
    }

    @Test
    fun testBind_Invalidation() {
        attachInvalidationListener()
        val v = ObservableObjectValueStub(ObservableCollections.observableSet(VALUE_1a))

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
    fun testBind_Change() {
        attachChangeListener()
        val v = ObservableObjectValueStub(ObservableCollections.observableSet(VALUE_1a))

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
    fun testBind_SetChange() {
        attachSetChangeListener()
        val v = ObservableObjectValueStub(ObservableCollections.observableSet(VALUE_1a))

        this.property.bind(v)
        assertEquals(VALUE_1a, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        assertEquals(0, this.setChangeListener.callsNumber)

        // change binding once
        this.setChangeListener.clear()
        v.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.setChangeListener.assertMultipleCalls(Call(null, OBJECT_2a_0), Call(null, OBJECT_2a_1))

        // change binding twice without reading
        v.set(VALUE_1a)
        this.setChangeListener.clear()
        v.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.setChangeListener.assertAdded(Tuple.tup(OBJECT_1b))

        // change binding twice to same value
        v.set(VALUE_1a)
        this.setChangeListener.clear()
        v.set(VALUE_1a)
        assertEquals(VALUE_1a, this.property.get())
        this.property.check(2)
        assertEquals(0, this.setChangeListener.callsNumber)
    }

    @Test
    fun testRebind() {
        attachInvalidationListener()
        val v1: SetProperty<Any> = SimpleSetProperty(VALUE_1a)
        val v2: SetProperty<Any> = SimpleSetProperty(VALUE_2a)
        this.property.bind(v1)
        this.property.get()
        this.property.reset()
        this.invalidationListener.reset()

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

        // change old binding
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
        val v: SetProperty<Any> = SimpleSetProperty(VALUE_1a)
        this.property.bind(v)
        this.property.unbind()
        assertEquals(VALUE_1a, this.property.get())
        assertFalse(this.property.bound)
        this.property.reset()
        this.invalidationListener.reset()

        // change binding
        v.set(VALUE_2a)
        assertEquals(VALUE_1a, this.property.get())
        this.property.check(0)
        this.invalidationListener.check(null, 0)

        // set value
        this.property.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    @Suppress("UNUSED_VALUE")
    fun testBindNull() {
        var property: SetPropertyMock? = SetPropertyMock()
        val v = ObservableSetValueStub(VALUE_1a)
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
        v.set(VALUE_2a)
        publicListener.reset()
        privateListener.reset()
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
        val v: SetProperty<Any> = SimpleSetProperty(VALUE_1a)
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
        val value0: ObservableSet<Any>? = null
        val value1 = ObservableCollections.observableSet(Any(), Any())
        val value2: ObservableSet<Any> = ObservableCollections.observableSet()
        val v: SetProperty<Any> = SimpleSetProperty(value2)

        property.set(value1)
        assertEquals("SetProperty [value: $value1]", property.toString())

        property.bind(v)
        assertEquals("SetProperty [bound, invalid]", property.toString())
        property.get()
        assertEquals("SetProperty [bound, value: $value2]", property.toString())
        v.set(value1)
        assertEquals("SetProperty [bound, invalid]", property.toString())
        property.get()
        assertEquals("SetProperty [bound, value: $value1]", property.toString())

        val bean = Any()
        val name = "My name"
        val v1: SetProperty<Any> = SetPropertyMock(bean, name)
        assertEquals("SetProperty [bean: $bean, name: My name, value: ${null}]", v1.toString())
        v1.set(value1)
        assertEquals("SetProperty [bean: $bean, name: My name, value: $value1]", v1.toString())
        v1.set(value0)
        assertEquals("SetProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v2: SetProperty<Any> = SetPropertyMock(bean, NO_NAME_1)
        assertEquals("SetProperty [bean: $bean, value: null]", v2.toString())
        v2.set(value1)
        assertEquals("SetProperty [bean: $bean, value: $value1]", v2.toString())
        v1.set(value0)
        assertEquals("SetProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v3: SetProperty<Any> = SetPropertyMock(bean, NO_NAME_2)
        assertEquals("SetProperty [bean: $bean, value: ${null}]", v3.toString())
        v3.set(value1)
        assertEquals("SetProperty [bean: $bean, value: $value1]", v3.toString())
        v1.set(value0)
        assertEquals("SetProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v4: SetProperty<Any> = SetPropertyMock(NO_BEAN, name)
        assertEquals("SetProperty [name: My name, value: ${null}]", v4.toString())
        v4.set(value1)
        v1.set(value0)
        assertEquals("SetProperty [bean: $bean, name: My name, value: $value0]", v1.toString())
        assertEquals("SetProperty [name: My name, value: $value1]", v4.toString())
    }

    private class SetPropertyMock : SetPropertyBase<Any> {

        override val bean: Any?

        override val name: String?

        var counter: Int = 0

        constructor(bean: Any?, name: String?) : super() {
            this.bean = bean
            this.name = name
        }

        constructor(initialValue: ObservableSet<Any>) : super(initialValue) {
            this.bean = NO_BEAN
            this.name = NO_NAME_1
        }

        constructor() : this(NO_BEAN, NO_NAME_1)

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

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

        private val OBJECT_1b: Any = Any()

        private val OBJECT_2a_0: Any = Any()

        private val OBJECT_2a_1: Any = Any()

        private val OBJECT_2b_0: Any = Any()

        private val OBJECT_2b_1: Any = Any()

        private val OBJECT_2b_2: Any = Any()

        private val UNDEFINED: ObservableSet<Any> = ObservableCollections.observableSet()

        private val VALUE_1a: ObservableSet<Any> = ObservableCollections.observableSet()

        private val VALUE_1b: ObservableSet<Any> = ObservableCollections.observableSet(OBJECT_1b)

        private val VALUE_2a: ObservableSet<Any> = ObservableCollections.observableSet(OBJECT_2a_0, OBJECT_2a_1)

        private val VALUE_2b: ObservableSet<Any> =
                ObservableCollections.observableSet(OBJECT_2b_0, OBJECT_2b_1, OBJECT_2b_2)

    }

}