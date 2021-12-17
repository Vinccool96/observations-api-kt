package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * A mock observer that tracks calls to its onChanged() method, combined with utility methods to make assertions on the
 * calls made.
 */
class MockListObserver<E> : ListChangeListener<E> {

    private var tooManyCalls: Boolean = false

    internal class Call<E>(change: Change<out E>) {

        internal val list: ObservableList<out E> = change.list

        internal val removed: List<E> = change.removed

        internal val from: Int = change.from

        internal val to: Int = change.to

        internal val permutation: IntArray =
                if (change.wasPermutated)
                    IntArray(change.to - change.from) { i: Int -> change.getPermutation(i + change.from) }
                else IntArray(0)

        internal val update: Boolean = change.wasUpdated

        override fun toString(): String {
            return "removed: $removed, from: $from, to: $to, permutation: ${permutation.contentToString()}"
        }

    }

    internal val calls: MutableList<Call<E>> = LinkedList()

    override fun onChanged(change: Change<out E>) {
        if (this.calls.isEmpty()) {
            while (change.next()) {
                val call: Call<E> = Call(change)
                this.calls.add(call)

                // Check generic change assertions
                assertFalse(change.wasPermutated && change.wasUpdated)
                assertFalse((change.wasAdded || change.wasRemoved) && change.wasUpdated)
                assertFalse((change.wasAdded || change.wasRemoved) && change.wasPermutated)
            }
        } else {
            this.tooManyCalls = true
        }
    }

    fun check0() {
        assertEquals(0, this.calls.size)
    }

    fun check1AddRemove(list: ObservableList<E>, removed: List<E>, from: Int, to: Int) {
        if (!this.tooManyCalls) {
            assertFalse(this.tooManyCalls)
        }
        assertEquals(1, this.calls.size)
        checkAddRemove(0, list, removed, from, to)
    }

    fun checkAddRemove(idx: Int, list: ObservableList<E>, removed: List<E>, from: Int, to: Int) {
        if (!this.tooManyCalls) {
            assertFalse(this.tooManyCalls)
        }
        val call: Call<E> = this.calls[idx]
        assertSame(list, call.list)
        assertEquals(removed, call.removed)
        assertEquals(from, call.from)
        assertEquals(to, call.to)
        assertEquals(0, call.permutation.size)
    }

    fun check1Permutation(list: ObservableList<E>, perm: IntArray) {
        assertFalse(this.tooManyCalls)
        assertEquals(1, this.calls.size)
        checkPermutation(0, list, 0, list.size, perm)
    }

    fun check1Permutation(list: ObservableList<E>, from: Int, to: Int, perm: IntArray) {
        assertFalse(this.tooManyCalls)
        assertEquals(1, this.calls.size)
        checkPermutation(0, list, from, to, perm)
    }

    fun checkPermutation(idx: Int, list: ObservableList<E>, from: Int, to: Int, perm: IntArray) {
        assertFalse(this.tooManyCalls)
        val call: Call<E> = this.calls[idx]
        assertEquals(list, call.list)
        assertEquals(mutableListOf(), call.removed)
        assertEquals(from, call.from)
        assertEquals(to, call.to)
        assertTrue(perm.contentEquals(call.permutation))
    }

    fun check1Update(list: ObservableList<E>, from: Int, to: Int) {
        assertFalse(this.tooManyCalls)
        assertEquals(1, this.calls.size)
        checkUpdate(0, list, from, to)
    }

    fun checkUpdate(idx: Int, list: ObservableList<E>, from: Int, to: Int) {
        assertFalse(this.tooManyCalls)
        val call: Call<E> = this.calls[idx]
        assertEquals(list, call.list)
        assertEquals(mutableListOf(), call.removed)
        assertTrue(IntArray(0).contentEquals(call.permutation))
        assertTrue(call.update)
        assertEquals(from, call.from)
        assertEquals(to, call.to)
    }

    fun check1() {
        assertFalse(this.tooManyCalls)
        assertEquals(1, this.calls.size)
    }

    fun clear() {
        this.calls.clear()
        this.tooManyCalls = false
    }

}