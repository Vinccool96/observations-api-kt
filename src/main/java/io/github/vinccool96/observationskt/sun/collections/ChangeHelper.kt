package io.github.vinccool96.observationskt.sun.collections

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

    fun permChangeToString(permutation: IntArray): String {
        return "permutated by $permutation"
    }

    fun updateChangeToString(from: Int, to: Int): String {
        return "updated at range [$from, $to]"
    }
}