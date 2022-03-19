package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.sun.collections.ElementObservableListDecorator
import io.github.vinccool96.observationskt.util.Callback
import kotlin.test.BeforeTest
import kotlin.test.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class ObservableListWithExtractorTest(private val mode: Mode) {

    private lateinit var modifiedList: ObservableList<Person>

    private lateinit var observedList: ObservableList<Person>

    private lateinit var obs: MockListObserver<Person>

    private lateinit var p0: Person

    @BeforeTest
    fun setUp() {
        this.p0 = Person()
        this.obs = MockListObserver()
        val callback = Callback { param: Person -> arrayOf<Observable>(param.name) }
        if (this.mode == Mode.OBSERVABLE_LIST_WRAPPER) {
            ObservableCollections.observableArrayList(callback).let {
                this.modifiedList = it
                this.observedList = it
            }
        } else {
            this.modifiedList = ObservableCollections.observableArrayList()
            this.observedList = ElementObservableListDecorator(this.modifiedList, callback)
        }

        this.modifiedList.add(this.p0)
        this.observedList.addListener(this.obs)
    }

    private fun updateP0() {
        this.p0.name.set("bar")
    }

    @Test
    fun testUpdate_add() {
        updateP0()
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testUpdate_add1() {
        this.modifiedList.clear()
        this.modifiedList.add(0, this.p0)
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testUpdate_addAll() {
        this.modifiedList.clear()
        this.modifiedList.addAll(listOf(this.p0, this.p0))
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 2)
    }

    @Test
    fun testUpdate_addAll1() {
        this.modifiedList.clear()
        this.modifiedList.addAll(0, listOf(this.p0, this.p0))
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 2)
    }

    @Test
    fun testUpdate_addAll2() {
        this.modifiedList.clear()
        this.modifiedList.addAll(this.p0, this.p0)
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 2)
    }

    @Test
    fun testUpdate_set() {
        val p1 = Person()
        this.modifiedList[0] = p1
        this.obs.clear()
        updateP0()
        this.obs.check0()
        p1.name.set("bar")
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testUpdate_setAll() {
        val p1 = Person()
        this.modifiedList.setAll(p1)
        this.obs.clear()
        updateP0()
        this.obs.check0()
        p1.name.set("bar")
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testUpdate_remove() {
        this.modifiedList.remove(this.p0)
        this.obs.clear()
        updateP0()
        this.obs.check0()
    }

    @Test
    fun testUpdate_removeAt() {
        this.modifiedList.removeAt(0)
        this.obs.clear()
        updateP0()
        this.obs.check0()
    }

    @Test
    fun testUpdate_removeAll() {
        this.modifiedList.removeAll(this.p0)
        this.obs.clear()
        updateP0()
        this.obs.check0()
    }

    @Test
    fun testUpdate_retainAll() {
        this.modifiedList.retainAll()
        this.obs.clear()
        updateP0()
        this.obs.check0()
    }

    @Test
    fun testUpdate_iterator_add() {
        this.modifiedList.clear()
        this.modifiedList.listIterator().add(this.p0)
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testUpdate_iterator_set() {
        val p1 = Person()
        val listIterator = this.modifiedList.listIterator()
        listIterator.next()
        listIterator.set(p1)
        this.obs.clear()
        updateP0()
        this.obs.check0()
        p1.name.set("bar")
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testUpdate_sublist_add() {
        val sublist = this.modifiedList.subList(0, 1)
        sublist.add(this.p0)
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 2)
    }

    @Test
    fun testUpdate_sublist_add1() {
        val sublist = this.modifiedList.subList(0, 1)
        sublist.clear()
        sublist.add(0, this.p0)
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testUpdate_sublist_addAll() {
        val sublist = this.modifiedList.subList(0, 1)
        sublist.clear()
        sublist.addAll(listOf(this.p0, this.p0))
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 2)
    }

    @Test
    fun testUpdate_sublist_addAll1() {
        val sublist = this.modifiedList.subList(0, 1)
        sublist.clear()
        sublist.addAll(0, listOf(this.p0, this.p0))
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 2)
    }

    @Test
    fun testUpdate_sublist_set() {
        val sublist = this.modifiedList.subList(0, 1)
        val p1 = Person()
        sublist[0] = p1
        this.obs.clear()
        updateP0()
        this.obs.check0()
        p1.name.set("bar")
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testUpdate_sublist_remove() {
        val sublist = this.modifiedList.subList(0, 1)
        sublist.remove(this.p0)
        this.obs.clear()
        updateP0()
        this.obs.check0()
    }

    @Test
    fun testUpdate_sublist_removeAt() {
        val sublist = this.modifiedList.subList(0, 1)
        sublist.removeAt(0)
        this.obs.clear()
        updateP0()
        this.obs.check0()
    }

    @Test
    fun testUpdate_sublist_removeAll() {
        val sublist = this.modifiedList.subList(0, 1)
        sublist.removeAll(listOf(this.p0))
        this.obs.clear()
        updateP0()
        this.obs.check0()
    }

    @Test
    fun testUpdate_sublist_retainAll() {
        val sublist = this.modifiedList.subList(0, 1)
        sublist.retainAll(listOf())
        this.obs.clear()
        updateP0()
        this.obs.check0()
    }

    @Test
    fun testUpdate_iterator_sublist_add() {
        val sublist = this.modifiedList.subList(0, 1)
        sublist.clear()
        sublist.listIterator().add(this.p0)
        this.obs.clear()
        updateP0()
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testUpdate_iterator_sublist_set() {
        val sublist = this.modifiedList.subList(0, 1)
        val p1 = Person()
        val listIterator = sublist.listIterator()
        listIterator.next()
        listIterator.set(p1)
        this.obs.clear()
        updateP0()
        this.obs.check0()
        p1.name.set("bar")
        this.obs.check1Update(this.observedList, 0, 1)
    }

    @Test
    fun testMultipleUpdate() {
        this.modifiedList.add(Person())
        this.modifiedList.addAll(this.p0, this.p0)

        this.obs.clear()

        updateP0()
        this.obs.checkUpdate(0, this.observedList, 0, 1)
        this.obs.checkUpdate(1, this.observedList, 2, 4)
        assertEquals(2, this.obs.calls.size)
    }

    @Test
    fun testPreFilledList() {
        val arrayList = ArrayList<Person>()
        arrayList.add(this.p0)
        this.obs = MockListObserver()
        val callback = Callback { param: Person -> arrayOf<Observable>(param.name) }
        if (this.mode == Mode.OBSERVABLE_LIST_WRAPPER) {
            ObservableCollections.observableList(arrayList, callback).let {
                this.modifiedList = it
                this.observedList = it
            }
        } else {
            this.modifiedList = ObservableCollections.observableArrayList(arrayList)
            this.observedList = ElementObservableListDecorator(this.modifiedList, callback)
        }

        this.observedList.addListener(this.obs)

        updateP0()

        this.obs.check1Update(this.observedList, 0, 1)
    }

    enum class Mode {

        OBSERVABLE_LIST_WRAPPER,

        DECORATOR

    }

    companion object {

        @Parameters
        @JvmStatic
        fun createParameters(): List<Array<out Any?>> {
            return listOf(arrayOf(Mode.OBSERVABLE_LIST_WRAPPER), arrayOf(Mode.DECORATOR))
        }

    }

}