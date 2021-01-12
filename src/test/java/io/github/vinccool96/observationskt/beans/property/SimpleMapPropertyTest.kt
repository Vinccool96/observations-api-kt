package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableMap
import org.junit.Test
import kotlin.test.assertEquals

class SimpleMapPropertyTest {

    @Test
    fun testConstructor_NoArguments() {
        val v: MapProperty<Any, Any> = SimpleMapProperty()
        assertEquals(DEFAULT_BEAN, v.bean)
        assertEquals(DEFAULT_NAME, v.name)
        assertEquals(DEFAULT_VALUE, v.get())
    }

    @Test
    fun testConstructor_InitialValue() {
        val v1: MapProperty<Any, Any> = SimpleMapProperty(VALUE_1)
        assertEquals(DEFAULT_BEAN, v1.bean)
        assertEquals(DEFAULT_NAME, v1.name)
        assertEquals(VALUE_1, v1.get())
        val v2: MapProperty<Any, Any> = SimpleMapProperty(DEFAULT_VALUE)
        assertEquals(DEFAULT_BEAN, v2.bean)
        assertEquals(DEFAULT_NAME, v2.name)
        assertEquals(DEFAULT_VALUE, v2.get())
    }

    @Test
    fun testConstructor_Bean_Name() {
        val bean = Any()
        val name = "My name"
        val v: MapProperty<Any, Any> = SimpleMapProperty(bean, name)
        assertEquals(bean, v.bean)
        assertEquals(name, v.name)
        assertEquals(DEFAULT_VALUE, v.get())
        val v2: MapProperty<Any, Any> = SimpleMapProperty(bean, null)
        assertEquals(bean, v2.bean)
        assertEquals(null, v2.name)
        assertEquals(DEFAULT_VALUE, v2.get())
    }

    @Test
    fun testConstructor_Bean_Name_InitialValue() {
        val bean = Any()
        val name = "My name"
        val v1: MapProperty<Any, Any> = SimpleMapProperty(bean, name, VALUE_1)
        assertEquals(bean, v1.bean)
        assertEquals(name, v1.name)
        assertEquals(VALUE_1, v1.get())
        val v2: MapProperty<Any, Any> = SimpleMapProperty(bean, name, DEFAULT_VALUE)
        assertEquals(bean, v2.bean)
        assertEquals(name, v2.name)
        assertEquals(DEFAULT_VALUE, v2.get())
        val v3: MapProperty<Any, Any> = SimpleMapProperty(bean, null, VALUE_1)
        assertEquals(bean, v3.bean)
        assertEquals(null, v3.name)
        assertEquals(VALUE_1, v3.get())
        val v4: MapProperty<Any, Any> = SimpleMapProperty(bean, null, DEFAULT_VALUE)
        assertEquals(bean, v4.bean)
        assertEquals(null, v4.name)
        assertEquals(DEFAULT_VALUE, v4.get())
    }

    companion object {

        private val DEFAULT_BEAN: Any? = null

        private const val DEFAULT_NAME: String = ""

        private val DEFAULT_VALUE: ObservableMap<Any, Any>? = null

        val VALUE_1: ObservableMap<Any, Any> = ObservableCollections.observableHashMap(Any() to Any())

    }
}