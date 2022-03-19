package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

class ReadOnlyMapPropertyTest {

    @Test
    fun testBidirectionalContentBinding() {
        val model: ObservableMap<String, String> = ObservableCollections.observableHashMap("A" to "a", "B" to "b")
        val map2: ObservableMap<String, String> = ObservableCollections.observableHashMap("A" to "a", "B" to "b",
                "C" to "c")
        val map1: ReadOnlyMapProperty<String, String> = SimpleMapProperty(model)
        assertNotEquals(map1, map2)

        map1.bindContentBidirectional(map2)
        assertEquals(map1, map2)
        map2["D"] = "d"
        assertEquals(map1, map2)
        map1["E"] = "e"
        assertEquals(map1, map2)
        map1.unbindContentBidirectional(map2)
        assertEquals(map1, map2)
        map2.remove("E")
        assertNotEquals(map1, map2)
        map2["E"] = "e"
        assertEquals(map1, map2)
        map1["F"] = "f"
        assertNotEquals(map1, map2)
    }

    @Test
    fun testContentBinding() {
        val model: ObservableMap<String, String> = ObservableCollections.observableHashMap("A" to "a", "B" to "b")
        val map2: ObservableMap<String, String> = ObservableCollections.observableHashMap("A" to "a", "B" to "b",
                "C" to "c")
        val map1: ReadOnlyMapProperty<String, String> = SimpleMapProperty(model)
        assertNotEquals(map1, map2)

        map1.bindContent(map2)
        assertEquals(map1, map2)
        map2["D"] = "d"
        assertEquals(map1, map2)
        map1["E"] = "e"
        assertNotEquals(map1, map2)
        map1.remove("E")
        map1.unbindContent(map2)
        assertEquals(map1, map2)
        map2["E"] = "e"
        assertNotEquals(map1, map2)
    }

    @Test
    fun testEquals() {
        val model1: ObservableMap<String?, String?> = ObservableCollections.observableHashMap("A" to null, "B" to "b")
        val model2: ObservableMap<String?, String?> = ObservableCollections.observableHashMap("A" to "a", "B" to "b")
        val map1: ReadOnlyMapProperty<String?, String?> = SimpleMapProperty(model1)
        val map2: ReadOnlyMapProperty<String?, String?> = SimpleMapProperty(model2)
        val map3: Map<String?, String?> = mapOf("A" to "A", "B" to "b")
        val map4 = MapClassCastException()
        val map5 = MapNullPointerException()
        assertNotEquals(map1, map3)
        assertNotEquals(map2, map3)
        assertNotEquals(map1, map4)
        assertNotEquals(map1, map5)
    }

    @Test
    fun testToString() {
        val v1: ReadOnlyMapProperty<Any, Any> = ReadOnlyMapPropertyStub(null, "")
        assertEquals("ReadOnlyMapProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyMapProperty<Any, Any> = ReadOnlyMapPropertyStub(null, null)
        assertEquals("ReadOnlyMapProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "My name"
        val v3: ReadOnlyMapProperty<Any, Any> = ReadOnlyMapPropertyStub(bean, name)
        assertEquals("ReadOnlyMapProperty [bean: $bean, name: My name, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyMapProperty<Any, Any> = ReadOnlyMapPropertyStub(bean, "")
        assertEquals("ReadOnlyMapProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyMapProperty<Any, Any> = ReadOnlyMapPropertyStub(bean, null)
        assertEquals("ReadOnlyMapProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyMapProperty<Any, Any> = ReadOnlyMapPropertyStub(null, name)
        assertEquals("ReadOnlyMapProperty [name: My name, value: $DEFAULT]", v6.toString())
    }

    private class MapClassCastException :
            SimpleMapProperty<String?, String?>(ObservableCollections.observableHashMap("A" to "a", "B" to "b")) {

        override operator fun get(key: String?): String? {
            throw ClassCastException("For the test")
        }

    }

    private class MapNullPointerException :
            SimpleMapProperty<String?, String?>(ObservableCollections.observableHashMap("A" to "a", "B" to "b")) {

        override operator fun get(key: String?): String? {
            throw NullPointerException("For the test")
        }

    }

    private class ReadOnlyMapPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyMapProperty<Any, Any>() {

        override fun get(): ObservableMap<Any, Any>? {
            return null
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in ObservableMap<Any, Any>?>) {
        }

        override fun removeListener(listener: ChangeListener<in ObservableMap<Any, Any>?>) {
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableMap<Any, Any>?>): Boolean {
            return false
        }

        override fun addListener(listener: MapChangeListener<in Any, in Any>) {
        }

        override fun removeListener(listener: MapChangeListener<in Any, in Any>) {
        }

        override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in Any, in Any>): Boolean {
            return false
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = fail("Not in use")

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = fail("Not in use")

    }

    companion object {

        private val DEFAULT: Any? = null

    }

}