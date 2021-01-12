package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableValueBase
import io.github.vinccool96.observationskt.collections.MockMapObserver
import io.github.vinccool96.observationskt.collections.MockMapObserver.Call
import io.github.vinccool96.observationskt.collections.MockMapObserver.Tuple
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.collections.HashMap
import kotlin.test.*

@Suppress("RedundantNullableReturnType")
class MapBindingTest {

    private lateinit var dependency1: ObservableStub

    private lateinit var dependency2: ObservableStub

    private lateinit var binding0: MapBindingImpl

    private lateinit var binding1: MapBindingImpl

    private lateinit var binding2: MapBindingImpl

    private lateinit var emptyMap: ObservableMap<Any, Any>

    private lateinit var map1: ObservableMap<Any, Any>

    private lateinit var map2: ObservableMap<Any, Any>

    private lateinit var listener: MockMapObserver<Any, Any>

    @Before
    fun setUp() {
        this.dependency1 = ObservableStub()
        this.dependency2 = ObservableStub()
        this.binding0 = MapBindingImpl()
        this.binding1 = MapBindingImpl(dependency1)
        this.binding2 = MapBindingImpl(dependency1, dependency2)
        this.emptyMap = ObservableCollections.observableMap(Collections.emptyMap())
        this.map1 = ObservableCollections.observableMap(Collections.singletonMap(KEY_1, DATA_1))
        val map: HashMap<Any, Any> = HashMap()
        map[KEY_2_0] = DATA_2_0
        map[KEY_2_1] = DATA_2_1
        this.map2 = ObservableCollections.observableMap(map)
        this.listener = MockMapObserver()
        this.binding0.value = this.map2
        this.binding1.value = this.map2
        this.binding2.value = this.map2
    }

    @Test
    fun testSizeProperty() {
        assertSame(this.binding0, this.binding0.sizeProperty.bean)
        assertSame(this.binding1, this.binding1.sizeProperty.bean)
        assertSame(this.binding2, this.binding2.sizeProperty.bean)

        val size = this.binding1.sizeProperty
        assertEquals("size", size.name)

        assertEquals(2, size.get())
        this.binding1.value = this.emptyMap
        this.dependency1.fireValueChangedEvent()
        assertEquals(0, size.get())
        this.binding1.value = this.map1
        this.dependency1.fireValueChangedEvent()
        assertEquals(1, size.get())
        this.binding1.value = null
        this.dependency1.fireValueChangedEvent()
        assertEquals(0, size.get())
    }

    @Test
    fun testEmptyProperty() {
        assertSame(this.binding0, this.binding0.emptyProperty.bean)
        assertSame(this.binding1, this.binding1.emptyProperty.bean)
        assertSame(this.binding2, this.binding2.emptyProperty.bean)

        val empty = this.binding1.emptyProperty
        assertEquals("empty", empty.name)

        assertFalse(empty.get())
        this.binding1.value = this.emptyMap
        this.dependency1.fireValueChangedEvent()
        assertTrue(empty.get())
        this.binding1.value = this.map1
        this.dependency1.fireValueChangedEvent()
        assertFalse(empty.get())
        this.binding1.value = null
        this.dependency1.fireValueChangedEvent()
        assertTrue(empty.get())
    }

    @Test
    fun testNoDependency_MapChangeListener() {
        this.binding0.value
        this.binding0.addListener(this.listener)
        System.gc() // making sure we did not not overdo weak references
        assertEquals(true, this.binding0.valid)

        // calling value
        this.binding0.reset()
        this.binding0.value
        assertEquals(0, this.binding0.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding0.valid)
    }

    @Test
    fun testSingleDependency_MapChangeListener() {
        this.binding1.value
        this.binding1.addListener(this.listener)
        System.gc() // making sure we did not not overdo weak references
        assertEquals(true, this.binding1.valid)

        // fire single change event
        this.binding1.reset()
        this.listener.clear()
        this.binding1.value = this.map1
        this.dependency1.fireValueChangedEvent()
        assertEquals(1, this.binding1.computeValueCounter)
        this.listener.assertMultipleCalls(Call(KEY_2_0, DATA_2_0, null), Call(KEY_2_1, DATA_2_1, null),
                Call(KEY_1, null, DATA_1))
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        // fire single change event with same value
        this.binding1.value = this.map1
        this.dependency1.fireValueChangedEvent()
        assertEquals(1, this.binding1.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        // fire two change events
        this.binding1.value = this.map2
        this.dependency1.fireValueChangedEvent()
        this.listener.clear()
        this.binding1.value = this.map1
        this.dependency1.fireValueChangedEvent()
        assertEquals(2, this.binding1.computeValueCounter)
        this.listener.assertMultipleCalls(Call(KEY_2_0, DATA_2_0, null), Call(KEY_2_1, DATA_2_1, null),
                Call(KEY_1, null, DATA_1))
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        // fire two change events with same value
        this.binding1.value = this.map2
        this.dependency1.fireValueChangedEvent()
        this.binding1.value = this.map2
        this.dependency1.fireValueChangedEvent()
        assertEquals(2, this.binding1.computeValueCounter)
        this.listener.assertMultipleCalls(Call(KEY_1, DATA_1, null), Call(KEY_2_0, null, DATA_2_0),
                Call(KEY_2_1, null, DATA_2_1))
        assertEquals(true, binding1.valid)
        this.listener.clear()

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding1.valid)
    }

    @Test
    fun testChangeContent_InvalidationListener() {
        val listenerMock = InvalidationListenerMock()
        this.binding1.get()
        this.binding1.addListener(listenerMock)
        assertTrue(this.binding1.valid)

        this.binding1.reset()
        listenerMock.reset()
        this.map2[Any()] = Any()
        assertEquals(0, this.binding1.computeValueCounter)
        listenerMock.check(this.binding1, 1)
        assertTrue(this.binding1.valid)
    }

    @Test
    fun testChangeContent_ChangeListener() {
        val listenerMock = ChangeListenerMock<ObservableMap<Any, Any>?>(null)
        this.binding1.get()
        this.binding1.addListener(listenerMock)
        assertTrue(this.binding1.valid)

        this.binding1.reset()
        listenerMock.reset()
        this.map2[Any()] = Any()
        assertEquals(0, this.binding1.computeValueCounter)
        listenerMock.check(this.binding1, this.map2, this.map2, 1)
        assertTrue(this.binding1.valid)
    }

    @Test
    fun testChangeContent_MapChangeListener() {
        this.binding1.get()
        this.binding1.addListener(this.listener)
        assertTrue(this.binding1.valid)

        val newKey = Any()
        val newData = Any()
        this.binding1.reset()
        this.listener.clear()
        this.map2[newKey] = newData
        assertEquals(0, this.binding1.computeValueCounter)
        this.listener.assertAdded(Tuple.tup(newKey, newData))
        assertTrue(this.binding1.valid)
    }

    private class ObservableStub : ObservableValueBase<Any?>() {

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

        override val value: Any?
            get() = null

    }

    private class MapBindingImpl(vararg dep: Observable) : MapBinding<Any, Any>() {

        private var computeValueCounterState = 0

        private var valueState: ObservableMap<Any, Any>? = null

        init {
            super.bind(*dep)
        }

        override var value: ObservableMap<Any, Any>?
            get() = super.value
            set(value) {
                this.valueState = value
            }

        val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): ObservableMap<Any, Any>? {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    companion object {

        private val KEY_1: Any = Any()

        private val KEY_2_0: Any = Any()

        private val KEY_2_1: Any = Any()

        private val DATA_1: Any = Any()

        private val DATA_2_0: Any = Any()

        private val DATA_2_1: Any = Any()

    }

}