package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.binding.SetExpression
import io.github.vinccool96.observationskt.collections.ObservableSet

/**
 * Super class for all readonly properties wrapping an [ObservableSet].
 *
 * @param E the type of the `MutableSet` elements
 *
 * @see ObservableSet
 * @see io.github.vinccool96.observationskt.beans.value.ObservableSetValue
 * @see SetExpression
 * @see ReadOnlyProperty
 */
abstract class ReadOnlySetProperty<E> : SetExpression<E>(), ReadOnlyProperty<ObservableSet<E>?> {

    /**
     * Creates a bidirectional content binding of the [ObservableSet], that is wrapped in this `ReadOnlySetProperty`,
     * and another `ObservableSet`.
     *
     * A bidirectional content binding ensures that the content of two `ObservableSets` is the same. If the content of
     * one of the sets changes, the other one will be updated automatically.
     *
     * @param set the `ObservableSet` this property should be bound to
     *
     * @throws IllegalArgumentException if `set` is the same set that this `ReadOnlySetProperty` points to
     */
    fun bindContentBidirectional(set: ObservableSet<E>) {
        Bindings.bindContentBidirectional(this, set)
    }

    /**
     * Deletes a bidirectional content binding between the [ObservableSet], that is wrapped in this
     * `ReadOnlySetProperty`, and another `Object`.
     *
     * @param obj the `Object` to which the bidirectional binding should be removed
     *
     * @throws IllegalArgumentException if `obj` is the same set that this `ReadOnlySetProperty` points to
     */
    fun unbindContentBidirectional(obj: Any) {
        Bindings.unbindContentBidirectional(this, obj)
    }

    /**
     * Creates a content binding between the [ObservableSet], that is wrapped in this `ReadOnlySetProperty`, and another
     * `ObservableSet`.
     *
     * A content binding ensures that the content of the wrapped `ObservableSet` is the same as that of the other set.
     * If the content of the other set changes, the wrapped set will be updated automatically. Once the wrapped set is
     * bound to another set, you must not change it directly.
     *
     * @param set the `ObservableSet` this property should be bound to
     *
     * @throws IllegalArgumentException if `set` is the same set that this `ReadOnlySetProperty` points to
     */
    fun bindContent(set: ObservableSet<E>) {
        Bindings.bindContent(this, set)
    }

    /**
     * Deletes a content binding between the [ObservableSet], that is wrapped in this `ReadOnlySetProperty`, and another
     * `Object`.
     *
     * @param obj the `Object` to which the binding should be removed
     *
     * @throws IllegalArgumentException if `obj` is the same set that this `ReadOnlySetProperty` points to
     */
    fun unbindContent(obj: Any) {
        Bindings.unbindContent(this, obj)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is Set<*>) {
            return false
        }
        if (this.size != other.size) {
            return false
        }
        return try {
            this.containsAll(other)
        } catch (_: ClassCastException) {
            false
        } catch (_: NullPointerException) {
            false
        }
    }

    /**
     * Returns a hash code for this `ReadOnlySetProperty` object.
     *
     * @return a hash code for this `ReadOnlySetProperty` object.
     */
    override fun hashCode(): Int {
        var h = 0
        for (e in this) {
            h += e?.hashCode() ?: 0
        }
        return h
    }

    /**
     * Returns a string representation of this `ReadOnlySetProperty` object.
     *
     * @return a string representation of this `ReadOnlySetProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlySetProperty [")
        if (bean != null) {
            result.append("bean: $bean, ")
        }
        if (name != null && name.isNotEmpty()) {
            result.append("name: $name, ")
        }
        result.append("value: ${get()}]")
        return result.toString()
    }

}