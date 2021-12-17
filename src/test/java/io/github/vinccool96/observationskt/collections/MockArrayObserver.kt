package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * A mock observer that tracks calls to its [onChanged] method, combined with utility methods to make assertions on the
 * calls made.
 */
class MockArrayObserver<T> : ArrayChangeListener<T> {

    private var tooManyCalls: Boolean = false

    private val calls: MutableList<Call<T>> = LinkedList()

    internal class Call<T>(val change: Change<out T>) {

        internal val array = this.change.array

        internal val from = this.change.from

        internal val to = this.change.to

        internal val removed = this.change.removed

        internal val permutation: IntArray =
                if (change.wasPermutated)
                    IntArray(change.to - change.from) { i: Int -> change.getPermutation(i + change.from) }
                else IntArray(0)

        internal val update: Boolean = change.wasUpdated

        override fun toString(): String {
            return "change: $change"
        }

    }

    override fun onChanged(change: Change<out T>) {
        if (this.calls.isEmpty()) {
            while (change.next()) {
                this.calls.add(Call(change))

                // Check generic change assertions
                assertFalse(change.from < 0, "Negative from index")
                assertFalse(change.to < 0, "Negative to index")
                assertFalse(change.from > change.to, "from index is greater then to index")
                assertFalse(change.from == change.to && !change.sizeChanged, "No change in both elements and size")
                assertFalse(change.from < change.to && change.from >= change.array.size,
                        "from index is greater than array size")
                assertFalse(change.from < change.to && change.to > change.array.size,
                        "to index is greater than array size")
            }
        } else {
            this.tooManyCalls = true
        }
    }

    fun check0() {
        assertEquals(0, this.calls.size)
    }

    fun check1AddRemove(array: ObservableArray<T>, removed: Array<T>, from: Int, to: Int) {
        if (!this.tooManyCalls) {
            assertFalse(this.tooManyCalls)
        }
        assertEquals(1, this.calls.size)
        checkAddRemove(0, array, removed, from, to)
    }

    fun checkAddRemove(idx: Int, array: ObservableArray<T>, removed: Array<T>, from: Int, to: Int) {
        if (!this.tooManyCalls) {
            assertFalse(this.tooManyCalls)
        }
        val call: Call<T> = this.calls[idx]
        assertSame(array, call.array)
        assertTrue(removed.contentEquals(call.removed))
        assertEquals(from, call.from)
        assertEquals(to, call.to)
        assertEquals(0, call.permutation.size)
    }

    fun check1Permutation(array: ObservableArray<T>, perm: IntArray) {
        assertFalse(this.tooManyCalls)
        assertEquals(1, this.calls.size)
        checkPermutation(0, array, 0, array.size, perm)
    }

    fun check1Permutation(array: ObservableArray<T>, from: Int, to: Int, perm: IntArray) {
        assertFalse(this.tooManyCalls)
        assertEquals(1, this.calls.size)
        checkPermutation(0, array, from, to, perm)
    }

    fun checkPermutation(idx: Int, array: ObservableArray<T>, from: Int, to: Int, perm: IntArray) {
        assertFalse(this.tooManyCalls)
        val call: Call<T> = this.calls[idx]
        assertSame(array, call.array)
        assertEquals(mutableListOf(), call.removed.toList())
        assertEquals(from, call.from)
        assertEquals(to, call.to)
        assertTrue(perm.contentEquals(call.permutation))
    }

    fun check1Update(array: ObservableArray<T>, from: Int, to: Int) {
        assertFalse(this.tooManyCalls)
        assertEquals(1, this.calls.size)
        checkUpdate(0, array, from, to)
    }

    fun checkUpdate(idx: Int, array: ObservableArray<T>, from: Int, to: Int) {
        assertFalse(this.tooManyCalls)
        val call: Call<T> = this.calls[idx]
        assertSame(array, call.array)
        assertEquals(mutableListOf(), call.removed.toList())
        assertTrue(IntArray(0).contentEquals(call.permutation))
        assertTrue(call.update)
        assertEquals(from, call.from)
        assertEquals(to, call.to)
    }

    fun check1() {
        assertFalse(this.tooManyCalls)
        assertEquals(1, this.calls.size)
    }

    fun reset() {
        this.calls.clear()
        this.tooManyCalls = false
    }

}