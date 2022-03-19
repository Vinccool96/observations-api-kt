package io.github.vinccool96.observationskt.collections.transformation

import io.github.vinccool96.observationskt.beans.property.ObjectProperty
import io.github.vinccool96.observationskt.beans.property.SimpleObjectProperty
import io.github.vinccool96.observationskt.collections.MockListObserver
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.Person
import io.github.vinccool96.observationskt.sun.collections.ObservableListWrapper
import kotlin.test.BeforeTest
import kotlin.test.Test
import java.util.*
import java.util.function.Predicate
import kotlin.test.assertEquals

class FilteredListTest {

    private lateinit var list: ObservableList<String>

    private lateinit var mlo: MockListObserver<String>

    private lateinit var filteredList: FilteredList<String>

    @BeforeTest
    fun setUp() {
        this.list = ObservableCollections.observableArrayList()
        this.list.addAll("a", "c", "d", "c")
        val predicate: Predicate<String> = Predicate {e: String -> e != "c"}
        this.mlo = MockListObserver()
        this.filteredList = FilteredList(this.list, predicate)
        this.filteredList.addListener(this.mlo)
    }

    @Test
    fun testLiveMode() {
        assertEquals(listOf("a", "d"), this.filteredList)
        this.mlo.check0()
    }

    @Test
    fun testLiveMode_Add() {
        this.list.clear()
        this.mlo.clear()
        assertEquals(Collections.emptyList(), this.filteredList)
        this.list.addAll("a", "c", "d", "c")
        assertEquals(listOf("a", "d"), this.filteredList)
        this.mlo.check1AddRemove(this.filteredList, listOf(), 0, 2)
        this.mlo.clear()
        this.list.add("c")
        this.mlo.check0()
        this.list.add(1, "b")
        assertEquals(listOf("a", "b", "d"), this.filteredList)
        this.mlo.check1AddRemove(this.filteredList, listOf(), 1, 2)
    }

    @Test
    fun testLiveMode_Remove() {
        assertEquals(listOf("a", "d"), this.filteredList)
        this.list.removeAll(listOf("c"))
        assertEquals(listOf("a", "d"), this.filteredList)
        this.mlo.check0()
        this.mlo.clear()
        this.list.remove("a")
        assertEquals(listOf("d"), this.filteredList)
        this.mlo.check1AddRemove(this.filteredList, listOf("a"), 0, 0)
    }

    @Test
    fun testLiveMode_Permutation() {
        ObservableCollections.sort(this.list) {o1, o2 -> -o1.compareTo(o2)}
        this.mlo.check1Permutation(this.filteredList, intArrayOf(1, 0))
        assertEquals(listOf("d", "a"), this.filteredList)
    }

    @Test
    fun testLiveMode_changeMatcher() {
        val pProperty: ObjectProperty<Predicate<String>> = SimpleObjectProperty(Predicate {e: String -> e != "c"})
        this.filteredList = FilteredList(this.list)
        this.filteredList.predicateProperty.bind(pProperty)
        this.filteredList.addListener(this.mlo)
        assertEquals(listOf("a", "d"), this.filteredList)
        this.mlo.check0()
        pProperty.set(Predicate {s: String -> s != "d"})
        this.mlo.check1AddRemove(this.filteredList, listOf("a", "d"), 0, 3)
    }

    @Test
    fun testLiveMode_mutableElement() {
        val p1 = Person("a")
        val list: ObservableList<Person> =
                Person.createPersonsList(p1, p1, Person("BB"), Person("B"), p1, p1, Person("BC"), p1,
                        Person("C"))

        val filtered: FilteredList<Person> = FilteredList(list) {p: Person -> p.name.get()!!.length > 1}
        val lo: MockListObserver<Person> = MockListObserver()
        filtered.addListener(lo)

        assertEquals(Person.createPersonsList("BB", "BC"), filtered)

        p1.name.set("AA")
        lo.checkAddRemove(0, filtered, listOf(), 0, 2)
        lo.checkAddRemove(1, filtered, listOf(), 3, 5)
        lo.checkAddRemove(2, filtered, listOf(), 6, 7)
        assertEquals(Person.createPersonsList("AA", "AA", "BB", "AA", "AA", "BC", "AA"), filtered)

        lo.clear()
        p1.name.set("AAA")
        lo.checkUpdate(0, filtered, 0, 2)
        lo.checkUpdate(1, filtered, 3, 5)
        lo.checkUpdate(2, filtered, 6, 7)
        assertEquals(Person.createPersonsList("AAA", "AAA", "BB", "AAA", "AAA", "BC", "AAA"), filtered)

        lo.clear()
        p1.name.set("A")
        lo.checkAddRemove(0, filtered, Person.createPersonsList("A", "A"), 0, 0)
        lo.checkAddRemove(1, filtered, Person.createPersonsList("A", "A"), 1, 1)
        lo.checkAddRemove(2, filtered, Person.createPersonsList("A"), 2, 2)
        assertEquals(Person.createPersonsList("BB", "BC"), filtered)
    }

    private class Updater<E>(list: MutableList<E>) : ObservableListWrapper<E>(list) {

        fun update(from: Int, to: Int) {
            beginChange()
            for (i in from until to) {
                nextUpdate(i)
            }
            endChange()
        }

        fun updateAll() {
            update(0, this.size)
        }

    }

    @Test
    fun testCustomMutableElements() {
        val list: Updater<Person> =
                Updater(Person.createPersonsFromNames("A0", "A1", "BB2", "B3", "A4", "A5", "BC6", "A7", "C8"))

        val filtered: FilteredList<Person> = FilteredList(list) {p: Person -> p.name.get()!!.length > 2}
        val lo: MockListObserver<Person> = MockListObserver()
        filtered.addListener(lo)

        assertEquals(Person.createPersonsList("BB2", "BC6"), filtered)

        list.updateAll()
        lo.checkUpdate(0, filtered, 0, filtered.size)

        lo.clear()
        list[0].name.set("AA0")
        list[3].name.set("BB3")
        list[5].name.set("AA5")
        list[6].name.set("B6")
        list[7].name.set("AA7")
        list.updateAll()
        assertEquals(Person.createPersonsList("AA0", "BB2", "BB3", "AA5", "AA7"), filtered)
        lo.checkAddRemove(0, filtered, listOf(), 0, 1)
        lo.checkAddRemove(1, filtered, Person.createPersonsList("B6"), 2, 5)
        lo.checkUpdate(2, filtered, 1, 2)
    }

    @Test
    fun testNullPredicate() {
        this.filteredList.predicate = null
        assertEquals(this.list.size, this.filteredList.size)
        assertEquals(this.list, this.filteredList)
        this.mlo.check1AddRemove(this.filteredList, listOf("a", "d"), 0, 4)
    }

    @Test
    fun testSingleArgConstructor() {
        this.filteredList = FilteredList(this.list)
        assertEquals(this.list.size, this.filteredList.size)
        assertEquals(this.list, this.filteredList)
    }

}