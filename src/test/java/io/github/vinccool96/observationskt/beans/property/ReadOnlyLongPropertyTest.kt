package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import org.junit.Test
import kotlin.test.assertEquals

class ReadOnlyLongPropertyTest {

    @Test
    fun testToString() {
        val v1: ReadOnlyLongProperty = ReadOnlyLongPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ReadOnlyLongProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyLongProperty = ReadOnlyLongPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ReadOnlyLongProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "General Kenobi"

        val v3: ReadOnlyLongProperty = ReadOnlyLongPropertyStub(bean, name)
        assertEquals("ReadOnlyLongProperty [bean: $bean, name: General Kenobi, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyLongProperty = ReadOnlyLongPropertyStub(bean, NO_NAME_1)
        assertEquals("ReadOnlyLongProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyLongProperty = ReadOnlyLongPropertyStub(bean, NO_NAME_2)
        assertEquals("ReadOnlyLongProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyLongProperty = ReadOnlyLongPropertyStub(NO_BEAN, name)
        assertEquals("ReadOnlyLongProperty [name: General Kenobi, value: $DEFAULT]", v6.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel = ReadOnlyLongWrapper()
        val exp: ReadOnlyObjectProperty<Long> = valueModel.readOnlyProperty.asObject()

        assertEquals(0L, exp.get())
        valueModel.set(-4354L)
        assertEquals(-4354L, exp.get())
        valueModel.set(5L)
        assertEquals(5L, exp.get())
    }

    @Test
    fun testObjectToDouble() {
        val valueModel: ReadOnlyObjectWrapper<Long?> = ReadOnlyObjectWrapper(null)
        val exp: ReadOnlyLongProperty = ReadOnlyLongProperty.readOnlyLongProperty(valueModel.readOnlyProperty)

        assertEquals(0L, exp.get())
        valueModel.set(-4354L)
        assertEquals(-4354L, exp.get())
        valueModel.set(5L)
        assertEquals(5L, exp.get())
    }

    private class ReadOnlyLongPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyLongProperty() {

        override fun get(): Long {
            return 0L
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

        private const val DEFAULT: Long = 0L

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

    }

}