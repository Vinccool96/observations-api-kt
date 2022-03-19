package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.MockSetObserver.Tuple
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change
import io.github.vinccool96.observationskt.sun.binding.SetExpressionHelper.SimpleChange
import io.github.vinccool96.observationskt.sun.collections.ObservableSetWrapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("UNUSED_VALUE")
class WeakSetChangeListenerTest {

    @Test
    fun testHandle() {
        var listener: MockSetObserver<Any>? = MockSetObserver()
        val weakListener = WeakSetChangeListener(listener!!)
        val set = ObservableSetMock()
        val removedElement = Any()
        val change: Change<Any> = SimpleChange(set).setRemoved(removedElement)

        // regular call
        weakListener.onChanged(change)
        listener.assertRemoved(Tuple.tup(removedElement))
        assertFalse(weakListener.wasGarbageCollected)

        // GC-ed call
        set.reset()
        listener = null
        System.gc()
        assertTrue(weakListener.wasGarbageCollected)
        weakListener.onChanged(change)
        assertEquals(1, set.removeCounter)
    }

    private class ObservableSetMock : ObservableSetWrapper<Any>(HashSet()) {

        var removeCounter = 0

        fun reset() {
            this.removeCounter = 0
        }

        override fun removeListener(listener: SetChangeListener<in Any>) {
            this.removeCounter++
        }

    }

}