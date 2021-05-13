package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.SetProperty
import io.github.vinccool96.observationskt.beans.property.SimpleSetProperty
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableSet
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BindingsSetTest {

    private lateinit var property: SetProperty<Any>

    private lateinit var set1: ObservableSet<Any>

    private lateinit var set2: ObservableSet<Any>

    @Before
    fun setUp() {
        this.property = SimpleSetProperty()
        this.set1 = ObservableCollections.observableSet(data1, data2)
        this.set2 = ObservableCollections.observableSet()
    }

    @Test
    fun testSize() {
        val size = Bindings.size(this.property)
        DependencyUtils.checkDependencies(size.dependencies, this.property)

        assertEquals(0, size.get())
        this.property.set(this.set1)
        assertEquals(2, size.get())
        this.set1.remove(data2)
        assertEquals(1, size.get())
        this.property.set(this.set2)
        assertEquals(0, size.get())
        this.set2.add(data2)
        this.set2.add(data2)
        assertEquals(1, size.get())
        this.property.set(null)
        assertEquals(0, size.get())
    }

    @Test
    fun testIsEmpty() {
        val empty = Bindings.isEmpty(this.property)
        DependencyUtils.checkDependencies(empty.dependencies, this.property)

        assertTrue(empty.get())
        this.property.set(this.set1)
        assertFalse(empty.get())
        this.set1.remove(data2)
        assertFalse(empty.get())
        this.property.set(this.set2)
        assertTrue(empty.get())
        this.set2.add(data2)
        this.set2.add(data2)
        assertFalse(empty.get())
        this.property.set(null)
        assertTrue(empty.get())
    }

    @Test
    fun testIsNotEmpty() {
        val notEmpty = Bindings.isNotEmpty(this.property)
        DependencyUtils.checkDependencies(notEmpty.dependencies, this.property)

        assertFalse(notEmpty.get())
        this.property.set(this.set1)
        assertTrue(notEmpty.get())
        this.set1.remove(data2)
        assertTrue(notEmpty.get())
        this.property.set(this.set2)
        assertFalse(notEmpty.get())
        this.set2.add(data2)
        this.set2.add(data2)
        assertTrue(notEmpty.get())
        this.property.set(null)
        assertFalse(notEmpty.get())
    }

    companion object {

        private val data1 = Any()

        private val data2 = Any()

    }

}