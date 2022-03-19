package io.github.vinccool96.observationskt.beans

import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("UNUSED_VALUE")
class WeakInvalidationListenerTest {

    @Test
    fun testHandle() {
        var listener: InvalidationListenerMock? = InvalidationListenerMock()
        val weakListener = WeakInvalidationListener(listener!!)
        val o = ObservableMock()

        // regular call
        weakListener.invalidated(o)
        listener.check(o, 1)
        assertFalse(weakListener.wasGarbageCollected)

        // GC-ed call
        o.reset()
        listener = null
        System.gc()
        assertTrue(weakListener.wasGarbageCollected)
        weakListener.invalidated(o)
        assertEquals(1, o.removeCounter)
    }

    private class ObservableMock : ObservableValue<Any?> {

        var removeCounter: Int = 0

        fun reset() {
            this.removeCounter = 0
        }

        override fun removeListener(listener: InvalidationListener) {
            this.removeCounter++
        }

        override val value: Any?
            get() = null

        override fun addListener(listener: InvalidationListener) {
            // not used
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            // not used
            return false
        }

        override fun addListener(listener: ChangeListener<in Any?>) {
            // not used
        }

        override fun removeListener(listener: ChangeListener<in Any?>) {
            // not used
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Any?>): Boolean {
            // not used
            return false
        }

    }

}