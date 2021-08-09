package io.github.vinccool96.observationskt.collections.transformation

import io.github.vinccool96.observationskt.beans.NamedArg
import io.github.vinccool96.observationskt.beans.property.ObjectProperty
import io.github.vinccool96.observationskt.beans.property.ObjectPropertyBase
import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.NonIterableChange.SimplePermutationChange
import io.github.vinccool96.observationskt.sun.collections.SortHelper
import io.github.vinccool96.observationskt.sun.collections.SourceAdapterChange

/**
 * Wraps an ObservableList and sorts its content. All changes in the ObservableList are propagated immediately to the
 * SortedList.
 *
 * Note: invalid SortedList (as a result of broken comparison) doesn't send any notification to listeners on becoming
 * valid again.
 *
 * @see TransformationList
 */
@Suppress("UNCHECKED_CAST")
open class SortedList<E> : TransformationList<E, E> {

    private var elementComparator: Comparator<Element<E>>? = null

    private var sorted: Array<Element<E>?>

    private var perm: IntArray

    private var sizeState = 0

    private val helper = SortHelper()

    private lateinit var tempElement: Element<E>

    /**
     * Creates a new SortedList wrapped around the source list. The source list will be sorted using the comparator
     * provided. If `null` is provided, the list stays unordered and is equal to the source list.
     *
     * @param source a list to wrap
     * @param comparator a comparator to use or `null` for unordered List
     */
    constructor(@NamedArg("source") source: ObservableList<E>, @NamedArg("comparator") comparator: Comparator<in E>?) :
            super(source) {
        this.sorted = arrayOfNulls<Element<E>?>(source.size * 3 / 2 + 1)
        this.perm = IntArray(sorted.size)
        this.sizeState = source.size
        for (i in this.indices) {
            this.sorted[i] = Element(source[i], i)
            this.perm[i] = i
        }
        if (comparator != null) {
            this.comparator = comparator
        }
    }

    /**
     * Constructs a new unordered SortedList wrapper around the source list.
     *
     * @param source the source list
     */
    constructor(@NamedArg("source") source: ObservableList<E>) : this(source, null)

    @Suppress("CascadeIf")
    override fun sourceChanged(c: Change<out E>) {
        if (this.elementComparator != null) {
            beginChange()
            while (c.next()) {
                if (c.wasPermutated) {
                    updatePermutationIndexes(c)
                } else if (c.wasUpdated) {
                    update(c)
                } else {
                    addRemove(c)
                }
            }
            endChange()
        } else {
            updateUnsorted(c)
            fireChange(SourceAdapterChange(this, c))
        }
    }

    private val baseComparator: Comparator<in E> = Comparator {_, _ -> 0}

    /**
     * The comparator that denotes the order of this SortedList. Null for unordered SortedList.
     */
    private var comparatorObjectProperty: ObjectProperty<Comparator<in E>?> =
            object : ObjectPropertyBase<Comparator<in E>?>(this@SortedList.baseComparator) {

                override fun invalidated() {
                    val current: Comparator<in E>? = this.get()
                    this@SortedList.elementComparator =
                            if (current != null) ElementComparator(current) as Comparator<Element<E>> else null
                    doSortWithPermutationChange()
                }

                override val bean: Any
                    get() = this@SortedList

                override val name: String
                    get() = "comparator"

            }

    val comparatorProperty: ObjectProperty<Comparator<in E>?>
        get() = this.comparatorObjectProperty

    var comparator: Comparator<in E>?
        get() = this.comparatorObjectProperty.get() ?: this.baseComparator
        set(value) = this.comparatorProperty.set(value)

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     *
     * @return the element at the specified position in this list
     *
     * @throws IndexOutOfBoundsException
     * if the index is out of range (`index < 0 || index >= size()`)
     */
    override fun get(index: Int): E {
        if (index >= this.sizeState) {
            throw IndexOutOfBoundsException()
        }
        return this.sorted[index]!!.e
    }

    override val size: Int
        get() = this.sizeState

    private fun doSortWithPermutationChange() {
        if (elementComparator != null) {
            val perm = helper.sort(this.sorted, 0, this.sizeState, this.elementComparator as Comparator<in Element<E>?>)
            for (i in this.indices) {
                this.perm[this.sorted[i]!!.index] = i
            }
            fireChange(SimplePermutationChange(0, size, perm, this))
        } else {
            val perm = IntArray(this.sizeState)
            val rperm = IntArray(this.sizeState)
            for (i in this.indices) {
                rperm[i] = i
                perm[i] = rperm[i]
            }
            var changed = false
            var idx = 0
            while (idx < this.sizeState) {
                val otherIdx = this.sorted[idx]!!.index
                if (otherIdx == idx) {
                    ++idx
                    continue
                }
                val other = this.sorted[otherIdx]
                this.sorted[otherIdx] = this.sorted[idx]
                this.sorted[idx] = other
                this.perm[idx] = idx
                this.perm[otherIdx] = otherIdx
                perm[rperm[idx]] = otherIdx
                perm[rperm[otherIdx]] = idx
                val tp = rperm[idx]
                rperm[idx] = rperm[otherIdx]
                rperm[otherIdx] = tp
                changed = true
            }
            if (changed) {
                fireChange(SimplePermutationChange(0, this.sizeState, perm, this))
            }
        }
    }

    override fun getSourceIndex(index: Int): Int {
        return this.sorted[index]!!.index
    }

    private fun updatePermutationIndexes(change: Change<out E>) {
        for (i in 0 until size) {
            val p = change.getPermutation(this.sorted[i]!!.index)
            this.sorted[i]!!.index = p
            this.perm[p] = i
        }
    }

    private fun updateUnsorted(c: Change<out E>) {
        while (c.next()) {
            if (c.wasPermutated) {
                val sortedTmp = arrayOfNulls<Element<E>?>(this.sorted.size)
                for (i in this.indices) {
                    if (i >= c.from && i < c.to) {
                        val p = c.getPermutation(i)
                        sortedTmp[p] = this.sorted[i]
                        sortedTmp[p]!!.index = p
                        this.perm[i] = i
                    } else {
                        sortedTmp[i] = sorted[i]
                    }
                }
                this.sorted = sortedTmp
            }
            if (c.wasRemoved) {
                val removedTo = c.from + c.removedSize
                this.sorted.copyInto(this.sorted, c.from, removedTo, this.sizeState)
                this.perm.copyInto(this.perm, c.from, removedTo, this.sizeState)
                this.sizeState -= c.removedSize
                updateIndices(removedTo, removedTo, -c.removedSize)
            }
            if (c.wasAdded) {
                ensureSize(size + c.addedSize)
                updateIndices(c.from, c.from, c.addedSize)
                this.sorted.copyInto(this.sorted, c.to, c.from, this.sizeState)
                this.perm.copyInto(this.perm, c.to, c.from, this.sizeState)
                this.sizeState += c.addedSize
                for (i in c.from until c.to) {
                    this.sorted[i] = Element(c.list[i], i)
                    this.perm[i] = i
                }
            }
        }
    }

    private class Element<E>(var e: E, var index: Int)

    private class ElementComparator<E>(val comparator: Comparator<E>) : Comparator<Element<E>> {

        override fun compare(o1: Element<E>, o2: Element<E>): Int {
            return this.comparator.compare(o1.e, o2.e)
        }

    }

    private fun ensureSize(size: Int) {
        if (this.sorted.size < size) {
            this.sorted = this.sorted.copyOf(size * 3 / 2 + 1)
            this.perm = this.perm.copyOf(size * 3 / 2 + 1)
        }
    }

    private fun updateIndices(from: Int, viewFrom: Int, difference: Int) {
        for (i in this.indices) {
            if (this.sorted[i]!!.index >= from) {
                this.sorted[i]!!.index += difference
            }
            if (this.perm[i] >= viewFrom) {
                this.perm[i] += difference
            }
        }
    }

    private fun findPosition(e: E): Int {
        if (this.sorted.isEmpty()) {
            return 0
        }
        this.tempElement = if (!this::tempElement.isInitialized) Element(e, -1) else Element(e, this.tempElement.index)
        return this.sorted.binarySearch(this.tempElement, this.elementComparator!! as Comparator<in Element<E>?>, 0,
                this.sizeState)
    }

    private fun insertToMapping(e: E, idx: Int) {
        var pos = findPosition(e)
        if (pos < 0) {
            pos = pos.inv()
        }
        ensureSize(this.sizeState + 1)
        updateIndices(idx, pos, 1)
        this.sorted.copyInto(this.sorted, pos + 1, pos, this.sizeState)
        this.sorted[pos] = Element(e, idx)
        this.perm.copyInto(this.perm, idx + 1, idx, this.sizeState)
        this.perm[idx] = pos
        ++this.sizeState
        nextAdd(pos, pos + 1)
    }

    private fun setAllToMapping(list: List<E>, to: Int) {
        ensureSize(to)
        this.sizeState = to
        for (i in 0 until to) {
            this.sorted[i] = Element(list[i], i)
        }
        val perm =
                this.helper.sort(this.sorted, 0, this.sizeState, this.elementComparator as Comparator<in Element<E>?>)
        perm.copyInto(this.perm)
        nextAdd(0, this.sizeState)
    }

    private fun removeFromMapping(idx: Int, e: E) {
        val pos = this.perm[idx]
        this.sorted.copyInto(this.sorted, pos, pos + 1)
        this.perm.copyInto(this.perm, idx, idx + 1)
        --this.sizeState
        this.sorted[this.sizeState] = null
        updateIndices(idx + 1, pos, -1)
        nextRemove(pos, e)
    }

    private fun removeAllFromMapping() {
        val removed: MutableList<E> = ArrayList(this)
        for (i in this.indices) {
            sorted[i] = null
        }
        this.sizeState = 0
        nextRemove(0, removed)
    }

    private fun update(c: Change<out E>) {
        val perm = helper.sort(this.sorted, 0, size, this.elementComparator as Comparator<in Element<E>?>)
        for (i in this.indices) {
            this.perm[this.sorted[i]!!.index] = i
        }
        nextPermutation(0, this.sizeState, perm)
        var i = c.from
        val to = c.to
        while (i < to) {
            nextUpdate(this.perm[i])
            ++i
        }
    }

    private fun addRemove(c: Change<out E>) {
        if (c.from == 0 && c.removedSize == this.sizeState) {
            removeAllFromMapping()
        } else {
            var i = 0
            val sz = c.removedSize
            while (i < sz) {
                removeFromMapping(c.from, c.removed[i])
                ++i
            }
        }
        if (this.sizeState == 0) {
            setAllToMapping(c.list, c.to) // This is basically equivalent to getAddedSubList
            // as size is 0, only valid "from" is also 0
        } else {
            var i = c.from
            val to = c.to
            while (i < to) {
                insertToMapping(c.list[i], i)
                ++i
            }
        }
    }

}