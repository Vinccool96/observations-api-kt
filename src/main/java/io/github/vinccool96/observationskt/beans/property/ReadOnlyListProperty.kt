package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.Bindings
import io.github.vinccool96.observationskt.beans.binding.ListExpression
import io.github.vinccool96.observationskt.collections.ObservableList

/**
 * Super class for all readonly properties wrapping an [ObservableList].
 *
 * @param E the type of the `List` elements
 *
 * @see ObservableList
 * @see io.github.vinccool96.observationskt.beans.value.ObservableListValue
 * @see ListExpression
 * @see ReadOnlyProperty
 */
@Suppress("UNCHECKED_CAST")
abstract class ReadOnlyListProperty<E> : ListExpression<E>(), ReadOnlyProperty<ObservableList<E>?> {

    /**
     * Creates a bidirectional content binding of the [ObservableList], that is wrapped in this `ReadOnlyListProperty`,
     * and another `ObservableList`.
     *
     * A bidirectional content binding ensures that the content of two `ObservableLists` is the same. If the content of
     * one of the lists changes, the other one will be updated automatically.
     *
     * @param list the `ObservableList` this property should be bound to
     *
     * @throws IllegalArgumentException if `list` is the same list that this `ReadOnlyListProperty` points to
     */
    fun bindContentBidirectional(list: ObservableList<E>) {
        Bindings.bindContentBidirectional(this, list)
    }

    /**
     * Deletes a bidirectional content binding between the [ObservableList], that is wrapped in this
     * `ReadOnlyListProperty`, and another `Object`.
     *
     * @param obj the `Object` to which the bidirectional binding should be removed
     *
     * @throws IllegalArgumentException if `obj` is the same list that this `ReadOnlyListProperty` points to
     */
    fun unbindContentBidirectional(obj: Any) {
        Bindings.unbindContentBidirectional(this, obj)
    }

    /**
     * Creates a content binding between the [ObservableList], that is wrapped in this `ReadOnlyListProperty`, and
     * another `ObservableList`.
     *
     * A content binding ensures that the content of the wrapped `ObservableLists` is the same as that of the other
     * list. If the content of the other list changes, the wrapped list will be updated automatically. Once the wrapped
     * list is bound to another list, you **must not** change it directly.
     *
     * @param list the `ObservableList` this property should be bound to
     *
     * @throws IllegalArgumentException if `list` is the same list that this `ReadOnlyListProperty` points to
     */
    fun bindContent(list: ObservableList<E>) {
        Bindings.bindContent(this, list)
    }

    /**
     * Deletes a content binding between the [ObservableList], that is wrapped in this `ReadOnlyListProperty`, and
     * another `Object`.
     *
     * @param obj the `Object` to which the binding should be removed
     *
     * @throws IllegalArgumentException if `obj` is the same list that this `ReadOnlyListProperty` points to
     */
    fun unbindContent(obj: Any) {
        Bindings.unbindContent(this, obj)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is List<*>) {
            return false
        }
        val list: List<E> = other as List<E>

        if (size != list.size) {
            return false
        }

        val e1: ListIterator<E> = listIterator()
        val e2 = list.listIterator()

        while (e1.hasNext() && e2.hasNext()) {
            val o1 = e1.next()
            val o2 = e2.next()
            if (o1 != o2) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var hashCode = 1
        for (e in this) {
            hashCode = 31 * hashCode + (e?.hashCode() ?: 0)
        }
        return hashCode
    }

    /**
     * Returns a string representation of this `ReadOnlyListProperty` object.
     *
     * @return a string representation of this `ReadOnlyListProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyListProperty [")
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ")
        }
        if (name != null && name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        result.append("value: ").append(get()).append("]")
        return result.toString()
    }

}