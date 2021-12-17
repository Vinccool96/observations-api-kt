package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableArray

class SourceAdapterArrayChange<T>(array: ObservableArray<T>, private val change: Change<out T>) : Change<T>(array) {

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
    override val removed: Array<T>
        get() = this.change.removed as Array<T>

    override val wasUpdated: Boolean
        get() = this.change.wasUpdated

    override val permutation: IntArray
        get() {
            if (this.perm == null) {
                if (this.change.wasPermutated) {
                    val from = this.change.from
                    val n = this.change.to - from
                    this.perm = IntArray(n) { i: Int -> this.change.getPermutation(from + i) }
                } else {
                    this.perm = IntArray(0)
                }
            }
            return this.perm!!
        }

}