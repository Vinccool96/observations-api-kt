package io.github.vinccool96.observationskt.collections

fun interface Callable<V> {

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     *
     * @throws Exception if unable to compute a result
     */
    @Throws(Exception::class)
    fun call(): V

}