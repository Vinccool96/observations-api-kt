package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ArrayChangeListener
import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.sun.binding.ArrayExpressionHelper
import java.lang.ref.WeakReference

/**
 * The class `ArrayPropertyBase` is the base class for a property wrapping an [ObservableArray].
 *
 * It provides all the functionality required for a property except for the [bean] and [name] values, which must be
 * implemented by extending classes.
 *
 * @param T the type of the `Array` elements
 *
 * @see ObservableArray
 * @see ArrayProperty
 *
 * @constructor The constructor of the `ArrayPropertyBase`.
 *
 * @param initialValue the initial value of the wrapped value
 * @param baseArrayOfNull the base array when the value is `null`
 */
abstract class ArrayPropertyBase<T>(initialValue: ObservableArray<T>?, baseArrayOfNull: Array<T>) :
        ArrayProperty<T>(baseArrayOfNull) {

    private var valueState: ObservableArray<T>? = initialValue

    private var valid: Boolean = true

    private var observable: ObservableValue<out ObservableArray<T>?>? = null

    private var listener: InvalidationListener? = null

    private var helper: ArrayExpressionHelper<T>? = null

    private val arrayChangeListener: ArrayChangeListener<T> = ArrayChangeListener { change ->
        invalidateProperties()
        invalidated()
        fireValueChangedEvent(change)
    }

    private lateinit var size0: SizeProperty

    private lateinit var empty0: EmptyProperty

    init {
        this.valueState?.addListener(this.arrayChangeListener)
    }

    /**
     * The constructor of `ListPropertyBase`
     */
    constructor(baseArrayOfNull: Array<T>) : this(null, baseArrayOfNull)

    override val sizeProperty: ReadOnlyIntProperty
        get() {
            if (!this::size0.isInitialized) {
                this.size0 = SizeProperty()
            }
            return this.size0
        }

    private inner class SizeProperty : ReadOnlyIntPropertyBase() {

        override fun get(): Int {
            return this@ArrayPropertyBase.size
        }

        override val bean: Any = this@ArrayPropertyBase

        override val name: String = "size"

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
            return this@ArrayPropertyBase.isEmpty()
        }

        override val bean: Any = this@ArrayPropertyBase

        override val name: String = "empty"

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

    }

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableArray<T>?>) {
        if (!hasListener(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableArray<T>?>) {
        if (hasListener(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ChangeListener<in ObservableArray<T>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: ArrayChangeListener<in T>) {
        if (!hasListener(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ArrayChangeListener<in T>) {
        if (hasListener(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ArrayChangeListener<in T>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.arrayChangeListeners.contains(listener)
    }

    /**
     * This method needs to be called if the reference to the [ObservableArray] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [ArrayChangeListeners][ArrayChangeListener].
     *
     * This method needs to be called, if the value of this property changes.
     */
    protected open fun fireValueChangedEvent() {
        ArrayExpressionHelper.fireValueChangedEvent(this.helper)
    }

    /**
     * This method needs to be called if the content of the referenced [ObservableArray] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [ArrayChangeListeners][ArrayChangeListener].
     *
     * This method is called when the content of the array changes.
     *
     * @param change the change that needs to be propagated
     */
    protected open fun fireValueChangedEvent(change: Change<out T>) {
        ArrayExpressionHelper.fireValueChangedEvent(this.helper, change)
    }

    private fun invalidateProperties() {
        if (this::size0.isInitialized) {
            this.size0.fireValueChangedEvent()
        }
        if (this::empty0.isInitialized) {
            this.empty0.fireValueChangedEvent()
        }
    }

    private fun markInvalid(oldValue: ObservableArray<T>?) {
        if (this.valid) {
            oldValue?.removeListener(this.arrayChangeListener)
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

    override fun get(): ObservableArray<T>? {
        if (!this.valid) {
            this.valueState = if (this.observable == null) this.valueState else this.observable!!.value
            this.valid = true
            this.valueState?.addListener(this.arrayChangeListener)
        }
        return this.valueState
    }

    override fun set(value: ObservableArray<T>?) {
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

    override fun bind(observable: ObservableValue<out ObservableArray<T>?>) {
        if (observable != this.observable) {
            unbind()
            this.observable = observable
            if (this.listener == null) {
                this.listener = Listener(this)
            }
            this.observable!!.addListener(this.listener!!)
            markInvalid(this.valueState)
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
     * Returns a string representation of this `ArrayPropertyBase` object.
     *
     * @return a string representation of this `ArrayPropertyBase` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ArrayProperty [")
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

    private class Listener<T>(ref: ArrayPropertyBase<T>) : InvalidationListener {

        private val wref: WeakReference<ArrayPropertyBase<T>> = WeakReference(ref)

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