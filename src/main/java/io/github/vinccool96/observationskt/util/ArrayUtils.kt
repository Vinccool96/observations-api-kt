package io.github.vinccool96.observationskt.util

object ArrayUtils {

    @Suppress("UNCHECKED_CAST")
    fun <T> copyOfNotNulls(array: Array<T?>): Array<T> {
        val listOfNotNull = array.filterNot {t -> t == null}
        val filtered = array.copyOf(listOfNotNull.size)
        for (indexedValue in listOfNotNull.withIndex()) {
            filtered[indexedValue.index] = indexedValue.value
        }
        return filtered as Array<T>
    }

}