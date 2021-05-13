package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.MockSetObserver.Call.Companion.call
import io.github.vinccool96.observationskt.collections.MockSetObserver.Tuple.Companion.tup
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change
import io.github.vinccool96.observationskt.collections.TestedObservableSets.CallableTreeSetImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
@Suppress("MemberVisibilityCanBePrivate")
class ObservableSetTest(val setFactory: Callable<ObservableSet<String?>>) {

    private lateinit var observableSet: ObservableSet<String?>

    private lateinit var observer: MockSetObserver<String?>

    @Before
    fun setUp() {
        this.observableSet = this.setFactory.call()
        this.observer = MockSetObserver()
        this.observableSet.addListener(this.observer)

        useData("one", "two", "foo")
    }

    /**
     * Modifies the set in the fixture to use the strings passed in instead of the default strings, and re-creates the
     * observable set and the observer. If no strings are passed in, the result is an empty set.
     *
     * @param strings the strings to use for the list in the fixture
     */
    fun useData(vararg strings: String) {
        this.observableSet.clear()
        this.observableSet.addAll(strings.asList())
        this.observer.clear()
    }

    @Test
    fun testAddRemove() {
        this.observableSet.add("observedFoo")
        this.observableSet.add("foo")
        assertTrue(this.observableSet.contains("observedFoo"))

        this.observableSet.remove("observedFoo")
        this.observableSet.remove("foo")
        this.observableSet.remove("bar")
        this.observableSet.add("one")

        assertFalse(this.observableSet.contains("foo"))

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
        this.observableSet.addAll(set)

        assertTrue(this.observableSet.contains("oFoo"))
        this.observer.assertMultipleCalls(call(null, "oFoo"), call(null, "pFoo"))
    }

    @Test
    fun testRemoveAll() {
        this.observableSet.removeAll(listOf("one", "two", "three"))

        this.observer.assertMultipleRemoved(tup("one"), tup("two"))
        assertEquals(1, this.observableSet.size)
    }

    @Test
    fun testClear() {
        this.observableSet.clear()

        assertTrue(this.observableSet.isEmpty())
        this.observer.assertMultipleRemoved(tup("one"), tup("two"), tup("foo"))
    }

    @Test
    fun testRetainAll() {
        this.observableSet.retainAll(listOf("one", "two", "three"))

        this.observer.assertRemoved(tup("foo"))
        assertEquals(2, this.observableSet.size)
    }

    @Test
    fun testIterator() {
        val iterator: MutableIterator<String?> = this.observableSet.iterator()
        assertTrue(iterator.hasNext())

        val toBeRemoved = iterator.next()
        iterator.remove()

        assertEquals(2, this.observableSet.size)
        this.observer.assertRemoved(tup(toBeRemoved))
    }

    @Test
    fun testOther() {
        assertEquals(3, this.observableSet.size)
        assertFalse(this.observableSet.isEmpty())

        assertTrue(this.observableSet.contains("foo"))
        assertFalse(this.observableSet.contains("bar"))
    }

    @Test
    fun testNull() {
        if (this.setFactory is CallableTreeSetImpl) {
            return // TreeSet doesn't accept nulls
        }
        this.observableSet.add(null)
        assertEquals(4, this.observableSet.size)
        this.observer.assertAdded(tup(null))

        this.observableSet.remove(null)
        assertEquals(3, this.observableSet.size)
        this.observer.assertRemoved(tup(null))
    }

    @Test
    fun testObserverCanRemoveObservers() {
        val setObserver: SetChangeListener<String?> = SetChangeListener { change ->
            change.set.removeListener(this.observer)
        }
        this.observableSet.addListener(setObserver)
        this.observableSet.add("x")
        this.observer.clear()
        this.observableSet.add("y")
        this.observer.check0()
        this.observableSet.removeListener(setObserver)

        val listener = StringSetChangeListener()
        this.observableSet.addListener(listener)
        this.observableSet.add("z")
        assertEquals(1, listener.counter)
        this.observableSet.add("zz")
        assertEquals(1, listener.counter)
    }

    @Test
    @Suppress("ReplaceAssertBooleanWithAssertEquality")
    fun testEqualsAndHashCode() {
        val other: Set<String?> = HashSet(listOf("one", "two", "foo"))
        assertTrue(this.observableSet == other)
        assertEquals(other.hashCode(), this.observableSet.hashCode())
    }

    private class StringSetChangeListener : SetChangeListener<String?> {

        var counter: Int = 0

        override fun onChanged(change: Change<out String?>) {
            change.set.removeListener(this)
            this.counter++
        }

    }

    companion object {

        @Parameterized.Parameters
        @JvmStatic
        fun setUpClass(): List<Array<out Any?>> {
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