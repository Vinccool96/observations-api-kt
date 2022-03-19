package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableSet
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.fail

class ReadOnlySetPropertyBaseTest {

    private lateinit var property: ReadOnlyPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<ObservableSet<Any>?>

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

    private class ReadOnlyPropertyMock : ReadOnlySetPropertyBase<Any>() {

        private var set: ObservableSet<Any>? = null

        // not used
        override val bean: Any? = null

        // not used
        override val name: String? = null

        fun set(set: ObservableSet<Any>) {
            this.set = set
            fireValueChangedEvent()
        }

        override fun get(): ObservableSet<Any>? {
            return this.set
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = fail("Not in use")

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = fail("Not in use")

    }

    companion object {

        private val UNDEFINED: ObservableSet<Any>? = null

        private val DEFAULT: ObservableSet<Any>? = null

        private val VALUE_1: ObservableSet<Any> = ObservableCollections.observableSet()

        private val VALUE_2: ObservableSet<Any> = ObservableCollections.observableSet(Any())

    }

}