package io.github.vinccool96.observationskt.beans.binding

import org.junit.Test
import kotlin.test.assertTrue

class ObjectBindingTest {

    @Test
    fun testDefaultDependencies() {
        assertTrue(ObjectBindingMock().dependencies.isEmpty())
    }

    private class ObjectBindingMock : ObjectBinding<Any?>() {

        override fun computeValue(): Any? {
            return null
        }

    }

}