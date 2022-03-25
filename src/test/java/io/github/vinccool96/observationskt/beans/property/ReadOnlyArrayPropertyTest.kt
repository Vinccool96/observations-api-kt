package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.ArrayChangeListener
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

class ReadOnlyArrayPropertyTest {

    @Test
    fun testBidirectionalContentBinding() {
        val model: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()), Any(), Any())
        val array2: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()), Any(), Any(),
                Any())
        val array1: ReadOnlyArrayProperty<Any> = SimpleArrayProperty(model, arrayOf(Any()))
        assertNotEquals(array1, array2)
        array1.bindContentBidirectional(array2)
        assertEquals(array1, array2)
        array2 += arrayOf(Any())
        assertEquals(array1, array2)
        array1 += arrayOf(Any())
        assertEquals(array1, array2)
        array1.unbindContentBidirectional(array2)
        array2 += arrayOf(Any())
        assertNotEquals(array1, array2)
        array1 += arrayOf(Any())
        assertNotEquals(array1, array2)
    }

    @Test
    fun testToString() {
        val v1: ReadOnlyArrayProperty<Any> = ReadOnlyArrayPropertyStub(null, "")
        assertEquals("ReadOnlyArrayProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyArrayProperty<Any> = ReadOnlyArrayPropertyStub(null, null)
        assertEquals("ReadOnlyArrayProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "My name"
        val v3: ReadOnlyArrayProperty<Any> = ReadOnlyArrayPropertyStub(bean, name)
        assertEquals("ReadOnlyArrayProperty [bean: $bean, name: My name, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyArrayProperty<Any> = ReadOnlyArrayPropertyStub(bean, "")
        assertEquals("ReadOnlyArrayProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyArrayProperty<Any> = ReadOnlyArrayPropertyStub(bean, null)
        assertEquals("ReadOnlyArrayProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyArrayProperty<Any> = ReadOnlyArrayPropertyStub(null, name)
        assertEquals("ReadOnlyArrayProperty [name: My name, value: $DEFAULT]", v6.toString())
    }

    private class ReadOnlyArrayPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyArrayProperty<Any>(arrayOf(Any())) {

        override fun get(): ObservableArray<Any>? {
            return null
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in ObservableArray<Any>?>) {
        }

        override fun removeListener(listener: ChangeListener<in ObservableArray<Any>?>) {
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableArray<Any>?>): Boolean {
            return false
        }

        override fun addListener(listener: ArrayChangeListener<in Any>) {
        }

        override fun removeListener(listener: ArrayChangeListener<in Any>) {
        }

        override fun isArrayChangeListenerAlreadyAdded(listener: ArrayChangeListener<in Any>): Boolean {
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