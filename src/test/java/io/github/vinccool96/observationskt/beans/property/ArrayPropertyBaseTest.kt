package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableArrayValueStub
import io.github.vinccool96.observationskt.beans.value.ObservableObjectValueStub
import io.github.vinccool96.observationskt.collections.MockArrayObserver
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import kotlin.test.*

class ArrayPropertyBaseTest {

    private lateinit var property: ArrayPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<ObservableArray<Any>?>

    private lateinit var listChangeListener: MockArrayObserver<Any>

    @BeforeTest
    fun setUp() {
        this.property = ArrayPropertyMock()
        this.invalidationListener = InvalidationListenerMock()
        this.changeListener = ChangeListenerMock(UNDEFINED)
        this.listChangeListener = MockArrayObserver()
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

    private fun attachArrayChangeListener() {
        this.property.addListener(this.listChangeListener)
        this.property.get()
        this.listChangeListener.reset()
    }

    @Test
    fun testConstructor() {
        val p1: ArrayProperty<Any> = ArrayPropertyMock()
        assertEquals(null, p1.get())
        assertEquals(null, p1.value)
        assertFalse(p1.bound)

        val p2: ArrayProperty<Any> = ArrayPropertyMock(VALUE_1b)
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
    fun testArrayChangeListener() {
        attachArrayChangeListener()
        this.property.set(VALUE_2a)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_ARRAY, 0, 2)
        this.property.removeListener(this.listChangeListener)
        this.listChangeListener.reset()
        this.property.set(VALUE_1a)
        this.listChangeListener.check0()
    }

    @Test
    fun testSourceArray_Invalidation() {
        val source1: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))
        val source2: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))
        val value1 = Any()
        val value2 = Any()

        // constructor
        this.property = ArrayPropertyMock(source1)
        this.property.reset()
        attachInvalidationListener()

        // add element
        source1 += arrayOf(value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // replace element
        source1[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.invalidationListener.reset()

        // add element
        source2.setAll(value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // replace element
        source2[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testSourceArray_Change() {
        val source1: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))
        val source2: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))
        val value1 = Any()
        val value2 = Any()

        // constructor
        this.property = ArrayPropertyMock(source1)
        this.property.reset()
        attachChangeListener()

        // add element
        source1 += arrayOf(value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // replace element
        source1[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.changeListener.reset()

        // add element
        source2.setAll(value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)

        // replace element
        source2[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)
    }

    @Test
    fun testSourceArray_ArrayChange() {
        val source1: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))
        val source2: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))
        val value1 = Any()
        val value2 = Any()

        // constructor
        this.property = ArrayPropertyMock(source1)
        this.property.reset()
        attachArrayChangeListener()

        // add element
        source1 += arrayOf(value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_ARRAY, 0, 1)
        this.listChangeListener.reset()

        // replace element
        source1[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, arrayOf(value1), 0, 1)
        this.listChangeListener.reset()

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.listChangeListener.reset()

        // add element
        source2.setAll(value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_ARRAY, 0, 1)
        this.listChangeListener.reset()

        // replace element
        source2[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, arrayOf(value1), 0, 1)
        this.listChangeListener.reset()
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
    fun testSet_ArrayChange() {
        attachArrayChangeListener()

        // set value once
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_ARRAY, 0, 2)
        this.listChangeListener.reset()

        // set same value again
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        this.listChangeListener.check0()
        this.listChangeListener.reset()

        // set value twice without reading
        this.property.set(VALUE_1a)
        this.listChangeListener.reset()
        this.property.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.listChangeListener.check1AddRemove(this.property, VALUE_1a.toTypedArray(), 0, 1)
        this.listChangeListener.reset()
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
    fun testValueSet_ArrayChange() {
        attachArrayChangeListener()

        // set value once
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_ARRAY, 0, 2)
        this.listChangeListener.reset()

        // set same value again
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        this.listChangeListener.check0()
        this.listChangeListener.reset()

        // set value twice without reading
        this.property.value = VALUE_1a
        this.listChangeListener.reset()
        this.property.value = VALUE_1b
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.listChangeListener.check1AddRemove(this.property, VALUE_1a.toTypedArray(), 0, 1)
        this.listChangeListener.reset()
    }

    @Test
    fun testSetBoundValue() {
        assertFailsWith<RuntimeException> {
            val v: ArrayProperty<Any> = SimpleArrayProperty(VALUE_2b, arrayOf(Any()))
            this.property.bind(v)
            this.property.set(VALUE_1a)
        }
    }

    @Test
    fun testBind_Invalidation() {
        attachInvalidationListener()
        val v: ObservableObjectValueStub<ObservableArray<Any>> =
                ObservableObjectValueStub(ObservableCollections.observableObjectArray(VALUE_1a))

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
        val v: ObservableObjectValueStub<ObservableArray<Any>> =
                ObservableObjectValueStub(ObservableCollections.observableObjectArray(VALUE_1a))

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
    fun testBind_ArrayChange() {
        attachArrayChangeListener()
        val v: ObservableObjectValueStub<ObservableArray<Any>> =
                ObservableObjectValueStub(ObservableCollections.observableObjectArray(VALUE_1a))

        this.property.bind(v)
        assertEquals(VALUE_1a, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_ARRAY, 0, 0)

        // change binding once
        this.listChangeListener.reset()
        v.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, VALUE_1a.toTypedArray(), 0, 2)

        // change binding twice without reading
        v.set(VALUE_1a)
        this.listChangeListener.reset()
        v.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.listChangeListener.check1AddRemove(this.property, VALUE_1a.toTypedArray(), 0, 1)

        // change binding twice to same value
        v.set(VALUE_1a)
        this.listChangeListener.reset()
        v.set(VALUE_1a)
        assertEquals(VALUE_1a, this.property.get())
        this.property.check(2)
        this.listChangeListener.check0()
    }

    @Test
    fun testRebind() {
        attachInvalidationListener()
        val v1: ArrayProperty<Any> = SimpleArrayProperty(VALUE_1a, arrayOf(Any()))
        val v2: ArrayProperty<Any> = SimpleArrayProperty(VALUE_2a, arrayOf(Any()))
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
        this.property.check(0)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testUnbind() {
        attachInvalidationListener()
        val v: ArrayProperty<Any> = SimpleArrayProperty(VALUE_1a, arrayOf(Any()))
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
        var property: ArrayPropertyMock? = ArrayPropertyMock()
        val v = ObservableArrayValueStub(VALUE_1a, arrayOf(Any()))
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
        attachInvalidationListener()
        attachChangeListener()
        val v = SimpleArrayProperty(VALUE_1a, arrayOf(Any()))
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
        val value0: ObservableArray<Any>? = null
        val value1 = ObservableCollections.observableObjectArray(arrayOf(Any()), Any(), Any())
        val value2: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))
        val v: ArrayProperty<Any> = SimpleArrayProperty(value2, arrayOf(Any()))

        property.set(value1)
        assertEquals("ArrayProperty [value: $value1]", property.toString())

        property.bind(v)
        assertEquals("ArrayProperty [bound, invalid]", property.toString())
        property.get()
        assertEquals("ArrayProperty [bound, value: $value2]", property.toString())
        v.set(value1)
        assertEquals("ArrayProperty [bound, invalid]", property.toString())
        property.get()
        assertEquals("ArrayProperty [bound, value: $value1]", property.toString())

        val bean = Any()
        val name = "My name"
        val v1: ArrayProperty<Any> = ArrayPropertyMock(bean, name)
        assertEquals("ArrayProperty [bean: $bean, name: My name, value: ${null}]", v1.toString())
        v1.set(value1)
        assertEquals("ArrayProperty [bean: $bean, name: My name, value: $value1]", v1.toString())
        v1.set(value0)
        assertEquals("ArrayProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v2: ArrayProperty<Any> = ArrayPropertyMock(bean, NO_NAME_1)
        assertEquals("ArrayProperty [bean: $bean, value: null]", v2.toString())
        v2.set(value1)
        assertEquals("ArrayProperty [bean: $bean, value: $value1]", v2.toString())
        v1.set(value0)
        assertEquals("ArrayProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v3: ArrayProperty<Any> = ArrayPropertyMock(bean, NO_NAME_2)
        assertEquals("ArrayProperty [bean: $bean, value: ${null}]", v3.toString())
        v3.set(value1)
        assertEquals("ArrayProperty [bean: $bean, value: $value1]", v3.toString())
        v1.set(value0)
        assertEquals("ArrayProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v4: ArrayProperty<Any> = ArrayPropertyMock(NO_BEAN, name)
        assertEquals("ArrayProperty [name: My name, value: ${null}]", v4.toString())
        v4.set(value1)
        v1.set(value0)
        assertEquals("ArrayProperty [bean: $bean, name: My name, value: $value0]", v1.toString())
        assertEquals("ArrayProperty [name: My name, value: $value1]", v4.toString())
    }

    private class ArrayPropertyMock : ArrayPropertyBase<Any> {

        override val bean: Any?

        override val name: String?

        private var counter: Int = 0

        constructor(bean: Any?, name: String?) : super(arrayOf(Any())) {
            this.bean = bean
            this.name = name
        }

        constructor(initialValue: ObservableArray<Any>) : super(initialValue, arrayOf(Any())) {
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

        private val UNDEFINED: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))

        private val VALUE_1a: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))

        private val VALUE_1b: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()), Any())

        private val VALUE_2a: ObservableArray<Any> =
                ObservableCollections.observableObjectArray(arrayOf(Any()), Any(), Any())

        private val VALUE_2b: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()), Any(),
                Any(), Any())

        private val EMPTY_ARRAY: Array<Any> = arrayOf()

    }

}