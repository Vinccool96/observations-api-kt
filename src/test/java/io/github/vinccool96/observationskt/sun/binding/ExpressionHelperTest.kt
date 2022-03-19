package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.WeakInvalidationListenerMock
import io.github.vinccool96.observationskt.beans.value.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import java.lang.Thread.UncaughtExceptionHandler
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExpressionHelperTest {

    private var helper: ExpressionHelper<Any>? = null

    private lateinit var observable: ObservableObjectValueStub<Any>

    private lateinit var invalidationListeners: Array<InvalidationListenerMock>

    private lateinit var changeListeners: Array<ChangeListenerMock<Any>>

    @BeforeTest
    fun setUp() {
        this.helper = null
        this.observable = ObservableObjectValueStub(DATA_1)
        this.invalidationListeners = arrayOf(InvalidationListenerMock(), InvalidationListenerMock(),
                InvalidationListenerMock(), InvalidationListenerMock())
        this.changeListeners = arrayOf(ChangeListenerMock(UNDEFINED), ChangeListenerMock(UNDEFINED),
                ChangeListenerMock(UNDEFINED), ChangeListenerMock(UNDEFINED))
    }

    @Test
    fun testEmptyHelper() {
        // all of these calls should be no-ops
        ExpressionHelper.removeListener(this.helper, this.invalidationListeners[0])
        ExpressionHelper.removeListener(this.helper, this.changeListeners[0])
        ExpressionHelper.fireValueChangedEvent(this.helper)
    }

    @Test
    fun testSingeInvalidation() {
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[0])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[1])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.invalidationListeners[1].check(null, 0)

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[1])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.changeListeners[1].check(null, UNDEFINED, UNDEFINED, 0)

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[1])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.invalidationListeners[1].check(this.observable, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[1])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.invalidationListeners[1].check(null, 0)

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[1])
        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.changeListeners[1].check(this.observable, DATA_1, DATA_2, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[1])
        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.changeListeners[1].check(null, UNDEFINED, UNDEFINED, 0)

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[0])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(null, 0)
    }

    @Test
    fun testSingeChange() {
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[0])
        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_1, DATA_2, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[1])
        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_2, DATA_1, 1)
        this.invalidationListeners[1].check(null, 0)

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[1])
        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[1].check(null, UNDEFINED, UNDEFINED, 0)

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[1])
        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_2, DATA_1, 1)
        this.invalidationListeners[1].check(this.observable, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[1])
        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_1, DATA_2, 1)
        this.invalidationListeners[1].check(null, 0)

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[1])
        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_2, DATA_1, 1)
        this.changeListeners[1].check(this.observable, DATA_2, DATA_1, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[1])
        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[1].check(null, UNDEFINED, UNDEFINED, 0)

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[0])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)
    }

    @Test
    fun testAddInvalidation() {
        val weakListener: InvalidationListener = WeakInvalidationListenerMock()

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[0])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[1])

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, weakListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[0])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, weakListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[1])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.invalidationListeners[1].check(this.observable, 1)

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, weakListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[2])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.invalidationListeners[1].check(this.observable, 1)
        this.invalidationListeners[2].check(this.observable, 1)
    }

    @Test
    fun testRemoveInvalidation() {
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[0])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[1])

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[1])

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[0])

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[1])

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[1])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[2])

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[0])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(null, 0)
        this.invalidationListeners[1].check(this.observable, 1)
        this.invalidationListeners[2].check(this.observable, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[1])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(null, 0)
        this.invalidationListeners[1].check(null, 0)
        this.invalidationListeners[2].check(this.observable, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.invalidationListeners[2])
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(null, 0)
        this.invalidationListeners[1].check(null, 0)
        this.invalidationListeners[2].check(null, 0)
    }

    @Test
    fun testAddInvalidationWhileLocked() {
        val addingListener: ChangeListener<Any> = object : ChangeListener<Any> {

            var index = 0

            override fun changed(observable: ObservableValue<out Any>, oldValue: Any, newValue: Any) {
                if (this.index < this@ExpressionHelperTest.invalidationListeners.size) {
                    this@ExpressionHelperTest.helper = ExpressionHelper.addListener(this@ExpressionHelperTest.helper,
                            this@ExpressionHelperTest.observable,
                            this@ExpressionHelperTest.invalidationListeners[this.index++])
                }
            }

        }
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, addingListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[0])

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].reset()

        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.invalidationListeners[1].reset()

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.invalidationListeners[1].check(this.observable, 1)
        this.invalidationListeners[2].reset()

        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.invalidationListeners[1].check(this.observable, 1)
        this.invalidationListeners[2].check(this.observable, 1)
        this.invalidationListeners[3].reset()

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.invalidationListeners[1].check(this.observable, 1)
        this.invalidationListeners[2].check(this.observable, 1)
        this.invalidationListeners[3].check(this.observable, 1)
    }

    @Test
    fun testRemoveInvalidationWhileLocked() {
        val removingListener: ChangeListener<Any> = object : ChangeListener<Any> {

            var index = 0

            override fun changed(observable: ObservableValue<out Any>, oldValue: Any, newValue: Any) {
                if (this.index < this@ExpressionHelperTest.invalidationListeners.size) {
                    this@ExpressionHelperTest.helper = ExpressionHelper.removeListener(this@ExpressionHelperTest.helper,
                            this@ExpressionHelperTest.invalidationListeners[this.index++])
                }
            }

        }
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, removingListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[0])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[0])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[1])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[2])

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].reset()
        this.invalidationListeners[1].check(this.observable, 1)
        this.invalidationListeners[2].check(this.observable, 1)

        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(null, 0)
        this.invalidationListeners[1].reset()
        this.invalidationListeners[2].check(this.observable, 1)

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(null, 0)
        this.invalidationListeners[1].check(null, 0)
        this.invalidationListeners[2].reset()

        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(null, 0)
        this.invalidationListeners[1].check(null, 0)
        this.invalidationListeners[2].check(null, 0)
    }

    @Test
    fun testAddChange() {
        val weakListener: ChangeListener<Any> = WeakChangeListenerMock()

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[0])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[1])

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, weakListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[0])
        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_1, DATA_2, 1)

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, weakListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[1])
        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_2, DATA_1, 1)
        this.changeListeners[1].check(this.observable, DATA_2, DATA_1, 1)

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, weakListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[2])
        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[1].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[2].check(this.observable, DATA_1, DATA_2, 1)
    }

    @Test
    fun testRemoveChange() {
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[0])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[1])

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[1])

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[0])

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[1])

        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[1])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[2])

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[0])
        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[1].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[2].check(this.observable, DATA_1, DATA_2, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[1])
        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[1].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[2].check(this.observable, DATA_2, DATA_1, 1)

        this.helper = ExpressionHelper.removeListener(this.helper, this.changeListeners[2])
        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[1].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[2].check(null, UNDEFINED, UNDEFINED, 0)
    }

    @Test
    fun testAddChangeWhileLocked() {
        val addingListener: ChangeListener<Any> = object : ChangeListener<Any> {

            var index = 0

            override fun changed(observable: ObservableValue<out Any>, oldValue: Any, newValue: Any) {
                if (this.index < this@ExpressionHelperTest.changeListeners.size) {
                    this@ExpressionHelperTest.helper = ExpressionHelper.addListener(this@ExpressionHelperTest.helper,
                            this@ExpressionHelperTest.observable,
                            this@ExpressionHelperTest.changeListeners[this.index++])
                }
            }

        }
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, addingListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[0])

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].reset()

        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_2, DATA_1, 1)
        this.changeListeners[1].reset()

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[1].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[2].reset()

        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_2, DATA_1, 1)
        this.changeListeners[1].check(this.observable, DATA_2, DATA_1, 1)
        this.changeListeners[2].check(this.observable, DATA_2, DATA_1, 1)
        this.changeListeners[3].reset()

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[1].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[2].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[3].check(this.observable, DATA_1, DATA_2, 1)
    }

    @Test
    fun testRemoveChangeWhileLocked() {
        val removingListener: ChangeListener<Any> = object : ChangeListener<Any> {

            var index = 0

            override fun changed(observable: ObservableValue<out Any>, oldValue: Any, newValue: Any) {
                if (this.index < this@ExpressionHelperTest.changeListeners.size) {
                    this@ExpressionHelperTest.helper = ExpressionHelper.removeListener(this@ExpressionHelperTest.helper,
                            this@ExpressionHelperTest.changeListeners[this.index++])
                }
            }

        }
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, removingListener)
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[0])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[0])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[1])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[2])

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].reset()
        this.changeListeners[1].check(this.observable, DATA_1, DATA_2, 1)
        this.changeListeners[2].check(this.observable, DATA_1, DATA_2, 1)

        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[1].reset()
        this.changeListeners[2].check(this.observable, DATA_2, DATA_1, 1)

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[1].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[2].reset()

        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[1].check(null, UNDEFINED, UNDEFINED, 0)
        this.changeListeners[2].check(null, UNDEFINED, UNDEFINED, 0)
    }

    @Test
    fun testFireValueChangedEvent() {
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.invalidationListeners[0])
        this.helper = ExpressionHelper.addListener(this.helper, this.observable, this.changeListeners[0])

        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.changeListeners[0].check(this.observable, DATA_1, DATA_2, 1)

        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)

        this.observable.set(DATA_1)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.changeListeners[0].check(this.observable, DATA_2, DATA_1, 1)

        ExpressionHelper.fireValueChangedEvent(this.helper)
        this.invalidationListeners[0].check(this.observable, 1)
        this.changeListeners[0].check(null, UNDEFINED, UNDEFINED, 0)
    }

    @Test
    fun testExceptionNotPropagatedFromSingleInvalidation() {
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ -> throw RuntimeException()}

        ExpressionHelper.fireValueChangedEvent(this.helper)
    }

    @Test
    fun testExceptionNotPropagatedFromMultipleInvalidation() {
        val called = BitSet()

        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ ->
            called.set(0)
            throw RuntimeException()
        }
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ ->
            called.set(1)
            throw RuntimeException()
        }

        ExpressionHelper.fireValueChangedEvent(this.helper)
        assertTrue(called[0])
        assertTrue(called[1])
    }

    @Test
    fun testExceptionNotPropagatedFromSingleChange() {
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ -> throw RuntimeException()}

        ExpressionHelper.fireValueChangedEvent(this.helper)
    }

    @Test
    fun testExceptionNotPropagatedFromMultipleChange() {
        val called = BitSet()

        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ ->
            called.set(0)
            throw RuntimeException()
        }
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ ->
            called.set(1)
            throw RuntimeException()
        }

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        assertTrue(called[0])
        assertTrue(called[1])
    }

    @Test
    fun testExceptionNotPropagatedFromMultipleChangeAndInvalidation() {
        val called = BitSet()

        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ ->
            called.set(0)
            throw RuntimeException()
        }
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ ->
            called.set(1)
            throw RuntimeException()
        }
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ ->
            called.set(2)
            throw RuntimeException()
        }
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ ->
            called.set(3)
            throw RuntimeException()
        }

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        assertTrue(called[0])
        assertTrue(called[1])
        assertTrue(called[2])
        assertTrue(called[3])
    }

    @Test
    fun testExceptionHandledByThreadUncaughtHandlerInSingleInvalidation() {
        val called = AtomicBoolean()

        Thread.currentThread().uncaughtExceptionHandler = UncaughtExceptionHandler {_, _ -> called.set(true)}

        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ -> throw RuntimeException()}

        ExpressionHelper.fireValueChangedEvent(this.helper)
        assertTrue(called.get())
    }

    @Test
    fun testExceptionHandledByThreadUncaughtHandlerInMultipleInvalidation() {
        val called = AtomicInteger()

        Thread.currentThread().uncaughtExceptionHandler = UncaughtExceptionHandler {_, _ -> called.incrementAndGet()}

        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ -> throw RuntimeException()}
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ -> throw RuntimeException()}

        ExpressionHelper.fireValueChangedEvent(this.helper)
        assertEquals(2, called.get())
    }

    @Test
    fun testExceptionHandledByThreadUncaughtHandlerInSingleChange() {
        val called = AtomicBoolean()

        Thread.currentThread().uncaughtExceptionHandler = UncaughtExceptionHandler {_, _ -> called.set(true)}

        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ -> throw RuntimeException()}

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        assertTrue(called.get())
    }

    @Test
    fun testExceptionHandledByThreadUncaughtHandlerInMultipleChange() {
        val called = AtomicInteger()

        Thread.currentThread().uncaughtExceptionHandler = UncaughtExceptionHandler {_, _ -> called.incrementAndGet()}

        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ -> throw RuntimeException()}
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ -> throw RuntimeException()}

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        assertEquals(2, called.get())
    }

    @Test
    fun testExceptionHandledByThreadUncaughtHandlerInMultipleChangeAndInvalidation() {
        val called = AtomicInteger()

        Thread.currentThread().uncaughtExceptionHandler = UncaughtExceptionHandler {_, _ -> called.incrementAndGet()}

        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ -> throw RuntimeException()}
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_, _, _ -> throw RuntimeException()}
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ -> throw RuntimeException()}
        this.helper = ExpressionHelper.addListener(this.helper, this.observable) {_ -> throw RuntimeException()}

        this.observable.set(DATA_2)
        ExpressionHelper.fireValueChangedEvent(this.helper)
        assertEquals(4, called.get())
    }

    companion object {

        private val UNDEFINED: Any = Any()

        private val DATA_1: Any = Any()

        private val DATA_2: Any = Any()

    }

}