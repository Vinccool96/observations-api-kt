package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.sun.binding.ErrorLoggingUtility
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ObjectPropertyTest {

    @Test
    fun testBindBidirectional() {
        val p1: ObjectProperty<Any?> = SimpleObjectProperty(VALUE_2)
        val p2: ObjectProperty<Any?> = SimpleObjectProperty(VALUE_1)

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
        val v0: ObjectProperty<Any?> = ObjectPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ObjectProperty [value: ${DEFAULT}]", v0.toString())

        val v1: ObjectProperty<Any?> = ObjectPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ObjectProperty [value: ${DEFAULT}]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: ObjectProperty<Any?> = ObjectPropertyStub(bean, name)
        assertEquals("ObjectProperty [bean: $bean, name: My name, value: ${DEFAULT}]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("ObjectProperty [bean: $bean, name: My name, value: ${VALUE_1}]", v2.toString())

        val v3: ObjectProperty<Any?> = ObjectPropertyStub(bean, NO_NAME_1)
        assertEquals("ObjectProperty [bean: $bean, value: ${DEFAULT}]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("ObjectProperty [bean: $bean, value: ${VALUE_1}]", v3.toString())

        val v4: ObjectProperty<Any?> = ObjectPropertyStub(bean, NO_NAME_2)
        assertEquals("ObjectProperty [bean: $bean, value: ${DEFAULT}]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("ObjectProperty [bean: $bean, value: ${VALUE_1}]", v4.toString())

        val v5: ObjectProperty<Any?> = ObjectPropertyStub(NO_BEAN, name)
        assertEquals("ObjectProperty [name: My name, value: ${DEFAULT}]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("ObjectProperty [name: My name, value: ${VALUE_1}]", v5.toString())
    }

    private class ObjectPropertyStub(override val bean: Any?, override val name: String?) : ObjectProperty<Any?>() {

        private var valueState: Any? = null

        override fun get(): Any? {
            return this.valueState
        }

        override fun set(value: Any?) {
            this.valueState = value
        }

        override fun addListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun removeListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: ChangeListener<in Any?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in Any?>) {
            fail("Not in use")
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Any?>): Boolean {
            fail("Not in use")
        }

        override fun bind(observable: ObservableValue<out Any?>) {
            fail("Not in use")
        }

        override fun unbind() {
            fail("Not in use")
        }

        override val bound: Boolean
            get() = fail("Not in use")

    }

    companion object {

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

        private val VALUE_1: Any? = Any()

        private val VALUE_2: Any? = Any()

        private val DEFAULT: Any? = null

        private val log: ErrorLoggingUtility = ErrorLoggingUtility()

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            log.start()
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            log.stop()
        }

    }

}