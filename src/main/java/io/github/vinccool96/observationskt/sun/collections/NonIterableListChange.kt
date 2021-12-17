package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableList

abstract class NonIterableListChange<E>(private val f: Int, private val t: Int, list: ObservableList<E>) :
        Change<E>(list) {

    private var invalid: Boolean = true

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
                    ChangeHelper.addRemoveChangeToString(this.f, this.t, this.list, this.removed)
                }
        this.invalid = oldInvalid
        return "{ $ret }"
    }

    class GenericAddRemoveChange<E>(f: Int, t: Int, private val removedList: List<E>, list: ObservableList<E>) :
            NonIterableListChange<E>(f, t, list) {

        override val removed: List<E>
            get() {
                checkState()
                return this.removedList
            }

    }

    class SimpleRemovedChange<E>(f: Int, t: Int, list: ObservableList<E>) : NonIterableListChange<E>(f, t, list) {

        override val wasRemoved: Boolean
            get() {
                checkState()
                return false
            }

        override val removed: List<E>
            get() {
                checkState()
                return ArrayList()
            }

    }

    class SimpleAddChange<E>(f: Int, t: Int, list: ObservableList<E>) : NonIterableListChange<E>(f, t, list) {

        override val wasRemoved: Boolean
            get() {
                checkState()
                return false
            }

        override val removed: List<E>
            get() {
                checkState()
                return ArrayList()
            }

    }

    class SimplePermutationChange<E>(f: Int, t: Int, private val perm: IntArray, list: ObservableList<E>) :
            NonIterableListChange<E>(f, t, list) {

        override val removed: List<E>
            get() {
                checkState()
                return ArrayList()
            }

        override val permutation: IntArray
            get() {
                checkState()
                return this.perm
            }

    }

    class SimpleUpdateChange<E>(f: Int, t: Int, list: ObservableList<E>) : NonIterableListChange<E>(f, t, list) {

        constructor(position: Int, list: ObservableList<E>) : this(position, position + 1, list)

        override val removed: List<E> = ArrayList()

        override val wasUpdated: Boolean = true

    }

    companion object {

        val EMPTY_PERM: IntArray = IntArray(0)

    }

}
