package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableSet
import kotlin.test.*

@Suppress("ReplaceAssertBooleanWithAssertEquality", "KotlinConstantConditions")
class BidirectionalContentBindingSetTest {

    private lateinit var op1: ObservableSet<Int>

    private lateinit var op2: ObservableSet<Int>

    private lateinit var op3: ObservableSet<Int>

    private lateinit var set0: Set<Int>

    private lateinit var set1: Set<Int>

    private lateinit var set2: Set<Int>

    @BeforeTest
    fun setUp() {
        this.set0 = hashSetOf()
        this.set1 = hashSetOf(-1)
        this.set2 = hashSetOf(2, 1)

        this.op1 = ObservableCollections.observableSet(this.set1)
        this.op2 = ObservableCollections.observableSet(this.set2)
        this.op3 = ObservableCollections.observableSet(this.set0)
    }

    @Test
    fun testBind() {
        Bindings.bindContentBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not overdo weak references

        assertEquals(this.set2, this.op1)
        assertEquals(this.set2, this.op2)

        this.op1.setAll(this.set1)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set1, this.op2)

        this.op1.setAll(this.set0)
        assertEquals(this.set0, this.op1)
        assertEquals(this.set0, this.op2)

        this.op1.setAll(this.set2)
        assertEquals(this.set2, this.op1)
        assertEquals(this.set2, this.op2)

        this.op2.setAll(this.set1)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set1, this.op2)

        this.op2.setAll(this.set0)
        assertEquals(this.set0, this.op1)
        assertEquals(this.set0, this.op2)

        this.op2.setAll(this.set2)
        assertEquals(this.set2, this.op1)
        assertEquals(this.set2, this.op2)
    }

    @Test
    fun testBind_X_Self() {
        assertFailsWith<IllegalArgumentException> {
            Bindings.bindContentBidirectional(this.op1, this.op1)
        }
    }

    @Test
    fun testUnbind() {
        // unbind non-existing binding => no-op
        Bindings.unbindContentBidirectional(this.op1, this.op2)

        Bindings.bindContentBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not overdo weak references

        assertEquals(this.set2, this.op1)
        assertEquals(this.set2, this.op2)

        Bindings.unbindContentBidirectional(this.op1, this.op2)
        System.gc()
        assertEquals(this.set2, this.op1)
        assertEquals(this.set2, this.op2)

        this.op1.setAll(this.set1)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set2, this.op2)

        this.op2.setAll(this.set0)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set0, this.op2)

        // unbind in flipped order
        Bindings.bindContentBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not overdo weak references

        assertEquals(this.set0, this.op1)
        assertEquals(this.set0, this.op2)

        Bindings.unbindContentBidirectional(this.op2, this.op1)
        System.gc()
        assertEquals(this.set0, this.op1)
        assertEquals(this.set0, this.op2)

        this.op1.setAll(this.set1)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set0, this.op2)

        this.op2.setAll(this.set2)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set2, this.op2)
    }

    @Test
    fun testUnbind_X_Self() {
        assertFailsWith<IllegalArgumentException> {
            Bindings.unbindContentBidirectional(this.op1, this.op1)
        }
    }

    @Test
    fun testChaining() {
        Bindings.bindContentBidirectional(this.op1, this.op2)
        Bindings.bindContentBidirectional(this.op2, this.op3)
        System.gc() // making sure we did not overdo weak references
        assertEquals(this.set0, this.op1)
        assertEquals(this.set0, this.op2)
        assertEquals(this.set0, this.op3)
        this.op1.setAll(this.set1)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set1, this.op2)
        assertEquals(this.set1, this.op3)
        this.op2.setAll(this.set2)
        assertEquals(this.set2, this.op1)
        assertEquals(this.set2, this.op2)
        assertEquals(this.set2, this.op3)
        this.op3.setAll(this.set0)
        assertEquals(this.set0, this.op1)
        assertEquals(this.set0, this.op2)
        assertEquals(this.set0, this.op3)

        // now unbind
        Bindings.unbindContentBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not overdo weak references
        assertEquals(this.set0, this.op1)
        assertEquals(this.set0, this.op2)
        assertEquals(this.set0, this.op3)
        this.op1.setAll(this.set1)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set0, this.op2)
        assertEquals(this.set0, this.op3)
        this.op2.setAll(this.set2)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set2, this.op2)
        assertEquals(this.set2, this.op3)
        this.op3.setAll(this.set0)
        assertEquals(this.set1, this.op1)
        assertEquals(this.set0, this.op2)
        assertEquals(this.set0, this.op3)
    }

    @Test
    fun testHashCode() {
        val hc1 = BidirectionalContentBinding.bind(this.op1, this.op2).hashCode()
        BidirectionalContentBinding.unbind(this.op1, this.op2)
        val hc2 = BidirectionalContentBinding.bind(this.op2, this.op1).hashCode()
        assertEquals(hc1.toLong(), hc2.toLong())
    }

    @Test
    @Suppress("SENSELESS_COMPARISON")
    fun testEquals() {
        val golden = BidirectionalContentBinding.bind(this.op1, this.op2)
        BidirectionalContentBinding.unbind(this.op1, this.op2)
        assertTrue(golden == golden)
        assertFalse(golden == null)
        assertFalse(golden == this.op1)
        assertTrue(golden == BidirectionalContentBinding.bind(this.op1, this.op2))
        BidirectionalContentBinding.unbind(this.op1, this.op2)
        assertTrue(golden == BidirectionalContentBinding.bind(this.op2, this.op1))
        BidirectionalContentBinding.unbind(this.op1, this.op2)
        assertFalse(golden == BidirectionalContentBinding.bind(this.op1, this.op3))
        BidirectionalContentBinding.unbind(this.op1, this.op3)
        assertFalse(golden == BidirectionalContentBinding.bind(this.op3, this.op1))
        BidirectionalContentBinding.unbind(this.op1, this.op3)
        assertFalse(golden == BidirectionalContentBinding.bind(this.op3, this.op2))
        BidirectionalContentBinding.unbind(this.op2, this.op3)
        assertFalse(golden == BidirectionalContentBinding.bind(this.op2, this.op3))
        BidirectionalContentBinding.unbind(this.op2, this.op3)
    }

    @Test
    @Suppress("UNUSED_VALUE")
    fun testEqualsWithGCedProperty() {
        var op: ObservableSet<Int>? = ObservableCollections.observableSet(this.set1)
        val binding1 = BidirectionalContentBinding.bind(op!!, this.op2)
        BidirectionalContentBinding.unbind(op, this.op2)
        val binding2 = BidirectionalContentBinding.bind(op, this.op2)
        BidirectionalContentBinding.unbind(op, this.op2)
        val binding3 = BidirectionalContentBinding.bind(this.op2, op)
        BidirectionalContentBinding.unbind(op, this.op2)
        val binding4 = BidirectionalContentBinding.bind(this.op2, op)
        BidirectionalContentBinding.unbind(op, this.op2)
        op = null
        System.gc()
        assertTrue(binding1 == binding1)
        assertFalse(binding1 == binding2)
        assertFalse(binding1 == binding3)
        assertTrue(binding3 == binding3)
        assertFalse(binding3 == binding1)
        assertFalse(binding3 == binding4)
    }

}