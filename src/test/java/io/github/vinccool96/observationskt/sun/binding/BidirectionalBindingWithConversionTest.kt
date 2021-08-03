package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.property.Property
import io.github.vinccool96.observationskt.beans.property.SimpleObjectProperty
import io.github.vinccool96.observationskt.beans.property.SimpleStringProperty
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.util.StringConverter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.text.DateFormat
import java.util.*
import kotlin.test.assertEquals

@Suppress("UNUSED_VALUE")
@RunWith(Parameterized::class)
class BidirectionalBindingWithConversionTest<S, T>(private val func: Functions<S, T>, private val v0: Array<S>,
        private val v1: Array<T>) {

    interface Functions<U, V> {

        fun create0(): PropertyMock<U>

        fun create1(): PropertyMock<V>

        fun bind(obj0: PropertyMock<U>, obj1: PropertyMock<V>)

        fun unbind(obj0: Any, obj1: Any)

        fun check0(obj0: U, obj1: U)

        fun check1(obj0: V, obj1: V)

    }

    interface PropertyMock<T> : Property<T> {

        val listenerCount: Int

    }

    private val op0: PropertyMock<S> = this.func.create0()

    private val op1: PropertyMock<T> = this.func.create1()

    @Before
    fun setUp() {
        this.op0.value = this.v0[0]
        this.op1.value = this.v1[1]
    }

    @Test
    fun testBind() {
        this.func.bind(this.op0, this.op1)
        System.gc() // making sure we did not overdo weak references
        this.func.check0(this.v0[1], this.op0.value)
        this.func.check1(this.v1[1], this.op1.value)

        this.op0.value = this.v0[2]
        this.func.check0(this.v0[2], this.op0.value)
        this.func.check1(this.v1[2], this.op1.value)

        this.op1.value = this.v1[3]
        this.func.check0(this.v0[3], this.op0.value)
        this.func.check1(this.v1[3], this.op1.value)
    }

    @Test
    fun testUnbind() {
        // unbind non-existing binding => no-op
        this.func.unbind(this.op0, this.op1)

        // unbind properties of different beans
        this.func.bind(this.op0, this.op1)
        System.gc() // making sure we did not overdo weak references
        this.func.check0(this.v0[1], this.op0.value)
        this.func.check1(this.v1[1], this.op1.value)

        this.func.unbind(this.op0, this.op1)
        System.gc()
        this.op0.value = this.v0[2]
        this.func.check0(this.v0[2], this.op0.value)
        this.func.check1(this.v1[1], this.op1.value)

        this.op1.value = this.v1[3]
        this.func.check0(this.v0[2], this.op0.value)
        this.func.check1(this.v1[3], this.op1.value)
    }

    @Test
    fun testWeakReferencing() {
        var p0: PropertyMock<S>? = this.func.create0()
        var p1: PropertyMock<T>? = this.func.create1()
        p0!!.value = this.v0[0]
        p1!!.value = this.v1[1]

        this.func.bind(p0, p1)
        assertEquals(1, p0.listenerCount)
        assertEquals(1, p1.listenerCount)

        p0 = null
        System.gc()
        p1.value = this.v1[2]
        assertEquals(0, p1.listenerCount)

        p0 = this.func.create0()
        p0.value = this.v0[0]
        this.func.bind(p0, p1)
        assertEquals(1, p0.listenerCount)
        assertEquals(1, p1.listenerCount)

        p1 = null
        System.gc()
        p0.value = this.v0[3]
        assertEquals(0, p0.listenerCount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUnbind_X_Self() {
        this.func.unbind(this.op0, this.op0)
    }

    private class ObjectPropertyMock<T>(initialValue: T) : SimpleObjectProperty<T>(initialValue), PropertyMock<T> {

        private var count: Int = 0

        override val listenerCount: Int
            get() = this.count

        override fun addListener(listener: ChangeListener<in T>) {
            super.addListener(listener)
            this.count++
        }

        override fun removeListener(listener: ChangeListener<in T>) {
            super.removeListener(listener)
            this.count--
        }

    }

    private class StringPropertyMock : SimpleStringProperty(), PropertyMock<String?> {

        private var count: Int = 0

        override val listenerCount: Int
            get() = this.count

        override fun addListener(listener: ChangeListener<in String?>) {
            super.addListener(listener)
            this.count++
        }

        override fun removeListener(listener: ChangeListener<in String?>) {
            super.removeListener(listener)
            this.count--
        }

    }

    companion object {

        @Parameterized.Parameters
        @JvmStatic
        fun parameters(): Collection<Array<Any>> {
            val format: DateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US)
            val dates: Array<Date> = arrayOf(Date(), Date(0), Date(Int.MIN_VALUE.toLong()), Date(Long.MAX_VALUE))
            val strings: Array<String> = Array(dates.size) {i: Int -> format.format(dates[i])}

            val converter: StringConverter<Date> = object : StringConverter<Date>() {

                override fun toString(value: Date): String {
                    return format.format(value)
                }

                override fun fromString(string: String): Date {
                    return format.parse(string)
                }

            }

            return listOf(
                    // Format
                    arrayOf(
                            object : Functions<String?, Date> {

                                override fun create0(): PropertyMock<String?> {
                                    return StringPropertyMock()
                                }

                                override fun create1(): PropertyMock<Date> {
                                    return ObjectPropertyMock(Date())
                                }

                                override fun bind(obj0: PropertyMock<String?>, obj1: PropertyMock<Date>) {
                                    Bindings.bindBidirectional(obj0, obj1, format)
                                }

                                override fun unbind(obj0: Any, obj1: Any) {
                                    Bindings.unbindBidirectional(obj0, obj1)
                                }

                                override fun check0(obj0: String?, obj1: String?) {
                                    assertEquals(obj0, obj1)
                                }

                                override fun check1(obj0: Date, obj1: Date) {
                                    assertEquals(obj0.toString(), obj1.toString())
                                }

                            },
                            strings, dates
                    ),
                    // Converter
                    arrayOf(
                            object : Functions<String?, Date> {

                                override fun create0(): PropertyMock<String?> {
                                    return StringPropertyMock()
                                }

                                override fun create1(): PropertyMock<Date> {
                                    return ObjectPropertyMock(Date())
                                }

                                override fun bind(obj0: PropertyMock<String?>, obj1: PropertyMock<Date>) {
                                    Bindings.bindBidirectional(obj0, obj1, converter)
                                }

                                override fun unbind(obj0: Any, obj1: Any) {
                                    Bindings.unbindBidirectional(obj0, obj1)
                                }

                                override fun check0(obj0: String?, obj1: String?) {
                                    assertEquals(obj0, obj1)
                                }

                                override fun check1(obj0: Date, obj1: Date) {
                                    assertEquals(obj0.toString(), obj1.toString())
                                }

                            },
                            strings, dates
                    )
            )
        }

    }

}