package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import io.github.vinccool96.observationskt.sun.collections.ChangeHelper

@Suppress("UNCHECKED_CAST")
internal class ArrayChangeBuilder<T> internal constructor(private val array: ObservableArrayBase<T>) :
        ChangeBuilder<T>() {

    override val size: Int
        get() = this.array.size

    override fun commit() {
        val addRemoveNotEmpty = this.addRemoveChanges.isNotEmpty()
        val updateNotEmpty = this.updateChanges.isNotEmpty()
        if (this.changeLock == 0 && (addRemoveNotEmpty || updateNotEmpty || this.permutationChange != null)) {
            var totalSize =
                    this.updateChanges.size + this.addRemoveChanges.size + if (this.permutationChange != null) 1 else 0
            if (totalSize == 1) {
                if (addRemoveNotEmpty) {
                    this.array.fireChanges(SingleChange(finalizeSubChange(this.addRemoveChanges[0]), this.array))
                    this.addRemoveChanges.clear()
                } else if (updateNotEmpty) {
                    this.array.fireChanges(SingleChange(finalizeSubChange(this.updateChanges[0]), this.array))
                    this.updateChanges.clear()
                } else {
                    this.array.fireChanges(SingleChange(finalizeSubChange(this.permutationChange!!), this.array))
                    this.permutationChange = null
                }
            } else {
                if (updateNotEmpty) {
                    val removed = compress(this.updateChanges as MutableList<SubChange<T>?>)
                    totalSize -= removed
                }
                if (addRemoveNotEmpty) {
                    val removed = compress(this.addRemoveChanges as MutableList<SubChange<T>?>)
                    totalSize -= removed
                }
                val array: Array<SubChange<T>?> = arrayOfNulls(totalSize)
                var ptr = 0
                if (this.permutationChange != null) {
                    array[ptr++] = this.permutationChange
                }
                if (addRemoveNotEmpty) {
                    val sz = this.addRemoveChanges.size
                    for (i in 0 until sz) {
                        val change = this.addRemoveChanges[i]
                        array[ptr++] = change
                    }
                }
                if (updateNotEmpty) {
                    val sz = this.updateChanges.size
                    for (i in 0 until sz) {
                        val change = this.updateChanges[i]
                        array[ptr++] = change
                    }
                }
                this.array.fireChanges(IterableChange(finalizeSubChangeArray(array as Array<SubChange<T>>), this.array))
                this.addRemoveChanges.clear()
                this.updateChanges.clear()
                this.permutationChange = null
            }
        }
    }

    private class SingleChange<T>(private val change: SubChange<T>, array: ObservableArrayBase<T>) : Change<T>(array) {

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

        override val removed: Array<T>
            get() {
                checkState()
                return toArray(this.array, this.change.removed)
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
                ChangeHelper.permChangeToString(this.change.perm)
            } else if (this.change.updated) {
                ChangeHelper.updateChangeToString(this.change.from, this.change.to)
            } else {
                ChangeHelper.addRemoveChangeToString(this.change.from, this.change.to, this.array,
                        this.change.removed!!)
            }
            return "{ $ret }"
        }

    }

    private class IterableChange<T>(private val changes: Array<SubChange<T>>, array: ObservableArrayBase<T>) :
            Change<T>(array) {

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

        override val removed: Array<T>
            get() {
                checkState()
                return toArray(this.array, this.changes[this.cursor].removed)
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
                    b.append(ChangeHelper.permChangeToString(this.changes[c].perm))
                } else if (this.changes[c].updated) {
                    b.append(ChangeHelper.updateChangeToString(this.changes[c].from, this.changes[c].to))
                } else {
                    b.append(ChangeHelper.addRemoveChangeToString(this.changes[c].from, this.changes[c].to, this.array,
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

        private fun <T> toArray(array: ObservableArray<T>, removed: MutableList<T>?): Array<T> {
            val baseArray = array.baseArray
            var result = baseArray.copyOfRange(0, 0)
            if (removed != null) {
                for (elem in removed) {
                    val part = array.baseArray
                    part[0] = elem
                    result += part
                }
            }
            return result
        }

    }

}