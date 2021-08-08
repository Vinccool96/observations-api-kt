package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ReadOnlyIntPropertyTest {

    @Test
    fun testToString() {
        val v1: ReadOnlyIntProperty = ReadOnlyIntPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ReadOnlyIntProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyIntProperty = ReadOnlyIntPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ReadOnlyIntProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "General Kenobi"

        val v3: ReadOnlyIntProperty = ReadOnlyIntPropertyStub(bean, name)
        assertEquals("ReadOnlyIntProperty [bean: $bean, name: General Kenobi, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyIntProperty = ReadOnlyIntPropertyStub(bean, NO_NAME_1)
        assertEquals("ReadOnlyIntProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyIntProperty = ReadOnlyIntPropertyStub(bean, NO_NAME_2)
        assertEquals("ReadOnlyIntProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyIntProperty = ReadOnlyIntPropertyStub(NO_BEAN, name)
        assertEquals("ReadOnlyIntProperty [name: General Kenobi, value: $DEFAULT]", v6.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel = ReadOnlyIntWrapper()
        val exp: ReadOnlyObjectProperty<Int> = valueModel.readOnlyProperty.asObject()
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0, exp.get())
        valueModel.set(-4354)
        assertEquals(-4354, exp.get())
        valueModel.set(5)
        assertEquals(5, exp.get())
    }

    @Test
    fun testObjectToDouble() {
        val valueModel: ReadOnlyObjectWrapper<Int?> = ReadOnlyObjectWrapper(null)
        val exp: ReadOnlyIntProperty = ReadOnlyIntProperty.readOnlyIntProperty(valueModel.readOnlyProperty)
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0, exp.get())
        valueModel.set(-4354)
        assertEquals(-4354, exp.get())
        valueModel.set(5)
        assertEquals(5, exp.get())
    }

    private class ReadOnlyIntPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyIntProperty() {

        override fun get(): Int {
            return 0
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in Number?>) {
        }

        override fun removeListener(listener: ChangeListener<in Number?>) {
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Number?>): Boolean {
            return false
        }

    }

    companion object {

        private const val DEFAULT: Int = 0

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

    }

}