package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.sun.collections.ChangeHelper.addRemoveChangeToString
import io.github.vinccool96.observationskt.sun.collections.ChangeHelper.permChangeToString
import io.github.vinccool96.observationskt.sun.collections.ChangeHelper.updateChangeToString
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
        var idx = findSubChange(pos, updateChanges)
        if (idx < 0) { //If not found
            idx = idx.inv()
            lateinit var change: SubChange<E>
            if (idx > 0 && (updateChanges[idx - 1].also {change = it}).to == pos) {
                change.to = pos + 1
            } else if (idx < updateChanges.size && (updateChanges[idx].also {change = it}).from == pos + 1) {
                change.from = pos
            } else {
                updateChanges.add(idx, SubChange(pos, pos + 1, null, EMPTY_PERM, true))
            }
        } // If found, no need to do another update
    }

    private fun insertRemoved(pos: Int, removed: E) {
        var idx = findSubChange(pos, addRemoveChanges)
        if (idx < 0) { // Not found
            idx = idx.inv()
            lateinit var change: SubChange<E>
            if (idx > 0 && addRemoveChanges[idx - 1].also {change = it}.to == pos) {
                change.removed!!.add(removed)
                --idx // Idx index will be used as a starting point for update
            } else if (idx < addRemoveChanges.size && addRemoveChanges[idx].also {change = it}.from == pos + 1) {
                change.from--
                change.to--
                change.removed!!.add(0, removed)
            } else {
                val removedList = ArrayList<E>()
                removedList.add(removed)
                addRemoveChanges.add(idx, SubChange(pos, pos, removedList, EMPTY_PERM, false))
            }
        } else {
            val change = addRemoveChanges[idx]
            change.to-- // Removed one element from the previously added list
            if (change.from == change.to && (change.removed == null || change.removed!!.isEmpty())) {
                addRemoveChanges.removeAt(idx)
            }
        }
        for (i in idx + 1 until addRemoveChanges.size) {
            val change = addRemoveChanges[i]
            change.from--
            change.to--
        }
    }

    private fun insertAdd(from: Int, to: Int) {
        var idx = findSubChange(from, addRemoveChanges)
        val numberOfAdded = to - from
        if (idx < 0) { // Not found
            idx = idx.inv()
            lateinit var change: SubChange<E>
            if (idx > 0 && addRemoveChanges[idx - 1].also {change = it}.to == from) {
                change.to = to
                --idx
            } else {
                addRemoveChanges.add(idx, SubChange(from, to, ArrayList(), EMPTY_PERM, false))
            }
        } else {
            val change = addRemoveChanges[idx]
            change.to += numberOfAdded
        }
        for (i in idx + 1 until addRemoveChanges.size) {
            val change = addRemoveChanges[i]
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

    private class SubChange<E>(internal var from: Int, internal var to: Int, internal var removed: MutableList<E>?,
            internal var perm: IntArray, internal var updated: Boolean)

    init {
        this.addRemoveChanges = ArrayList()
        this.updateChanges = ArrayList()
        this.permutationChange = null
    }

    fun nextRemove(idx: Int, removed: E) {
        checkState()
        val last =
                if (addRemoveChanges.isEmpty()) null else addRemoveChanges[addRemoveChanges.size - 1]
        if (last != null && last.to == idx) {
            last.removed!!.add(removed)
        } else if (last != null && last.from == idx + 1) {
            last.from--
            last.to--
            last.removed!!.add(0, removed)
        } else {
            insertRemoved(idx, removed)
        }
        if (updateChanges != null && !updateChanges.isEmpty()) {
            var uPos = findSubChange(idx, updateChanges)
            if (uPos < 0) {
                uPos = uPos.inv()
            } else {
                val change =
                        updateChanges[uPos]
                if (change.from == change.to - 1) {
                    updateChanges.removeAt(uPos)
                } else {
                    change.to--
                    ++uPos // Do the update from the next position
                }
            }
            for (i in uPos until updateChanges.size) {
                updateChanges[i].from--
                updateChanges[i].to--
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
        val last =
                if (addRemoveChanges.isEmpty()) null else addRemoveChanges[addRemoveChanges.size - 1]
        val numberOfAdded = to - from
        if (last != null && last.to == from) {
            last.to = to
        } else if (last != null && from >= last.from && from < last.to) { // Adding to the middle
            last.to += numberOfAdded
        } else {
            insertAdd(from, to)
        }
        if (updateChanges != null && !updateChanges.isEmpty()) {
            var uPos = findSubChange(from, updateChanges)
            if (uPos < 0) {
                uPos = uPos.inv()
            } else {
                // We have to split the change into 2
                val change =
                        updateChanges[uPos]
                updateChanges.add(uPos + 1,
                        SubChange(to,
                                change.to + to - from, null,
                                EMPTY_PERM, true))
                change.to = from
                uPos += 2 // skip those 2 for the update
            }
            for (i in uPos until updateChanges.size) {
                updateChanges[i].from += numberOfAdded
                updateChanges[i].to += numberOfAdded
            }
        }
    }

    fun nextPermutation(from: Int, to: Int, perm: IntArray) {
        checkState()
        var prePermFrom = from
        var prePermTo = to
        var prePerm = perm
        if (addRemoveChanges != null && !addRemoveChanges.isEmpty()) {
            //Because there were already some changes to the list, we need
            // to "reconstruct" the original list and create a permutation
            // as-if there were no changes to the list. We can then
            // merge this with the permutation we already did

            // This maps elements from current list to the original list.
            // -1 means the map was not in the original list.
            // Note that for performance reasons, the map is permutated when created
            // by the permutation. So it basically contains the order in which the original
            // items were permutated by our new permutation.
            val mapToOriginal = IntArray(list.size)
            // Marks the original-list indexes that were removed
            val removed: MutableSet<Int> = TreeSet()
            var last = 0
            var offset = 0
            run {
                var i = 0
                val sz = addRemoveChanges.size
                while (i < sz) {
                    val change =
                            addRemoveChanges[i]
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
        if (permutationChange != null) {
            if (prePermFrom == permutationChange!!.from && prePermTo == permutationChange!!.to) {
                for (i in prePerm.indices) {
                    permutationChange!!.perm[i] = prePerm[permutationChange!!.perm[i] - prePermFrom]
                }
            } else {
                val newTo = Math.max(permutationChange!!.to, prePermTo)
                val newFrom = Math.min(permutationChange!!.from, prePermFrom)
                val newPerm = IntArray(newTo - newFrom)
                for (i in newFrom until newTo) {
                    if (i < permutationChange!!.from || i >= permutationChange!!.to) {
                        newPerm[i - newFrom] = prePerm[i - prePermFrom]
                    } else {
                        val p = permutationChange!!.perm[i - permutationChange!!.from]
                        if (p < prePermFrom || p >= prePermTo) {
                            newPerm[i - newFrom] = p
                        } else {
                            newPerm[i - newFrom] = prePerm[p - prePermFrom]
                        }
                    }
                }
                permutationChange!!.from = newFrom
                permutationChange!!.to = newTo
                permutationChange!!.perm = newPerm
            }
        } else {
            permutationChange = SubChange(prePermFrom, prePermTo, null, prePerm, false)
        }
        if (addRemoveChanges != null && addRemoveChanges.isNotEmpty()) {
            val newAdded: MutableSet<Int> = TreeSet()
            val newRemoved: MutableMap<Int, MutableList<E>?> =
                    HashMap()
            var i = 0
            val sz = addRemoveChanges.size
            while (i < sz) {
                val change = addRemoveChanges[i]
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
            addRemoveChanges.clear()
            var lastChange: SubChange<E>? = null
            for (i in newAdded) {
                if (lastChange == null || lastChange.to != i) {
                    lastChange = SubChange(i, i + 1, null, EMPTY_PERM, false)
                    addRemoveChanges.add(lastChange)
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
                val idx = findSubChange(at, addRemoveChanges)
                assert(idx < 0)
                addRemoveChanges.add(idx.inv(), SubChange(at, at, value, IntArray(0), false))
            }
        }
        if (updateChanges != null && !updateChanges.isEmpty()) {
            val newUpdated: MutableSet<Int> = TreeSet()
            var i = 0
            val sz = updateChanges.size
            while (i < sz) {
                val change = updateChanges[i]
                for (cIndex in change.from until change.to) {
                    if (cIndex < from || cIndex >= to) {
                        newUpdated.add(cIndex)
                    } else {
                        newUpdated.add(perm[cIndex - from])
                    }
                }
                ++i
            }
            updateChanges.clear()
            var lastUpdateChange: SubChange<E>? = null
            for (i in newUpdated) {
                if (lastUpdateChange == null || lastUpdateChange.to != i) {
                    lastUpdateChange = SubChange(i, i + 1, null, EMPTY_PERM, true)
                    updateChanges.add(lastUpdateChange)
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
        if (updateChanges == null) {
            updateChanges = ArrayList()
        }
        val last = if (updateChanges.isEmpty()) null else updateChanges[updateChanges.size - 1]
        if (last != null && last.to == idx) {
            last.to = idx + 1
        } else {
            insertUpdate(idx)
        }
    }

    private fun commit() {
        val addRemoveNotEmpty = addRemoveChanges.isNotEmpty()
        val updateNotEmpty = updateChanges.isNotEmpty()
        if (changeLock == 0 && (addRemoveNotEmpty || updateNotEmpty || permutationChange != null)) {
            var totalSize = updateChanges.size + addRemoveChanges.size + if (permutationChange != null) 1 else 0
            if (totalSize == 1) {
                if (addRemoveNotEmpty) {
                    list.fireChanges(SingleChange(finalizeSubChange(addRemoveChanges[0])!!, list))
                    addRemoveChanges.clear()
                } else if (updateNotEmpty) {
                    list.fireChanges(SingleChange(finalizeSubChange(updateChanges[0])!!, list))
                    updateChanges.clear()
                } else {
                    list.fireChanges(SingleChange(finalizeSubChange(permutationChange!!)!!, list))
                    permutationChange = null
                }
            } else {
                if (updateNotEmpty) {
                    val removed = compress(updateChanges as MutableList<SubChange<E>?>)
                    totalSize -= removed
                }
                if (addRemoveNotEmpty) {
                    val removed = compress(addRemoveChanges as MutableList<SubChange<E>?>)
                    totalSize -= removed
                }
                val array: Array<SubChange<E>?> = arrayOfNulls(totalSize)
                var ptr = 0
                if (permutationChange != null) {
                    array[ptr++] = permutationChange
                }
                if (addRemoveNotEmpty) {
                    val sz = addRemoveChanges.size
                    for (i in 0 until sz) {
                        val change =
                                addRemoveChanges[i]
                        if (change != null) {
                            array[ptr++] = change
                        }
                    }
                }
                if (updateNotEmpty) {
                    val sz = updateChanges.size
                    for (i in 0 until sz) {
                        val change = updateChanges[i]
                        if (change != null) {
                            array[ptr++] = change
                        }
                    }
                }
                list.fireChanges(
                        IterableChange(finalizeSubChangeArray(array as Array<SubChange<E>>) as Array<SubChange<E>>,
                                list))
                addRemoveChanges.clear()
                if (updateChanges != null) {
                    updateChanges.clear()
                }
                permutationChange = null
            }
        }
    }

    fun beginChange() {
        changeLock++
    }

    fun endChange() {
        check(changeLock > 0) {"Called endChange before beginChange"}
        changeLock--
        commit()
    }

    private class SingleChange<E>(private val change: SubChange<E>, list: ObservableListBase<E>) : Change<E>(list) {

        private var onChange: Boolean = false

        override fun next(): Boolean {
            if (this.onChange) {
                return false
            } else {
                this.onChange = true
                return true
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
            val ret: String = if (change.perm.isNotEmpty()) {
                permChangeToString(change.perm)
            } else if (change.updated) {
                updateChangeToString(change.from, change.to)
            } else {
                addRemoveChangeToString(change.from, change.to, list, change.removed!!)
            }
            return "{ $ret }"
        }

    }

    private class IterableChange<E>(private val changes: Array<SubChange<E>>,
            list: ObservableList<E>) :
            Change<E>(list) {

        private var cursor = -1
        override operator fun next(): Boolean {
            if (cursor + 1 < changes.size) {
                ++cursor
                return true
            }
            return false
        }

        override fun reset() {
            cursor = -1
        }

        override val from: Int
            get() {
                checkState()
                return changes[cursor].from
            }

        override val to: Int
            get() {
                checkState()
                return changes[cursor].to
            }

        override val removed: MutableList<E>
            get() {
                checkState()
                return changes[cursor].removed ?: ArrayList()
            }

        override val permutation: IntArray
            get() {
                checkState()
                return changes[cursor].perm
            }

        override val wasUpdated: Boolean
            get() {
                checkState()
                return changes[cursor].updated
            }

        private fun checkState() {
            check(cursor != -1) {"Invalid Change state: next() must be called before inspecting the Change."}
        }

        override fun toString(): String {
            var c = 0
            val b = StringBuilder()
            b.append("{ ")
            while (c < changes.size) {
                if (changes[c].perm.isNotEmpty()) {
                    b.append(permChangeToString(
                            changes[c].perm))
                } else if (changes[c].updated) {
                    b.append(updateChangeToString(
                            changes[c].from, changes[c].to))
                } else {
                    b.append(addRemoveChangeToString(changes[c].from, changes[c].to, list,
                            changes[c].removed!!))
                }
                if (c != changes.size - 1) {
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
