package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import org.junit.Before
import org.junit.Test
import kotlin.math.E
import kotlin.math.PI

class ReadOnlyFloatPropertyBaseTest {

    private lateinit var property: ReadOnlyPropertyMock

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<Number?>

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

    private class ReadOnlyPropertyMock : ReadOnlyFloatPropertyBase() {

        override val bean: Any? = null

        override val name: String? = null

        private var valueState: Float = 0.0f

        override fun get(): Float {
            return this.valueState
        }

        fun set(value: Float) {
            this.valueState = value
            fireValueChangedEvent()
        }

    }

    companion object {

        private val UNDEFINED: Float? = null

        private const val DEFAULT: Float = 0.0f

        private const val VALUE_1: Float = PI.toFloat()

        private const val VALUE_2: Float = -E.toFloat()

    }

}