package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ListPropertyTest {

    @Test
    fun testBindBidirectional() {
        val p1: ListProperty<Any> = SimpleListProperty(VALUE_2)
        val p2: ListProperty<Any> = SimpleListProperty(VALUE_1)

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
        val v0: ListProperty<Any> = ListPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ListProperty [value: $DEFAULT]", v0.toString())

        val v1: ListProperty<Any> = ListPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ListProperty [value: $DEFAULT]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: ListProperty<Any> = ListPropertyStub(bean, name)
        assertEquals("ListProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("ListProperty [bean: $bean, name: My name, value: $VALUE_1]", v2.toString())

        val v3: ListProperty<Any> = ListPropertyStub(bean, NO_NAME_1)
        assertEquals("ListProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("ListProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: ListProperty<Any> = ListPropertyStub(bean, NO_NAME_2)
        assertEquals("ListProperty [bean: $bean, value: $DEFAULT]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("ListProperty [bean: $bean, value: $VALUE_1]", v4.toString())

        val v5: ListProperty<Any> = ListPropertyStub(NO_BEAN, name)
        assertEquals("ListProperty [name: My name, value: $DEFAULT]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("ListProperty [name: My name, value: $VALUE_1]", v5.toString())
    }

    private class ListPropertyStub(override val bean: Any?, override val name: String?) :
            ListProperty<Any>() {

        private var valueState: ObservableList<Any>? = null

        override fun get(): ObservableList<Any>? {
            return this.valueState
        }

        override fun set(value: ObservableList<Any>?) {
            this.valueState = value
        }

        override fun bind(observable: ObservableValue<out ObservableList<Any>?>) {
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

        override fun hasListener(listener: InvalidationListener): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: ChangeListener<in ObservableList<Any>?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in ObservableList<Any>?>) {
            fail("Not in use")
        }

        override fun hasListener(listener: ChangeListener<in ObservableList<Any>?>): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: ListChangeListener<in Any>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ListChangeListener<in Any>) {
            fail("Not in use")
        }

        override fun hasListener(listener: ListChangeListener<in Any>): Boolean {
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

        private val VALUE_1 = ObservableCollections.emptyObservableList<Any>()

        private val VALUE_2 = ObservableCollections.singletonObservableList(Any())

        private val DEFAULT: Any? = null

    }

}
