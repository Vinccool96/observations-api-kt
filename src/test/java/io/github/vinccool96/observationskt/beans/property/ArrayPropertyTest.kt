package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ArrayChangeListener
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ArrayPropertyTest {

    @Test
    fun testBindBidirectional() {
        val p1: ArrayProperty<Any> = SimpleArrayProperty(VALUE_2, arrayOf(Any()))
        val p2: ArrayProperty<Any> = SimpleArrayProperty(VALUE_1, arrayOf(Any()))

        p1.bindBidirectional(p2)
        assertEquals(VALUE_1, p1.get())
        assertEquals(VALUE_1, p2.get())

        p1.set(VALUE_2)
        assertEquals(VALUE_2, p1.get())
        assertEquals(VALUE_2, p2.get())

        p2.set(VALUE_1)
        assertEquals(VALUE_1, p1.get())
        assertEquals(VALUE_1, p2.get())

        p1.unbindBidirectional(p2)
        p1.set(VALUE_2)
        assertEquals(VALUE_2, p1.get())
        assertEquals(VALUE_1, p2.get())

        p1.set(VALUE_1)
        p2.set(VALUE_2)
        assertEquals(VALUE_1, p1.get())
        assertEquals(VALUE_2, p2.get())
    }

    @Test
    fun testToString() {
        val v0: ArrayProperty<Any> = ArrayPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ArrayProperty [value: $DEFAULT]", v0.toString())

        val v1: ArrayProperty<Any> = ArrayPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ArrayProperty [value: $DEFAULT]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: ArrayProperty<Any> = ArrayPropertyStub(bean, name)
        assertEquals("ArrayProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("ArrayProperty [bean: $bean, name: My name, value: $VALUE_1]", v2.toString())

        val v3: ArrayProperty<Any> = ArrayPropertyStub(bean, NO_NAME_1)
        assertEquals("ArrayProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("ArrayProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: ArrayProperty<Any> = ArrayPropertyStub(bean, NO_NAME_2)
        assertEquals("ArrayProperty [bean: $bean, value: $DEFAULT]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("ArrayProperty [bean: $bean, value: $VALUE_1]", v4.toString())

        val v5: ArrayProperty<Any> = ArrayPropertyStub(NO_BEAN, name)
        assertEquals("ArrayProperty [name: My name, value: $DEFAULT]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("ArrayProperty [name: My name, value: $VALUE_1]", v5.toString())
    }

    private class ArrayPropertyStub(override val bean: Any?, override val name: String?) :
            ArrayProperty<Any>(arrayOf(Any())) {

        private var valueState: ObservableArray<Any>? = null

        override fun get(): ObservableArray<Any>? {
            return this.valueState
        }

        override fun set(value: ObservableArray<Any>?) {
            this.valueState = value
        }

        override fun bind(observable: ObservableValue<out ObservableArray<Any>?>) {
            fail("Not in use")
        }

        override fun unbind() {
            fail("Not in use")
        }

        override val bound: Boolean
            get() = fail("Not in use")

        override fun addListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun removeListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: ChangeListener<in ObservableArray<Any>?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in ObservableArray<Any>?>) {
            fail("Not in use")
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableArray<Any>?>): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: ArrayChangeListener<in Any>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ArrayChangeListener<in Any>) {
            fail("Not in use")
        }

        override fun isArrayChangeListenerAlreadyAdded(listener: ArrayChangeListener<in Any>): Boolean {
            fail("Not in use")
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = fail("Not in use")

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = fail("Not in use")

    }

    companion object {

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

        private val VALUE_1 = ObservableCollections.emptyObservableArray(arrayOf(Any()))

        private val VALUE_2 = ObservableCollections.observableObjectArray(arrayOf(Any()), Any())

        private val DEFAULT: Any? = null

    }

}