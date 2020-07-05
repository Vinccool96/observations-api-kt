package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListenerMock
import io.github.vinccool96.observationskt.beans.property.ObjectProperty
import io.github.vinccool96.observationskt.beans.property.SimpleObjectProperty
import io.github.vinccool96.observationskt.beans.property.SimpleStringProperty
import io.github.vinccool96.observationskt.beans.property.StringProperty
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BindingsIsNullTest {

    private lateinit var oo: ObjectProperty<Any?>

    private lateinit var os: StringProperty

    private lateinit var observer: InvalidationListenerMock

    @Before
    fun setUp() {
        this.oo = SimpleObjectProperty(null)
        this.os = SimpleStringProperty()
        this.observer = InvalidationListenerMock()
    }

    @Test
    fun test_Object_IsNull() {
        val binding: BooleanBinding = Bindings.isNull(this.oo)
        binding.addListener(this.observer)

        // check initial value
        assertTrue(binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.oo)
        this.observer.reset()

        // change operand
        this.oo.set(Any())
        assertFalse(binding.get())
        this.observer.check(binding, 1)

        // change again
        this.oo.set(null)
        assertTrue(binding.get())
        this.observer.check(binding, 1)
    }

    @Test
    fun test_Object_IsNotNull() {
        val binding: BooleanBinding = Bindings.isNotNull(this.oo)
        binding.addListener(this.observer)

        // check initial value
        assertFalse(binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.oo)
        this.observer.reset()

        // change operand
        this.oo.set(Any())
        assertTrue(binding.get())
        this.observer.check(binding, 1)

        // change again
        this.oo.set(null)
        assertFalse(binding.get())
        this.observer.check(binding, 1)
    }

    @Test
    fun test_String_IsNull() {
        val binding: BooleanBinding = Bindings.isNull(this.os)
        binding.addListener(this.observer)

        // check initial value
        assertTrue(binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.os)
        this.observer.reset()

        // change operand
        this.os.set("Hello World")
        assertFalse(binding.get())
        this.observer.check(binding, 1)

        // change again
        this.os.set(null)
        assertTrue(binding.get())
        this.observer.check(binding, 1)
    }

    @Test
    fun test_String_IsNotNull() {
        val binding: BooleanBinding = Bindings.isNotNull(this.os)
        binding.addListener(this.observer)

        // check initial value
        assertFalse(binding.get())
        DependencyUtils.checkDependencies(binding.dependencies, this.os)
        this.observer.reset()

        // change operand
        this.os.set("Hello World")
        assertTrue(binding.get())
        this.observer.check(binding, 1)

        // change again
        this.os.set(null)
        assertFalse(binding.get())
        this.observer.check(binding, 1)
    }

}