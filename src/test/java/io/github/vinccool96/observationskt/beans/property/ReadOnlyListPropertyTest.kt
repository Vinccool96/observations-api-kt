package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

class ReadOnlyListPropertyTest {

    @Test
    fun testBidirectionalContentBinding() {
        val model: ObservableList<Any> = ObservableCollections.observableArrayList(Any(), Any())
        val list2: ObservableList<Any> = ObservableCollections.observableArrayList(Any(), Any(), Any())
        val list1: ReadOnlyListProperty<Any> = SimpleListProperty(model)
        assertNotEquals(list1, list2)
        list1.bindContentBidirectional(list2)
        assertEquals(list1, list2)
        list2.add(Any())
        assertEquals(list1, list2)
        list1.add(Any())
        assertEquals(list1, list2)
        list1.unbindContentBidirectional(list2)
        list2.add(Any())
        assertNotEquals(list1, list2)
        list1.add(Any())
        assertNotEquals(list1, list2)
    }

    @Test
    fun testContentBinding() {
        val model = ObservableCollections.observableArrayList(Any(), Any())
        val list2 = ObservableCollections.observableArrayList(Any(), Any(), Any())
        val list1: ReadOnlyListProperty<Any> = SimpleListProperty(model)
        assertNotEquals(list1, list2)
        list1.bindContent(list2)
        assertEquals(list1, list2)
        list2.add(Any())
        assertEquals(list1, list2)
        list1.add(Any())
        assertNotEquals(list1, list2)
        list1.remove(list1.size - 1)
        list1.unbindContent(list2)
        list2.add(Any())
        assertNotEquals(list1, list2)
        list1.add(Any())
        assertNotEquals(list1, list2)
    }

    @Test
    fun testToString() {
        val v1: ReadOnlyListProperty<Any> = ReadOnlyListPropertyStub(null, "")
        assertEquals("ReadOnlyListProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyListProperty<Any> = ReadOnlyListPropertyStub(null, null)
        assertEquals("ReadOnlyListProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "My name"
        val v3: ReadOnlyListProperty<Any> = ReadOnlyListPropertyStub(bean, name)
        assertEquals("ReadOnlyListProperty [bean: $bean, name: My name, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyListProperty<Any> = ReadOnlyListPropertyStub(bean, "")
        assertEquals("ReadOnlyListProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyListProperty<Any> = ReadOnlyListPropertyStub(bean, null)
        assertEquals("ReadOnlyListProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyListProperty<Any> = ReadOnlyListPropertyStub(null, name)
        assertEquals("ReadOnlyListProperty [name: My name, value: $DEFAULT]", v6.toString())
    }

    private class ReadOnlyListPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyListProperty<Any>() {

        override fun get(): ObservableList<Any>? {
            return null
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in ObservableList<Any>?>) {
        }

        override fun removeListener(listener: ChangeListener<in ObservableList<Any>?>) {
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableList<Any>?>): Boolean {
            return false
        }

        override fun addListener(listener: ListChangeListener<in Any>) {
        }

        override fun removeListener(listener: ListChangeListener<in Any>) {
        }

        override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in Any>): Boolean {
            return false
        }

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = fail("Not used")

        override val sizeProperty: ReadOnlyIntProperty
            get() = fail("Not used")

    }

    companion object {

        private val DEFAULT: Any? = null

    }

}