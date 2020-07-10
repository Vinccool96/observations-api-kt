package io.github.vinccool96.observationskt.util

/**
 * The Callback interface is designed to allow for a common, reusable interface to exist for defining APIs that requires
 * a call back in certain situations.
 *
 * Callback is defined with two generic parameters: the first parameter specifies the type of the object passed in to
 * the `call` method, with the second parameter specifying the return type of the method.
 *
 * @param P
 *         The type of the argument provided to the `call` method.
 * @param R
 *         The type of the return type of the `call` method.
 */
@FunctionalInterface
interface Callback<P, R> {

    fun call(param: P): R

}