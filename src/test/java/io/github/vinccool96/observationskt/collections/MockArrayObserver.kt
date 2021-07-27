package io.github.vinccool96.observationskt.collections

import kotlin.test.*

/**
 * A mock observer that tracks calls to its [onChanged] method, combined with utility methods to make assertions on the
 * calls made.
 */
class MockArrayObserver<T : ObservableArray<T>> : ArrayChangeListener<T> {

    private var tooManyCalls: Boolean = false

    private var call: Call<T>? = null

    private class Call<T>(val array: T, val sizeChanged: Boolean, val from: Int, val to: Int) {

        override fun toString(): String {
            return "sizeChanged: $sizeChanged, from: $from, to: $to"
        }

    }

    override fun onChanged(observableArray: T, sizeChanged: Boolean, from: Int, to: Int) {
        if (this.call == null) {
            this.call = Call(observableArray, sizeChanged, from, to)

            // Check generic change assertions
            assertFalse(from < 0, "Negative from index")
            assertFalse(to < 0, "Negative to index")
            assertFalse(from > to, "from index is greater then to index")
            assertFalse(from == to && !sizeChanged, "No change in both elements and size")
            assertFalse(from < to && from >= observableArray.size, "from index is greater than array size")
            assertFalse(from < to && to > observableArray.size, "to index is greater than array size")
        } else {
            this.tooManyCalls = true
        }
    }

    fun check0() {
        assertNull(this.call)
    }

    fun checkOnlySizeChanged(array: T) {
        assertFalse(this.tooManyCalls, "Too many array change events")
        assertSame(array, this.call!!.array)
        assertEquals(true, this.call!!.sizeChanged)
    }

    fun checkOnlyElementsChanged(array: T, from: Int, to: Int) {
        assertFalse(this.tooManyCalls, "Too many array change events")
        assertSame(array, this.call!!.array)
        assertEquals(false, this.call!!.sizeChanged)
        assertEquals(from, this.call!!.from)
        assertEquals(to, this.call!!.to)
    }

    fun check(array: T, sizeChanged: Boolean, from: Int, to: Int) {
        assertFalse(this.tooManyCalls, "Too many array change events")
        assertSame(array, this.call!!.array)
        assertEquals(sizeChanged, this.call!!.sizeChanged)
        assertEquals(from, this.call!!.from)
        assertEquals(to, this.call!!.to)
    }

    fun check1() {
        assertFalse(this.tooManyCalls, "Too many array change events")
        assertNotNull(this.call)
    }

    fun reset() {
        this.call = null
        this.tooManyCalls = false
    }

}