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

class LongPropertyTest {

    @Test
    fun testValueSet_Null() {
        val p: LongProperty = SimpleLongProperty(VALUE_1)
        p.value = null
        assertEquals(DEFAULT, p.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testBindBidirectional() {
        val p1: LongProperty = SimpleLongProperty(VALUE_2)
        val p2: LongProperty = SimpleLongProperty(VALUE_1)

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
        val v0: LongProperty = LongPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("LongProperty [value: $DEFAULT]", v0.toString())

        val v1: LongProperty = LongPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("LongProperty [value: $DEFAULT]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: LongProperty = LongPropertyStub(bean, name)
        assertEquals("LongProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("LongProperty [bean: $bean, name: My name, value: $VALUE_1]", v2.toString())

        val v3: LongProperty = LongPropertyStub(bean, NO_NAME_1)
        assertEquals("LongProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("LongProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: LongProperty = LongPropertyStub(bean, NO_NAME_2)
        assertEquals("LongProperty [bean: $bean, value: $DEFAULT]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("LongProperty [bean: $bean, value: $VALUE_1]", v4.toString())

        val v5: LongProperty = LongPropertyStub(NO_BEAN, name)
        assertEquals("LongProperty [name: My name, value: $DEFAULT]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("LongProperty [name: My name, value: $VALUE_1]", v5.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel: LongProperty = SimpleLongProperty()
        val exp: ObjectProperty<Long> = valueModel.asObject()
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0L, exp.get())
        valueModel.set(-4354L)
        assertEquals(-4354L, exp.get())
        valueModel.set(5L)
        assertEquals(5L, exp.get())

        exp.set(10L)
        assertEquals(10L, valueModel.get())
    }

    @Test
    fun testObjectToLong() {
        val valueModel: ObjectProperty<Long?> = SimpleObjectProperty(null)
        val exp: LongProperty = LongProperty.longProperty(valueModel)
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0L, exp.get())
        valueModel.set(-4354L)
        assertEquals(-4354L, exp.get())
        valueModel.set(5L)
        assertEquals(5L, exp.get())

        exp.set(10L)
        assertEquals(10L, valueModel.get())
    }

    private class LongPropertyStub(override val bean: Any?, override val name: String?) : LongProperty() {

        private var valueState: Long = 0L

        override fun get(): Long {
            return this.valueState
        }

        override fun set(value: Long) {
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

        private const val VALUE_1: Long = 1234567890L

        private const val VALUE_2: Long = -987654321L

        private const val DEFAULT: Long = 0L

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