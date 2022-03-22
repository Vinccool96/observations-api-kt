package io.github.vinccool96.observationskt.collections

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.math.max
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for ObservableArray.
 */
@RunWith(Parameterized::class)
class ObservablePrimitiveArrayTest<T : ObservableArray<P>, A : Any, P>(private val wrapper: ArrayWrapper<T, A, P>) {

    private var initialSize: Int = 0

    private lateinit var initialElements: A

    private lateinit var array: T

    private lateinit var mao: MockArrayObserver<P>

    @BeforeTest
    fun setUp() {
        this.initialSize = INITIAL_SIZE
        this.initialElements = this.wrapper.createPrimitiveArray(this.initialSize)
        this.array = this.wrapper.createNotEmptyArray(this.initialElements)
        this.mao = MockArrayObserver()
        this.array.addListener(this.mao)
    }

    private fun makeEmpty() {
        this.initialSize = 0
        this.initialElements = this.wrapper.createPrimitiveArray(this.initialSize)
        this.array.clear()
        this.mao.reset()
    }

    private fun assertUnchanged() {
        this.mao.check0()
        assertEquals(this.initialSize, this.array.size)
        val actual = this.wrapper.toArray()
        assertEquals(this.initialSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, this.array.size, this.initialElements, 0)
    }

    // ========================= pre-condition tests =========================

    @Test
    fun testSize() {
        this.mao.check0()
        assertEquals(INITIAL_SIZE, this.array.size)
    }

    @Test
    fun testClear() {
        val removed = this.array.toTypedArray()
        this.array.clear()
        this.mao.check1AddRemove(this.array, removed, 0, 0)
        assertEquals(0, this.array.size)
    }

    @Test
    fun testGet() {
        for (i in 0 until this.array.size) {
            val expected = this.wrapper.get(this.initialElements, i)
            val actual = this.wrapper[i]
            assertEquals(expected, actual)
        }
        assertUnchanged()
    }

    @Test
    fun testToArray() {
        val expected = this.initialElements
        val actual = this.wrapper.toArray()
        assertEquals(INITIAL_SIZE, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, this.array.size, expected, 0)
    }

    // ========================= add/remove listener tests =========================

    @Test
    fun testAddRemoveListener() {
        val mao2 = MockArrayObserver<P>()
        this.array.addListener(mao2)
        this.array.removeListener(this.mao)
        this.wrapper[0] = this.wrapper.nextValue
        this.mao.check0()
        mao2.check1()
    }

    @Test
    fun testAddTwoListenersElementChange() {
        val mao2 = MockArrayObserver<P>()
        this.array.addListener(mao2)
        this.wrapper[0] = this.wrapper.nextValue
        this.mao.check1()
        mao2.check1()
    }

    @Test
    fun testAddTwoListenersSizeChange() {
        val mao2 = MockArrayObserver<P>()
        this.array.addListener(mao2)
        this.array.resize(3)
        this.mao.check1()
        mao2.check1()
    }

    @Test
    fun testAddThreeListeners() {
        val mao2 = MockArrayObserver<P>()
        val mao3 = MockArrayObserver<P>()
        this.array.addListener(mao2)
        this.array.addListener(mao3)
        this.wrapper[0] = this.wrapper.nextValue
        this.mao.check1()
        mao2.check1()
        mao3.check1()
    }

    @Test
    fun testAddThreeListenersSizeChange() {
        val mao2 = MockArrayObserver<P>()
        val mao3 = MockArrayObserver<P>()
        this.array.addListener(mao2)
        this.array.addListener(mao3)
        this.array.resize(10)
        this.mao.check1()
        mao2.check1()
        mao3.check1()
    }

    @Test
    fun testAddListenerTwice() {
        this.array.addListener(this.mao) // add it a second time
        this.wrapper[1] = this.wrapper.nextValue
        this.mao.check1()
    }

    @Test
    fun testRemoveListenerTwice() {
        this.array.removeListener(this.mao)
        this.array.removeListener(this.mao)
        this.wrapper[1] = this.wrapper.nextValue
        this.mao.check0()
    }

    // ========================= resize tests =========================

    private fun testResize(noChange: Boolean, newSize: Int, matchingElements: Int) {
        val expected = this.wrapper.toArray()
        this.array.resize(newSize)
        if (noChange) {
            assertUnchanged()
        } else {
            val oldSize = this.wrapper.arraySize(expected)
            if (newSize > oldSize) {
                this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
            } else {
                this.mao.check1AddRemove(this.array, this.wrapper.toTypedArray(expected, newSize, oldSize),
                        newSize, newSize)
            }
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, matchingElements, expected, 0)
        this.wrapper.assertElementsEqual(actual, matchingElements, newSize,
                this.wrapper.createPrimitiveArray(max(0, newSize - matchingElements), false), 0)
    }

    @Test
    fun testResizeTo0() {
        testResize(false, 0, 0)
    }

    @Test
    fun testResizeToSmaller() {
        testResize(false, 3, 3)
    }

    @Test
    fun testResizeToSameSize() {
        testResize(true, this.array.size, this.array.size)
    }

    @Test
    fun testResizeToBigger() {
        testResize(false, 10, this.array.size)
    }

    @Test
    fun testResizeOnEmpty() {
        makeEmpty()
        testResize(false, 10, 0)
    }

    @Test
    fun testResizeOnEmptyToEmpty() {
        makeEmpty()
        testResize(true, 0, 0)
    }

    @Test(expected = NegativeArraySizeException::class)
    fun testResizeToNegative() {
        try {
            this.array.resize(-5)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= setAll(primitive array) tests =========================

    private fun testSetAllA(newSize: Int) {
        val removed = this.wrapper.toTypedArray()
        val expected = this.wrapper.createPrimitiveArray(newSize)

        this.wrapper.setAllA(expected)

        this.mao.check1AddRemove(this.array, removed, 0, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(this.wrapper.arraySize(expected), this.array.size)
        assertEquals(this.wrapper.arraySize(expected), this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, this.wrapper.arraySize(expected), expected, 0)
    }

    @Test
    fun testSetAllASmaller() {
        testSetAllA(3)
    }

    @Test
    fun testSetAllABigger() {
        testSetAllA(10)
    }

    @Test
    fun testSetAllAOnSameSize() {
        testSetAllA(INITIAL_SIZE)
    }

    @Test
    fun testSetAllAOnEmpty() {
        makeEmpty()
        testSetAllA(3)
    }

    @Test
    fun testSetAllAOnEmptyToEmpty() {
        makeEmpty()
        this.wrapper.setAllA(this.wrapper.createPrimitiveArray(0))
        assertUnchanged()
        assertEquals(0, this.array.size)
    }

    // ========================= setAll(array) tests =========================

    private fun testSetAllP(newSize: Int) {
        val removed = this.wrapper.toTypedArray()
        val expected = this.wrapper.createArray(newSize)

        this.wrapper.setAllP(expected)

        this.mao.check1AddRemove(this.array, removed, 0, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(expected.size, this.array.size)
        assertEquals(expected.size, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, expected.size, expected, 0)
    }

    @Test
    fun testSetAllPSmaller() {
        testSetAllP(3)
    }

    @Test
    fun testSetAllPBigger() {
        testSetAllP(10)
    }

    @Test
    fun testSetAllPOnSameSize() {
        testSetAllP(INITIAL_SIZE)
    }

    @Test
    fun testSetAllPOnEmpty() {
        makeEmpty()
        testSetAllP(3)
    }

    @Test
    fun testSetAllPOnEmptyToEmpty() {
        makeEmpty()
        this.wrapper.setAllP(this.wrapper.createArray(0))
        assertUnchanged()
        assertEquals(0, this.array.size)
    }

    // ========================= setAll(ObservableArray) tests =========================

    private fun testSetAllT(newSize: Int) {
        val removed = this.wrapper.toTypedArray()
        val wrapper2 = this.wrapper.newInstance()
        val expected = this.wrapper.createPrimitiveArray(newSize)
        val src = wrapper2.createNotEmptyArray(expected)

        this.wrapper.setAllT(src)

        this.mao.check1AddRemove(this.array, removed, 0, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(this.wrapper.arraySize(expected), this.array.size)
        assertEquals(this.wrapper.arraySize(expected), this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, this.wrapper.arraySize(expected), expected, 0)
    }

    @Test
    fun testSetAllTSmaller() {
        testSetAllT(3)
    }

    @Test
    fun testSetAllTBigger() {
        testSetAllT(10)
    }

    @Test
    fun testSetAllTOnSameSize() {
        testSetAllT(INITIAL_SIZE)
    }

    @Test
    fun testSetAllTOnEmpty() {
        makeEmpty()
        testSetAllT(3)
    }

    @Test
    fun testSetAllTOnEmptyToEmpty() {
        makeEmpty()
        this.wrapper.setAllT(this.wrapper.newInstance().createEmptyArray())
        assertUnchanged()
        assertEquals(0, this.array.size)
    }

    @Test
    fun testSetAllTSelf() {
        this.wrapper.setAllT(this.array)

        this.mao.check0()
        val actual = this.wrapper.toArray()
        assertEquals(this.initialSize, this.array.size)
        assertEquals(this.initialSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, this.initialSize, this.initialElements, 0)
    }

    @Test
    fun testSetAllTSelfEmpty() {
        makeEmpty()

        this.wrapper.setAllT(this.array)

        this.mao.check0()
        val actual = this.wrapper.toArray()
        assertEquals(0, this.array.size)
        assertEquals(0, this.wrapper.arraySize(actual))
    }

    // ========================= setAll(primitive array, range) tests =========================

    private fun testSetAllARange(newSize: Int, startIndex: Int, endIndex: Int) {
        val removed = this.wrapper.toTypedArray()
        val length = endIndex - startIndex
        val expected = this.wrapper.createPrimitiveArray(newSize)

        this.wrapper.setAllA(expected, startIndex, endIndex)

        this.mao.check1AddRemove(this.array, removed, 0, length)
        val actual = this.wrapper.toArray()
        assertEquals(length, this.array.size)
        assertEquals(length, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, length, expected, startIndex)
    }

    @Test
    fun testSetAllARange1() {
        testSetAllARange(INITIAL_SIZE, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllARange2() {
        testSetAllARange(INITIAL_SIZE + 10, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllARange3() {
        testSetAllARange(INITIAL_SIZE + 10, 10, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllARange4() {
        testSetAllARange(INITIAL_SIZE + 10, 2, INITIAL_SIZE + 2)
    }

    @Test
    fun testSetAllARange5() {
        testSetAllARange(INITIAL_SIZE, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testSetAllARange6() {
        testSetAllARange(INITIAL_SIZE + 10, 0, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllARange7() {
        testSetAllARange(INITIAL_SIZE + 20, 10, INITIAL_SIZE + 20)
    }

    @Test
    fun testSetAllARange8() {
        testSetAllARange(INITIAL_SIZE + 10, 2, INITIAL_SIZE - 1)
    }

    @Test
    fun testSetAllARangeOnEmpty() {
        makeEmpty()
        testSetAllARange(INITIAL_SIZE, 1, 4)
    }

    @Test
    fun testSetAllARangeOnEmptyToEmpty() {
        makeEmpty()
        this.wrapper.setAllA(this.wrapper.createPrimitiveArray(INITIAL_SIZE), 1, 1)
        assertUnchanged()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllARangeNegative1() {
        try {
            testSetAllARange(INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllARangeNegative2() {
        try {
            testSetAllARange(INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllARangeNegative3() {
        try {
            testSetAllARange(INITIAL_SIZE, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllARangeNegative4() {
        try {
            testSetAllARange(INITIAL_SIZE, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= setAll(array, range) tests =========================

    private fun testSetAllPRange(newSize: Int, startIndex: Int, endIndex: Int) {
        val removed = this.wrapper.toTypedArray()
        val length = endIndex - startIndex
        val expected = this.wrapper.createArray(newSize)

        this.wrapper.setAllP(expected, startIndex, endIndex)

        this.mao.check1AddRemove(this.array, removed, 0, length)
        val actual = this.wrapper.toArray()
        assertEquals(length, this.array.size)
        assertEquals(length, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, length, expected, startIndex)
    }

    @Test
    fun testSetAllPRange1() {
        testSetAllPRange(INITIAL_SIZE, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllPRange2() {
        testSetAllPRange(INITIAL_SIZE + 10, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllPRange3() {
        testSetAllPRange(INITIAL_SIZE + 10, 10, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllPRange4() {
        testSetAllPRange(INITIAL_SIZE + 10, 2, INITIAL_SIZE + 2)
    }

    @Test
    fun testSetAllPRange5() {
        testSetAllPRange(INITIAL_SIZE, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testSetAllPRange6() {
        testSetAllPRange(INITIAL_SIZE + 10, 0, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllPRange7() {
        testSetAllPRange(INITIAL_SIZE + 20, 10, INITIAL_SIZE + 20)
    }

    @Test
    fun testSetAllPRange8() {
        testSetAllPRange(INITIAL_SIZE + 10, 2, INITIAL_SIZE - 1)
    }

    @Test
    fun testSetAllPRangeOnEmpty() {
        makeEmpty()
        testSetAllPRange(INITIAL_SIZE, 1, 4)
    }

    @Test
    fun testSetAllPRangeOnEmptyToEmpty() {
        makeEmpty()
        this.wrapper.setAllP(this.wrapper.createArray(INITIAL_SIZE), 1, 1)
        assertUnchanged()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllPRangeNegative1() {
        try {
            testSetAllPRange(INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllPRangeNegative2() {
        try {
            testSetAllPRange(INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllPRangeNegative3() {
        try {
            testSetAllPRange(INITIAL_SIZE, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllPRangeNegative4() {
        try {
            testSetAllPRange(INITIAL_SIZE, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= setAll(observable array, range) tests =========================

    private fun testSetAllTRange(newSize: Int, startIndex: Int, endIndex: Int) {
        val removed = this.wrapper.toTypedArray()
        val length = endIndex - startIndex
        val expected = this.wrapper.createPrimitiveArray(newSize)
        val src = this.wrapper.newInstance().createNotEmptyArray(expected)

        this.wrapper.setAllT(src, startIndex, endIndex)

        this.mao.check1AddRemove(this.array, removed, 0, length)
        val actual = this.wrapper.toArray()
        assertEquals(length, this.array.size)
        assertEquals(length, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, length, expected, startIndex)
    }

    @Test
    fun testSetAllTRange1() {
        testSetAllTRange(INITIAL_SIZE, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllTRange2() {
        testSetAllTRange(INITIAL_SIZE + 10, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllTRange3() {
        testSetAllTRange(INITIAL_SIZE + 10, 10, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllTRange4() {
        testSetAllTRange(INITIAL_SIZE + 10, 2, INITIAL_SIZE + 2)
    }

    @Test
    fun testSetAllTRange5() {
        testSetAllTRange(INITIAL_SIZE, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testSetAllTRange6() {
        testSetAllTRange(INITIAL_SIZE + 10, 0, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllTRange7() {
        testSetAllTRange(INITIAL_SIZE + 20, 10, INITIAL_SIZE + 20)
    }

    @Test
    fun testSetAllTRange8() {
        testSetAllTRange(INITIAL_SIZE + 10, 2, INITIAL_SIZE - 1)
    }

    @Test
    fun testSetAllTRangeOnEmpty() {
        makeEmpty()
        testSetAllTRange(INITIAL_SIZE, 1, 4)
    }

    @Test
    fun testSetAllTRangeOnEmptyToEmpty() {
        makeEmpty()
        this.wrapper.setAllT(this.wrapper.newInstance().createNotEmptyArray(
                this.wrapper.createPrimitiveArray(INITIAL_SIZE)), 1, 1)
        assertUnchanged()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeNegative1() {
        try {
            testSetAllTRange(INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeNegative2() {
        try {
            testSetAllTRange(INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeNegative3() {
        try {
            testSetAllTRange(INITIAL_SIZE, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeNegative4() {
        try {
            testSetAllTRange(INITIAL_SIZE, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeNegativeAfterSrcEnsureCapacity() {
        val expected = this.wrapper.createPrimitiveArray(INITIAL_SIZE)
        val src = this.wrapper.newInstance().createNotEmptyArray(expected)
        src.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.setAllT(src, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeNegativeAfterSrcClear() {
        val expected = this.wrapper.createPrimitiveArray(INITIAL_SIZE)
        val src = this.wrapper.newInstance().createNotEmptyArray(expected)
        src.clear()
        try {
            this.wrapper.setAllT(src, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    private fun testSetAllTRangeSelf(startIndex: Int, endIndex: Int) {
        val removed = this.wrapper.toTypedArray()
        val length = endIndex - startIndex
        this.wrapper.setAllT(this.array, startIndex, endIndex)

        if (startIndex == 0 && length == this.initialSize) {
            this.mao.check0()
        } else {
            this.mao.check1AddRemove(this.array, removed, 0, length)
        }
        val actual = this.wrapper.toArray()
        assertEquals(length, this.array.size)
        assertEquals(length, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, length, this.initialElements, startIndex)
    }

    @Test
    fun testSetAllTRangeSelf() {
        testSetAllTRangeSelf(0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllTRangeSelfBeginning() {
        testSetAllTRangeSelf(0, INITIAL_SIZE / 2)
    }

    @Test
    fun testSetAllTRangeSelfTrailing() {
        testSetAllTRangeSelf(INITIAL_SIZE / 2, INITIAL_SIZE)
    }

    @Test
    fun testSetAllTRangeSelfMiddle() {
        testSetAllTRangeSelf(3, 5)
    }

    @Test
    fun testSetAllTRangeSelfEmpty() {
        makeEmpty()
        testSetAllTRangeSelf(0, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegative1() {
        try {
            this.wrapper.setAllT(this.array, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegative2() {
        try {
            this.wrapper.setAllT(this.array, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegative3() {
        try {
            this.wrapper.setAllT(this.array, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegative4() {
        try {
            this.wrapper.setAllT(this.array, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.setAllT(this.array, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegativeAfterClear() {
        makeEmpty()
        try {
            this.wrapper.setAllT(this.array, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= addAll(primitive array) tests =========================

    private fun testAddAllA(srcSize: Int) {
        val src = this.wrapper.createPrimitiveArray(srcSize)
        val oldSize = this.array.size

        this.wrapper.addAllA(src)

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, oldSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, oldSize, newSize, src, 0)
    }

    @Test
    fun testAddAllA0() {
        this.wrapper.addAllA(this.wrapper.createPrimitiveArray(0))
        assertUnchanged()
    }

    @Test
    fun testAddAllA1() {
        testAddAllA(1)
    }

    @Test
    fun testAddAllA3() {
        testAddAllA(3)
    }

    @Test
    fun testAddAllABig() {
        testAddAllA(INITIAL_SIZE * 2)
    }

    @Test
    fun testAddAllASameSize() {
        testAddAllA(INITIAL_SIZE)
    }

    @Test
    fun testAddAllAOnEmpty1() {
        makeEmpty()
        testAddAllA(1)
    }

    @Test
    fun testAddAllAOnEmptySameSize() {
        makeEmpty()
        testAddAllA(INITIAL_SIZE)
    }

    @Test
    fun testAddAllAOnEmptyBig() {
        makeEmpty()
        testAddAllA(INITIAL_SIZE * 3)
    }

    @Test
    fun testAddAllAOnEmpty0() {
        makeEmpty()
        this.wrapper.addAllA(this.wrapper.createPrimitiveArray(0))
        assertUnchanged()
    }

    @Test
    fun testAddAllAManyPoints() {
        for (i in 0 until 65_000) {
            this.wrapper.addAllA(this.wrapper.createPrimitiveArray(3))
        }
    }

    // ========================= addAll(array) tests =========================

    private fun testAddAllP(srcSize: Int) {
        val src = this.wrapper.createArray(srcSize)
        val oldSize = this.array.size

        this.wrapper.addAllP(src)

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, oldSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, oldSize, newSize, src, 0)
    }

    @Test
    fun testAddAllP0() {
        this.wrapper.addAllP(this.wrapper.createArray(0))
        assertUnchanged()
    }

    @Test
    fun testAddAllP1() {
        testAddAllP(1)
    }

    @Test
    fun testAddAllP3() {
        testAddAllP(3)
    }

    @Test
    fun testAddAllPBig() {
        testAddAllP(INITIAL_SIZE * 2)
    }

    @Test
    fun testAddAllPSameSize() {
        testAddAllP(INITIAL_SIZE)
    }

    @Test
    fun testAddAllPOnEmpty1() {
        makeEmpty()
        testAddAllP(1)
    }

    @Test
    fun testAddAllPOnEmptySameSize() {
        makeEmpty()
        testAddAllP(INITIAL_SIZE)
    }

    @Test
    fun testAddAllPOnEmptyBig() {
        makeEmpty()
        testAddAllP(INITIAL_SIZE * 3)
    }

    @Test
    fun testAddAllPOnEmpty0() {
        makeEmpty()
        this.wrapper.addAllP(this.wrapper.createArray(0))
        assertUnchanged()
    }

    @Test
    fun testAddAllPManyPoints() {
        for (i in 0 until 65_000) {
            this.wrapper.addAllP(this.wrapper.createArray(3))
        }
    }

    // ========================= addAll(observable array) tests =========================

    private fun testAddAllT(srcSize: Int) {
        val src = this.wrapper.createPrimitiveArray(srcSize)
        val oldSize = this.array.size

        this.wrapper.addAllT(this.wrapper.newInstance().createNotEmptyArray(src))

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, oldSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, oldSize, newSize, src, 0)
    }

    @Test
    fun testAddAllT0() {
        this.wrapper.addAllT(this.wrapper.newInstance().createEmptyArray())
        assertUnchanged()
    }

    @Test
    fun testAddAllT1() {
        testAddAllT(1)
    }

    @Test
    fun testAddAllT3() {
        testAddAllT(3)
    }

    @Test
    fun testAddAllTBig() {
        testAddAllT(INITIAL_SIZE * 2)
    }

    @Test
    fun testAddAllTSameSize() {
        testAddAllT(INITIAL_SIZE)
    }

    @Test
    fun testAddAllTOnEmpty1() {
        makeEmpty()
        testAddAllT(1)
    }

    @Test
    fun testAddAllTOnEmptySameSize() {
        makeEmpty()
        testAddAllT(INITIAL_SIZE)
    }

    @Test
    fun testAddAllTOnEmptyBig() {
        makeEmpty()
        testAddAllT(INITIAL_SIZE * 3)
    }

    @Test
    fun testAddAllTOnEmpty0() {
        makeEmpty()
        this.wrapper.addAllT(this.wrapper.newInstance().createEmptyArray())
        assertUnchanged()
    }

    @Test
    fun testAddAllTSelf() {
        this.wrapper.addAllT(this.array)

        this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), this.initialSize, this.initialSize * 2)
        assertEquals(this.initialSize * 2, this.array.size)
        val actual = this.wrapper.toArray()
        this.wrapper.assertElementsEqual(actual, 0, this.initialSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, this.initialSize, this.initialSize * 2, this.initialElements, 0)
    }

    @Test
    fun testAddAllTSelfEmpty() {
        makeEmpty()

        this.wrapper.addAllT(this.array)

        this.mao.check0()
        val actual = this.wrapper.toArray()
        assertEquals(0, this.array.size)
        assertEquals(0, this.wrapper.arraySize(actual))
    }

    @Test
    fun testAddAllTManyPoints() {
        for (i in 0 until 65_000) {
            this.wrapper.addAllT(this.wrapper.newInstance().createNotEmptyArray(
                    this.wrapper.createPrimitiveArray(3)))
        }
    }

    // ========================= plusAssign(primitive array) tests =========================

    private fun testPlusAssignA(srcSize: Int) {
        val src = this.wrapper.createPrimitiveArray(srcSize)
        val oldSize = this.array.size

        this.wrapper += src

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, oldSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, oldSize, newSize, src, 0)
    }

    @Test
    fun testPlusAssignA0() {
        this.wrapper += this.wrapper.createPrimitiveArray(0)
        assertUnchanged()
    }

    @Test
    fun testPlusAssignA1() {
        testPlusAssignA(1)
    }

    @Test
    fun testPlusAssignA3() {
        testPlusAssignA(3)
    }

    @Test
    fun testPlusAssignABig() {
        testPlusAssignA(INITIAL_SIZE * 2)
    }

    @Test
    fun testPlusAssignASameSize() {
        testPlusAssignA(INITIAL_SIZE)
    }

    @Test
    fun testPlusAssignAOnEmpty1() {
        makeEmpty()
        testPlusAssignA(1)
    }

    @Test
    fun testPlusAssignAOnEmptySameSize() {
        makeEmpty()
        testPlusAssignA(INITIAL_SIZE)
    }

    @Test
    fun testPlusAssignAOnEmptyBig() {
        makeEmpty()
        testPlusAssignA(INITIAL_SIZE * 3)
    }

    @Test
    fun testPlusAssignAOnEmpty0() {
        makeEmpty()
        this.wrapper += this.wrapper.createPrimitiveArray(0)
        assertUnchanged()
    }

    @Test
    fun testPlusAssignAManyPoints() {
        for (i in 0 until 65_000) {
            this.wrapper += this.wrapper.createPrimitiveArray(3)
        }
    }

    // ========================= plusAssign(array) tests =========================

    private fun testPlusAssignP(srcSize: Int) {
        val src = this.wrapper.createArray(srcSize)
        val oldSize = this.array.size

        this.wrapper += src

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, oldSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, oldSize, newSize, src, 0)
    }

    @Test
    fun testPlusAssignP0() {
        this.wrapper += this.wrapper.createArray(0)
        assertUnchanged()
    }

    @Test
    fun testPlusAssignP1() {
        testPlusAssignP(1)
    }

    @Test
    fun testPlusAssignP3() {
        testPlusAssignP(3)
    }

    @Test
    fun testPlusAssignPBig() {
        testPlusAssignP(INITIAL_SIZE * 2)
    }

    @Test
    fun testPlusAssignPSameSize() {
        testPlusAssignP(INITIAL_SIZE)
    }

    @Test
    fun testPlusAssignPOnEmpty1() {
        makeEmpty()
        testPlusAssignP(1)
    }

    @Test
    fun testPlusAssignPOnEmptySameSize() {
        makeEmpty()
        testPlusAssignP(INITIAL_SIZE)
    }

    @Test
    fun testPlusAssignPOnEmptyBig() {
        makeEmpty()
        testPlusAssignP(INITIAL_SIZE * 3)
    }

    @Test
    fun testPlusAssignPOnEmpty0() {
        makeEmpty()
        this.wrapper += this.wrapper.createArray(0)
        assertUnchanged()
    }

    @Test
    fun testPlusAssignPManyPoints() {
        for (i in 0 until 65_000) {
            this.wrapper += this.wrapper.createArray(3)
        }
    }

    // ========================= plusAssign(observable array) tests =========================

    private fun testPlusAssignT(srcSize: Int) {
        val src = this.wrapper.createPrimitiveArray(srcSize)
        val oldSize = this.array.size

        this.wrapper += this.wrapper.newInstance().createNotEmptyArray(src)

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, oldSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, oldSize, newSize, src, 0)
    }

    @Test
    fun testPlusAssignT0() {
        this.wrapper += this.wrapper.newInstance().createEmptyArray()
        assertUnchanged()
    }

    @Test
    fun testPlusAssignT1() {
        testPlusAssignT(1)
    }

    @Test
    fun testPlusAssignT3() {
        testPlusAssignT(3)
    }

    @Test
    fun testPlusAssignTBig() {
        testPlusAssignT(INITIAL_SIZE * 2)
    }

    @Test
    fun testPlusAssignTSameSize() {
        testPlusAssignT(INITIAL_SIZE)
    }

    @Test
    fun testPlusAssignTOnEmpty1() {
        makeEmpty()
        testPlusAssignT(1)
    }

    @Test
    fun testPlusAssignTOnEmptySameSize() {
        makeEmpty()
        testPlusAssignT(INITIAL_SIZE)
    }

    @Test
    fun testPlusAssignTOnEmptyBig() {
        makeEmpty()
        testPlusAssignT(INITIAL_SIZE * 3)
    }

    @Test
    fun testPlusAssignTOnEmpty0() {
        makeEmpty()
        this.wrapper += this.wrapper.newInstance().createEmptyArray()
        assertUnchanged()
    }

    @Test
    fun testPlusAssignTSelf() {
        this.wrapper += this.array

        this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), this.initialSize, this.initialSize * 2)
        assertEquals(this.initialSize * 2, this.array.size)
        val actual = this.wrapper.toArray()
        this.wrapper.assertElementsEqual(actual, 0, this.initialSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, this.initialSize, this.initialSize * 2, this.initialElements, 0)
    }

    @Test
    fun testPlusAssignTSelfEmpty() {
        makeEmpty()

        this.wrapper += this.array

        this.mao.check0()
        val actual = this.wrapper.toArray()
        assertEquals(0, this.array.size)
        assertEquals(0, this.wrapper.arraySize(actual))
    }

    @Test
    fun testPlusAssignTManyPoints() {
        for (i in 0 until 65_000) {
            this.wrapper += this.wrapper.newInstance().createNotEmptyArray(
                    this.wrapper.createPrimitiveArray(3))
        }
    }

    // ========================= addAll(primitive array, range) tests =========================

    private fun testAddAllARange(srcSize: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val src = this.wrapper.createPrimitiveArray(srcSize)
        val oldSize = this.array.size

        this.wrapper.addAllA(src, startIndex, endIndex)

        val newSize = oldSize + length
        val sizeChanged = newSize != oldSize

        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, oldSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, oldSize, newSize, src, startIndex)
    }

    @Test
    fun testAddAllARange1() {
        testAddAllARange(INITIAL_SIZE, 0, INITIAL_SIZE)
    }

    @Test
    fun testAddAllARange2() {
        testAddAllARange(INITIAL_SIZE + 10, 0, INITIAL_SIZE)
    }

    @Test
    fun testAddAllARange3() {
        testAddAllARange(INITIAL_SIZE + 10, 10, INITIAL_SIZE + 10)
    }

    @Test
    fun testAddAllARange4() {
        testAddAllARange(INITIAL_SIZE + 10, 2, INITIAL_SIZE + 2)
    }

    @Test
    fun testAddAllARange5() {
        testAddAllARange(INITIAL_SIZE, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testAddAllARange6() {
        testAddAllARange(INITIAL_SIZE + 10, 0, INITIAL_SIZE + 10)
    }

    @Test
    fun testAddAllARange7() {
        testAddAllARange(INITIAL_SIZE + 20, 10, INITIAL_SIZE + 20)
    }

    @Test
    fun testAddAllARange8() {
        testAddAllARange(INITIAL_SIZE + 10, 2, INITIAL_SIZE - 1)
    }

    @Test
    fun testAddAllARangeOnEmpty1() {
        makeEmpty()
        testAddAllARange(INITIAL_SIZE, 1, 4)
    }

    @Test
    fun testAddAllARangeOnEmpty2() {
        makeEmpty()
        testAddAllARange(INITIAL_SIZE * 3, INITIAL_SIZE, INITIAL_SIZE * 3)
    }

    @Test
    fun testAddAllARangeOnEmpty3() {
        makeEmpty()
        this.wrapper.addAllA(this.wrapper.createPrimitiveArray(INITIAL_SIZE), 1, 1)
        assertUnchanged()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllARangeNegative1() {
        try {
            testAddAllARange(INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllARangeNegative2() {
        try {
            testAddAllARange(INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllARangeNegative3() {
        try {
            testAddAllARange(INITIAL_SIZE, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllARangeNegative4() {
        try {
            testAddAllARange(INITIAL_SIZE, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= addAll(array, range) tests =========================

    private fun testAddAllPRange(srcSize: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val src = this.wrapper.createArray(srcSize)
        val oldSize = this.array.size

        this.wrapper.addAllP(src, startIndex, endIndex)

        val newSize = oldSize + length
        val sizeChanged = newSize != oldSize

        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, oldSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, oldSize, newSize, src, startIndex)
    }

    @Test
    fun testAddAllPRange1() {
        testAddAllPRange(INITIAL_SIZE, 0, INITIAL_SIZE)
    }

    @Test
    fun testAddAllPRange2() {
        testAddAllPRange(INITIAL_SIZE + 10, 0, INITIAL_SIZE)
    }

    @Test
    fun testAddAllPRange3() {
        testAddAllPRange(INITIAL_SIZE + 10, 10, INITIAL_SIZE + 10)
    }

    @Test
    fun testAddAllPRange4() {
        testAddAllPRange(INITIAL_SIZE + 10, 2, INITIAL_SIZE + 2)
    }

    @Test
    fun testAddAllPRange5() {
        testAddAllPRange(INITIAL_SIZE, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testAddAllPRange6() {
        testAddAllPRange(INITIAL_SIZE + 10, 0, INITIAL_SIZE + 10)
    }

    @Test
    fun testAddAllPRange7() {
        testAddAllPRange(INITIAL_SIZE + 20, 10, INITIAL_SIZE + 20)
    }

    @Test
    fun testAddAllPRange8() {
        testAddAllPRange(INITIAL_SIZE + 10, 2, INITIAL_SIZE - 1)
    }

    @Test
    fun testAddAllPRangeOnEmpty1() {
        makeEmpty()
        testAddAllPRange(INITIAL_SIZE, 1, 4)
    }

    @Test
    fun testAddAllPRangeOnEmpty2() {
        makeEmpty()
        testAddAllPRange(INITIAL_SIZE * 3, INITIAL_SIZE, INITIAL_SIZE * 3)
    }

    @Test
    fun testAddAllPRangeOnEmpty3() {
        makeEmpty()
        this.wrapper.addAllP(this.wrapper.createArray(INITIAL_SIZE), 1, 1)
        assertUnchanged()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllPRangeNegative1() {
        try {
            testAddAllPRange(INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllPRangeNegative2() {
        try {
            testAddAllPRange(INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllPRangeNegative3() {
        try {
            testAddAllPRange(INITIAL_SIZE, 1, -1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllPRangeNegative4() {
        try {
            testAddAllPRange(INITIAL_SIZE, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= addAll(observable array, range) tests =========================

    private fun testAddAllTRange(srcSize: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val src = this.wrapper.createPrimitiveArray(srcSize)
        val oldSize = this.array.size

        this.wrapper.addAllT(this.wrapper.newInstance().createNotEmptyArray(src), startIndex, endIndex)

        val newSize = oldSize + length
        val sizeChanged = newSize != oldSize

        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, oldSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, oldSize, newSize, src, startIndex)
    }

    @Test
    fun testAddAllTRange1() {
        testAddAllTRange(INITIAL_SIZE, 0, INITIAL_SIZE)
    }

    @Test
    fun testAddAllTRange2() {
        testAddAllTRange(INITIAL_SIZE + 10, 0, INITIAL_SIZE)
    }

    @Test
    fun testAddAllTRange3() {
        testAddAllTRange(INITIAL_SIZE + 10, 10, INITIAL_SIZE + 10)
    }

    @Test
    fun testAddAllTRange4() {
        testAddAllTRange(INITIAL_SIZE + 10, 2, INITIAL_SIZE + 2)
    }

    @Test
    fun testAddAllTRange5() {
        testAddAllTRange(INITIAL_SIZE, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testAddAllTRange6() {
        testAddAllTRange(INITIAL_SIZE + 10, 0, INITIAL_SIZE + 10)
    }

    @Test
    fun testAddAllTRange7() {
        testAddAllTRange(INITIAL_SIZE + 20, 10, INITIAL_SIZE + 20)
    }

    @Test
    fun testAddAllTRange8() {
        testAddAllTRange(INITIAL_SIZE + 10, 2, INITIAL_SIZE - 1)
    }

    @Test
    fun testAddAllTRangeOnEmpty1() {
        makeEmpty()
        testAddAllTRange(INITIAL_SIZE, 1, 4)
    }

    @Test
    fun testAddAllTRangeOnEmpty2() {
        makeEmpty()
        testAddAllTRange(INITIAL_SIZE * 3, INITIAL_SIZE, INITIAL_SIZE * 3)
    }

    @Test
    fun testAddAllTRangeOnEmpty3() {
        makeEmpty()
        this.wrapper.addAllT(
                this.wrapper.newInstance().createNotEmptyArray(this.wrapper.createPrimitiveArray(INITIAL_SIZE)), 1, 1)
        assertUnchanged()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeNegative1() {
        try {
            testAddAllTRange(INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeNegative2() {
        try {
            testAddAllTRange(INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeNegative3() {
        try {
            testAddAllTRange(INITIAL_SIZE, 1, -1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeNegative4() {
        try {
            testAddAllTRange(INITIAL_SIZE, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeNegativeAfterSrcEnsureCapacity() {
        val srcA = this.wrapper.createPrimitiveArray(INITIAL_SIZE)
        val src = this.wrapper.newInstance().createNotEmptyArray(srcA)
        src.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.addAllT(src, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeNegativeAfterSrcClear() {
        val srcA = this.wrapper.createPrimitiveArray(INITIAL_SIZE)
        val src = this.wrapper.newInstance().createNotEmptyArray(srcA)
        src.clear()
        try {
            this.wrapper.addAllT(src, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    private fun testAddAllTRangeSelf(startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        this.wrapper.addAllT(this.array, startIndex, endIndex)

        val expSize = this.initialSize + length
        this.mao.check1AddRemove(this.array, this.wrapper.createArray(0), this.initialSize, this.initialSize + length)
        val actual = this.wrapper.toArray()
        assertEquals(expSize, this.array.size)
        assertEquals(expSize, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, this.initialSize, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, this.initialSize, expSize, this.initialElements, startIndex)
    }

    @Test
    fun testAddAllTRangeSelf() {
        testAddAllTRangeSelf(0, INITIAL_SIZE)
    }

    @Test
    fun testAddAllTRangeSelfBeginning() {
        testAddAllTRangeSelf(0, INITIAL_SIZE / 2)
    }

    @Test
    fun testAddAllTRangeSelfTrailing() {
        testAddAllTRangeSelf(INITIAL_SIZE / 2, INITIAL_SIZE)
    }

    @Test
    fun testAddAllTRangeSelfMiddle() {
        testAddAllTRangeSelf(2, 4)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegative1() {
        try {
            testAddAllTRangeSelf(-1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegative2() {
        try {
            testAddAllTRangeSelf(0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegative3() {
        try {
            testAddAllTRangeSelf(1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegative4() {
        try {
            testAddAllTRangeSelf(INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegativeAfterSrcEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.addAllT(this.array, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegativeAfterSrcClear() {
        makeEmpty()
        try {
            this.wrapper.addAllT(this.array, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= set(primitive array, range) tests =========================

    private fun testSetARange(srcSize: Int, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val removedOld = this.wrapper.toTypedArray()
        val expected = this.wrapper.createPrimitiveArray(srcSize)

        this.wrapper.setA(expected, destinationOffset, startIndex, endIndex)

        val removed = removedOld.copyOfRange(destinationOffset, destinationOffset + length)

        this.mao.check1AddRemove(this.array, removed, destinationOffset, destinationOffset + length)
        val actual = this.wrapper.toArray()
        assertEquals(INITIAL_SIZE, this.array.size)
        assertEquals(INITIAL_SIZE, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, destinationOffset, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, destinationOffset, destinationOffset + length, expected, startIndex)
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, INITIAL_SIZE, this.initialElements,
                destinationOffset + length)
    }

    @Test
    fun testSetARange1() {
        testSetARange(5, 0, 0, 5)
    }

    @Test
    fun testSetARange2() {
        testSetARange(3, 2, 0, 3)
    }

    @Test
    fun testSetARange3() {
        testSetARange(5, 0, 2, 5)
    }

    @Test
    fun testSetARange4() {
        testSetARange(5, 0, 0, 3)
    }

    @Test
    fun testSetARange5() {
        testSetARange(10, 3, 5, 8)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetARangeNegative1() {
        try {
            testSetARange(10, -1, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetARangeNegative2() {
        try {
            testSetARange(10, 0, -1, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetARangeNegative3() {
        try {
            testSetARange(10, 1, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetARangeNegative4() {
        try {
            testSetARange(10, INITIAL_SIZE, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetARangeNegative5() {
        try {
            testSetARange(10, 0, 10, 11)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetARangeNegative6() {
        try {
            testSetARange(3, 0, 1, 5)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetARangeNegative7() {
        try {
            testSetARange(10, INITIAL_SIZE - 3, 0, 4)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetARangeNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            testSetARange(10, INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetARangeNegativeAfterClear() {
        makeEmpty()
        try {
            testSetARange(1, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= set(array, range) tests =========================

    private fun testSetPRange(srcSize: Int, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val removed = this.wrapper.toTypedArray().copyOfRange(destinationOffset, destinationOffset + length)
        val expected = this.wrapper.createArray(srcSize)

        this.wrapper.setP(expected, destinationOffset, startIndex, endIndex)

        this.mao.check1AddRemove(this.array, removed, destinationOffset, destinationOffset + length)
        val actual = this.wrapper.toArray()
        assertEquals(INITIAL_SIZE, this.array.size)
        assertEquals(INITIAL_SIZE, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, destinationOffset, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, destinationOffset, destinationOffset + length, expected, startIndex)
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, INITIAL_SIZE, this.initialElements,
                destinationOffset + length)
    }

    @Test
    fun testSetPRange1() {
        testSetPRange(5, 0, 0, 5)
    }

    @Test
    fun testSetPRange2() {
        testSetPRange(3, 2, 0, 3)
    }

    @Test
    fun testSetPRange3() {
        testSetPRange(5, 0, 2, 5)
    }

    @Test
    fun testSetPRange4() {
        testSetPRange(5, 0, 0, 3)
    }

    @Test
    fun testSetPRange5() {
        testSetPRange(10, 3, 5, 8)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetPRangeNegative1() {
        try {
            testSetPRange(10, -1, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetPRangeNegative2() {
        try {
            testSetPRange(10, 0, -1, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetPRangeNegative3() {
        try {
            testSetPRange(10, 1, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetPRangeNegative4() {
        try {
            testSetPRange(10, INITIAL_SIZE, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetPRangeNegative5() {
        try {
            testSetPRange(10, 0, 10, 11)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetPRangeNegative6() {
        try {
            testSetPRange(3, 0, 1, 5)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetPRangeNegative7() {
        try {
            testSetPRange(10, INITIAL_SIZE - 3, 0, 4)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetPRangeNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            testSetPRange(10, INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetPRangeNegativeAfterClear() {
        makeEmpty()
        try {
            testSetPRange(1, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= set(observable array, range) tests =========================

    private fun testSetTRange(srcSize: Int, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val removed = this.wrapper.toTypedArray().copyOfRange(destinationOffset, destinationOffset + length)
        val expected = this.wrapper.createPrimitiveArray(srcSize)
        val src = this.wrapper.newInstance().createNotEmptyArray(expected)

        this.wrapper.setT(src, destinationOffset, startIndex, endIndex)

        this.mao.check1AddRemove(this.array, removed, destinationOffset, destinationOffset + length)
        val actual = this.wrapper.toArray()
        assertEquals(INITIAL_SIZE, this.array.size)
        assertEquals(INITIAL_SIZE, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, destinationOffset, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, destinationOffset, destinationOffset + length, expected, startIndex)
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, INITIAL_SIZE, this.initialElements,
                destinationOffset + length)
    }

    @Test
    fun testSetTRange1() {
        testSetTRange(5, 0, 0, 5)
    }

    @Test
    fun testSetTRange2() {
        testSetTRange(3, 2, 0, 3)
    }

    @Test
    fun testSetTRange3() {
        testSetTRange(5, 0, 2, 5)
    }

    @Test
    fun testSetTRange4() {
        testSetTRange(5, 0, 0, 3)
    }

    @Test
    fun testSetTRange5() {
        testSetTRange(10, 3, 5, 8)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegative1() {
        try {
            testSetTRange(10, -1, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegative2() {
        try {
            testSetTRange(10, 0, -1, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetTRangeNegative3() {
        try {
            testSetTRange(10, 1, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegative4() {
        try {
            testSetTRange(10, INITIAL_SIZE, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegative5() {
        try {
            testSetTRange(10, 0, 10, 11)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegative6() {
        try {
            testSetTRange(3, 0, 1, 5)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegative7() {
        try {
            testSetTRange(10, INITIAL_SIZE - 3, 0, 4)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            testSetTRange(10, INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegativeAfterClear() {
        makeEmpty()
        try {
            testSetTRange(1, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegativeAfterSrcEnsureCapacity() {
        val srcA = this.wrapper.createPrimitiveArray(1)
        val src = this.wrapper.newInstance().createNotEmptyArray(srcA)
        src.ensureCapacity(2)
        try {
            this.wrapper.setT(src, 0, 1, 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeNegativeAfterSrcClear() {
        val srcA = this.wrapper.createPrimitiveArray(1)
        val src = this.wrapper.newInstance().createNotEmptyArray(srcA)
        src.clear()
        try {
            this.wrapper.setT(src, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    private fun testSetTRangeSelf(destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val removed = this.wrapper.toTypedArray().copyOfRange(destinationOffset, destinationOffset + length)
        this.wrapper.setT(this.array, destinationOffset, startIndex, endIndex)

        this.mao.check1AddRemove(this.array, removed, destinationOffset, destinationOffset + length)
        val actual = this.wrapper.toArray()
        assertEquals(INITIAL_SIZE, this.array.size)
        assertEquals(INITIAL_SIZE, this.wrapper.arraySize(actual))
        this.wrapper.assertElementsEqual(actual, 0, destinationOffset, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, destinationOffset, destinationOffset + length, this.initialElements,
                startIndex)
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, INITIAL_SIZE, this.initialElements,
                destinationOffset + length)
    }

    @Test
    fun testSetTRangeSelf() {
        testSetTRangeSelf(0, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetTRangeSelfLeft() {
        testSetTRangeSelf(0, 1, INITIAL_SIZE)
    }

    @Test
    fun testSetTRangeSelfRight() {
        testSetTRangeSelf(1, 0, INITIAL_SIZE - 1)
    }

    @Test
    fun testSetTRangeSelfRightDifferentParts() {
        testSetTRangeSelf(0, INITIAL_SIZE / 2, INITIAL_SIZE)
    }

    @Test
    fun testSetTRangeSelfLeftDifferentParts() {
        testSetTRangeSelf(INITIAL_SIZE / 2, 0, INITIAL_SIZE / 2)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative1() {
        try {
            this.wrapper.setT(this.array, -1, 0, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative2() {
        try {
            this.wrapper.setT(this.array, 0, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative3() {
        try {
            this.wrapper.setT(this.array, 0, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetTRangeSelfNegative4() {
        try {
            this.wrapper.setT(this.array, 0, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative5() {
        try {
            this.wrapper.setT(this.array, INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetTRangeSelfNegative6() {
        try {
            this.wrapper.setT(this.array, 0, INITIAL_SIZE, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.setT(this.array, 0, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegativeAfterClear() {
        makeEmpty()
        try {
            this.wrapper.setT(this.array, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= negative get(index) tests =========================

    @Test(expected = IndexOutOfBoundsException::class)
    fun testGetNegative() {
        try {
            this.wrapper[-1]
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testGetOutOfBounds() {
        try {
            this.wrapper[this.array.size]
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testGetAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper[INITIAL_SIZE]
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testGetAfterClear() {
        makeEmpty()
        try {
            this.wrapper[0]
        } finally {
            assertUnchanged()
        }
    }

    // ========================= set(index) tests =========================

    @Test
    fun testSetValue() {
        val removed = this.wrapper.createArray(1)
        for (i in 0 until INITIAL_SIZE) {
            removed[0] = this.wrapper[i]
            val expected = this.wrapper.nextValue

            this.wrapper[i] = expected

            this.mao.check1AddRemove(this.array, removed, i, i + 1)
            this.mao.reset()
            assertEquals(expected, this.wrapper[i])
            assertEquals(INITIAL_SIZE, this.array.size)
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetValueNegative() {
        try {
            this.wrapper[-1] = this.wrapper.nextValue
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetValueOutOfBounds() {
        try {
            this.wrapper[this.array.size] = this.wrapper.nextValue
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetValueAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper[INITIAL_SIZE] = this.wrapper.nextValue
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testSetValueAfterClear() {
        makeEmpty()
        try {
            this.wrapper[0] = this.wrapper.nextValue
        } finally {
            assertUnchanged()
        }
    }

    // ========================= toArray range tests =========================

    private fun testToArrayRange(startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val actual = this.wrapper.toArray(startIndex, endIndex)
        assertUnchanged()
        this.wrapper.assertElementsEqual(actual, 0, length, this.initialElements, startIndex)
    }

    @Test
    fun testToArrayRange0() {
        testToArrayRange(0, this.array.size)
    }

    @Test
    fun testToArrayRange1() {
        testToArrayRange(3, this.array.size)
    }

    @Test
    fun testToArrayRange2() {
        testToArrayRange(0, this.array.size - 3)
    }

    @Test
    fun testToArrayRange3() {
        testToArrayRange(2, 4)
    }

    @Test
    fun testToArrayRange4() {
        testToArrayRange(2, 2)
    }

    @Test
    fun testToArrayRange5() {
        makeEmpty()
        testToArrayRange(0, 0)
    }

    @Test
    fun testToArrayRange6() {
        testToArrayRange(3, 5)
    }

    @Test
    fun testToArrayRange7() {
        testToArrayRange(5, 6)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testToArrayRangeNegative1() {
        try {
            testToArrayRange(-1, 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testToArrayRangeNegative2() {
        try {
            testToArrayRange(this.array.size, this.array.size + 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testToArrayRangeNegative3() {
        try {
            testToArrayRange(5, this.array.size + 6)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testToArrayRangeNegative4() {
        makeEmpty()
        try {
            testToArrayRange(2, 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testToArrayRangeNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            testToArrayRange(INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testToArrayRangeNegativeAfterClear() {
        makeEmpty()
        try {
            testToArrayRange(0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= copyInto(primitive array) tests =========================

    private fun testCopyIntoA(destSize: Int, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val actual = this.wrapper.createPrimitiveArray(destSize)
        val initial = this.wrapper.clonePrimitiveArray(actual)
        this.wrapper.copyIntoA(actual, destinationOffset, startIndex, endIndex)
        assertUnchanged()
        val length = endIndex - startIndex
        val expected = this.wrapper.toArray()
        this.wrapper.assertElementsEqual(actual, 0, destinationOffset, initial, 0)
        this.wrapper.assertElementsEqual(actual, destinationOffset, destinationOffset + length, expected, startIndex)
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, this.wrapper.arraySize(actual), initial,
                destinationOffset + length)
    }

    @Test
    fun testCopyIntoA1() {
        testCopyIntoA(this.array.size, 0, 0, this.array.size)
    }

    @Test
    fun testCopyIntoA2() {
        testCopyIntoA(this.array.size, 2, 1, 4)
    }

    @Test
    fun testCopyIntoA3() {
        testCopyIntoA(this.array.size, 2, 2, 4)
    }

    @Test
    fun testCopyIntoA4() {
        testCopyIntoA(this.array.size, 2, 0, 2)
    }

    @Test
    fun testCopyIntoA5() {
        testCopyIntoA(3, 1, 0, 2)
    }

    @Test
    fun testCopyIntoA6() {
        testCopyIntoA(this.array.size * 3, this.array.size * 2, 0, this.array.size)
    }

    @Test
    fun testCopyIntoA7() {
        testCopyIntoA(this.array.size, 0, 3, this.array.size)
    }

    @Test
    fun testCopyIntoA8() {
        testCopyIntoA(10, 7, 0, 3)
    }

    @Test
    fun testCopyIntoA9() {
        testCopyIntoA(0, 0, 1, 1)
    }

    @Test
    fun testCopyIntoA10() {
        makeEmpty()
        testCopyIntoA(0, 0, 0, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegative1() {
        try {
            testCopyIntoA(this.array.size, 0, -1, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegative2() {
        try {
            testCopyIntoA(this.array.size / 2, 0, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegative3() {
        try {
            testCopyIntoA(this.array.size, 0, this.array.size, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegative4() {
        try {
            testCopyIntoA(this.array.size, -1, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegative5() {
        try {
            testCopyIntoA(this.array.size, this.array.size, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegative6() {
        try {
            testCopyIntoA(this.array.size, 0, 0, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegative7() {
        makeEmpty()
        try {
            testCopyIntoA(0, 0, 1, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegative8() {
        try {
            testCopyIntoA(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegativeAfterEnsureCapacity() {
        try {
            testCopyIntoA(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoANegativeAfterClear() {
        makeEmpty()
        try {
            testCopyIntoA(1, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= copyInto(array) tests =========================

    private fun testCopyIntoP(destSize: Int, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val actual = this.wrapper.createArray(destSize)
        val initial = this.wrapper.cloneArray(actual)
        this.wrapper.copyIntoP(actual, destinationOffset, startIndex, endIndex)
        assertUnchanged()
        val length = endIndex - startIndex
        val expected = this.wrapper.toArray()
        this.wrapper.assertElementsEqual(actual, 0, destinationOffset, initial, 0)
        this.wrapper.assertElementsEqual(actual, destinationOffset, destinationOffset + length, expected, startIndex)
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, actual.size, initial,
                destinationOffset + length)
    }

    @Test
    fun testCopyIntoP1() {
        testCopyIntoP(this.array.size, 0, 0, this.array.size)
    }

    @Test
    fun testCopyIntoP2() {
        testCopyIntoP(this.array.size, 2, 1, 4)
    }

    @Test
    fun testCopyIntoP3() {
        testCopyIntoP(this.array.size, 2, 2, 4)
    }

    @Test
    fun testCopyIntoP4() {
        testCopyIntoP(this.array.size, 2, 0, 2)
    }

    @Test
    fun testCopyIntoP5() {
        testCopyIntoP(3, 1, 0, 2)
    }

    @Test
    fun testCopyIntoP6() {
        testCopyIntoP(this.array.size * 3, this.array.size * 2, 0, this.array.size)
    }

    @Test
    fun testCopyIntoP7() {
        testCopyIntoP(this.array.size, 0, 3, this.array.size)
    }

    @Test
    fun testCopyIntoP8() {
        testCopyIntoP(10, 7, 0, 3)
    }

    @Test
    fun testCopyIntoP9() {
        testCopyIntoP(0, 0, 1, 1)
    }

    @Test
    fun testCopyIntoP10() {
        makeEmpty()
        testCopyIntoP(0, 0, 0, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegative1() {
        try {
            testCopyIntoP(this.array.size, 0, -1, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegative2() {
        try {
            testCopyIntoP(this.array.size / 2, 0, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegative3() {
        try {
            testCopyIntoP(this.array.size, 0, this.array.size, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegative4() {
        try {
            testCopyIntoP(this.array.size, -1, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegative5() {
        try {
            testCopyIntoP(this.array.size, this.array.size, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegative6() {
        try {
            testCopyIntoP(this.array.size, 0, 0, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegative7() {
        makeEmpty()
        try {
            testCopyIntoP(0, 0, 1, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegative8() {
        try {
            testCopyIntoP(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegativeAfterEnsureCapacity() {
        try {
            testCopyIntoP(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoPNegativeAfterClear() {
        makeEmpty()
        try {
            testCopyIntoP(1, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= copyInto(primitive array) tests =========================

    private fun testCopyIntoT(destSize: Int, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val wrapper2 = this.wrapper.newInstance()
        val initial = wrapper2.createPrimitiveArray(destSize)
        val dest = wrapper2.createNotEmptyArray(initial)

        this.wrapper.copyIntoT(dest, destinationOffset, startIndex, endIndex)

        assertUnchanged()
        val length = endIndex - startIndex
        val expected = this.wrapper.toArray()
        val actual = wrapper2.toArray()
        this.wrapper.assertElementsEqual(actual, 0, destinationOffset, initial, 0)
        this.wrapper.assertElementsEqual(actual, destinationOffset, destinationOffset + length, expected, startIndex)
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, this.wrapper.arraySize(actual), initial,
                destinationOffset + length)
    }

    @Test
    fun testCopyIntoT1() {
        testCopyIntoT(this.array.size, 0, 0, this.array.size)
    }

    @Test
    fun testCopyIntoT2() {
        testCopyIntoT(this.array.size, 2, 1, 4)
    }

    @Test
    fun testCopyIntoT3() {
        testCopyIntoT(this.array.size, 2, 2, 4)
    }

    @Test
    fun testCopyIntoT4() {
        testCopyIntoT(this.array.size, 2, 0, 2)
    }

    @Test
    fun testCopyIntoT5() {
        testCopyIntoT(3, 1, 0, 2)
    }

    @Test
    fun testCopyIntoT6() {
        testCopyIntoT(this.array.size * 3, this.array.size * 2, 0, this.array.size)
    }

    @Test
    fun testCopyIntoT7() {
        testCopyIntoT(this.array.size, 0, 3, this.array.size)
    }

    @Test
    fun testCopyIntoT8() {
        testCopyIntoT(10, 7, 0, 3)
    }

    @Test
    fun testCopyIntoT9() {
        testCopyIntoT(0, 0, 1, 1)
    }

    @Test
    fun testCopyIntoT10() {
        makeEmpty()
        testCopyIntoT(0, 0, 0, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegative1() {
        try {
            testCopyIntoT(this.array.size, 0, -1, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegative2() {
        try {
            testCopyIntoT(this.array.size / 2, 0, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegative3() {
        try {
            testCopyIntoT(this.array.size, 0, this.array.size, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegative4() {
        try {
            testCopyIntoT(this.array.size, -1, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegative5() {
        try {
            testCopyIntoT(this.array.size, this.array.size, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegative6() {
        try {
            testCopyIntoT(this.array.size, 0, 0, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegative7() {
        makeEmpty()
        try {
            testCopyIntoT(0, 0, 1, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegative8() {
        try {
            testCopyIntoT(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegativeAfterEnsureCapacity() {
        try {
            testCopyIntoT(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegativeAfterClear() {
        makeEmpty()
        try {
            testCopyIntoT(1, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegativeAfterDestEnsureCapacity() {
        val wrapper2 = this.wrapper.newInstance()
        val initial = wrapper2.createPrimitiveArray(1)
        val dest = wrapper2.createNotEmptyArray(initial)
        dest.ensureCapacity(2)
        try {
            this.wrapper.copyIntoT(dest, 1, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTNegativeAfterDestClear() {
        val wrapper2 = this.wrapper.newInstance()
        val initial = wrapper2.createPrimitiveArray(1)
        val dest = wrapper2.createNotEmptyArray(initial)
        dest.clear()
        try {
            this.wrapper.copyIntoT(dest, 1, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    private fun testCopyIntoTSelf(destinationOffset: Int, startIndex: Int, endIndex: Int) {
        this.wrapper.copyIntoT(this.array, destinationOffset, startIndex, endIndex)

        val length = endIndex - startIndex
        val actual = this.wrapper.toArray()
        this.wrapper.assertElementsEqual(actual, 0, destinationOffset, this.initialElements, 0)
        this.wrapper.assertElementsEqual(actual, destinationOffset, destinationOffset + length, this.initialElements,
                startIndex)
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, this.wrapper.arraySize(actual),
                this.initialElements, destinationOffset + length)
    }

    @Test
    fun testCopyIntoTSelf() {
        testCopyIntoTSelf(0, 0, INITIAL_SIZE)
    }

    @Test
    fun testCopyIntoTSelfRight() {
        testCopyIntoTSelf(1, 0, INITIAL_SIZE - 1)
    }

    @Test
    fun testCopyIntoTSelfLeft() {
        testCopyIntoTSelf(0, 1, INITIAL_SIZE - 1)
    }

    @Test
    fun testCopyIntoTSelfRightDifferentParts() {
        testCopyIntoTSelf(INITIAL_SIZE / 2, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testCopyIntoTSelfLeftDifferentParts() {
        testCopyIntoTSelf(0, INITIAL_SIZE / 2, INITIAL_SIZE / 2)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative1() {
        try {
            testCopyIntoTSelf(0, -1, INITIAL_SIZE - 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative2() {
        try {
            testCopyIntoTSelf(0, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative3() {
        try {
            testCopyIntoTSelf(-1, 0, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative4() {
        try {
            testCopyIntoTSelf(INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCopyIntoTSelfNegative5() {
        try {
            testCopyIntoTSelf(1, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative6() {
        try {
            testCopyIntoTSelf(1, 0, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative7() {
        try {
            testCopyIntoTSelf(0, 1, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            testCopyIntoTSelf(INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegativeAfterClear() {
        makeEmpty()
        try {
            testCopyIntoTSelf(0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= ensureCapacity() and trimToSize() tests =========================

    @Test
    fun testTrimToSize() {
        this.array.trimToSize()
        assertUnchanged()
    }

    @Test
    fun testTrimToSizeEmpty() {
        makeEmpty()
        this.array.trimToSize()
        assertUnchanged()
    }

    @Test
    fun testTrimToSizeResize() {
        this.array.resize(3)
        this.initialSize = 3
        this.mao.reset()
        this.array.trimToSize()
        assertUnchanged()
    }

    @Test
    fun testTrimToSizeAddRemove() {
        this.array.resize(1000)
        this.array.resize(INITIAL_SIZE)
        this.mao.reset()
        this.array.trimToSize()
        assertUnchanged()
    }

    @Test
    fun testEnsureCapacity0() {
        this.array.ensureCapacity(0)
        assertUnchanged()
    }

    @Test
    fun testEnsureCapacityBy1() {
        this.array.ensureCapacity(INITIAL_SIZE + 1)
        assertUnchanged()
    }

    @Test
    fun testEnsureCapacity1000() {
        this.array.ensureCapacity(1000)
        assertUnchanged()
    }

    @Test
    fun testEnsureCapacitySmaller() {
        this.array.ensureCapacity(INITIAL_SIZE / 2)
        assertUnchanged()
    }

    @Test
    fun testEnsureCapacityNegative() {
        this.array.ensureCapacity(-1000)
        assertUnchanged()
    }

    @Test
    fun testEnsureCapacityOnEmpty() {
        makeEmpty()
        this.array.ensureCapacity(100)
        assertUnchanged()
    }

    @Test
    fun testEnsureCapacityOnEmpty0() {
        makeEmpty()
        this.array.ensureCapacity(0)
        assertUnchanged()
    }

    @Test
    fun testEnsureCapacityOnEmptyNegative() {
        makeEmpty()
        this.array.ensureCapacity(-1)
        assertUnchanged()
    }

    @Test
    fun testTrimToSizeEnsureCapacity() {
        this.array.ensureCapacity(1000)
        this.array.trimToSize()
        assertUnchanged()
    }

    // ========================= clear() tests =========================

    @Test
    fun testClearEmpty() {
        makeEmpty()
        this.array.clear()
        this.mao.check0()
        assertEquals(0, this.array.size)
    }

    @Test
    fun testClear1000() {
        this.array.resize(1000)
        val removed = this.array.toTypedArray()
        this.mao.reset()

        this.array.clear()

        this.mao.check1AddRemove(this.array, removed, 0, 0)
        assertEquals(0, this.array.size)
    }

    // ========================= toString() tests =========================

    @Test
    fun testToString() {
        val actual = this.array.toString()
        val expected = this.wrapper.primitiveArrayToString(this.wrapper.toArray())
        assertEquals(expected, actual)
        if (this.wrapper is BooleanArrayWrapper) {
            val regex = "\\[(false|true)(, (false|true)){${this.initialSize - 1}}]"
            assertTrue(actual.matches(regex.toRegex()),
                    "toString() output matches to regex '$regex'. Actual = '$actual'")
        } else {
            val regex = "\\[(-)?[0-9]+(\\.[0-9]+)?(, (-)?[0-9]+(.[0-9]+)?){${this.initialSize - 1}}]"
            assertTrue(actual.matches(regex.toRegex()),
                    "toString() output matches to regex '$regex'. Actual = '$actual'")
        }
    }

    @Test
    fun testToStringAfterResize() {
        this.array.resize(this.initialSize / 2)
        val actual = this.array.toString()
        val expected = this.wrapper.primitiveArrayToString(this.wrapper.toArray())
        assertEquals(expected, actual)
        if (this.wrapper is BooleanArrayWrapper) {
            val regex = "\\[(false|true)(, (false|true)){${this.array.size - 1}}]"
            assertTrue(actual.matches(regex.toRegex()),
                    "toString() output matches to regex '$regex'. Actual = '$actual'")
        } else {
            val regex = "\\[(-)?[0-9]+(\\.[0-9]+)?(, (-)?[0-9]+(.[0-9]+)?){${this.array.size - 1}}]"
            assertTrue(actual.matches(regex.toRegex()),
                    "toString() output matches to regex '$regex'. Actual = '$actual'")
        }
    }

    @Test
    fun testToStringAfterClear() {
        this.array.clear()
        val actual = this.array.toString()
        assertEquals("[]", actual)
    }

    // ========================= implementations for the tests =========================

    /**
     * @param T ObservableArray subclass
     * @param A corresponding primitive array
     * @param P corresponding class for boxed elements
     */
    abstract class ArrayWrapper<T : ObservableArray<P>, A, P> {

        protected lateinit var array: T

        abstract fun createEmptyArray(): T

        abstract fun createNotEmptyArray(src: A): T

        abstract fun newInstance(): ArrayWrapper<T, A, P>

        abstract val nextValue: P

        abstract operator fun set(index: Int, value: P)

        abstract fun setAllA(src: A)

        abstract fun setAllP(src: Array<P>)

        abstract fun setAllT(src: T)

        abstract fun setAllA(src: A, startIndex: Int, endIndex: Int)

        abstract fun setAllP(src: Array<P>, startIndex: Int, endIndex: Int)

        abstract fun setAllT(src: T, startIndex: Int, endIndex: Int)

        abstract fun addAllA(src: A)

        abstract fun addAllP(src: Array<P>)

        abstract fun addAllT(src: T)

        abstract operator fun plusAssign(src: A)

        abstract operator fun plusAssign(src: Array<P>)

        abstract operator fun plusAssign(src: T)

        abstract fun addAllA(src: A, startIndex: Int, endIndex: Int)

        abstract fun addAllP(src: Array<P>, startIndex: Int, endIndex: Int)

        abstract fun addAllT(src: T, startIndex: Int, endIndex: Int)

        abstract fun setA(src: A, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract fun setP(src: Array<P>, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract fun setT(src: T, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract fun copyIntoA(dest: A, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract fun copyIntoP(dest: Array<P>, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract fun copyIntoT(dest: T, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract operator fun get(index: Int): P

        abstract fun toArray(): A

        abstract fun toArray(startIndex: Int, endIndex: Int): A

        abstract fun toTypedArray(): Array<P>

        abstract fun toTypedArray(array: A, startIndex: Int, endIndex: Int): Array<P>

        fun createPrimitiveArray(size: Int): A {
            return createPrimitiveArray(size, true)
        }

        abstract fun createPrimitiveArray(size: Int, fillWithData: Boolean): A

        fun createArray(size: Int): Array<P> {
            return createArray(size, true)
        }

        abstract fun createArray(size: Int, fillWithData: Boolean): Array<P>

        abstract fun clonePrimitiveArray(array: A): A

        abstract fun cloneArray(array: Array<P>): Array<P>

        abstract fun arraySize(array: A): Int

        abstract fun get(array: A, index: Int): P

        abstract fun assertElementsEqual(actual: A, from: Int, to: Int, expected: A, expFrom: Int)

        abstract fun assertElementsEqual(actual: A, from: Int, to: Int, expected: Array<P>, expFrom: Int)

        abstract fun assertElementsEqual(actual: Array<P>, from: Int, to: Int, expected: A, expFrom: Int)

        abstract fun assertElementsEqual(actual: Array<P>, from: Int, to: Int, expected: Array<P>, expFrom: Int)

        abstract fun primitiveArrayToString(array: A): String

    }

    private class BooleanArrayWrapper : ArrayWrapper<ObservableBooleanArray, BooleanArray, Boolean>() {

        private var counter = 0

        private var nextValueState: Boolean = false

        override fun createEmptyArray(): ObservableBooleanArray {
            return ObservableCollections.observableBooleanArray().also { this.array = it }
        }

        override fun createNotEmptyArray(src: BooleanArray): ObservableBooleanArray {
            return when (this.counter % 3) {
                0 -> ObservableCollections.observableBooleanArray(*src)
                1 -> ObservableCollections.observableBooleanArray(src.toTypedArray())
                else -> ObservableCollections.observableBooleanArray(ObservableCollections.observableBooleanArray(*src))
            }.also { this.array = it }
        }

        override fun newInstance(): ArrayWrapper<ObservableBooleanArray, BooleanArray, Boolean> {
            return BooleanArrayWrapper()
        }

        override val nextValue: Boolean
            get() {
                this.counter++
                this.nextValueState = !this.nextValueState
                return this.nextValueState
            }

        override operator fun set(index: Int, value: Boolean) {
            this.array[index] = value
        }

        override fun setAllA(src: BooleanArray) {
            this.array.setAll(*src)
        }

        override fun setAllP(src: Array<Boolean>) {
            this.array.setAll(*src)
        }

        override fun setAllT(src: ObservableBooleanArray) {
            this.array.setAll(src)
        }

        override fun setAllA(src: BooleanArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllP(src: Array<Boolean>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllT(src: ObservableBooleanArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun addAllA(src: BooleanArray) {
            this.array.addAll(*src)
        }

        override fun addAllP(src: Array<Boolean>) {
            this.array.addAll(*src)
        }

        override fun addAllT(src: ObservableBooleanArray) {
            this.array.addAll(src)
        }

        override operator fun plusAssign(src: BooleanArray) {
            this.array += src
        }

        override operator fun plusAssign(src: Array<Boolean>) {
            this.array += src
        }

        override operator fun plusAssign(src: ObservableBooleanArray) {
            this.array += src
        }

        override fun addAllA(src: BooleanArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllP(src: Array<Boolean>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllT(src: ObservableBooleanArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun setA(src: BooleanArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setP(src: Array<Boolean>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setT(src: ObservableBooleanArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoA(dest: BooleanArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoP(dest: Array<Boolean>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoT(dest: ObservableBooleanArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): Boolean {
            return this.array[index]
        }

        override fun toArray(): BooleanArray {
            return this.array.toBooleanArray()
        }

        override fun toArray(startIndex: Int, endIndex: Int): BooleanArray {
            return this.array.toBooleanArray(startIndex, endIndex)
        }

        override fun toTypedArray(): Array<Boolean> {
            return this.array.toTypedArray()
        }

        override fun toTypedArray(array: BooleanArray, startIndex: Int, endIndex: Int): Array<Boolean> {
            return array.copyOfRange(startIndex, endIndex).toTypedArray()
        }

        override fun createPrimitiveArray(size: Int, fillWithData: Boolean): BooleanArray {
            return if (fillWithData) BooleanArray(size) { this.nextValue } else BooleanArray(size)
        }

        override fun createArray(size: Int, fillWithData: Boolean): Array<Boolean> {
            return Array(size) { if (fillWithData) this.nextValue else false }
        }

        override fun clonePrimitiveArray(array: BooleanArray): BooleanArray {
            return array.copyOf()
        }

        override fun cloneArray(array: Array<Boolean>): Array<Boolean> {
            return array.copyOf()
        }

        override fun arraySize(array: BooleanArray): Int {
            return array.size
        }

        override fun get(array: BooleanArray, index: Int): Boolean {
            return array[index]
        }

        override fun assertElementsEqual(actual: BooleanArray, from: Int, to: Int, expected: BooleanArray,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected boolean = ${expected[j]}, actual boolean = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: BooleanArray, from: Int, to: Int, expected: Array<Boolean>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected boolean = ${expected[j]}, actual boolean = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Boolean>, from: Int, to: Int, expected: BooleanArray,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected boolean = ${expected[j]}, actual boolean = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Boolean>, from: Int, to: Int, expected: Array<Boolean>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected boolean = ${expected[j]}, actual boolean = ${actual[i]}")
                j++
            }
        }

        override fun primitiveArrayToString(array: BooleanArray): String {
            return array.contentToString()
        }

    }

    private class DoubleArrayWrapper : ArrayWrapper<ObservableDoubleArray, DoubleArray, Double>() {

        private var nextValueState: Double = 0.0

        override fun createEmptyArray(): ObservableDoubleArray {
            return ObservableCollections.observableDoubleArray().also { this.array = it }
        }

        override fun createNotEmptyArray(src: DoubleArray): ObservableDoubleArray {
            return when (this.nextValueState.toInt() % 3) {
                0 -> ObservableCollections.observableDoubleArray(*src)
                1 -> ObservableCollections.observableDoubleArray(src.toTypedArray())
                else -> ObservableCollections.observableDoubleArray(ObservableCollections.observableDoubleArray(*src))
            }.also { this.array = it }
        }

        override fun newInstance(): ArrayWrapper<ObservableDoubleArray, DoubleArray, Double> {
            return DoubleArrayWrapper()
        }

        override val nextValue: Double
            get() = this.nextValueState++

        override operator fun set(index: Int, value: Double) {
            this.array[index] = value
        }

        override fun setAllA(src: DoubleArray) {
            this.array.setAll(*src)
        }

        override fun setAllP(src: Array<Double>) {
            this.array.setAll(*src)
        }

        override fun setAllT(src: ObservableDoubleArray) {
            this.array.setAll(src)
        }

        override fun setAllA(src: DoubleArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllP(src: Array<Double>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllT(src: ObservableDoubleArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun addAllA(src: DoubleArray) {
            this.array.addAll(*src)
        }

        override fun addAllP(src: Array<Double>) {
            this.array.addAll(*src)
        }

        override fun addAllT(src: ObservableDoubleArray) {
            this.array.addAll(src)
        }

        override operator fun plusAssign(src: DoubleArray) {
            this.array += src
        }

        override operator fun plusAssign(src: Array<Double>) {
            this.array += src
        }

        override operator fun plusAssign(src: ObservableDoubleArray) {
            this.array += src
        }

        override fun addAllA(src: DoubleArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllP(src: Array<Double>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllT(src: ObservableDoubleArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun setA(src: DoubleArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setP(src: Array<Double>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setT(src: ObservableDoubleArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoA(dest: DoubleArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoP(dest: Array<Double>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoT(dest: ObservableDoubleArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): Double {
            return this.array[index]
        }

        override fun toArray(): DoubleArray {
            return this.array.toDoubleArray()
        }

        override fun toArray(startIndex: Int, endIndex: Int): DoubleArray {
            return this.array.toDoubleArray(startIndex, endIndex)
        }

        override fun toTypedArray(): Array<Double> {
            return this.array.toTypedArray()
        }

        override fun toTypedArray(array: DoubleArray, startIndex: Int, endIndex: Int): Array<Double> {
            return array.copyOfRange(startIndex, endIndex).toTypedArray()
        }

        override fun createPrimitiveArray(size: Int, fillWithData: Boolean): DoubleArray {
            return if (fillWithData) DoubleArray(size) { this.nextValueState++ } else DoubleArray(size)
        }

        override fun createArray(size: Int, fillWithData: Boolean): Array<Double> {
            return Array(size) { if (fillWithData) this.nextValueState++ else 0.0 }
        }

        override fun clonePrimitiveArray(array: DoubleArray): DoubleArray {
            return array.copyOf()
        }

        override fun cloneArray(array: Array<Double>): Array<Double> {
            return array.copyOf()
        }

        override fun arraySize(array: DoubleArray): Int {
            return array.size
        }

        override fun get(array: DoubleArray, index: Int): Double {
            return array[index]
        }

        override fun assertElementsEqual(actual: DoubleArray, from: Int, to: Int, expected: DoubleArray, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j].toRawBits(), actual[i].toRawBits(),
                        "expected double = ${expected[j]}, actual double = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: DoubleArray, from: Int, to: Int, expected: Array<Double>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j].toRawBits(), actual[i].toRawBits(),
                        "expected double = ${expected[j]}, actual double = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Double>, from: Int, to: Int, expected: DoubleArray,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j].toRawBits(), actual[i].toRawBits(),
                        "expected double = ${expected[j]}, actual double = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Double>, from: Int, to: Int, expected: Array<Double>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j].toRawBits(), actual[i].toRawBits(),
                        "expected double = ${expected[j]}, actual double = ${actual[i]}")
                j++
            }
        }

        override fun primitiveArrayToString(array: DoubleArray): String {
            return array.contentToString()
        }

    }

    private class FloatArrayWrapper : ArrayWrapper<ObservableFloatArray, FloatArray, Float>() {

        private var nextValueState: Float = 0.0f

        override fun createEmptyArray(): ObservableFloatArray {
            return ObservableCollections.observableFloatArray().also { this.array = it }
        }

        override fun createNotEmptyArray(src: FloatArray): ObservableFloatArray {
            return when (this.nextValueState.toInt() % 3) {
                0 -> ObservableCollections.observableFloatArray(*src)
                1 -> ObservableCollections.observableFloatArray(src.toTypedArray())
                else -> ObservableCollections.observableFloatArray(ObservableCollections.observableFloatArray(*src))
            }.also { this.array = it }
        }

        override fun newInstance(): ArrayWrapper<ObservableFloatArray, FloatArray, Float> {
            return FloatArrayWrapper()
        }

        override val nextValue: Float
            get() = this.nextValueState++

        override operator fun set(index: Int, value: Float) {
            this.array[index] = value
        }

        override fun setAllA(src: FloatArray) {
            this.array.setAll(*src)
        }

        override fun setAllP(src: Array<Float>) {
            this.array.setAll(*src)
        }

        override fun setAllT(src: ObservableFloatArray) {
            this.array.setAll(src)
        }

        override fun setAllA(src: FloatArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllP(src: Array<Float>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllT(src: ObservableFloatArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun addAllA(src: FloatArray) {
            this.array.addAll(*src)
        }

        override fun addAllP(src: Array<Float>) {
            this.array.addAll(*src)
        }

        override fun addAllT(src: ObservableFloatArray) {
            this.array.addAll(src)
        }

        override operator fun plusAssign(src: FloatArray) {
            this.array += src
        }

        override operator fun plusAssign(src: Array<Float>) {
            this.array += src
        }

        override operator fun plusAssign(src: ObservableFloatArray) {
            this.array += src
        }

        override fun addAllA(src: FloatArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllP(src: Array<Float>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllT(src: ObservableFloatArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun setA(src: FloatArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setP(src: Array<Float>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setT(src: ObservableFloatArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoA(dest: FloatArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoP(dest: Array<Float>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoT(dest: ObservableFloatArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): Float {
            return this.array[index]
        }

        override fun toArray(): FloatArray {
            return this.array.toFloatArray()
        }

        override fun toArray(startIndex: Int, endIndex: Int): FloatArray {
            return this.array.toFloatArray(startIndex, endIndex)
        }

        override fun toTypedArray(): Array<Float> {
            return this.array.toTypedArray()
        }

        override fun toTypedArray(array: FloatArray, startIndex: Int, endIndex: Int): Array<Float> {
            return array.copyOfRange(startIndex, endIndex).toTypedArray()
        }

        override fun createPrimitiveArray(size: Int, fillWithData: Boolean): FloatArray {
            return if (fillWithData) FloatArray(size) { this.nextValueState++ } else FloatArray(size)
        }

        override fun createArray(size: Int, fillWithData: Boolean): Array<Float> {
            return Array(size) { if (fillWithData) this.nextValueState++ else 0.0f }
        }

        override fun clonePrimitiveArray(array: FloatArray): FloatArray {
            return array.copyOf()
        }

        override fun cloneArray(array: Array<Float>): Array<Float> {
            return array.copyOf()
        }

        override fun arraySize(array: FloatArray): Int {
            return array.size
        }

        override fun get(array: FloatArray, index: Int): Float {
            return array[index]
        }

        override fun assertElementsEqual(actual: FloatArray, from: Int, to: Int, expected: FloatArray, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j].toRawBits(), actual[i].toRawBits(),
                        "expected float = ${expected[j]}, actual float = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: FloatArray, from: Int, to: Int, expected: Array<Float>, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j].toRawBits(), actual[i].toRawBits(),
                        "expected float = ${expected[j]}, actual float = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Float>, from: Int, to: Int, expected: FloatArray, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j].toRawBits(), actual[i].toRawBits(),
                        "expected float = ${expected[j]}, actual float = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Float>, from: Int, to: Int, expected: Array<Float>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j].toRawBits(), actual[i].toRawBits(),
                        "expected float = ${expected[j]}, actual float = ${actual[i]}")
                j++
            }
        }

        override fun primitiveArrayToString(array: FloatArray): String {
            return array.contentToString()
        }

    }

    private class IntArrayWrapper : ArrayWrapper<ObservableIntArray, IntArray, Int>() {

        private var nextValueState: Int = 0

        override fun createEmptyArray(): ObservableIntArray {
            return ObservableCollections.observableIntArray().also { this.array = it }
        }

        override fun createNotEmptyArray(src: IntArray): ObservableIntArray {
            return when (this.nextValueState % 3) {
                0 -> ObservableCollections.observableIntArray(*src)
                1 -> ObservableCollections.observableIntArray(src.toTypedArray())
                else -> ObservableCollections.observableIntArray(ObservableCollections.observableIntArray(*src))
            }.also { this.array = it }
        }

        override fun newInstance(): ArrayWrapper<ObservableIntArray, IntArray, Int> {
            return IntArrayWrapper()
        }

        override val nextValue: Int
            get() = this.nextValueState++

        override operator fun set(index: Int, value: Int) {
            this.array[index] = value
        }

        override fun setAllA(src: IntArray) {
            this.array.setAll(*src)
        }

        override fun setAllP(src: Array<Int>) {
            this.array.setAll(*src)
        }

        override fun setAllT(src: ObservableIntArray) {
            this.array.setAll(src)
        }

        override fun setAllA(src: IntArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllP(src: Array<Int>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllT(src: ObservableIntArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun addAllA(src: IntArray) {
            this.array.addAll(*src)
        }

        override fun addAllP(src: Array<Int>) {
            this.array.addAll(*src)
        }

        override fun addAllT(src: ObservableIntArray) {
            this.array.addAll(src)
        }

        override operator fun plusAssign(src: IntArray) {
            this.array += src
        }

        override operator fun plusAssign(src: Array<Int>) {
            this.array += src
        }

        override operator fun plusAssign(src: ObservableIntArray) {
            this.array += src
        }

        override fun addAllA(src: IntArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllP(src: Array<Int>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllT(src: ObservableIntArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun setA(src: IntArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setP(src: Array<Int>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setT(src: ObservableIntArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoA(dest: IntArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoP(dest: Array<Int>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoT(dest: ObservableIntArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): Int {
            return this.array[index]
        }

        override fun toArray(): IntArray {
            return this.array.toIntArray()
        }

        override fun toArray(startIndex: Int, endIndex: Int): IntArray {
            return this.array.toIntArray(startIndex, endIndex)
        }

        override fun toTypedArray(): Array<Int> {
            return this.array.toTypedArray()
        }

        override fun toTypedArray(array: IntArray, startIndex: Int, endIndex: Int): Array<Int> {
            return array.copyOfRange(startIndex, endIndex).toTypedArray()
        }

        override fun createPrimitiveArray(size: Int, fillWithData: Boolean): IntArray {
            return if (fillWithData) IntArray(size) { this.nextValueState++ } else IntArray(size)
        }

        override fun createArray(size: Int, fillWithData: Boolean): Array<Int> {
            return Array(size) { if (fillWithData) this.nextValueState++ else 0 }
        }

        override fun clonePrimitiveArray(array: IntArray): IntArray {
            return array.copyOf()
        }

        override fun cloneArray(array: Array<Int>): Array<Int> {
            return array.copyOf()
        }

        override fun arraySize(array: IntArray): Int {
            return array.size
        }

        override fun get(array: IntArray, index: Int): Int {
            return array[index]
        }

        override fun assertElementsEqual(actual: IntArray, from: Int, to: Int, expected: IntArray, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i])
                j++
            }
        }

        override fun assertElementsEqual(actual: IntArray, from: Int, to: Int, expected: Array<Int>, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i])
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Int>, from: Int, to: Int, expected: IntArray, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i])
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Int>, from: Int, to: Int, expected: Array<Int>, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i])
                j++
            }
        }

        override fun primitiveArrayToString(array: IntArray): String {
            return array.contentToString()
        }

    }

    private class LongArrayWrapper : ArrayWrapper<ObservableLongArray, LongArray, Long>() {

        private var nextValueState: Long = 0L

        override fun createEmptyArray(): ObservableLongArray {
            return ObservableCollections.observableLongArray().also { this.array = it }
        }

        override fun createNotEmptyArray(src: LongArray): ObservableLongArray {
            return when (this.nextValueState.toInt() % 3) {
                0 -> ObservableCollections.observableLongArray(*src)
                1 -> ObservableCollections.observableLongArray(src.toTypedArray())
                else -> ObservableCollections.observableLongArray(ObservableCollections.observableLongArray(*src))
            }.also { this.array = it }
        }

        override fun newInstance(): ArrayWrapper<ObservableLongArray, LongArray, Long> {
            return LongArrayWrapper()
        }

        override val nextValue: Long
            get() = this.nextValueState++

        override operator fun set(index: Int, value: Long) {
            this.array[index] = value
        }

        override fun setAllA(src: LongArray) {
            this.array.setAll(*src)
        }

        override fun setAllP(src: Array<Long>) {
            this.array.setAll(*src)
        }

        override fun setAllT(src: ObservableLongArray) {
            this.array.setAll(src)
        }

        override fun setAllA(src: LongArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllP(src: Array<Long>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllT(src: ObservableLongArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun addAllA(src: LongArray) {
            this.array.addAll(*src)
        }

        override fun addAllP(src: Array<Long>) {
            this.array.addAll(*src)
        }

        override fun addAllT(src: ObservableLongArray) {
            this.array.addAll(src)
        }

        override operator fun plusAssign(src: LongArray) {
            this.array += src
        }

        override operator fun plusAssign(src: Array<Long>) {
            this.array += src
        }

        override operator fun plusAssign(src: ObservableLongArray) {
            this.array += src
        }

        override fun addAllA(src: LongArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllP(src: Array<Long>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllT(src: ObservableLongArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun setA(src: LongArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setP(src: Array<Long>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setT(src: ObservableLongArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoA(dest: LongArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoP(dest: Array<Long>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoT(dest: ObservableLongArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): Long {
            return this.array[index]
        }

        override fun toArray(): LongArray {
            return this.array.toLongArray()
        }

        override fun toArray(startIndex: Int, endIndex: Int): LongArray {
            return this.array.toLongArray(startIndex, endIndex)
        }

        override fun toTypedArray(): Array<Long> {
            return this.array.toTypedArray()
        }

        override fun toTypedArray(array: LongArray, startIndex: Int, endIndex: Int): Array<Long> {
            return array.copyOfRange(startIndex, endIndex).toTypedArray()
        }

        override fun createPrimitiveArray(size: Int, fillWithData: Boolean): LongArray {
            return if (fillWithData) LongArray(size) { this.nextValueState++ } else LongArray(size)
        }

        override fun createArray(size: Int, fillWithData: Boolean): Array<Long> {
            return Array(size) { if (fillWithData) this.nextValueState++ else 0L }
        }

        override fun clonePrimitiveArray(array: LongArray): LongArray {
            return array.copyOf()
        }

        override fun cloneArray(array: Array<Long>): Array<Long> {
            return array.copyOf()
        }

        override fun arraySize(array: LongArray): Int {
            return array.size
        }

        override fun get(array: LongArray, index: Int): Long {
            return array[index]
        }

        override fun assertElementsEqual(actual: LongArray, from: Int, to: Int, expected: LongArray, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i])
                j++
            }
        }

        override fun assertElementsEqual(actual: LongArray, from: Int, to: Int, expected: Array<Long>, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i])
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Long>, from: Int, to: Int, expected: LongArray, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i])
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Long>, from: Int, to: Int, expected: Array<Long>, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i])
                j++
            }
        }

        override fun primitiveArrayToString(array: LongArray): String {
            return array.contentToString()
        }

    }

    private class ShortArrayWrapper : ArrayWrapper<ObservableShortArray, ShortArray, Short>() {

        private var nextValueState: Short = 0

        override fun createEmptyArray(): ObservableShortArray {
            return ObservableCollections.observableShortArray().also { this.array = it }
        }

        override fun createNotEmptyArray(src: ShortArray): ObservableShortArray {
            return when (this.nextValueState.toInt() % 3) {
                0 -> ObservableCollections.observableShortArray(*src)
                1 -> ObservableCollections.observableShortArray(src.toTypedArray())
                else -> ObservableCollections.observableShortArray(ObservableCollections.observableShortArray(*src))
            }.also { this.array = it }
        }

        override fun newInstance(): ArrayWrapper<ObservableShortArray, ShortArray, Short> {
            return ShortArrayWrapper()
        }

        override val nextValue: Short
            get() = this.nextValueState++

        override operator fun set(index: Int, value: Short) {
            this.array[index] = value
        }

        override fun setAllA(src: ShortArray) {
            this.array.setAll(*src)
        }

        override fun setAllP(src: Array<Short>) {
            this.array.setAll(*src)
        }

        override fun setAllT(src: ObservableShortArray) {
            this.array.setAll(src)
        }

        override fun setAllA(src: ShortArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllP(src: Array<Short>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllT(src: ObservableShortArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun addAllA(src: ShortArray) {
            this.array.addAll(*src)
        }

        override fun addAllP(src: Array<Short>) {
            this.array.addAll(*src)
        }

        override fun addAllT(src: ObservableShortArray) {
            this.array.addAll(src)
        }

        override operator fun plusAssign(src: ShortArray) {
            this.array += src
        }

        override operator fun plusAssign(src: Array<Short>) {
            this.array += src
        }

        override operator fun plusAssign(src: ObservableShortArray) {
            this.array += src
        }

        override fun addAllA(src: ShortArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllP(src: Array<Short>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllT(src: ObservableShortArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun setA(src: ShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setP(src: Array<Short>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setT(src: ObservableShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoA(dest: ShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoP(dest: Array<Short>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoT(dest: ObservableShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): Short {
            return this.array[index]
        }

        override fun toArray(): ShortArray {
            return this.array.toShortArray()
        }

        override fun toArray(startIndex: Int, endIndex: Int): ShortArray {
            return this.array.toShortArray(startIndex, endIndex)
        }

        override fun toTypedArray(): Array<Short> {
            return this.array.toTypedArray()
        }

        override fun toTypedArray(array: ShortArray, startIndex: Int, endIndex: Int): Array<Short> {
            return array.copyOfRange(startIndex, endIndex).toTypedArray()
        }

        override fun createPrimitiveArray(size: Int, fillWithData: Boolean): ShortArray {
            return if (fillWithData) ShortArray(size) { this.nextValueState++ } else ShortArray(size)
        }

        override fun createArray(size: Int, fillWithData: Boolean): Array<Short> {
            return Array(size) { if (fillWithData) this.nextValueState++ else 0 }
        }

        override fun clonePrimitiveArray(array: ShortArray): ShortArray {
            return array.copyOf()
        }

        override fun cloneArray(array: Array<Short>): Array<Short> {
            return array.copyOf()
        }

        override fun arraySize(array: ShortArray): Int {
            return array.size
        }

        override fun get(array: ShortArray, index: Int): Short {
            return array[index]
        }

        override fun assertElementsEqual(actual: ShortArray, from: Int, to: Int, expected: ShortArray, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected short = ${expected[j]}, actual short = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: ShortArray, from: Int, to: Int, expected: Array<Short>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected short = ${expected[j]}, actual short = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Short>, from: Int, to: Int, expected: ShortArray,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected short = ${expected[j]}, actual short = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Short>, from: Int, to: Int, expected: Array<Short>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected short = ${expected[j]}, actual short = ${actual[i]}")
                j++
            }
        }

        override fun primitiveArrayToString(array: ShortArray): String {
            return array.contentToString()
        }

    }

    private class ByteArrayWrapper : ArrayWrapper<ObservableByteArray, ByteArray, Byte>() {

        private var nextValueState: Byte = 0

        override fun createEmptyArray(): ObservableByteArray {
            return ObservableCollections.observableByteArray().also { this.array = it }
        }

        override fun createNotEmptyArray(src: ByteArray): ObservableByteArray {
            return when (this.nextValueState.toInt() % 3) {
                0 -> ObservableCollections.observableByteArray(*src)
                1 -> ObservableCollections.observableByteArray(src.toTypedArray())
                else -> ObservableCollections.observableByteArray(ObservableCollections.observableByteArray(*src))
            }.also { this.array = it }
        }

        override fun newInstance(): ArrayWrapper<ObservableByteArray, ByteArray, Byte> {
            return ByteArrayWrapper()
        }

        override val nextValue: Byte
            get() = this.nextValueState++

        override operator fun set(index: Int, value: Byte) {
            this.array[index] = value
        }

        override fun setAllA(src: ByteArray) {
            this.array.setAll(*src)
        }

        override fun setAllP(src: Array<Byte>) {
            this.array.setAll(*src)
        }

        override fun setAllT(src: ObservableByteArray) {
            this.array.setAll(src)
        }

        override fun setAllA(src: ByteArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllP(src: Array<Byte>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllT(src: ObservableByteArray, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun addAllA(src: ByteArray) {
            this.array.addAll(*src)
        }

        override fun addAllP(src: Array<Byte>) {
            this.array.addAll(*src)
        }

        override fun addAllT(src: ObservableByteArray) {
            this.array.addAll(src)
        }

        override operator fun plusAssign(src: ByteArray) {
            this.array += src
        }

        override operator fun plusAssign(src: Array<Byte>) {
            this.array += src
        }

        override operator fun plusAssign(src: ObservableByteArray) {
            this.array += src
        }

        override fun addAllA(src: ByteArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllP(src: Array<Byte>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllT(src: ObservableByteArray, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun setA(src: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setP(src: Array<Byte>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setT(src: ObservableByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoA(dest: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoP(dest: Array<Byte>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoT(dest: ObservableByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): Byte {
            return this.array[index]
        }

        override fun toArray(): ByteArray {
            return this.array.toByteArray()
        }

        override fun toArray(startIndex: Int, endIndex: Int): ByteArray {
            return this.array.toByteArray(startIndex, endIndex)
        }

        override fun toTypedArray(): Array<Byte> {
            return this.array.toTypedArray()
        }

        override fun toTypedArray(array: ByteArray, startIndex: Int, endIndex: Int): Array<Byte> {
            return array.copyOfRange(startIndex, endIndex).toTypedArray()
        }

        override fun createPrimitiveArray(size: Int, fillWithData: Boolean): ByteArray {
            return if (fillWithData) ByteArray(size) { this.nextValueState++ } else ByteArray(size)
        }

        override fun createArray(size: Int, fillWithData: Boolean): Array<Byte> {
            return Array(size) { if (fillWithData) this.nextValueState++ else 0 }
        }

        override fun clonePrimitiveArray(array: ByteArray): ByteArray {
            return array.copyOf()
        }

        override fun cloneArray(array: Array<Byte>): Array<Byte> {
            return array.copyOf()
        }

        override fun arraySize(array: ByteArray): Int {
            return array.size
        }

        override fun get(array: ByteArray, index: Int): Byte {
            return array[index]
        }

        override fun assertElementsEqual(actual: ByteArray, from: Int, to: Int, expected: ByteArray, expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected byte = ${expected[j]}, actual byte = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: ByteArray, from: Int, to: Int, expected: Array<Byte>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected byte = ${expected[j]}, actual byte = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Byte>, from: Int, to: Int, expected: ByteArray,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected byte = ${expected[j]}, actual byte = ${actual[i]}")
                j++
            }
        }

        override fun assertElementsEqual(actual: Array<Byte>, from: Int, to: Int, expected: Array<Byte>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected byte = ${expected[j]}, actual byte = ${actual[i]}")
                j++
            }
        }

        override fun primitiveArrayToString(array: ByteArray): String {
            return array.contentToString()
        }

    }

    companion object {

        private const val INITIAL_SIZE: Int = 6

        @Parameters
        @JvmStatic
        fun createParameters(): List<Array<out Any?>> {
            return listOf(
                    arrayOf(BooleanArrayWrapper()),
                    arrayOf(DoubleArrayWrapper()),
                    arrayOf(FloatArrayWrapper()),
                    arrayOf(IntArrayWrapper()),
                    arrayOf(LongArrayWrapper()),
                    arrayOf(ShortArrayWrapper()),
                    arrayOf(ByteArrayWrapper())
            )
        }

    }

}