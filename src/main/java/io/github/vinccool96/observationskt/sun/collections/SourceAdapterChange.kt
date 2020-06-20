package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableList

class SourceAdapterChange<E>(list: ObservableList<E>, private val change: Change<out E>) : Change<E>(list) {

    private var perm: IntArray? = null

    override fun next(): Boolean {
        this.perm = null
        return this.change.next()
    }

    override fun reset() {
        this.change.reset()
    }

    override val to: Int
        get() = this.change.to

    @Suppress("UNCHECKED_CAST")
    override val removedElements: MutableList<E>
        get() = this.change.removedElements as MutableList<E>

    override val from: Int
        get() = this.change.from

    override val updated: Boolean
        get() = this.change.updated

    override val permutation: IntArray
        get() {
            if (this.perm == null) {
                if (this.change.permutated) {
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