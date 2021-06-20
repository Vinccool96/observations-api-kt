package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableListBase
import io.github.vinccool96.observationskt.collections.WeakListChangeListener
import io.github.vinccool96.observationskt.util.Callback

class ElementObservableListDecorator<E>(private val decoratedList: ObservableList<E>,
        extractor: Callback<E, Array<Observable>>) : ObservableListBase<E>(), ObservableList<E> {

    private val listener: ListChangeListener<E> = ListChangeListener { change ->
        while (change.next()) {
            if (change.wasAdded || change.wasRemoved) {
                val removedSize = change.removedSize
                val removed = change.removed
                for (i in 0 until removedSize) {
                    this@ElementObservableListDecorator.observer.detachListener(removed[i])
                }
                if (this@ElementObservableListDecorator.decoratedList is RandomAccess) {
                    val to = change.to
                    for (i in change.from until to) {
                        this@ElementObservableListDecorator.observer.attachListener(
                                this@ElementObservableListDecorator.decoratedList[i])
                    }
                } else {
                    for (e in change.addedSubList)
                        this@ElementObservableListDecorator.observer.attachListener(e)
                }
            }
        }
        change.reset()
        fireChange(change)
    }

    private val observer: ElementObserver<E> = ElementObserver(extractor, Callback { param: E ->
        return@Callback InvalidationListener {
            beginChange()
            var i = 0
            if (this@ElementObservableListDecorator.decoratedList is RandomAccess) {
                val size = this.size
                while (i < size) {
                    if (this@ElementObservableListDecorator[i] === param) {
                        nextUpdate(i)
                    }
                    i++
                }
            } else {
                val itr = iterator()
                while (itr.hasNext()) {
                    if (itr.next() === param) {
                        nextUpdate(i)
                    }
                    i++
                }
            }
            endChange()
        }
    }, this)

    init {
        for (value in this.decoratedList) {
            this.observer.attachListener(value)
        }
        this.decoratedList.addListener(WeakListChangeListener(this.listener))
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        return this.decoratedList.subList(fromIndex, toIndex)
    }

    override val size: Int
        get() = this.decoratedList.size

    override fun set(index: Int, element: E): E {
        return this.decoratedList.set(index, element)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return this.decoratedList.retainAll(elements)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return this.decoratedList.removeAll(elements)
    }

    override fun removeAt(index: Int): E {
        return this.decoratedList.removeAt(index)
    }

    override fun remove(element: E): Boolean {
        return this.decoratedList.remove(element)
    }

    override fun listIterator(index: Int): MutableListIterator<E> {
        return this.decoratedList.listIterator(index)
    }

    override fun listIterator(): MutableListIterator<E> {
        return this.decoratedList.listIterator()
    }

    override fun lastIndexOf(element: E): Int {
        return this.decoratedList.lastIndexOf(element)
    }

    override fun iterator(): MutableIterator<E> {
        return this.decoratedList.iterator()
    }

    override fun isEmpty(): Boolean {
        return this.decoratedList.isEmpty()
    }

    override fun indexOf(element: E): Int {
        return this.decoratedList.indexOf(element)
    }

    override fun get(index: Int): E {
        return this.decoratedList[index]
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return this.decoratedList.containsAll(elements)
    }

    override fun contains(element: E): Boolean {
        return this.decoratedList.contains(element)
    }

    override fun clear() {
        this.decoratedList.clear()
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        return this.decoratedList.addAll(index, elements)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return this.decoratedList.addAll(elements)
    }

    override fun add(index: Int, element: E) {
        this.decoratedList.add(index, element)
    }

    override fun add(element: E): Boolean {
        return this.decoratedList.add(element)
    }

    override fun setAll(col: Collection<E>): Boolean {
        return this.decoratedList.setAll(col)
    }

    override fun setAll(vararg elements: E): Boolean {
        return this.decoratedList.setAll(*elements)
    }

    override fun retainAll(vararg elements: E): Boolean {
        return this.decoratedList.retainAll(*elements)
    }

    override fun removeAll(vararg elements: E): Boolean {
        return this.decoratedList.removeAll(*elements)
    }

    override fun remove(from: Int, to: Int) {
        this.decoratedList.remove(from, to)
    }

    override fun addAll(vararg elements: E): Boolean {
        return this.decoratedList.addAll(*elements)
    }

}