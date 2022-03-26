package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.collections.SetChangeListener
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class SetPropertyTest {

    @Test
    fun testBindBidirectional() {
        val p1: SetProperty<Any> = SimpleSetProperty(VALUE_2)
        val p2: SetProperty<Any> = SimpleSetProperty(VALUE_1)

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
        val v0: SetProperty<Any> = SetPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("SetProperty [value: $DEFAULT]", v0.toString())

        val v1: SetProperty<Any> = SetPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("SetProperty [value: $DEFAULT]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: SetProperty<Any> = SetPropertyStub(bean, name)
        assertEquals("SetProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("SetProperty [bean: $bean, name: My name, value: $VALUE_1]", v2.toString())

        val v3: SetProperty<Any> = SetPropertyStub(bean, NO_NAME_1)
        assertEquals("SetProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("SetProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: SetProperty<Any> = SetPropertyStub(bean, NO_NAME_2)
        assertEquals("SetProperty [bean: $bean, value: $DEFAULT]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("SetProperty [bean: $bean, value: $VALUE_1]", v4.toString())

        val v5: SetProperty<Any> = SetPropertyStub(NO_BEAN, name)
        assertEquals("SetProperty [name: My name, value: $DEFAULT]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("SetProperty [name: My name, value: $VALUE_1]", v5.toString())
    }

    private class SetPropertyStub(override val bean: Any?, override val name: String?) :
            SetProperty<Any>() {

        private var valueState: ObservableSet<Any>? = null

        override fun get(): ObservableSet<Any>? {
            return this.valueState
        }

        override fun set(value: ObservableSet<Any>?) {
            this.valueState = value
        }

        override fun bind(observable: ObservableValue<out ObservableSet<Any>?>) {
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

        override fun addListener(listener: ChangeListener<in ObservableSet<Any>?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in ObservableSet<Any>?>) {
            fail("Not in use")
        }

        override fun hasListener(listener: ChangeListener<in ObservableSet<Any>?>): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: SetChangeListener<in Any>) {
            fail("Not in use")
        }

        override fun removeListener(listener: SetChangeListener<in Any>) {
            fail("Not in use")
        }

        override fun hasListener(listener: SetChangeListener<in Any>): Boolean {
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

        private val VALUE_1 = ObservableCollections.emptyObservableSet<Any>()

        private val VALUE_2 = ObservableCollections.observableSet(Any())

        private val DEFAULT: Any? = null

    }

}