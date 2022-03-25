package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.fail

class ReadOnlyArrayPropertyBaseTest {

    private lateinit var property: ReadOnlyPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<ObservableArray<Any>?>

    @BeforeTest
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

    private class ReadOnlyPropertyMock : ReadOnlyArrayPropertyBase<Any>(arrayOf(Any())) {

        private var array: ObservableArray<Any>? = DEFAULT

        override val bean: Any? = null // not used

        override val name: String? = null // not used

        override fun get(): ObservableArray<Any>? {
            return this.array
        }

        fun set(array: ObservableArray<Any>?) {
            this.array = array
            fireValueChangedEvent()
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = fail("Not in use")

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = fail("Not in use")

    }

    companion object {

        private val UNDEFINED: ObservableArray<Any>? = null

        private val DEFAULT: ObservableArray<Any>? = null

        private val VALUE_1: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()))

        private val VALUE_2: ObservableArray<Any> = ObservableCollections.observableObjectArray(arrayOf(Any()), Any())

    }

}