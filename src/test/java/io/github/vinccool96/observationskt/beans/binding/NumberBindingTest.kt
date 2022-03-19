package io.github.vinccool96.observationskt.beans.binding

import kotlin.test.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class NumberBindingTest(private val numberBinding: NumberBinding) {

    @Test
    fun testDefaultDependencies() {
        assertTrue(this.numberBinding.dependencies.isEmpty())
    }

    private class DoubleBindingMock : DoubleBinding() {

        override fun computeValue(): Double {
            return 0.0
        }

    }

    private class FloatBindingMock : FloatBinding() {

        override fun computeValue(): Float {
            return 0.0f
        }

    }

    private class IntBindingMock : IntBinding() {

        override fun computeValue(): Int {
            return 0
        }

    }

    private class LongBindingMock : LongBinding() {

        override fun computeValue(): Long {
            return 0L
        }

    }

    private class ShortBindingMock : ShortBinding() {

        override fun computeValue(): Short {
            return 0
        }

    }

    private class ByteBindingMock : ByteBinding() {

        override fun computeValue(): Byte {
            return 0
        }

    }

    companion object {

        @Parameters
        @JvmStatic
        fun createParameters(): List<Array<out Any?>> {
            return listOf(
                    arrayOf(DoubleBindingMock()),
                    arrayOf(FloatBindingMock()),
                    arrayOf(IntBindingMock()),
                    arrayOf(LongBindingMock()),
                    arrayOf(ShortBindingMock()),
                    arrayOf(ByteBindingMock())
            )
        }

    }

}