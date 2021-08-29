package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.sun.binding.ErrorLoggingUtility
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.fail

class ShortPropertyTest {

    @Test
    fun testValueSet_Null() {
        val p: ShortProperty = SimpleShortProperty(VALUE_1)
        p.value = null
        assertEquals(DEFAULT, p.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testBindBidirectional() {
        val p1: ShortProperty = SimpleShortProperty(VALUE_2)
        val p2: ShortProperty = SimpleShortProperty(VALUE_1)

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
        val v0: ShortProperty = ShortPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ShortProperty [value: $DEFAULT]", v0.toString())

        val v1: ShortProperty = ShortPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ShortProperty [value: $DEFAULT]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: ShortProperty = ShortPropertyStub(bean, name)
        assertEquals("ShortProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("ShortProperty [bean: $bean, name: My name, value: $VALUE_1]", v2.toString())

        val v3: ShortProperty = ShortPropertyStub(bean, NO_NAME_1)
        assertEquals("ShortProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("ShortProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: ShortProperty = ShortPropertyStub(bean, NO_NAME_2)
        assertEquals("ShortProperty [bean: $bean, value: $DEFAULT]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("ShortProperty [bean: $bean, value: $VALUE_1]", v4.toString())

        val v5: ShortProperty = ShortPropertyStub(NO_BEAN, name)
        assertEquals("ShortProperty [name: My name, value: $DEFAULT]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("ShortProperty [name: My name, value: $VALUE_1]", v5.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel: ShortProperty = SimpleShortProperty()
        val exp: ObjectProperty<Short> = valueModel.asObject()
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0, exp.get())
        valueModel.set(-4354)
        assertEquals(-4354, exp.get())
        valueModel.set(5)
        assertEquals(5, exp.get())

        exp.set(10)
        assertEquals(10, valueModel.get())
    }

    @Test
    fun testObjectToShort() {
        val valueModel: ObjectProperty<Short?> = SimpleObjectProperty(null)
        val exp: ShortProperty = ShortProperty.shortProperty(valueModel)
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0, exp.get())
        valueModel.set(-4354)
        assertEquals(-4354, exp.get())
        valueModel.set(5)
        assertEquals(5, exp.get())

        exp.set(10)
        assertEquals(10, valueModel.get())
    }

    private class ShortPropertyStub(override val bean: Any?, override val name: String?) : ShortProperty() {

        private var valueState: Short = 0

        override fun get(): Short {
            return this.valueState
        }

        override fun set(value: Short) {
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

        override fun addListener(listener: ChangeListener<in Number?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in Number?>) {
            fail("Not in use")
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Number?>): Boolean {
            fail("Not in use")
        }

        override fun bind(observable: ObservableValue<out Number?>) {
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

        private const val VALUE_1: Short = 12345

        private const val VALUE_2: Short = -9876

        private const val DEFAULT: Short = 0

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