package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ReadOnlyShortPropertyTest {

    @Test
    fun testToString() {
        val v1: ReadOnlyShortProperty = ReadOnlyShortPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ReadOnlyShortProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyShortProperty = ReadOnlyShortPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ReadOnlyShortProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "General Kenobi"

        val v3: ReadOnlyShortProperty = ReadOnlyShortPropertyStub(bean, name)
        assertEquals("ReadOnlyShortProperty [bean: $bean, name: General Kenobi, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyShortProperty = ReadOnlyShortPropertyStub(bean, NO_NAME_1)
        assertEquals("ReadOnlyShortProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyShortProperty = ReadOnlyShortPropertyStub(bean, NO_NAME_2)
        assertEquals("ReadOnlyShortProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyShortProperty = ReadOnlyShortPropertyStub(NO_BEAN, name)
        assertEquals("ReadOnlyShortProperty [name: General Kenobi, value: $DEFAULT]", v6.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel = ReadOnlyShortWrapper()
        val exp: ReadOnlyObjectProperty<Short> = valueModel.readOnlyProperty.asObject()
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0, exp.get())
        valueModel.set(-43)
        assertEquals(-43, exp.get())
        valueModel.set(5)
        assertEquals(5, exp.get())
    }

    @Test
    fun testObjectToDouble() {
        val valueModel: ReadOnlyObjectWrapper<Short?> = ReadOnlyObjectWrapper(null)
        val exp: ReadOnlyShortProperty = ReadOnlyShortProperty.readOnlyShortProperty(valueModel.readOnlyProperty)
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0, exp.get())
        valueModel.set(-43)
        assertEquals(-43, exp.get())
        valueModel.set(5)
        assertEquals(5, exp.get())
    }

    private class ReadOnlyShortPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyShortProperty() {

        override fun get(): Short {
            return 0
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun hasListener(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in Number?>) {
        }

        override fun removeListener(listener: ChangeListener<in Number?>) {
        }

        override fun hasListener(listener: ChangeListener<in Number?>): Boolean {
            return false
        }

    }

    companion object {

        private const val DEFAULT: Short = 0

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

    }

}