package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableArray

abstract class NonIterableArrayChange<T>(private val f: Int, private val t: Int, array: ObservableArray<T>) :
        Change<T>(array) {

    private var invalid = true

    override val from: Int
        get() {
            checkState()
            return this.f
        }

    override val to: Int
        get() {
            checkState()
            return this.t
        }

    override val permutation: IntArray
        get() {
            checkState()
            return EMPTY_PERM
        }

    override fun next(): Boolean {
        if (this.invalid) {
            this.invalid = false
            return true
        }
        return false
    }

    override fun reset() {
        this.invalid = true
    }

    fun checkState() {
        if (this.invalid) {
            throw IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.")
        }
    }

    override fun toString(): String {
        val oldInvalid = this.invalid
        this.invalid = false
        val ret: String =
                if (this.wasPermutated) {
                    ChangeHelper.permChangeToString(this.permutation)
                } else if (this.wasUpdated) {
                    ChangeHelper.updateChangeToString(this.f, this.t)
                } else {
                    ChangeHelper.addRemoveChangeToString(this.f, this.t, this.array, this.removed)
                }
        this.invalid = oldInvalid
        return "{ $ret }"
    }

    class GenericAddRemoveChange<T>(f: Int, t: Int, private val removedArray: Array<T>, array: ObservableArray<T>) :
            NonIterableArrayChange<T>(f, t, array) {

        override val removed: Array<T>
            get() {
                checkState()
                return this.removedArray
            }

    }

    companion object {

        private val EMPTY_PERM = IntArray(0)

    }

}