package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.binding.StringExpression
import io.github.vinccool96.observationskt.beans.value.ChangeListener

class StringConstant private constructor(override val value: String?) : StringExpression() {

    final override fun get(): String? {
        return this.value
    }

    override fun addListener(listener: InvalidationListener) {
        // no-op
    }

    override fun removeListener(listener: InvalidationListener) {
        // no-op
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        // no-op
        return false
    }

    override fun addListener(listener: ChangeListener<in String?>) {
        // no-op
    }

    override fun removeListener(listener: ChangeListener<in String?>) {
        // no-op
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in String?>): Boolean {
        // no-op
        return false
    }

    companion object {

        fun valueOf(value: String?): StringConstant {
            return StringConstant(value)
        }

    }

}