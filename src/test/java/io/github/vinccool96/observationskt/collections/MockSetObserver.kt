package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.SetChangeListener.Change
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MockSetObserver<E> : SetChangeListener<E> {

    private val calls: MutableList<Call<E>> = ArrayList()

    override fun onChanged(change: Change<out E>) {
        this.calls.add(Call(change.elementRemoved, change.elementAdded))
    }

    val callsNumber: Int
        get() = this.calls.size

    fun clear() {
        this.calls.clear()
    }

    fun check0() {
        assertEquals(0, this.calls.size)
    }

    fun assertAdded(tuple: Tuple<E>) {
        assertAdded(0, tuple)
    }

    fun assertAdded(call: Int, tuple: Tuple<E>) {
        assertTrue(call < this.calls.size, "Missing call to the observer # $call")
        assertEquals(this.calls[call].added, tuple.value)
    }

    fun assertMultipleCalls(vararg calls: Call<E>) {
        assertEquals(this.calls.size, calls.size)
        for (call in calls) {
            assertTrue(this.calls.contains(call), "${this.calls} doesn't contain $call")
        }
    }

    fun assertMultipleRemove(vararg tuples: Tuple<E>) {
        assertEquals(this.calls.size, tuples.size)
        for (tuple in tuples) {
            assertTrue(this.calls.contains(Call(tuple.value, null)), "$calls doesn't contain $tuple")
        }
    }

    fun assertRemoved(tuple: Tuple<E>) {
        assertRemoved(0, tuple)
    }

    fun assertRemoved(call: Int, tuple: Tuple<E>) {
        assertTrue(call < this.calls.size, "Missing call to the observer # $call")
        assertEquals(this.calls[call].removed, tuple.value)
    }

    fun assertMultipleRemoved(vararg tuples: Tuple<E>) {
        for (tuple in tuples) {
            var found = false
            for (call in this.calls) {
                if (call.removed == tuple.value) {
                    found = true
                    break
                }
            }
            assertTrue(found)
        }
    }

    class Call<E>(val removed: E?, val added: E?) {

        override fun equals(other: Any?): Boolean {
            if (other == null) {
                return false
            }
            if (other !is Call<*> || this.javaClass !== other.javaClass) {
                return false
            }
            if (this.removed != other.removed) {
                return false
            }
            if (this.added != other.added) {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            var hash = 7
            hash = 47 * hash + (this.removed?.hashCode() ?: 0)
            hash = 47 * hash + (this.added?.hashCode() ?: 0)
            return hash
        }

        override fun toString(): String {
            return "[ $added ($removed) ]"
        }

        companion object {

            fun <E> call(removed: E?, added: E?): Call<E> {
                return Call(removed, added)
            }

        }

    }

    class Tuple<E> private constructor(val value: E) {

        companion object {

            fun <E> tup(v: E): Tuple<E> {
                return Tuple(v)
            }

        }

    }

}