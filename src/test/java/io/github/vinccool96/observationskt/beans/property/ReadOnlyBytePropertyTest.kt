package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ReadOnlyBytePropertyTest {

    @Test
    fun testToString() {
        val v1: ReadOnlyByteProperty = ReadOnlyBytePropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ReadOnlyByteProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyByteProperty = ReadOnlyBytePropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ReadOnlyByteProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "General Kenobi"

        val v3: ReadOnlyByteProperty = ReadOnlyBytePropertyStub(bean, name)
        assertEquals("ReadOnlyByteProperty [bean: $bean, name: General Kenobi, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyByteProperty = ReadOnlyBytePropertyStub(bean, NO_NAME_1)
        assertEquals("ReadOnlyByteProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyByteProperty = ReadOnlyBytePropertyStub(bean, NO_NAME_2)
        assertEquals("ReadOnlyByteProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyByteProperty = ReadOnlyBytePropertyStub(NO_BEAN, name)
        assertEquals("ReadOnlyByteProperty [name: General Kenobi, value: $DEFAULT]", v6.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel = ReadOnlyByteWrapper()
        val exp: ReadOnlyObjectProperty<Byte> = valueModel.readOnlyProperty.asObject()
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
        val valueModel: ReadOnlyObjectWrapper<Byte?> = ReadOnlyObjectWrapper(null)
        val exp: ReadOnlyByteProperty = ReadOnlyByteProperty.readOnlyByteProperty(valueModel.readOnlyProperty)
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0, exp.get())
        valueModel.set(-43)
        assertEquals(-43, exp.get())
        valueModel.set(5)
        assertEquals(5, exp.get())
    }

    private class ReadOnlyBytePropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyByteProperty() {

        override fun get(): Byte {
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

        private const val DEFAULT: Byte = 0

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

    }

}