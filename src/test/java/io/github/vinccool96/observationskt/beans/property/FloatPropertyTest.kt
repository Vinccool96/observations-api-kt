package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.sun.binding.ErrorLoggingUtility
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.fail

class FloatPropertyTest {

    @Test
    fun testValueSet_Null() {
        val p: FloatProperty = SimpleFloatProperty(VALUE_1)
        p.value = null
        assertEquals(DEFAULT, p.get(), EPSILON)
        log.checkFine(NullPointerException::class.java)
    }

    @Test
    fun testBindBidirectional() {
        val p1: FloatProperty = SimpleFloatProperty(VALUE_2)
        val p2: FloatProperty = SimpleFloatProperty(VALUE_1)

        p1.bindBidirectional(p2)
        assertEquals(VALUE_1, p1.get(), EPSILON)
        assertEquals(VALUE_1, p2.get(), EPSILON)

        p1.set(VALUE_2)
        assertEquals(VALUE_2, p1.get(), EPSILON)
        assertEquals(VALUE_2, p2.get(), EPSILON)

        p2.set(VALUE_1)
        assertEquals(VALUE_1, p1.get(), EPSILON)
        assertEquals(VALUE_1, p2.get(), EPSILON)

        p1.unbindBidirectional(p2)
        p1.set(VALUE_2)
        assertEquals(VALUE_2, p1.get(), EPSILON)
        assertEquals(VALUE_1, p2.get(), EPSILON)

        p1.set(VALUE_1)
        p2.set(VALUE_2)
        assertEquals(VALUE_1, p1.get(), EPSILON)
        assertEquals(VALUE_2, p2.get(), EPSILON)
    }

    @Test
    fun testToString() {
        val v0: FloatProperty = FloatPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("FloatProperty [value: $DEFAULT]", v0.toString())

        val v1: FloatProperty = FloatPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("FloatProperty [value: $DEFAULT]", v1.toString())

        val bean = Any()
        val name = "My name"
        val v2: FloatProperty = FloatPropertyStub(bean, name)
        assertEquals("FloatProperty [bean: $bean, name: My name, value: $DEFAULT]", v2.toString())
        v2.set(VALUE_1)
        assertEquals("FloatProperty [bean: $bean, name: My name, value: $VALUE_1]", v2.toString())

        val v3: FloatProperty = FloatPropertyStub(bean, NO_NAME_1)
        assertEquals("FloatProperty [bean: $bean, value: $DEFAULT]", v3.toString())
        v3.set(VALUE_1)
        assertEquals("FloatProperty [bean: $bean, value: $VALUE_1]", v3.toString())

        val v4: FloatProperty = FloatPropertyStub(bean, NO_NAME_2)
        assertEquals("FloatProperty [bean: $bean, value: $DEFAULT]", v4.toString())
        v4.set(VALUE_1)
        assertEquals("FloatProperty [bean: $bean, value: $VALUE_1]", v4.toString())

        val v5: FloatProperty = FloatPropertyStub(NO_BEAN, name)
        assertEquals("FloatProperty [name: My name, value: $DEFAULT]", v5.toString())
        v5.set(VALUE_1)
        assertEquals("FloatProperty [name: My name, value: $VALUE_1]", v5.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel: FloatProperty = SimpleFloatProperty(2.0f)
        val exp: ObjectProperty<Float> = valueModel.asObject()
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(2.0f, exp.get(), EPSILON)
        valueModel.set(-4354.3f)
        assertEquals(-4354.3f, exp.get(), EPSILON)
        valueModel.set(5e11f)
        assertEquals(5e11f, exp.get(), EPSILON)

        exp.set(1234.0f)
        assertEquals(1234.0f, valueModel.get(), EPSILON)
    }

    @Test
    fun testObjectToFloat() {
        val valueModel: ObjectProperty<Float?> = SimpleObjectProperty(null)
        val exp: FloatProperty = FloatProperty.floatProperty(valueModel)
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        assertEquals(0.0f, exp.get(), EPSILON)
        valueModel.set(-4354.3f)
        assertEquals(-4354.3f, exp.get(), EPSILON)
        valueModel.set(5e11f)
        assertEquals(5e11f, exp.get(), EPSILON)

        exp.set(1234.0f)
        assertEquals(1234.0f, valueModel.get()!!, EPSILON)
    }

    private class FloatPropertyStub(override val bean: Any?, override val name: String?) : FloatProperty() {

        private var valueState: Float = 0.0f

        override fun get(): Float {
            return this.valueState
        }

        override fun set(value: Float) {
            this.valueState = value
        }

        override fun addListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun removeListener(listener: InvalidationListener) {
            fail("Not in use")
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            fail("Not in use")
        }

        override fun addListener(listener: ChangeListener<in Number?>) {
            fail("Not in use")
        }

        override fun removeListener(listener: ChangeListener<in Number?>) {
            fail("Not in use")
        }

        override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Number?>): Boolean {
            fail("Not in use")
        }

        override fun bind(observable: ObservableValue<out Number?>) {
            fail("Not in use")
        }

        override fun unbind() {
            fail("Not in use")
        }

        override val bound: Boolean
            get() = fail("Not in use")

    }

    companion object {

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

        private const val VALUE_1: Float = PI.toFloat()

        private const val VALUE_2: Float = -E.toFloat()

        private const val DEFAULT: Float = 0.0f

        private const val EPSILON: Float = 1e-6f

        private val log: ErrorLoggingUtility = ErrorLoggingUtility()

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

    }

}