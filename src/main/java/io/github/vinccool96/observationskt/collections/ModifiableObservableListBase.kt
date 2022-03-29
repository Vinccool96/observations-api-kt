package io.github.vinccool96.observationskt.collections

/**
 * Abstract class that serves as a base class for [ObservableList] implementations that are modifiable.
 *
 * To implement a modifiable `ObservableList` class, you just need to implement the following set of methods:
 *
 * * [get]
 * * [size]
 * * [doAdd]
 * * [doRemove]
 * * [doSet]
 *
 * and the notifications and built and fired automatically for you.
 *
 * Example of a simple `ObservableList` delegating to another `List` would look like this:
 *
 * ```
 * class ArrayObservableList<E>: ModifiableObservableList<E>() {
 *
 *     private val delegate: MutableList<E> = ArrayList()
 *
 *     override fun get(int index): E {
 *         return this.delegate[index]
 *     }
 *
 *     override val size: Int
 *         get() = this.delegate.size
 *
 *     override fun doAdd(index: Int, element: E) {
 *         this.delegate.add(index, element)
 *     }
 *
 *     override fun doSet(index: Int, element: E): E {
 *         return this.delegate[index] = element
 *     }
 *
 *     override fun doRemove(index: Int): E {
 *         return this.delegate.remove(index)
 *     }
 *
 *     ...
 *
 * }
 * ```
 *
 * @param E the type of the elements contained in the List
 *
 * @see ObservableListBase
 */
abstract class ModifiableObservableListBase<E> : ObservableListBase<E>() {

    override fun setAll(col: Collection<E>): Boolean {
        if (col === this) {
            return setAll(col.toList())
        }
        beginChange()
        try {
            clear()
            addAll(col)
        } finally {
            endChange()
        }
        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        if (elements === this) {
            return addAll(elements.toList())
        }
        beginChange()
        try {
            return super.addAll(elements)
        } finally {
            endChange()
        }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        beginChange()
        try {
            return super.addAll(index, elements)
        } finally {
            endChange()
        }
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        beginChange()
        try {
            super.removeRange(fromIndex, toIndex)
        } finally {
            endChange()
        }
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        beginChange()
        try {
            return super.removeAll(elements)
        } finally {
            endChange()
        }
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        beginChange()
        try {
            return super.retainAll(elements)
        } finally {
            endChange()
        }
    }

    override fun add(index: Int, element: E) {
        doAdd(index, element)
        beginChange()
        nextAdd(index, index + 1)
        this.modCount++
        endChange()
    }

    override fun set(index: Int, element: E): E {
        val old: E = doSet(index, element)
        beginChange()
        nextSet(index, old)
        endChange()
        return old
    }

    override fun remove(element: E): Boolean {
        val i = indexOf(element)
        if (i != -1) {
            removeAt(i)
            return true
        }
        return false
    }

    override fun removeAt(index: Int): E {
        val old: E = doRemove(index)
        beginChange()
        nextRemove(index, old)
        this.modCount++
        endChange()
        return old
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        return SubObservableList(super.subList(fromIndex, toIndex), this)
    }

    abstract override fun get(index: Int): E

    abstract override val size: Int

    /**
     * Adds the `element` to the List at the position of `index`.
     *
     * For the description of possible exceptions, please refer to the documentation of [add] method.
     *
     * @param index the position where to add the element
     * @param element the element that will be added
     *
     * @throws ClassCastException if the class of the specified element prevents it from being added to this list
     * @throws NullPointerException if the specified element is null and this list does not permit null elements
     * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this
     * list
     * @throws IndexOutOfBoundsException if the index is out of range (`index < 0 || index >= size`)
     */
    protected abstract fun doAdd(index: Int, element: E)

    /**
     * Sets the `element` in the List at the position of `index`.
     *
     *
     * For the description of possible exceptions, please refer to the documentation of [set] method.
     *
     * @param index the position where to set the element
     * @param element the element that will be set at the specified position
     *
     * @return the old element at the specified position
     *
     * @throws ClassCastException if the class of the specified element prevents it from being added to this list
     * @throws NullPointerException if the specified element is null and this list does not permit null elements
     * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this
     * list
     * @throws IndexOutOfBoundsException if the index is out of range (`index < 0 || index >= size`)
     */
    protected abstract fun doSet(index: Int, element: E): E

    /**
     * Removes the element at position of `index`.
     *
     * @param index the index of the removed element
     *
     * @return the removed element
     *
     * @throws IndexOutOfBoundsException if the index is out of range (`index < 0 || index >= size`)
     */
    protected abstract fun doRemove(index: Int): E

    private class SubObservableList<E>(private val sublist: MutableList<E>,
            private val base: ModifiableObservableListBase<E>) : MutableList<E> {

        override val size: Int
            get() = this.sublist.size

        override fun isEmpty(): Boolean {
            return this.sublist.isEmpty()
        }

        override operator fun contains(element: E): Boolean {
            return this.sublist.contains(element)
        }

        override fun iterator(): MutableIterator<E> {
            return this.sublist.iterator()
        }

        override fun add(element: E): Boolean {
            return this.sublist.add(element)
        }

        override fun remove(element: E): Boolean {
            return this.sublist.remove(element)
        }

        override fun containsAll(elements: Collection<E>): Boolean {
            return this.sublist.containsAll(elements)
        }

        override fun addAll(elements: Collection<E>): Boolean {
            this.base.beginChange()
            return try {
                sublist.addAll(elements)
            } finally {
                this.base.endChange()
            }
        }

        override fun addAll(index: Int, elements: Collection<E>): Boolean {
            this.base.beginChange()
            return try {
                sublist.addAll(index, elements)
            } finally {
                this.base.endChange()
            }
        }

        override fun removeAll(elements: Collection<E>): Boolean {
            this.base.beginChange()
            return try {
                sublist.removeAll(elements)
            } finally {
                this.base.endChange()
            }
        }

        override fun retainAll(elements: Collection<E>): Boolean {
            this.base.beginChange()
            return try {
                sublist.retainAll(elements)
            } finally {
                this.base.endChange()
            }
        }

        override fun clear() {
            this.base.beginChange()
            try {
                sublist.clear()
            } finally {
                this.base.endChange()
            }
        }

        override fun get(index: Int): E {
            return this.sublist[index]
        }

        override fun set(index: Int, element: E): E {
            return this.sublist.set(index, element)
        }

        override fun add(index: Int, element: E) {
            sublist.add(index, element)
        }

        override fun removeAt(index: Int): E {
            return this.sublist.removeAt(index)
        }

        override fun indexOf(element: E): Int {
            return this.sublist.indexOf(element)
        }

        override fun lastIndexOf(element: E): Int {
            return this.sublist.lastIndexOf(element)
        }

        override fun listIterator(): MutableListIterator<E> {
            return this.sublist.listIterator()
        }

        override fun listIterator(index: Int): MutableListIterator<E> {
            return this.sublist.listIterator(index)
        }

        override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
            return SubObservableList(sublist.subList(fromIndex, toIndex), this.base)
        }

        override fun equals(other: Any?): Boolean {
            return this.sublist == other
        }

        override fun hashCode(): Int {
            return this.sublist.hashCode()
        }

        override fun toString(): String {
            return this.sublist.toString()
        }

    }

}