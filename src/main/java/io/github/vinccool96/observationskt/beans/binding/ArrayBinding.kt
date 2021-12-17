package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanPropertyBase
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntPropertyBase
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
import io.github.vinccool96.observationskt.collections.ArrayChangeListener
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.ArrayExpressionHelper
import io.github.vinccool96.observationskt.sun.binding.BindingHelperObserver
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * Base class that provides most of the functionality needed to implement a [Binding] of an [ObservableArray].
 *
 * `ArrayBinding` provides a simple invalidation-scheme. An extending class can register dependencies by calling [bind].
 * If one of the registered dependencies becomes invalid, this `ArrayBinding` is marked as invalid. With [unbind]
 * listening to dependencies can be stopped.
 *
 * To provide a concrete implementation of this class, the method [computeValue] has to be implemented to calculate the
 * value of this binding based on the current state of the dependencies. It is called when [get] is called for an
 * invalid binding.
 *
 * See [DoubleBinding] for an example how this base class can be extended.
 *
 * @param T the type of the `Array` elements
 *
 * @see Binding
 * @see ArrayExpression
 *
 * @constructor The constructor needs a base array that [baseArray] will return when [ObservableArrayValue.get] returns
 * `null`. Therefore, `baseArray` will return the equivalent of `get()?.baseArray ?: baseArrayOfNull`.
 *
 * @param baseArrayOfNull the base array when the value is `null`
 */
abstract class ArrayBinding<T>(baseArrayOfNull: Array<T>) : ArrayExpression<T>(baseArrayOfNull),
        Binding<ObservableArray<T>?> {

    private var valueState: ObservableArray<T>? = null

    private var validState: Boolean = false

    private var observer: BindingHelperObserver? = null

    private var helper: ArrayExpressionHelper<T>? = null

    private val arrayChangeListener = ArrayChangeListener<T> { change ->
        invalidateProperties()
        onInvalidating()
        ArrayExpressionHelper.fireValueChangedEvent(this.helper, change)
    }

    private lateinit var size0: SizeProperty

    override val sizeProperty: ReadOnlyIntProperty
        get() {
            if (!this::size0.isInitialized) {
                this.size0 = SizeProperty()
            }
            return this.size0
        }

    private inner class SizeProperty : ReadOnlyIntPropertyBase() {

        override val bean: Any
            get() = this@ArrayBinding

        override val name: String
            get() = "size"

        override fun get(): Int {
            return this@ArrayBinding.size
        }

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

    }

    private lateinit var empty0: EmptyProperty

    override val emptyProperty: ReadOnlyBooleanProperty
        get() {
            if (!this::empty0.isInitialized) {
                this.empty0 = EmptyProperty()
            }
            return this.empty0
        }

    private inner class EmptyProperty : ReadOnlyBooleanPropertyBase() {

        override val bean: Any
            get() = this@ArrayBinding

        override val name: String
            get() = "empty"

        override fun get(): Boolean {
            return this@ArrayBinding.size == 0
        }

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

    }

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableArray<T>?>) {
        if (!isChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableArray<T>?>) {
        if (isChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableArray<T>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: ArrayChangeListener<T>) {
        if (!isArrayChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ArrayChangeListener<T>) {
        if (isArrayChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isArrayChangeListenerAlreadyAdded(listener: ArrayChangeListener<T>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.arrayChangeListeners.contains(listener)
    }

    /**
     * Start observing the dependencies for changes. If the value of one of the dependencies changes, the binding is
     * marked as invalid.
     *
     * @param dependencies the dependencies to observe
     */
    protected fun bind(vararg dependencies: Observable) {
        if (dependencies.isNotEmpty()) {
            if (this.observer == null) {
                this.observer = BindingHelperObserver(this)
            }
            for (dependency in dependencies) {
                dependency.addListener(this.observer!!)
            }
        }
    }

    /**
     * Stop observing the dependencies for changes.
     *
     * @param dependencies the dependencies to stop observing
     */
    protected fun unbind(vararg dependencies: Observable) {
        if (this.observer != null) {
            for (dependency in dependencies) {
                dependency.removeListener(this.observer!!)
            }
            this.observer = null
        }
    }

    /**
     * A default implementation of `dispose()` that is empty.
     */
    override fun dispose() {
    }

    @get:ReturnsUnmodifiableCollection
    override val dependencies: ObservableList<*>
        get() = ObservableCollections.emptyObservableList<Any>()

    /**
     * Returns the result of [computeValue]. The method `computeValue()` is only called if the binding is invalid. The
     * result is cached and returned if the binding did not become invalid since the last call of `get`.
     *
     * @return the current value
     */
    final override fun get(): ObservableArray<T>? {
        if (!this.validState) {
            this.valueState = computeValue()
            this.validState = true
            this.valueState?.addListener(this.arrayChangeListener)
        }
        return this.valueState
    }

    /**
     * The method onInvalidating() can be overridden by extending classes to react, if this binding becomes invalid. The
     * default implementation is empty.
     */
    protected open fun onInvalidating() {
    }

    private fun invalidateProperties() {
        if (this::size0.isInitialized) {
            this.size0.fireValueChangedEvent()
        }
        if (this::empty0.isInitialized) {
            this.empty0.fireValueChangedEvent()
        }
    }

    final override fun invalidate() {
        if (this.validState) {
            this.valueState?.removeListener(this.arrayChangeListener)
            this.validState = false
            invalidateProperties()
            onInvalidating()
            ArrayExpressionHelper.fireValueChangedEvent(this.helper)
        }
    }

    final override val valid: Boolean
        get() = this.validState

    /**
     * Calculates the current value of this binding.
     *
     * Classes extending `ArrayBinding` have to provide an implementation of `computeValue`.
     *
     * @return the current value
     */
    protected abstract fun computeValue(): ObservableArray<T>?

    /**
     * Returns a string representation of this `ArrayBinding` object.
     *
     * @return a string representation of this `ArrayBinding` object.
     */
    override fun toString(): String {
        return if (this.validState) "ArrayBinding [value: ${get()}]" else "ArrayBinding [invalid]"
    }

}