package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.WeakListener

open class ExpressionHelperBase {
    protected companion object HelperBaseCompanion {
        fun trim(size: Int, listeners: Array<Any?>): Int {
            var size = size
            var index = 0
            while (index < size) {
                val listener = listeners[index]
                if (listener is WeakListener) {
                    if (listener.wasGarbageCollected) {
                        val numMoved = size - index - 1
                        if (numMoved > 0) {
                            System.arraycopy(listeners, index + 1, listeners, index, numMoved)
                        }
                        listeners[--size] = null // Let gc do its work
                        index--
                    }
                }
                index++
            }
            return size
        }
    }
}