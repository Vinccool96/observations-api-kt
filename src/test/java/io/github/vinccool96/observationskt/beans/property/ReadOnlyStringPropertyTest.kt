package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadOnlyStringPropertyTest {

    @Test
    fun testToString() {
        val v1: ReadOnlyStringProperty = ReadOnlyStringPropertyStub(null, "")
        assertEquals("ReadOnlyStringProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyStringProperty = ReadOnlyStringPropertyStub(null, null)
        assertEquals("ReadOnlyStringProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "My name"
        val v3: ReadOnlyStringProperty = ReadOnlyStringPropertyStub(bean, name)
        assertEquals("ReadOnlyStringProperty [bean: $bean, name: My name, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyStringProperty = ReadOnlyStringPropertyStub(bean, "")
        assertEquals("ReadOnlyStringProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyStringProperty = ReadOnlyStringPropertyStub(bean, null)
        assertEquals("ReadOnlyStringProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyStringProperty = ReadOnlyStringPropertyStub(null, name)
        assertEquals("ReadOnlyStringProperty [name: My name, value: $DEFAULT]", v6.toString())
    }

    private class ReadOnlyStringPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyStringProperty() {

        override fun get(): String? {
            return null
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun hasListener(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in String?>) {
        }

        override fun removeListener(listener: ChangeListener<in String?>) {
        }

        override fun hasListener(listener: ChangeListener<in String?>): Boolean {
            return false
        }

    }

    companion object {

        private val DEFAULT: Any? = null

    }

}