package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.MapChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.sun.binding.MapExpressionHelper
import java.lang.ref.WeakReference

/**
 * The class `MapPropertyBase` is the base class for a property wrapping an [ObservableMap].
 *
 * It provides all the functionality required for a property except for the [bean] and [name] values, which must be
 * implemented by extending classes.
 *
 * @param K the type of the key elements of the `Map`
 * @param V the type of the value elements of the `Map`
 *
 * @see ObservableMap
 * @see MapProperty
 *
 * @constructor The constructor of the `MapPropertyBase`.
 *
 * @param initialValue the initial value of the wrapped value
 *
 */
@Suppress("RedundantNullableReturnType")
abstract class MapPropertyBase<K, V>(initialValue: ObservableMap<K, V>?) : MapProperty<K, V>() {

    private var valueState: ObservableMap<K, V>? = initialValue

    private var observable: ObservableValue<out ObservableMap<K, V>?>? = null

    private var listener: InvalidationListener? = null

    private var validState: Boolean = true

    private var helper: MapExpressionHelper<K, V>? = null

    private val mapChangeListener: MapChangeListener<K, V> = MapChangeListener { change ->
        invalidateProperties()
        invalidated()
        fireValueChangedEvent(change)
    }

    private lateinit var size0: SizeProperty

    private lateinit var empty0: EmptyProperty

    init {
        if (this.valueState != null) {
            this.valueState!!.addListener(this.mapChangeListener)
        }
    }

    /**
     * The constructor of `MapPropertyBase`
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
            return this@MapPropertyBase.size
        }

        override val bean: Any? = this@MapPropertyBase

        override val name: String? = "size"

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
            return this@MapPropertyBase.isEmpty()
        }

        override val bean: Any? = this@MapPropertyBase

        override val name: String? = "empty"

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

    }

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableMap<K, V>?>) {
        if (!isChangeListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableMap<K, V>?>) {
        if (isChangeListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableMap<K, V>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: MapChangeListener<in K, in V>) {
        if (!isMapChangeListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: MapChangeListener<in K, in V>) {
        if (isMapChangeListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in K, in V>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.mapChangeListeners.contains(listener)
    }

    /**
     * Sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [MapExpressionHelper].
     *
     * This method is called when the value is changed, either manually by calling [set] or in case of a bound property,
     * if the binding becomes invalid.
     */
    protected open fun fireValueChangedEvent() {
        MapExpressionHelper.fireValueChangedEvent(this.helper)
    }

    /**
     * Sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [MapExpressionHelper].
     *
     * This method is called when the content of the list changes.
     *
     * @param change the change that needs to be propagated
     */
    protected open fun fireValueChangedEvent(change: Change<out K, out V>) {
        MapExpressionHelper.fireValueChangedEvent(this.helper, change)
    }

    private fun invalidateProperties() {
        if (this::size0.isInitialized) {
            this.size0.fireValueChangedEvent()
        }
        if (this::empty0.isInitialized) {
            this.empty0.fireValueChangedEvent()
        }
    }

    private fun markInvalid(oldValue: ObservableMap<K, V>?) {
        if (this.validState) {
            oldValue?.removeListener(this.mapChangeListener)
            this.validState = false
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

    override fun get(): ObservableMap<K, V>? {
        if (!this.validState) {
            this.valueState = if (this.observable != null) this.observable!!.value else this.valueState
            this.validState = true
            if (this.valueState != null) {
                this.valueState!!.addListener(this.mapChangeListener)
            }
        }
        return this.valueState
    }

    override fun set(value: ObservableMap<K, V>?) {
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

    override fun bind(observable: ObservableValue<out ObservableMap<K, V>?>) {
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
     * Returns a string representation of this `MapPropertyBase` object.
     *
     * @return a string representation of this `MapPropertyBase` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("MapProperty [")
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ")
        }
        if (name != null && name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        if (this.bound) {
            result.append("bound, ")
            if (this.validState) {
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

    private class Listener<K, V>(ref: MapPropertyBase<K, V>) : InvalidationListener {

        private val wref: WeakReference<MapPropertyBase<K, V>> = WeakReference(ref)

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