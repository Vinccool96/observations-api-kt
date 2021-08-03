package io.github.vinccool96.observationskt.beans.binding

import org.junit.Test
import kotlin.test.assertTrue

class StringBindingTest {

    @Test
    fun testDefaultDependencies() {
        assertTrue(StringBindingMock().dependencies.isEmpty())
    }

    private class StringBindingMock : StringBinding() {

        override fun computeValue(): String? {
            return null
        }

    }

}