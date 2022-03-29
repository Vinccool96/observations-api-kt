package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.utils.RandomUtils
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class ExtendedObservableListTest(private val listFactory: Callable<ObservableList<String?>>) {

    private var initialSize: Int = 0

    private lateinit var initialElements: List<String?>

    private lateinit var array: ObservableList<String?>

    private lateinit var mao: MockListObserver<String?>

    @BeforeTest
    fun setUp() {
        this.initialSize = INITIAL_SIZE
        this.initialElements = createCollection(this.initialSize).toList()
        this.array = createNotEmptyList(this.initialElements)
        this.mao = MockListObserver()
        this.array.addListener(this.mao)
    }

    private fun makeEmpty() {
        this.initialSize = 0
        this.initialElements = createCollection(this.initialSize).toList()
        this.array.clear()
        this.mao.clear()
    }

    private fun assertUnchanged() {
        this.mao.check0()
        assertEquals(this.initialSize, this.array.size)
        assertEquals(this.initialSize, this.array.size)
        assertElementsEqual(this.array, 0, this.array.size, this.initialElements)
    }

    private val nextValue: String?
        get() {
            return if (RandomUtils.nextInt() % 2 == 0) RandomUtils.randomString(16) else null
        }

    private fun createCollection(size: Int, fillWithData: Boolean = true): Collection<String?> {
        val col: MutableCollection<String?> =
                if (fillWithData && RandomUtils.nextInt() % 2 == 0) mutableSetOf() else mutableListOf()
        while (col.size < size) {
            col.add(if (fillWithData) this.nextValue else null)
        }
        return col
    }

    private fun createEmptyList(): ObservableList<String?> {
        return this.listFactory.call()
    }

    private fun createNotEmptyList(src: Collection<String?>): ObservableList<String?> {
        val list = this.listFactory.call()
        list.setAll(src)
        return list
    }

    private fun assertElementsEqual(actual: List<String?>, from: Int, to: Int, expected: List<String?>) {
        for ((j, i) in (from until to).withIndex()) {
            assertEquals(expected[j], actual[i],
                    "expected String? = ${expected[j]}, actual String? = ${actual[i]}")
        }
    }

    // ========================= pre-condition tests =========================

    @Test
    fun testSize() {
        this.mao.check0()
        assertEquals(INITIAL_SIZE, this.array.size)
    }

    @Test
    fun testClear() {
        val removed = this.array.toList()
        this.array.clear()
        this.mao.check1AddRemove(this.array, removed, 0, 0)
        assertEquals(0, this.array.size)
    }

    @Test
    fun testGet() {
        for (i in 0 until this.array.size) {
            val expected = this.initialElements[i]
            val actual = this.array[i]
            assertEquals(expected, actual)
        }
        assertUnchanged()
    }

    @Test
    fun testToArray() {
        val expected = this.initialElements
        val actual = this.array.toTypedArray()
        assertEquals(INITIAL_SIZE, actual.size)
        assertElementsEqual(actual.asList(), 0, this.array.size, expected)
    }

    // ========================= add/remove listener tests =========================

    @Test
    fun testAddRemoveListener() {
        val mao2 = MockListObserver<String?>()
        this.array.addListener(mao2)
        this.array.removeListener(this.mao)
        this.array[0] = this.nextValue
        this.mao.check0()
        mao2.check1()
    }

    @Test
    fun testAddTwoListenersElementChange() {
        val mao2 = MockListObserver<String?>()
        this.array.addListener(mao2)
        this.array[0] = this.nextValue
        this.mao.check1()
        mao2.check1()
    }

    @Test
    fun testAddTwoListenersSizeChange() {
        val mao2 = MockListObserver<String?>()
        this.array.addListener(mao2)
        this.array.clear()
        this.mao.check1()
        mao2.check1()
    }

    @Test
    fun testAddThreeListeners() {
        val mao2 = MockListObserver<String?>()
        val mao3 = MockListObserver<String?>()
        this.array.addListener(mao2)
        this.array.addListener(mao3)
        this.array[0] = this.nextValue
        this.mao.check1()
        mao2.check1()
        mao3.check1()
    }

    @Test
    fun testAddThreeListenersSizeChange() {
        val mao2 = MockListObserver<String?>()
        val mao3 = MockListObserver<String?>()
        this.array.addListener(mao2)
        this.array.addListener(mao3)
        this.array.clear()
        this.mao.check1()
        mao2.check1()
        mao3.check1()
    }

    @Test
    fun testAddListenerTwice() {
        this.array.addListener(this.mao) // add it a second time
        this.array[1] = this.nextValue
        this.mao.check1()
    }

    @Test
    fun testRemoveListenerTwice() {
        this.array.removeListener(this.mao)
        this.array.removeListener(this.mao)
        this.array[1] = this.nextValue
        this.mao.check0()
    }

    // ========================= setAll(vararg) tests =========================

    private fun testSetAllP(newSize: Int) {
        val expected = createCollection(newSize)
        val removed = this.array.toList()

        this.array.setAll(*expected.toTypedArray())

        this.mao.check1AddRemove(this.array, removed, 0, newSize)
        val actual = this.array.toList()
        assertEquals(expected.size, this.array.size)
        assertEquals(expected.size, actual.size)
        assertElementsEqual(actual, 0, expected.size, expected.toList())
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
        this.array.setAll(*createCollection(0).toTypedArray())
        assertUnchanged()
        assertEquals(0, this.array.size)
    }

    // ========================= setAll(collection) tests =========================

    private fun testSetAllT(newSize: Int) {
        val expected = createCollection(newSize)
        val src = createNotEmptyList(expected)
        val removed = this.array.toList()

        this.array.setAll(src)

        this.mao.check1AddRemove(this.array, removed, 0, newSize)
        val actual = this.array.toList()
        assertEquals(expected.size, this.array.size)
        assertEquals(expected.size, actual.size)
        assertElementsEqual(actual, 0, expected.size, expected.toList())
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
        this.array.setAll(createEmptyList())
        assertUnchanged()
        assertEquals(0, this.array.size)
    }

    @Test
    fun testSetAllTSelf() {
        this.array.setAll(this.array)

        this.mao.check1AddRemove(this.array, this.array.toList(), 0, this.array.size)
        val actual = this.array.toList()
        assertEquals(this.initialSize, this.array.size)
        assertEquals(this.initialSize, actual.size)
        assertElementsEqual(actual, 0, this.initialSize, this.initialElements)
    }

    @Test
    fun testSetAllTSelfEmpty() {
        makeEmpty()

        this.array.setAll(this.array)

        this.mao.check0()
        val actual = this.array.toList()
        assertEquals(0, this.array.size)
        assertEquals(0, actual.size)
    }

    // ========================= addAll(vararg) tests =========================

    private fun testAddAllP(srcSize: Int) {
        val src = createCollection(srcSize)
        val oldSize = this.array.size

        this.array.addAll(*src.toTypedArray())

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, listOf(), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.array.toList()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
        assertElementsEqual(actual, 0, oldSize, this.initialElements)
        assertElementsEqual(actual, oldSize, newSize, src.toList())
    }

    @Test
    fun testAddAllP0() {
        this.array.addAll(createCollection(0))
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
        this.array.addAll(createCollection(0))
        assertUnchanged()
    }

    @Test
    fun testAddAllPManyPoints() {
        for (i in 0 until 65_000) {
            this.array.addAll(*createCollection(3).toTypedArray())
        }
    }

    // ========================= addAll(collection) tests =========================

    private fun testAddAllT(srcSize: Int) {
        val src = createCollection(srcSize)
        val oldSize = this.array.size

        this.array.addAll(src)

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, listOf(), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.array.toList()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
        assertElementsEqual(actual, 0, oldSize, this.initialElements)
        assertElementsEqual(actual, oldSize, newSize, src.toList())
    }

    @Test
    fun testAddAllT0() {
        this.array.addAll(createEmptyList())
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
        this.array.addAll(createEmptyList())
        assertUnchanged()
    }

    @Test
    fun testAddAllTSelf() {
        this.array.addAll(this.array)

        this.mao.check1AddRemove(this.array, listOf(), this.initialSize, this.initialSize * 2)
        assertEquals(this.initialSize * 2, this.array.size)
        val actual = this.array.toList()
        assertElementsEqual(actual, 0, this.initialSize, this.initialElements)
        assertElementsEqual(actual, this.initialSize, this.initialSize * 2, this.initialElements)
    }

    @Test
    fun testAddAllTSelfEmpty() {
        makeEmpty()

        this.array.addAll(this.array)

        this.mao.check0()
        val actual = this.array.toList()
        assertEquals(0, this.array.size)
        assertEquals(0, actual.size)
    }

    @Test
    fun testAddAllTManyPoints() {
        for (i in 0 until 65_000) {
            this.array.addAll(createNotEmptyList(createCollection(3)))
        }
    }

    // ========================= plusAssign(array) tests =========================

    private fun testPlusAssignP(srcSize: Int) {
        val src = createCollection(srcSize)
        val oldSize = this.array.size

        this.array += src.toTypedArray()

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, listOf(), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.array.toList()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
        assertElementsEqual(actual, 0, oldSize, this.initialElements)
        assertElementsEqual(actual, oldSize, newSize, src.toList())
    }

    @Test
    fun testPlusAssignP0() {
        this.array += createCollection(0).toTypedArray()
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
        this.array += createCollection(0).toTypedArray()
        assertUnchanged()
    }

    @Test
    fun testPlusAssignPManyPoints() {
        for (i in 0 until 65_000) {
            this.array += createCollection(3).toTypedArray()
        }
    }

    // ========================= plusAssign(observable array) tests =========================

    private fun testPlusAssignT(srcSize: Int) {
        val src = createCollection(srcSize)
        val oldSize = this.array.size

        this.array += src

        val newSize = oldSize + srcSize
        val sizeChanged = newSize != oldSize
        if (sizeChanged) {
            this.mao.check1AddRemove(this.array, listOf(), oldSize, newSize)
        } else {
            this.mao.check0()
        }
        val actual = this.array.toList()
        assertEquals(newSize, this.array.size)
        assertEquals(newSize, actual.size)
        assertElementsEqual(actual, 0, oldSize, this.initialElements)
        assertElementsEqual(actual, oldSize, newSize, src.toList())
    }

    @Test
    fun testPlusAssignT0() {
        this.array += createEmptyList()
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
        this.array += createEmptyList()
        assertUnchanged()
    }

    @Test
    fun testPlusAssignTSelf() {
        this.array += this.array

        this.mao.check1AddRemove(this.array, listOf(), this.initialSize, this.initialSize * 2)
        assertEquals(this.initialSize * 2, this.array.size)
        val actual = this.array.toList()
        assertElementsEqual(actual, 0, this.initialSize, this.initialElements)
        assertElementsEqual(actual, this.initialSize, this.initialSize * 2, this.initialElements)
    }

    @Test
    fun testPlusAssignTSelfEmpty() {
        makeEmpty()

        this.array += this.array

        this.mao.check0()
        val actual = this.array.toList()
        assertEquals(0, this.array.size)
        assertEquals(0, actual.size)
    }

    @Test
    fun testPlusAssignTManyPoints() {
        for (i in 0 until 65_000) {
            this.array += createNotEmptyList(createCollection(3))
        }
    }

    companion object {

        private const val INITIAL_SIZE: Int = 6

        @Parameters
        @JvmStatic
        fun createParameters(): List<Array<out Any?>> {
            return listOf(
                    arrayOf(TestedObservableLists.ARRAY_LIST),
                    arrayOf(TestedObservableLists.LINKED_LIST),
                    arrayOf(TestedObservableLists.CHECKED_OBSERVABLE_ARRAY_LIST),
                    arrayOf(TestedObservableLists.SYNCHRONIZED_OBSERVABLE_ARRAY_LIST),
                    arrayOf(TestedObservableLists.OBSERVABLE_LIST_PROPERTY)
            )
        }

    }

}