package io.github.vinccool96.observationskt.collections.transformation

import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.property.SimpleObjectProperty
import io.github.vinccool96.observationskt.collections.*
import io.github.vinccool96.observationskt.sun.collections.NonIterableListChange.SimplePermutationChange
import io.github.vinccool96.observationskt.sun.collections.ObservableListWrapper
import io.github.vinccool96.observationskt.util.Callback
import kotlin.test.*

class SortedListTest {

    private lateinit var list: ObservableList<String>

    private lateinit var mockListObserver: MockListObserver<String>

    private lateinit var sortedList: SortedList<String>

    @BeforeTest
    fun setUp() {
        this.list = ObservableCollections.observableArrayList()
        this.list.addAll("a", "c", "d", "c")
        this.sortedList = this.list.sorted()
        this.mockListObserver = MockListObserver()
        this.sortedList.addListener(this.mockListObserver)
    }

    @Test
    fun testBaseComparator() {
        assertNotNull(this.sortedList.comparator)
    }

    @Test
    fun testComparatorProperty() {
        val comparator = this.sortedList.comparatorProperty
        assertSame(this.sortedList, comparator.bean)
        assertEquals("comparator", comparator.name)
    }

    @Test
    fun testNoChange() {
        assertEquals(listOf("a", "c", "c", "d"), this.sortedList)
        this.mockListObserver.check0()

        compareIndices()
    }

    @Test
    fun testGet() {
        assertFailsWith<IndexOutOfBoundsException> {
            this.sortedList[8]
        }
    }

    @Test
    fun testGetSourceIndexFor() {
        val sortedList2 = this.sortedList.sorted { o1, o2 -> o2.compareTo(o1) }
        assertEquals(3, sortedList2.getSourceIndexFor(this.sortedList, 0))
        assertEquals(2, sortedList2.getSourceIndexFor(this.list, 0))
    }

    @Test
    @Suppress("RemoveExplicitTypeArguments")
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

    @Test
    fun testAddSingle() {
        this.list.add("b")
        assertEquals(listOf("a", "b", "c", "c", "d"), this.sortedList)
        this.mockListObserver.check1AddRemove(this.sortedList, listOf(), 1, 2)
        assertEquals(0, this.sortedList.getSourceIndex(0))
        assertEquals(4, this.sortedList.getSourceIndex(1))
        assertEquals(1, this.sortedList.getSourceIndex(2))
        assertEquals(3, this.sortedList.getSourceIndex(3))
        assertEquals(2, this.sortedList.getSourceIndex(4))

        compareIndices()
    }

    @Test
    fun testRemove() {
        this.list.removeAll(listOf("c"))
        assertEquals(listOf("a", "d"), this.sortedList)
        this.mockListObserver.check1AddRemove(this.sortedList, listOf("c", "c"), 1, 1)
        assertEquals(0, this.sortedList.getSourceIndex(0))
        assertEquals(1, this.sortedList.getSourceIndex(1))
        this.mockListObserver.clear()
        this.list.removeAll(listOf("a", "d"))
        this.mockListObserver.check1AddRemove(this.sortedList, listOf("a", "d"), 0, 0)

        compareIndices()
    }

    @Test
    fun testRemoveSingle() {
        this.list.remove("a")
        assertEquals(listOf("c", "c", "d"), this.sortedList)
        this.mockListObserver.check1AddRemove(this.sortedList, listOf("a"), 0, 0)
        assertEquals(0, this.sortedList.getSourceIndex(0))
        assertEquals(2, this.sortedList.getSourceIndex(1))
        assertEquals(1, this.sortedList.getSourceIndex(2))

        compareIndices()
    }

    @Test
    fun testMultipleOperations() {
        this.list.removeAt(2)
        assertEquals(listOf("a", "c", "c"), this.sortedList)
        this.mockListObserver.check1AddRemove(this.sortedList, listOf("d"), 3, 3)
        this.mockListObserver.clear()
        this.list.add("b")
        assertEquals(listOf("a", "b", "c", "c"), this.sortedList)
        this.mockListObserver.check1AddRemove(this.sortedList, listOf(), 1, 2)

        compareIndices()
    }

    @Test
    fun testPureRemove() {
        this.list.removeAll(listOf("c", "d"))
        this.mockListObserver.check1AddRemove(this.sortedList, listOf("c", "c", "d"), 1, 1)
        assertEquals(0, this.sortedList.getSourceIndex(0))

        compareIndices()
    }

    @Test
    fun testChangeComparator() {
        val op: SimpleObjectProperty<Comparator<String>?> = SimpleObjectProperty(naturalOrder())

        this.sortedList = SortedList(this.list)
        assertEquals(listOf("a", "c", "d", "c"), this.sortedList)
        compareIndices()

        this.sortedList.comparatorProperty.bind(op)
        assertEquals(listOf("a", "c", "c", "d"), this.sortedList)
        compareIndices()

        this.sortedList.addListener(this.mockListObserver)
        op.set(Comparator { o1, o2 -> -o1.compareTo(o2) })
        assertEquals(listOf("d", "c", "c", "a"), this.sortedList)
        this.mockListObserver.check1Permutation(this.sortedList, intArrayOf(3, 1, 2, 0))
        // could be also 3, 2, 1, 0, but the algorithm goes this way
        compareIndices()

        this.mockListObserver.clear()
        op.set(null)
        assertEquals(listOf("a", "c", "d", "c"), this.sortedList)
        this.mockListObserver.check1Permutation(this.sortedList, intArrayOf(2, 1, 3, 0))
        compareIndices()
    }

    /**
     * A slightly updated test provided by "Kleopatra" (https://bugs.openjdk.java.net/browse/JDK-8112763)
     */
    @Test
    fun testSourceIndex() {
        val sourceList: ObservableList<Double> = ObservableCollections.observableArrayList(1300.0, 400.0, 600.0)
        // the list to be removed again, note that its highest value is greater than the highest in the base list before
        // adding
        val other: List<Double> = listOf(50.0, -300.0, 4000.0)
        sourceList.addAll(other)
        // wrap into a sorted list and add a listener to the sorted
        val sorted: SortedList<Double> = sourceList.sorted()
        val listener: ListChangeListener<Double> = ListChangeListener { change ->
            assertEquals(listOf(400.0, 600.0, 1300.0), change.list)

            change.next()
            assertEquals(listOf(-300.0, 50.0), change.removed)
            assertEquals(0, change.from)
            assertEquals(0, change.to)
            assertTrue(change.next())
            assertEquals(listOf(4000.0), change.removed)
            assertEquals(3, change.from)
            assertEquals(3, change.to)
            assertFalse(change.next())

            // grab sourceIndex of last (aka: highest) value in sorted list
            val sourceIndex: Int = sorted.getSourceIndex(sorted.lastIndex)
            assertEquals(0, sourceIndex)
        }
        sorted.addListener(listener)
        sourceList.removeAll(other)

        compareIndices(sorted)
    }

    @Test
    fun testMutableElement() {
        val list: ObservableList<Person> = createPersonsList()
        val sorted: SortedList<Person> = list.sorted()
        assertEquals(listOf(Person("five"), Person("four"), Person("one"), Person("three"), Person("two")), sorted)
        val listener: MockListObserver<Person> = MockListObserver()
        sorted.addListener(listener)
        list[3].name.set("zero") // four -> zero
        val expected: ObservableList<Person> = ObservableCollections.observableArrayList(Person("five"), Person("one"),
                Person("three"), Person("two"), Person("zero"))
        listener.checkPermutation(0, expected, 0, list.size, intArrayOf(0, 4, 1, 2, 3))
        listener.checkUpdate(1, expected, 4, 5)
        assertEquals(expected, sorted)

        compareIndices(sorted)
    }

    @Test
    fun testMutableElementUnsorted_rt39541() {
        val list: ObservableList<Person> = createPersonsList()
        val unsorted: SortedList<Person> = SortedList(list)
        val listener: MockListObserver<Person> = MockListObserver()
        unsorted.addListener(listener)
        list[3].name.set("zero") // four -> zero
        val expected: ObservableList<Person> = ObservableCollections.observableArrayList(Person("one"), Person("two"),
                Person("three"), Person("zero"), Person("five"))
        listener.check1Update(expected, 3, 4)

        compareIndices(unsorted)
    }

    @Test
    fun testMutableElementUnsortedChain_rt39541() {
        val items: ObservableList<Person> = createPersonsList()

        val sorted: SortedList<Person> = items.sorted()
        val unsorted: SortedList<Person> = SortedList(sorted)

        assertEquals(sorted, unsorted)

        val listener: MockListObserver<Person> = MockListObserver()
        unsorted.addListener(listener)
        items[3].name.set("zero") // four -> zero
        val expected: ObservableList<Person> = ObservableCollections.observableArrayList(Person("five"), Person("one"),
                Person("three"), Person("two"), Person("zero"))
        listener.checkPermutation(0, expected, 0, expected.size, intArrayOf(0, 4, 1, 2, 3))
        listener.checkUpdate(1, expected, 4, 5)
        assertEquals(expected, sorted)
        assertEquals(expected, unsorted)

        compareIndices(sorted)
        compareIndices(unsorted)
    }

    @Test
    fun testMutableElementSortedFilteredChain() {
        val extractor: Callback<Person, Array<Observable>> = Callback { param -> arrayOf(param.name) }
        val items: ObservableList<Person> = ObservableCollections.observableArrayList(extractor)
        items.addAll(Person("b"), Person("c"), Person("a"), Person("f"), Person("e"), Person("d"))

        val filtered: FilteredList<Person> = items.filtered { e: Person -> !e.name.valueSafe.startsWith("z") }
        val filterListener: MockListObserver<Person> = MockListObserver()
        filtered.addListener(filterListener)

        val sorted: SortedList<Person> = filtered.sorted(Comparator.comparing { t: Person -> t.name.valueSafe })
        val sortListener: MockListObserver<Person> = MockListObserver()
        sorted.addListener(sortListener)
        items[2].name.set("z") // "a" -> "z"
        filterListener.check1AddRemove(filtered, listOf(Person("z")), 2, 2)
        sortListener.check1AddRemove(sorted, listOf(Person("z")), 0, 0)
        val expected: ObservableList<Person> = ObservableCollections.observableArrayList(Person("b"), Person("c"),
                Person("d"), Person("e"), Person("f"))
        assertEquals(expected, sorted)

        compareIndices(sorted)
    }

    private fun createPersonsList(): ObservableList<Person> {
        val list: ObservableList<Person> = ObservableCollections.observableArrayList { param -> arrayOf(param.name) }
        list.addAll(Person("one"), Person("two"), Person("three"), Person("four"), Person("five"))
        return list
    }

    @Test
    fun testNotComparable() {
        val o1: Any = object : Any() {
            override fun toString(): String {
                return "c"
            }
        }
        val o2: Any = object : Any() {
            override fun toString(): String {
                return "a"
            }
        }

        val o3: Any = object : Any() {
            override fun toString(): String {
                return "d"
            }
        }
        val list: ObservableList<Any> = ObservableCollections.observableArrayList(o1, o2, o3)

        val sorted: SortedList<Any> = list.sorted()
        assertEquals(listOf(o2, o1, o3), sorted)

        compareIndices(sorted)
    }

    @Test
    fun testCompareNulls() {
        val list: ObservableList<String?> = ObservableCollections.observableArrayList("g", "a", null, "z")

        val sorted: SortedList<String?> = list.sorted()
        assertEquals(listOf(null, "a", "g", "z"), sorted)
    }

    private class Permutator<E>(list: MutableList<E>) : ObservableListWrapper<E>(list) {

        private val backingList: MutableList<E> = list

        fun swap() {
            val first: E = this[0]
            this.backingList[0] = this[this.lastIndex]
            this.backingList[this.lastIndex] = first
            fireChange(SimplePermutationChange(0, this.size, intArrayOf(2, 1, 0), this))
        }

    }

    /**
     * SortedList can't cope with permutations.
     */
    @Test
    fun testPermutate() {
        val list: MutableList<Int> = ArrayList()
        for (i in 0 until 3) {
            list.add(i)
        }
        val permutator: Permutator<Int> = Permutator(list)
        val sorted: SortedList<Int> = SortedList(permutator)
        permutator.swap()

        compareIndices(sorted)
    }

    @Test
    fun testUnsorted() {
        val sorted: SortedList<String> = SortedList(this.list)
        assertEquals(sorted, this.list)
        assertEquals(this.list, sorted)

        this.list.removeAll("a", "d")

        assertEquals(sorted, this.list)

        this.list.addAll(0, listOf("a", "b", "c"))

        assertEquals(sorted, this.list)

        ObservableCollections.sort(this.list)

        assertEquals(sorted, this.list)

        compareIndices(sorted)
    }

    @Test
    fun testUnsorted2() {
        this.list.setAll("a", "b", "c", "d", "e", "f")
        val sorted: SortedList<String> = SortedList(this.list)
        assertEquals(sorted, this.list)

        this.list.removeAll("b", "c", "d")

        assertEquals(sorted, this.list)

        compareIndices(sorted)
    }

    @Test
    fun testSortedNaturalOrder() {
        assertEquals(listOf("a", "c", "c", "d"), this.list.sorted())
    }

    @Test
    fun testRemoveFromDuplicates() {
        val toRemove = "A"
        val other = "A"
        this.list = ObservableCollections.observableArrayList(other, toRemove)
        val c: Comparator<String> = naturalOrder()
        val sorted: SortedList<String> = this.list.sorted(c)

        this.list.removeAt(1)

        assertEquals(1, sorted.size)
        assertSame(sorted[0], other)

        compareIndices(sorted)
    }

    @Test
    fun testAddAllOnEmpty() {
        this.list = ObservableCollections.observableArrayList()
        val sl: SortedList<String> = this.list.sorted(String.CASE_INSENSITIVE_ORDER)
        this.list.addAll("B", "A")

        assertEquals(listOf("A", "B"), sl)

        compareIndices(sl)
    }

    @Test
    fun test_rt36353_sortedList() {
        val data: ObservableList<String> = ObservableCollections.observableArrayList("2", "1", "3")
        val sortedList: SortedList<String> = SortedList(data)

        val pMap: HashMap<Int, Int> = HashMap()
        sortedList.addListener(ListChangeListener { change ->
            while (change.next()) {
                if (change.wasPermutated) {
                    for (i in change.from until change.to) {
                        pMap[i] = change.getPermutation(i)
                    }
                }
            }
        })

        val expected: MutableMap<Int, Int> = HashMap()

        // comparator that will create list of [1,2,3]. Sort indices based on previous order [2,1,3].
        sortedList.comparator = naturalOrder()
        assertEquals(listOf("1", "2", "3"), sortedList)
        expected[0] = 1 // item "2" has moved from index 0 to index 1
        expected[1] = 0 // item "1" has moved from index 1 to index 0
        expected[2] = 2 // item "3" has remained in index 2
        assertEquals(expected, pMap)
        compareIndices(sortedList)

        // comparator that will create list of [3,2,1]. Sort indices based on previous order [1,2,3].
        sortedList.comparator = reverseOrder()
        assertEquals(listOf("3", "2", "1"), sortedList)
        expected[0] = 2 // item "1" has moved from index 0 to index 2
        expected[1] = 1 // item "2" has remained in index 1
        expected[2] = 0 // item "3" has moved from index 2 to index 0
        assertEquals(expected, pMap)
        compareIndices(sortedList)

        // null comparator so sort order should return to [2,1,3]. Sort indices based on previous order [3,2,1].
        sortedList.comparator = null
        assertEquals(listOf("2", "1", "3"), sortedList)
        expected[0] = 2 // item "3" has moved from index 0 to index 2
        expected[1] = 0 // item "2" has moved from index 1 to index 0
        expected[2] = 1 // item "1" has moved from index 2 to index 1
        assertEquals(expected, pMap)
        compareIndices(sortedList)
    }

    @Test
    fun testAddWhenUnsorted() {
        this.sortedList.comparator = null
        this.mockListObserver.clear()
        this.list.add(2, "b")
        assertEquals(5, this.sortedList.size)
        assertEquals(listOf("a", "c", "b", "d", "c"), this.sortedList)
        this.mockListObserver.check1AddRemove(this.sortedList, listOf(), 2, 3)
        compareIndices()

        this.mockListObserver.clear()
        this.sortedList.comparator = naturalOrder()
        assertEquals(5, this.sortedList.size)
        assertEquals(listOf("a", "b", "c", "c", "d"), this.sortedList)
        this.mockListObserver.check1Permutation(this.sortedList, intArrayOf(0, 2, 1, 4, 3))
        compareIndices()

        this.mockListObserver.clear()
        this.sortedList.comparator = null
        assertEquals(5, this.sortedList.size)
        assertEquals(listOf("a", "c", "b", "d", "c"), this.sortedList)
        this.mockListObserver.check1Permutation(this.sortedList, intArrayOf(0, 2, 1, 4, 3))
        compareIndices()
    }

    @Test
    fun testRemoveWhenUnsorted() {
        this.sortedList.comparator = null
        this.mockListObserver.clear()
        this.list.removeAt(1)
        assertEquals(3, this.sortedList.size)
        assertEquals(listOf("a", "d", "c"), this.sortedList)
        this.mockListObserver.check1AddRemove(this.sortedList, listOf("c"), 1, 1)
        compareIndices()

        this.mockListObserver.clear()
        this.sortedList.comparator = naturalOrder()
        assertEquals(3, this.sortedList.size)
        assertEquals(listOf("a", "c", "d"), this.sortedList)
        this.mockListObserver.check1Permutation(this.sortedList, intArrayOf(0, 2, 1))
        compareIndices()

        this.mockListObserver.clear()
        this.sortedList.comparator = null
        assertEquals(3, this.sortedList.size)
        assertEquals(listOf("a", "d", "c"), this.sortedList)
        this.mockListObserver.check1Permutation(this.sortedList, intArrayOf(0, 2, 1))
        compareIndices()
    }

    @Test
    fun testSetWhenUnsorted() {
        this.sortedList.comparator = null
        this.mockListObserver.clear()
        this.list[1] = "e"
        assertEquals(4, this.sortedList.size)
        assertEquals(listOf("a", "e", "d", "c"), this.sortedList)
        this.mockListObserver.check1AddRemove(this.sortedList, listOf("c"), 1, 2)
        compareIndices()

        this.mockListObserver.clear()
        this.sortedList.comparator = naturalOrder()
        assertEquals(4, this.sortedList.size)
        assertEquals(listOf("a", "c", "d", "e"), this.sortedList)
        this.mockListObserver.check1Permutation(this.sortedList, intArrayOf(0, 3, 2, 1))
        compareIndices()

        this.mockListObserver.clear()
        this.sortedList.comparator = null
        assertEquals(4, this.sortedList.size)
        assertEquals(listOf("a", "e", "d", "c"), this.sortedList)
        this.mockListObserver.check1Permutation(this.sortedList, intArrayOf(0, 3, 2, 1))
        compareIndices()
    }

}