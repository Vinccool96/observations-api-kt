package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class ObservableListTest(private val listFactory: Callable<ObservableList<String?>>) {

    private lateinit var list: ObservableList<String?>

    private lateinit var mlo: MockListObserver<String?>

    @Before
    fun setUp() {
        this.list = this.listFactory.call()
        this.mlo = MockListObserver()
        this.list.addListener(this.mlo)

        useListData("one", "two", "three")
    }

    /**
     * Modifies the list in the fixture to use the strings passed in instead of the default strings, and re-creates the
     * observable list and the observer. If no strings are passed in, the result is an empty list.
     *
     * @param strings the strings to use for the list in the fixture
     */
    private fun useListData(vararg strings: String?) {
        this.list.clear()
        this.list.addAll(*strings)
        this.mlo.clear()
    }

    // ========== observer add/remove tests ==========

    @Test
    fun testObserverAddRemove() {
        val mlo2: MockListObserver<String?> = MockListObserver()
        this.list.addListener(mlo2)
        this.list.removeListener(this.mlo)
        this.list.add("xyzzy")
        this.mlo.check0()
        mlo2.check1AddRemove(this.list, EMPTY, 3, 4)
    }

    @Test
    fun testObserverAddTwice() {
        this.list.addListener(this.mlo) // add it a second time
        this.list.add("plugh")
        this.mlo.check1AddRemove(this.list, EMPTY, 3, 4)
    }

    @Test
    fun testObserverRemoveTwice() {
        this.list.removeListener(this.mlo)
        this.list.removeListener(this.mlo)
        this.list.add("plugh")
        this.mlo.check0()
    }

    // ========== list mutation tests ==========

    @Test
    fun testAddToEmpty() {
        useListData()
        this.list.add("asdf")
        this.mlo.check1AddRemove(this.list, EMPTY, 0, 1)
    }

    @Test
    fun testAddAtEnd() {
        this.list.add("four")
        this.mlo.check1AddRemove(this.list, EMPTY, 3, 4)
    }

    @Test
    fun testAddInMiddle() {
        this.list.add(1, "xyz")
        this.mlo.check1AddRemove(this.list, EMPTY, 1, 2)
    }

    @Test
    fun testAddSeveralToEmpty() {
        useListData()
        this.list.addAll("alpha", "bravo", "charlie")
        this.mlo.check1AddRemove(this.list, EMPTY, 0, 3)
    }

    @Test
    fun testAddSeveralAtEnd() {
        this.list.addAll(listOf("four", "five"))
        this.mlo.check1AddRemove(this.list, EMPTY, 3, 5)
    }

    @Test
    fun testAddSeveralInMiddle() {
        this.list.addAll(1, listOf("a", "b"))
        this.mlo.check1AddRemove(this.list, EMPTY, 1, 3)
    }

    @Test
    fun testClearNonempty() {
        this.list.clear()
        this.mlo.check1AddRemove(this.list, listOf("one", "two", "three"), 0, 0)
    }

    @Test
    fun testRemoveAt() {
        val r = this.list.removeAt(1)
        this.mlo.check1AddRemove(this.list, listOf("two"), 1, 1)
        assertEquals("two", r)
    }

    @Test
    fun testRemoveObject() {
        useListData("one", "x", "two", "three")
        val b = this.list.remove("two")
        this.mlo.check1AddRemove(this.list, listOf("two"), 2, 2)
        assertTrue(b)
    }

    @Test
    fun testRemove_Range() {
        useListData("one", "two", "three", "four", "five")
        this.list.remove(1, 3)
        this.mlo.check1AddRemove(this.list, listOf("two", "three"), 1, 1)
    }

    @Test
    fun testRemoveNull() {
        useListData("one", "two", null, "three")
        val b = this.list.remove(null)
        this.mlo.check1AddRemove(this.list, listOf(null), 2, 2)
        assertTrue(b)
    }

    @Test
    fun testRemoveAll() {
        useListData("one", "two", "three", "four", "five")
        this.list.removeAll("one", "two", "four", "six")
        assertEquals(2, this.mlo.calls.size)
        this.mlo.checkAddRemove(0, this.list, listOf("one", "two"), 0, 0)
        this.mlo.checkAddRemove(1, this.list, listOf("four"), 1, 1)
    }

    @Test
    fun testRemoveAll_1() {
        useListData("a", "c", "d", "c")
        this.list.removeAll(listOf("c"))
        assertEquals(2, this.mlo.calls.size)
        this.mlo.checkAddRemove(0, this.list, listOf("c"), 1, 1)
        this.mlo.checkAddRemove(1, this.list, listOf("c"), 2, 2)
    }

    @Test
    fun testRemoveAll_2() {
        useListData("one", "two")
        this.list.removeAll(listOf("three", "four"))
        this.mlo.check0()
    }

    @Test
    fun testRemoveAll_3() {
        useListData("a", "c", "d", "c")
        this.list.removeAll("d")
        this.mlo.check1AddRemove(this.list, listOf("d"), 2, 2)
    }

    @Test
    fun testRemoveAll_4() {
        useListData("a", "c", "d", "c")
        this.list.removeAll(listOf("d", "c"))
        this.mlo.check1AddRemove(this.list, listOf("c", "d", "c"), 1, 1)
    }

    @Test
    fun testRetainAll() {
        useListData("one", "two", "three", "four", "five")
        this.list.retainAll("two", "five", "six")
        assertEquals(2, this.mlo.calls.size)
        this.mlo.checkAddRemove(0, this.list, listOf("one"), 0, 0)
        this.mlo.checkAddRemove(1, this.list, listOf("three", "four"), 1, 1)
    }

    @Test
    fun testRetainAll_Collection() {
        useListData("one", "two", "three", "four", "five")
        this.list.retainAll(listOf("two", "five", "six"))
        assertEquals(2, this.mlo.calls.size)
        this.mlo.checkAddRemove(0, this.list, listOf("one"), 0, 0)
        this.mlo.checkAddRemove(1, this.list, listOf("three", "four"), 1, 1)
    }

    @Test
    fun testRemoveNonexistent() {
        useListData("one", "two", "x", "three")
        val b = this.list.remove("four")
        this.mlo.check0()
        assertFalse(b)
    }

    @Test
    fun testSet() {
        val r = this.list.set(1, "fnord")
        this.mlo.check1AddRemove(this.list, listOf("two"), 1, 2)
        assertEquals("two", r)
    }

    @Test
    fun testSetAll_Collection() {
        this.list.setAll(listOf("foo", "bar"))
        this.mlo.check1AddRemove(this.list, listOf("one", "two", "three"), 0, 2)
    }

    @Test
    fun testSetAll_Elements() {
        this.list.setAll("foo", "bar")
        this.mlo.check1AddRemove(this.list, listOf("one", "two", "three"), 0, 2)
    }

    @Test
    fun testIndexOf() {
        useListData("zero", "one", "two", "three", "four", "five", "two")
        assertEquals(2, this.list.indexOf("two"))
        assertNotEquals(6, this.list.indexOf("two"))
    }

    @Test
    fun testLastIndexOf() {
        useListData("zero", "one", "two", "three", "four", "five", "two")
        assertEquals(6, this.list.lastIndexOf("two"))
        assertNotEquals(2, this.list.lastIndexOf("two"))
    }

    @Test
    fun testObserverCanRemoveObservers() {
        val listObserver = ListChangeListener { change: Change<out String?> -> change.list.removeListener(this.mlo) }
        this.list.addListener(listObserver)
        this.list.add("x")
        this.mlo.clear()
        this.list.add("y")
        this.mlo.check0()
        this.list.removeListener(listObserver)

        val listener = StringListChangeListener()
        this.list.addListener(listener)
        this.list.add("z")
        assertEquals(1, listener.counter)
        this.list.add("zz")
        assertEquals(1, listener.counter)
    }

    @Test
    fun testListIterator() {
        useListData("zero", "one", "two", "three", "four", "five")
        val listIterator = this.list.listIterator()
        val seq = StringSequence()
        while (listIterator.hasNext()) {
            assertEquals(seq.nextElement, listIterator.next())
        }
    }

    @Test
    fun testListIterator_Index() {
        useListData("zero", "one", "two", "three", "four", "five")
        val listIterator = this.list.listIterator(3)
        val seq = StringSequence(3)
        while (listIterator.hasNext()) {
            assertEquals(seq.nextElement, listIterator.next())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val other = listOf("one", "two", "three")
        assertTrue(this.list == other)
        assertEquals(other.hashCode(), this.list.hashCode())
    }

    private class StringSequence(private var index: Int = 0) {

        private val elements = listOf("zero", "one", "two", "three", "four", "five")

        val nextElement: String
            get() = this.elements[this.index++]

    }

    private class StringListChangeListener : ListChangeListener<String?> {

        var counter: Int = 0

        override fun onChanged(change: Change<out String?>) {
            change.list.removeListener(this)
            this.counter++
        }

    }

    companion object {

        private val EMPTY: List<String?> = emptyList()

        @Parameters
        @JvmStatic
        fun createParameters(): List<Array<out Any?>> {
            return listOf(
                    arrayOf(TestedObservableLists.ARRAY_LIST),
                    arrayOf(TestedObservableLists.LINKED_LIST),
                    arrayOf(TestedObservableLists.CHECKED_OBSERVABLE_ARRAY_LIST),
                    arrayOf(TestedObservableLists.SYNCHRONIZED_OBSERVABLE_ARRAY_LIST),
                    arrayOf(TestedObservableLists.OBSERVABLE_LIST_PROPERTY)
            )
        }

    }

}