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

class BooleanPropertyTest {

    @Test
    fun testValueSet_Null() {
        val p: BooleanProperty = SimpleBooleanProperty(VALUE_1)
        p.value = null
        assertEquals(DEFAULT, p.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testBindBidirectional() {
        val p1: BooleanProperty = SimpleBooleanProperty(VALUE_2)
        val p2: BooleanProperty = SimpleBooleanProperty(VALUE_1)

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
        val v0: BooleanProperty = BooleanPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("BooleanProperty [value: $DEFAULT]", v0.toString())

        val v1: BooleanProperty = BooleanPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("BooleanProperty [value: $DEFAULT]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: BooleanProperty = BooleanPropertyStub(bean, name)
        assertEquals("BooleanProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("BooleanProperty [bean: $bean, name: My name, value: $VALUE_1]", v2.toString())

        val v3: BooleanProperty = BooleanPropertyStub(bean, NO_NAME_1)
        assertEquals("BooleanProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("BooleanProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: BooleanProperty = BooleanPropertyStub(bean, NO_NAME_2)
        assertEquals("BooleanProperty [bean: $bean, value: $DEFAULT]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("BooleanProperty [bean: $bean, value: $VALUE_1]", v4.toString())

        val v5: BooleanProperty = BooleanPropertyStub(NO_BEAN, name)
        assertEquals("BooleanProperty [name: My name, value: $DEFAULT]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("BooleanProperty [name: My name, value: $VALUE_1]", v5.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel: BooleanProperty = SimpleBooleanProperty()
        val exp: ObjectProperty<Boolean> = valueModel.asObject()

        assertEquals(false, exp.get())
        valueModel.set(true)
        assertEquals(true, exp.get())
        valueModel.set(false)
        assertEquals(false, exp.get())

        exp.set(true)
        assertEquals(true, valueModel.get())
    }

    @Test
    fun testObjectToBoolean() {
        val valueModel: ObjectProperty<Boolean?> = SimpleObjectProperty(null)
        val exp: BooleanProperty = BooleanProperty.booleanProperty(valueModel)

        assertEquals(false, exp.get())
        valueModel.set(true)
        assertEquals(true, exp.get())
        valueModel.set(false)
        assertEquals(false, exp.get())

        exp.set(true)
        assertEquals(true, valueModel.get())
    }

    private class BooleanPropertyStub(override val bean: Any?, override val name: String?) : BooleanProperty() {

        private var valueState: Boolean = false

        override fun get(): Boolean {
            return this.valueState
        }

        override fun set(value: Boolean) {
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

        override fun addListener(listener: ChangeListener<in Boolean?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in Boolean?>) {
            fail("Not in use")
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Boolean?>): Boolean {
            fail("Not in use")
        }

        override fun bind(observable: ObservableValue<out Boolean?>) {
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

        private const val VALUE_1: Boolean = true

        private const val VALUE_2: Boolean = false

        private const val DEFAULT: Boolean = false

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