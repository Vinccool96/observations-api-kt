package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleArrayPropertyTest {

    @Test
    fun testConstructor_NoArguments() {
        val v: ArrayProperty<Any> = SimpleArrayProperty(arrayOf(Any()))
        assertEquals(DEFAULT_BEAN, v.bean)
        assertEquals(DEFAULT_NAME, v.name)
        assertEquals(DEFAULT_VALUE, v.get())
    }

    @Test
    fun testConstructor_InitialValue() {
        val v1: ArrayProperty<Any> = SimpleArrayProperty(VALUE_1, arrayOf(Any()))
        assertEquals(DEFAULT_BEAN, v1.bean)
        assertEquals(DEFAULT_NAME, v1.name)
        assertEquals(VALUE_1, v1.get())

        val v2: ArrayProperty<Any> = SimpleArrayProperty(DEFAULT_VALUE, arrayOf(Any()))
        assertEquals(DEFAULT_BEAN, v2.bean)
        assertEquals(DEFAULT_NAME, v2.name)
        assertEquals(DEFAULT_VALUE, v2.get())
    }

    @Test
    fun testConstructor_Bean_Name() {
        val bean = Any()
        val name = "My name"
        val v: ArrayProperty<Any> = SimpleArrayProperty(bean, name, arrayOf(Any()))
        assertEquals(bean, v.bean)
        assertEquals(name, v.name)
        assertEquals(DEFAULT_VALUE, v.get())

        val v2: ArrayProperty<Any> = SimpleArrayProperty(bean, null, arrayOf(Any()))
        assertEquals(bean, v2.bean)
        assertEquals(null, v2.name)
        assertEquals(DEFAULT_VALUE, v2.get())
    }

    @Test
    fun testConstructor_Bean_Name_InitialValue() {
        val bean = Any()
        val name = "My name"
        val v1: ArrayProperty<Any> = SimpleArrayProperty(bean, name, VALUE_1, arrayOf(Any()))
        assertEquals(bean, v1.bean)
        assertEquals(name, v1.name)
        assertEquals(VALUE_1, v1.get())

        val v2: ArrayProperty<Any> = SimpleArrayProperty(bean, name, DEFAULT_VALUE, arrayOf(Any()))
        assertEquals(bean, v2.bean)
        assertEquals(name, v2.name)
        assertEquals(DEFAULT_VALUE, v2.get())

        val v3: ArrayProperty<Any> = SimpleArrayProperty(bean, null, VALUE_1, arrayOf(Any()))
        assertEquals(bean, v3.bean)
        assertEquals(null, v3.name)
        assertEquals(VALUE_1, v3.get())

        val v4: ArrayProperty<Any> = SimpleArrayProperty(bean, null, DEFAULT_VALUE, arrayOf(Any()))
        assertEquals(bean, v4.bean)
        assertEquals(null, v4.name)
        assertEquals(DEFAULT_VALUE, v4.get())
    }

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

        private val DEFAULT_VALUE: ObservableArray<Any>? = null

        val VALUE_1: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()), Any())

    }

}