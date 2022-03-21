package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableMap
import kotlin.test.*

@Suppress("ReplaceAssertBooleanWithAssertEquality", "KotlinConstantConditions")
class BidirectionalContentBindingMapTest {

    private lateinit var op1: ObservableMap<String, Int>

    private lateinit var op2: ObservableMap<String, Int>

    private lateinit var op3: ObservableMap<String, Int>

    private lateinit var map0: Map<String, Int>

    private lateinit var map1: Map<String, Int>

    private lateinit var map2: Map<String, Int>

    @BeforeTest
    fun setUp() {
        this.map0 = hashMapOf()
        this.map1 = hashMapOf(key1 to -1)
        this.map2 = hashMapOf(key2_1 to 2, key2_2 to 1)

        this.op1 = ObservableCollections.observableMap(this.map1)
        this.op2 = ObservableCollections.observableMap(this.map2)
        this.op3 = ObservableCollections.observableMap(this.map0)
    }

    @Test
    fun testBind() {
        Bindings.bindContentBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not overdo weak references
        assertEquals(this.map2, this.op1)
        assertEquals(this.map2, this.op2)

        this.op1.clear()
        this.op1.putAll(this.map1)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map1, this.op2)

        this.op1.clear()
        this.op1.putAll(this.map0)
        assertEquals(this.map0, this.op1)
        assertEquals(this.map0, this.op2)

        this.op1.clear()
        this.op1.putAll(this.map2)
        assertEquals(this.map2, this.op1)
        assertEquals(this.map2, this.op2)

        this.op2.clear()
        this.op2.putAll(this.map1)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map1, this.op2)

        this.op2.clear()
        this.op2.putAll(this.map0)
        assertEquals(this.map0, this.op1)
        assertEquals(this.map0, this.op2)

        this.op2.clear()
        this.op2.putAll(this.map2)
        assertEquals(this.map2, this.op1)
        assertEquals(this.map2, this.op2)
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

        assertEquals(this.map2, this.op1)
        assertEquals(this.map2, this.op2)

        Bindings.unbindContentBidirectional(this.op1, this.op2)
        System.gc()
        assertEquals(this.map2, this.op1)
        assertEquals(this.map2, this.op2)

        this.op1.clear()
        this.op1.putAll(this.map1)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map2, this.op2)

        this.op2.clear()
        this.op2.putAll(this.map0)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map0, this.op2)

        // unbind in flipped order
        Bindings.bindContentBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not overdo weak references

        assertEquals(this.map0, this.op1)
        assertEquals(this.map0, this.op2)

        Bindings.unbindContentBidirectional(this.op2, this.op1)
        System.gc()
        assertEquals(this.map0, this.op1)
        assertEquals(this.map0, this.op2)

        this.op1.clear()
        this.op1.putAll(this.map1)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map0, this.op2)

        this.op2.clear()
        this.op2.putAll(this.map2)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map2, this.op2)
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
        assertEquals(this.map0, this.op1)
        assertEquals(this.map0, this.op2)
        assertEquals(this.map0, this.op3)
        this.op1.putAll(this.map1)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map1, this.op2)
        assertEquals(this.map1, this.op3)
        this.op2.putAll(this.map2)
        assertEquals(this.map2, this.op1)
        assertEquals(this.map2, this.op2)
        assertEquals(this.map2, this.op3)
        this.op3.putAll(this.map0)
        assertEquals(this.map0, this.op1)
        assertEquals(this.map0, this.op2)
        assertEquals(this.map0, this.op3)

        // now unbind
        Bindings.unbindContentBidirectional(this.op1, this.op2)
        System.gc() // making sure we did not overdo weak references
        assertEquals(this.map0, this.op1)
        assertEquals(this.map0, this.op2)
        assertEquals(this.map0, this.op3)
        this.op1.putAll(this.map1)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map0, this.op2)
        assertEquals(this.map0, this.op3)
        this.op2.putAll(this.map2)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map2, this.op2)
        assertEquals(this.map2, this.op3)
        this.op3.putAll(this.map0)
        assertEquals(this.map1, this.op1)
        assertEquals(this.map0, this.op2)
        assertEquals(this.map0, this.op3)
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
        var op: ObservableMap<String, Int>? = ObservableCollections.observableMap(this.map1)
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

    companion object {

        private const val key1 = "Key1"

        private const val key2_1 = "Key2_1"

        private const val key2_2 = "Key2_2"

    }

}