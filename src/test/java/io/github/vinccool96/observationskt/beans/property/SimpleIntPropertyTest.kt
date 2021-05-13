package io.github.vinccool96.observationskt.beans.property

import org.junit.Test
import kotlin.test.assertEquals

class SimpleIntPropertyTest {

    @Test
    fun testConstructor_NoArguments() {
        val v: IntProperty = SimpleIntProperty()
        assertEquals(DEFAULT_BEAN, v.bean)
        assertEquals(DEFAULT_NAME, v.name)
        assertEquals(DEFAULT_VALUE, v.get())
    }

    @Test
    fun testConstructor_InitialValue() {
        val v1: IntProperty = SimpleIntProperty(VALUE_1)
        assertEquals(DEFAULT_BEAN, v1.bean)
        assertEquals(DEFAULT_NAME, v1.name)
        assertEquals(VALUE_1, v1.get())

        val v2: IntProperty = SimpleIntProperty(DEFAULT_VALUE)
        assertEquals(DEFAULT_BEAN, v2.bean)
        assertEquals(DEFAULT_NAME, v2.name)
        assertEquals(DEFAULT_VALUE, v2.get())
    }

    @Test
    fun testConstructor_Bean_Name() {
        val bean = Any()
        val name = "My name"
        val v: IntProperty = SimpleIntProperty(bean, name)
        assertEquals(bean, v.bean)
        assertEquals(name, v.name)
        assertEquals(DEFAULT_VALUE, v.get())

        val v2: IntProperty = SimpleIntProperty(bean, null)
        assertEquals(bean, v2.bean)
        assertEquals(null, v2.name)
        assertEquals(DEFAULT_VALUE, v2.get())
    }

    @Test
    fun testConstructor_Bean_Name_InitialValue() {
        val bean = Any()
        val name = "My name"
        val v1: IntProperty = SimpleIntProperty(bean, name, VALUE_1)
        assertEquals(bean, v1.bean)
        assertEquals(name, v1.name)
        assertEquals(VALUE_1, v1.get())

        val v2: IntProperty = SimpleIntProperty(bean, name, DEFAULT_VALUE)
        assertEquals(bean, v2.bean)
        assertEquals(name, v2.name)
        assertEquals(DEFAULT_VALUE, v2.get())

        val v3: IntProperty = SimpleIntProperty(bean, null, VALUE_1)
        assertEquals(bean, v3.bean)
        assertEquals(null, v3.name)
        assertEquals(VALUE_1, v3.get())

        val v4: IntProperty = SimpleIntProperty(bean, null, DEFAULT_VALUE)
        assertEquals(bean, v4.bean)
        assertEquals(null, v4.name)
        assertEquals(DEFAULT_VALUE, v4.get())
    }

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME = ""

        private const val DEFAULT_VALUE: Int = 0

        private const val VALUE_1 = -4354

    }

}