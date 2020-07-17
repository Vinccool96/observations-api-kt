package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.collections.ModifiableObservableListBase
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.NonIterableChange.SimplePermutationChange
import io.github.vinccool96.observationskt.util.Callback

@Suppress("DuplicatedCode")
class ObservableSequentialListWrapper<E> : ModifiableObservableListBase<E>, ObservableList<E>, SortableList<E> {

    private val backingList: MutableList<E>

    private val elementObserver: ElementObserver<E>?

    private lateinit var helper: SortHelper

    constructor(list: MutableList<E>) : super() {
        this.backingList = list
        this.elementObserver = null
    }

    constructor(list: MutableList<E>, extractor: Callback<E, Array<Observable>>) : super() {
        this.backingList = list
        this.elementObserver = ElementObserver(extractor, {param ->
            InvalidationListener {
                beginChange()
                val size = this@ObservableSequentialListWrapper.size
                for (i in 0 until size) {
                    if (this@ObservableSequentialListWrapper[i] == param) {
                        nextUpdate(i)
                    }
                }
                endChange()
            }
        }, this)
        for (e in this.backingList) {
            this.elementObserver.attachListener(e)
        }
    }

    override fun contains(element: E): Boolean {
        return this.backingList.contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return this.backingList.containsAll(elements)
    }

    override fun indexOf(element: E): Int {
        return this.backingList.indexOf(element)
    }

    override fun lastIndexOf(element: E): Int {
        return this.backingList.lastIndexOf(element)
    }

    override fun listIterator(index: Int): MutableListIterator<E> {
        return object : MutableListIterator<E> {

            private val backingIt: MutableListIterator<E> =
                    this@ObservableSequentialListWrapper.backingList.listIterator(index)

            private var lastReturned: E? = null

            override fun hasNext(): Boolean {
                return this.backingIt.hasNext()
            }

            override fun next(): E {
                return this.backingIt.next().also {this.lastReturned = it}
            }

            override fun hasPrevious(): Boolean {
                return this.backingIt.hasPrevious()
            }

            override fun previous(): E {
                return this.backingIt.previous().also {this.lastReturned = it}
            }

            override fun nextIndex(): Int {
                return this.backingIt.nextIndex()
            }

            override fun previousIndex(): Int {
                return this.backingIt.previousIndex()
            }

            override fun remove() {
                beginChange()
                val idx: Int = previousIndex()
                this.backingIt.remove()
                nextRemove(idx, this.lastReturned!!)
                endChange()
            }

            override fun set(element: E) {
                beginChange()
                val idx: Int = previousIndex()
                this.backingIt.set(element)
                nextSet(idx, this.lastReturned!!)
                endChange()
            }

            override fun add(element: E) {
                beginChange()
                val idx: Int = previousIndex()
                this.backingIt.add(element)
                nextAdd(idx, idx + 1)
                endChange()
            }

        }
    }

    override fun iterator(): MutableIterator<E> {
        return listIterator()
    }

    override fun get(index: Int): E {
        try {
            return this.backingList.listIterator(index).next()
        } catch (exc: NoSuchElementException) {
            throw IndexOutOfBoundsException("Index: $index")
        }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        try {
            beginChange()
            var modified = false
            val e1: MutableListIterator<E> = listIterator(index)
            val e2: Iterator<E> = elements.iterator()
            while (e2.hasNext()) {
                e1.add(e2.next())
                modified = true
            }
            endChange()
            return modified
        } catch (exc: NoSuchElementException) {
            throw IndexOutOfBoundsException("Index: $index")
        }
    }

    override val size: Int
        get() = this.backingList.size

    override fun doAdd(index: Int, element: E) {
        try {
            this.backingList.listIterator(index).add(element)
        } catch (exc: NoSuchElementException) {
            throw IndexOutOfBoundsException("Index: $index")
        }
    }

    override fun doSet(index: Int, element: E): E {
        try {
            val e: MutableListIterator<E> = this.backingList.listIterator(index)
            val oldVal: E = e.next()
            e.set(element)
            return oldVal
        } catch (exc: NoSuchElementException) {
            throw IndexOutOfBoundsException("Index: $index")
        }
    }

    override fun doRemove(index: Int): E {
        try {
            val e: MutableListIterator<E> = this.backingList.listIterator(index)
            val outcast: E = e.next()
            e.remove()
            return outcast
        } catch (exc: NoSuchElementException) {
            throw IndexOutOfBoundsException("Index: $index")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun sort() {
        if (this.backingList.isNotEmpty()) {
            val perm: IntArray = this.sortHelper.sort(this.backingList as MutableList<Comparable<Any?>>)
            fireChange(SimplePermutationChange(0, this.size, perm, this))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun sortWith(comparator: Comparator<in E>) {
        if (this.backingList.isNotEmpty()) {
            val perm: IntArray = this.sortHelper.sort(this.backingList as MutableList<Comparable<Any?>>,
                    comparator as Comparator<in Comparable<Any?>>)
            fireChange(SimplePermutationChange(0, this.size, perm, this))
        }
    }

    private val sortHelper: SortHelper
        get() {
            if (!this::helper.isInitialized) {
                this.helper = SortHelper()
            }
            return this.helper
        }

}