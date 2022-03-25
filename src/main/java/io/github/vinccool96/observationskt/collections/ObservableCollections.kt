package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.collections.ObservableCollections.emptyObservableList
import io.github.vinccool96.observationskt.collections.ObservableCollections.synchronizedObservableList
import io.github.vinccool96.observationskt.sun.collections.*
import io.github.vinccool96.observationskt.util.Callback
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Utility object that consists of methods that are 1:1 copies of [Collections] methods.
 *
 * The wrapper methods (like [synchronizedObservableList] or [emptyObservableList]) has exactly the same functionality
 * as the methods in `Collections`, with exception that they return [ObservableList] and are therefore suitable for
 * methods that require `ObservableList` on input.
 *
 * The utility methods are here mainly for performance reasons. All methods are optimized in a way that they yield only
 * limited number of notifications. On the other hand, `Collections` methods might call "modification methods" on an
 * `ObservableList` multiple times, resulting in a number of notifications.
 */
@Suppress("SuspiciousEqualsCombination")
object ObservableCollections {

    private fun <E> singletonIterator(e: E): MutableIterator<E> {
        return object : MutableIterator<E> {

            private var hasNext: Boolean = true

            override fun hasNext(): Boolean {
                return this.hasNext
            }

            override fun next(): E {
                if (this.hasNext) {
                    this.hasNext = false
                    return e
                }
                throw NoSuchElementException()
            }

            override fun remove() {
                throw UnsupportedOperationException()
            }

        }
    }

    /**
     * Constructs an ObservableList that is backed by the specified list. Mutation operations on the ObservableList
     * instance will be reported to observers that have registered on that instance.
     *
     * Note that mutation operations made directly to the underlying list are *not* reported to observers of any
     * ObservableList that wraps it.
     *
     * @param E the list element type
     * @param list a concrete MutableList that backs this ObservableList
     *
     * @return a newly created ObservableList
     */
    fun <E> observableList(list: MutableList<E>): ObservableList<E> {
        return if (list is RandomAccess) ObservableListWrapper(list) else ObservableSequentialListWrapper(list)
    }

    /**
     * Constructs an ObservableList that is backed by the specified list. Mutation operations on the ObservableList
     * instance will be reported to observers that have registered on that instance.
     *
     * Note that mutation operations made directly to the underlying list are *not* reported to observers of any
     * ObservableList that wraps it.
     *
     * This list also reports mutations of the elements in it by using `extractor`. Observable objects returned by
     * `extractor` (applied to each list element) are listened for changes and transformed into "update" change of
     * ListChangeListener.
     *
     * @param E the list element type
     * @param list a concrete MutableList that backs this ObservableList
     * @param extractor element to Observable[] convertor
     *
     * @return a newly created ObservableList
     */
    fun <E> observableList(list: MutableList<E>, extractor: Callback<E, Array<Observable>>): ObservableList<E> {
        return if (list is RandomAccess) ObservableListWrapper(list, extractor)
        else ObservableSequentialListWrapper(list, extractor)
    }

    /**
     * Constructs an ObservableMap that is backed by the specified map. Mutation operations on the ObservableMap
     * instance will be reported to observers that have registered on that instance.
     *
     * Note that mutation operations made directly to the underlying map are *not* reported to observers of any
     * ObservableMap that wraps it.
     *
     * @param K the type of keys maintained by this map
     * @param V the type of mapped values
     * @param map a MutableMap that backs this ObservableMap
     *
     * @return a newly created ObservableMap
     */
    @Suppress("KotlinConstantConditions")
    fun <K, V> observableMap(map: Map<K, V>): ObservableMap<K, V> {
        return if (map is MutableMap<*, *>) {
            ObservableMapWrapper(map as MutableMap<K, V>)
        } else {
            val wrappedMap: MutableMap<K, V> = HashMap(map)
            ObservableMapWrapper(wrappedMap)
        }
    }

    /**
     * Constructs an ObservableSet that is backed by the specified set. Mutation operations on the ObservableSet
     * instance will be reported to observers that have registered on that instance.
     *
     * Note that mutation operations made directly to the underlying set are *not* reported to observers of any
     * ObservableSet that wraps it.
     *
     * @param E the type of elements maintained by this set
     * @param set a Set that backs this ObservableSet
     *
     * @return a newly created ObservableSet
     */
    fun <E> observableSet(set: Set<E>): ObservableSet<E> {
        return if (set is MutableSet<E>) {
            ObservableSetWrapper(set)
        } else {
            ObservableSetWrapper(set.toMutableSet())
        }
    }

    /**
     * Constructs an ObservableSet backed by a HashSet that contains all the specified elements.
     *
     * @param E the type of elements maintained by this set
     * @param elements elements that will be added into returned ObservableSet
     *
     * @return a newly created ObservableSet
     */
    fun <E> observableSet(vararg elements: E): ObservableSet<E> {
        return ObservableSetWrapper(hashSetOf(*elements))
    }

    /**
     * Constructs a read-only interface to the specified ObservableMap. Only mutation operations made to the underlying
     * ObservableMap will be reported to observers that have registered on the unmodifiable instance. This allows
     * clients to track changes in a Map but disallows the ability to modify it.
     *
     * @param K the type of keys maintained by this map
     * @param V the type of mapped values
     * @param map an ObservableMap that is to be monitored by this interface
     *
     * @return a newly created UnmodifiableObservableMap
     */
    fun <K, V> unmodifiableObservableMap(map: ObservableMap<K, V>): ObservableMap<K, V> {
        return UnmodifiableObservableMap(map)
    }

    /**
     * Creates and returns a typesafe wrapper on top of provided observable map.
     *
     * @param K the type of keys maintained by this map
     * @param V the type of mapped values
     * @param map an Observable map to be wrapped
     * @param keyType the type of key that `map` is permitted to hold
     * @param valueType the type of value that `map` is permitted to hold
     *
     * @return a dynamically typesafe view of the specified map
     *
     * @see Collections.checkedMap
     */
    fun <K, V> checkedObservableMap(map: ObservableMap<K, V>, keyType: KType, valueType: KType): ObservableMap<K, V> {
        return CheckedObservableMap(map, keyType, valueType)
    }

    /**
     * Creates and returns a synchronized wrapper on top of provided observable map.
     *
     * @param K the type of keys maintained by this map
     * @param V the type of mapped values
     * @param map the map to be "wrapped" in a synchronized map.
     *
     * @return A synchronized version of the observable map
     *
     * @see Collections.synchronizedMap
     */
    fun <K, V> synchronizedObservableMap(map: ObservableMap<K, V>): ObservableMap<K, V> {
        return SynchronizedObservableMap(map)
    }

    /**
     * Creates and empty unmodifiable observable map.
     *
     * @param K the type of keys maintained by this map
     * @param V the type of mapped values
     *
     * @return An empty unmodifiable observable map
     *
     * @see Collections.emptyMap
     */
    @ReturnsUnmodifiableCollection
    fun <K, V> emptyObservableMap(): ObservableMap<K, V> {
        return EmptyObservableMap()
    }

    /**
     * Returns an immutable map, mapping only the specified key to the specified value.
     *
     * @param K the class of the map keys
     * @param V the class of the map values
     * @param key the sole key to be stored in the returned map.
     * @param value the value to which the returned map maps `key`.
     *
     * @return an immutable map containing only the specified key-value mapping.
     */
    @ReturnsUnmodifiableCollection
    fun <K, V> singletonObservableMap(key: K, value: V): ObservableMap<K, V> {
        return SingletonObservableMap(key, value)
    }

    /**
     * Creates and returns unmodifiable wrapper array on top of provided observable array.
     *
     * @param T the type of the objects in the array
     * @param array an ObservableArray that is to be wrapped
     *
     * @return an ObservableArray wrapper that is unmodifiable
     */
    fun <T> unmodifiableObservableArray(array: ObservableArray<T>): ObservableArray<T> {
        return UnmodifiableObservableArrayImpl(array)
    }

    /**
     * Creates an empty unmodifiable observable array.
     *
     * @param T the type of the objects in the array
     *
     * @return An empty unmodifiable observable array
     */
    fun <T> emptyObservableArray(baseArray: Array<T>): ObservableArray<T> {
        return EmptyObservableArray(baseArray)
    }

    /**
     * Creates a new empty observable boolean array.
     *
     * @return a newly created ObservableBooleanArray
     */
    fun observableBooleanArray(): ObservableBooleanArray {
        return ObservableBooleanArrayImpl()
    }

    /**
     * Creates a new observable boolean array with `values` set to it.
     *
     * @param values the values that will be in the new observable boolean array
     *
     * @return a newly created ObservableBooleanArray
     */
    fun observableBooleanArray(vararg values: Boolean): ObservableBooleanArray {
        return ObservableBooleanArrayImpl(*values)
    }

    /**
     * Creates a new observable boolean array with copy of elements in given `array`.
     *
     * @param array observable boolean array to copy
     *
     * @return a newly created ObservableBooleanArray
     */
    fun observableBooleanArray(array: Array<Boolean>): ObservableBooleanArray {
        return ObservableBooleanArrayImpl(array)
    }

    /**
     * Creates a new observable boolean array with copy of elements in given `array`.
     *
     * @param array observable boolean array to copy
     *
     * @return a newly created ObservableBooleanArray
     */
    fun observableBooleanArray(array: ObservableBooleanArray): ObservableBooleanArray {
        return ObservableBooleanArrayImpl(array)
    }

    /**
     * Creates a new empty observable double array.
     *
     * @return a newly created ObservableDoubleArray
     */
    fun observableDoubleArray(): ObservableDoubleArray {
        return ObservableDoubleArrayImpl()
    }

    /**
     * Creates a new observable double array with `values` set to it.
     *
     * @param values the values that will be in the new observable double array
     *
     * @return a newly created ObservableDoubleArray
     */
    fun observableDoubleArray(vararg values: Double): ObservableDoubleArray {
        return ObservableDoubleArrayImpl(*values)
    }

    /**
     * Creates a new observable double array with copy of elements in given `array`.
     *
     * @param array observable double array to copy
     *
     * @return a newly created ObservableDoubleArray
     */
    fun observableDoubleArray(array: Array<Double>): ObservableDoubleArray {
        return ObservableDoubleArrayImpl(array)
    }

    /**
     * Creates a new observable double array with copy of elements in given `array`.
     *
     * @param array observable double array to copy
     *
     * @return a newly created ObservableDoubleArray
     */
    fun observableDoubleArray(array: ObservableDoubleArray): ObservableDoubleArray {
        return ObservableDoubleArrayImpl(array)
    }

    /**
     * Creates a new empty observable float array.
     *
     * @return a newly created ObservableFloatArray
     */
    fun observableFloatArray(): ObservableFloatArray {
        return ObservableFloatArrayImpl()
    }

    /**
     * Creates a new observable float array with `values` set to it.
     *
     * @param values the values that will be in the new observable float array
     *
     * @return a newly created ObservableFloatArray
     */
    fun observableFloatArray(vararg values: Float): ObservableFloatArray {
        return ObservableFloatArrayImpl(*values)
    }

    /**
     * Creates a new observable float array with copy of elements in given `array`.
     *
     * @param array observable float array to copy
     *
     * @return a newly created ObservableFloatArray
     */
    fun observableFloatArray(array: Array<Float>): ObservableFloatArray {
        return ObservableFloatArrayImpl(array)
    }

    /**
     * Creates a new observable float array with copy of elements in given `array`.
     *
     * @param array observable float array to copy
     *
     * @return a newly created ObservableFloatArray
     */
    fun observableFloatArray(array: ObservableFloatArray): ObservableFloatArray {
        return ObservableFloatArrayImpl(array)
    }

    /**
     * Creates a new empty observable int array.
     *
     * @return a newly created ObservableIntArray
     */
    fun observableIntArray(): ObservableIntArray {
        return ObservableIntArrayImpl()
    }

    /**
     * Creates a new observable int array with `values` set to it.
     *
     * @param values the values that will be in the new observable int array
     *
     * @return a newly created ObservableIntArray
     */
    fun observableIntArray(vararg values: Int): ObservableIntArray {
        return ObservableIntArrayImpl(*values)
    }

    /**
     * Creates a new observable int array with copy of elements in given `array`.
     *
     * @param array observable int array to copy
     *
     * @return a newly created ObservableIntArray
     */
    fun observableIntArray(array: Array<Int>): ObservableIntArray {
        return ObservableIntArrayImpl(array)
    }

    /**
     * Creates a new observable int array with copy of elements in given `array`.
     *
     * @param array observable int array to copy
     *
     * @return a newly created ObservableIntArray
     */
    fun observableIntArray(array: ObservableIntArray): ObservableIntArray {
        return ObservableIntArrayImpl(array)
    }

    /**
     * Creates a new empty observable long array.
     *
     * @return a newly created ObservableLongArray
     */
    fun observableLongArray(): ObservableLongArray {
        return ObservableLongArrayImpl()
    }

    /**
     * Creates a new observable long array with `values` set to it.
     *
     * @param values the values that will be in the new observable long array
     *
     * @return a newly created ObservableLongArray
     */
    fun observableLongArray(vararg values: Long): ObservableLongArray {
        return ObservableLongArrayImpl(*values)
    }

    /**
     * Creates a new observable long array with copy of elements in given `array`.
     *
     * @param array observable long array to copy
     *
     * @return a newly created ObservableLongArray
     */
    fun observableLongArray(array: Array<Long>): ObservableLongArray {
        return ObservableLongArrayImpl(array)
    }

    /**
     * Creates a new observable long array with copy of elements in given `array`.
     *
     * @param array observable long array to copy
     *
     * @return a newly created ObservableLongArray
     */
    fun observableLongArray(array: ObservableLongArray): ObservableLongArray {
        return ObservableLongArrayImpl(array)
    }

    /**
     * Creates a new empty observable short array.
     *
     * @return a newly created ObservableShortArray
     */
    fun observableShortArray(): ObservableShortArray {
        return ObservableShortArrayImpl()
    }

    /**
     * Creates a new observable short array with `values` set to it.
     *
     * @param values the values that will be in the new observable short array
     *
     * @return a newly created ObservableShortArray
     */
    fun observableShortArray(vararg values: Short): ObservableShortArray {
        return ObservableShortArrayImpl(*values)
    }

    /**
     * Creates a new observable short array with copy of elements in given `array`.
     *
     * @param array observable short array to copy
     *
     * @return a newly created ObservableShortArray
     */
    fun observableShortArray(array: Array<Short>): ObservableShortArray {
        return ObservableShortArrayImpl(array)
    }

    /**
     * Creates a new observable short array with copy of elements in given `array`.
     *
     * @param array observable short array to copy
     *
     * @return a newly created ObservableShortArray
     */
    fun observableShortArray(array: ObservableShortArray): ObservableShortArray {
        return ObservableShortArrayImpl(array)
    }

    /**
     * Creates a new empty observable byte array.
     *
     * @return a newly created ObservableByteArray
     */
    fun observableByteArray(): ObservableByteArray {
        return ObservableByteArrayImpl()
    }

    /**
     * Creates a new observable byte array with `values` set to it.
     *
     * @param values the values that will be in the new observable byte array
     *
     * @return a newly created ObservableByteArray
     */
    fun observableByteArray(vararg values: Byte): ObservableByteArray {
        return ObservableByteArrayImpl(*values)
    }

    /**
     * Creates a new observable byte array with copy of elements in given `array`.
     *
     * @param array observable byte array to copy
     *
     * @return a newly created ObservableByteArray
     */
    fun observableByteArray(array: Array<Byte>): ObservableByteArray {
        return ObservableByteArrayImpl(array)
    }

    /**
     * Creates a new observable byte array with copy of elements in given `array`.
     *
     * @param array observable byte array to copy
     *
     * @return a newly created ObservableByteArray
     */
    fun observableByteArray(array: ObservableByteArray): ObservableByteArray {
        return ObservableByteArrayImpl(array)
    }

    /**
     * Creates a new empty observable array.
     *
     * @param baseArray the base array of size `1` containing the base element
     * @param T the type of the objects in the array
     *
     * @return a newly created ObservableObjectArray
     *
     * @throws IllegalArgumentException if [baseArray] isn't of size `1`
     */
    fun <T> observableObjectArray(baseArray: Array<T>): ObservableObjectArray<T> {
        return ObservableObjectArrayImpl(baseArray)
    }

    /**
     * Creates a new observable array with `values` set to it.
     *
     * @param baseArray the base array of size `1` containing the base element
     * @param values the values that will be in the new observable array
     * @param T the type of the objects in the array
     *
     * @return a newly created ObservableObjectArray
     *
     * @throws IllegalArgumentException if [baseArray] isn't of size `1`
     */
    fun <T> observableObjectArray(baseArray: Array<T>, vararg values: T): ObservableObjectArray<T> {
        return ObservableObjectArrayImpl(baseArray, *values)
    }

    /**
     * Creates a new observable array with copy of elements in given `array`.
     *
     * @param array observable array to copy
     * @param T the type of the objects in the array
     *
     * @return a newly created ObservableObjectArray
     */
    fun <T> observableObjectArray(array: ObservableArray<T>): ObservableObjectArray<T> {
        return ObservableObjectArrayImpl(array.baseArray.copyOf(), array)
    }

    /**
     * Creates a new observable array with copy of elements in given `array`.
     *
     * @param baseArray the base array of size `1` containing the base element
     * @param array observable array to copy
     * @param T the type of the objects in the array
     *
     * @return a newly created ObservableObjectArray
     *
     * @throws IllegalArgumentException if [baseArray] isn't of size `1`
     */
    fun <T> observableObjectArray(baseArray: Array<T>, array: ObservableArray<T>): ObservableObjectArray<T> {
        return ObservableObjectArrayImpl(baseArray, array)
    }

    /**
     * Creates a new empty observable list that is backed by an arraylist.
     *
     * @param E the list element type
     *
     * @return a newly created ObservableList
     *
     * @see observableList
     */
    fun <E> observableArrayList(): ObservableList<E> {
        return observableList(ArrayList())
    }

    /**
     * Creates a new empty observable list backed by an arraylist.
     *
     * This list reports element updates.
     *
     * @param E the list element type
     * @param extractor element to Observable[] convertor. Observable objects are listened for changes on the element.
     *
     * @return a newly created ObservableList
     *
     * @see observableList
     */
    fun <E> observableArrayList(extractor: Callback<E, Array<Observable>>): ObservableList<E> {
        return observableList(ArrayList(), extractor)
    }

    /**
     * Creates a new observable array list with `items` added to it.
     *
     * @param E the list element type
     * @param items the items that will be in the new observable ArrayList
     *
     * @return a newly created observableArrayList
     *
     * @see observableArrayList
     */
    fun <E> observableArrayList(vararg items: E): ObservableList<E> {
        val list: ObservableList<E> = observableArrayList()
        list.addAll(*items)
        return list
    }

    /**
     * Creates a new observable array list and adds a content of collection `col` to it.
     *
     * @param E the list element type
     * @param col a collection which content should be added to the observableArrayList
     *
     * @return a newly created observableArrayList
     */
    fun <E> observableArrayList(col: Collection<E>): ObservableList<E> {
        val list: ObservableList<E> = observableArrayList()
        list.addAll(col)
        return list
    }

    /**
     * Creates a new empty observable map that is backed by a HashMap.
     *
     * @param K the type of keys
     * @param V the type of values
     *
     * @return a newly created observable HashMap
     */
    fun <K, V> observableHashMap(): ObservableMap<K, V> {
        return observableMap(HashMap())
    }

    /**
     * Constructs an ObservableMap that is backed by a HashMap, containing the specified key-value [Pairs][Pair].
     * Mutation operations on the ObservableMap instance will be reported to observers that have registered on that
     * instance.
     *
     * @param K the type of keys maintained by this map
     * @param V the type of mapped values
     * @param pairs the key-value pairs
     *
     * @return a newly created ObservableMap
     */
    fun <K, V> observableHashMap(vararg pairs: Pair<K, V>): ObservableMap<K, V> {
        return observableMap(hashMapOf(*pairs))
    }

    /**
     * Creates and returns unmodifiable wrapper list on top of provided observable list.
     *
     * @param E the list element type
     * @param list an ObservableList that is to be wrapped
     *
     * @return an ObservableList wrapper that is unmodifiable
     *
     * @see Collections.unmodifiableList
     */
    fun <E> unmodifiableObservableList(list: ObservableList<E>): ObservableList<E> {
        return UnmodifiableObservableListImpl(list)
    }

    /**
     * Creates and returns a typesafe wrapper on top of provided observable list.
     *
     * @param E the list element type
     * @param list an Observable list to be wrapped
     * @param type the type of element that `list` is permitted to hold
     *
     * @return a dynamically typesafe view of the specified list
     *
     * @see Collections.checkedList
     */
    fun <E> checkedObservableList(list: ObservableList<E>, type: KType): ObservableList<E> {
        return CheckedObservableList(list, type)
    }

    /**
     * Creates and returns a synchronized wrapper on top of provided observable list.
     *
     * @param E the list element type
     * @param list the list to be "wrapped" in a synchronized list.
     *
     * @return A synchronized version of the observable list
     *
     * @see Collections.synchronizedList
     */
    fun <E> synchronizedObservableList(list: ObservableList<E>): ObservableList<E> {
        return SynchronizedObservableList(list)
    }

    /**
     * Creates an empty unmodifiable observable list.
     *
     * @param E the list element type
     *
     * @return An empty unmodifiable observable list
     *
     * @see Collections.emptyList
     */
    @ReturnsUnmodifiableCollection
    fun <E> emptyObservableList(): ObservableList<E> {
        return EmptyObservableList()
    }

    /**
     * Creates an unmodifiable observable list with single element.
     *
     * @param E the list element type
     * @param e the only elements that will be contained in this singleton observable list
     *
     * @return a singleton observable list
     *
     * @see Collections.singletonList
     */
    @ReturnsUnmodifiableCollection
    fun <E> singletonObservableList(e: E): ObservableList<E> {
        return SingletonObservableList(e)
    }

    /**
     * Creates and returns unmodifiable wrapper on top of provided observable set.
     *
     * @param E the set element type
     * @param set an ObservableSet that is to be wrapped
     *
     * @return an ObservableSet wrapper that is unmodifiable
     *
     * @see Collections.unmodifiableSet
     */
    @ReturnsUnmodifiableCollection
    fun <E> unmodifiableObservableSet(set: ObservableSet<E>): ObservableSet<E> {
        return UnmodifiableObservableSet(set)
    }

    /**
     * Creates and returns a typesafe wrapper on top of provided observable set.
     *
     * @param E the set element type
     * @param set an Observable set to be wrapped
     * @param type the type of element that `set` is permitted to hold
     *
     * @return a dynamically typesafe view of the specified set
     *
     * @see Collections.checkedSet
     */
    fun <E> checkedObservableSet(set: ObservableSet<E>, type: KType): ObservableSet<E> {
        return CheckedObservableSet(set, type)
    }

    /**
     * Creates and returns a synchronized wrapper on top of provided observable set.
     *
     * @param E the set element type
     * @param set the set to be "wrapped" in a synchronized set.
     *
     * @return A synchronized version of the observable set
     *
     * @see Collections.synchronizedSet
     */
    fun <E> synchronizedObservableSet(set: ObservableSet<E>): ObservableSet<E> {
        return SynchronizedObservableSet(set)
    }

    /**
     * Creates and empty unmodifiable observable set.
     *
     * @param E the set element type
     *
     * @return An empty unmodifiable observable set
     *
     * @see Collections.emptySet
     */
    @ReturnsUnmodifiableCollection
    fun <E> emptyObservableSet(): ObservableSet<E> {
        return EmptyObservableSet()
    }

    /**
     * Returns an immutable set containing only the specified object. The returned set is serializable.
     *
     * @param E the class of the objects in the set
     * @param o the sole object to be stored in the returned set.
     *
     * @return an immutable set containing only the specified object.
     */
    @ReturnsUnmodifiableCollection
    fun <E> singletonObservable(o: E): ObservableSet<E> {
        return SingletonObservableSet(o)
    }

    /**
     * Sorts the provided observable list. Fires only **one** change notification on the list.
     *
     * @param T the list element type
     * @param list the list to be sorted
     *
     * @see Collections.sort
     */
    fun <T : Comparable<T>> sort(list: ObservableList<T>) {
        if (list is SortableList<*>) {
            list.sort()
        } else {
            val newContent: MutableList<T> = ArrayList(list)
            newContent.sort()
            list.setAll(newContent)
        }
    }

    /**
     * Sorts the provided observable list using the c comparator. Fires only **one** change notification on the list.
     *
     * @param T the list element type
     * @param list the list to sort
     * @param c comparator used for sorting. Null if natural ordering is required.
     *
     * @see Collections.sort
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> sort(list: ObservableList<T>, c: Comparator<in T>) {
        if (list is SortableList<*>) {
            (list as SortableList<T>).sortWith(c)
        } else {
            val newContent: MutableList<T> = ArrayList(list)
            newContent.sortWith(c)
            list.setAll(newContent)
        }
    }

    private class EmptyObservableList<E> : AbstractMutableList<E>(), ObservableList<E> {

        private val iterator: MutableListIterator<E>

        init {
            this.iterator = object : MutableListIterator<E> {

                override fun hasNext(): Boolean {
                    return false
                }

                override fun next(): E {
                    throw NoSuchElementException()
                }

                override fun remove() {
                    throw UnsupportedOperationException()
                }

                override fun hasPrevious(): Boolean {
                    return false
                }

                override fun previous(): E {
                    throw NoSuchElementException()
                }

                override fun nextIndex(): Int {
                    return 0
                }

                override fun previousIndex(): Int {
                    return -1
                }

                override fun set(element: E) {
                    throw UnsupportedOperationException()
                }

                override fun add(element: E) {
                    throw UnsupportedOperationException()
                }

            }
        }

        override fun add(index: Int, element: E) {
            throw UnsupportedOperationException()
        }

        override fun removeAt(index: Int): E {
            throw UnsupportedOperationException()
        }

        override fun set(index: Int, element: E): E {
            throw UnsupportedOperationException()
        }

        override fun addAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(col: Collection<E>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun removeAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun retainAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun remove(from: Int, to: Int) {
            throw UnsupportedOperationException()
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ListChangeListener<in E>) {
        }

        override fun removeListener(listener: ListChangeListener<in E>) {
        }

        override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in E>): Boolean {
            return false
        }

        override val size: Int
            get() = 0

        override operator fun contains(element: E): Boolean {
            return false
        }

        override fun iterator(): MutableIterator<E> {
            return this.iterator
        }

        override fun containsAll(elements: Collection<E>): Boolean {
            return elements.isEmpty()
        }

        override fun get(index: Int): E {
            throw IndexOutOfBoundsException()
        }

        override fun indexOf(element: E): Int {
            return -1
        }

        override fun lastIndexOf(element: E): Int {
            return -1
        }

        override fun listIterator(): MutableListIterator<E> {
            return this.iterator
        }

        override fun listIterator(index: Int): MutableListIterator<E> {
            if (index != 0) {
                throw IndexOutOfBoundsException()
            }
            return this.iterator
        }

        override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
            if (fromIndex != 0 || toIndex != 0) {
                throw IndexOutOfBoundsException()
            }
            return this
        }

    }

    private class SingletonObservableList<E>(private val element: E) : AbstractMutableList<E>(), ObservableList<E> {

        override fun add(index: Int, element: E) {
            throw UnsupportedOperationException()
        }

        override fun removeAt(index: Int): E {
            throw UnsupportedOperationException()
        }

        override fun set(index: Int, element: E): E {
            throw UnsupportedOperationException()
        }

        override fun addAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(col: Collection<E>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun removeAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun retainAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun remove(from: Int, to: Int) {
            throw UnsupportedOperationException()
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ListChangeListener<in E>) {
        }

        override fun removeListener(listener: ListChangeListener<in E>) {
        }

        override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in E>): Boolean {
            return false
        }

        override val size: Int
            get() = 1

        override fun isEmpty(): Boolean {
            return false
        }

        override operator fun contains(element: E): Boolean {
            return this.element == element
        }

        override fun get(index: Int): E {
            if (index != 0) {
                throw IndexOutOfBoundsException()
            }
            return this.element
        }

    }

    @Suppress("JoinDeclarationAndAssignment")
    private class UnmodifiableObservableListImpl<T>(private val backingList: ObservableList<T>) :
            ObservableListBase<T>(), ObservableList<T> {

        private val listener: ListChangeListener<T>

        init {
            this.listener = ListChangeListener { change ->
                fireChange(SourceAdapterListChange(this@UnmodifiableObservableListImpl, change))
            }
            this.backingList.addListener(this.listener)
        }

        override fun get(index: Int): T {
            return this.backingList[index]
        }

        override val size: Int
            get() = this.backingList.size

        override fun add(index: Int, element: T) {
            throw UnsupportedOperationException()
        }

        override fun removeAt(index: Int): T {
            throw UnsupportedOperationException()
        }

        override fun set(index: Int, element: T): T {
            throw UnsupportedOperationException()
        }

        override fun addAll(vararg elements: T): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(vararg elements: T): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(col: Collection<T>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun removeAll(vararg elements: T): Boolean {
            throw UnsupportedOperationException()
        }

        override fun retainAll(vararg elements: T): Boolean {
            throw UnsupportedOperationException()
        }

        override fun remove(from: Int, to: Int) {
            throw UnsupportedOperationException()
        }

    }

    private open class SynchronizedList<T>(list: MutableList<T>, protected val mutex: Any) : MutableList<T> {

        private val backingList: MutableList<T> = list

        override val size: Int
            get() = synchronized(this.mutex) { return@synchronized this.backingList.size }

        override fun isEmpty(): Boolean {
            synchronized(this.mutex) {
                return this.backingList.isEmpty()
            }
        }

        override fun contains(element: T): Boolean {
            synchronized(this.mutex) {
                return this.backingList.contains(element)
            }
        }

        override fun iterator(): MutableIterator<T> {
            return this.backingList.iterator()
        }

        override fun add(element: T): Boolean {
            synchronized(this.mutex) {
                return this.backingList.add(element)
            }
        }

        override fun remove(element: T): Boolean {
            synchronized(this.mutex) {
                return this.backingList.remove(element)
            }
        }

        override fun containsAll(elements: Collection<T>): Boolean {
            synchronized(this.mutex) {
                return this.backingList.containsAll(elements)
            }
        }

        override fun addAll(elements: Collection<T>): Boolean {
            synchronized(this.mutex) {
                return this.backingList.addAll(elements)
            }
        }

        override fun addAll(index: Int, elements: Collection<T>): Boolean {
            synchronized(this.mutex) {
                return this.backingList.addAll(index, elements)
            }
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            synchronized(this.mutex) {
                return this.backingList.removeAll(elements)
            }
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            synchronized(this.mutex) {
                return this.backingList.retainAll(elements)
            }
        }

        override fun clear() {
            synchronized(this.mutex) {
                this.backingList.clear()
            }
        }

        override fun get(index: Int): T {
            synchronized(this.mutex) {
                return this.backingList[index]
            }
        }

        override fun set(index: Int, element: T): T {
            synchronized(this.mutex) {
                return this.backingList.set(index, element)
            }
        }

        override fun add(index: Int, element: T) {
            synchronized(this.mutex) {
                this.backingList.add(index, element)
            }
        }

        override fun removeAt(index: Int): T {
            synchronized(this.mutex) {
                return this.backingList.removeAt(index)
            }
        }

        override fun indexOf(element: T): Int {
            synchronized(this.mutex) {
                return this.backingList.indexOf(element)
            }
        }

        override fun lastIndexOf(element: T): Int {
            synchronized(this.mutex) {
                return this.backingList.lastIndexOf(element)
            }
        }

        override fun listIterator(): MutableListIterator<T> {
            synchronized(this.mutex) {
                return this.backingList.listIterator()
            }
        }

        override fun listIterator(index: Int): MutableListIterator<T> {
            synchronized(this.mutex) {
                return this.backingList.listIterator(index)
            }
        }

        override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
            synchronized(this.mutex) {
                return this.backingList.subList(fromIndex, toIndex)
            }
        }

        override fun toString(): String {
            synchronized(this.mutex) {
                return this.backingList.toString()
            }
        }

        override fun equals(other: Any?): Boolean {
            synchronized(this.mutex) {
                return this.backingList == other
            }
        }

        override fun hashCode(): Int {
            synchronized(this.mutex) {
                return this.backingList.hashCode()
            }
        }

    }

    private class SynchronizedObservableList<T>(seq: ObservableList<T>, mutex: Any) : SynchronizedList<T>(seq, mutex),
            ObservableList<T> {

        private var helper: ListListenerHelper<T>? = null

        private val backingList: ObservableList<T> = seq

        private val listener: ListChangeListener<T> = ListChangeListener { change ->
            ListListenerHelper.fireValueChangedEvent(this.helper, SourceAdapterListChange(this, change))
        }

        init {
            this.backingList.addListener(WeakListChangeListener(this.listener))
        }

        constructor(seq: ObservableList<T>) : this(seq, Any())

        override fun addAll(vararg elements: T): Boolean {
            synchronized(this.mutex) {
                return this.backingList.addAll(*elements)
            }
        }

        override fun setAll(vararg elements: T): Boolean {
            synchronized(this.mutex) {
                return this.backingList.setAll(*elements)
            }
        }

        override fun removeAll(vararg elements: T): Boolean {
            synchronized(this.mutex) {
                return this.backingList.removeAll(*elements)
            }
        }

        override fun retainAll(vararg elements: T): Boolean {
            synchronized(this.mutex) {
                return this.backingList.retainAll(*elements)
            }
        }

        override fun remove(from: Int, to: Int) {
            synchronized(this.mutex) {
                this.backingList.remove(from, to)
            }
        }

        override fun setAll(col: Collection<T>): Boolean {
            synchronized(this.mutex) {
                return this.backingList.setAll(col)
            }
        }

        override fun addListener(listener: InvalidationListener) {
            synchronized(this.mutex) {
                if (!isInvalidationListenerAlreadyAdded(listener)) {
                    this.helper = ListListenerHelper.addListener(this.helper, listener)
                }
            }
        }

        override fun removeListener(listener: InvalidationListener) {
            synchronized(this.mutex) {
                if (isInvalidationListenerAlreadyAdded(listener)) {
                    this.helper = ListListenerHelper.removeListener(this.helper, listener)
                }
            }
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            val curHelper = this.helper
            return curHelper != null && curHelper.invalidationListeners.contains(listener)
        }

        override fun addListener(listener: ListChangeListener<in T>) {
            synchronized(this.mutex) {
                if (!isListChangeListenerAlreadyAdded(listener)) {
                    this.helper = ListListenerHelper.addListener(this.helper, listener)
                }
            }
        }

        override fun removeListener(listener: ListChangeListener<in T>) {
            synchronized(this.mutex) {
                if (isListChangeListenerAlreadyAdded(listener)) {
                    this.helper = ListListenerHelper.removeListener(this.helper, listener)
                }
            }
        }

        override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in T>): Boolean {
            val curHelper = this.helper
            return curHelper != null && curHelper.listChangeListeners.contains(listener)
        }

    }

    private class CheckedObservableList<T>(private val list: ObservableList<T>, private val type: KType) :
            ObservableListBase<T>(), ObservableList<T> {

        private val listener: ListChangeListener<T> = ListChangeListener { change ->
            fireChange(SourceAdapterListChange(this, change))
        }

        init {
            this.list.addListener(WeakListChangeListener(this.listener))
        }

        private fun typeCheck(o: Any?) {
            if ((o == null && !this.type.isMarkedNullable) ||
                    (o != null && !(this.type.classifier as KClass<*>).isInstance(o))) {
                throw ClassCastException("Attempt to insert ${o?.javaClass} element into collection with element type" +
                        " ${this.type}")
            }
        }

        override val size: Int
            get() = this.list.size

        override fun isEmpty(): Boolean {
            return this.list.isEmpty()
        }

        override fun contains(element: T): Boolean {
            return this.list.contains(element)
        }

        override fun toString(): String {
            return this.list.toString()
        }

        override fun remove(element: T): Boolean {
            return this.list.remove(element)
        }

        override fun containsAll(elements: Collection<T>): Boolean {
            return this.list.containsAll(elements)
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            return this.list.removeAll(elements)
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            return this.list.retainAll(elements)
        }

        override fun removeAll(vararg elements: T): Boolean {
            return this.list.removeAll(*elements)
        }

        override fun retainAll(vararg elements: T): Boolean {
            return this.list.retainAll(*elements)
        }

        override fun remove(from: Int, to: Int) {
            this.list.remove(from, to)
        }

        override fun clear() {
            this.list.clear()
        }

        override fun equals(other: Any?): Boolean {
            return other === this || this.list == other
        }

        override fun hashCode(): Int {
            return this.list.hashCode()
        }

        override fun get(index: Int): T {
            return this.list[index]
        }

        override fun removeAt(index: Int): T {
            return this.list.removeAt(index)
        }

        override fun indexOf(element: T): Int {
            return this.list.indexOf(element)
        }

        override fun lastIndexOf(element: T): Int {
            return this.list.lastIndexOf(element)
        }

        override fun set(index: Int, element: T): T {
            typeCheck(element)
            return this.list.set(index, element)
        }

        override fun add(index: Int, element: T) {
            typeCheck(element)
            return this.list.add(index, element)
        }

        @Suppress("UNCHECKED_CAST")
        override fun addAll(index: Int, elements: Collection<T>): Boolean {
            val a: Array<T>
            val c = ArrayList(elements)
            try {
                val kClass = this.type.classifier as KClass<*>
                a = c.toArray(java.lang.reflect.Array.newInstance(kClass.java, 0) as Array<T>)
            } catch (e: ArrayStoreException) {
                throw ClassCastException()
            }

            return this.list.addAll(index, a.asList())
        }

        @Suppress("UNCHECKED_CAST")
        override fun addAll(elements: Collection<T>): Boolean {
            val a: Array<T>
            val c = ArrayList(elements)
            try {
                val kClass = this.type.classifier as KClass<*>
                a = c.toArray(java.lang.reflect.Array.newInstance(kClass.java, 0) as Array<T>)
            } catch (e: ArrayStoreException) {
                throw ClassCastException()
            }

            return this.list.addAll(a.asList())
        }

        override fun listIterator(): MutableListIterator<T> {
            return listIterator(0)
        }

        override fun listIterator(index: Int): MutableListIterator<T> {
            return object : MutableListIterator<T> {

                private val i: MutableListIterator<T> = this@CheckedObservableList.list.listIterator(index)

                override fun hasNext(): Boolean {
                    return this.i.hasNext()
                }

                override fun next(): T {
                    return this.i.next()
                }

                override fun hasPrevious(): Boolean {
                    return this.i.hasPrevious()
                }

                override fun previous(): T {
                    return this.i.previous()
                }

                override fun nextIndex(): Int {
                    return this.i.nextIndex()
                }

                override fun previousIndex(): Int {
                    return this.i.previousIndex()
                }

                override fun remove() {
                    this.i.remove()
                }

                override fun set(element: T) {
                    typeCheck(element)
                    this.i.set(element)
                }

                override fun add(element: T) {
                    typeCheck(element)
                    this.i.add(element)
                }

            }
        }

        override fun iterator(): MutableIterator<T> {
            return object : MutableIterator<T> {

                private val it: MutableIterator<T> = this@CheckedObservableList.list.iterator()

                override fun hasNext(): Boolean {
                    return this.it.hasNext()
                }

                override fun next(): T {
                    return this.it.next()
                }

                override fun remove() {
                    this.it.remove()
                }

            }
        }

        override fun add(element: T): Boolean {
            typeCheck(element)
            return this.list.add(element)
        }

        override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
            return CheckedObservableList(observableList(this.list.subList(fromIndex, toIndex)), this.type)
        }

        @Suppress("UNCHECKED_CAST")
        override fun addAll(vararg elements: T): Boolean {
            return addAll(elements.toList())
        }

        override fun setAll(vararg elements: T): Boolean {
            return setAll(elements.toList())
        }

        @Suppress("UNCHECKED_CAST")
        override fun setAll(col: Collection<T>): Boolean {
            val a: Array<T>
            val c = ArrayList(col)
            try {
                val kClass = this.type.classifier as KClass<*>
                a = c.toArray(java.lang.reflect.Array.newInstance(kClass.java, 0) as Array<T>)
            } catch (e: ArrayStoreException) {
                throw ClassCastException()
            }

            return this.list.setAll(a.asList())
        }

    }

    private class EmptyObservableSet<E> : AbstractMutableSet<E>(), ObservableSet<E> {

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: SetChangeListener<in E>) {
        }

        override fun removeListener(listener: SetChangeListener<in E>) {
        }

        override fun isSetChangeListenerAlreadyAdded(listener: SetChangeListener<in E>): Boolean {
            return false
        }

        override val size: Int
            get() = 0

        override fun isEmpty(): Boolean {
            return true
        }

        override fun contains(element: E): Boolean {
            return false
        }

        override fun containsAll(elements: Collection<E>): Boolean {
            return elements.isEmpty()
        }

        override fun toArray(): Array<Any> {
            return arrayOf()
        }

        override fun <T : Any?> toArray(a: Array<T?>): Array<T?> {
            if (a.isNotEmpty()) {
                a[0] = null
            }
            return a
        }

        override fun iterator(): MutableIterator<E> {
            return object : MutableIterator<E> {

                override fun hasNext(): Boolean {
                    return false
                }

                override fun next(): E {
                    throw NoSuchElementException()
                }

                override fun remove() {
                    throw UnsupportedOperationException()
                }

            }
        }

        override fun add(element: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(elements: Collection<E>): Boolean {
            throw UnsupportedOperationException()
        }

    }

    private class SingletonObservableSet<E>(private val element: E) : AbstractMutableSet<E>(), ObservableSet<E> {

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: SetChangeListener<in E>) {
        }

        override fun removeListener(listener: SetChangeListener<in E>) {
        }

        override fun isSetChangeListenerAlreadyAdded(listener: SetChangeListener<in E>): Boolean {
            return false
        }

        override val size: Int
            get() = 1

        override fun isEmpty(): Boolean {
            return false
        }

        override fun contains(element: E): Boolean {
            return this.element == element
        }

        override fun iterator(): MutableIterator<E> {
            return singletonIterator(this.element)
        }

        override fun add(element: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(elements: Collection<E>): Boolean {
            throw UnsupportedOperationException()
        }

    }

    private class UnmodifiableObservableSet<E>(private val backingSet: ObservableSet<E>) : AbstractMutableSet<E>(),
            ObservableSet<E> {

        private var listenerHelper: SetListenerHelper<E>? = null

        private lateinit var listener: SetChangeListener<E>

        private fun initListener() {
            if (!this::listener.isInitialized) {
                this.listener = SetChangeListener { change -> callObservers(SetAdapterChange(this, change)) }
                this.backingSet.addListener(WeakSetChangeListener(this.listener))
            }
        }

        private fun callObservers(change: SetChangeListener.Change<out E>) {
            SetListenerHelper.fireValueChangedEvent(this.listenerHelper, change)
        }

        override fun iterator(): MutableIterator<E> {
            return object : MutableIterator<E> {

                private val i = this@UnmodifiableObservableSet.backingSet.iterator()

                override fun hasNext(): Boolean {
                    return this.i.hasNext()
                }

                override fun next(): E {
                    return this.i.next()
                }

                override fun remove() {
                    throw UnsupportedOperationException()
                }

            }
        }

        override val size: Int
            get() = this.backingSet.size

        override fun addListener(listener: InvalidationListener) {
            if (!isInvalidationListenerAlreadyAdded(listener)) {
                initListener()
                this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener)
            }
        }

        override fun removeListener(listener: InvalidationListener) {
            if (isInvalidationListenerAlreadyAdded(listener)) {
                this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener)
            }
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.invalidationListeners.contains(listener)
        }

        override fun addListener(listener: SetChangeListener<in E>) {
            if (!isSetChangeListenerAlreadyAdded(listener)) {
                initListener()
                this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener)
            }
        }

        override fun removeListener(listener: SetChangeListener<in E>) {
            if (isSetChangeListenerAlreadyAdded(listener)) {
                this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener)
            }
        }

        override fun isSetChangeListenerAlreadyAdded(listener: SetChangeListener<in E>): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.setChangeListeners.contains(listener)
        }

        override fun add(element: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun remove(element: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun addAll(elements: Collection<E>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(vararg elements: E): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(elements: Collection<E>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun retainAll(elements: Collection<E>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun removeAll(elements: Collection<E>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun clear() {
            throw UnsupportedOperationException()
        }

    }

    private open class SynchronizedSet<E>(set: MutableSet<E>, protected val mutex: Any) : MutableSet<E> {

        private val backingSet: MutableSet<E> = set

        constructor(set: MutableSet<E>) : this(set, Any())

        override val size: Int
            get() = synchronized(this.mutex) { return@synchronized this.backingSet.size }

        override fun isEmpty(): Boolean {
            return synchronized(this.mutex) {
                this.backingSet.isEmpty()
            }
        }

        override fun contains(element: E): Boolean {
            return synchronized(this.mutex) {
                this.backingSet.contains(element)
            }
        }

        override fun iterator(): MutableIterator<E> {
            return this.backingSet.iterator()
        }

        override fun add(element: E): Boolean {
            return synchronized(this.mutex) {
                this.backingSet.add(element)
            }
        }

        override fun remove(element: E): Boolean {
            return synchronized(this.mutex) {
                this.backingSet.remove(element)
            }
        }

        override fun containsAll(elements: Collection<E>): Boolean {
            return synchronized(this.mutex) {
                this.backingSet.containsAll(elements)
            }
        }

        override fun addAll(elements: Collection<E>): Boolean {
            return synchronized(this.mutex) {
                this.backingSet.addAll(elements)
            }
        }

        override fun retainAll(elements: Collection<E>): Boolean {
            return synchronized(this.mutex) {
                this.backingSet.retainAll(elements)
            }
        }

        override fun removeAll(elements: Collection<E>): Boolean {
            return synchronized(this.mutex) {
                this.backingSet.removeAll(elements)
            }
        }

        override fun clear() {
            synchronized(this.mutex) {
                this.backingSet.clear()
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            return synchronized(this.mutex) {
                this.backingSet == other
            }
        }

        override fun hashCode(): Int {
            return synchronized(this.mutex) {
                this.backingSet.hashCode()
            }
        }

    }

    private class SynchronizedObservableSet<E>(set: ObservableSet<E>, mutex: Any) : SynchronizedSet<E>(set, mutex),
            ObservableSet<E> {

        private val backingSet: ObservableSet<E> = set

        private var listenerHelper: SetListenerHelper<E>? = null

        private val listener: SetChangeListener<E> = SetChangeListener { change ->
            SetListenerHelper.fireValueChangedEvent(this.listenerHelper, SetAdapterChange(this, change))
        }

        init {
            this.backingSet.addListener(WeakSetChangeListener(this.listener))
        }

        constructor(set: ObservableSet<E>) : this(set, Any())

        override fun addListener(listener: InvalidationListener) {
            synchronized(this.mutex) {
                if (!isInvalidationListenerAlreadyAdded(listener)) {
                    this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener)
                }
            }
        }

        override fun removeListener(listener: InvalidationListener) {
            synchronized(this.mutex) {
                if (isInvalidationListenerAlreadyAdded(listener)) {
                    this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener)
                }
            }
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.invalidationListeners.contains(listener)
        }

        override fun addListener(listener: SetChangeListener<in E>) {
            synchronized(this.mutex) {
                if (!isSetChangeListenerAlreadyAdded(listener)) {
                    this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener)
                }
            }
        }

        override fun removeListener(listener: SetChangeListener<in E>) {
            synchronized(this.mutex) {
                if (isSetChangeListenerAlreadyAdded(listener)) {
                    this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener)
                }
            }
        }

        override fun isSetChangeListenerAlreadyAdded(listener: SetChangeListener<in E>): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.setChangeListeners.contains(listener)
        }

        override fun setAll(vararg elements: E): Boolean {
            synchronized(this.mutex) {
                val toRemove = LinkedList<E>()
                for (e in backingSet) {
                    if (e !in elements) {
                        toRemove.add(e)
                    }
                }

                for (e in toRemove) {
                    remove(e)
                }

                for (element in elements) {
                    add(element)
                }

                return true
            }
        }

        override fun setAll(elements: Collection<E>): Boolean {
            synchronized(this.mutex) {
                val toRemove = LinkedList<E>()
                for (e in backingSet) {
                    if (e !in elements) {
                        toRemove.add(e)
                    }
                }

                for (e in toRemove) {
                    remove(e)
                }

                for (element in elements) {
                    add(element)
                }

                return true
            }
        }

    }

    private class CheckedObservableSet<E>(set: ObservableSet<E>, private val type: KType) :
            AbstractMutableSet<E>(), ObservableSet<E> {

        private val backingSet: ObservableSet<E> = set

        private var listenerHelper: SetListenerHelper<E>? = null

        private val listener: SetChangeListener<E> = SetChangeListener { change ->
            callObservers(SetAdapterChange(this, change))
        }

        init {
            this.backingSet.addListener(this.listener)
        }

        private fun callObservers(change: SetChangeListener.Change<out E>) {
            SetListenerHelper.fireValueChangedEvent(this.listenerHelper, change)
        }

        private fun typeCheck(o: Any?) {
            if ((o == null && !this.type.isMarkedNullable) ||
                    (o != null && !(this.type.classifier as KClass<*>).isInstance(o))) {
                throw ClassCastException("Attempt to insert ${o?.javaClass} element into collection with element " +
                        "type $type")
            }
        }

        override fun addListener(listener: InvalidationListener) {
            if (!isInvalidationListenerAlreadyAdded(listener)) {
                this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener)
            }
        }

        override fun removeListener(listener: InvalidationListener) {
            if (isInvalidationListenerAlreadyAdded(listener)) {
                this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener)
            }
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.invalidationListeners.contains(listener)
        }

        override fun addListener(listener: SetChangeListener<in E>) {
            if (!isSetChangeListenerAlreadyAdded(listener)) {
                this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener)
            }
        }

        override fun removeListener(listener: SetChangeListener<in E>) {
            if (isSetChangeListenerAlreadyAdded(listener)) {
                this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener)
            }
        }

        override fun isSetChangeListenerAlreadyAdded(listener: SetChangeListener<in E>): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.setChangeListeners.contains(listener)
        }

        override val size: Int
            get() = this.backingSet.size

        override fun isEmpty(): Boolean {
            return this.backingSet.isEmpty()
        }

        override fun contains(element: E): Boolean {
            return this.backingSet.contains(element)
        }

        override fun add(element: E): Boolean {
            typeCheck(element)
            return this.backingSet.add(element)
        }

        override fun remove(element: E): Boolean {
            return this.backingSet.remove(element)
        }

        override fun containsAll(elements: Collection<E>): Boolean {
            return this.backingSet.containsAll(elements)
        }

        @Suppress("UNCHECKED_CAST")
        override fun addAll(elements: Collection<E>): Boolean {
            val a: Array<E>
            val c = ArrayList(elements)
            try {
                val kClass = this.type.classifier as KClass<*>
                a = c.toArray(java.lang.reflect.Array.newInstance(kClass.java, 0) as Array<E>)
            } catch (e: ArrayStoreException) {
                throw ClassCastException()
            }
            return this.backingSet.addAll(a.asList())
        }

        override fun retainAll(elements: Collection<E>): Boolean {
            return this.backingSet.retainAll(elements)
        }

        override fun removeAll(elements: Collection<E>): Boolean {
            return this.backingSet.removeAll(elements)
        }

        override fun clear() {
            this.backingSet.clear()
        }

        override fun equals(other: Any?): Boolean {
            return other !== this && this.backingSet == other
        }

        override fun hashCode(): Int {
            return this.backingSet.hashCode()
        }

        override fun iterator(): MutableIterator<E> {
            val it = this.backingSet.iterator()

            return object : MutableIterator<E> {

                override fun hasNext(): Boolean {
                    return it.hasNext()
                }

                override fun next(): E {
                    return it.next()
                }

                override fun remove() {
                    it.remove()
                }

            }
        }

        override fun setAll(vararg elements: E): Boolean {
            val toRemove = LinkedList<E>()
            for (e in backingSet) {
                if (e !in elements) {
                    toRemove.add(e)
                }
            }

            for (e in toRemove) {
                remove(e)
            }

            for (element in elements) {
                add(element)
            }

            return true
        }

        override fun setAll(elements: Collection<E>): Boolean {
            val toRemove = LinkedList<E>()
            for (e in backingSet) {
                if (e !in elements) {
                    toRemove.add(e)
                }
            }

            for (e in toRemove) {
                remove(e)
            }

            for (element in elements) {
                add(element)
            }

            return true
        }

    }

    private class EmptyObservableMap<K, V> : AbstractMutableMap<K, V>(), ObservableMap<K, V> {

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: MapChangeListener<in K, in V>) {
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>) {
        }

        override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in K, in V>): Boolean {
            return false
        }

        override val size: Int
            get() = 0

        override fun isEmpty(): Boolean {
            return true
        }

        override fun put(key: K, value: V): V? {
            throw UnsupportedOperationException()
        }

        override fun setAll(vararg pairs: Pair<K, V>) {
            throw UnsupportedOperationException()
        }

        override fun setAll(map: Map<out K, V>) {
            throw UnsupportedOperationException()
        }

        override fun containsKey(key: K): Boolean {
            return false
        }

        override fun containsValue(value: V): Boolean {
            return false
        }

        override fun get(key: K): V? {
            return null
        }

        override val keys: MutableSet<K>
            get() = emptyObservableSet()

        override val values: MutableCollection<V>
            get() = emptyObservableSet()

        override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
            get() = emptyObservableSet()

        override fun equals(other: Any?): Boolean {
            return if (other is Map<*, *>) other.isEmpty() else false
        }

        override fun hashCode(): Int {
            return 0
        }

    }

    private class SingletonObservableMap<K, V>(private val key: K, private val value: V) : AbstractMutableMap<K, V>(),
            ObservableMap<K, V> {

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: MapChangeListener<in K, in V>) {
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>) {
        }

        override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in K, in V>): Boolean {
            return false
        }

        override val size: Int
            get() = 1

        override fun isEmpty(): Boolean {
            return false
        }

        override fun containsKey(key: K): Boolean {
            return this.key == key
        }

        override fun containsValue(value: V): Boolean {
            return this.value == value
        }

        override fun get(key: K): V? {
            return if (key == this.key) this.value else null
        }

        override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
            get() = singletonObservable(SimpleImmutableEntry(this.key, this.value))

        override fun put(key: K, value: V): V? {
            throw UnsupportedOperationException()
        }

        override fun setAll(vararg pairs: Pair<K, V>) {
            throw UnsupportedOperationException()
        }

        override fun setAll(map: Map<out K, V>) {
            throw UnsupportedOperationException()
        }

    }

    private class CheckedObservableMap<K, V>(map: ObservableMap<K, V>, private val keyType: KType,
            private val valueType: KType) : AbstractMutableMap<K, V>(), ObservableMap<K, V> {

        private val backingMap: ObservableMap<K, V> = map

        private var listenerHelper: MapListenerHelper<K, V>? = null

        private val listener: MapChangeListener<K, V> = MapChangeListener { change ->
            callObservers(MapAdapterChange(this, change))
        }

        init {
            this.backingMap.addListener(WeakMapChangeListener(this.listener))
        }

        private fun callObservers(c: MapChangeListener.Change<out K, out V>) {
            MapListenerHelper.fireValueChangedEvent(this.listenerHelper, c)
        }

        fun typeCheck(key: Any?, value: Any?) {
            if ((key == null && !this.keyType.isMarkedNullable) ||
                    (key != null && !(this.keyType.classifier as KClass<*>).isInstance(key))) {
                throw ClassCastException(
                        "Attempt to insert ${key?.javaClass} key into map with key type ${this.keyType}")
            }

            if ((value == null && !this.valueType.isMarkedNullable) ||
                    (value != null && !(this.valueType.classifier as KClass<*>).isInstance(value))) {
                throw ClassCastException(
                        "Attempt to insert ${value?.javaClass} value into map with value type ${this.valueType}")
            }
        }

        override fun addListener(listener: InvalidationListener) {
            if (!isInvalidationListenerAlreadyAdded(listener)) {
                this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener)
            }
        }

        override fun removeListener(listener: InvalidationListener) {
            if (isInvalidationListenerAlreadyAdded(listener)) {
                this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener)
            }
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.invalidationListeners.contains(listener)
        }

        override fun addListener(listener: MapChangeListener<in K, in V>) {
            if (!isMapChangeListenerAlreadyAdded(listener)) {
                this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener)
            }
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>) {
            if (isMapChangeListenerAlreadyAdded(listener)) {
                this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener)
            }
        }

        override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in K, in V>): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.mapChangeListeners.contains(listener)
        }

        override val size: Int
            get() = this.backingMap.size

        override fun isEmpty(): Boolean {
            return this.backingMap.isEmpty()
        }

        override fun containsKey(key: K): Boolean {
            return this.backingMap.containsKey(key)
        }

        override fun containsValue(value: V): Boolean {
            return this.backingMap.containsValue(value)
        }

        override fun get(key: K): V? {
            return this.backingMap[key]
        }

        override fun put(key: K, value: V): V? {
            typeCheck(key, value)
            return this.backingMap.put(key, value)
        }

        override fun setAll(vararg pairs: Pair<K, V>) {
            val currentEntries = this.entries
            val toRemove = LinkedList<MutableMap.MutableEntry<K, V>>()
            val otherEntries = currentEntries.filter { entry ->
                val toKeep = pairs.any { pair -> pair.first == entry.key }
                if (!toKeep) {
                    toRemove.add(entry)
                }
                toKeep
            }

            for (entry in toRemove) {
                remove(entry.key)
            }

            for (pair in pairs) {
                if (!otherEntries.any { entry -> entry.toPair() == pair }) {
                    put(pair.first, pair.second)
                }
            }
        }

        override fun setAll(map: Map<out K, V>) {
            val newEntries = map.entries

            val currentEntries = this.entries
            val toRemove = LinkedList<MutableMap.MutableEntry<K, V>>()
            val otherEntries = currentEntries.filter { entry ->
                val toKeep = newEntries.any { newEntry -> newEntry.key == entry.key }
                if (!toKeep) {
                    toRemove.add(entry)
                }
                toKeep
            }

            for (entry in toRemove) {
                remove(entry.key)
            }

            for (newEntry in newEntries) {
                if (!otherEntries.any { entry -> entry == newEntry }) {
                    put(newEntry.key, newEntry.value)
                }
            }
        }

        override fun remove(key: K): V? {
            return this.backingMap.remove(key)
        }

        override fun putAll(from: Map<out K, V>) {
            // Satisfy the following goals:
            // - good diagnostics in case of type mismatch
            // - all-or-nothing semantics
            // - protection from malicious t
            // - correct behavior if t is a concurrent map
            val entries = from.entries.toTypedArray()
            val checked: MutableList<MutableMap.MutableEntry<K, V>> = ArrayList(entries.size)
            for (e in entries) {
                val k = e.key
                val v = e.value
                typeCheck(k, v)
                checked.add(SimpleImmutableEntry(k, v))
            }
            for (e in checked) {
                this.backingMap[e.key] = e.value
            }
        }

        override fun clear() {
            this.backingMap.clear()
        }

        override val keys: MutableSet<K>
            get() = this.backingMap.keys

        override val values: MutableCollection<V>
            get() = this.backingMap.values

        @Transient
        private lateinit var entrySet: MutableSet<MutableMap.MutableEntry<K, V>>

        override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
            get() {
                if (!this::entrySet.isInitialized) {
                    this.entrySet = CheckedEntrySet(this.backingMap.entries, this.valueType)
                }
                return this.entrySet
            }

        override fun equals(other: Any?): Boolean {
            return this === other || this.backingMap == other
        }

        override fun hashCode(): Int {
            return this.backingMap.hashCode()
        }

        private class CheckedEntrySet<K, V>(private val s: MutableSet<MutableMap.MutableEntry<K, V>>,
                private val valueType: KType) : MutableSet<MutableMap.MutableEntry<K, V>> {

            override val size: Int
                get() = this.s.size

            override fun isEmpty(): Boolean {
                return this.s.isEmpty()
            }

            override fun toString(): String {
                return this.s.toString()
            }

            override fun hashCode(): Int {
                return this.s.hashCode()
            }

            override fun clear() {
                this.s.clear()
            }

            override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
                throw UnsupportedOperationException()
            }

            override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
                throw UnsupportedOperationException()
            }

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
                val i = this.s.iterator()
                return object : MutableIterator<MutableMap.MutableEntry<K, V>> {

                    override fun hasNext(): Boolean {
                        return i.hasNext()
                    }

                    override fun remove() {
                        i.remove()
                    }

                    override fun next(): MutableMap.MutableEntry<K, V> {
                        return checkedEntry(i.next(), this@CheckedEntrySet.valueType)
                    }

                }
            }

            @Suppress("USELESS_CAST")
            override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean {
                return this.s.contains(
                        if (element is CheckedEntry<*, *>) element as MutableMap.MutableEntry<K, V>
                        else checkedEntry(element, this.valueType))
            }

            override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
                return this.s.containsAll(elements)
            }

            override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
                return this.s.remove(element)
            }

            override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
                return batchRemove(elements, false)
            }

            override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
                return batchRemove(elements, true)
            }

            private fun batchRemove(elements: Collection<MutableMap.MutableEntry<K, V>>, complement: Boolean): Boolean {
                var modified = false
                val it = iterator()
                while (it.hasNext()) {
                    if (elements.contains(it.next()) != complement) {
                        it.remove()
                        modified = true
                    }
                }
                return modified
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) {
                    return true
                }
                if (other !is Set<*>) {
                    return false
                }
                return other.size == this.s.size && containsAll(other)
            }

            /**
             * This "wrapper class" serves two purposes: it prevents the client from modifying the backing Map, by
             * short-circuiting the setValue method, and it protects the backing Map against an ill-behaved Map.Entry
             * that attempts to modify another Map.Entry when asked to perform an equality check.
             */
            private class CheckedEntry<K, V>(private val e: MutableMap.MutableEntry<K, V>,
                    private val valueType: KType) : MutableMap.MutableEntry<K, V> {

                override val key: K
                    get() = this.e.key

                override val value: V
                    get() = this.e.value

                override fun hashCode(): Int {
                    return this.e.hashCode()
                }

                override fun toString(): String {
                    return this.e.toString()
                }

                override fun setValue(newValue: V): V {
                    if ((newValue == null && !this.valueType.isMarkedNullable) ||
                            (newValue != null && !(this.valueType.classifier as KClass<*>).isInstance(newValue))) {
                        throw ClassCastException(badValueMsg(newValue))
                    }
                    return this.e.setValue(newValue)
                }

                private fun badValueMsg(value: Any?): String {
                    return "Attempt to insert ${value?.javaClass} value into map with value type ${this.valueType}"
                }

                override fun equals(other: Any?): Boolean {
                    if (this === other) {
                        return true
                    }
                    if (other !is Map.Entry<*, *>) {
                        return false
                    }
                    return e == SimpleImmutableEntry(other)
                }

            }

            companion object {

                private fun <K, V> checkedEntry(e: MutableMap.MutableEntry<K, V>,
                        valueType: KType): CheckedEntry<K, V> {
                    return CheckedEntry(e, valueType)
                }

            }

        }

    }

    private open class SynchronizedMap<K, V>(map: MutableMap<K, V>, protected val mutex: Any) : MutableMap<K, V> {

        private val backingMap: MutableMap<K, V> = map

        constructor(map: MutableMap<K, V>) : this(map, Any())

        override val size: Int
            get() = synchronized(this.mutex) { return@synchronized this.backingMap.size }

        override fun isEmpty(): Boolean {
            return synchronized(this.mutex) {
                this.backingMap.isEmpty()
            }
        }

        override fun containsKey(key: K): Boolean {
            return synchronized(this.mutex) {
                this.backingMap.containsKey(key)
            }
        }

        override fun containsValue(value: V): Boolean {
            return synchronized(this.mutex) {
                this.backingMap.containsValue(value)
            }
        }

        override fun get(key: K): V? {
            return synchronized(this.mutex) {
                this.backingMap[key]
            }
        }

        override fun put(key: K, value: V): V? {
            return synchronized(this.mutex) {
                this.backingMap.put(key, value)
            }
        }

        override fun remove(key: K): V? {
            return synchronized(this.mutex) {
                this.backingMap.remove(key)
            }
        }

        override fun putAll(from: Map<out K, V>) {
            return synchronized(this.mutex) {
                this.backingMap.putAll(from)
            }
        }

        override fun clear() {
            synchronized(this.mutex) {
                this.backingMap.clear()
            }
        }

        @Transient
        private lateinit var keySet: MutableSet<K>

        @Transient
        private lateinit var valueCollection: MutableCollection<V>

        @Transient
        private lateinit var entrySet: MutableSet<MutableMap.MutableEntry<K, V>>

        override val keys: MutableSet<K>
            get() {
                return synchronized(this.mutex) {
                    if (!this::keySet.isInitialized) {
                        this.keySet = SynchronizedSet(this.backingMap.keys, this.mutex)
                    }
                    this.keySet
                }
            }

        override val values: MutableCollection<V>
            get() {
                return synchronized(this.mutex) {
                    if (!this::valueCollection.isInitialized) {
                        this.valueCollection = SynchronizedCollection(this.backingMap.values, this.mutex)
                    }
                    this.valueCollection
                }
            }

        override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
            get() {
                return synchronized(this.mutex) {
                    if (!this::entrySet.isInitialized) {
                        this.entrySet = SynchronizedSet(this.backingMap.entries, this.mutex)
                    }
                    this.entrySet
                }
            }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            return synchronized(this.mutex) {
                this.backingMap == other
            }
        }

        override fun hashCode(): Int {
            return synchronized(this.mutex) {
                this.backingMap.hashCode()
            }
        }

    }

    private class SynchronizedObservableMap<K, V>(map: ObservableMap<K, V>, mutex: Any) :
            SynchronizedMap<K, V>(map, mutex), ObservableMap<K, V> {

        private val backingMap: ObservableMap<K, V> = map

        private var listenerHelper: MapListenerHelper<K, V>? = null

        private val listener: MapChangeListener<K, V> = MapChangeListener { change ->
            MapListenerHelper.fireValueChangedEvent(this.listenerHelper, MapAdapterChange(this, change))
        }

        init {
            this.backingMap.addListener(WeakMapChangeListener(this.listener))
        }

        constructor(map: ObservableMap<K, V>) : this(map, Any())

        override fun addListener(listener: InvalidationListener) {
            synchronized(this.mutex) {
                if (!isInvalidationListenerAlreadyAdded(listener)) {
                    this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener)
                }
            }
        }

        override fun removeListener(listener: InvalidationListener) {
            synchronized(this.mutex) {
                if (isInvalidationListenerAlreadyAdded(listener)) {
                    this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener)
                }
            }
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.invalidationListeners.contains(listener)
        }

        override fun addListener(listener: MapChangeListener<in K, in V>) {
            synchronized(this.mutex) {
                if (!isMapChangeListenerAlreadyAdded(listener)) {
                    this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener)
                }
            }
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>) {
            synchronized(this.mutex) {
                if (isMapChangeListenerAlreadyAdded(listener)) {
                    this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener)
                }
            }
        }

        override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in K, in V>): Boolean {
            val curHelper = this.listenerHelper
            return curHelper != null && curHelper.mapChangeListeners.contains(listener)
        }

        override fun setAll(vararg pairs: Pair<K, V>) {
            synchronized(this.mutex) {
                val currentEntries = this.entries
                val toRemove = LinkedList<MutableMap.MutableEntry<K, V>>()
                val otherEntries = currentEntries.filter { entry ->
                    val toKeep = pairs.any { pair -> pair.first == entry.key }
                    if (!toKeep) {
                        toRemove.add(entry)
                    }
                    toKeep
                }

                for (entry in toRemove) {
                    remove(entry.key)
                }

                for (pair in pairs) {
                    if (!otherEntries.any { entry -> entry.toPair() == pair }) {
                        put(pair.first, pair.second)
                    }
                }
            }
        }

        override fun setAll(map: Map<out K, V>) {
            synchronized(this.mutex) {
                val newEntries = map.entries

                val currentEntries = this.entries
                val toRemove = LinkedList<MutableMap.MutableEntry<K, V>>()
                val otherEntries = currentEntries.filter { entry ->
                    val toKeep = newEntries.any { newEntry -> newEntry.key == entry.key }
                    if (!toKeep) {
                        toRemove.add(entry)
                    }
                    toKeep
                }

                for (entry in toRemove) {
                    remove(entry.key)
                }

                for (newEntry in newEntries) {
                    if (!otherEntries.any { entry -> entry == newEntry }) {
                        put(newEntry.key, newEntry.value)
                    }
                }
            }
        }

    }

    private class SynchronizedCollection<E>(c: MutableCollection<E>, private val mutex: Any) : MutableCollection<E> {

        private val backingCollection: MutableCollection<E> = c

        constructor(c: MutableCollection<E>) : this(c, Any())

        override val size: Int
            get() = synchronized(this.mutex) { return@synchronized this.backingCollection.size }

        override fun isEmpty(): Boolean {
            return synchronized(this.mutex) {
                this.backingCollection.isEmpty()
            }
        }

        override fun contains(element: E): Boolean {
            return synchronized(this.mutex) {
                this.backingCollection.contains(element)
            }
        }

        override fun iterator(): MutableIterator<E> {
            return this.backingCollection.iterator()
        }

        override fun add(element: E): Boolean {
            return synchronized(this.mutex) {
                this.backingCollection.add(element)
            }
        }

        override fun remove(element: E): Boolean {
            return synchronized(this.mutex) {
                this.backingCollection.remove(element)
            }
        }

        override fun containsAll(elements: Collection<E>): Boolean {
            return synchronized(this.mutex) {
                this.backingCollection.containsAll(elements)
            }
        }

        override fun addAll(elements: Collection<E>): Boolean {
            return synchronized(this.mutex) {
                this.backingCollection.addAll(elements)
            }
        }

        override fun removeAll(elements: Collection<E>): Boolean {
            return synchronized(this.mutex) {
                this.backingCollection.removeAll(elements)
            }
        }

        override fun retainAll(elements: Collection<E>): Boolean {
            return synchronized(this.mutex) {
                this.backingCollection.retainAll(elements)
            }
        }

        override fun clear() {
            synchronized(this.mutex) {
                this.backingCollection.clear()
            }
        }

    }

    private class EmptyObservableArray<T>(override val baseArray: Array<T>) : ObservableArrayBase<T>(),
            ObservableArray<T> {

        private val initialArray: Array<T> = this.baseArray.copyOfRange(0, 0)

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ArrayChangeListener<in T>) {
        }

        override fun removeListener(listener: ArrayChangeListener<in T>) {
        }

        override fun isArrayChangeListenerAlreadyAdded(listener: ArrayChangeListener<in T>): Boolean {
            return false
        }

        override fun resize(size: Int) {
            throw UnsupportedOperationException()
        }

        override fun addAllInternal(src: Array<T>, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override var size: Int
            get() = 0
            set(value) {}

        override fun growCapacity(length: Int) {
            throw UnsupportedOperationException()
        }

        override fun setAllInternal(src: Array<T>, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override fun setAllInternal(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override fun copyInto(destination: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override fun copyInto(destination: ObservableArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override fun ensureCapacity(capacity: Int) {
            throw UnsupportedOperationException()
        }

        override operator fun get(index: Int): T {
            throw ArrayIndexOutOfBoundsException()
        }

        override fun trimToSize() {
            throw UnsupportedOperationException()
        }

        override fun toTypedArray(): Array<T> {
            return this.initialArray.copyOf()
        }

        override fun toTypedArray(startIndex: Int, endIndex: Int): Array<T> {
            rangeCheck(endIndex)
            return this.initialArray.copyOf()
        }

        override fun setInternal(src: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override fun doOperatorSet(index: Int, value: T) {
            throw UnsupportedOperationException()
        }

    }

    @Suppress("JoinDeclarationAndAssignment")
    private class UnmodifiableObservableArrayImpl<T>(private val backingArray: ObservableArray<T>) :
            ObservableArrayBase<T>() {

        private val listener: ArrayChangeListener<in T>

        init {
            this.listener = ArrayChangeListener { change ->
                fireChange(SourceAdapterArrayChange(this@UnmodifiableObservableArrayImpl, change))
            }
            this.backingArray.addListener(this.listener)
        }

        override val baseArray: Array<T>
            get() = this.backingArray.baseArray

        override var size: Int
            get() = this.backingArray.size
            set(value) {}

        override fun copyInto(destination: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.backingArray.copyInto(destination, destinationOffset, startIndex, endIndex)
        }

        override fun copyInto(destination: ObservableArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.backingArray.copyInto(destination, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): T {
            return this.backingArray[index]
        }

        override fun resize(size: Int) {
            throw UnsupportedOperationException()
        }

        override fun trimToSize() {
            throw UnsupportedOperationException()
        }

        override fun addAllInternal(src: Array<T>, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override fun setAllInternal(src: Array<T>, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override fun setAllInternal(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override fun ensureCapacity(capacity: Int) {
            throw UnsupportedOperationException()
        }

        override fun growCapacity(length: Int) {
            throw UnsupportedOperationException()
        }

        override fun toTypedArray(): Array<T> {
            return this.backingArray.toTypedArray()
        }

        override fun toTypedArray(startIndex: Int, endIndex: Int): Array<T> {
            return this.backingArray.toTypedArray(startIndex, endIndex)
        }

        override fun setInternal(src: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            throw UnsupportedOperationException()
        }

        override fun doOperatorSet(index: Int, value: T) {
            throw UnsupportedOperationException()
        }

    }

}