package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableValueBase
import io.github.vinccool96.observationskt.collections.MockSetObserver
import io.github.vinccool96.observationskt.collections.MockSetObserver.Call
import io.github.vinccool96.observationskt.collections.MockSetObserver.Tuple
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection
import org.junit.Before
import org.junit.Test
import kotlin.test.*

class SetBindingTest {

    private lateinit var dependency1: ObservableStub

    private lateinit var dependency2: ObservableStub

    private lateinit var binding0: SetBindingImpl

    private lateinit var binding1: SetBindingImpl

    private lateinit var binding2: SetBindingImpl

    private lateinit var emptySet: ObservableSet<Any>

    private lateinit var set1: ObservableSet<Any>

    private lateinit var set2: ObservableSet<Any>

    private lateinit var listener: MockSetObserver<Any>

    @Before
    fun setUp() {
        this.dependency1 = ObservableStub()
        this.dependency2 = ObservableStub()
        this.binding0 = SetBindingImpl()
        this.binding1 = SetBindingImpl(this.dependency1)
        this.binding2 = SetBindingImpl(this.dependency1, this.dependency2)
        this.emptySet = ObservableCollections.observableSet()
        this.set1 = ObservableCollections.observableSet(DATA_1)
        this.set2 = ObservableCollections.observableSet(DATA_2_0, DATA_2_1)
        this.listener = MockSetObserver()
        this.binding0.value = this.set2
        this.binding1.value = this.set2
        this.binding2.value = this.set2
    }

    @Test
    fun testSizeProperty() {
        assertSame(this.binding0, this.binding0.sizeProperty.bean)
        assertSame(this.binding1, this.binding1.sizeProperty.bean)
        assertSame(this.binding2, this.binding2.sizeProperty.bean)

        val size = this.binding1.sizeProperty
        assertEquals("size", size.name)

        assertEquals(2, size.get())
        this.binding1.value = this.emptySet
        this.dependency1.fireValueChangedEvent()
        assertEquals(0, size.get())
        this.binding1.value = null
        this.dependency1.fireValueChangedEvent()
        assertEquals(0, size.get())
        this.binding1.value = this.set1
        this.dependency1.fireValueChangedEvent()
        assertEquals(1, size.get())
    }

    @Test
    fun testEmptyProperty() {
        assertSame(this.binding0, this.binding0.emptyProperty.bean)
        assertSame(this.binding1, this.binding1.emptyProperty.bean)
        assertSame(this.binding2, this.binding2.emptyProperty.bean)

        val empty = this.binding1.emptyProperty
        assertEquals("empty", empty.name)

        assertFalse(empty.get())
        this.binding1.value = this.emptySet
        this.dependency1.fireValueChangedEvent()
        assertTrue(empty.get())
        this.binding1.value = null
        this.dependency1.fireValueChangedEvent()
        assertTrue(empty.get())
        this.binding1.value = this.set1
        this.dependency1.fireValueChangedEvent()
        assertFalse(empty.get())
    }

    @Test
    fun testNoDependency_SetChangeListener() {
        this.binding0.value
        this.binding0.addListener(this.listener)
        System.gc() // making sure we did not overdo weak references
        assertEquals(true, this.binding0.valid)

        // calling value
        this.binding0.reset()
        this.binding0.value
        assertEquals(0, this.binding0.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding0.valid)
    }

    @Test
    fun testSingleDependency_SetChangeListener() {
        this.binding1.value
        this.binding1.addListener(this.listener)
        System.gc() // making sure we did not overdo weak references
        assertEquals(true, this.binding1.valid)

        // fire single change event
        this.binding1.reset()
        this.listener.clear()
        this.binding1.value = this.set1
        this.dependency1.fireValueChangedEvent()
        assertEquals(1, this.binding1.computeValueCounter)
        this.listener.assertMultipleCalls(Call(DATA_2_0, null), Call(DATA_2_1, null), Call(null, DATA_1))
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        // fire single change event with same value
        this.binding1.value = this.set1
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
        this.binding1.value = this.set2
        this.dependency1.fireValueChangedEvent()
        this.listener.clear()
        this.binding1.value = this.set1
        this.dependency1.fireValueChangedEvent()
        assertEquals(2, this.binding1.computeValueCounter)
        this.listener.assertMultipleCalls(Call(DATA_2_0, null), Call(DATA_2_1, null), Call(null, DATA_1))
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        // fire two change events with same value
        this.binding1.value = this.set2
        this.dependency1.fireValueChangedEvent()
        this.binding1.value = this.set2
        this.dependency1.fireValueChangedEvent()
        assertEquals(2, this.binding1.computeValueCounter)
        this.listener.assertMultipleCalls(Call(DATA_1, null), Call(null, DATA_2_0), Call(null, DATA_2_1))
        assertEquals(true, this.binding1.valid)
        this.listener.clear()

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        assertEquals(0, this.listener.callsNumber)
        assertEquals(true, this.binding1.valid)
        this.listener.clear()
    }

    @Test
    fun testChangeContent_InvalidationListener() {
        val listenerMock = InvalidationListenerMock()
        this.binding1.get()
        this.binding1.addListener(listenerMock)
        assertTrue(this.binding1.valid)

        this.binding1.reset()
        listenerMock.reset()
        this.set2.add(Any())
        assertEquals(0, this.binding1.computeValueCounter)
        listenerMock.check(this.binding1, 1)
        assertTrue(this.binding1.valid)
    }

    @Test
    fun testChangeContent_ChangeListener() {
        val listenerMock: ChangeListenerMock<ObservableSet<Any>?> = ChangeListenerMock(null)
        this.binding1.get()
        this.binding1.addListener(listenerMock)
        assertTrue(this.binding1.valid)

        this.binding1.reset()
        listenerMock.reset()
        this.set2.add(Any())
        assertEquals(0, this.binding1.computeValueCounter)
        listenerMock.check(this.binding1, this.set2, this.set2, 1)
        assertTrue(this.binding1.valid)
    }

    @Test
    fun testChangeContent_SetChangeListener() {
        this.binding1.get()
        this.binding1.addListener(this.listener)
        assertTrue(this.binding1.valid)

        this.binding1.reset()
        this.listener.clear()
        val newObject = Any()
        this.set2.add(newObject)
        assertEquals(0, this.binding1.computeValueCounter)
        this.listener.assertAdded(Tuple.tup(newObject))
        assertTrue(this.binding1.valid)

        this.binding1.removeListener(this.listener)
        this.binding1.reset()
        this.listener.clear()
        this.set2.add(Any())
        assertEquals(0, this.binding1.computeValueCounter)
        this.listener.check0()
        assertTrue(this.binding1.valid)
    }

    @Test
    fun testDefaultDependencies() {
        assertTrue(SetBindingMock().dependencies.isEmpty())
    }

    private class SetBindingMock : SetBinding<String>() {

        override fun computeValue(): ObservableSet<String>? {
            return null
        }

    }

    private class ObservableStub : ObservableValueBase<Any>() {

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

        override val value: Any
            get() = Any()

    }

    private class SetBindingImpl(vararg dep: Observable) : SetBinding<Any>() {

        private var computeValueCounterState: Int = 0

        private var valueState: ObservableSet<Any>? = null

        override var value: ObservableSet<Any>?
            get() = super.value
            set(value) {
                this.valueState = value
            }

        init {
            super.bind(*dep)
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

        override fun computeValue(): ObservableSet<Any>? {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    companion object {

        private val DATA_1: Any = Any()

        private val DATA_2_0: Any = Any()

        private val DATA_2_1: Any = Any()

    }

}