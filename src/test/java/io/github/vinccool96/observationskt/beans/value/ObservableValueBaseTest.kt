package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.Observable
import org.junit.Before
import org.junit.Test

class ObservableValueBaseTest {

    private lateinit var valueModel: ObservableObjectValueStub<Any?>

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<Any?>

    @Before
    fun setUp() {
        this.valueModel = ObservableObjectValueStub(DEFAULT_VALUE)
        this.invalidationListener = InvalidationListenerMock()
        this.changeListener = ChangeListenerMock(UNDEFINED_VALUE)
    }

    @Test
    fun testInitialState() {
        // no exceptions etc.
        this.valueModel.fireChange()
    }

    @Test
    fun testOneInvalidationListener() {
        // adding one observer
        this.valueModel.addListener(this.invalidationListener)
        System.gc() // making sure we did not not overdo weak references
        this.valueModel.set(V1)
        this.invalidationListener.check(this.valueModel, 1)

        // remove observer
        this.valueModel.removeListener(this.invalidationListener)
        this.valueModel.set(V2)
        this.invalidationListener.check(null, 0)

        // remove observer again
        this.valueModel.removeListener(this.invalidationListener)
        this.valueModel.set(V1)
        this.invalidationListener.check(null, 0)
    }

    @Test
    fun testOneChangeListener() {
        // adding one observer
        this.valueModel.addListener(this.changeListener)
        System.gc() // making sure we did not not overdo weak references
        this.valueModel.set(V1)
        this.changeListener.check(this.valueModel, DEFAULT_VALUE, V1, 1)

        // set same value again
        this.valueModel.set(V1)
        this.changeListener.check(null, UNDEFINED_VALUE, UNDEFINED_VALUE, 0)

        // set null
        this.valueModel.set(null)
        this.changeListener.check(this.valueModel, V1, null, 1)
        this.valueModel.set(null)
        this.changeListener.check(null, UNDEFINED_VALUE, UNDEFINED_VALUE, 0)

        // remove observer
        this.valueModel.removeListener(this.changeListener)
        this.valueModel.set(V2)
        this.changeListener.check(null, UNDEFINED_VALUE, UNDEFINED_VALUE, 0)

        // remove observer again
        this.valueModel.removeListener(this.changeListener)
        this.valueModel.set(V1)
        this.changeListener.check(null, UNDEFINED_VALUE, UNDEFINED_VALUE, 0)
    }

    @Test
    fun testTwoObservers() {
        val observer2 = InvalidationListenerMock()

        // adding two observers
        this.valueModel.addListener(this.invalidationListener)
        this.valueModel.addListener(observer2)
        System.gc() // making sure we did not not overdo weak references
        this.valueModel.fireChange()
        this.invalidationListener.check(this.valueModel, 1)
        observer2.check(this.valueModel, 1)

        // remove first observer
        this.valueModel.removeListener(this.invalidationListener)
        this.valueModel.fireChange()
        this.invalidationListener.check(null, 0)
        observer2.check(this.valueModel, 1)

        // remove second observer
        this.valueModel.removeListener(observer2)
        this.valueModel.fireChange()
        this.invalidationListener.check(null, 0)
        observer2.check(null, 0)

        // remove observers in reverse order
        this.valueModel.removeListener(observer2)
        this.valueModel.removeListener(this.invalidationListener)
        this.valueModel.fireChange()
        this.invalidationListener.check(null, 0)
        observer2.check(null, 0)
    }

    @Test
    fun testConcurrentAdd() {
        val observer2: InvalidationListenerMock = AddingListenerMock()
        this.valueModel.addListener(observer2)

        // fire event that adds a second observer
        // Note: there is no assumption if observer that is being added is notified
        this.valueModel.fireChange()
        observer2.check(this.valueModel, 1)

        // fire event again, this time both observers need to be notified
        this.invalidationListener.reset()
        this.valueModel.fireChange()
        this.invalidationListener.check(this.valueModel, 1)
        observer2.check(this.valueModel, 1)
    }

    @Test
    fun testConcurrentRemove() {
        val observer2: InvalidationListenerMock = RemovingListenerMock()
        this.valueModel.addListener(observer2)
        this.valueModel.addListener(this.invalidationListener)

        // fire event that adds a second observer
        // Note: there is no assumption if observer that is being removed is notified
        this.valueModel.fireChange()
        observer2.check(this.valueModel, 1)

        // fire event again, this time only non-removed observer is notified
        this.invalidationListener.reset()
        this.valueModel.fireChange()
        this.invalidationListener.check(null, 0)
        observer2.check(this.valueModel, 1)
    }

    private inner class AddingListenerMock : InvalidationListenerMock() {

        override fun invalidated(observable: Observable) {
            super.invalidated(observable)
            observable.addListener(this@ObservableValueBaseTest.invalidationListener)
        }

    }

    private inner class RemovingListenerMock : InvalidationListenerMock() {

        override fun invalidated(observable: Observable) {
            super.invalidated(observable)
            observable.removeListener(this@ObservableValueBaseTest.invalidationListener)
        }

    }

    companion object {

        private val UNDEFINED_VALUE: Any = Any()

        private val DEFAULT_VALUE: Any = Any()

        private val V1: Any = Any()

        private val V2: Any = Any()

    }

}