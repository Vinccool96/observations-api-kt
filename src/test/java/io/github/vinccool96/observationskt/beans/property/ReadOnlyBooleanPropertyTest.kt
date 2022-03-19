package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame

class ReadOnlyBooleanPropertyTest {

    @Test
    fun testToString() {
        val v1: ReadOnlyBooleanProperty = ReadOnlyBooleanPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ReadOnlyBooleanProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyBooleanProperty = ReadOnlyBooleanPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ReadOnlyBooleanProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "General Kenobi"

        val v3: ReadOnlyBooleanProperty = ReadOnlyBooleanPropertyStub(bean, name)
        assertEquals("ReadOnlyBooleanProperty [bean: $bean, name: General Kenobi, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyBooleanProperty = ReadOnlyBooleanPropertyStub(bean, NO_NAME_1)
        assertEquals("ReadOnlyBooleanProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyBooleanProperty = ReadOnlyBooleanPropertyStub(bean, NO_NAME_2)
        assertEquals("ReadOnlyBooleanProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyBooleanProperty = ReadOnlyBooleanPropertyStub(NO_BEAN, name)
        assertEquals("ReadOnlyBooleanProperty [name: General Kenobi, value: $DEFAULT]", v6.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel = ReadOnlyBooleanWrapper()
        val exp: ReadOnlyObjectProperty<Boolean> = valueModel.readOnlyProperty.asObject()
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(false, exp.get())
        valueModel.set(true)
        assertEquals(true, exp.get())
        valueModel.set(false)
        assertEquals(false, exp.get())
    }

    @Test
    fun testObjectToBoolean() {
        val valueModel: ReadOnlyObjectWrapper<Boolean?> = ReadOnlyObjectWrapper(null)
        val exp: ReadOnlyBooleanProperty = ReadOnlyBooleanProperty.readOnlyBooleanProperty(valueModel.readOnlyProperty)
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertFalse(exp.get())
        valueModel.set(true)
        assertEquals(true, exp.get())
        valueModel.set(false)
        assertEquals(false, exp.get())
    }

    private class ReadOnlyBooleanPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyBooleanProperty() {

        override fun get(): Boolean {
            return false
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in Boolean?>) {
        }

        override fun removeListener(listener: ChangeListener<in Boolean?>) {
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Boolean?>): Boolean {
            return false
        }

    }

    companion object {

        private const val DEFAULT: Boolean = false

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

    }

}