package io.github.vinccool96.observationskt.collections

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

/**
 * Tests for initially empty ObservableList.
 */
@RunWith(Parameterized::class)
class ObservableListEmptyTest(private val listFactory: Callable<ObservableList<String?>>) {

    private lateinit var list: ObservableList<String?>

    private lateinit var mlo: MockListObserver<String?>

    @Before
    fun setUp() {
        this.list = this.listFactory.call()
        this.mlo = MockListObserver()
        this.list.addListener(this.mlo)
    }

    @Test
    fun testClearEmpty() {
        this.list.clear()
        this.mlo.check0()
    }

    companion object {

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