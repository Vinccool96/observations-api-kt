package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.sun.binding.ErrorLoggingUtility
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StringPropertyTest {

    @Test
    fun testBindBidirectional() {
        val p1: StringProperty = SimpleStringProperty(VALUE_2)
        val p2: StringProperty = SimpleStringProperty(VALUE_1)

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
        val v0: StringProperty = StringPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("StringProperty [value: $DEFAULT]", v0.toString())

        val v1: StringProperty = StringPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("StringProperty [value: $DEFAULT]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: StringProperty = StringPropertyStub(bean, name)
        assertEquals("StringProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("StringProperty [bean: $bean, name: My name, value: $VALUE_1]", v2.toString())

        val v3: StringProperty = StringPropertyStub(bean, NO_NAME_1)
        assertEquals("StringProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("StringProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: StringProperty = StringPropertyStub(bean, NO_NAME_2)
        assertEquals("StringProperty [bean: $bean, value: $DEFAULT]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("StringProperty [bean: $bean, value: $VALUE_1]", v4.toString())

        val v5: StringProperty = StringPropertyStub(NO_BEAN, name)
        assertEquals("StringProperty [name: My name, value: $DEFAULT]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("StringProperty [name: My name, value: $VALUE_1]", v5.toString())
    }

    private class StringPropertyStub(override val bean: Any?, override val name: String?) : StringProperty() {

        private var valueState: String? = null

        override fun get(): String? {
            return this.valueState
        }

        override fun set(value: String?) {
            this.valueState = value
        }

        override fun addListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun removeListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun hasListener(listener: InvalidationListener): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: ChangeListener<in String?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in String?>) {
            fail("Not in use")
        }

        override fun hasListener(listener: ChangeListener<in String?>): Boolean {
            fail("Not in use")
        }

        override fun bind(observable: ObservableValue<out String?>) {
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

        private val VALUE_1: String? = "Hello World"

        private val VALUE_2: String? = "Goodbye World"

        private val DEFAULT: String? = null

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