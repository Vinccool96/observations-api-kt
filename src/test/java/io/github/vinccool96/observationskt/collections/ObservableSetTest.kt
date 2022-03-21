package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.MockSetObserver.Call.Companion.call
import io.github.vinccool96.observationskt.collections.MockSetObserver.Tuple.Companion.tup
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change
import io.github.vinccool96.observationskt.collections.TestedObservableSets.CallableTreeSetImpl
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.test.*

@RunWith(Parameterized::class)
@Suppress("MemberVisibilityCanBePrivate")
class ObservableSetTest(val setFactory: Callable<ObservableSet<String?>>) {

    private lateinit var set: ObservableSet<String?>

    private lateinit var observer: MockSetObserver<String?>

    @BeforeTest
    fun setUp() {
        this.set = this.setFactory.call()
        this.observer = MockSetObserver()
        this.set.addListener(this.observer)

        useData("one", "two", "foo")
    }

    /**
     * Modifies the set in the fixture to use the strings passed in instead of the default strings, and re-creates the
     * observable set and the observer. If no strings are passed in, the result is an empty set.
     *
     * @param strings the strings to use for the list in the fixture
     */
    fun useData(vararg strings: String) {
        this.set.clear()
        this.set.addAll(strings.asList())
        this.observer.clear()
    }

    @Test
    fun testAddRemove() {
        this.set.add("observedFoo")
        this.set.add("foo")
        assertTrue(this.set.contains("observedFoo"))

        this.set.remove("observedFoo")
        this.set.remove("foo")
        this.set.remove("bar")
        this.set.add("one")

        assertFalse(this.set.contains("foo"))

        this.observer.assertAdded(0, tup("observedFoo"))
        this.observer.assertRemoved(1, tup("observedFoo"))
        this.observer.assertRemoved(2, tup("foo"))

        assertEquals(3, this.observer.callsNumber)
    }

    @Test
    fun testAddAll() {
        val set: MutableSet<String> = HashSet()
        set.add("oFoo")
        set.add("pFoo")
        set.add("foo")
        set.add("one")
        this.set.addAll(set)

        assertTrue(this.set.contains("oFoo"))
        this.observer.assertMultipleCalls(call(null, "oFoo"), call(null, "pFoo"))
    }

    @Test
    fun testSetAll_Collection() {
        this.set.setAll(listOf("three", "bar"))
        this.observer.assertMultipleCalls(call("one", null), call("two", null), call("foo", null), call(null, "three"),
                call(null, "bar"))
    }

    @Test
    fun testSetAll_Elements() {
        this.set.setAll("three", "bar")
        this.observer.assertMultipleCalls(call("one", null), call("two", null), call("foo", null), call(null, "three"),
                call(null, "bar"))
    }

    @Test
    fun testSetAll_ElementsAlreadyIn() {
        this.set.setAll("one", "bar")
        this.observer.assertMultipleCalls(call("two", null), call("foo", null), call(null, "bar"))
    }

    @Test
    fun testRemoveAll() {
        this.set.removeAll(listOf("one", "two", "three"))

        this.observer.assertMultipleRemoved(tup("one"), tup("two"))
        assertEquals(1, this.set.size)
    }

    @Test
    fun testClear() {
        this.set.clear()

        assertTrue(this.set.isEmpty())
        this.observer.assertMultipleRemoved(tup("one"), tup("two"), tup("foo"))
    }

    @Test
    fun testRetainAll() {
        this.set.retainAll(listOf("one", "two", "three"))

        this.observer.assertRemoved(tup("foo"))
        assertEquals(2, this.set.size)
    }

    @Test
    fun testIterator() {
        val iterator: MutableIterator<String?> = this.set.iterator()
        assertTrue(iterator.hasNext())

        val toBeRemoved = iterator.next()
        iterator.remove()

        assertEquals(2, this.set.size)
        this.observer.assertRemoved(tup(toBeRemoved))
    }

    @Test
    fun testOther() {
        assertEquals(3, this.set.size)
        assertFalse(this.set.isEmpty())

        assertTrue(this.set.contains("foo"))
        assertFalse(this.set.contains("bar"))
    }

    @Test
    fun testNull() {
        if (this.setFactory is CallableTreeSetImpl) {
            return // TreeSet doesn't accept nulls
        }
        this.set.add(null)
        assertEquals(4, this.set.size)
        this.observer.assertAdded(tup(null))

        this.set.remove(null)
        assertEquals(3, this.set.size)
        this.observer.assertRemoved(tup(null))
    }

    @Test
    fun testObserverCanRemoveObservers() {
        val setObserver: SetChangeListener<String?> = SetChangeListener { change ->
            change.set.removeListener(this.observer)
        }
        this.set.addListener(setObserver)
        this.set.add("x")
        this.observer.clear()
        this.set.add("y")
        this.observer.check0()
        this.set.removeListener(setObserver)

        val listener = StringSetChangeListener()
        this.set.addListener(listener)
        this.set.add("z")
        assertEquals(1, listener.counter)
        this.set.add("zz")
        assertEquals(1, listener.counter)
    }

    @Test
    @Suppress("ReplaceAssertBooleanWithAssertEquality")
    fun testEqualsAndHashCode() {
        val other: Set<String?> = HashSet(listOf("one", "two", "foo"))
        assertTrue(this.set == other)
        assertEquals(other.hashCode(), this.set.hashCode())
    }

    private class StringSetChangeListener : SetChangeListener<String?> {

        var counter: Int = 0

        override fun onChanged(change: Change<out String?>) {
            change.set.removeListener(this)
            this.counter++
        }

    }

    companion object {

        @Parameters
        @JvmStatic
        fun createParameters(): List<Array<out Any?>> {
            return listOf(
                    arrayOf(TestedObservableSets.HASH_SET),
                    arrayOf(TestedObservableSets.TREE_SET),
                    arrayOf(TestedObservableSets.LINKED_HASH_SET),
                    arrayOf(TestedObservableSets.CHECKED_OBSERVABLE_HASH_SET),
                    arrayOf(TestedObservableSets.SYNCHRONIZED_OBSERVABLE_HASH_SET),
                    arrayOf(TestedObservableSets.OBSERVABLE_SET_PROPERTY),
            )
        }

    }

}