package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.collections.SetChangeListener
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change
import io.github.vinccool96.observationskt.sun.binding.SetExpressionHelper
import java.lang.ref.WeakReference

/**
 * The class `SetPropertyBase` is the base class for a property wrapping an [ObservableSet].
 *
 * It provides all the functionality required for a property except for the [bean] and [name] values, which must be
 * implemented by extending classes.
 *
 * @param E the type of the `MutableSet` elements
 *
 * @see ObservableSet
 * @see SetProperty
 *
 * @constructor The constructor of the `SetPropertyBase`.
 *
 * @param initialValue the initial value of the wrapped value
 */
abstract class SetPropertyBase<E>(initialValue: ObservableSet<E>?) : SetProperty<E>() {

    private var valueState: ObservableSet<E>? = initialValue

    private var observable: ObservableValue<out ObservableSet<E>?>? = null

    private lateinit var listener: InvalidationListener

    private var valid: Boolean = true

    private var helper: SetExpressionHelper<E>? = null

    private val setChangeListener: SetChangeListener<E> = SetChangeListener { change ->
        invalidateProperties()
        invalidated()
        fireValueChangedEvent(change)
    }

    private lateinit var size0: SizeProperty

    private lateinit var empty0: EmptyProperty

    init {
        initialValue?.addListener(this.setChangeListener)
    }

    /**
     * The constructor of `SetPropertyBase`
     */
    constructor() : this(null)

    override val sizeProperty: ReadOnlyIntProperty
        get() {
            if (!this::size0.isInitialized) {
                this.size0 = SizeProperty()
            }
            return this.size0
        }

    private inner class SizeProperty : ReadOnlyIntPropertyBase() {

        override fun get(): Int {
            return this@SetPropertyBase.size
        }

        override val bean: Any
            get() = this@SetPropertyBase

        override val name: String
            get() = "size"

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

    }

    override val emptyProperty: ReadOnlyBooleanProperty
        get() {
            if (!this::empty0.isInitialized) {
                this.empty0 = EmptyProperty()
            }
            return this.empty0
        }

    private inner class EmptyProperty : ReadOnlyBooleanPropertyBase() {

        override fun get(): Boolean {
            return this@SetPropertyBase.isEmpty()
        }

        override val bean: Any
            get() = this@SetPropertyBase

        override val name: String
            get() = "empty"

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

    }

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.helper = SetExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.helper = SetExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableSet<E>?>) {
        if (!hasListener(listener)) {
            this.helper = SetExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableSet<E>?>) {
        if (hasListener(listener)) {
            this.helper = SetExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ChangeListener<in ObservableSet<E>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: SetChangeListener<in E>) {
        if (!hasListener(listener)) {
            this.helper = SetExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: SetChangeListener<in E>) {
        if (hasListener(listener)) {
            this.helper = SetExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: SetChangeListener<in E>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.setChangeListeners.contains(listener)
    }

    /**
     * This method needs to be called if the reference to the [ObservableSet] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [SetChangeListeners][SetChangeListener].
     *
     * This method needs to be called, if the value of this property changes.
     */
    protected open fun fireValueChangedEvent() {
        SetExpressionHelper.fireValueChangedEvent(this.helper)
    }

    /**
     * This method needs to be called if the content of the referenced [ObservableSet] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [SetChangeListeners][SetChangeListener].
     *
     * This method is called when the content of the set changes.
     *
     * @param change the change that needs to be propagated
     */
    protected open fun fireValueChangedEvent(change: Change<out E>) {
        SetExpressionHelper.fireValueChangedEvent(this.helper, change)
    }

    private fun invalidateProperties() {
        if (this::size0.isInitialized) {
            this.size0.fireValueChangedEvent()
        }
        if (this::empty0.isInitialized) {
            this.empty0.fireValueChangedEvent()
        }
    }

    private fun markInvalid(oldValue: ObservableSet<E>?) {
        if (this.valid) {
            oldValue?.removeListener(this.setChangeListener)
            this.valid = false
            invalidateProperties()
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

    override fun get(): ObservableSet<E>? {
        if (!this.valid) {
            this.valueState = if (this.observable == null) this.valueState else this.observable!!.value
            this.valid = true
            this.valueState?.addListener(this.setChangeListener)
        }
        return this.valueState
    }

    override fun set(value: ObservableSet<E>?) {
        if (this.bound) {
            val curBean = this.bean
            throw RuntimeException((if (curBean != null) "${curBean.javaClass.simpleName}.$name : " else "") +
                    "A bound value cannot be set.")
        }
        if (this.valueState !== value) {
            val oldValue = this.valueState
            this.valueState = value
            markInvalid(oldValue)
        }
    }

    override val bound: Boolean
        get() = this.observable != null

    override fun bind(observable: ObservableValue<out ObservableSet<E>?>) {
        if (observable != this.observable) {
            unbind()
            this.observable = observable
            if (!this::listener.isInitialized) {
                this.listener = Listener(this)
            }
            this.observable!!.addListener(this.listener)
            markInvalid(this.valueState)
        }
    }

    override fun unbind() {
        if (this.observable != null) {
            this.valueState = this.observable!!.value
            this.observable!!.removeListener(this.listener)
            this.observable = null
        }
    }

    /**
     * Returns a string representation of this `SetPropertyBase` object.
     *
     * @return a string representation of this `SetPropertyBase` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("SetProperty [")
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

    private class Listener<E>(ref: SetPropertyBase<E>) : InvalidationListener {

        private val wref: WeakReference<SetPropertyBase<E>> = WeakReference(ref)

        override fun invalidated(observable: Observable) {
            val ref = this.wref.get()
            if (ref == null) {
                observable.removeListener(this)
            } else {
                ref.markInvalid(ref.valueState)
            }
        }

    }

}