package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.utils.RandomUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.math.max
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class ObservableArrayTest<T : ObservableArray<T>, P>(private val wrapper: ArrayWrapper<T, P>) {

    private var initialSize: Int = 0

    private lateinit var initialElements: Array<P>

    private lateinit var array: T

    private lateinit var mao: MockArrayObserver<T>

    @Before
    fun setUp() {
        this.initialSize = INITIAL_SIZE
        this.initialElements = this.wrapper.createArray(this.initialSize)
        this.array = this.wrapper.createNotEmptyArray(this.initialElements)
        this.mao = MockArrayObserver()
        this.array.addListener(this.mao)
    }

    private fun makeEmpty() {
        this.initialSize = 0
        this.initialElements = this.wrapper.createArray(this.initialSize)
        this.array.clear()
        this.mao.reset()
    }

    private fun assertUnchanged() {
        this.mao.check0()
        assertEquals(this.initialSize, this.array.size)
        val actual = this.wrapper.toArray()
        assertEquals(this.initialSize, actual.size)
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
        this.array.clear()
        this.mao.checkOnlySizeChanged(this.array)
        assertEquals(0, this.array.size)
    }

    @Test
    fun testGet() {
        for (i in 0 until this.array.size) {
            val expected = this.initialElements[i]
            val actual = this.wrapper[i]
            assertEquals(expected, actual)
        }
        assertUnchanged()
    }

    @Test
    fun testToArray() {
        val expected = this.initialElements
        val actual = this.wrapper.toArray()
        assertEquals(INITIAL_SIZE, actual.size)
        this.wrapper.assertElementsEqual(actual, 0, this.array.size, expected, 0)
    }

    // ========================= add/remove listener tests =========================

    @Test
    fun testAddRemoveListener() {
        val mao2 = MockArrayObserver<T>()
        this.array.addListener(mao2)
        this.array.removeListener(this.mao)
        this.wrapper[0] = this.wrapper.nextValue
        this.mao.check0()
        mao2.check(this.array, false, 0, 1)
    }

    @Test
    fun testAddTwoListenersElementChange() {
        val mao2 = MockArrayObserver<T>()
        this.array.addListener(mao2)
        this.wrapper[0] = this.wrapper.nextValue
        this.mao.check(this.array, false, 0, 1)
        mao2.check(this.array, false, 0, 1)
    }

    @Test
    fun testAddTwoListenersSizeChange() {
        val mao2 = MockArrayObserver<T>()
        this.array.addListener(mao2)
        this.array.resize(3)
        this.mao.checkOnlySizeChanged(this.array)
        mao2.checkOnlySizeChanged(this.array)
    }

    @Test
    fun testAddThreeListeners() {
        val mao2 = MockArrayObserver<T>()
        val mao3 = MockArrayObserver<T>()
        this.array.addListener(mao2)
        this.array.addListener(mao3)
        this.wrapper[0] = this.wrapper.nextValue
        this.mao.check(this.array, false, 0, 1)
        mao2.check(this.array, false, 0, 1)
        mao3.check(this.array, false, 0, 1)
    }

    @Test
    fun testAddThreeListenersSizeChange() {
        val mao2 = MockArrayObserver<T>()
        val mao3 = MockArrayObserver<T>()
        this.array.addListener(mao2)
        this.array.addListener(mao3)
        this.array.resize(10)
        this.mao.checkOnlySizeChanged(this.array)
        mao2.checkOnlySizeChanged(this.array)
        mao3.checkOnlySizeChanged(this.array)
    }

    @Test
    fun testAddListenerTwice() {
        this.array.addListener(this.mao) // add it a second time
        this.wrapper[1] = this.wrapper.nextValue
        this.mao.check(this.array, false, 1, 2)
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
            this.mao.checkOnlySizeChanged(this.array)
        }
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
        this.wrapper.assertElementsEqual(actual, 0, matchingElements, expected, 0)
        this.wrapper.assertElementsEqual(actual, matchingElements, newSize,
                this.wrapper.createArray(max(0, newSize - matchingElements), false), 0)
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

    // ========================= setAll(array) tests =========================

    private fun testSetAllP(sizeChanged: Boolean, newSize: Int) {
        val expected = this.wrapper.createArray(newSize)

        this.wrapper.setAllP(expected)

        this.mao.check(this.array, sizeChanged, 0, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(expected.size, this.array.size)
        assertEquals(expected.size, actual.size)
        this.wrapper.assertElementsEqual(actual, 0, expected.size, expected, 0)
    }

    @Test
    fun testSetAllPSmaller() {
        testSetAllP(true, 3)
    }

    @Test
    fun testSetAllPBigger() {
        testSetAllP(true, 10)
    }

    @Test
    fun testSetAllPOnSameSize() {
        testSetAllP(false, INITIAL_SIZE)
    }

    @Test
    fun testSetAllPOnEmpty() {
        makeEmpty()
        testSetAllP(true, 3)
    }

    @Test
    fun testSetAllPOnEmptyToEmpty() {
        makeEmpty()
        this.wrapper.setAllP(this.wrapper.createArray(0))
        assertUnchanged()
        assertEquals(0, this.array.size)
    }

    // ========================= setAll(ObservableArray) tests =========================

    private fun testSetAllT(sizeChanged: Boolean, newSize: Int) {
        val wrapper2 = this.wrapper.newInstance()
        val expected = this.wrapper.createArray(newSize)
        val src = wrapper2.createNotEmptyArray(expected)

        this.wrapper.setAllT(src)

        this.mao.check(this.array, sizeChanged, 0, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(expected.size, this.array.size)
        assertEquals(expected.size, actual.size)
        this.wrapper.assertElementsEqual(actual, 0, expected.size, expected, 0)
    }

    @Test
    fun testSetAllTSmaller() {
        testSetAllT(true, 3)
    }

    @Test
    fun testSetAllTBigger() {
        testSetAllT(true, 10)
    }

    @Test
    fun testSetAllTOnSameSize() {
        testSetAllT(false, INITIAL_SIZE)
    }

    @Test
    fun testSetAllTOnEmpty() {
        makeEmpty()
        testSetAllT(true, 3)
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
        assertEquals(this.initialSize, actual.size)
        this.wrapper.assertElementsEqual(actual, 0, this.initialSize, this.initialElements, 0)
    }

    @Test
    fun testSetAllTSelfEmpty() {
        makeEmpty()

        this.wrapper.setAllT(this.array)

        this.mao.check0()
        val actual = this.wrapper.toArray()
        assertEquals(0, this.array.size)
        assertEquals(0, actual.size)
    }

    // ========================= setAll(array, range) tests =========================

    private fun testSetAllPRange(sizeChanged: Boolean, newSize: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val expected = this.wrapper.createArray(newSize)

        this.wrapper.setAllP(expected, startIndex, endIndex)

        this.mao.check(this.array, sizeChanged, 0, length)
        val actual = this.wrapper.toArray()
        assertEquals(length, this.array.size)
        assertEquals(length, actual.size)
        this.wrapper.assertElementsEqual(actual, 0, length, expected, startIndex)
    }

    @Test
    fun testSetAllPRange1() {
        testSetAllPRange(false, INITIAL_SIZE, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllPRange2() {
        testSetAllPRange(false, INITIAL_SIZE + 10, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllPRange3() {
        testSetAllPRange(false, INITIAL_SIZE + 10, 10, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllPRange4() {
        testSetAllPRange(false, INITIAL_SIZE + 10, 2, INITIAL_SIZE + 2)
    }

    @Test
    fun testSetAllPRange5() {
        testSetAllPRange(true, INITIAL_SIZE, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testSetAllPRange6() {
        testSetAllPRange(true, INITIAL_SIZE + 10, 0, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllPRange7() {
        testSetAllPRange(true, INITIAL_SIZE + 20, 10, INITIAL_SIZE + 20)
    }

    @Test
    fun testSetAllPRange8() {
        testSetAllPRange(true, INITIAL_SIZE + 10, 2, INITIAL_SIZE - 1)
    }

    @Test
    fun testSetAllPRangeOnEmpty() {
        makeEmpty()
        testSetAllPRange(true, INITIAL_SIZE, 1, 4)
    }

    @Test
    fun testSetAllPRangeOnEmptyToEmpty() {
        makeEmpty()
        this.wrapper.setAllP(this.wrapper.createArray(INITIAL_SIZE), 1, 1)
        assertUnchanged()
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllPRangeNegative1() {
        try {
            testSetAllPRange(true, INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllPRangeNegative2() {
        try {
            testSetAllPRange(true, INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllPRangeNegative3() {
        try {
            testSetAllPRange(true, INITIAL_SIZE, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllPRangeNegative4() {
        try {
            testSetAllPRange(true, INITIAL_SIZE, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= setAll(observable array, range) tests =========================

    private fun testSetAllTRange(sizeChanged: Boolean, newSize: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val expected = this.wrapper.createArray(newSize)
        val src = this.wrapper.newInstance().createNotEmptyArray(expected)

        this.wrapper.setAllT(src, startIndex, endIndex)

        this.mao.check(this.array, sizeChanged, 0, length)
        val actual = this.wrapper.toArray()
        assertEquals(length, this.array.size)
        assertEquals(length, actual.size)
        this.wrapper.assertElementsEqual(actual, 0, length, expected, startIndex)
    }

    @Test
    fun testSetAllTRange1() {
        testSetAllTRange(false, INITIAL_SIZE, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllTRange2() {
        testSetAllTRange(false, INITIAL_SIZE + 10, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllTRange3() {
        testSetAllTRange(false, INITIAL_SIZE + 10, 10, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllTRange4() {
        testSetAllTRange(false, INITIAL_SIZE + 10, 2, INITIAL_SIZE + 2)
    }

    @Test
    fun testSetAllTRange5() {
        testSetAllTRange(true, INITIAL_SIZE, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testSetAllTRange6() {
        testSetAllTRange(true, INITIAL_SIZE + 10, 0, INITIAL_SIZE + 10)
    }

    @Test
    fun testSetAllTRange7() {
        testSetAllTRange(true, INITIAL_SIZE + 20, 10, INITIAL_SIZE + 20)
    }

    @Test
    fun testSetAllTRange8() {
        testSetAllTRange(true, INITIAL_SIZE + 10, 2, INITIAL_SIZE - 1)
    }

    @Test
    fun testSetAllTRangeOnEmpty() {
        makeEmpty()
        testSetAllTRange(true, INITIAL_SIZE, 1, 4)
    }

    @Test
    fun testSetAllTRangeOnEmptyToEmpty() {
        makeEmpty()
        this.wrapper.setAllT(this.wrapper.newInstance().createNotEmptyArray(
                this.wrapper.createArray(INITIAL_SIZE)), 1, 1)
        assertUnchanged()
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeNegative1() {
        try {
            testSetAllTRange(true, INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeNegative2() {
        try {
            testSetAllTRange(true, INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeNegative3() {
        try {
            testSetAllTRange(true, INITIAL_SIZE, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeNegative4() {
        try {
            testSetAllTRange(true, INITIAL_SIZE, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeNegativeAfterSrcEnsureCapacity() {
        val expected = this.wrapper.createArray(INITIAL_SIZE)
        val src = this.wrapper.newInstance().createNotEmptyArray(expected)
        src.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.setAllT(src, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeNegativeAfterSrcClear() {
        val expected = this.wrapper.createArray(INITIAL_SIZE)
        val src = this.wrapper.newInstance().createNotEmptyArray(expected)
        src.clear()
        try {
            this.wrapper.setAllT(src, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    private fun testSetAllTRangeSelf(sizeChanged: Boolean, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        this.wrapper.setAllT(this.array, startIndex, endIndex)

        if (startIndex == 0) {
            if (length == this.initialSize) {
                this.mao.check0()
            } else {
                this.mao.checkOnlySizeChanged(this.array)
            }
        } else {
            this.mao.check(this.array, sizeChanged, 0, length)
        }
        val actual = this.wrapper.toArray()
        assertEquals(length, this.array.size)
        assertEquals(length, actual.size)
        this.wrapper.assertElementsEqual(actual, 0, length, this.initialElements, startIndex)
    }

    @Test
    fun testSetAllTRangeSelf() {
        testSetAllTRangeSelf(true, 0, INITIAL_SIZE)
    }

    @Test
    fun testSetAllTRangeSelfBeginning() {
        testSetAllTRangeSelf(true, 0, INITIAL_SIZE / 2)
    }

    @Test
    fun testSetAllTRangeSelfTrailing() {
        testSetAllTRangeSelf(true, INITIAL_SIZE / 2, INITIAL_SIZE)
    }

    @Test
    fun testSetAllTRangeSelfMiddle() {
        testSetAllTRangeSelf(true, 3, 5)
    }

    @Test
    fun testSetAllTRangeSelfEmpty() {
        makeEmpty()
        testSetAllTRangeSelf(false, 0, 0)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegative1() {
        try {
            this.wrapper.setAllT(this.array, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegative2() {
        try {
            this.wrapper.setAllT(this.array, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegative3() {
        try {
            this.wrapper.setAllT(this.array, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegative4() {
        try {
            this.wrapper.setAllT(this.array, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.setAllT(this.array, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetAllTRangeSelfNegativeAfterClear() {
        makeEmpty()
        try {
            this.wrapper.setAllT(this.array, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= addAll(array) tests =========================

    private fun testAddAllP(srcSize: Int) {
        val src = this.wrapper.createArray(srcSize)
        val oldSize = this.array.size

        this.wrapper.addAllP(src)

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        this.mao.check(this.array, sizeChanged, oldSize, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
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
        val src = this.wrapper.createArray(srcSize)
        val oldSize = this.array.size

        this.wrapper.addAllT(this.wrapper.newInstance().createNotEmptyArray(src))

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        this.mao.check(this.array, sizeChanged, oldSize, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
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

        this.mao.check(this.array, true, this.initialSize, this.initialSize * 2)
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
        assertEquals(0, actual.size)
    }

    @Test
    fun testAddAllTManyPoints() {
        for (i in 0 until 65_000) {
            this.wrapper.addAllT(this.wrapper.newInstance().createNotEmptyArray(
                    this.wrapper.createArray(3)))
        }
    }

    // ========================= plusAssign(array) tests =========================

    private fun testPlusAssignP(srcSize: Int) {
        val src = this.wrapper.createArray(srcSize)
        val oldSize = this.array.size

        this.wrapper += src

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        this.mao.check(this.array, sizeChanged, oldSize, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
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
        val src = this.wrapper.createArray(srcSize)
        val oldSize = this.array.size

        this.wrapper += this.wrapper.newInstance().createNotEmptyArray(src)

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        this.mao.check(this.array, sizeChanged, oldSize, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
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

        this.mao.check(this.array, true, this.initialSize, this.initialSize * 2)
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
        assertEquals(0, actual.size)
    }

    @Test
    fun testPlusAssignTManyPoints() {
        for (i in 0 until 65_000) {
            this.wrapper += this.wrapper.newInstance().createNotEmptyArray(
                    this.wrapper.createArray(3))
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

        this.mao.check(this.array, sizeChanged, oldSize, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
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

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllPRangeNegative1() {
        try {
            testAddAllPRange(INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllPRangeNegative2() {
        try {
            testAddAllPRange(INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllPRangeNegative3() {
        try {
            testAddAllPRange(INITIAL_SIZE, 1, -1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
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
        val src = this.wrapper.createArray(srcSize)
        val oldSize = this.array.size

        this.wrapper.addAllT(this.wrapper.newInstance().createNotEmptyArray(src), startIndex, endIndex)

        val newSize = oldSize + length
        val sizeChanged = newSize != oldSize

        this.mao.check(this.array, sizeChanged, oldSize, newSize)
        val actual = this.wrapper.toArray()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
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
                this.wrapper.newInstance().createNotEmptyArray(this.wrapper.createArray(INITIAL_SIZE)), 1, 1)
        assertUnchanged()
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeNegative1() {
        try {
            testAddAllTRange(INITIAL_SIZE, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeNegative2() {
        try {
            testAddAllTRange(INITIAL_SIZE, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeNegative3() {
        try {
            testAddAllTRange(INITIAL_SIZE, 1, -1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeNegative4() {
        try {
            testAddAllTRange(INITIAL_SIZE, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeNegativeAfterSrcEnsureCapacity() {
        val srcA = this.wrapper.createArray(INITIAL_SIZE)
        val src = this.wrapper.newInstance().createNotEmptyArray(srcA)
        src.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.addAllT(src, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeNegativeAfterSrcClear() {
        val srcA = this.wrapper.createArray(INITIAL_SIZE)
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
        this.mao.check(this.array, true, this.initialSize, expSize)
        val actual = this.wrapper.toArray()
        assertEquals(expSize, this.array.size)
        assertEquals(expSize, actual.size)
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

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegative1() {
        try {
            testAddAllTRangeSelf(-1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegative2() {
        try {
            testAddAllTRangeSelf(0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegative3() {
        try {
            testAddAllTRangeSelf(1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegative4() {
        try {
            testAddAllTRangeSelf(INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegativeAfterSrcEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.addAllT(this.array, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testAddAllTRangeSelfNegativeAfterSrcClear() {
        makeEmpty()
        try {
            this.wrapper.addAllT(this.array, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= set(array, range) tests =========================

    private fun testSetPRange(srcSize: Int, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        val length = endIndex - startIndex
        val expected = this.wrapper.createArray(srcSize)

        this.wrapper.setP(expected, destinationOffset, startIndex, endIndex)

        this.mao.checkOnlyElementsChanged(this.array, destinationOffset, destinationOffset + length)
        val actual = this.wrapper.toArray()
        assertEquals(INITIAL_SIZE, this.array.size)
        assertEquals(INITIAL_SIZE, actual.size)
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

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetPRangeNegative1() {
        try {
            testSetPRange(10, -1, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetPRangeNegative2() {
        try {
            testSetPRange(10, 0, -1, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetPRangeNegative3() {
        try {
            testSetPRange(10, 1, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetPRangeNegative4() {
        try {
            testSetPRange(10, INITIAL_SIZE, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetPRangeNegative5() {
        try {
            testSetPRange(10, 0, 10, 11)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetPRangeNegative6() {
        try {
            testSetPRange(3, 0, 1, 5)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetPRangeNegative7() {
        try {
            testSetPRange(10, INITIAL_SIZE - 3, 0, 4)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetPRangeNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            testSetPRange(10, INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
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
        val expected = this.wrapper.createArray(srcSize)
        val src = this.wrapper.newInstance().createNotEmptyArray(expected)

        this.wrapper.setT(src, destinationOffset, startIndex, endIndex)

        this.mao.checkOnlyElementsChanged(this.array, destinationOffset, destinationOffset + length)
        val actual = this.wrapper.toArray()
        assertEquals(INITIAL_SIZE, this.array.size)
        assertEquals(INITIAL_SIZE, actual.size)
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

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegative1() {
        try {
            testSetTRange(10, -1, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegative2() {
        try {
            testSetTRange(10, 0, -1, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegative3() {
        try {
            testSetTRange(10, 1, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegative4() {
        try {
            testSetTRange(10, INITIAL_SIZE, 0, 3)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegative5() {
        try {
            testSetTRange(10, 0, 10, 11)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegative6() {
        try {
            testSetTRange(3, 0, 1, 5)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegative7() {
        try {
            testSetTRange(10, INITIAL_SIZE - 3, 0, 4)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            testSetTRange(10, INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegativeAfterClear() {
        makeEmpty()
        try {
            testSetTRange(1, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegativeAfterSrcEnsureCapacity() {
        val srcA = this.wrapper.createArray(1)
        val src = this.wrapper.newInstance().createNotEmptyArray(srcA)
        src.ensureCapacity(2)
        try {
            this.wrapper.setT(src, 0, 1, 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeNegativeAfterSrcClear() {
        val srcA = this.wrapper.createArray(1)
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
        this.wrapper.setT(this.array, destinationOffset, startIndex, endIndex)

        this.mao.checkOnlyElementsChanged(this.array, destinationOffset, destinationOffset + length)
        val actual = this.wrapper.toArray()
        assertEquals(INITIAL_SIZE, this.array.size)
        assertEquals(INITIAL_SIZE, actual.size)
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

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative1() {
        try {
            this.wrapper.setT(this.array, -1, 0, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative2() {
        try {
            this.wrapper.setT(this.array, 0, -1, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative3() {
        try {
            this.wrapper.setT(this.array, 0, 0, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative4() {
        try {
            this.wrapper.setT(this.array, 0, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative5() {
        try {
            this.wrapper.setT(this.array, INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegative6() {
        try {
            this.wrapper.setT(this.array, 0, INITIAL_SIZE, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper.setT(this.array, 0, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetTRangeSelfNegativeAfterClear() {
        makeEmpty()
        try {
            this.wrapper.setT(this.array, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    // ========================= negative get(index) tests =========================

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testGetNegative() {
        try {
            this.wrapper[-1]
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testGetOutOfBounds() {
        try {
            this.wrapper[this.array.size]
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testGetAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper[INITIAL_SIZE]
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
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
        for (i in 0 until INITIAL_SIZE) {
            val expected = this.wrapper.nextValue

            this.wrapper[i] = expected

            this.mao.check(this.array, false, i, i + 1)
            this.mao.reset()
            assertEquals(expected, this.wrapper[i])
            assertEquals(INITIAL_SIZE, this.array.size)
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetValueNegative() {
        try {
            this.wrapper[-1] = this.wrapper.nextValue
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetValueOutOfBounds() {
        try {
            this.wrapper[this.array.size] = this.wrapper.nextValue
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetValueAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            this.wrapper[INITIAL_SIZE] = this.wrapper.nextValue
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
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

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testToArrayRangeNegative1() {
        try {
            testToArrayRange(-1, 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testToArrayRangeNegative2() {
        try {
            testToArrayRange(this.array.size, this.array.size + 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testToArrayRangeNegative3() {
        try {
            testToArrayRange(5, this.array.size + 6)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testToArrayRangeNegative4() {
        makeEmpty()
        try {
            testToArrayRange(2, 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testToArrayRangeNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            testToArrayRange(INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testToArrayRangeNegativeAfterClear() {
        makeEmpty()
        try {
            testToArrayRange(0, 1)
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

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoPNegative1() {
        try {
            testCopyIntoP(this.array.size, 0, -1, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoPNegative2() {
        try {
            testCopyIntoP(this.array.size / 2, 0, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoPNegative3() {
        try {
            testCopyIntoP(this.array.size, 0, this.array.size, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoPNegative4() {
        try {
            testCopyIntoP(this.array.size, -1, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoPNegative5() {
        try {
            testCopyIntoP(this.array.size, this.array.size, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoPNegative6() {
        try {
            testCopyIntoP(this.array.size, 0, 0, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoPNegative7() {
        makeEmpty()
        try {
            testCopyIntoP(0, 0, 1, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoPNegative8() {
        try {
            testCopyIntoP(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoPNegativeAfterEnsureCapacity() {
        try {
            testCopyIntoP(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
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
        val initial = wrapper2.createArray(destSize)
        val dest = wrapper2.createNotEmptyArray(initial)

        this.wrapper.copyIntoT(dest, destinationOffset, startIndex, endIndex)

        assertUnchanged()
        val length = endIndex - startIndex
        val expected = this.wrapper.toArray()
        val actual = wrapper2.toArray()
        this.wrapper.assertElementsEqual(actual, 0, destinationOffset, initial, 0)
        this.wrapper.assertElementsEqual(actual, destinationOffset, destinationOffset + length, expected, startIndex)
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, actual.size, initial,
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

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegative1() {
        try {
            testCopyIntoT(this.array.size, 0, -1, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegative2() {
        try {
            testCopyIntoT(this.array.size / 2, 0, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegative3() {
        try {
            testCopyIntoT(this.array.size, 0, this.array.size, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegative4() {
        try {
            testCopyIntoT(this.array.size, -1, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegative5() {
        try {
            testCopyIntoT(this.array.size, this.array.size, 0, this.array.size)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegative6() {
        try {
            testCopyIntoT(this.array.size, 0, 0, this.array.size * 2)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegative7() {
        makeEmpty()
        try {
            testCopyIntoT(0, 0, 1, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegative8() {
        try {
            testCopyIntoT(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegativeAfterEnsureCapacity() {
        try {
            testCopyIntoT(0, 1, 0, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegativeAfterClear() {
        makeEmpty()
        try {
            testCopyIntoT(1, 0, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegativeAfterDestEnsureCapacity() {
        val wrapper2 = this.wrapper.newInstance()
        val initial = wrapper2.createArray(1)
        val dest = wrapper2.createNotEmptyArray(initial)
        dest.ensureCapacity(2)
        try {
            this.wrapper.copyIntoT(dest, 1, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTNegativeAfterDestClear() {
        val wrapper2 = this.wrapper.newInstance()
        val initial = wrapper2.createArray(1)
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
        this.wrapper.assertElementsEqual(actual, destinationOffset + length, actual.size,
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

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative1() {
        try {
            testCopyIntoTSelf(0, -1, INITIAL_SIZE - 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative2() {
        try {
            testCopyIntoTSelf(0, INITIAL_SIZE, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative3() {
        try {
            testCopyIntoTSelf(-1, 0, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative4() {
        try {
            testCopyIntoTSelf(INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative5() {
        try {
            testCopyIntoTSelf(1, 1, 0)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative6() {
        try {
            testCopyIntoTSelf(1, 0, INITIAL_SIZE)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegative7() {
        try {
            testCopyIntoTSelf(0, 1, INITIAL_SIZE + 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testCopyIntoTSelfNegativeAfterEnsureCapacity() {
        this.array.ensureCapacity(INITIAL_SIZE * 2)
        try {
            testCopyIntoTSelf(INITIAL_SIZE, 0, 1)
        } finally {
            assertUnchanged()
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
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
        this.mao.reset()

        this.array.clear()

        this.mao.checkOnlySizeChanged(this.array)
        assertEquals(0, this.array.size)
    }

    // ========================= toString() tests =========================

    @Test
    fun testToString() {
        val actual = this.array.toString()
        val expected = this.wrapper.toArray().contentToString()
        assertEquals(expected, actual)
        val regex = "\\[(\\w{16}|null)?(, (\\w{16}|null)){${this.initialSize - 1}}]"
        assertTrue(actual.matches(regex.toRegex()), "toString() output matches to regex '$regex'. Actual = '$actual'")
    }

    @Test
    fun testToStringAfterResize() {
        this.array.resize(this.initialSize / 2)
        val actual = this.array.toString()
        val expected = this.wrapper.toArray().contentToString()
        assertEquals(expected, actual)
        val regex = "\\[(\\w{16}|null)?(, (\\w{16}|null)){${this.array.size - 1}}]"
        assertTrue(actual.matches(regex.toRegex()), "toString() output matches to regex '$regex'. Actual = '$actual'")
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
     * @param P corresponding class for boxed elements
     */
    abstract class ArrayWrapper<T : ObservableArray<T>, P> {

        protected lateinit var array: T

        abstract fun createEmptyArray(): T

        abstract fun createNotEmptyArray(src: Array<P>): T

        abstract fun newInstance(): ArrayWrapper<T, P>

        abstract val nextValue: P

        abstract operator fun set(index: Int, value: P)

        abstract fun setAllP(src: Array<P>)

        abstract fun setAllT(src: T)

        abstract fun setAllP(src: Array<P>, startIndex: Int, endIndex: Int)

        abstract fun setAllT(src: T, startIndex: Int, endIndex: Int)

        abstract fun addAllP(src: Array<P>)

        abstract fun addAllT(src: T)

        abstract operator fun plusAssign(src: Array<P>)

        abstract operator fun plusAssign(src: T)

        abstract fun addAllP(src: Array<P>, startIndex: Int, endIndex: Int)

        abstract fun addAllT(src: T, startIndex: Int, endIndex: Int)

        abstract fun setP(src: Array<P>, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract fun setT(src: T, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract fun copyIntoP(dest: Array<P>, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract fun copyIntoT(dest: T, destinationOffset: Int, startIndex: Int, endIndex: Int)

        abstract operator fun get(index: Int): P

        abstract fun toArray(): Array<P>

        abstract fun toArray(startIndex: Int, endIndex: Int): Array<P>

        fun createArray(size: Int): Array<P> {
            return createArray(size, true)
        }

        abstract fun createArray(size: Int, fillWithData: Boolean): Array<P>

        abstract fun cloneArray(array: Array<P>): Array<P>

        abstract fun assertElementsEqual(actual: Array<P>, from: Int, to: Int, expected: Array<P>, expFrom: Int)

    }

    private class StringWrapper : ArrayWrapper<ObservableObjectArray<String>, String>() {

        private var counter = 0

        override fun createEmptyArray(): ObservableObjectArray<String> {
            return ObservableCollections.observableObjectArray(arrayOf("1")).also { this.array = it }
        }

        override fun createNotEmptyArray(src: Array<String>): ObservableObjectArray<String> {
            return when (this.counter % 1) {
                0 -> ObservableCollections.observableObjectArray(arrayOf("1"), *src)
                else -> ObservableCollections.observableObjectArray<String>(arrayOf("1"),
                        ObservableCollections.observableObjectArray(arrayOf("1"), *src))
            }.also { this.array = it }
        }

        override fun newInstance(): ArrayWrapper<ObservableObjectArray<String>, String> {
            return StringWrapper()
        }

        override val nextValue: String
            get() {
                return RandomUtils.randomString(16)
            }

        override operator fun set(index: Int, value: String) {
            this.array[index] = value
        }

        override fun setAllP(src: Array<String>) {
            this.array.setAll(*src)
        }

        override fun setAllT(src: ObservableObjectArray<String>) {
            this.array.setAll(src)
        }

        override fun setAllP(src: Array<String>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllT(src: ObservableObjectArray<String>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun addAllP(src: Array<String>) {
            this.array.addAll(*src)
        }

        override fun addAllT(src: ObservableObjectArray<String>) {
            this.array.addAll(src)
        }

        override operator fun plusAssign(src: Array<String>) {
            this.array += src
        }

        override operator fun plusAssign(src: ObservableObjectArray<String>) {
            this.array += src
        }

        override fun addAllP(src: Array<String>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllT(src: ObservableObjectArray<String>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun setP(src: Array<String>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setT(src: ObservableObjectArray<String>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoP(dest: Array<String>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoT(dest: ObservableObjectArray<String>, destinationOffset: Int, startIndex: Int,
                endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): String {
            return this.array[index]
        }

        override fun toArray(): Array<String> {
            return this.array.toTypedArray()
        }

        override fun toArray(startIndex: Int, endIndex: Int): Array<String> {
            return this.array.toTypedArray(startIndex, endIndex)
        }

        override fun createArray(size: Int, fillWithData: Boolean): Array<String> {
            return (if (fillWithData) Array(size) { this.nextValue } else Array(size) { "1" })
        }

        override fun cloneArray(array: Array<String>): Array<String> {
            return array.copyOf()
        }

        override fun assertElementsEqual(actual: Array<String>, from: Int, to: Int, expected: Array<String>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected String = ${expected[j]}, actual String = ${actual[i]}")
                j++
            }
        }

    }

    private class NullableStringWrapper : ArrayWrapper<ObservableObjectArray<String?>, String?>() {

        private var counter = 0

        override fun createEmptyArray(): ObservableObjectArray<String?> {
            return ObservableCollections.observableObjectArray(arrayOf<String?>(null)).also { this.array = it }
        }

        override fun createNotEmptyArray(src: Array<String?>): ObservableObjectArray<String?> {
            return when (this.counter % 1) {
                0 -> ObservableCollections.observableObjectArray(arrayOf(null), *src)
                else -> ObservableCollections.observableObjectArray<String?>(arrayOf(null),
                        ObservableCollections.observableObjectArray(arrayOf(null), *src))
            }.also { this.array = it }
        }

        override fun newInstance(): ArrayWrapper<ObservableObjectArray<String?>, String?> {
            return NullableStringWrapper()
        }

        override val nextValue: String?
            get() {
                return if (RandomUtils.nextInt() % 2 == 0) RandomUtils.randomString(16) else null
            }

        override operator fun set(index: Int, value: String?) {
            this.array[index] = value
        }

        override fun setAllP(src: Array<String?>) {
            this.array.setAll(*src)
        }

        override fun setAllT(src: ObservableObjectArray<String?>) {
            this.array.setAll(src)
        }

        override fun setAllP(src: Array<String?>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun setAllT(src: ObservableObjectArray<String?>, startIndex: Int, endIndex: Int) {
            this.array.setAll(src, startIndex, endIndex)
        }

        override fun addAllP(src: Array<String?>) {
            this.array.addAll(*src)
        }

        override fun addAllT(src: ObservableObjectArray<String?>) {
            this.array.addAll(src)
        }

        override operator fun plusAssign(src: Array<String?>) {
            this.array += src
        }

        override operator fun plusAssign(src: ObservableObjectArray<String?>) {
            this.array += src
        }

        override fun addAllP(src: Array<String?>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun addAllT(src: ObservableObjectArray<String?>, startIndex: Int, endIndex: Int) {
            this.array.addAll(src, startIndex, endIndex)
        }

        override fun setP(src: Array<String?>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun setT(src: ObservableObjectArray<String?>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.set(src, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoP(dest: Array<String?>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override fun copyIntoT(dest: ObservableObjectArray<String?>, destinationOffset: Int, startIndex: Int,
                endIndex: Int) {
            this.array.copyInto(dest, destinationOffset, startIndex, endIndex)
        }

        override operator fun get(index: Int): String? {
            return this.array[index]
        }

        override fun toArray(): Array<String?> {
            return this.array.toTypedArray()
        }

        override fun toArray(startIndex: Int, endIndex: Int): Array<String?> {
            return this.array.toTypedArray(startIndex, endIndex)
        }

        override fun createArray(size: Int, fillWithData: Boolean): Array<String?> {
            return (if (fillWithData) Array(size) { this.nextValue } else Array(size) { null })
        }

        override fun cloneArray(array: Array<String?>): Array<String?> {
            return array.copyOf()
        }

        override fun assertElementsEqual(actual: Array<String?>, from: Int, to: Int, expected: Array<String?>,
                expFrom: Int) {
            var j = expFrom
            for (i in from until to) {
                assertEquals(expected[j], actual[i],
                        "expected String? = ${expected[j]}, actual String? = ${actual[i]}")
                j++
            }
        }

    }

    companion object {

        private const val INITIAL_SIZE: Int = 6

        @Parameters
        @JvmStatic
        fun createParameters(): List<Array<out Any?>> {
            return listOf(
                    arrayOf(StringWrapper()),
                    arrayOf(NullableStringWrapper())
            )
        }

    }

}