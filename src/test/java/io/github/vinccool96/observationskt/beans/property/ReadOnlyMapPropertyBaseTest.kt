package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableMap
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.fail

class ReadOnlyMapPropertyBaseTest {

    private lateinit var property: ReadOnlyPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<ObservableMap<Any, Any>?>

    @Before
    fun setUp() {
        this.property = ReadOnlyPropertyMock()
        this.invalidationListener = InvalidationListenerMock()
        this.changeListener = ChangeListenerMock(UNDEFINED)
    }

    @Test
    fun testInvalidationListener() {
        this.property.addListener(this.invalidationListener)
        this.property.get()
        this.invalidationListener.reset()
        this.property.set(VALUE_1)
        this.invalidationListener.check(this.property, 1)
        this.property.removeListener(this.invalidationListener)
        this.invalidationListener.reset()
        this.property.set(VALUE_2)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testChangeListener() {
        this.property.addListener(this.changeListener)
        this.property.get()
        this.changeListener.reset()
        this.property.set(VALUE_1)
        this.changeListener.check(this.property, DEFAULT, VALUE_1, 1)
        this.property.removeListener(this.changeListener)
        this.changeListener.reset()
        this.property.set(VALUE_2)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
    }

    private class ReadOnlyPropertyMock : ReadOnlyMapPropertyBase<Any, Any>() {

        private var valueState: ObservableMap<Any, Any>? = null

        override val bean: Any? = null // not used

        override val name: String? = null // not used

        fun set(value: ObservableMap<Any, Any>) {
            this.valueState = value
            this.fireValueChangedEvent()
        }

        override fun get(): ObservableMap<Any, Any>? {
            return this.valueState
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = fail("Not in use")

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = fail("Not in use")

    }

    companion object {

        private val UNDEFINED: ObservableMap<Any, Any>? = null

        private val DEFAULT: ObservableMap<Any, Any>? = null

        private val VALUE_1: ObservableMap<Any, Any> = ObservableCollections.observableMap(Collections.emptyMap())

        private val VALUE_2: ObservableMap<Any, Any> = ObservableCollections.singletonObservableMap(Any(), Any())

    }

}