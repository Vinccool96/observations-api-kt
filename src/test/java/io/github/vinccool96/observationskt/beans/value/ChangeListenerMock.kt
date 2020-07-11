package io.github.vinccool96.observationskt.beans.value

import kotlin.test.assertEquals

class ChangeListenerMock<T>(private val undefined: T) : ChangeListener<T> {

    private var valueModel: ObservableValue<out T>? = null

    private var oldValue: T = this.undefined

    private var newValue: T = this.undefined

    private var counter = 0

    override fun changed(observable: ObservableValue<out T>, oldValue: T, newValue: T) {
        this.valueModel = observable
        this.oldValue = oldValue
        this.newValue = newValue
        this.counter++
    }

    fun reset() {
        this.valueModel = null
        this.oldValue = this.undefined
        this.newValue = this.undefined
        this.counter = 0
    }

    fun check(observable: ObservableValue<out T>?, oldValue: T, newValue: T, counter: Int) {
        assertEquals(observable, this.valueModel)
        if (oldValue is Double && this.oldValue is Double) {
            org.junit.Assert.assertEquals(oldValue, this.oldValue as Double, EPSILON_DOUBLE)
        } else if (oldValue is Float && this.oldValue is Float) {
            org.junit.Assert.assertEquals(oldValue, this.oldValue as Float, EPSILON_FLOAT)
        } else {
            assertEquals(oldValue, this.oldValue)
        }
        if (newValue is Double && this.newValue is Double) {
            org.junit.Assert.assertEquals(newValue, this.newValue as Double, EPSILON_DOUBLE)
        } else if (newValue is Float && this.newValue is Float) {
            org.junit.Assert.assertEquals(newValue, this.newValue as Float, EPSILON_FLOAT)
        } else {
            assertEquals(newValue, this.newValue)
        }
        assertEquals(counter, this.counter)
        reset()
    }

    companion object {

        private const val EPSILON_DOUBLE: Double = 1e-12

        private const val EPSILON_FLOAT: Float = 1e-6f

    }

}