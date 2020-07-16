package io.github.vinccool96.observationskt.beans.property

import org.junit.Test
import kotlin.test.assertEquals

class SimpleObjectPropertyTest {

    @Test
    fun testConstructor_InitialValue() {
        val v1: ObjectProperty<Any> = SimpleObjectProperty(VALUE_1)
        assertEquals(DEFAULT_BEAN, v1.bean)
        assertEquals(DEFAULT_NAME, v1.name)
        assertEquals(VALUE_1, v1.get())
    }

    @Test
    fun testConstructor_Bean_Name_InitialValue() {
        val bean = Any()
        val name = "My name"
        val v1: ObjectProperty<Any> = SimpleObjectProperty(bean, name, VALUE_1)
        assertEquals(bean, v1.bean)
        assertEquals(name, v1.name)
        assertEquals(VALUE_1, v1.get())

        val v2: ObjectProperty<Any> = SimpleObjectProperty(bean, null, VALUE_1)
        assertEquals(bean, v2.bean)
        assertEquals(null, v2.name)
        assertEquals(VALUE_1, v2.get())
    }

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private val DEFAULT_NAME: String? = ""

        private val VALUE_1: Any = Any()

    }

}