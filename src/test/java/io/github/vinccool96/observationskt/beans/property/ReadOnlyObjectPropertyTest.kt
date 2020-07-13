package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import org.junit.Test
import kotlin.test.assertEquals

class ReadOnlyObjectPropertyTest {

    @Test
    fun testToString() {
        val v1: ReadOnlyObjectProperty<Any?> = ReadOnlyObjectPropertyStub(null, "")
        assertEquals("ReadOnlyObjectProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyObjectProperty<Any?> = ReadOnlyObjectPropertyStub(null, null)
        assertEquals("ReadOnlyObjectProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "My name"
        val v3: ReadOnlyObjectProperty<Any?> = ReadOnlyObjectPropertyStub(bean, name)
        assertEquals("ReadOnlyObjectProperty [bean: $bean, name: My name, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyObjectProperty<Any?> = ReadOnlyObjectPropertyStub(bean, "")
        assertEquals("ReadOnlyObjectProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyObjectProperty<Any?> = ReadOnlyObjectPropertyStub(bean, null)
        assertEquals("ReadOnlyObjectProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyObjectProperty<Any?> = ReadOnlyObjectPropertyStub(null, name)
        assertEquals("ReadOnlyObjectProperty [name: My name, value: $DEFAULT]", v6.toString())
    }

    private class ReadOnlyObjectPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyObjectProperty<Any?>() {

        override fun get(): Any? {
            return null
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in Any?>) {
        }

        override fun removeListener(listener: ChangeListener<in Any?>) {
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Any?>): Boolean {
            return false
        }

    }

    companion object {

        private val DEFAULT: Any? = null

    }

}