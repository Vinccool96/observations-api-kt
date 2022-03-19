package io.github.vinccool96.observationskt.collections.transformation

import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TransformationListTest {

    private lateinit var list1: TransformationList<String, String>

    private lateinit var list2: TransformationList<String, String>

    private lateinit var list3: ObservableList<String>

    private lateinit var list4: ObservableList<String>

    @BeforeTest
    fun setUp() {
        this.list4 = ObservableCollections.observableArrayList()
        this.list3 = ObservableCollections.observableArrayList()
        this.list2 = TransformationListImpl(list3)
        this.list1 = TransformationListImpl(list2)
    }

    @Test
    fun testDirect() {
        assertEquals(list2, list1.source)
        assertEquals(list3, list2.source)
    }

    @Test
    fun testIsInTransformationChain() {
        assertTrue(this.list1.isInTransformationChain(this.list2))
        assertTrue(this.list1.isInTransformationChain(this.list3))
        assertFalse(this.list1.isInTransformationChain(this.list4))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testGetSourceIndexFor_Exception() {
        this.list1.getSourceIndexFor(this.list4, 0)
    }

    private class TransformationListImpl(list: ObservableList<String>) : TransformationList<String, String>(list) {

        override fun sourceChanged(c: Change<out String>) {
            throw UnsupportedOperationException("Not supported yet.")
        }

        override fun get(index: Int): String {
            throw UnsupportedOperationException("Not supported yet.")
        }

        override val size: Int
            get() = throw UnsupportedOperationException("Not supported yet.")

        override fun addAll(vararg elements: String): Boolean {
            throw UnsupportedOperationException("Not supported yet.")
        }

        override fun setAll(vararg elements: String): Boolean {
            throw UnsupportedOperationException("Not supported yet.")
        }

        override fun setAll(col: Collection<String>): Boolean {
            throw UnsupportedOperationException("Not supported yet.")
        }

        override fun getSourceIndex(index: Int): Int {
            throw UnsupportedOperationException("Not supported yet.")
        }

        override fun removeAll(vararg elements: String): Boolean {
            throw UnsupportedOperationException("Not supported yet.")
        }

        override fun retainAll(vararg elements: String): Boolean {
            throw UnsupportedOperationException("Not supported yet.")
        }

        override fun remove(from: Int, to: Int) {
            throw UnsupportedOperationException("Not supported yet.")
        }

    }

}