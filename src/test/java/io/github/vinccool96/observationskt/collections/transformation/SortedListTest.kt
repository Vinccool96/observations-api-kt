package io.github.vinccool96.observationskt.collections.transformation

import io.github.vinccool96.observationskt.collections.MockListObserver
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SortedListTest {

    private lateinit var list: ObservableList<String>

    private lateinit var mockListObserver: MockListObserver<String>

    private lateinit var sortedList: SortedList<String>

    @Before
    fun setUp() {
        this.list = ObservableCollections.observableArrayList()
        this.list.addAll("a", "c", "d", "c")
        this.sortedList = this.list.sorted()
        this.mockListObserver = MockListObserver()
        this.sortedList.addListener(this.mockListObserver)
    }

    @Test
    fun testNoChange() {
        assertEquals(listOf("a", "c", "c", "d"), this.sortedList)
        this.mockListObserver.check0()

        compareIndices()
    }

    @Test
    fun testAdd() {
        this.list.clear()
        this.mockListObserver.clear()
        assertEquals(listOf<String>(), this.sortedList)
        this.list.addAll("a", "c", "d", "c")
        assertEquals(listOf("a", "c", "c", "d"), this.sortedList)
        this.mockListObserver.check1AddRemove(this.sortedList, listOf(), 0, 4)
        assertEquals(0, this.sortedList.getSourceIndex(0))
        assertEquals(2, this.sortedList.getSourceIndex(3))

        compareIndices()
    }

    private fun <E> getViewIndex(sorted: SortedList<E>, sourceIndex: Int): Int {
        for (i in sorted.indices) {
            if (sourceIndex == sorted.getSourceIndex(i)) {
                return i
            }
        }
        return -1
    }

    private fun <E> compareIndices(sorted: SortedList<E>) {
        val source: ObservableList<out E> = sorted.source
        for (i in sorted.indices) {
            // i as a view index
            val sourceIndex: Int = sorted.getSourceIndex(i)
            assertEquals(i, getViewIndex(sorted, sourceIndex))
            assertSame(sorted[i], source[sourceIndex])

            // i as a source index
            val viewIndex: Int = getViewIndex(sorted, i)
            assertEquals(i, sorted.getSourceIndex(viewIndex))
            assertSame(source[i], sorted[viewIndex])
        }
    }

    private fun compareIndices() {
        compareIndices(this.sortedList)
    }

}