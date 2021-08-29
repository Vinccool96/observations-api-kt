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

class BytePropertyTest {

    @Test
    fun testValueSet_Null() {
        val p: ByteProperty = SimpleByteProperty(VALUE_1)
        p.value = null
        assertEquals(DEFAULT, p.get())
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testBindBidirectional() {
        val p1: ByteProperty = SimpleByteProperty(VALUE_2)
        val p2: ByteProperty = SimpleByteProperty(VALUE_1)

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
        val v0: ByteProperty = BytePropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ByteProperty [value: $DEFAULT]", v0.toString())

        val v1: ByteProperty = BytePropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ByteProperty [value: $DEFAULT]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: ByteProperty = BytePropertyStub(bean, name)
        assertEquals("ByteProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("ByteProperty [bean: $bean, name: My name, value: $VALUE_1]", v2.toString())

        val v3: ByteProperty = BytePropertyStub(bean, NO_NAME_1)
        assertEquals("ByteProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("ByteProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: ByteProperty = BytePropertyStub(bean, NO_NAME_2)
        assertEquals("ByteProperty [bean: $bean, value: $DEFAULT]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("ByteProperty [bean: $bean, value: $VALUE_1]", v4.toString())

        val v5: ByteProperty = BytePropertyStub(NO_BEAN, name)
        assertEquals("ByteProperty [name: My name, value: $DEFAULT]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("ByteProperty [name: My name, value: $VALUE_1]", v5.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel: ByteProperty = SimpleByteProperty()
        val exp: ObjectProperty<Byte> = valueModel.asObject()
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0, exp.get())
        valueModel.set(-43)
        assertEquals(-43, exp.get())
        valueModel.set(5)
        assertEquals(5, exp.get())

        exp.set(10)
        assertEquals(10, valueModel.get())
    }

    @Test
    fun testObjectToByte() {
        val valueModel: ObjectProperty<Byte?> = SimpleObjectProperty(null)
        val exp: ByteProperty = ByteProperty.byteProperty(valueModel)
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0, exp.get())
        valueModel.set(-43)
        assertEquals(-43, exp.get())
        valueModel.set(5)
        assertEquals(5, exp.get())

        exp.set(10)
        assertEquals(10, valueModel.get())
    }

    private class BytePropertyStub(override val bean: Any?, override val name: String?) : ByteProperty() {

        private var valueState: Byte = 0

        override fun get(): Byte {
            return this.valueState
        }

        override fun set(value: Byte) {
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

        private const val VALUE_1: Byte = 123

        private const val VALUE_2: Byte = -98

        private const val DEFAULT: Byte = 0

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