package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableList

class SourceAdapterListChange<E>(list: ObservableList<E>, private val change: Change<out E>) : Change<E>(list) {

    private var perm: IntArray? = null

    override fun next(): Boolean {
        this.perm = null
        return this.change.next()
    }

    override fun reset() {
        this.change.reset()
    }

    override val from: Int
        get() = this.change.from

    override val to: Int
        get() = this.change.to

    @Suppress("UNCHECKED_CAST")
    override val removed: List<E>
        get() = this.change.removed

    override val wasUpdated: Boolean
        get() = this.change.wasUpdated

    override val permutation: IntArray
        get() {
            if (this.perm == null) {
                if (this.change.wasPermutated) {
                    val from: Int = this.change.from
                    val n: Int = this.change.to - from
                    this.perm = IntArray(n) {i: Int -> this.change.getPermutation(from + i)}
                } else {
                    this.perm = IntArray(0)
                }
            }
            return this.perm!!
        }

    override fun toString(): String {
        return this.change.toString()
    }

}