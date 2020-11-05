package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.WeakListener
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ObservableList
import java.lang.ref.WeakReference

/**
 *
 */
@Suppress("UNCHECKED_CAST")
object BidirectionalContentBinding {

    private fun checkParameters(property1: Any, property2: Any) {
        require(property1 !== property2) {"Cannot bind object to itself"}
    }

    fun <E> bind(list1: ObservableList<E>, list2: ObservableList<E>): Any {
        checkParameters(list1, list2)
        val binding: ListContentBinding<E> = ListContentBinding(list1, list2)
        list1.setAll(list2)
        list1.addListener(binding)
        list2.addListener(binding)
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

}