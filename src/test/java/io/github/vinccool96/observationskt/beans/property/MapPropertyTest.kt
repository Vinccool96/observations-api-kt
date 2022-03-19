package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableMap
import kotlin.test.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.fail

class MapPropertyTest {

    @Test
    fun testBindBidirectional() {
        val p1: MapProperty<Any, Any> = SimpleMapProperty(VALUE_2)
        val p2: MapProperty<Any, Any> = SimpleMapProperty(VALUE_1)

        p1.bindBidirectional(p2)
        assertEquals(VALUE_1, p1.get())
        assertEquals(VALUE_1, p2.get())

        p1.set(VALUE_2)
        assertEquals(VALUE_2, p1.get())
        assertEquals(VALUE_2, p2.get())

        p2.set(VALUE_1)
        assertEquals(VALUE_1, p1.get())
        assertEquals(VALUE_1, p2.get())

        p1.unbindBidirectional(p2)
        p1.set(VALUE_2)
        assertEquals(VALUE_2, p1.get())
        assertEquals(VALUE_1, p2.get())

        p1.set(VALUE_1)
        p2.set(VALUE_2)
        assertEquals(VALUE_1, p1.get())
        assertEquals(VALUE_2, p2.get())
    }

    @Test
    fun testToString() {
        val v0: MapProperty<Any, Any> = MapPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("MapProperty [value: ${DEFAULT}]", v0.toString())

        val v1: MapProperty<Any, Any> = MapPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("MapProperty [value: ${DEFAULT}]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: MapProperty<Any, Any> = MapPropertyStub(bean, name)
        assertEquals("MapProperty [bean: $bean, name: My name, value: ${DEFAULT}]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("MapProperty [bean: $bean, name: My name, value: ${VALUE_1}]", v2.toString())

        val v3: MapProperty<Any, Any> = MapPropertyStub(bean, NO_NAME_1)
        assertEquals("MapProperty [bean: $bean, value: ${DEFAULT}]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("MapProperty [bean: $bean, value: ${VALUE_1}]", v3.toString())

        val v4: MapProperty<Any, Any> = MapPropertyStub(bean, NO_NAME_2)
        assertEquals("MapProperty [bean: $bean, value: ${DEFAULT}]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("MapProperty [bean: $bean, value: ${VALUE_1}]", v4.toString())

        val v5: MapProperty<Any, Any> = MapPropertyStub(NO_BEAN, name)
        assertEquals("MapProperty [name: My name, value: ${DEFAULT}]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("MapProperty [name: My name, value: ${VALUE_1}]", v5.toString())
    }

    private class MapPropertyStub(override val bean: Any?, override val name: String?) : MapProperty<Any, Any>() {

        private var valueState: ObservableMap<Any, Any>? = null

        override fun get(): ObservableMap<Any, Any>? {
            return this.valueState
        }

        override fun set(value: ObservableMap<Any, Any>?) {
            this.valueState = value
        }

        override fun bind(observable: ObservableValue<out ObservableMap<Any, Any>?>) {
            fail("Not in use")
        }

        override fun unbind() {
            fail("Not in use")
        }

        override val bound: Boolean
            get() = fail("Not in use")

        override fun addListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun removeListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: ChangeListener<in ObservableMap<Any, Any>?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in ObservableMap<Any, Any>?>) {
            fail("Not in use")
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableMap<Any, Any>?>): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: MapChangeListener<in Any, in Any>) {
            fail("Not in use")
        }

        override fun removeListener(listener: MapChangeListener<in Any, in Any>) {
            fail("Not in use")
        }

        override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in Any, in Any>): Boolean {
            fail("Not in use")
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = fail("Not in use")

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = fail("Not in use")

    }

    companion object {

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

        private val VALUE_1: ObservableMap<Any, Any> = ObservableCollections.observableMap(Collections.emptyMap())

        private val VALUE_2: ObservableMap<Any, Any> =
                ObservableCollections.observableMap(Collections.singletonMap(Any(), Any()))

        private val DEFAULT: ObservableMap<Any, Any>? = null

    }

}