package io.github.vinccool96.observationskt.sun.collections

@Suppress("UNCHECKED_CAST", "DuplicatedCode")
class SortHelper {

    private var permutation: IntArray? = null

    private var reversePermutation: IntArray? = null

    inline fun <reified T : Comparable<T>> sort(list: MutableList<T>): IntArray {
        val a: Array<T> = list.toTypedArray()
        val result: IntArray = sort(a)
        val i: MutableListIterator<T> = list.listIterator()
        for (j in a.indices) {
            i.next()
            i.set(a[j])
        }
        return result
    }

    inline fun <reified T> sort(list: MutableList<T>, c: Comparator<in T>?): IntArray {
        val a: Array<Any> = list.toTypedArray() as Array<Any>
        val result: IntArray = sort(a as Array<T>, c)
        val i: MutableListIterator<T> = list.listIterator()
        for (j in a.indices) {
            i.next()
            i.set(a[j])
        }
        return result
    }

    fun <T : Comparable<T>> sort(a: Array<T>): IntArray {
        return sort(a, null)
    }

    fun <T> sort(a: Array<T>, c: Comparator<in T>?): IntArray {
        val aux: Array<T> = a.clone()
        val result: IntArray = initPermutation(a.size)
        if (c == null) {
            mergeSort(aux as Array<Any>, a as Array<Any>, 0, a.size, 0)
        } else {
            mergeSort(aux as Array<Any>, a as Array<Any>, 0, a.size, 0, c as Comparator<Any>)
        }
        this.reversePermutation = null
        this.permutation = null
        return result
    }

    fun <T> sort(a: Array<T>, from: Int, to: Int, c: Comparator<in T>?): IntArray {
        rangeCheck(a.size, from, to)
        val aux: Array<T> = a.copyOfRange(from, to)
        val result: IntArray = initPermutation(a.size)
        if (c == null) {
            mergeSort(aux as Array<Any>, a as Array<Any>, from, to, -from)
        } else {
            mergeSort(aux as Array<Any>, a as Array<Any>, from, to, -from, c as Comparator<Any>)
        }
        this.reversePermutation = null
        this.permutation = null
        return result.copyOfRange(from, to)
    }

    fun sort(a: IntArray, from: Int, to: Int): IntArray {
        rangeCheck(a.size, from, to)
        val aux: IntArray = a.copyOfRange(from, to)
        val result: IntArray = initPermutation(a.size)
        mergeSort(aux, a, from, to, -from)
        this.reversePermutation = null
        this.permutation = null
        return result.copyOfRange(from, to)
    }

    /**
     * Merge sort from Oracle JDK 6
     */
    private fun mergeSort(src: IntArray, dest: IntArray, paramLow: Int, paramHigh: Int, off: Int) {
        var low = paramLow
        var high = paramHigh
        val length = high - low

        // Insertion sort on smallest arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            for (i in low until high) {
                var j = i
                while (j > low && (dest[j - 1]) > dest[j]) {
                    swap(dest, j, j - 1)
                    j--
                }
            }
            return
        }

        // Recursively sort halves of dest into src
        val destLow = low
        val destHigh = high
        low += off
        high += off
        val mid = (low + high) ushr 1
        mergeSort(dest, src, low, mid, -off)
        mergeSort(dest, src, mid, high, -off)

        // If list is already sorted, just copy from src to dest.  This is an optimization that results in faster sorts
        // for nearly ordered lists.
        if (src[mid - 1] <= src[mid]) {
            System.arraycopy(src, low, dest, destLow, length)
            return
        }

        // Merge sorted halves (now in src) into dest
        var p = low
        var q = mid

        for (i in destLow until destHigh) {
            if (q >= high || p < mid && src[p] <= src[q]) {
                dest[i] = src[p]
                this.permutation!![this.reversePermutation!![p++]] = i
            } else {
                dest[i] = src[q]
                this.permutation!![this.reversePermutation!![q++]] = i
            }
        }

        for (i in destLow until destHigh) {
            this.reversePermutation!![this.permutation!![i]] = i
        }
    }

    private fun mergeSort(src: Array<Any>, dest: Array<Any>, paramLow: Int, paramHigh: Int, off: Int) {
        var low = paramLow
        var high = paramHigh
        val length = high - low

        // Insertion sort on smallest arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            for (i in low until high) {
                var j = i
                while (j > low && (dest[j - 1] as Comparable<Any>) > dest[j]) {
                    swap(dest, j, j - 1)
                    j--
                }
            }
            return
        }

        // Recursively sort halves of dest into src
        val destLow = low
        val destHigh = high
        low += off
        high += off
        val mid = (low + high) ushr 1
        mergeSort(dest, src, low, mid, -off)
        mergeSort(dest, src, mid, high, -off)

        // If list is already sorted, just copy from src to dest.  This is an optimization that results in faster sorts
        // for nearly ordered lists.
        if ((src[mid - 1] as Comparable<Any>) <= src[mid]) {
            System.arraycopy(src, low, dest, destLow, length)
            return
        }

        // Merge sorted halves (now in src) into dest
        var p = low
        var q = mid

        for (i in destLow until destHigh) {
            if (q >= high || p < mid && (src[p] as Comparable<Any>) <= src[q]) {
                dest[i] = src[p]
                this.permutation!![this.reversePermutation!![p++]] = i
            } else {
                dest[i] = src[q]
                this.permutation!![this.reversePermutation!![q++]] = i
            }
        }

        for (i in destLow until destHigh) {
            this.reversePermutation!![this.permutation!![i]] = i
        }
    }

    private fun mergeSort(src: Array<Any>, dest: Array<Any>, paramLow: Int, paramHigh: Int, off: Int,
            c: Comparator<Any>) {
        var low = paramLow
        var high = paramHigh
        val length = high - low

        // Insertion sort on smallest arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            for (i in low until high) {
                var j = i
                while (j > low && c.compare(dest[j - 1], dest[j]) > 0) {
                    swap(dest, j, j - 1)
                    j--
                }
            }
            return
        }

        // Recursively sort halves of dest into src
        val destLow = low
        val destHigh = high
        low += off
        high += off
        val mid = (low + high) ushr 1
        mergeSort(dest, src, low, mid, -off)
        mergeSort(dest, src, mid, high, -off)

        // If list is already sorted, just copy from src to dest.  This is an optimization that results in faster sorts
        // for nearly ordered lists.
        if (c.compare(src[mid - 1], src[mid]) <= 0) {
            System.arraycopy(src, low, dest, destLow, length)
            return
        }

        // Merge sorted halves (now in src) into dest
        var p = low
        var q = mid

        for (i in destLow until destHigh) {
            if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0) {
                dest[i] = src[p]
                this.permutation!![this.reversePermutation!![p++]] = i
            } else {
                dest[i] = src[q]
                this.permutation!![this.reversePermutation!![q++]] = i
            }
        }

        for (i in destLow until destHigh) {
            this.reversePermutation!![this.permutation!![i]] = i
        }
    }

    private fun swap(x: IntArray, a: Int, b: Int) {
        val t = x[a]
        x[a] = x[b]
        x[b] = t
        this.permutation!![this.reversePermutation!![a]] = b
        this.permutation!![this.reversePermutation!![b]] = a
        val tp = this.reversePermutation!![a]
        this.reversePermutation!![a] = this.reversePermutation!![b]
        this.reversePermutation!![b] = tp
    }

    private fun swap(x: Array<Any>, a: Int, b: Int) {
        val t = x[a]
        x[a] = x[b]
        x[b] = t
        this.permutation!![this.reversePermutation!![a]] = b
        this.permutation!![this.reversePermutation!![b]] = a
        val tp = this.reversePermutation!![a]
        this.reversePermutation!![a] = this.reversePermutation!![b]
        this.reversePermutation!![b] = tp
    }

    private fun initPermutation(length: Int): IntArray {
        this.permutation = IntArray(length)
        this.reversePermutation = IntArray(length)
        for (i in 0 until length) {
            reversePermutation!![i] = i
            permutation!![i] = reversePermutation!![i]
        }
        return this.permutation!!
    }

    companion object {

        const val INSERTION_SORT_THRESHOLD = 7

        private fun rangeCheck(arrayLen: Int, from: Int, to: Int) {
            require(from <= to) {
                "from($from) > to($to)"
            }
            if (from < 0) {
                throw ArrayIndexOutOfBoundsException(from)
            }
            if (to > arrayLen) {
                throw ArrayIndexOutOfBoundsException(to)
            }
        }
    }

}