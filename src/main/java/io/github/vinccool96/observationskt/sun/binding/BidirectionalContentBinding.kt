package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.WeakListener
import io.github.vinccool96.observationskt.collections.*
import java.lang.ref.WeakReference

/**
 *
 */
@Suppress("UNCHECKED_CAST")
object BidirectionalContentBinding {

    private fun checkParameters(property1: Any, property2: Any) {
        require(property1 !== property2) { "Cannot bind object to itself" }
    }

    fun <E> bind(list1: ObservableList<E>, list2: ObservableList<E>): Any {
        checkParameters(list1, list2)
        val binding: ListContentBinding<E> = ListContentBinding(list1, list2)
        list1.setAll(list2)
        list1.addListener(binding)
        list2.addListener(binding)
        return binding
    }

    fun <E> bind(set1: ObservableSet<E>, set2: ObservableSet<E>): Any {
        checkParameters(set1, set2)
        val binding = SetContentBinding(set1, set2)
        set1.clear()
        set1.addAll(set2)
        set1.addListener(binding)
        set2.addListener(binding)
        return binding
    }

    fun <K, V> bind(map1: ObservableMap<K, V>, map2: ObservableMap<K, V>): Any {
        checkParameters(map1, map2)
        val binding = MapContentBinding(map1, map2)
        map1.clear()
        map1.putAll(map2)
        map1.addListener(binding)
        map2.addListener(binding)
        return binding
    }

    fun unbind(obj1: Any, obj2: Any) {
        checkParameters(obj1, obj2)
        if (obj1 is ObservableList<*> && obj2 is ObservableList<*>) {
            val list1: ObservableList<Any?> = obj1 as ObservableList<Any?>
            val list2: ObservableList<Any?> = obj2 as ObservableList<Any?>
            val binding: ListContentBinding<Any?> = ListContentBinding(list1, list2)
            list1.removeListener(binding)
            list2.removeListener(binding)
        } else if (obj1 is ObservableSet<*> && obj2 is ObservableSet<*>) {
            val set1: ObservableSet<Any?> = obj1 as ObservableSet<Any?>
            val set2: ObservableSet<Any?> = obj2 as ObservableSet<Any?>
            val binding: SetContentBinding<Any?> = SetContentBinding(set1, set2)
            set1.removeListener(binding)
            set2.removeListener(binding)
        } else if (obj1 is ObservableMap<*, *> && obj2 is ObservableMap<*, *>) {
            val map1: ObservableMap<Any?, Any?> = obj1 as ObservableMap<Any?, Any?>
            val map2: ObservableMap<Any?, Any?> = obj2 as ObservableMap<Any?, Any?>
            val binding: MapContentBinding<Any?, Any?> = MapContentBinding(map1, map2)
            map1.removeListener(binding)
            map2.removeListener(binding)
        }
    }

    private class ListContentBinding<E>(list1: ObservableList<E>, list2: ObservableList<E>) : ListChangeListener<E>,
            WeakListener {

        private val propertyRef1: WeakReference<ObservableList<E>> = WeakReference(list1)

        private val propertyRef2: WeakReference<ObservableList<E>> = WeakReference(list2)

        private var updating: Boolean = false

        override fun onChanged(change: ListChangeListener.Change<out E>) {
            if (!this.updating) {
                val list1 = this.propertyRef1.get()
                val list2 = this.propertyRef2.get()
                if (list1 != null && list2 != null) {
                    try {
                        this.updating = true
                        val dest: ObservableList<E> = if (list1 === change.list) list2 else list1
                        while (change.next()) {
                            if (change.wasPermutated) {
                                dest.remove(change.from, change.to)
                                dest.addAll(change.from, change.list.subList(change.from, change.to))
                            } else {
                                if (change.wasRemoved) {
                                    dest.remove(change.from, change.from + change.removedSize)
                                }
                                if (change.wasAdded) {
                                    dest.addAll(change.from, change.addedSubList)
                                }
                            }
                        }
                    } finally {
                        this.updating = false
                    }
                } else {
                    list1?.removeListener(this)
                    list2?.removeListener(this)
                }
            }
        }

        override val wasGarbageCollected: Boolean
            get() = this.propertyRef1.get() == null || this.propertyRef2.get() == null

        override fun hashCode(): Int {
            val list1 = this.propertyRef1.get()
            val list2 = this.propertyRef2.get()
            val hc1: Int = list1?.hashCode() ?: 0
            val hc2: Int = list2?.hashCode() ?: 0
            return hc1 * hc2
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            val propertyA1 = this.propertyRef1.get()
            val propertyA2 = this.propertyRef2.get()
            if (propertyA1 == null || propertyA2 == null) {
                return false
            }

            if (other is ListContentBinding<*>) {
                val propertyB1 = other.propertyRef1.get()
                val propertyB2 = other.propertyRef2.get()
                if (propertyB1 == null || propertyB2 == null) {
                    return false
                }

                if (propertyA1 === propertyB1 && propertyA2 === propertyB2) {
                    return true
                }
                if (propertyA1 === propertyB2 && propertyA2 === propertyB1) {
                    return true
                }
            }
            return false
        }

    }

    private class SetContentBinding<E>(set1: ObservableSet<E>, set2: ObservableSet<E>) : SetChangeListener<E>,
            WeakListener {

        private val propertyRef1: WeakReference<ObservableSet<E>> = WeakReference(set1)

        private val propertyRef2: WeakReference<ObservableSet<E>> = WeakReference(set2)

        private var updating: Boolean = false

        override fun onChanged(change: SetChangeListener.Change<out E>) {
            if (!this.updating) {
                val set1 = this.propertyRef1.get()
                val set2 = this.propertyRef2.get()
                if (set1 == null || set2 == null) {
                    set1?.removeListener(this)
                    set2?.removeListener(this)
                } else {
                    try {
                        this.updating = true
                        val dest = if (set1 == change.set) set2 else set1
                        if (change.wasRemoved) {
                            dest.remove(change.elementRemoved as E)
                        } else {
                            dest.add(change.elementAdded as E)
                        }
                    } finally {
                        this.updating = false
                    }
                }
            }
        }

        override val wasGarbageCollected: Boolean
            get() = this.propertyRef1.get() == null || this.propertyRef2.get() == null

        override fun hashCode(): Int {
            val hc1 = this.propertyRef1.get()?.hashCode() ?: 0
            val hc2 = this.propertyRef2.get()?.hashCode() ?: 0
            return hc1 * hc2
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            val propertyA1 = this.propertyRef1.get()
            val propertyA2 = this.propertyRef2.get()

            if (propertyA1 == null || propertyA2 == null) {
                return false
            }

            if (other is SetContentBinding<*>) {
                val propertyB1 = other.propertyRef1.get()
                val propertyB2 = other.propertyRef2.get()
                if (propertyB1 == null || propertyB2 == null) {
                    return false
                }

                if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
                    return true
                }
                if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
                    return true
                }
            }

            return false
        }

    }

    private class MapContentBinding<K, V>(map1: ObservableMap<K, V>, map2: ObservableMap<K, V>) :
            MapChangeListener<K, V>, WeakListener {

        private val propertyRef1: WeakReference<ObservableMap<K, V>> = WeakReference(map1)

        private val propertyRef2: WeakReference<ObservableMap<K, V>> = WeakReference(map2)

        private var updating: Boolean = false

        override fun onChanged(change: MapChangeListener.Change<out K, out V>) {
            if (!this.updating) {
                val map1 = this.propertyRef1.get()
                val map2 = this.propertyRef2.get()
                if (map1 != null && map2 != null) {
                    try {
                        this.updating = true
                        val dest = if (map1 == change.map) map2 else map1
                        if (change.wasRemoved) {
                            dest.remove(change.key)
                        } else {
                            dest[change.key] = change.valueAdded as V
                        }
                    } finally {
                        this.updating = false
                    }
                } else {
                    map1?.removeListener(this)
                    map2?.removeListener(this)
                }
            }
        }

        override val wasGarbageCollected: Boolean
            get() = this.propertyRef1.get() == null || this.propertyRef2.get() == null

        override fun hashCode(): Int {
            val map1 = this.propertyRef1.get()
            val map2 = this.propertyRef2.get()
            val hc1 = map1?.hashCode() ?: 0
            val hc2 = map2?.hashCode() ?: 0
            return hc1 * hc2
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }

            val propertyA1 = this.propertyRef1.get()
            val propertyA2 = this.propertyRef2.get()
            if (propertyA1 == null || propertyA2 == null) {
                return false
            }

            if (other is MapContentBinding<*, *>) {
                val propertyB1 = other.propertyRef1.get()
                val propertyB2 = other.propertyRef2.get()
                if (propertyB1 == null || propertyB2 == null) {
                    return false
                }

                if (propertyA1 === propertyB1 && propertyA2 === propertyB2) {
                    return true
                }
                if (propertyA1 === propertyB2 && propertyA2 === propertyB1) {
                    return true
                }
            }
            return false
        }

    }

}