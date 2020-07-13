package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.property.*
import io.github.vinccool96.observationskt.sun.binding.ErrorLoggingUtility
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.Callable
import java.util.logging.Level
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
@Suppress("UNCHECKED_CAST")
class BindingsCreateBindingTest<T>(private val p0: Property<T>, private val p1: Property<T>,
        private val f: Functions<T>, private val value0: T, private val value1: T, private val defaultValue: T) {

    interface Functions<S> {

        fun create(func: Callable<S>, vararg dependencies: Observable): Binding<S>

        fun check(value0: S, value1: Any?)

    }

    @Test
    fun testNoDependencies() {
        val func0: Callable<T> = Callable {this.value0}
        val binding0: Binding<T> = this.f.create(func0)

        this.f.check(this.value0, binding0.value)
        assertTrue(binding0.dependencies.isEmpty())
        binding0.dispose()

        // func throws exception, dependencies set to empty array
        val func1: Callable<T> = Callable {throw Exception()}
        val binding1: Binding<T> = this.f.create(func1)

        this.f.check(this.defaultValue, if (binding1 !is ObjectBinding) binding1.value else binding1.get())
        log.check(Level.WARNING, Exception::class.java)
        assertTrue(binding1.dependencies.isEmpty())
        binding1.dispose()
    }

    @Test
    fun testOneDependency() {
        val func: Callable<T> = Callable {this.p0.value}
        val binding = this.f.create(func, this.p0)

        this.f.check(this.p0.value, if (binding !is ObjectBinding) binding.value else binding.get())
        assertEquals<List<*>>(binding.dependencies, listOf(this.p0))
        this.p0.value = this.value1
        this.f.check(this.p0.value, if (binding !is ObjectBinding) binding.value else binding.get())
        binding.dispose()
    }

    @Test
    fun testCreateBoolean_TwoDependencies() {
        val func: Callable<T> = Callable {this.p0.value}
        val binding = this.f.create(func, this.p0, this.p1)

        this.f.check(this.p0.value, if (binding !is ObjectBinding) binding.value else binding.get())
        assertTrue(binding.dependencies == listOf(this.p0, this.p1) || binding.dependencies == listOf(this.p1, this.p0))
        this.p0.value = this.value1
        this.f.check(this.p0.value, if (binding !is ObjectBinding) binding.value else binding.get())
        binding.dispose()
    }

    companion object {

        private const val EPSILON_FLOAT: Float = 1e-5f

        private const val EPSILON_DOUBLE: Double = 1e-10

        private val log = ErrorLoggingUtility()

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            log.start()
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            log.stop()
        }

        @Parameterized.Parameters
        @JvmStatic
        fun parameters(): List<Array<out Any?>> {
            return listOf(arrayOf(
                    SimpleBooleanProperty(), SimpleBooleanProperty(),
                    object : Functions<Boolean?> {

                        override fun create(func: Callable<Boolean?>,
                                vararg dependencies: Observable): Binding<Boolean?> {
                            return Bindings.createBooleanBinding(func as Callable<Boolean>, *dependencies)
                        }

                        override fun check(value0: Boolean?, value1: Any?) {
                            assertEquals(value0, value1)
                        }

                    }, true, false, false
            ), arrayOf(
                    SimpleDoubleProperty(), SimpleDoubleProperty(),
                    object : Functions<Number?> {

                        override fun create(func: Callable<Number?>,
                                vararg dependencies: Observable): Binding<Number?> {
                            return Bindings.createDoubleBinding(func as Callable<Double>, *dependencies)
                        }

                        override fun check(value0: Number?, value1: Any?) {
                            org.junit.Assert.assertEquals(value0!!.toDouble(), (value1 as Number).toDouble(),
                                    EPSILON_DOUBLE)
                        }

                    }, PI, -E, 0.0
            ), arrayOf(
                    SimpleFloatProperty(), SimpleFloatProperty(),
                    object : Functions<Number?> {

                        override fun create(func: Callable<Number?>,
                                vararg dependencies: Observable): Binding<Number?> {
                            return Bindings.createFloatBinding(func as Callable<Float>, *dependencies)
                        }

                        override fun check(value0: Number?, value1: Any?) {
                            org.junit.Assert.assertEquals(value0!!.toFloat(), (value1 as Number).toFloat(),
                                    EPSILON_FLOAT)
                        }

                    }, PI.toFloat(), -E.toFloat(), 0.0f
            ), arrayOf(
                    SimpleIntProperty(), SimpleIntProperty(),
                    object : Functions<Number?> {

                        override fun create(func: Callable<Number?>,
                                vararg dependencies: Observable): Binding<Number?> {
                            return Bindings.createIntBinding(func as Callable<Int>, *dependencies)
                        }

                        override fun check(value0: Number?, value1: Any?) {
                            assertEquals(value0!!.toInt(), (value1 as Number).toInt())
                        }

                    }, Int.MAX_VALUE, Int.MIN_VALUE, 0
            ), arrayOf(
                    SimpleLongProperty(), SimpleLongProperty(),
                    object : Functions<Number?> {

                        override fun create(func: Callable<Number?>,
                                vararg dependencies: Observable): Binding<Number?> {
                            return Bindings.createLongBinding(func as Callable<Long>, *dependencies)
                        }

                        override fun check(value0: Number?, value1: Any?) {
                            assertEquals(value0!!.toLong(), (value1 as Number).toLong())
                        }

                    }, Long.MAX_VALUE, Long.MIN_VALUE, 0L
            ), arrayOf(
                    SimpleObjectProperty<Any?>(null), SimpleObjectProperty<Any?>(null),
                    object : Functions<Any?> {

                        override fun create(func: Callable<Any?>, vararg dependencies: Observable): Binding<Any?> {
                            return Bindings.createObjectBinding(func, null, *dependencies)
                        }

                        override fun check(value0: Any?, value1: Any?) {
                            assertEquals(value0, value1)
                        }

                    }, Any(), Any(), null
            ), arrayOf(
                    SimpleStringProperty(), SimpleStringProperty(),
                    object : Functions<String?> {

                        override fun create(func: Callable<String?>,
                                vararg dependencies: Observable): Binding<String?> {
                            return Bindings.createStringBinding(func, *dependencies)
                        }

                        override fun check(value0: String?, value1: Any?) {
                            assertEquals(value0, value1)
                        }

                    }, "Hello World", "Goodbye World", ""
            )
            )
        }

    }

}