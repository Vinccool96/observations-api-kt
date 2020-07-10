package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.StringExpression

/**
 * Super class for all readonly properties wrapping a [String].
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableStringValue
 * @see StringExpression
 * @see ReadOnlyProperty
 */
abstract class ReadOnlyStringProperty : StringExpression(), ReadOnlyProperty<String?> {

    /**
     * Returns a string representation of this `ReadOnlyStringProperty` object.
     *
     * @return a string representation of this `ReadOnlyStringProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyStringProperty [")
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ")
        }
        if (name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        result.append("value: ").append(get()).append("]")
        return result.toString()
    }

}