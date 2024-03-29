package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.sun.binding.ExpressionHelper
import java.lang.ref.WeakReference

/**
 * The class `ObjectPropertyBase` is the base class for a property wrapping an arbitrary `Object`.
 *
 * It provides all the functionality required for a property except for the [bean] and [name] values, which must be
 * implemented by extending classes.
 *
 * @param T the type of the wrapped value
 *
 * @see ObjectProperty
 *
 * @constructor The constructor of the `ObjectPropertyBase`.
 *
 * @param initialValue the initial value of the wrapped `Object`
 */
@Suppress("DuplicatedCode")
abstract class ObjectPropertyBase<T>(initialValue: T) : ObjectProperty<T>() {

    private var valueState: T = initialValue

    private var observable: ObservableValue<out T>? = null

    private var listener: InvalidationListener? = null

    private var valid: Boolean = true

    private var helper: ExpressionHelper<T>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.helper = ExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.helper = ExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in T>) {
        if (!hasListener(listener)) {
            this.helper = ExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in T>) {
        if (hasListener(listener)) {
            this.helper = ExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ChangeListener<in T>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    /**
     * Sends notifications to all attached [InvalidationListeners][InvalidationListener] and
     * [ChangeListeners][ChangeListener].
     *
     * This method is called when the value is changed, either manually by calling [set] or in case of a bound
     * property, if the binding becomes invalid.
     */
    protected open fun fireValueChangedEvent() {
        ExpressionHelper.fireValueChangedEvent(this.helper)
    }

    private fun markInvalid() {
        if (this.valid) {
            this.valid = false
            invalidated()
            fireValueChangedEvent()
        }
    }

    /**
     * The method `invalidated()` can be overridden to receive invalidation notifications. This is the preferred option
     * in `Objects` defining the property, because it requires less memory.
     *
     * The default implementation is empty.
     */
    protected open fun invalidated() {
    }

    final override fun get(): T {
        this.valid = true
        return if (this.observable == null) this.valueState else this.observable!!.value
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(value: T) {
        if (this.bound) {
            val curBean = this.bean
            throw RuntimeException((if (curBean != null) "${curBean.javaClass.simpleName}.$name : " else "") +
                    "A bound value cannot be set.")
        }
        if (this.valueState !== value) {
            this.valueState = value
            markInvalid()
        }
    }

    override val bound: Boolean
        get() = this.observable != null

    override fun bind(observable: ObservableValue<out T>) {
        if (observable != this.observable) {
            unbind()
            this.observable = observable
            if (this.listener == null) {
                this.listener = Listener(this)
            }
            this.observable!!.addListener(this.listener!!)
            markInvalid()
        }
    }

    override fun unbind() {
        if (this.observable != null) {
            this.valueState = this.observable!!.value
            this.observable!!.removeListener(this.listener!!)
            this.observable = null
        }
    }

    /**
     * Returns a string representation of this `ObjectPropertyBase` object.
     *
     * @return a string representation of this `ObjectPropertyBase` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ObjectProperty [")
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ")
        }
        if (name != null && name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        if (this.bound) {
            result.append("bound, ")
            if (this.valid) {
                result.append("value: ").append(get())
            } else {
                result.append("invalid")
            }
        } else {
            result.append("value: ").append(get())
        }
        result.append("]")
        return result.toString()
    }

    private class Listener<T>(ref: ObjectPropertyBase<T>) : InvalidationListener {

        private val wref: WeakReference<ObjectPropertyBase<T>> = WeakReference(ref)

        override fun invalidated(observable: Observable) {
            val ref = this.wref.get()
            if (ref == null) {
                observable.removeListener(this)
            } else {
                ref.markInvalid()
            }
        }

    }

}