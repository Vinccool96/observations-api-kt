package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.WeakListener
import io.github.vinccool96.observationskt.collections.*
import java.lang.ref.WeakReference

@Suppress("UNCHECKED_CAST")
object ContentBinding {

    private fun checkParameters(property1: Any, property2: Any) {
        require(property1 !== property2) { "Cannot bind object to itself" }
    }

    fun <E> bind(list1: MutableList<E>, list2: ObservableList<out E>): Any {
        checkParameters(list1, list2)
        val binding: ListContentBinding<E> = ListContentBinding(list1)
        if (list1 is ObservableList<E>) {
            list1.setAll(list2)
        } else {
            list1.clear()
            list1.addAll(list2)
        }
        list2.removeListener(binding)
        list2.addListener(binding)
        return binding
    }

    fun <E> bind(set1: MutableSet<E>, set2: ObservableSet<out E>): Any {
        checkParameters(set1, set2)
        val contentBinding: SetContentBinding<E> = SetContentBinding(set1)
        set1.clear()
        set1.addAll(set2)
        set2.removeListener(contentBinding)
        set2.addListener(contentBinding)
        return contentBinding
    }

    fun <K, V> bind(map1: MutableMap<K, V>, map2: ObservableMap<out K, out V>): Any {
        checkParameters(map1, map2)
        val contentBinding = MapContentBinding(map1)
        map1.clear()
        map1.putAll(map2)
        map2.removeListener(contentBinding)
        map2.addListener(contentBinding)
        return contentBinding
    }

    fun unbind(obj1: Any, obj2: Any) {
        checkParameters(obj1, obj2)
        if (obj1 is MutableList<*> && obj2 is ObservableList<*>) {
            obj2.removeListener(ListContentBinding(obj1 as MutableList<Any?>))
        } else if (obj1 is MutableSet<*> && obj2 is ObservableSet<*>) {
            obj2.removeListener(SetContentBinding(obj2 as ObservableSet<Any?>))
        } else if (obj1 is MutableMap<*, *> && obj2 is ObservableMap<*, *>) {
            obj2.removeListener(MapContentBinding(obj1 as MutableMap<Any?, Any?>))
        }
    }

    private class ListContentBinding<E>(list: MutableList<E>) : ListChangeListener<E>, WeakListener {

        private val listRef: WeakReference<MutableList<E>> = WeakReference(list)

        override fun onChanged(change: ListChangeListener.Change<out E>) {
            val list = this.listRef.get()
            if (list == null) {
                change.list.removeListener(this)
            } else {
                while (change.next()) {
                    if (change.wasPermutated) {
                        list.subList(change.from, change.to).clear()
                        list.addAll(change.from, change.list.subList(change.from, change.to))
                    } else {
                        if (change.wasRemoved) {
                            list.subList(change.from, change.from + change.removedSize).clear()
                        }
                        if (change.wasAdded) {
                            list.addAll(change.from, change.addedSubList)
                        }
                    }
                }
            }
        }

        override val wasGarbageCollected: Boolean
            get() = this.listRef.get() == null

        override fun hashCode(): Int {
            return this.listRef.get()?.hashCode() ?: 0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            val list = this.listRef.get() ?: return false

            if (other is ListContentBinding<*>) {
                return list === other.listRef.get()
            }

            return false
        }

    }

    private class SetContentBinding<E>(set: MutableSet<E>) : SetChangeListener<E>, WeakListener {

        private val setRef: WeakReference<MutableSet<E>> = WeakReference(set)

        override fun onChanged(change: SetChangeListener.Change<out E>) {
            val set = this.setRef.get()
            if (set == null) {
                change.set.removeListener(this)
            } else {
                if (change.wasRemoved) {
                    set.remove(change.elementRemoved as E)
                } else {
                    set.add(change.elementAdded as E)
                }
            }
        }

        override val wasGarbageCollected: Boolean
            get() = this.setRef.get() == null

        override fun hashCode(): Int {
            return this.setRef.get()?.hashCode() ?: 0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            val set1 = this.setRef.get() ?: return false

            if (other is SetContentBinding<*>) {
                val set2 = other.setRef.get()
                return set1 == set2
            }

            return false
        }

    }

    private class MapContentBinding<K, V>(map: MutableMap<K, V>) : MapChangeListener<K, V>, WeakListener {

        private val mapRef: WeakReference<MutableMap<K, V>> = WeakReference(map)

        override fun onChanged(change: MapChangeListener.Change<out K, out V>) {
            val map = this.mapRef.get()
            if (map == null) {
                change.map.removeListener(this)
            } else {
                if (change.wasRemoved) {
                    map.remove(change.key)
                } else {
                    map[change.key] = change.valueAdded as V
                }
            }
        }

        override val wasGarbageCollected: Boolean
            get() = this.mapRef.get() == null

        override fun hashCode(): Int {
            return this.mapRef.get()?.hashCode() ?: 0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            val map = this.mapRef.get() ?: return false

            if (other is MapContentBinding<*, *>) {
                return map === other.mapRef.get()
            }

            return false
        }

    }

}