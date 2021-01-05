package io.github.vinccool96.observationskt.collections.transformation

import io.github.vinccool96.observationskt.beans.NamedArg
import io.github.vinccool96.observationskt.beans.property.ObjectProperty
import io.github.vinccool96.observationskt.beans.property.ObjectPropertyBase
import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.NonIterableChange.GenericAddRemoveChange
import io.github.vinccool96.observationskt.sun.collections.SortHelper
import java.util.function.Predicate

/**
 * Wraps an ObservableList and filters its content using the provided Predicate. All changes in the ObservableList are
 * propagated immediately to the FilteredList.
 *
 * @see TransformationList
 */
@Suppress("RedundantNullableReturnType", "UNCHECKED_CAST")
class FilteredList<E> : TransformationList<E, E> {

    private var filtered: IntArray

    private var sizeState: Int

    private lateinit var helper: SortHelper

    /**
     * Constructs a new FilteredList wrapper around the source list. The provided predicate will match the elements in
     * the source list that will be visible. If the predicate is `null`, all elements will be matched and the list is
     * equal to the source list.
     *
     * @param source the source list
     * @param predicate the predicate to match the elements or `null` to match all elements.
     */
    constructor(@NamedArg("source") source: ObservableList<E>,
            @NamedArg("predicate") predicate: Predicate<in E>?) : super(source) {
        this.filtered = IntArray(source.size * 3 / 2 + 1)
        this.sizeState = 0
        if (predicate != null) {
            this.predicate = predicate
        } else {
            while (this.sizeState < source.size) {
                this.filtered[this.sizeState] = this.sizeState
                this.sizeState++
            }
        }
    }

    /**
     * Constructs a new FilteredList wrapper around the source list. This list has an "always true" predicate,
     * containing all the elements of the source list.
     *
     * This constructor might be useful if you want to bind [predicateObjectProperty] of this list.
     *
     * @param source the source list
     */
    constructor(@NamedArg("source") source: ObservableList<E>) : this(source, null)

    private val basePredicate: Predicate<in E> = Predicate<E> { true }

    /**
     * The predicate that will match the elements that will be in this FilteredList. Elements not matching the predicate
     * will be filtered-out. Null predicate means "always true" predicate, all elements will be matched.
     */
    private var predicateObjectProperty: ObjectProperty<Predicate<in E>> =
            object : ObjectPropertyBase<Predicate<in E>>(this@FilteredList.basePredicate) {

                override fun invalidated() {
                    refilter()
                }

                override val bean: Any?
                    get() = this@FilteredList

                override val name: String?
                    get() = "predicate"

            }

    val predicateProperty: ObjectProperty<Predicate<in E>>
        get() = this.predicateObjectProperty

    var predicate: Predicate<in E>?
        get() = this.predicateObjectProperty.get()
        set(value) = this.predicateObjectProperty.set(((value) ?: this.basePredicate) as Predicate<in E>)

    private val predicateImpl: Predicate<in E>
        get() = this.predicate!!

    @Suppress("CascadeIf")
    override fun sourceChanged(c: Change<out E>) {
        beginChange()
        while (c.next()) {
            if (c.wasPermutated) {
                permutate(c)
            } else if (c.wasUpdated) {
                update(c)
            } else {
                addRemove(c)
            }
        }
        endChange()
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    override val size: Int
        get() = this.sizeState

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     *
     * @return the element at the specified position in this list
     *
     * @throws IndexOutOfBoundsException if the index is out of range (`index < 0 || index >= size()`)
     */
    override fun get(index: Int): E {
        if (index >= this.sizeState) {
            throw IndexOutOfBoundsException()
        }
        return this.source[this.filtered[index]]
    }

    override fun getSourceIndex(index: Int): Int {
        if (index >= this.sizeState) {
            throw IndexOutOfBoundsException()
        }
        return this.filtered[index]
    }

    private val sortHelper: SortHelper
        get() {
            if (!this::helper.isInitialized) {
                this.helper = SortHelper()
            }
            return this.helper
        }

    private fun findPosition(p: Int): Int {
        if (this.filtered.isEmpty()) {
            return 0
        }
        if (p == 0) {
            return 0
        }
        var pos = this.filtered.binarySearch(p, 0, this.sizeState)
        if (pos < 0) {
            pos = pos.inv()
        }
        return pos
    }

    private fun ensureSize(size: Int) {
        if (this.filtered.size < size) {
            val replacement = IntArray(size * 3 / 2 + 1)
            this.filtered.copyInto(replacement)
            this.filtered = replacement
        }
    }

    private fun updateIndexes(from: Int, delta: Int) {
        for (i in from until this.size) {
            this.filtered[i] += delta
        }
    }

    private fun permutate(c: Change<out E>) {
        val from: Int = findPosition(c.from)
        val to: Int = findPosition(c.to)

        if (to > from) {
            for (i in from until to) {
                this.filtered[i] = c.getPermutation(this.filtered[i])
            }
            val perm: IntArray = this.sortHelper.sort(this.filtered, from, to)
            nextPermutation(from, to, perm)
        }
    }

    private fun addRemove(c: Change<out E>) {
        val pred = this.predicateImpl
        ensureSize(this.source.size)
        val from: Int = findPosition(c.from)
        val to: Int = findPosition(c.from + c.removedSize)

        // Mark the nodes that are going to be removed
        for (i in from until to) {
            nextRemove(from, c.removed[this.filtered[i] - c.from])
        }

        // Update indexes of the sublist following the last element that was removed
        updateIndexes(to, c.addedSize - c.removedSize)

        // Replace as many removed elements as possible
        var fpos: Int = from
        var pos: Int = c.from

        val it: MutableListIterator<out E> = this.source.listIterator(pos)
        while (fpos < to && it.nextIndex() < c.to) {
            if (pred.test(it.next())) {
                this.filtered[fpos] = it.previousIndex()
                nextAdd(fpos, fpos + 1)
                ++fpos
            }
        }

        if (fpos < to) {
            // If there were more removed elements than added
            this.filtered.copyInto(this.filtered, fpos, to)
            this.sizeState -= to - fpos
        } else {
            // Add the remaining elements
            while (it.nextIndex() < c.to) {
                if (pred.test(it.next())) {
                    this.filtered.copyInto(filtered, fpos + 1, fpos, size)
                    this.filtered[fpos] = it.previousIndex()
                    nextAdd(fpos, fpos + 1)
                    ++fpos
                    ++this.sizeState
                }
                ++pos
            }
        }
    }

    private fun update(c: Change<out E>) {
        val pred = this.predicateImpl
        ensureSize(this.source.size)
        var sourceFrom: Int = c.from
        val sourceTo: Int = c.to
        val filterFrom: Int = findPosition(sourceFrom)
        var filterTo: Int = findPosition(sourceTo)
        val it: MutableListIterator<out E> = this.source.listIterator(sourceFrom)
        var pos: Int = filterFrom
        while (pos < filterTo || sourceFrom < sourceTo) {
            val el: E = it.next()
            if (pos < this.sizeState && this.filtered[pos] == sourceFrom) {
                if (!pred.test(el)) {
                    nextRemove(pos, el)
                    this.filtered.copyInto(this.filtered, pos, pos + 1, this.sizeState)
                    --this.sizeState
                    --filterTo
                } else {
                    nextUpdate(pos)
                    ++pos
                }
            } else {
                if (pred.test(el)) {
                    nextAdd(pos, pos + 1)
                    this.filtered.copyInto(this.filtered, pos + 1, pos, this.sizeState)
                    this.filtered[pos] = sourceFrom
                    ++this.sizeState
                    ++pos
                    ++filterTo
                }
            }
            sourceFrom++
        }
    }

    private fun refilter() {
        ensureSize(this.source.size)
        val removed: MutableList<E>? = if (this.hasListeners) ArrayList(this) else null
        this.sizeState = 0
        var i = 0
        val pred = this.predicateImpl
        val it: Iterator<E> = this.source.iterator()
        while (it.hasNext()) {
            val next: E = it.next()
            if (pred.test(next)) {
                this.filtered[this.sizeState++] = i
            }
            ++i
        }
        if (this.hasListeners) {
            fireChange(GenericAddRemoveChange(0, this.sizeState, removed!!, this))
        }
    }

}