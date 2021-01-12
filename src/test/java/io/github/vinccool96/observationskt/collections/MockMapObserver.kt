package io.github.vinccool96.observationskt.collections

import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MockMapObserver<K, V> : MapChangeListener<K, V> {

    private val calls: MutableList<Call<K, V>> = ArrayList()

    override fun onChanged(change: MapChangeListener.Change<out K, out V>) {
        this.calls.add(Call(change.key, change.valueRemoved, change.valueAdded))
    }

    val callsNumber: Int
        get() = this.calls.size

    fun clear() {
        this.calls.clear()
    }

    fun check0() {
        assertEquals(0, this.calls.size)
    }

    fun assertAdded(tuple: Tuple<K, V>) {
        assertAdded(0, tuple)
    }

    fun assertAdded(call: Int, tuple: Tuple<K, V>) {
        assertTrue(call < this.calls.size, "Missing call to the observer # $call")
        assertEquals(this.calls[call].key, tuple.key)
        assertEquals(this.calls[call].added, tuple.value)
    }

    fun assertMultipleCalls(vararg calls: Call<K, V>) {
        assertEquals(this.calls.size, calls.size)
        for (c in calls) {
            assertTrue(this.calls.contains(c), "$calls doesn't contain $c")
        }
    }

    fun assertMultipleRemove(vararg tuples: Tuple<K, V>) {
        assertEquals(this.calls.size, calls.size)
        for (t in tuples) {
            assertTrue(this.calls.contains(Call(t.key, t.value, null)), "$calls doesn't contain $t")
        }
    }

    fun assertRemoved(tuple: Tuple<K, V>) {
        assertRemoved(0, tuple)
    }

    fun assertRemoved(call: Int, tuple: Tuple<K, V>) {
        assertTrue(call < this.calls.size)
        assertEquals(this.calls[call].key, tuple.key)
        assertEquals(this.calls[call].removed, tuple.value)
    }

    fun assertMultipleRemoved(vararg tuples: Tuple<K, V>) {
        for (t in tuples) {
            var found = false
            for (c in this.calls) {
                if (c.key == t.key) {
                    assertEquals(c.removed, t.value)
                    found = true
                    break
                }
            }
            assertTrue(found)
        }
    }

    class Call<K, V>(val key: K?, val removed: V?, val added: V?) {

        @Suppress("UNCHECKED_CAST")
        override fun equals(other: Any?): Boolean {
            if (other == null) {
                return false
            }
            if (this.javaClass != other.javaClass) {
                return false
            }
            val obj: Call<K, V> = other as Call<K, V>
            if (this.key !== obj.key && (this.key == null || this.key != obj.key)) {
                return false
            } else if (this.removed !== obj.removed && (this.removed == null || this.removed != obj.removed)) {
                return false
            } else if (this.added !== obj.added && (this.added == null || this.added != obj.added)) {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            var hash = 7
            hash = 47 * hash + (this.key?.hashCode() ?: 0)
            hash = 47 * hash + (this.removed?.hashCode() ?: 0)
            hash = 47 * hash + (this.added?.hashCode() ?: 0)
            return hash
        }

        override fun toString(): String {
            return "[ $key -> $added ($removed) ]"
        }

        companion object {

            fun <K, V> call(k: K, o: V?, n: V?): Call<K, V> {
                return Call(k, o, n)
            }
        }
    }

    class Tuple<K, V>(var key: K, var value: V) {

        companion object {

            fun <K, V> tup(k: K, v: V): Tuple<K, V> {
                return Tuple(k, v)
            }

        }

    }

}