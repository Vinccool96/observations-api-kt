package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.sun.collections.ObservableListWrapper
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ListChangeBuilderTest {

    private lateinit var builder: ListChangeBuilder<String>

    private lateinit var observableList: ObservableListWrapper<String>

    private lateinit var list: ArrayList<String>

    private lateinit var observer: MockListObserver<String>

    @BeforeTest
    fun setUp() {
        this.observer = MockListObserver()
        this.list = arrayListOf("a", "b", "c", "d")
        this.observableList = ObservableListWrapper(this.list)
        this.observableList.addListener(this.observer)
        this.builder = ListChangeBuilder(this.observableList)
    }

    @Test
    fun testAddRemove() {
        this.builder.beginChange()
        this.list.removeAt(2)
        this.builder.nextRemove(2, "c")
        this.list.add(2, "cc")
        this.list.add(3, "ccc")
        this.builder.nextAdd(2, 4)
        this.list.removeAt(2)
        this.builder.nextRemove(2, "cc")
        this.list.removeAt(3)
        this.builder.nextRemove(3, "d")
        this.list.add(0, "aa")
        this.builder.nextAdd(0, 1)
        this.builder.endChange()

        assertEquals(listOf("aa", "a", "b", "ccc"), this.list)

        this.observer.checkAddRemove(0, this.observableList, emptyList(), 0, 1)
        this.observer.checkAddRemove(1, this.observableList, listOf("c", "d"), 3, 4)
    }

    @Test
    fun testAddRemove_2() {
        this.builder.beginChange()
        this.list.add("e")
        this.builder.nextAdd(4, 5)
        this.list.add(3, "dd")
        this.builder.nextAdd(3, 4)
        this.list.removeAt(4)
        this.builder.nextRemove(4, "d")

        this.list.removeAt(0)
        this.builder.nextRemove(0, "a")
        this.builder.endChange()

        assertEquals(listOf("b", "c", "dd", "e"), this.list)

        this.observer.checkAddRemove(0, this.observableList, listOf("a"), 0, 0)
        this.observer.checkAddRemove(1, this.observableList, listOf("d"), 2, 4)
    }

    @Test
    fun testAddRemove_3() {
        this.builder.beginChange()
        this.list.add("e")
        this.builder.nextAdd(4, 5)

        this.list[0] = "aa"
        this.builder.nextReplace(0, 1, listOf("a"))

        this.list.removeAt(4)
        this.builder.nextRemove(4, "e")

        this.list.removeAt(0)
        this.builder.nextRemove(0, "aa")
        this.builder.endChange()

        assertEquals(listOf("b", "c", "d"), this.list)

        this.observer.check1AddRemove(this.observableList, listOf("a"), 0, 0)
    }

    @Test
    fun testAddRemove_4() {
        this.builder.beginChange()

        this.list.add("e")
        this.builder.nextAdd(4, 5)

        this.list.removeAt(1)
        this.builder.nextRemove(1, "b")

        this.list.add(1, "bb")
        this.builder.nextAdd(1, 2)

        this.builder.endChange()

        assertEquals(listOf("a", "bb", "c", "d", "e"), this.list)

        this.observer.checkAddRemove(0, this.observableList, listOf("b"), 1, 2)
        this.observer.checkAddRemove(1, this.observableList, emptyList(), 4, 5)
    }

    @Test
    fun testAddRemove_5() {
        this.builder.beginChange()

        this.list.addAll(1, listOf("x", "y"))
        this.builder.nextAdd(1, 3)

        this.list.removeAt(2)
        this.builder.nextRemove(2, "y")

        this.builder.endChange()

        this.observer.check1AddRemove(this.observableList, emptyList(), 1, 2)
    }

    @Test
    fun testAdd() {
        this.builder.beginChange()

        this.list.add(1, "aa")
        this.builder.nextAdd(1, 2)
        this.list.add(5, "e")
        this.builder.nextAdd(5, 6)
        this.list.add(1, "aa")
        this.builder.nextAdd(1, 2)
        this.list.add(2, "aa")
        this.builder.nextAdd(2, 3)
        this.list.add(4, "aa")
        this.builder.nextAdd(4, 5)

        this.builder.endChange()

        assertEquals(listOf("a", "aa", "aa", "aa", "aa", "b", "c", "d", "e"), this.list)

        this.observer.checkAddRemove(0, this.observableList, emptyList(), 1, 5)
        this.observer.checkAddRemove(1, this.observableList, emptyList(), 8, 9)
    }

    @Test
    fun testRemove() {
        this.builder.beginChange()
        this.list.removeAt(0)
        this.builder.nextRemove(0, "a")
        this.list.removeAt(2)
        this.builder.nextRemove(2, "d")
        this.list.removeAt(0)
        this.builder.nextRemove(0, "b")
        this.builder.endChange()

        assertEquals(listOf("c"), this.list)

        this.observer.checkAddRemove(0, this.observableList, listOf("a", "b"), 0, 0)
        this.observer.checkAddRemove(1, this.observableList, listOf("d"), 1, 1)
    }

    @Test
    fun testRemove_2() {
        this.builder.beginChange()
        this.list.removeAt(1)
        this.builder.nextRemove(1, "b")
        this.list.removeAt(2)
        this.builder.nextRemove(2, "d")
        this.list.removeAt(0)
        this.builder.nextRemove(0, "a")
        this.builder.endChange()

        assertEquals(listOf("c"), this.list)

        this.observer.checkAddRemove(0, this.observableList, listOf("a", "b"), 0, 0)
        this.observer.checkAddRemove(1, this.observableList, listOf("d"), 1, 1)
    }

    @Test
    fun testUpdate() {
        this.builder.beginChange()
        this.builder.nextUpdate(1)
        this.builder.nextUpdate(0)
        this.builder.nextUpdate(3)
        this.builder.endChange()

        this.observer.checkUpdate(0, this.observableList, 0, 2)
        this.observer.checkUpdate(1, this.observableList, 3, 4)
    }

    @Test
    fun testUpdate_2() {
        this.builder.beginChange()
        this.builder.nextUpdate(3)
        this.builder.nextUpdate(1)
        this.builder.nextUpdate(0)
        this.builder.nextUpdate(0)
        this.builder.nextUpdate(2)
        this.builder.endChange()

        this.observer.checkUpdate(0, this.observableList, 0, 4)
    }

    @Test
    fun testPermutation() {
        this.builder.beginChange()

        this.builder.nextPermutation(0, 4, intArrayOf(3, 2, 1, 0))
        this.builder.nextPermutation(1, 4, intArrayOf(3, 2, 1))
        this.builder.endChange()

        this.observer.check1Permutation(this.observableList, intArrayOf(1, 2, 3, 0))
    }

    @Test
    fun testUpdateAndAddRemove() {
        this.builder.beginChange()
        this.builder.nextUpdate(1)
        this.builder.nextUpdate(2)
        this.list.removeAt(2)
        this.builder.nextRemove(2, "c")
        this.list.add(2, "cc")
        this.list.add(3, "ccc")
        this.builder.nextAdd(2, 4)
        this.builder.nextUpdate(2)
        this.list.removeAt(2)
        this.builder.nextRemove(2, "cc")
        this.list.removeAt(3)
        this.builder.nextRemove(3, "d")
        this.list.add(0, "aa")
        this.builder.nextAdd(0, 1)
        this.builder.endChange()

        assertEquals(listOf("aa", "a", "b", "ccc"), this.list)

        this.observer.checkAddRemove(0, this.observableList, emptyList(), 0, 1)
        this.observer.checkAddRemove(1, this.observableList, listOf("c", "d"), 3, 4)
        this.observer.checkUpdate(2, this.observableList, 2, 3)
    }

    @Test
    fun testUpdateAndAddRemove_2() {
        this.builder.beginChange()
        this.builder.nextUpdate(0)
        this.builder.nextUpdate(1)
        this.list.add(1, "aaa")
        this.list.add(1, "aa")
        this.builder.nextAdd(1, 3)
        this.builder.endChange()

        assertEquals(listOf("a", "aa", "aaa", "b", "c", "d"), this.list)

        this.observer.checkAddRemove(0, this.observableList, emptyList(), 1, 3)
        this.observer.checkUpdate(1, this.observableList, 0, 1)
        this.observer.checkUpdate(2, this.observableList, 3, 4)
    }

    @Test
    fun testUpdateAndAddRemove_3() {
        this.builder.beginChange()
        this.builder.nextUpdate(2)
        this.builder.nextUpdate(3)
        this.list.add(1, "aa")
        this.builder.nextAdd(1, 2)
        this.list.removeAt(0)
        this.builder.nextRemove(0, "a")
        this.builder.endChange()

        assertEquals(listOf("aa", "b", "c", "d"), this.list)

        this.observer.checkAddRemove(0, this.observableList, listOf("a"), 0, 1)
        this.observer.checkUpdate(1, this.observableList, 2, 4)
    }

    @Test
    fun testUpdateAndPermutation() {
        this.builder.beginChange()

        this.builder.nextUpdate(1)
        this.builder.nextUpdate(2)
        this.builder.nextPermutation(1, 4, intArrayOf(3, 2, 1))
        this.builder.endChange()

        this.observer.checkPermutation(0, this.observableList, 1, 4, intArrayOf(3, 2, 1))
        this.observer.checkUpdate(1, this.observableList, 2, 4)
    }

    @Test
    fun testUpdateAndPermutation_2() {
        this.builder.beginChange()

        this.builder.nextUpdate(0)
        this.builder.nextUpdate(2)
        this.builder.nextPermutation(0, 4, intArrayOf(1, 3, 2, 0))
        this.builder.endChange()

        this.observer.checkPermutation(0, this.observableList, 0, 4, intArrayOf(1, 3, 2, 0))
        this.observer.checkUpdate(1, this.observableList, 1, 3)
    }

    @Test
    fun testAddAndPermutation() {
        this.builder.beginChange()

        this.builder.nextAdd(1, 2) // as-if "b" was added
        this.builder.nextPermutation(0, 3, intArrayOf(2, 0, 1)) // new order is "b", "c", "a", "d"

        this.builder.endChange()
        // "c", "a", "d" before "b" was added
        this.observer.checkPermutation(0, this.observableList, 0, 3, intArrayOf(1, 0, 2))

        this.observer.checkAddRemove(1, this.observableList, emptyList(), 0, 1)
    }

    @Test
    fun testRemoveAndPermutation() {
        this.builder.beginChange()

        val removed = listOf("bb", "bbb")

        this.builder.nextRemove(2, removed)
        this.builder.nextPermutation(0, 3, intArrayOf(2, 0, 1))

        this.builder.endChange()

        this.observer.checkPermutation(0, this.observableList, 0, 6, intArrayOf(4, 0, 2, 3, 1, 5))
        this.observer.checkAddRemove(1, this.observableList, removed, 1, 1)
    }

    @Test
    fun testAddRemoveAndPermutation() {
        this.builder.beginChange()

        // Expect list to be "b", "c1", "c2", "d"
        val removed = listOf("c1", "c2")
        // After add: "a", "b", "c1", "c2", "d"
        this.builder.nextAdd(0, 1)
        // After replace: "a", "b", "c", "d"
        this.builder.nextReplace(2, 3, removed)
        this.builder.nextPermutation(1, 4, intArrayOf(3, 1, 2))

        this.builder.endChange()

        this.observer.checkPermutation(0, this.observableList, 0, 4, intArrayOf(3, 1, 2, 0))
        this.observer.checkAddRemove(1, this.observableList, removed, 0, 2)
    }

    @Test
    fun testPermutationAndAddRemove() {
        this.builder.beginChange()

        // Expect list to be "b", "c1", "c2", "d"
        // After perm: "b", "c2", "d", "c1"
        this.builder.nextPermutation(1, 4, intArrayOf(3, 1, 2))
        // After add: "a", "b", "c2", "d", "c1"
        this.builder.nextAdd(0, 1)
        this.builder.nextReplace(2, 3, listOf("c2"))
        this.builder.nextRemove(4, listOf("c1"))

        this.builder.endChange()

        this.observer.checkPermutation(0, this.observableList, 1, 4, intArrayOf(3, 1, 2))
        this.observer.checkAddRemove(1, this.observableList, listOf(), 0, 1)
        this.observer.checkAddRemove(2, this.observableList, listOf("c2"), 2, 3)
        this.observer.checkAddRemove(3, this.observableList, listOf("c1"), 4, 4)
    }

    @Test
    fun testPermutationAddRemoveAndPermutation() {
        this.builder.beginChange()
        // Expect list to be "b", "c1", "d"
        val removed = listOf("c1")
        // After perm: "c1", "b", "d"
        this.builder.nextPermutation(0, 2, intArrayOf(1, 0))
        // After add: "a", "c1", "b", "d"
        this.builder.nextAdd(0, 1)
        // After remove/add: "a", "b", "c", "d"
        this.builder.nextRemove(1, removed)
        this.builder.nextAdd(2, 3)
        // After perm: "a", "c", "d", "b"
        this.builder.nextPermutation(1, 4, intArrayOf(3, 1, 2))

        this.builder.endChange()

        // When combined, it's from the expected list:
        // permutation to "c1", "b", "d"
        this.observer.checkPermutation(0, this.observableList, 0, 3, intArrayOf(2, 0, 1))
        // add remove to "a", "c", "d", "b"
        this.observer.checkAddRemove(1, this.observableList, removed, 0, 2)
    }

    @Test(expected = IllegalStateException::class)
    fun testNextAddWithoutBegin() {
        this.builder.nextAdd(0, 1)
    }

    @Test(expected = IllegalStateException::class)
    fun testNextRemoveWithoutBegin() {
        this.builder.nextRemove(0, "a")
    }

    @Test(expected = IllegalStateException::class)
    fun testNextRemove2WithoutBegin() {
        this.builder.nextRemove(0, listOf("a"))
    }

    @Test(expected = IllegalStateException::class)
    fun testNextUpdateWithoutBegin() {
        this.builder.nextUpdate(0)
    }

    @Test(expected = IllegalStateException::class)
    fun testNextSetWithoutBegin() {
        this.builder.nextSet(0, "aa")
    }

    @Test(expected = IllegalStateException::class)
    fun testNextReplaceWithoutBegin() {
        this.builder.nextReplace(0, 1, listOf("aa"))
    }

    @Test(expected = IllegalStateException::class)
    fun testNextPermutationWithoutBegin() {
        this.builder.nextPermutation(0, 2, intArrayOf(1, 0))
    }

    @Test
    fun testEmpty() {
        this.builder.beginChange()
        this.builder.endChange()

        this.observer.check0()
    }

    @Test
    fun testToString_Update() {
        this.observableList.removeListener(this.observer)
        this.observableList.addListener(ListChangeListener { change -> assertNotNull(change.toString()) })
        this.builder.beginChange()

        this.builder.nextUpdate(0)

        this.builder.endChange()
    }

    @Test
    fun testToString_Add() {
        this.observableList.removeListener(this.observer)
        this.observableList.addListener(ListChangeListener { change -> assertNotNull(change.toString()) })
        this.builder.beginChange()

        this.builder.nextAdd(0, 1)

        this.builder.endChange()
    }

    @Test
    fun testToString_Remove() {
        this.observableList.removeListener(this.observer)
        this.observableList.addListener(ListChangeListener { change -> assertNotNull(change.toString()) })
        this.builder.beginChange()

        this.builder.nextRemove(0, "a")

        this.builder.endChange()
    }

    @Test
    fun testToString_Remove2() {
        this.observableList.removeListener(this.observer)
        this.observableList.addListener(ListChangeListener { change -> assertNotNull(change.toString()) })
        this.builder.beginChange()

        this.builder.nextRemove(0, listOf("a", "b"))

        this.builder.endChange()
    }

    @Test
    fun testToString_Set() {
        this.observableList.removeListener(this.observer)
        this.observableList.addListener(ListChangeListener { change -> assertNotNull(change.toString()) })
        this.builder.beginChange()

        this.builder.nextSet(0, "aa")

        this.builder.endChange()
    }

    @Test
    fun testToString_Replace() {
        this.observableList.removeListener(this.observer)
        this.observableList.addListener(ListChangeListener { change -> assertNotNull(change.toString()) })
        this.builder.beginChange()

        this.builder.nextReplace(0, 2, listOf("aa", "aaa"))

        this.builder.endChange()
    }

    @Test
    fun testToString_Composed() {
        this.observableList.removeListener(this.observer)
        this.observableList.addListener(ListChangeListener { change -> assertNotNull(change.toString()) })
        this.builder.beginChange()

        this.builder.nextUpdate(0)

        this.builder.nextAdd(0, 3)

        this.builder.endChange()
    }

    @Test
    fun testToString_Permutation() {
        this.observableList.removeListener(this.observer)
        this.observableList.addListener(ListChangeListener { change -> assertNotNull(change.toString()) })
        this.builder.beginChange()

        this.builder.nextPermutation(0, 2, intArrayOf(1, 0))

        this.builder.endChange()
    }

}