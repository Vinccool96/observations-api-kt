package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import org.junit.Assert
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ReadOnlyFloatPropertyTest {

    @Test
    fun testToString() {
        val v1: ReadOnlyFloatProperty = ReadOnlyFloatPropertyStub(NO_BEAN, NO_NAME_1)
        assertEquals("ReadOnlyFloatProperty [value: $DEFAULT]", v1.toString())

        val v2: ReadOnlyFloatProperty = ReadOnlyFloatPropertyStub(NO_BEAN, NO_NAME_2)
        assertEquals("ReadOnlyFloatProperty [value: $DEFAULT]", v2.toString())

        val bean = Any()
        val name = "General Kenobi"

        val v3: ReadOnlyFloatProperty = ReadOnlyFloatPropertyStub(bean, name)
        assertEquals("ReadOnlyFloatProperty [bean: $bean, name: General Kenobi, value: $DEFAULT]", v3.toString())

        val v4: ReadOnlyFloatProperty = ReadOnlyFloatPropertyStub(bean, NO_NAME_1)
        assertEquals("ReadOnlyFloatProperty [bean: $bean, value: $DEFAULT]", v4.toString())

        val v5: ReadOnlyFloatProperty = ReadOnlyFloatPropertyStub(bean, NO_NAME_2)
        assertEquals("ReadOnlyFloatProperty [bean: $bean, value: $DEFAULT]", v5.toString())

        val v6: ReadOnlyFloatProperty = ReadOnlyFloatPropertyStub(NO_BEAN, name)
        assertEquals("ReadOnlyFloatProperty [name: General Kenobi, value: $DEFAULT]", v6.toString())
    }

    @Test
    fun testAsObject() {
        val valueModel = ReadOnlyFloatWrapper()
        val exp: ReadOnlyObjectProperty<Float> = valueModel.readOnlyProperty.asObject()
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        Assert.assertEquals(0.0f, exp.get(), EPSILON)
        valueModel.set(-4354.3f)
        Assert.assertEquals(-4354.3f, exp.get(), EPSILON)
        valueModel.set(5e11f)
        Assert.assertEquals(5e11f, exp.get(), EPSILON)
    }

    @Test
    fun testObjectToDouble() {
        val valueModel: ReadOnlyObjectWrapper<Float?> = ReadOnlyObjectWrapper(null)
        val exp: ReadOnlyFloatProperty = ReadOnlyFloatProperty.readOnlyFloatProperty(valueModel.readOnlyProperty)
        assertNull(exp.bean)
        assertSame(valueModel.name, exp.name)

        Assert.assertEquals(0.0f, exp.get(), EPSILON)
        valueModel.set(-4354.3f)
        Assert.assertEquals(-4354.3f, exp.get(), EPSILON)
        valueModel.set(5e11f)
        Assert.assertEquals(5e11f, exp.get(), EPSILON)
    }

    private class ReadOnlyFloatPropertyStub(override val bean: Any?, override val name: String?) :
            ReadOnlyFloatProperty() {

        override fun get(): Float {
            return 0.0f
        }

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun hasListener(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ChangeListener<in Number?>) {
        }

        override fun removeListener(listener: ChangeListener<in Number?>) {
        }

        override fun hasListener(listener: ChangeListener<in Number?>): Boolean {
            return false
        }

    }

    companion object {

        private const val DEFAULT: Float = 0.0f

        private const val EPSILON: Float = 1e-6f

        private val NO_BEAN: Any? = null

        private val NO_NAME_1: String? = null

        private const val NO_NAME_2: String = ""

    }

}