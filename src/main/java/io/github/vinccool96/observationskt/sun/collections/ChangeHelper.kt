package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.collections.ObservableArray

object ChangeHelper {

    fun addRemoveChangeToString(from: Int, to: Int, list: List<*>, removed: List<*>): String {
        val stringBuilder = StringBuilder()

        if (removed.isEmpty()) {
            stringBuilder.append(list.subList(from, to))
            stringBuilder.append(" added at ").append(from)
        } else {
            stringBuilder.append(removed)
            if (from == to) {
                stringBuilder.append(" removed at ").append(from)
            } else {
                stringBuilder.append(" replaced by ")
                stringBuilder.append(list.subList(from, to))
                stringBuilder.append(" at ").append(from)
            }
        }

        return stringBuilder.toString()
    }

    fun <T> addRemoveChangeToString(from: Int, to: Int, array: ObservableArray<T>, removed: Array<T>): String {
        val stringBuilder = StringBuilder()

        if (removed.isEmpty()) {
            stringBuilder.append(array.toTypedArray(from, to).contentToString())
            stringBuilder.append(" added at ").append(from)
        } else {
            stringBuilder.append(removed.contentToString())
            if (from == to) {
                stringBuilder.append(" removed at ").append(from)
            } else {
                stringBuilder.append(" replaced by ")
                stringBuilder.append(array.toTypedArray(from, to).contentToString())
                stringBuilder.append(" at ").append(from)
            }
        }

        return stringBuilder.toString()
    }

    fun <T> addRemoveChangeToString(from: Int, to: Int, array: ObservableArray<T>, removed: List<T>): String {
        val stringBuilder = StringBuilder()

        if (removed.isEmpty()) {
            stringBuilder.append(array.toTypedArray(from, to).contentToString())
            stringBuilder.append(" added at ").append(from)
        } else {
            stringBuilder.append(removed)
            if (from == to) {
                stringBuilder.append(" removed at ").append(from)
            } else {
                stringBuilder.append(" replaced by ")
                stringBuilder.append(array.toTypedArray(from, to).contentToString())
                stringBuilder.append(" at ").append(from)
            }
        }

        return stringBuilder.toString()
    }

    fun permChangeToString(permutation: IntArray): String {
        return "permutated by $permutation"
    }

    fun updateChangeToString(from: Int, to: Int): String {
        return "updated at range [$from, $to]"
    }

}