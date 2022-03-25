package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableValueBase
import io.github.vinccool96.observationskt.collections.ArrayChangeListener
import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection
import kotlin.test.*

class ArrayBindingTest {

    private lateinit var dependency: ObservableStub

    private lateinit var binding0: ArrayBindingImpl

    private lateinit var binding1: ArrayBindingImpl

    private lateinit var emptyArray: ObservableArray<Any>

    private lateinit var array1: ObservableArray<Any>

    private lateinit var array2: ObservableArray<Any>

    private lateinit var listener: ArrayChangeListenerMock

    @BeforeTest
    fun setUp() {
        this.dependency = ObservableStub()
        this.binding0 = ArrayBindingImpl()
        this.binding1 = ArrayBindingImpl(this.dependency)
        this.emptyArray = ObservableCollections.observableObjectArray(arrayOf(Any()))
        this.array1 = ObservableCollections.observableObjectArray(arrayOf(Any()), Any())
        this.array2 = ObservableCollections.observableObjectArray(arrayOf(Any()), Any(), Any())
        this.listener = ArrayChangeListenerMock()
        this.binding0.value = this.array2
        this.binding1.value = this.array2
    }

    @Test
    fun testSizeProperty() {
        val dependency2 = ObservableStub()
        val binding2 = ArrayBindingImpl(this.dependency, dependency2)
        binding2.value = this.array2

        assertSame(this.binding0, this.binding0.sizeProperty.bean)
        assertSame(this.binding1, this.binding1.sizeProperty.bean)
        assertSame(binding2, binding2.sizeProperty.bean)

        val size = this.binding1.sizeProperty
        assertEquals("size", size.name)

        assertEquals(2, size.get())
        this.binding1.value = this.emptyArray
        this.dependency.fireValueChangedEvent()
        assertEquals(0, size.get())
        this.binding1.value = this.array1
        this.dependency.fireValueChangedEvent()
        assertEquals(1, size.get())
        this.binding1.value = null
        this.dependency.fireValueChangedEvent()
        assertEquals(0, size.get())

        assertEquals(2, binding2.sizeProperty.get())
        binding2.value = this.emptyArray
        dependency2.fireValueChangedEvent()
        assertEquals(0, binding2.sizeProperty.get())
    }

    @Test
    fun testEmptyProperty() {
        val dependency2 = ObservableStub()
        val binding2 = ArrayBindingImpl(this.dependency, dependency2)
        binding2.value = this.array2

        assertSame(this.binding0, this.binding0.emptyProperty.bean)
        assertSame(this.binding1, this.binding1.emptyProperty.bean)
        assertSame(binding2, binding2.emptyProperty.bean)

        val empty = this.binding1.emptyProperty
        assertEquals("empty", empty.name)

        assertFalse(empty.get())
        this.binding1.value = this.emptyArray
        this.dependency.fireValueChangedEvent()
        assertTrue(empty.get())
        this.binding1.value = this.array1
        this.dependency.fireValueChangedEvent()
        assertFalse(empty.get())
        this.binding1.value = null
        this.dependency.fireValueChangedEvent()
        assertTrue(empty.get())

        assertFalse(binding2.emptyProperty.get())
        binding2.value = this.emptyArray
        dependency2.fireValueChangedEvent()
        assertTrue(binding2.emptyProperty.get())
    }

    @Test
    fun testNoDependency_ArrayChangeListener() {
        this.binding0.value
        this.binding0.addListener(this.listener)
        System.gc() // making sure we did not overdo weak references
        assertEquals(true, this.binding0.valid)

        // calling value's getter
        this.binding0.reset()
        this.binding0.value
        assertEquals(0, this.binding0.computeValueCounter)
        this.listener.checkNotCalled()
        assertEquals(true, this.binding0.valid)
    }

    @Test
    fun testSingleDependency_ArrayChangeListener() {
        this.binding1.value
        this.binding1.addListener(this.listener)
        System.gc() // making sure we did not overdo weak references
        assertEquals(true, this.binding1.valid)

        // fire single change event
        this.binding1.reset()
        this.listener.reset()
        this.binding1.value = this.array1
        this.dependency.fireValueChangedEvent()
        assertEquals(1, this.binding1.computeValueCounter)
        this.listener.check(this.array2, this.array1, 1)
        assertEquals(true, this.binding1.valid)

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        this.listener.checkNotCalled()
        assertEquals(true, this.binding1.valid)

        // fire single change event with same value
        this.binding1.value = this.array1
        this.dependency.fireValueChangedEvent()
        assertEquals(1, this.binding1.computeValueCounter)
        this.listener.checkNotCalled()
        assertEquals(true, this.binding1.valid)

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        this.listener.checkNotCalled()
        assertEquals(true, this.binding1.valid)

        // fire two change events
        this.binding1.value = this.array2
        this.dependency.fireValueChangedEvent()
        this.binding1.value = this.array1
        this.dependency.fireValueChangedEvent()
        assertEquals(2, this.binding1.computeValueCounter)
        this.listener.check(this.array2, this.array1, 2)
        assertEquals(true, this.binding1.valid)

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        this.listener.checkNotCalled()
        assertEquals(true, this.binding1.valid)

        // fire two change events with same value
        this.binding1.value = this.array2
        this.dependency.fireValueChangedEvent()
        this.binding1.value = this.array2
        this.dependency.fireValueChangedEvent()
        assertEquals(2, this.binding1.computeValueCounter)
        this.listener.check(this.array1, this.array2, 1)
        assertEquals(true, binding1.valid)

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        this.listener.checkNotCalled()
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
        this.array2 += arrayOf(Any())
        assertEquals(0, this.binding1.computeValueCounter)
        listenerMock.check(this.binding1, 1)
        assertTrue(this.binding1.valid)
    }

    @Test
    fun testChangeContent_ChangeListener() {
        val listenerMock = ChangeListenerMock<Any?>(null)
        this.binding1.get()
        this.binding1.addListener(listenerMock)
        assertTrue(this.binding1.valid)

        this.binding1.reset()
        listenerMock.reset()
        this.array2 += arrayOf(Any())
        assertEquals(0, this.binding1.computeValueCounter)
        listenerMock.check(this.binding1, this.array2, this.array2, 1)
        assertTrue(this.binding1.valid)
    }

    @Test
    fun testChangeContent_ArrayChangeListener() {
        this.binding1.get()
        this.binding1.addListener(this.listener)
        assertTrue(this.binding1.valid)

        val oldSize = this.array2.size
        val newObject = Any()
        this.binding1.reset()
        this.listener.reset()
        this.array2 += arrayOf(newObject)
        assertEquals(0, this.binding1.computeValueCounter)
        this.listener.check(oldSize, newObject, 1)
        assertTrue(this.binding1.valid)

        this.binding1.removeListener(this.listener)
        this.binding1.reset()
        this.listener.reset()
        this.array2 += arrayOf(Any())
        assertEquals(0, this.binding1.computeValueCounter)
        this.listener.checkNotCalled()
        assertTrue(this.binding1.valid)
    }

    @Test
    fun testDefaultDependencies() {
        assertTrue(ArrayBindingMock().dependencies.isEmpty())
    }

    private class ArrayBindingMock : ArrayBinding<String>(arrayOf("")) {

        override fun computeValue(): ObservableArray<String>? {
            return null
        }

    }

    private class ObservableStub : ObservableValueBase<Any>() {

        override val value: Any = Any()

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

    }

    private class ArrayBindingImpl(vararg dep: Observable) : ArrayBinding<Any>(arrayOf(Any())) {

        init {
            super.bind(*dep)
        }

        private var counter: Int = 0

        private var array: ObservableArray<Any>? = null

        override var value: ObservableArray<Any>?
            get() = this.get()
            set(value) {
                this.array = value
            }

        val computeValueCounter: Int
            get() {
                val result = this.counter
                reset()
                return result
            }

        fun reset() {
            this.counter = 0
        }

        override fun computeValue(): ObservableArray<Any>? {
            this.counter++
            return this.array
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() {
                fail("Should not reach here")
            }

    }

    private class ArrayChangeListenerMock : ArrayChangeListener<Any> {

        private var change: Change<out Any>? = null

        private var counter: Int = 0

        override fun onChanged(change: Change<out Any>) {
            this.change = change
            this.counter++
        }

        fun reset() {
            this.change = null
            this.counter = 0
        }

        fun checkNotCalled() {
            assertEquals(null, this.change)
            assertEquals(0, this.counter)
            reset()
        }

        fun check(oldArray: ObservableArray<Any>, newArray: ObservableArray<Any>, counter: Int) {
            assertTrue(this.change!!.next())
            assertTrue(this.change!!.wasReplaced)
            assertContentEquals(oldArray, this.change!!.removed.asList())
            assertContentEquals(newArray, this.change!!.array)
            assertFalse(this.change!!.next())
            assertEquals(counter, this.counter)
            reset()
        }

        fun check(pos: Int, newObject: Any, counter: Int) {
            assertTrue(this.change!!.next())
            assertTrue(this.change!!.wasAdded)
            assertEquals(pos, this.change!!.from)
            assertEquals(listOf(newObject), this.change!!.addedSubArray.toList())
            assertFalse(this.change!!.next())
            assertEquals(counter, this.counter)
            reset()
        }

    }

}