package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.property.*
import io.github.vinccool96.observationskt.collections.*
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class BindingToStringTest {

    @Test
    fun testBooleanToString() {
        val value1 = true
        val value2 = false

        val v: BooleanProperty = SimpleBooleanProperty(value1)
        val binding: BooleanBinding = object : BooleanBinding() {

            init {
                super.bind(v)
            }

            override fun computeValue(): Boolean {
                return v.get()
            }

        }

        assertEquals("BooleanBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("BooleanBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("BooleanBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("BooleanBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testDoubleToString() {
        val value1: Double = PI
        val value2: Double = -E

        val v: DoubleProperty = SimpleDoubleProperty(value1)
        val binding: DoubleBinding = object : DoubleBinding() {

            init {
                super.bind(v)
            }

            override fun computeValue(): Double {
                return v.get()
            }

        }

        assertEquals("DoubleBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("DoubleBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("DoubleBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("DoubleBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testFloatToString() {
        val value1: Float = PI.toFloat()
        val value2: Float = -E.toFloat()

        val v: FloatProperty = SimpleFloatProperty(value1)
        val binding: FloatBinding = object : FloatBinding() {

            init {
                super.bind(v)
            }

            override fun computeValue(): Float {
                return v.get()
            }

        }

        assertEquals("FloatBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("FloatBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("FloatBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("FloatBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testIntToString() {
        val value1 = 42
        val value2 = 987654321

        val v: IntProperty = SimpleIntProperty(value1)
        val binding: IntBinding = object : IntBinding() {

            init {
                super.bind(v)
            }

            override fun computeValue(): Int {
                return v.get()
            }

        }

        assertEquals("IntBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("IntBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("IntBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("IntBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testLongToString() {
        val value1 = -987654321234567890L
        val value2 = 1234567890987654321L

        val v: LongProperty = SimpleLongProperty(value1)
        val binding: LongBinding = object : LongBinding() {

            init {
                super.bind(v)
            }

            override fun computeValue(): Long {
                return v.get()
            }

        }

        assertEquals("LongBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("LongBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("LongBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("LongBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testShortToString() {
        val value1: Short = -9876
        val value2: Short = 12345

        val v: ShortProperty = SimpleShortProperty(value1)
        val binding: ShortBinding = object : ShortBinding() {

            init {
                super.bind(v)
            }

            override fun computeValue(): Short {
                return v.get()
            }

        }

        assertEquals("ShortBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ShortBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("ShortBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ShortBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testByteToString() {
        val value1: Byte = -98
        val value2: Byte = 123

        val v: ByteProperty = SimpleByteProperty(value1)
        val binding: ByteBinding = object : ByteBinding() {

            init {
                super.bind(v)
            }

            override fun computeValue(): Byte {
                return v.get()
            }

        }

        assertEquals("ByteBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ByteBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("ByteBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ByteBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testObjectToString() {
        val value1 = Any()
        val value2 = Any()

        val v: ObjectProperty<Any> = SimpleObjectProperty(value1)
        val binding: ObjectBinding<Any> = object : ObjectBinding<Any>() {

            init {
                super.bind(v)
            }

            override fun computeValue(): Any {
                return v.get()
            }

        }

        assertEquals("ObjectBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ObjectBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("ObjectBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ObjectBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testStringToString() {
        val value1 = "Hello World"
        val value2 = "Goodbye"

        val v: StringProperty = SimpleStringProperty(value1)
        val binding: StringBinding = object : StringBinding() {

            init {
                super.bind(v)
            }

            override fun computeValue(): String? {
                return v.get()
            }

        }

        assertEquals("StringBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("StringBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("StringBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("StringBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testListToString() {
        val value1 = ObservableCollections.observableArrayList(Any())
        val value2 = ObservableCollections.observableArrayList(Any(), Any())
        val v: ListProperty<Any> = SimpleListProperty(value1)
        val binding: ListBinding<Any> = object : ListBinding<Any>() {

            init {
                super.bind(v)
            }

            override fun computeValue(): ObservableList<Any>? {
                return v.get()
            }

        }

        assertEquals("ListBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ListBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("ListBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ListBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testMapToString() {
        val value1 = ObservableCollections.observableHashMap(Any() to Any())
        val value2 = ObservableCollections.observableHashMap(Any() to Any(), Any() to Any())
        val v: MapProperty<Any, Any> = SimpleMapProperty(value1)
        val binding: MapBinding<Any, Any> = object : MapBinding<Any, Any>() {

            init {
                super.bind(v)
            }

            override fun computeValue(): ObservableMap<Any, Any>? {
                return v.get()
            }

        }

        assertEquals("MapBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("MapBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("MapBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("MapBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testSetToString() {
        val value1 = ObservableCollections.observableSet(Any())
        val value2 = ObservableCollections.observableSet(Any(), Any())
        val v: SetProperty<Any> = SimpleSetProperty(value1)
        val binding: SetBinding<Any> = object : SetBinding<Any>() {

            init {
                super.bind(v)
            }

            override fun computeValue(): ObservableSet<Any>? {
                return v.get()
            }

        }

        assertEquals("SetBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("SetBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("SetBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("SetBinding [value: $value2]", binding.toString())
    }

    @Test
    fun testArrayToString() {
        val value1 = ObservableCollections.observableObjectArray(arrayOf(Any()))
        val value2 = ObservableCollections.observableObjectArray(arrayOf(Any()), Any())
        val v: ArrayProperty<Any> = SimpleArrayProperty(value1, arrayOf(Any()))
        val binding: ArrayBinding<Any> = object : ArrayBinding<Any>(arrayOf(Any())) {

            init {
                super.bind(v)
            }

            override fun computeValue(): ObservableArray<Any>? {
                return v.get()
            }

        }

        assertEquals("ArrayBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ArrayBinding [value: $value1]", binding.toString())
        v.set(value2)
        assertEquals("ArrayBinding [invalid]", binding.toString())
        binding.get()
        assertEquals("ArrayBinding [value: $value2]", binding.toString())
    }

}