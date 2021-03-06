package io.github.vinccool96.observationskt.collections.transformation

import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class TransformationListTest {

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

    private lateinit var list1: TransformationList<String, String>

    private lateinit var list2: TransformationList<String, String>

    private lateinit var list3: ObservableList<String>

    @Before
    fun setUp() {
        this.list3 = ObservableCollections.observableArrayList()
        this.list2 = TransformationListImpl(list3)
        this.list1 = TransformationListImpl(list2)
    }

    @Test
    fun testDirect() {
        assertEquals(list2, list1.source)
        assertEquals(list3, list2.source)
    }

}