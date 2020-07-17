package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.beans.InvalidationListener
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("UNUSED_VALUE")
class WeakChangeListenerTest {

    @Test
    fun testHandle() {
        var listener: ChangeListenerMock<Any?>? = ChangeListenerMock(Any())
        val weakListener: WeakChangeListener<Any?> = WeakChangeListener(listener!!)
        val o = ObservableMock()
        val obj1 = Any()
        val obj2 = Any()

        // regular call
        weakListener.changed(o, obj1, obj2)
        listener.check(o, obj1, obj2, 1)
        assertFalse(weakListener.wasGarbageCollected)
        assertEquals(0, o.removeCounter)

        // GC-ed call
        o.reset()
        listener = null
        System.gc()
        assertTrue(weakListener.wasGarbageCollected)
        weakListener.changed(o, obj2, obj1)
        assertEquals(1, o.removeCounter)
    }

    private class ObservableMock : ObservableValue<Any?> {

        private var timesRemoved: Int = 0

        val removeCounter: Int
            get() = this.timesRemoved

        fun reset() {
            this.timesRemoved = 0
        }

        override fun removeListener(listener: ChangeListener<in Any?>) {
            this.timesRemoved++
        }

        override val value: Any?
            get() = null

        override fun addListener(listener: InvalidationListener) {
            // not used
        }

        override fun removeListener(listener: InvalidationListener) {
            // not used
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            // not used
            return false
        }

        override fun addListener(listener: ChangeListener<in Any?>) {
            // not used
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Any?>): Boolean {
            // not used
            return false
        }

    }

}