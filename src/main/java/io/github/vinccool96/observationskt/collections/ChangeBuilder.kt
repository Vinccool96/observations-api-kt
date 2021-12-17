package io.github.vinccool96.observationskt.collections

import java.util.*

@Suppress("DuplicatedCode", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NAME_SHADOWING")
internal abstract class ChangeBuilder<T> {

    protected var changeLock: Int = 0

    protected val addRemoveChanges: MutableList<SubChange<T>>

    protected var updateChanges: MutableList<SubChange<T>>

    protected var permutationChange: SubChange<T>?

    abstract val size: Int

    init {
        this.addRemoveChanges = ArrayList()
        this.updateChanges = ArrayList()
        this.permutationChange = null
    }

    private fun checkState() {
        if (this.changeLock == 0) {
            throw IllegalStateException("beginChange was not called on this builder")
        }
    }

    private fun findSubChange(idx: Int, list: MutableList<SubChange<T>>): Int {
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
            lateinit var change: SubChange<T>
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

    private fun insertRemoved(pos: Int, removed: T) {
        var idx = findSubChange(pos, this.addRemoveChanges)
        if (idx < 0) { // Not found
            idx = idx.inv()
            lateinit var change: SubChange<T>
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
                val removedList = ArrayList<T>()
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
            lateinit var change: SubChange<T>
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

    protected fun compress(list: MutableList<SubChange<T>?>): Int {
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

    internal class SubChange<T>(var from: Int, var to: Int, var removed: MutableList<T>?, var perm: IntArray,
            var updated: Boolean)

    fun nextRemove(idx: Int, removed: T) {
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
        if (this.updateChanges.isNotEmpty()) {
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

    fun nextRemove(idx: Int, removed: List<T>) {
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
        if (this.updateChanges.isNotEmpty()) {
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
        if (this.addRemoveChanges.isNotEmpty()) {
            //Because there were already some changes to the list, we need
            // to "reconstruct" the original list and create a permutation
            // as-if there were no changes to the list. We can then
            // merge this with the permutation we already did

            // This maps elements from current list to the original list.
            // -1 means the map was not in the original list.
            // Note that for performance reasons, the map is permutated when created
            // by the permutation. So it basically contains the order in which the original
            // items were permutated by our new permutation.
            val mapToOriginal = IntArray(this.size)
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
            val newPerm = IntArray(this.size + offset)
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
        if (this.addRemoveChanges.isNotEmpty()) {
            val newAdded: MutableSet<Int> = TreeSet()
            val newRemoved: MutableMap<Int, MutableList<T>?> = HashMap()
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
            var lastChange: SubChange<T>? = null
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
        if (this.updateChanges.isNotEmpty()) {
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
            var lastUpdateChange: SubChange<T>? = null
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

    fun nextReplace(from: Int, to: Int, removed: List<T>) {
        nextRemove(from, removed)
        nextAdd(from, to)
    }

    fun nextSet(idx: Int, old: T) {
        nextRemove(idx, old)
        nextAdd(idx, idx + 1)
    }

    fun nextUpdate(idx: Int) {
        checkState()
        val last = if (this.updateChanges.isEmpty()) null else this.updateChanges[this.updateChanges.size - 1]
        if (last != null && last.to == idx) {
            last.to = idx + 1
        } else {
            insertUpdate(idx)
        }
    }

    protected abstract fun commit()

    fun beginChange() {
        this.changeLock++
    }

    fun endChange() {
        check(this.changeLock > 0) { "Called endChange before beginChange" }
        this.changeLock--
        commit()
    }

    companion object {

        private val EMPTY_PERM = IntArray(0)

        internal fun <T> finalizeSubChangeArray(changes: Array<SubChange<T>>): Array<SubChange<T>> {
            for (c in changes) {
                finalizeSubChange(c)
            }
            return changes
        }

        internal fun <T> finalizeSubChange(c: SubChange<T>): SubChange<T> {
            if (c.removed == null) {
                c.removed = ArrayList()
            } else {
                c.removed = Collections.unmodifiableList(c.removed)
            }
            return c
        }
    }

}