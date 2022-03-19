package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.collections.SetChangeListener
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

class ReadOnlySetPropertyTest {

    @Test
    fun testBidirectionalContentBinding() {
        val model: ObservableSet<Any> = ObservableCollections.observableSet(Any(), Any())
        val set2: ObservableSet<Any> = ObservableCollections.observableSet(Any(), Any(), Any())
        val set1: ReadOnlySetProperty<Any> = SimpleSetProperty(model)
        assertNotEquals(set1, set2)
        set1.bindContentBidirectional(set2)
        assertEquals(set1, set2)
        set2.add(Any())
        assertEquals(set1, set2)
        set1.add(Any())
        assertEquals(set1, set2)
        set1.unbindContentBidirectional(set2)
        set2.add(Any())
        assertNotEquals(set1, set2)
        set1.add(Any())
        assertNotEquals(set1, set2)
    }

    @Test
    fun testContentBinding() {
        val model: ObservableSet<Any> = ObservableCollections.observableSet(Any(), Any())
        val set2: ObservableSet<Any> = ObservableCollections.observableSet(Any(), Any(), Any())
        val set1: ReadOnlySetProperty<Any> = SimpleSetProperty(model)
        assertNotEquals(set1, set2)
        set1.bindContent(set2)
        assertEquals(set1, set2)
        set2.add(Any())
        assertEquals(set1, set2)
        set1.add(Any())
        assertNotEquals(set1, set2)
        set1.remove(set1.size - 1)
        set1.unbindContent(set2)
        set2.add(Any())
        assertNotEquals(set1, set2)
        set1.add(Any())
        assertNotEquals(set1, set2)
    }

    @Test
    fun testEquals() {
        val model: ObservableSet<Any> = ObservableCollections.observableSet()
        val set1: ReadOnlySetProperty<Any> = SimpleSetProperty(model)
        val set2 = SetClassCastException()
        val set3 = SetNullPointerException()
        assertNotEquals(set1, set2)
        assertNotEquals(set1, set3)
    }

    @Test
    fun testToString() {
        val v1: ReadOnlySetProperty<Any> = ReadOnlySetPropertyStub(null, "")
        assertEquals("ReadOnlySetProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlySetProperty<Any> = ReadOnlySetPropertyStub(null, null)
        assertEquals("ReadOnlySetProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "My name"
        val v3: ReadOnlySetProperty<Any> = ReadOnlySetPropertyStub(bean, name)
        assertEquals("ReadOnlySetProperty [bean: $bean, name: My name, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlySetProperty<Any> = ReadOnlySetPropertyStub(bean, "")
        assertEquals("ReadOnlySetProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlySetProperty<Any> = ReadOnlySetPropertyStub(bean, null)
        assertEquals("ReadOnlySetProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlySetProperty<Any> = ReadOnlySetPropertyStub(null, name)
        assertEquals("ReadOnlySetProperty [name: My name, value: $DEFAULT]", v6.toString())
    }

    private class SetClassCastException : SimpleSetProperty<Any>() {

        override fun iterator(): MutableIterator<Any> {
            throw ClassCastException("For the test")
        }

    }

    private class SetNullPointerException : SimpleSetProperty<Any>() {

        override fun iterator(): MutableIterator<Any> {
            throw NullPointerException("For the test")
        }

    }

    private class ReadOnlySetPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlySetProperty<Any>() {

        override fun get(): ObservableSet<Any>? {
            return null
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in ObservableSet<Any>?>) {
        }

        override fun removeListener(listener: ChangeListener<in ObservableSet<Any>?>) {
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableSet<Any>?>): Boolean {
            return false
        }

        override fun addListener(listener: SetChangeListener<in Any>) {
        }

        override fun removeListener(listener: SetChangeListener<in Any>) {
        }

        override fun isSetChangeListenerAlreadyAdded(listener: SetChangeListener<in Any>): Boolean {
            return false
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = fail("Not in use")

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = fail("Not in use")

    }

    companion object {

        private val DEFAULT: Any? = null

    }

}