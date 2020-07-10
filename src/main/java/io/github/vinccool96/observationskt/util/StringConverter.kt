package io.github.vinccool96.observationskt.util

/**
 * Converter defines conversion behavior between strings and objects. The type of objects and formats of strings are
 * defined by the subclasses of Converter.
 *
 * @param T
 *         the type of the object
 */
abstract class StringConverter<T> {

    /**
     * Converts the value provided into its string form. Format of the returned string is defined by the specific
     * converter.
     *
     * @param value
     *         the value to convert
     *
     * @return a string representation of the value passed in.
     */
    abstract fun toString(value: T): String

    /**
     * Converts the string provided into an object defined by the specific converter. Format of the string and type of
     * the resulting object is defined by the specific converter.
     *
     * @param string
     *         the string to convert to an object
     *
     * @return an object representation of the string passed in.
     */
    abstract fun fromString(string: String): T

}