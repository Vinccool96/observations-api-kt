package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.MapChangeListener.Change
import io.github.vinccool96.observationskt.collections.MockMapObserver.Call.Companion.call
import io.github.vinccool96.observationskt.collections.MockMapObserver.Tuple.Companion.tup
import io.github.vinccool96.observationskt.collections.TestedObservableMaps.CallableConcurrentHashMapImpl
import io.github.vinccool96.observationskt.collections.TestedObservableMaps.CallableTreeMapImpl
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.collections.MutableMap.MutableEntry
import kotlin.test.*

@RunWith(Parameterized::class)
class ObservableMapTest(private val mapFactory: Callable<ObservableMap<String?, String?>>) {

    private lateinit var map: ObservableMap<String?, String?>

    private lateinit var observer: MockMapObserver<String?, String?>

    @BeforeTest
    fun setUp() {
        this.map = this.mapFactory.call()
        this.observer = MockMapObserver()
        this.map.addListener(this.observer)

        this.map.clear()
        this.map["one"] = "1"
        this.map["two"] = "2"
        this.map["foo"] = "bar"
        this.observer.clear()
    }

    @Test
    fun testPutRemove() {
        this.map["observedFoo"] = "barVal"
        this.map["foo"] = "barfoo"
        assertEquals("barVal", this.map["observedFoo"])

        this.map.remove("observedFoo")
        this.map.remove("foo")
        this.map.remove("bar")
        this.map["one"] = "1"

        assertFalse(this.map.containsKey("foo"))

        this.observer.assertAdded(0, tup("observedFoo", "barVal"))
        this.observer.assertAdded(1, tup("foo", "barfoo"))
        this.observer.assertRemoved(1, tup("foo", "bar"))
        this.observer.assertRemoved(2, tup("observedFoo", "barVal"))
        this.observer.assertRemoved(3, tup("foo", "barfoo"))

        assertEquals(4, this.observer.callsNumber)
    }

    @Test
    fun testPutRemove_Null() {
        if (this.mapFactory is CallableConcurrentHashMapImpl) {
            return // Do not perform on ConcurrentHashMap, as it doesn't accept nulls
        }
        this.map.clear()
        this.observer.clear()

        this.map["bar"] = null
        this.map["foo"] = "x"
        this.map["bar"] = "x"
        this.map["foo"] = null

        assertEquals(2, this.map.size)

        this.map.remove("bar")
        this.map.remove("foo")

        assertEquals(0, this.map.size)

        this.observer.assertAdded(0, tup("bar", null))
        this.observer.assertAdded(1, tup("foo", "x"))
        this.observer.assertAdded(2, tup("bar", "x"))
        this.observer.assertRemoved(2, tup("bar", null))
        this.observer.assertAdded(3, tup("foo", null))
        this.observer.assertRemoved(3, tup("foo", "x"))
        this.observer.assertRemoved(4, tup("bar", "x"))
        this.observer.assertRemoved(5, tup("foo", null))

        assertEquals(6, this.observer.callsNumber)
    }

    @Test
    fun testPutRemove_NullKey() {
        if (this.mapFactory is CallableConcurrentHashMapImpl || this.mapFactory is CallableTreeMapImpl) {
            return // Do not perform on ConcurrentHashMap or TreeMap, as they don't accept null keys
        }

        this.map[null] = "abc"

        assertEquals(4, this.map.size)

        this.map.remove(null)

        assertEquals(3, this.map.size)

        this.observer.assertAdded(0, tup(null, "abc"))
        this.observer.assertRemoved(1, tup(null, "abc"))

        assertEquals(2, this.observer.callsNumber)
    }

    @Test
    fun testPutAll() {
        val map = hashMapOf("oFoo" to "OFoo", "pFoo" to "PFoo", "foo" to "foofoo", "one" to "1")
        this.map.putAll(map)

        assertTrue(this.map.containsKey("oFoo"))
        this.observer.assertMultipleCalls(call("oFoo", null, "OFoo"), call("pFoo", null, "PFoo"),
                call("foo", "bar", "foofoo"))
    }

    @Test
    fun testSetAll_Pairs() {
        this.map.setAll("bar" to "Bar", "toto" to "Toto")
        this.observer.assertMultipleCalls(call("one", "1", null), call("two", "2", null), call("foo", "bar", null),
                call("bar", null, "Bar"), call("toto", null, "Toto"))
    }

    @Test
    fun testSetAll_Map() {
        val map = hashMapOf("bar" to "Bar", "toto" to "Toto")
        this.map.setAll(map)
        this.observer.assertMultipleCalls(call("one", "1", null), call("two", "2", null), call("foo", "bar", null),
                call("bar", null, "Bar"), call("toto", null, "Toto"))
    }

    @Test
    fun testSetAll_PairsAlreadyIn() {
        this.map.setAll("two" to "Two", "bar" to "Bar", "foo" to "bar")
        this.observer.assertMultipleCalls(call("one", "1", null), call("two", "2", "Two"), call("bar", null, "Bar"))
    }

    @Test
    fun testClear() {
        this.map.clear()

        assertTrue(this.map.isEmpty())
        this.observer.assertMultipleRemoved(tup("one", "1"), tup("two", "2"), tup("foo", "bar"))
    }

    @Test
    fun testOther() {
        assertEquals(3, this.map.size)
        assertFalse(this.map.isEmpty())

        assertTrue(this.map.containsKey("foo"))
        assertFalse(this.map.containsKey("bar"))

        assertFalse(this.map.containsValue("foo"))
        assertTrue(this.map.containsValue("bar"))
    }

    @Test
    fun testKeys_Remove() {
        this.map.keys.remove("one")
        this.map.keys.remove("two")
        this.map.keys.remove("three")

        this.observer.assertRemoved(0, tup("one", "1"))
        this.observer.assertRemoved(1, tup("two", "2"))
        assertEquals(2, this.observer.callsNumber)
    }

    @Test
    fun testKeys_RemoveAll() {
        this.map.keys.removeAll(listOf("one", "two", "three"))

        this.observer.assertMultipleRemoved(tup("one", "1"), tup("two", "2"))
        assertEquals(1, this.map.size)
    }

    @Test
    fun testKeys_RetainAll() {
        this.map.keys.retainAll(listOf("one", "two", "three"))

        this.observer.assertRemoved(tup("foo", "bar"))
        assertEquals(2, this.map.size)
    }

    @Test
    fun testKeys_Clear() {
        this.map.keys.clear()
        assertTrue(this.map.isEmpty())
        this.observer.assertMultipleRemoved(tup("one", "1"), tup("two", "2"), tup("foo", "bar"))
    }

    @Test
    fun testKeys_Iterator() {
        val iterator = this.map.keys.iterator()
        assertTrue(iterator.hasNext())

        val toBeRemoved = iterator.next()
        val toBeRemovedVal = this.map[toBeRemoved]
        iterator.remove()

        assertEquals(2, this.map.size)
        this.observer.assertRemoved(tup(toBeRemoved, toBeRemovedVal))
    }

    @Test
    fun testKeys_Other() {
        assertEquals(3, this.map.keys.size)
        assertTrue(this.map.keys.contains("foo"))
        assertFalse(this.map.keys.contains("bar"))

        assertTrue(this.map.keys.containsAll(listOf("one", "two")))
        assertFalse(this.map.keys.containsAll(listOf("one", "three")))

        assertEquals(3, this.map.keys.toTypedArray().size)
    }

    @Test
    fun testValues_Remove() {
        this.map.values.remove("1")
        this.map.values.remove("2")
        this.map.values.remove("3")

        this.observer.assertRemoved(0, tup("one", "1"))
        this.observer.assertRemoved(1, tup("two", "2"))
        assertEquals(2, this.observer.callsNumber)
    }

    @Test
    fun testValues_RemoveAll() {
        this.map.values.removeAll(listOf("1", "2", "3"))

        this.observer.assertMultipleRemoved(tup("one", "1"), tup("two", "2"))
        assertEquals(1, this.map.size)
    }

    @Test
    fun testValues_RetainAll() {
        this.map.values.retainAll(listOf("1", "2", "3"))

        this.observer.assertRemoved(tup("foo", "bar"))
        assertEquals(2, this.map.size)
    }

    @Test
    fun testValues_Clear() {
        this.map.values.clear()
        assertTrue(this.map.isEmpty())
        this.observer.assertMultipleRemoved(tup("one", "1"), tup("two", "2"), tup("foo", "bar"))
    }

    @Test
    fun testValues_Iterator() {
        val iterator = this.map.values.iterator()
        assertTrue(iterator.hasNext())

        val toBeRemovedVal = iterator.next()
        iterator.remove()

        assertEquals(2, this.map.size)
        this.observer.assertRemoved(tup(
                when (toBeRemovedVal) {
                    "1" -> "one"
                    "2" -> "two"
                    "bar" -> "foo"
                    else -> null
                }, toBeRemovedVal))
    }

    @Test
    fun testValues_Other() {
        assertEquals(3, this.map.values.size)
        assertFalse(this.map.values.contains("foo"))
        assertTrue(this.map.values.contains("bar"))

        assertTrue(this.map.values.containsAll(listOf("1", "2")))
        assertFalse(this.map.values.containsAll(listOf("1", "3")))

        assertEquals(3, this.map.keys.toTypedArray().size)
    }

    @Test
    fun testEntries_Remove() {
        this.map.entries.remove(entry("one", "1"))
        this.map.entries.remove(entry("two", "2"))
        this.map.entries.remove(entry("three", "3"))

        this.observer.assertRemoved(0, tup("one", "1"))
        this.observer.assertRemoved(1, tup("two", "2"))
        assertEquals(2, this.observer.callsNumber)
    }

    @Test
    fun testEntries_RemoveAll() {
        this.map.entries.removeAll(setOf(entry("one", "1"), entry("two", "2"), entry("three", "3")))

        this.observer.assertMultipleRemoved(tup("one", "1"), tup("two", "2"))
        assertEquals(1, this.map.size)
    }

    @Test
    fun testEntries_RetainAll() {
        this.map.entries.retainAll(setOf(entry("one", "1"), entry("two", "2"), entry("three", "3")))

        this.observer.assertRemoved(tup("foo", "bar"))
        assertEquals(2, this.map.size)
    }

    @Test
    fun testEntries_Clear() {
        this.map.entries.clear()
        assertTrue(this.map.isEmpty())
        this.observer.assertMultipleRemoved(tup("one", "1"), tup("two", "2"), tup("foo", "bar"))
    }

    @Test
    fun testEntries_Iterator() {
        val iterator = this.map.entries.iterator()
        assertTrue(iterator.hasNext())

        val toBeRemoved = iterator.next()
        val toBeRemovedKey = toBeRemoved.key
        val toBeRemovedVal = toBeRemoved.value
        iterator.remove()

        assertEquals(2, this.map.size)
        this.observer.assertRemoved(tup(toBeRemovedKey, toBeRemovedVal))
    }

    @Test
    fun testEntries_Other() {
        assertEquals(3, this.map.entries.size)
        assertTrue(this.map.entries.contains(entry("foo", "bar")))
        assertFalse(this.map.entries.contains(entry("bar", "foo")))

        assertTrue(this.map.entries.containsAll(listOf(entry("one", "1"), entry("two", "2"))))
        assertFalse(this.map.entries.containsAll(listOf(entry("one", "1"), entry("three", "3"))))

        assertEquals(3, this.map.entries.toTypedArray().size)
    }

    @Test
    fun testObserverCanRemoveObservers() {
        val mapObserver = MapChangeListener<String?, String?> { change ->
            change.map.removeListener(this.observer)
        }
        this.map.addListener(mapObserver)
        this.map["x"] = "x"
        this.observer.clear()
        this.map["y"] = "y"
        this.observer.check0()

        val listener = StringMapChangeListener()
        this.map.addListener(listener)
        this.map["z"] = "z"
        assertEquals(1, listener.counter)
        this.map["zz"] = "zz"
        assertEquals(1, listener.counter)
    }

    @Test
    fun testEqualsAndHashCode() {
        val other = HashMap(this.map)
        assertTrue(this.map == other)
        assertEquals(other.hashCode(), this.map.hashCode())
    }

    private fun <K, V> entry(key: K, value: V): MutableEntry<K, V> {
        return object : MutableEntry<K, V> {

            override val key: K = key

            override val value: V = value

            override fun setValue(newValue: V): V {
                throw UnsupportedOperationException("Not supported.")
            }

            override fun equals(other: Any?): Boolean {
                return if (other == null || other !is MutableEntry<*, *>) false
                else this.key == other.key && this.value == other.value
            }

            override fun hashCode(): Int {
                return (this.key?.hashCode() ?: 0) xor (this.value?.hashCode() ?: 0)
            }

        }
    }

    private class StringMapChangeListener : MapChangeListener<String?, String?> {

        var counter: Int = 0

        override fun onChanged(change: Change<out String?, out String?>) {
            change.map.removeListener(this)
            this.counter++
        }

    }

    companion object {

        @Parameters
        @JvmStatic
        fun createParameters(): List<Array<out Any?>> {
            return listOf(
                    arrayOf(TestedObservableMaps.HASH_MAP),
                    arrayOf(TestedObservableMaps.TREE_MAP),
                    arrayOf(TestedObservableMaps.LINKED_HASH_MAP),
                    arrayOf(TestedObservableMaps.CONCURRENT_HASH_MAP),
                    arrayOf(TestedObservableMaps.CHECKED_OBSERVABLE_HASH_MAP),
                    arrayOf(TestedObservableMaps.SYNCHRONIZED_OBSERVABLE_HASH_MAP),
                    arrayOf(TestedObservableMaps.OBSERVABLE_MAP_PROPERTY),
            )
        }

    }

}