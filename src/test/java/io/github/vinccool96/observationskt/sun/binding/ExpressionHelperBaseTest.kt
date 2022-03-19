package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.WeakListener
import org.junit.Assert.assertArrayEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionHelperBaseTest : ExpressionHelperBase() {

    @Test
    fun testEmptyArray() {
        var array: Array<Any?> = arrayOf()
        assertEquals(0, trim(0, array))
        assertArrayEquals(arrayOf(), array)

        array = arrayOfNulls(1)
        assertEquals(0, trim(0, array))
        assertArrayEquals(arrayOfNulls(1), array)
    }

    @Test
    fun testSingleElement() {
        var array: Array<Any?> = arrayOf(listener)
        assertEquals(1, trim(1, array))
        assertArrayEquals(arrayOf(listener), array)

        array = arrayOf(validWeakListener)
        assertEquals(1, trim(1, array))
        assertArrayEquals(arrayOf(validWeakListener), array)

        array = arrayOf(gcedWeakListener)
        assertEquals(0, trim(1, array))
        assertArrayEquals(arrayOfNulls(1), array)

        array = arrayOf(listener, null)
        assertEquals(1, trim(1, array))
        assertArrayEquals(arrayOf(listener, null), array)

        array = arrayOf(validWeakListener, null)
        assertEquals(1, trim(1, array))
        assertArrayEquals(arrayOf(validWeakListener, null), array)

        array = arrayOf(gcedWeakListener, null)
        assertEquals(0, trim(1, array))
        assertArrayEquals(arrayOfNulls(2), array)
    }

    @Test
    fun testMultipleElements() {
        var array: Array<Any?> = arrayOf(validWeakListener, listener, listener2)
        assertEquals(3, trim(3, array))
        assertArrayEquals(arrayOf(validWeakListener, listener, listener2), array)

        array = arrayOf(listener, validWeakListener, listener2)
        assertEquals(3, trim(3, array))
        assertArrayEquals(arrayOf(listener, validWeakListener, listener2), array)

        array = arrayOf(listener, listener2, validWeakListener)
        assertEquals(3, trim(3, array))
        assertArrayEquals(arrayOf(listener, listener2, validWeakListener), array)

        array = arrayOf(validWeakListener, listener, listener2, null)
        assertEquals(3, trim(3, array))
        assertArrayEquals(arrayOf(validWeakListener, listener, listener2, null), array)

        array = arrayOf(listener, validWeakListener, listener2, null)
        assertEquals(3, trim(3, array))
        assertArrayEquals(arrayOf(listener, validWeakListener, listener2, null), array)

        array = arrayOf(listener, listener2, validWeakListener, null)
        assertEquals(3, trim(3, array))
        assertArrayEquals(arrayOf(listener, listener2, validWeakListener, null), array)

        array = arrayOf(gcedWeakListener, validWeakListener, listener)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(validWeakListener, listener, null), array)

        array = arrayOf(gcedWeakListener, listener, validWeakListener)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(listener, validWeakListener, null), array)

        array = arrayOf(gcedWeakListener, validWeakListener, listener, null)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(validWeakListener, listener, null, null), array)

        array = arrayOf(gcedWeakListener, listener, validWeakListener, null)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(listener, validWeakListener, null, null), array)

        array = arrayOf(validWeakListener, gcedWeakListener, listener)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(validWeakListener, listener, null), array)

        array = arrayOf(listener, gcedWeakListener, validWeakListener)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(listener, validWeakListener, null), array)

        array = arrayOf(validWeakListener, gcedWeakListener, listener, null)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(validWeakListener, listener, null, null), array)

        array = arrayOf(listener, gcedWeakListener, validWeakListener, null)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(listener, validWeakListener, null, null), array)

        array = arrayOf(validWeakListener, listener, gcedWeakListener)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(validWeakListener, listener, null), array)

        array = arrayOf(listener, validWeakListener, gcedWeakListener)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(listener, validWeakListener, null), array)

        array = arrayOf(validWeakListener, listener, gcedWeakListener, null)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(validWeakListener, listener, null, null), array)

        array = arrayOf(listener, validWeakListener, gcedWeakListener, null)
        assertEquals(2, trim(3, array))
        assertArrayEquals(arrayOf(listener, validWeakListener, null, null), array)

        array = arrayOf(gcedWeakListener, gcedWeakListener)
        assertEquals(0, trim(2, array))
        assertArrayEquals(arrayOfNulls(2), array)

        array = arrayOf(gcedWeakListener, gcedWeakListener, null)
        assertEquals(0, trim(2, array))
        assertArrayEquals(arrayOfNulls(3), array)
    }

    companion object {

        private val listener: Any = Any()

        private val listener2: Any = Any()

        private val validWeakListener: WeakListener = object : WeakListener {

            override val wasGarbageCollected: Boolean
                get() = false

        }

        private val gcedWeakListener: WeakListener = object : WeakListener {

            override val wasGarbageCollected: Boolean
                get() = true

        }

    }

}