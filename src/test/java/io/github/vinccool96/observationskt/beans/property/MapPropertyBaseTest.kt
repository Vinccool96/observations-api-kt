package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableMapValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableObjectValueStub
import io.github.vinccool96.observationskt.collections.MockMapObserver
import io.github.vinccool96.observationskt.collections.MockMapObserver.Call
import io.github.vinccool96.observationskt.collections.MockMapObserver.Tuple
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableMap
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.*

class MapPropertyBaseTest {

    private lateinit var property: MapPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<ObservableMap<Any, Any>?>

    private lateinit var mapChangeListener: MockMapObserver<Any, Any>

    @Before
    fun setUp() {
        this.property = MapPropertyMock()
        this.invalidationListener = InvalidationListenerMock()
        this.changeListener = ChangeListenerMock(UNDEFINED)
        this.mapChangeListener = MockMapObserver()
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

    private fun attachMapChangeListener() {
        this.property.addListener(this.mapChangeListener)
        this.property.get()
        this.mapChangeListener.clear()
    }

    @Test
    fun testConstructor() {
        val p1: MapProperty<Any, Any> = SimpleMapProperty()
        assertEquals(null, p1.get())
        assertEquals(null, p1.value)
        assertFalse(p1.bound)

        val p2: MapProperty<Any, Any> = SimpleMapProperty(VALUE_1b)
        assertEquals(VALUE_1b, p2.get())
        assertEquals(VALUE_1b, p2.value)
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
    fun testMapChangeListener() {
        attachMapChangeListener()
        this.property.set(VALUE_2a)
        this.mapChangeListener.assertMultipleCalls(Call(KEY_2a_0, null, DATA_2a_0), Call(KEY_2a_1, null, DATA_2a_1))
        this.property.removeListener(this.mapChangeListener)
        this.mapChangeListener.clear()
        this.property.set(VALUE_1a)
        assertEquals(0, this.mapChangeListener.callsNumber)
    }

    @Test
    fun testSourceMap_Invalidation() {
        val source1: ObservableMap<Any, Any> = ObservableCollections.observableMap(HashMap())
        val source2: ObservableMap<Any, Any> = ObservableCollections.observableMap(HashMap())
        val key = Any()
        val value1 = Any()
        val value2 = Any()

        // constructor
        this.property = MapPropertyMock(source1)
        this.property.reset()
        attachInvalidationListener()

        // add element
        source1[key] = value1
        assertEquals(value1, this.property[key])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // replace element
        source1[key] = value2
        assertEquals(value2, this.property[key])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // remove element
        source1.remove(key)
        assertNull(this.property[key])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.invalidationListener.reset()

        // add element
        source2[key] = value1
        assertEquals(value1, this.property[key])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // replace element
        source2[key] = value2
        assertEquals(value2, this.property[key])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // remove element
        source2.remove(key)
        assertNull(this.property[key])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testSourceMap_Change() {
        val source1: ObservableMap<Any, Any> = ObservableCollections.observableMap(HashMap())
        val source2: ObservableMap<Any, Any> = ObservableCollections.observableMap(HashMap())
        val key = Any()
        val value1 = Any()
        val value2 = Any()

        // constructor
        this.property = MapPropertyMock(source1)
        this.property.reset()
        attachChangeListener()

        // add element
        source1[key] = value1
        assertEquals(value1, this.property[key])
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // replace element
        source1[key] = value2
        assertEquals(value2, this.property[key])
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // remove element
        source1.remove(key)
        assertNull(this.property[key])
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.changeListener.reset()

        // add element
        source2[key] = value1
        assertEquals(value1, this.property[key])
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)

        // replace element
        source2[key] = value2
        assertEquals(value2, this.property[key])
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)

        // remove element
        source2.remove(key)
        assertNull(this.property[key])
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)
    }

    @Test
    fun testSourceMap_MapChange() {
        val source1: ObservableMap<Any, Any> = ObservableCollections.observableMap(HashMap())
        val source2: ObservableMap<Any, Any> = ObservableCollections.observableMap(HashMap())
        val key = Any()
        val value1 = Any()
        val value2 = Any()

        // constructor
        this.property = MapPropertyMock(source1)
        this.property.reset()
        attachMapChangeListener()

        // add element
        source1[key] = value1
        assertEquals(value1, this.property[key])
        this.property.check(1)
        this.mapChangeListener.assertAdded(Tuple.tup(key, value1))
        this.mapChangeListener.clear()

        // replace element
        source1[key] = value2
        assertEquals(value2, this.property[key])
        this.property.check(1)
        this.mapChangeListener.assertMultipleCalls(Call(key, value1, value2))
        this.mapChangeListener.clear()

        // remove element
        source1.remove(key)
        assertNull(this.property[key])
        this.property.check(1)
        this.mapChangeListener.assertRemoved(Tuple.tup(key, value2))
        this.mapChangeListener.clear()

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.mapChangeListener.clear()

        // add element
        source2[key] = value1
        assertEquals(value1, this.property[key])
        this.property.check(1)
        this.mapChangeListener.assertAdded(Tuple.tup(key, value1))
        this.mapChangeListener.clear()

        // replace element
        source2[key] = value2
        assertEquals(value2, this.property[key])
        this.property.check(1)
        this.mapChangeListener.assertMultipleCalls(Call(key, value1, value2))
        this.mapChangeListener.clear()

        // remove element
        source2.remove(key)
        assertNull(this.property[key])
        this.property.check(1)
        this.mapChangeListener.assertRemoved(Tuple.tup(key, value2))
        this.mapChangeListener.clear()
    }

    @Test
    fun testMap_Invalidation() {
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
    fun testMap_Change() {
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
    fun testMap_MapChange() {
        attachMapChangeListener()

        // set value once
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.mapChangeListener.assertMultipleCalls(Call(KEY_2a_0, null, DATA_2a_0), Call(KEY_2a_1, null, DATA_2a_1))

        // set same value again
        this.mapChangeListener.clear()
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        assertEquals(0, this.mapChangeListener.callsNumber)

        // set value twice without reading
        this.property.set(VALUE_1a)
        this.mapChangeListener.clear()
        this.property.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.mapChangeListener.assertAdded(Tuple(KEY_1b, DATA_1b))
    }

    @Test
    fun testMapValue_Invalidation() {
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
    fun testMapValue_Change() {
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
    fun testMapValue_MapChange() {
        attachMapChangeListener()

        // set value once
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.mapChangeListener.assertMultipleCalls(Call(KEY_2a_0, null, DATA_2a_0), Call(KEY_2a_1, null, DATA_2a_1))

        // set same value again
        this.mapChangeListener.clear()
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        assertEquals(0, this.mapChangeListener.callsNumber)

        // set value twice without reading
        this.property.value = VALUE_1a
        this.mapChangeListener.clear()
        this.property.value = VALUE_1b
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.mapChangeListener.assertAdded(Tuple(KEY_1b, DATA_1b))
    }

    @Test(expected = RuntimeException::class)
    fun testMapBoundValue() {
        val v: MapProperty<Any, Any> = SimpleMapProperty(VALUE_1a)
        this.property.bind(v)
        this.property.set(VALUE_1b)
    }

    @Test
    fun testBind_Invalidation() {
        attachInvalidationListener()
        val v = ObservableObjectValueStub(ObservableCollections.observableMap(VALUE_1a))

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
        val v = ObservableObjectValueStub(ObservableCollections.observableMap(VALUE_1a))

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
    fun testBind_MapChange() {
        attachMapChangeListener()
        val v = ObservableObjectValueStub(ObservableCollections.observableMap(VALUE_1a))

        this.property.bind(v)
        assertEquals(VALUE_1a, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        assertEquals(0, this.mapChangeListener.callsNumber)

        // change binding once
        this.mapChangeListener.clear()
        v.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.mapChangeListener.assertMultipleCalls(Call(KEY_2a_0, null, DATA_2a_0), Call(KEY_2a_1, null, DATA_2a_1))

        // change binding twice without reading
        v.set(VALUE_1a)
        this.mapChangeListener.clear()
        v.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.mapChangeListener.assertAdded(Tuple.tup(KEY_1b, DATA_1b))

        // change binding twice to same value
        v.set(VALUE_1a)
        this.mapChangeListener.clear()
        v.set(VALUE_1a)
        assertEquals(VALUE_1a, this.property.get())
        this.property.check(2)
        assertEquals(0, this.mapChangeListener.callsNumber)
    }

    @Test
    fun testRebind() {
        attachInvalidationListener()
        val v1: MapProperty<Any, Any> = SimpleMapProperty(VALUE_1a)
        val v2: MapProperty<Any, Any> = SimpleMapProperty(VALUE_2a)
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
        val v: MapProperty<Any, Any> = SimpleMapProperty(VALUE_1a)
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
        var property: MapPropertyMock? = MapPropertyMock()
        val v = ObservableMapValueStub(VALUE_1a)
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
        val v: MapProperty<Any, Any> = SimpleMapProperty(VALUE_1a)
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
        val value0: ObservableMap<Any, Any>? = null
        val value1: ObservableMap<Any, Any> = ObservableCollections.observableMap(HashMap())
        value1[Any()] = Any()
        value1[Any()] = Any()
        val value2: ObservableMap<Any, Any> = ObservableCollections.observableMap(HashMap())
        val v: MapProperty<Any, Any> = SimpleMapProperty(value2)

        this.property.set(value1)
        assertEquals("MapProperty [value: $value1]", this.property.toString())

        this.property.bind(v)
        assertEquals("MapProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("MapProperty [bound, value: $value2]", this.property.toString())
        v.set(value1)
        assertEquals("MapProperty [bound, invalid]", this.property.toString())
        this.property.get()
        assertEquals("MapProperty [bound, value: $value1]", this.property.toString())

        val bean = Any()
        val name = "My name"
        val v1: MapProperty<Any, Any> = MapPropertyMock(bean, name)
        assertEquals("MapProperty [bean: $bean, name: My name, value: null]", v1.toString())
        v1.set(value1)
        assertEquals("MapProperty [bean: $bean, name: My name, value: $value1]", v1.toString())
        v1.set(value0)
        assertEquals("MapProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v2: MapProperty<Any, Any> = MapPropertyMock(bean, NO_NAME_1)
        assertEquals("MapProperty [bean: $bean, value: null]", v2.toString())
        v2.set(value1)
        assertEquals("MapProperty [bean: $bean, value: $value1]", v2.toString())
        v1.set(value0)
        assertEquals("MapProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v3: MapProperty<Any, Any> = MapPropertyMock(bean, NO_NAME_2)
        assertEquals("MapProperty [bean: $bean, value: null]", v3.toString())
        v3.set(value1)
        assertEquals("MapProperty [bean: $bean, value: $value1]", v3.toString())
        v1.set(value0)
        assertEquals("MapProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v4: MapProperty<Any, Any> = MapPropertyMock(NO_BEAN, name)
        assertEquals("MapProperty [name: My name, value: " + null + "]", v4.toString())
        v4.set(value1)
        v1.set(value0)
        assertEquals("MapProperty [bean: $bean, name: My name, value: $value0]", v1.toString())
        assertEquals("MapProperty [name: My name, value: $value1]", v4.toString())
    }

    private class MapPropertyMock : MapPropertyBase<Any, Any> {

        override val bean: Any?

        override val name: String?

        var counter: Int = 0

        constructor(bean: Any?, name: String?) : super() {
            this.bean = bean
            this.name = name
        }

        constructor(initialValue: ObservableMap<Any, Any>) : super(initialValue) {
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

        private val KEY_1b: Any = Any()

        private val DATA_1b: Any = Any()

        private val KEY_2a_0: Any = Any()

        private val DATA_2a_0: Any = Any()

        private val KEY_2a_1: Any = Any()

        private val DATA_2a_1: Any = Any()

        private val KEY_2b_0: Any = Any()

        private val DATA_2b_0: Any = Any()

        private val KEY_2b_1: Any = Any()

        private val DATA_2b_1: Any = Any()

        private val KEY_2b_2: Any = Any()

        private val DATA_2b_2: Any = Any()

        private val UNDEFINED: ObservableMap<Any, Any> = ObservableCollections.observableMap(Collections.emptyMap())

        private val VALUE_1a: ObservableMap<Any, Any> = ObservableCollections.observableMap(Collections.emptyMap())

        private val VALUE_1b: ObservableMap<Any, Any> =
                ObservableCollections.observableMap(Collections.singletonMap(KEY_1b, DATA_1b))

        private val VALUE_2a: ObservableMap<Any, Any> = ObservableCollections.observableHashMap(KEY_2a_0 to DATA_2a_0,
                KEY_2a_1 to DATA_2a_1)

        private val VALUE_2b: ObservableMap<Any, Any> = ObservableCollections.observableHashMap(KEY_2b_0 to DATA_2b_0,
                KEY_2b_1 to DATA_2b_1, KEY_2b_2 to DATA_2b_2)

    }

}