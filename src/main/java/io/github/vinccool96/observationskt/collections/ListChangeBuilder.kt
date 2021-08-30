package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.sun.collections.ChangeHelper.addRemoveChangeToString
import io.github.vinccool96.observationskt.sun.collections.ChangeHelper.permChangeToString
import io.github.vinccool96.observationskt.sun.collections.ChangeHelper.updateChangeToString
import java.util.*

@Suppress("SENSELESS_COMPARISON", "NAME_SHADOWING", "CascadeIf", "UNCHECKED_CAST",
        "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
internal class ListChangeBuilder<E> internal constructor(private val list: ObservableListBase<E>) {

    private var changeLock: Int = 0

    private val addRemoveChanges: MutableList<SubChange<E>>

    private var updateChanges: MutableList<SubChange<E>>

    private var permutationChange: SubChange<E>?

    private fun checkState() {
        if (this.changeLock == 0) {
            throw IllegalStateException("beginChange was not called on this builder")
        }
    }

    private fun findSubChange(idx: Int, list: MutableList<SubChange<E>>): Int {
        var from = 0
        var to = list.size - 1
        while (from <= to) {
            val changeIdx = (from + to) / 2
            val change = list[changeIdx]
            if (idx >= change.to) {
                from = changeIdx + 1
            } else if (idx < change.from) {
                to = changeIdx - 1
            } else {
                return changeIdx
            }
        }
        return from.inv()
    }

    private fun insertUpdate(pos: Int) {
        var idx = findSubChange(pos, this.updateChanges)
        if (idx < 0) { //If not found
            idx = idx.inv()
            lateinit var change: SubChange<E>
            if (idx > 0 && (this.updateChanges[idx - 1].also { change = it }).to == pos) {
                change.to = pos + 1
            } else if (idx < this.updateChanges.size && (this.updateChanges[idx].also {
                        change = it
                    }).from == pos + 1) {
                change.from = pos
            } else {
                this.updateChanges.add(idx, SubChange(pos, pos + 1, null, EMPTY_PERM, true))
            }
        } // If found, no need to do another update
    }

    private fun insertRemoved(pos: Int, removed: E) {
        var idx = findSubChange(pos, this.addRemoveChanges)
        if (idx < 0) { // Not found
            idx = idx.inv()
            lateinit var change: SubChange<E>
            if (idx > 0 && this.addRemoveChanges[idx - 1].also { change = it }.to == pos) {
                change.removed!!.add(removed)
                --idx // Idx index will be used as a starting point for update
            } else if (idx < this.addRemoveChanges.size && this.addRemoveChanges[idx].also {
                        change = it
                    }.from == pos + 1) {
                change.from--
                change.to--
                change.removed!!.add(0, removed)
            } else {
                val removedList = ArrayList<E>()
                removedList.add(removed)
                this.addRemoveChanges.add(idx, SubChange(pos, pos, removedList, EMPTY_PERM, false))
            }
        } else {
            val change = this.addRemoveChanges[idx]
            change.to-- // Removed one element from the previously added list
            if (change.from == change.to && (change.removed == null || change.removed!!.isEmpty())) {
                this.addRemoveChanges.removeAt(idx)
            }
        }
        for (i in idx + 1 until this.addRemoveChanges.size) {
            val change = this.addRemoveChanges[i]
            change.from--
            change.to--
        }
    }

    private fun insertAdd(from: Int, to: Int) {
        var idx = findSubChange(from, this.addRemoveChanges)
        val numberOfAdded = to - from
        if (idx < 0) { // Not found
            idx = idx.inv()
            lateinit var change: SubChange<E>
            if (idx > 0 && this.addRemoveChanges[idx - 1].also { change = it }.to == from) {
                change.to = to
                --idx
            } else {
                this.addRemoveChanges.add(idx, SubChange(from, to, ArrayList(), EMPTY_PERM, false))
            }
        } else {
            val change = this.addRemoveChanges[idx]
            change.to += numberOfAdded
        }
        for (i in idx + 1 until this.addRemoveChanges.size) {
            val change = this.addRemoveChanges[i]
            change.from += numberOfAdded
            change.to += numberOfAdded
        }
    }

    private fun compress(list: MutableList<SubChange<E>?>): Int {
        var removed = 0
        var prev = list[0]
        val sz = list.size
        for (i in 1 until sz) {
            val cur = list[i]
            if (prev!!.to == cur!!.from) {
                prev.to = cur.to
                if (prev.removed != null || cur.removed != null) {
                    if (prev.removed == null) {
                        prev.removed = ArrayList()
                    }
                    prev.removed!!.addAll(cur.removed!!)
                }
                list[i] = null
                ++removed
            } else {
                prev = cur
            }
        }
        return removed
    }

    private class SubChange<E>(var from: Int, var to: Int, var removed: MutableList<E>?, var perm: IntArray,
            var updated: Boolean)

    init {
        this.addRemoveChanges = ArrayList()
        this.updateChanges = ArrayList()
        this.permutationChange = null
    }

    fun nextRemove(idx: Int, removed: E) {
        checkState()
        val last = if (this.addRemoveChanges.isEmpty()) null else this.addRemoveChanges[this.addRemoveChanges.size - 1]
        if (last != null && last.to == idx) {
            last.removed!!.add(removed)
        } else if (last != null && last.from == idx + 1) {
            last.from--
            last.to--
            last.removed!!.add(0, removed)
        } else {
            insertRemoved(idx, removed)
        }
        if (this.updateChanges != null && this.updateChanges.isNotEmpty()) {
            var uPos = findSubChange(idx, this.updateChanges)
            if (uPos < 0) {
                uPos = uPos.inv()
            } else {
                val change = this.updateChanges[uPos]
                if (change.from == change.to - 1) {
                    this.updateChanges.removeAt(uPos)
                } else {
                    change.to--
                    ++uPos // Do the update from the next position
                }
            }
            for (i in uPos until this.updateChanges.size) {
                this.updateChanges[i].from--
                this.updateChanges[i].to--
            }
        }
    }

    fun nextRemove(idx: Int, removed: List<E>) {
        checkState()
        for (i in removed.indices) {
            nextRemove(idx, removed[i])
        }
    }

    fun nextAdd(from: Int, to: Int) {
        checkState()
        val last = if (this.addRemoveChanges.isEmpty()) null else this.addRemoveChanges[this.addRemoveChanges.size - 1]
        val numberOfAdded = to - from
        if (last != null && last.to == from) {
            last.to = to
        } else if (last != null && from >= last.from && from < last.to) { // Adding to the middle
            last.to += numberOfAdded
        } else {
            insertAdd(from, to)
        }
        if (this.updateChanges != null && this.updateChanges.isNotEmpty()) {
            var uPos = findSubChange(from, this.updateChanges)
            if (uPos < 0) {
                uPos = uPos.inv()
            } else {
                // We have to split the change into 2
                val change = this.updateChanges[uPos]
                this.updateChanges.add(uPos + 1, SubChange(to, change.to + to - from, null, EMPTY_PERM, true))
                change.to = from
                uPos += 2 // skip those 2 for the update
            }
            for (i in uPos until this.updateChanges.size) {
                this.updateChanges[i].from += numberOfAdded
                this.updateChanges[i].to += numberOfAdded
            }
        }
    }

    fun nextPermutation(from: Int, to: Int, perm: IntArray) {
        checkState()
        var prePermFrom = from
        var prePermTo = to
        var prePerm = perm
        if (this.addRemoveChanges != null && this.addRemoveChanges.isNotEmpty()) {
            //Because there were already some changes to the list, we need
            // to "reconstruct" the original list and create a permutation
            // as-if there were no changes to the list. We can then
            // merge this with the permutation we already did

            // This maps elements from current list to the original list.
            // -1 means the map was not in the original list.
            // Note that for performance reasons, the map is permutated when created
            // by the permutation. So it basically contains the order in which the original
            // items were permutated by our new permutation.
            val mapToOriginal = IntArray(this.list.size)
            // Marks the original-list indexes that were removed
            val removed: MutableSet<Int> = TreeSet()
            var last = 0
            var offset = 0
            run {
                var i = 0
                val sz = this.addRemoveChanges.size
                while (i < sz) {
                    val change = this.addRemoveChanges[i]
                    for (j in last until change.from) {
                        mapToOriginal[if (j < from || j >= to) j else perm[j - from]] = j + offset
                    }
                    for (j in change.from until change.to) {
                        mapToOriginal[if (j < from || j >= to) j else perm[j - from]] = -1
                    }
                    last = change.to
                    val removedSize = if (change.removed != null) change.removed!!.size else 0
                    var j = change.from + offset
                    val upTo = change.from + offset + removedSize
                    while (j < upTo) {
                        removed.add(j)
                        ++j
                    }
                    offset += removedSize - (change.to - change.from)
                    ++i
                }
            }
            // from the last add/remove change to the end of the list
            for (i in last until mapToOriginal.size) {
                mapToOriginal[if (i < from || i >= to) i else perm[i - from]] = i + offset
            }
            val newPerm = IntArray(list.size + offset)
            var mapPtr = 0
            for (i in newPerm.indices) {
                if (removed.contains(i)) {
                    newPerm[i] = i
                } else {
                    while (mapToOriginal[mapPtr] == -1) {
                        mapPtr++
                    }
                    newPerm[mapToOriginal[mapPtr++]] = i
                }
            }

            // We could theoretically find the first and last items such that
            // newPerm[i] != i and trim the permutation, but it is not necessary
            prePermFrom = 0
            prePermTo = newPerm.size
            prePerm = newPerm
        }
        if (this.permutationChange != null) {
            if (prePermFrom == this.permutationChange!!.from && prePermTo == this.permutationChange!!.to) {
                for (i in prePerm.indices) {
                    this.permutationChange!!.perm[i] = prePerm[this.permutationChange!!.perm[i] - prePermFrom]
                }
            } else {
                val newTo = this.permutationChange!!.to.coerceAtLeast(prePermTo)
                val newFrom = this.permutationChange!!.from.coerceAtMost(prePermFrom)
                val newPerm = IntArray(newTo - newFrom)
                for (i in newFrom until newTo) {
                    if (i < this.permutationChange!!.from || i >= this.permutationChange!!.to) {
                        newPerm[i - newFrom] = prePerm[i - prePermFrom]
                    } else {
                        val p = this.permutationChange!!.perm[i - this.permutationChange!!.from]
                        if (p < prePermFrom || p >= prePermTo) {
                            newPerm[i - newFrom] = p
                        } else {
                            newPerm[i - newFrom] = prePerm[p - prePermFrom]
                        }
                    }
                }
                this.permutationChange!!.from = newFrom
                this.permutationChange!!.to = newTo
                this.permutationChange!!.perm = newPerm
            }
        } else {
            this.permutationChange = SubChange(prePermFrom, prePermTo, null, prePerm, false)
        }
        if (this.addRemoveChanges != null && this.addRemoveChanges.isNotEmpty()) {
            val newAdded: MutableSet<Int> = TreeSet()
            val newRemoved: MutableMap<Int, MutableList<E>?> = HashMap()
            var i = 0
            val sz = this.addRemoveChanges.size
            while (i < sz) {
                val change = this.addRemoveChanges[i]
                for (cIndex in change.from until change.to) {
                    if (cIndex < from || cIndex >= to) {
                        newAdded.add(cIndex)
                    } else {
                        newAdded.add(perm[cIndex - from])
                    }
                }
                if (change.removed != null) {
                    if (change.from < from || change.from >= to) {
                        newRemoved[change.from] = change.removed
                    } else {
                        newRemoved[perm[change.from - from]] = change.removed
                    }
                }
                ++i
            }
            this.addRemoveChanges.clear()
            var lastChange: SubChange<E>? = null
            for (i in newAdded) {
                if (lastChange == null || lastChange.to != i) {
                    lastChange = SubChange(i, i + 1, null, EMPTY_PERM, false)
                    this.addRemoveChanges.add(lastChange)
                } else {
                    lastChange.to = i + 1
                }
                val removed = newRemoved.remove(i)
                if (removed != null) {
                    if (lastChange.removed != null) {
                        lastChange.removed!!.addAll(removed)
                    } else {
                        lastChange.removed = removed
                    }
                }
            }
            for ((at, value) in newRemoved) {
                val idx = findSubChange(at, this.addRemoveChanges)
                assert(idx < 0)
                this.addRemoveChanges.add(idx.inv(), SubChange(at, at, value, IntArray(0), false))
            }
        }
        if (this.updateChanges != null && this.updateChanges.isNotEmpty()) {
            val newUpdated: MutableSet<Int> = TreeSet()
            var i = 0
            val sz = this.updateChanges.size
            while (i < sz) {
                val change = this.updateChanges[i]
                for (cIndex in change.from until change.to) {
                    if (cIndex < from || cIndex >= to) {
                        newUpdated.add(cIndex)
                    } else {
                        newUpdated.add(perm[cIndex - from])
                    }
                }
                ++i
            }
            this.updateChanges.clear()
            var lastUpdateChange: SubChange<E>? = null
            for (i in newUpdated) {
                if (lastUpdateChange == null || lastUpdateChange.to != i) {
                    lastUpdateChange = SubChange(i, i + 1, null, EMPTY_PERM, true)
                    this.updateChanges.add(lastUpdateChange)
                } else {
                    lastUpdateChange.to = i + 1
                }
            }
        }
    }

    fun nextReplace(from: Int, to: Int, removed: List<E>) {
        nextRemove(from, removed)
        nextAdd(from, to)
    }

    fun nextSet(idx: Int, old: E) {
        nextRemove(idx, old)
        nextAdd(idx, idx + 1)
    }

    fun nextUpdate(idx: Int) {
        checkState()
        if (this.updateChanges == null) {
            this.updateChanges = ArrayList()
        }
        val last = if (this.updateChanges.isEmpty()) null else this.updateChanges[this.updateChanges.size - 1]
        if (last != null && last.to == idx) {
            last.to = idx + 1
        } else {
            insertUpdate(idx)
        }
    }

    private fun commit() {
        val addRemoveNotEmpty = this.addRemoveChanges.isNotEmpty()
        val updateNotEmpty = this.updateChanges.isNotEmpty()
        if (this.changeLock == 0 && (addRemoveNotEmpty || updateNotEmpty || this.permutationChange != null)) {
            var totalSize =
                    this.updateChanges.size + this.addRemoveChanges.size + if (this.permutationChange != null) 1 else 0
            if (totalSize == 1) {
                if (addRemoveNotEmpty) {
                    this.list.fireChanges(SingleChange(finalizeSubChange(this.addRemoveChanges[0])!!, list))
                    this.addRemoveChanges.clear()
                } else if (updateNotEmpty) {
                    this.list.fireChanges(SingleChange(finalizeSubChange(this.updateChanges[0])!!, list))
                    this.updateChanges.clear()
                } else {
                    this.list.fireChanges(SingleChange(finalizeSubChange(this.permutationChange!!)!!, list))
                    this.permutationChange = null
                }
            } else {
                if (updateNotEmpty) {
                    val removed = compress(this.updateChanges as MutableList<SubChange<E>?>)
                    totalSize -= removed
                }
                if (addRemoveNotEmpty) {
                    val removed = compress(this.addRemoveChanges as MutableList<SubChange<E>?>)
                    totalSize -= removed
                }
                val array: Array<SubChange<E>?> = arrayOfNulls(totalSize)
                var ptr = 0
                if (this.permutationChange != null) {
                    array[ptr++] = this.permutationChange
                }
                if (addRemoveNotEmpty) {
                    val sz = this.addRemoveChanges.size
                    for (i in 0 until sz) {
                        val change = this.addRemoveChanges[i]
                        if (change != null) {
                            array[ptr++] = change
                        }
                    }
                }
                if (updateNotEmpty) {
                    val sz = this.updateChanges.size
                    for (i in 0 until sz) {
                        val change = this.updateChanges[i]
                        if (change != null) {
                            array[ptr++] = change
                        }
                    }
                }
                this.list.fireChanges(
                        IterableChange(finalizeSubChangeArray(array as Array<SubChange<E>>) as Array<SubChange<E>>,
                                this.list))
                this.addRemoveChanges.clear()
                if (this.updateChanges != null) {
                    this.updateChanges.clear()
                }
                this.permutationChange = null
            }
        }
    }

    fun beginChange() {
        this.changeLock++
    }

    fun endChange() {
        check(this.changeLock > 0) { "Called endChange before beginChange" }
        this.changeLock--
        commit()
    }

    private class SingleChange<E>(private val change: SubChange<E>, list: ObservableListBase<E>) : Change<E>(list) {

        private var onChange: Boolean = false

        override fun next(): Boolean {
            return if (this.onChange) {
                false
            } else {
                this.onChange = true
                true
            }
        }

        override fun reset() {
            this.onChange = false
        }

        override val from: Int
            get() {
                checkState()
                return this.change.from
            }

        override val to: Int
            get() {
                checkState()
                return this.change.to
            }

        override val removed: MutableList<E>
            get() {
                checkState()
                return this.change.removed ?: ArrayList()
            }

        override val permutation: IntArray
            get() {
                checkState()
                return this.change.perm
            }

        override val wasUpdated: Boolean
            get() {
                checkState()
                return this.change.updated
            }

        private fun checkState() {
            if (!this.onChange) {
                throw IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.")
            }
        }

        override fun toString(): String {
            val ret: String = if (this.change.perm.isNotEmpty()) {
                permChangeToString(this.change.perm)
            } else if (this.change.updated) {
                updateChangeToString(this.change.from, this.change.to)
            } else {
                addRemoveChangeToString(this.change.from, this.change.to, this.list, this.change.removed!!)
            }
            return "{ $ret }"
        }

    }

    private class IterableChange<E>(private val changes: Array<SubChange<E>>, list: ObservableList<E>) :
            Change<E>(list) {

        private var cursor = -1

        override operator fun next(): Boolean {
            if (this.cursor + 1 < this.changes.size) {
                ++cursor
                return true
            }
            return false
        }

        override fun reset() {
            this.cursor = -1
        }

        override val from: Int
            get() {
                checkState()
                return this.changes[cursor].from
            }

        override val to: Int
            get() {
                checkState()
                return this.changes[cursor].to
            }

        override val removed: MutableList<E>
            get() {
                checkState()
                return this.changes[this.cursor].removed ?: ArrayList()
            }

        override val permutation: IntArray
            get() {
                checkState()
                return this.changes[this.cursor].perm
            }

        override val wasUpdated: Boolean
            get() {
                checkState()
                return this.changes[this.cursor].updated
            }

        private fun checkState() {
            check(this.cursor != -1) { "Invalid Change state: next() must be called before inspecting the Change." }
        }

        override fun toString(): String {
            var c = 0
            val b = StringBuilder()
            b.append("{ ")
            while (c < this.changes.size) {
                if (this.changes[c].perm.isNotEmpty()) {
                    b.append(permChangeToString(this.changes[c].perm))
                } else if (this.changes[c].updated) {
                    b.append(updateChangeToString(this.changes[c].from, this.changes[c].to))
                } else {
                    b.append(addRemoveChangeToString(this.changes[c].from, this.changes[c].to, this.list,
                            this.changes[c].removed!!))
                }
                if (c != this.changes.size - 1) {
                    b.append(", ")
                }
                ++c
            }
            b.append(" }")
            return b.toString()
        }

    }

    companion object {

        private val EMPTY_PERM = IntArray(0)

        private fun <E> finalizeSubChangeArray(changes: Array<SubChange<E>>): Array<SubChange<E>>? {
            for (c in changes) {
                finalizeSubChange(c)
            }
            return changes
        }

        private fun <E> finalizeSubChange(c: SubChange<E>): SubChange<E>? {
            if (c.perm == null) {
                c.perm = EMPTY_PERM
            }
            if (c.removed == null) {
                c.removed = ArrayList()
            } else {
                c.removed = Collections.unmodifiableList(c.removed)
            }
            return c
        }
    }

}
