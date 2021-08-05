package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.property.BooleanProperty
import io.github.vinccool96.observationskt.beans.property.SimpleBooleanProperty
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableBooleanValue
import org.junit.Before
import org.junit.Test
import kotlin.test.*

@Suppress("SimplifyBooleanWithConstants", "UNUSED_VALUE", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
class BindingsBooleanTest {

    private lateinit var op1: BooleanProperty

    private lateinit var op2: BooleanProperty

    private lateinit var observer: InvalidationListenerMock

    @Before
    fun setUp() {
        this.op1 = SimpleBooleanProperty(true)
        this.op2 = SimpleBooleanProperty(false)
        this.observer = InvalidationListenerMock()
    }

    @Test
    fun testAnd() {
        val binding: BooleanBinding = Bindings.and(this.op1, this.op2)
        binding.addListener(this.observer)

        // check initial value
        assertEquals(true && false, binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.op1, this.op2)

        // change first operand
        this.observer.reset()
        this.op1.set(false)
        assertEquals(false && false, binding.get())
        this.observer.check(binding, 1)

        // change second operand
        this.op1.set(true) // avoid short-circuit invalidation
        binding.get()
        this.observer.reset()
        this.op2.set(true)
        assertEquals(true && true, binding.get())
        this.observer.check(binding, 1)

        // last possibility
        this.op1.set(false)
        assertEquals(false && true, binding.get())
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testAnd_Efficiency() {
        val binding: BooleanBinding = Bindings.and(this.op1, this.op2)
        binding.addListener(this.observer)
        binding.get()

        // change both values
        this.op1.set(false)
        this.op2.set(true)
        this.observer.check(binding, 1)

        // check short circuit invalidation
        this.op2.set(false)
        this.observer.check(null, 0)
        binding.dispose()
    }

    @Test
    fun testAnd_Self() {
        val binding: BooleanBinding = Bindings.and(this.op1, this.op1)
        binding.addListener(this.observer)

        // check initial value
        assertEquals(true && true, binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.op1)

        // change value
        this.observer.reset()
        this.op1.set(false)
        assertEquals(false && false, binding.get())
        this.observer.check(binding, 1)

        // change value again
        this.op1.set(true)
        assertEquals(true && true, binding.get())
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testAnd_WeakReference() {
        val op1 = ObservableBooleanValueMock()
        val op2 = ObservableBooleanValueMock()
        var binding: BooleanBinding? = Bindings.and(op1, op2)
        assertNotNull(op1.listener)
        assertNotNull(op2.listener)
        binding = null
        System.gc()
        op1.fireInvalidationEvent()
        assertNull(op1.listener)
        assertNotNull(op2.listener)
        op2.fireInvalidationEvent()
        assertNull(op1.listener)
        assertNull(op2.listener)
    }

    @Test
    fun testOr() {
        val binding: BooleanBinding = Bindings.or(this.op1, this.op2)
        binding.addListener(this.observer)

        // check initial value
        assertEquals(true || false, binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.op1, this.op2)

        // change first operand
        this.observer.reset()
        this.op1.set(false)
        assertEquals(false || false, binding.get())
        this.observer.check(binding, 1)

        // change second operand
        this.op2.set(true)
        assertEquals(true || true, binding.get())
        this.observer.check(binding, 1)

        // last possibility
        this.op1.set(true)
        assertEquals(true || true, binding.get())
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testOr_Efficiency() {
        val binding: BooleanBinding = Bindings.or(this.op1, this.op2)
        binding.addListener(this.observer)
        binding.get()

        // change both values
        this.op1.set(false)
        this.op2.set(true)
        this.observer.check(binding, 1)

        // check short circuit invalidation
        this.op2.set(false)
        this.observer.check(null, 0)
        binding.dispose()
    }

    @Test
    fun testOr_Self() {
        val binding: BooleanBinding = Bindings.or(this.op1, this.op1)
        binding.addListener(this.observer)

        // check initial value
        assertEquals(true || true, binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.op1)

        // change value
        this.observer.reset()
        this.op1.set(false)
        assertEquals(false || false, binding.get())
        this.observer.check(binding, 1)

        // change value again
        this.op1.set(true)
        assertEquals(true || true, binding.get())
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testOr_WeakReference() {
        val op1 = ObservableBooleanValueMock()
        val op2 = ObservableBooleanValueMock()
        var binding: BooleanBinding? = Bindings.or(op1, op2)
        assertNotNull(op1.listener)
        assertNotNull(op2.listener)
        binding = null
        System.gc()
        op1.fireInvalidationEvent()
        assertNull(op1.listener)
        assertNotNull(op2.listener)
        op2.fireInvalidationEvent()
        assertNull(op1.listener)
        assertNull(op2.listener)
    }

    @Test
    fun testNot() {
        val binding: BooleanBinding = Bindings.not(this.op1)
        binding.addListener(this.observer)

        // check initial value
        assertEquals(!true, binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.op1)

        // change first operand
        this.observer.reset()
        this.op1.set(false)
        assertEquals(!false, binding.get())
        this.observer.check(binding, 1)

        // change again
        this.op1.set(true)
        assertEquals(!true, binding.get())
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testNot_Efficiency() {
        val binding: BooleanBinding = Bindings.not(this.op1)
        binding.addListener(this.observer)
        binding.get()

        // change value twice
        this.op1.set(false)
        this.op1.set(true)
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testEqual() {
        val binding: BooleanBinding = Bindings.equal(this.op1, this.op2)
        binding.addListener(this.observer)

        // check initial value
        assertEquals(true == false, binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.op1, this.op2)

        // change first operand
        this.observer.reset()
        this.op1.set(false)
        assertEquals(false == false, binding.get())
        this.observer.check(binding, 1)

        // change second operand
        this.op2.set(true)
        assertEquals(false == true, binding.get())
        this.observer.check(binding, 1)

        // last possibility
        this.op1.set(true)
        assertEquals(true == true, binding.get())
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testEqual_Efficiency() {
        val binding: BooleanBinding = Bindings.equal(this.op1, this.op2)
        binding.addListener(this.observer)
        binding.get()

        // change both values
        this.op1.set(false)
        this.op2.set(true)
        this.observer.check(binding, 1)

        // check short circuit invalidation
        this.op2.set(false)
        this.observer.check(null, 0)
        binding.dispose()
    }

    @Test
    fun testEqual_Self() {
        val binding: BooleanBinding = Bindings.equal(this.op1, this.op1)
        binding.addListener(this.observer)

        // check initial value
        assertEquals(true == true, binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.op1)

        // change value
        this.observer.reset()
        this.op1.set(false)
        assertEquals(false == false, binding.get())
        this.observer.check(binding, 1)

        // change value again
        this.op1.set(true)
        assertEquals(true == true, binding.get())
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testEqual_WeakReference() {
        val op1 = ObservableBooleanValueMock()
        val op2 = ObservableBooleanValueMock()
        var binding: BooleanBinding? = Bindings.equal(op1, op2)
        assertNotNull(op1.listener)
        assertNotNull(op2.listener)
        binding = null
        System.gc()
        op1.fireInvalidationEvent()
        assertNull(op1.listener)
        assertNotNull(op2.listener)
        op2.fireInvalidationEvent()
        assertNull(op1.listener)
        assertNull(op2.listener)
    }

    @Test
    fun testNotEqual() {
        val binding: BooleanBinding = Bindings.notEqual(this.op1, this.op2)
        binding.addListener(this.observer)

        // check initial value
        assertEquals(true != false, binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.op1, this.op2)

        // change first operand
        this.observer.reset()
        this.op1.set(false)
        assertEquals(false != false, binding.get())
        this.observer.check(binding, 1)

        // change second operand
        this.op2.set(true)
        assertEquals(false != true, binding.get())
        this.observer.check(binding, 1)

        // last possibility
        this.op1.set(true)
        assertEquals(true != true, binding.get())
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testNotEqual_Efficiency() {
        val binding: BooleanBinding = Bindings.notEqual(this.op1, this.op2)
        binding.addListener(this.observer)
        binding.get()

        // change both values
        this.op1.set(false)
        this.op2.set(true)
        this.observer.check(binding, 1)

        // check short circuit invalidation
        this.op2.set(false)
        this.observer.check(null, 0)
        binding.dispose()
    }

    @Test
    fun testNotEqual_Self() {
        val binding: BooleanBinding = Bindings.notEqual(this.op1, this.op1)
        binding.addListener(this.observer)

        // check initial value
        assertEquals(true != true, binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.op1)

        // change value
        this.observer.reset()
        this.op1.set(false)
        assertEquals(false != false, binding.get())
        this.observer.check(binding, 1)

        // change value again
        this.op1.set(true)
        assertEquals(true != true, binding.get())
        this.observer.check(binding, 1)
        binding.dispose()
    }

    @Test
    fun testNotEqual_WeakReference() {
        val op1 = ObservableBooleanValueMock()
        val op2 = ObservableBooleanValueMock()
        var binding: BooleanBinding? = Bindings.notEqual(op1, op2)
        assertNotNull(op1.listener)
        assertNotNull(op2.listener)
        binding = null
        System.gc()
        op1.fireInvalidationEvent()
        assertNull(op1.listener)
        assertNotNull(op2.listener)
        op2.fireInvalidationEvent()
        assertNull(op1.listener)
        assertNull(op2.listener)
    }

    @Test
    fun testDefaultDependencies() {
        assertTrue(BooleanBindingMock().dependencies.isEmpty())
    }

    private class BooleanBindingMock : BooleanBinding() {

        override fun computeValue(): Boolean {
            return false
        }

    }

    private class ObservableBooleanValueMock : ObservableBooleanValue {

        var listener: InvalidationListener? = null

        override fun get(): Boolean {
            return false
        }

        override val value: Boolean
            get() = false

        fun fireInvalidationEvent() {
            val curListener = this.listener
            if (curListener != null) {
                curListener.invalidated(this)
            } else {
                fail("Attempt to fire an event with no listener attached")
            }
        }

        override fun addListener(listener: InvalidationListener) {
            if (isInvalidationListenerAlreadyAdded(listener)) {
                fail("More than one listener set in mock.")
            }
            this.listener = listener
        }

        override fun removeListener(listener: InvalidationListener) {
            if (!isInvalidationListenerAlreadyAdded(listener)) {
                fail("Attempt to remove unknown listener")
            }
            this.listener = null
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return this.listener == listener
        }

        override fun addListener(listener: ChangeListener<in Boolean?>) {
            // not used
        }

        override fun removeListener(listener: ChangeListener<in Boolean?>) {
            // not used
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Boolean?>): Boolean {
            // not used
            return false
        }

    }

}