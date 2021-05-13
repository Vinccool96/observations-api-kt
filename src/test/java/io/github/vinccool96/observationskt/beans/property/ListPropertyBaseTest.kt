package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableObjectValueStub
import io.github.vinccool96.observationskt.collections.MockListObserver
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.Person
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ListPropertyBaseTest {

    private lateinit var property: ListPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<ObservableList<Any>?>

    private lateinit var listChangeListener: MockListObserver<Any>

    @Before
    fun setUp() {
        this.property = ListPropertyMock()
        this.invalidationListener = InvalidationListenerMock()
        this.changeListener = ChangeListenerMock(UNDEFINED)
        this.listChangeListener = MockListObserver()
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

    private fun attachListChangeListener() {
        this.property.addListener(this.listChangeListener)
        this.property.get()
        this.listChangeListener.clear()
    }

    @Test
    fun testConstructor() {
        val p1: ListProperty<Any> = ListPropertyMock()
        assertEquals(null, p1.get())
        assertEquals(null, p1.value)
        assertFalse(p1.bound)

        val p2: ListProperty<Any> = ListPropertyMock(VALUE_1b)
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
    fun testListChangeListener() {
        attachListChangeListener()
        this.property.set(VALUE_2a)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_LIST, 0, 2)
        this.property.removeListener(this.listChangeListener)
        this.listChangeListener.clear()
        this.property.set(VALUE_1a)
        this.listChangeListener.check0()
    }

    @Test
    fun testSourceList_Invalidation() {
        val source1: ObservableList<Any> = ObservableCollections.observableArrayList()
        val source2: ObservableList<Any> = ObservableCollections.observableArrayList()
        val value1 = Any()
        val value2 = Any()

        // constructor
        this.property = ListPropertyMock(source1)
        this.property.reset()
        attachInvalidationListener()

        // add element
        source1.add(value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // replace element
        source1[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // remove element
        source1.removeAt(0)
        assertTrue(this.property.isEmpty())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.invalidationListener.reset()

        // add element
        source2.add(0, value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // replace element
        source2[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)

        // remove element
        source2.removeAt(0)
        assertTrue(this.property.isEmpty())
        this.property.check(1)
        this.invalidationListener.check(this.property, 1)
    }

    @Test
    fun testSourceList_Change() {
        val source1: ObservableList<Any> = ObservableCollections.observableArrayList()
        val source2: ObservableList<Any> = ObservableCollections.observableArrayList()
        val value1 = Any()
        val value2 = Any()

        // constructor
        this.property = ListPropertyMock(source1)
        this.property.reset()
        attachChangeListener()

        // add element
        source1.add(value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // replace element
        source1[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // remove element
        source1.removeAt(0)
        assertTrue(this.property.isEmpty())
        this.property.check(1)
        this.changeListener.check(this.property, source1, source1, 1)

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.changeListener.reset()

        // add element
        source2.add(0, value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)

        // replace element
        source2[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)

        // remove element
        source2.removeAt(0)
        assertTrue(this.property.isEmpty())
        this.property.check(1)
        this.changeListener.check(this.property, source2, source2, 1)
    }

    @Test
    fun testSourceList_ListChange() {
        val source1: ObservableList<Any> = ObservableCollections.observableArrayList()
        val source2: ObservableList<Any> = ObservableCollections.observableArrayList()
        val value1 = Any()
        val value2 = Any()

        // constructor
        this.property = ListPropertyMock(source1)
        this.property.reset()
        attachListChangeListener()

        // add element
        source1.add(value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_LIST, 0, 1)
        this.listChangeListener.clear()

        // replace element
        source1[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, listOf(value1), 0, 1)
        this.listChangeListener.clear()

        // remove element
        source1.removeAt(0)
        assertTrue(this.property.isEmpty())
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, listOf(value2), 0, 0)
        this.listChangeListener.clear()

        // set
        this.property.set(source2)
        this.property.get()
        this.property.reset()
        this.listChangeListener.clear()

        // add element
        source2.add(0, value1)
        assertEquals(value1, this.property[0])
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_LIST, 0, 1)
        this.listChangeListener.clear()

        // replace element
        source2[0] = value2
        assertEquals(value2, this.property[0])
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, listOf(value1), 0, 1)
        this.listChangeListener.clear()

        // remove element
        source2.removeAt(0)
        assertTrue(this.property.isEmpty())
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, listOf(value2), 0, 0)
        this.listChangeListener.clear()
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
    fun testSet_ListChange() {
        attachListChangeListener()

        // set value once
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_LIST, 0, 2)
        this.listChangeListener.clear()

        // set same value again
        this.property.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        this.listChangeListener.check0()
        this.listChangeListener.clear()

        // set value twice without reading
        this.property.set(VALUE_1a)
        this.listChangeListener.clear()
        this.property.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.listChangeListener.check1AddRemove(this.property, VALUE_1a, 0, 1)
        this.listChangeListener.clear()
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
    fun testValueSet_ListChange() {
        attachListChangeListener()

        // set value once
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_LIST, 0, 2)
        this.listChangeListener.clear()

        // set same value again
        this.property.value = VALUE_2a
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(0)
        this.listChangeListener.check0()
        this.listChangeListener.clear()

        // set value twice without reading
        this.property.value = VALUE_1a
        this.listChangeListener.clear()
        this.property.value = VALUE_1b
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.listChangeListener.check1AddRemove(this.property, VALUE_1a, 0, 1)
        this.listChangeListener.clear()
    }

    @Test(expected = RuntimeException::class)
    fun testSetBoundValue() {
        val v: ListProperty<Any> = SimpleListProperty(VALUE_2b)
        this.property.bind(v)
        this.property.set(VALUE_1a)
    }

    @Test
    fun testBind_Invalidation() {
        attachInvalidationListener()
        val v = ObservableObjectValueStub(ObservableCollections.observableArrayList(VALUE_1a))

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
        val v = ObservableObjectValueStub(ObservableCollections.observableArrayList(VALUE_1a))

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
    fun testBind_ListChange() {
        attachListChangeListener()
        val v = ObservableObjectValueStub(ObservableCollections.observableArrayList(VALUE_1a))

        this.property.bind(v)
        assertEquals(VALUE_1a, this.property.get())
        assertTrue(this.property.bound)
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, EMPTY_LIST, 0, 0)

        // change binding once
        this.listChangeListener.clear()
        v.set(VALUE_2a)
        assertEquals(VALUE_2a, this.property.get())
        this.property.check(1)
        this.listChangeListener.check1AddRemove(this.property, VALUE_1a, 0, 2)

        // change binding twice without reading
        v.set(VALUE_1a)
        this.listChangeListener.clear()
        v.set(VALUE_1b)
        assertEquals(VALUE_1b, this.property.get())
        this.property.check(2)
        this.listChangeListener.check1AddRemove(this.property, VALUE_1a, 0, 1)

        // change binding twice to same value
        v.set(VALUE_1a)
        this.listChangeListener.clear()
        v.set(VALUE_1a)
        assertEquals(VALUE_1a, this.property.get())
        this.property.check(2)
        this.listChangeListener.check0()
    }

    @Test
    fun testRebind() {
        attachInvalidationListener()
        val v1: ListProperty<Any> = SimpleListProperty(VALUE_1a)
        val v2: ListProperty<Any> = SimpleListProperty(VALUE_2a)
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
        val v: ListProperty<Any> = SimpleListProperty(VALUE_1a)
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
    fun testAddingListenerWillAlwaysReceiveInvalidationEvent() {
        val v = SimpleListProperty(VALUE_1a)
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
    fun testUpdate() {
        val list = createPersonsList()
        val property = SimpleListProperty(list)
        val mlo = MockListObserver<Person>()
        property.addListener(mlo)
        list[3].name.set("zero") // four -> zero
        val expected = ObservableCollections.observableArrayList(Person("one"), Person("two"), Person("three"),
                Person("zero"), Person("five"))
        mlo.check1Update(expected, 3, 4)
    }

    @Test
    fun testPermutation() {
        val list = createPersonsList()
        val property = SimpleListProperty(list)
        val mlo = MockListObserver<Person>()
        property.addListener(mlo)
        ObservableCollections.sort(list)
        val expected = ObservableCollections.observableArrayList(Person("five"), Person("four"), Person("one"),
                Person("three"), Person("two"))
        mlo.check1Permutation(expected, intArrayOf(2, 4, 3, 1, 0))
    }

    @Test
    fun testPermutationUpdate() {
        val list = createPersonsList()
        val sorted = list.sorted(Person::compareTo)
        val property = SimpleListProperty(sorted)
        val mlo = MockListObserver<Person>()
        property.addListener(mlo)
        // add another listener to test Generic code path instead of SingleChange
        property.addListener(MockListObserver())
        list[3].name.set("zero") // four -> zero
        val expected = ObservableCollections.observableArrayList(Person("five"), Person("one"), Person("three"),
                Person("two"), Person("zero"))
        mlo.checkPermutation(0, expected, 0, expected.size, intArrayOf(0, 4, 1, 2, 3))
        mlo.checkUpdate(1, expected, 4, 5)
    }

    private fun createPersonsList(): ObservableList<Person> {
        val list: ObservableList<Person> = ObservableCollections.observableArrayList {param -> arrayOf(param.name)}
        list.addAll(Person("one"), Person("two"), Person("three"), Person("four"), Person("five"))
        return list
    }

    @Test
    fun testToString() {
        val value0: ObservableList<Any>? = null
        val value1 = ObservableCollections.observableArrayList(Any(), Any())
        val value2: ObservableList<Any> = ObservableCollections.observableArrayList()
        val v: ListProperty<Any> = SimpleListProperty(value2)

        property.set(value1)
        assertEquals("ListProperty [value: $value1]", property.toString())

        property.bind(v)
        assertEquals("ListProperty [bound, invalid]", property.toString())
        property.get()
        assertEquals("ListProperty [bound, value: $value2]", property.toString())
        v.set(value1)
        assertEquals("ListProperty [bound, invalid]", property.toString())
        property.get()
        assertEquals("ListProperty [bound, value: $value1]", property.toString())

        val bean = Any()
        val name = "My name"
        val v1: ListProperty<Any> = ListPropertyMock(bean, name)
        assertEquals("ListProperty [bean: $bean, name: My name, value: ${null}]", v1.toString())
        v1.set(value1)
        assertEquals("ListProperty [bean: $bean, name: My name, value: $value1]", v1.toString())
        v1.set(value0)
        assertEquals("ListProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v2: ListProperty<Any> = ListPropertyMock(bean, NO_NAME_1)
        assertEquals("ListProperty [bean: $bean, value: null]", v2.toString())
        v2.set(value1)
        assertEquals("ListProperty [bean: $bean, value: $value1]", v2.toString())
        v1.set(value0)
        assertEquals("ListProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v3: ListProperty<Any> = ListPropertyMock(bean, NO_NAME_2)
        assertEquals("ListProperty [bean: $bean, value: ${null}]", v3.toString())
        v3.set(value1)
        assertEquals("ListProperty [bean: $bean, value: $value1]", v3.toString())
        v1.set(value0)
        assertEquals("ListProperty [bean: $bean, name: My name, value: $value0]", v1.toString())

        val v4: ListProperty<Any> = ListPropertyMock(NO_BEAN, name)
        assertEquals("ListProperty [name: My name, value: ${null}]", v4.toString())
        v4.set(value1)
        v1.set(value0)
        assertEquals("ListProperty [bean: $bean, name: My name, value: $value0]", v1.toString())
        assertEquals("ListProperty [name: My name, value: $value1]", v4.toString())
    }

    private class ListPropertyMock : ListPropertyBase<Any> {

        override val bean: Any?

        override val name: String?

        private var counter: Int = 0

        constructor(bean: Any?, name: String?) : super() {
            this.bean = bean
            this.name = name
        }

        constructor(initialValue: ObservableList<Any>) : super(initialValue) {
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

        private val UNDEFINED: ObservableList<Any> = ObservableCollections.observableArrayList()

        private val VALUE_1a: ObservableList<Any> = ObservableCollections.observableArrayList()

        private val VALUE_1b: ObservableList<Any> = ObservableCollections.observableArrayList(Any())

        private val VALUE_2a: ObservableList<Any> = ObservableCollections.observableArrayList(Any(), Any())

        private val VALUE_2b: ObservableList<Any> = ObservableCollections.observableArrayList(Any(), Any(), Any())

        private val EMPTY_LIST: List<Any> = emptyList()

    }

}