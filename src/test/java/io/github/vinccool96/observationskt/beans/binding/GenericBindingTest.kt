package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.value.ChangeListenerMock
import io.github.vinccool96.observationskt.beans.value.ObservableValueBase
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(Parameterized::class)
class GenericBindingTest<T>(private val value1: T, private val value2: T, private val dependency1: ObservableStub,
        private val dependency2: ObservableStub, private val binding0: BindingMock<T>,
        private val binding1: BindingMock<T>, private val binding2: BindingMock<T>) {

    private lateinit var invalidationListener: InvalidationListenerMock

    private lateinit var changeListener: ChangeListenerMock<Any?>

    @Before
    fun setUp() {
        this.invalidationListener = InvalidationListenerMock()
        this.changeListener = ChangeListenerMock(UNDEFINED)
        this.binding0.value = this.value2
        this.binding1.value = this.value2
        this.binding2.value = this.value2
    }

    @After
    fun tearDown() {
        this.binding0.removeListener(this.invalidationListener)
        this.binding0.removeListener(this.changeListener)
        this.binding1.removeListener(this.invalidationListener)
        this.binding1.removeListener(this.changeListener)
        this.binding2.removeListener(this.invalidationListener)
        this.binding2.removeListener(this.changeListener)
    }

    @Test
    fun testNoDependencyLazy() {
        this.binding0.value
        this.binding0.addListener(this.invalidationListener)
        System.gc() // making sure we did not overdo weak references
        assertEquals(true, this.binding0.valid)

        // calling value
        this.binding0.reset()
        this.binding0.value
        assertEquals(0, this.binding0.computeValueCounter)
        this.invalidationListener.check(null, 0)
        assertEquals(true, this.binding0.valid)
    }

    @Test
    fun testNoDependencyEager() {
        this.binding0.value
        this.binding0.addListener(this.changeListener)
        System.gc() // making sure we did not overdo weak references

        assertEquals(true, this.binding0.valid)

        // calling value
        this.binding0.reset()
        this.binding0.value
        assertEquals(0, this.binding0.computeValueCounter)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
        assertEquals(true, this.binding0.valid)
    }

    @Test
    fun testSingleDependencyLazy() {
        this.binding1.value
        this.binding1.addListener(this.invalidationListener)
        System.gc() // making sure we did not overdo weak references

        assertEquals(true, this.binding1.valid)

        // fire single change event
        this.binding1.reset()
        this.invalidationListener.reset()
        this.binding1.value = this.value1
        this.dependency1.fireValueChangedEvent()
        assertEquals(0, this.binding1.computeValueCounter)
        this.invalidationListener.check(this.binding1, 1)
        assertEquals(false, this.binding1.valid)

        this.binding1.value
        assertEquals(1, this.binding1.computeValueCounter)
        this.invalidationListener.check(null, 0)
        assertEquals(true, this.binding1.valid)

        // fire single change event with same value
        this.binding1.value = this.value1
        this.dependency1.fireValueChangedEvent()
        assertEquals(0, this.binding1.computeValueCounter)
        this.invalidationListener.check(this.binding1, 1)
        assertEquals(false, this.binding1.valid)

        this.binding1.value
        assertEquals(1, this.binding1.computeValueCounter)
        this.invalidationListener.check(null, 0)
        assertEquals(true, this.binding1.valid)

        // fire two change events with different values
        this.binding1.value = this.value2
        this.dependency1.fireValueChangedEvent()
        this.binding1.value = this.value1
        this.dependency1.fireValueChangedEvent()
        assertEquals(0, this.binding1.computeValueCounter)
        this.invalidationListener.check(this.binding1, 1)
        assertEquals(false, this.binding1.valid)

        this.binding1.value
        assertEquals(1, this.binding1.computeValueCounter)
        this.invalidationListener.check(null, 0)
        assertEquals(true, this.binding1.valid)

        // fire two change events with same values
        this.binding1.value = this.value2
        this.dependency1.fireValueChangedEvent()
        this.binding1.value = value2
        this.dependency1.fireValueChangedEvent()
        assertEquals(0, this.binding1.computeValueCounter)
        this.invalidationListener.check(this.binding1, 1)
        assertEquals(false, this.binding1.valid)

        this.binding1.value
        assertEquals(1, this.binding1.computeValueCounter)
        this.invalidationListener.check(null, 0)
        assertEquals(true, this.binding1.valid)
    }

    @Test
    fun testSingleDependencyEager() {
        this.binding1.value
        this.binding1.addListener(this.changeListener)
        System.gc() // making sure we did not overdo weak references

        assertEquals(true, this.binding1.valid)

        // fire single change event
        this.binding1.reset()
        this.changeListener.reset()
        this.binding1.value = this.value1
        this.dependency1.fireValueChangedEvent()
        assertEquals(1, this.binding1.computeValueCounter)
        this.changeListener.check(this.binding1, this.value2, this.value1, 1)
        assertEquals(true, this.binding1.valid)

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
        assertEquals(true, this.binding1.valid)

        // fire single change event with same value
        this.binding1.value = this.value1
        this.dependency1.fireValueChangedEvent()
        assertEquals(1, this.binding1.computeValueCounter)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
        assertEquals(true, this.binding1.valid)

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
        assertEquals(true, this.binding1.valid)

        // fire two change events
        this.binding1.value = this.value2
        this.dependency1.fireValueChangedEvent()
        this.binding1.value = this.value1
        this.dependency1.fireValueChangedEvent()
        assertEquals(2, this.binding1.computeValueCounter)
        this.changeListener.check(this.binding1, this.value2, this.value1, 2)
        assertEquals(true, this.binding1.valid)

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
        assertEquals(true, this.binding1.valid)

        // fire two change events with same value
        this.binding1.value = this.value2
        this.dependency1.fireValueChangedEvent()
        this.binding1.value = this.value2
        this.dependency1.fireValueChangedEvent()
        assertEquals(2, this.binding1.computeValueCounter)
        this.changeListener.check(this.binding1, this.value1, this.value2, 1)
        assertEquals(true, this.binding1.valid)

        this.binding1.value
        assertEquals(0, this.binding1.computeValueCounter)
        this.changeListener.check(null, UNDEFINED, UNDEFINED, 0)
        assertEquals(true, this.binding1.valid)
    }

    @Test
    fun testTwoDependencies() {
        this.binding2.value
        this.binding2.addListener(this.invalidationListener)
        System.gc() // making sure we did not overdo weak references

        assertEquals(true, this.binding2.valid)

        // fire single change event on first dependency

        // fire single change event on first dependency
        this.binding2.reset()
        this.invalidationListener.reset()
        this.dependency1.fireValueChangedEvent()
        Assert.assertEquals(0, this.binding2.computeValueCounter)
        this.invalidationListener.check(this.binding2, 1)
        assertEquals(false, this.binding2.valid)

        this.binding2.value
        Assert.assertEquals(1, this.binding2.computeValueCounter)
        this.invalidationListener.check(null, 0)
        assertEquals(true, this.binding2.valid)

        // fire single change event on second dependency

        // fire single change event on second dependency
        this.binding2.reset()
        this.dependency2.fireValueChangedEvent()
        Assert.assertEquals(0, this.binding2.computeValueCounter)
        this.invalidationListener.check(this.binding2, 1)
        assertEquals(false, this.binding2.valid)

        this.binding2.value
        Assert.assertEquals(1, this.binding2.computeValueCounter)
        this.invalidationListener.check(null, 0)
        assertEquals(true, this.binding2.valid)

        // fire change events on each dependency

        // fire change events on each dependency
        this.binding2.reset()
        this.dependency1.fireValueChangedEvent()
        this.dependency2.fireValueChangedEvent()
        Assert.assertEquals(0, this.binding2.computeValueCounter)
        this.invalidationListener.check(this.binding2, 1)
        assertEquals(false, this.binding2.valid)

        this.binding2.value
        Assert.assertEquals(1, this.binding2.computeValueCounter)
        this.invalidationListener.check(null, 0)
        assertEquals(true, this.binding2.valid)
    }

    class ObservableStub : ObservableValueBase<Any?>() {

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

        override val value: Any?
            get() = null

    }

    interface BindingMock<T> : Binding<T> {

        val computeValueCounter: Int

        fun reset()

        override var value: T

    }

    class DoubleBindingImpl(vararg deps: Observable) : DoubleBinding(), BindingMock<Number?> {

        private var computeValueCounterState = 0

        private var valueState = 0.0

        init {
            super.bind(*deps)
        }

        override var value: Number?
            get() = this.get()
            set(value) {
                this.valueState = value?.toDouble() ?: 0.0
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): Double {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class FloatBindingImpl(vararg deps: Observable) : FloatBinding(), BindingMock<Number?> {

        private var computeValueCounterState = 0

        private var valueState = 0.0f

        init {
            super.bind(*deps)
        }

        override var value: Number?
            get() = this.get()
            set(value) {
                this.valueState = value?.toFloat() ?: 0.0f
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): Float {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class LongBindingImpl(vararg deps: Observable) : LongBinding(), BindingMock<Number?> {

        private var computeValueCounterState = 0

        private var valueState = 0L

        init {
            super.bind(*deps)
        }

        override var value: Number?
            get() = this.get()
            set(value) {
                this.valueState = value?.toLong() ?: 0L
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): Long {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class IntBindingImpl(vararg deps: Observable) : IntBinding(), BindingMock<Number?> {

        private var computeValueCounterState = 0

        private var valueState = 0

        init {
            super.bind(*deps)
        }

        override var value: Number?
            get() = this.get()
            set(value) {
                this.valueState = value?.toInt() ?: 0
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): Int {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class ShortBindingImpl(vararg deps: Observable) : ShortBinding(), BindingMock<Number?> {

        private var computeValueCounterState = 0

        private var valueState: Short = 0

        init {
            super.bind(*deps)
        }

        override var value: Number?
            get() = this.get()
            set(value) {
                this.valueState = value?.toShort() ?: 0
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): Short {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class BooleanBindingImpl(vararg deps: Observable) : BooleanBinding(), BindingMock<Boolean?> {

        private var computeValueCounterState = 0

        private var valueState = false

        init {
            super.bind(*deps)
        }

        override var value: Boolean?
            get() = this.get()
            set(value) {
                this.valueState = value ?: false
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): Boolean {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class ObjectBindingImpl(vararg deps: Observable) : ObjectBinding<Any?>(), BindingMock<Any?> {

        private var computeValueCounterState = 0

        private var valueState: Any? = null

        init {
            super.bind(*deps)
        }

        override var value: Any?
            get() = this.get()
            set(value) {
                this.valueState = value
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): Any? {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class StringBindingImpl(vararg deps: Observable) : StringBinding(), BindingMock<String?> {

        private var computeValueCounterState = 0

        private var valueState: String? = null

        init {
            super.bind(*deps)
        }

        override var value: String?
            get() = this.get()
            set(value) {
                this.valueState = value
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): String? {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class ListBindingImpl(vararg deps: Observable) : ListBinding<Any>(), BindingMock<ObservableList<Any>?> {

        private var computeValueCounterState = 0

        private var valueState: ObservableList<Any>? = null

        init {
            super.bind(*deps)
        }

        override var value: ObservableList<Any>?
            get() = this.get()
            set(value) {
                this.valueState = value
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): ObservableList<Any>? {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class MapBindingImpl(vararg deps: Observable) : MapBinding<Any, Any>(), BindingMock<ObservableMap<Any, Any>?> {

        private var computeValueCounterState = 0

        private var valueState: ObservableMap<Any, Any>? = null

        init {
            super.bind(*deps)
        }

        override var value: ObservableMap<Any, Any>?
            get() = this.get()
            set(value) {
                this.valueState = value
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): ObservableMap<Any, Any>? {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    class SetBindingImpl(vararg deps: Observable) : SetBinding<Any>(), BindingMock<ObservableSet<Any>?> {

        private var computeValueCounterState = 0

        private var valueState: ObservableSet<Any>? = null

        init {
            super.bind(*deps)
        }

        override var value: ObservableSet<Any>?
            get() = this.get()
            set(value) {
                this.valueState = value
            }

        override val computeValueCounter: Int
            get() {
                val result = this.computeValueCounterState
                reset()
                return result
            }

        override fun reset() {
            this.computeValueCounterState = 0
        }

        override fun computeValue(): ObservableSet<Any>? {
            this.computeValueCounterState++
            return this.valueState
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = fail("Should not reach here")

    }

    companion object {

        private val UNDEFINED: Any? = null

        @Parameterized.Parameters
        @JvmStatic
        fun parameters(): Collection<Array<Any>> {
            val dependency1 = ObservableStub()
            val dependency2 = ObservableStub()
            return listOf(
                    arrayOf(
                            Double.MIN_VALUE, Double.MAX_VALUE,
                            dependency1, dependency2,
                            DoubleBindingImpl(),
                            DoubleBindingImpl(dependency1),
                            DoubleBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            Float.MIN_VALUE, Float.MAX_VALUE,
                            dependency1, dependency2,
                            FloatBindingImpl(),
                            FloatBindingImpl(dependency1),
                            FloatBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            Long.MIN_VALUE, Long.MAX_VALUE,
                            dependency1, dependency2,
                            LongBindingImpl(),
                            LongBindingImpl(dependency1),
                            LongBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            Int.MIN_VALUE, Int.MAX_VALUE,
                            dependency1, dependency2,
                            IntBindingImpl(),
                            IntBindingImpl(dependency1),
                            IntBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            Short.MIN_VALUE, Short.MAX_VALUE,
                            dependency1, dependency2,
                            ShortBindingImpl(),
                            ShortBindingImpl(dependency1),
                            ShortBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            true, false,
                            dependency1, dependency2,
                            BooleanBindingImpl(),
                            BooleanBindingImpl(dependency1),
                            BooleanBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            Any(), Any(),
                            dependency1, dependency2,
                            ObjectBindingImpl(),
                            ObjectBindingImpl(dependency1),
                            ObjectBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            "Hello World", "Goodbye",
                            dependency1, dependency2,
                            StringBindingImpl(),
                            StringBindingImpl(dependency1),
                            StringBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            ObservableCollections.observableArrayList<Any>(),
                            ObservableCollections.observableArrayList<Any>(),
                            dependency1, dependency2,
                            ListBindingImpl(),
                            ListBindingImpl(dependency1),
                            ListBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            ObservableCollections.observableHashMap<Any, Any>(),
                            ObservableCollections.observableHashMap<Any, Any>(),
                            dependency1, dependency2,
                            MapBindingImpl(),
                            MapBindingImpl(dependency1),
                            MapBindingImpl(dependency1, dependency2)
                    ),
                    arrayOf(
                            ObservableCollections.observableSet<Any>(),
                            ObservableCollections.observableSet<Any>(),
                            dependency1, dependency2,
                            SetBindingImpl(),
                            SetBindingImpl(dependency1),
                            SetBindingImpl(dependency1, dependency2)
                    )
            )
        }

    }

}