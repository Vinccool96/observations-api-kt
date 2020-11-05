package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.WeakListener
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ObservableList
import java.lang.ref.WeakReference

@Suppress("UNCHECKED_CAST")
object ContentBinding {

    private fun checkParameters(property1: Any, property2: Any) {
        require(property1 !== property2) {"Cannot bind object to itself"}
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

    fun unbind(obj1: Any, obj2: Any) {
        checkParameters(obj1, obj2)
        if (obj1 is MutableList<*> && obj2 is ObservableList<*>) {
            obj2.removeListener(ListContentBinding(obj1 as MutableList<Any?>))
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

}