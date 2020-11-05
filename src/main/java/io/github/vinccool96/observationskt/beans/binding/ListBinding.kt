package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanPropertyBase
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntPropertyBase
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.BindingHelperObserver
import io.github.vinccool96.observationskt.sun.binding.ListExpressionHelper
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * Base class that provides most of the functionality needed to implement a [Binding] of an [ObservableList].
 *
 * `ListBinding` provides a simple invalidation-scheme. An extending class can register dependencies by calling [bind].
 * If one of the registered dependencies becomes invalid, this `ListBinding` is marked as invalid. With [unbind]
 * listening to dependencies can be stopped.
 *
 * To provide a concrete implementation of this class, the method [computeValue] has to be implemented to calculate the
 * value of this binding based on the current state of the dependencies. It is called when [get] is called for an
 * invalid binding.
 *
 * See [DoubleBinding] for an example how this base class can be extended.
 *
 * @param E the type of the `List` element
 *
 * @see Binding
 * @see ListExpression
 */
abstract class ListBinding<E> : ListExpression<E>(), Binding<ObservableList<E>?> {

    private var valueState: ObservableList<E>? = null

    private var validState: Boolean = false

    private var observer: BindingHelperObserver? = null

    private var helper: ListExpressionHelper<E>? = null

    private val listChangeListener: ListChangeListener<E> = ListChangeListener {change ->
        invalidateProperties()
        onInvalidating()
        ListExpressionHelper.fireValueChangedEvent(this.helper, change)
    }

    private lateinit var size0: SizeProperty

    private lateinit var empty0: EmptyProperty

    override val sizeProperty: ReadOnlyIntProperty
        get() {
            if (!this::size0.isInitialized) {
                this.size0 = SizeProperty()
            }
            return this.size0
        }

    private inner class SizeProperty : ReadOnlyIntPropertyBase() {

        override fun get(): Int {
            return this@ListBinding.size
        }

        override val bean: Any? = this@ListBinding

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
            return this@ListBinding.isEmpty()
        }

        override val bean: Any? = this@ListBinding

        override val name: String? = "empty"

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

    }

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableList<E>?>) {
        if (!isChangeListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableList<E>?>) {
        if (isChangeListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableList<E>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: ListChangeListener<in E>) {
        if (!isListChangeListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ListChangeListener<in E>) {
        if (isListChangeListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in E>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.listChangeListeners.contains(listener)
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
    override fun get(): ObservableList<E>? {
        if (!this.validState) {
            this.valueState = computeValue()
            this.validState = true
            if (this.valueState != null) {
                this.valueState!!.addListener(this.listChangeListener)
            }
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

    override fun invalidate() {
        if (this.validState) {
            if (this.valueState != null) {
                this.valueState!!.removeListener(this.listChangeListener)
            }
            this.validState = false
            invalidateProperties()
            onInvalidating()
            ListExpressionHelper.fireValueChangedEvent(this.helper)
        }
    }

    override val valid: Boolean
        get() = this.validState

    /**
     * Calculates the current value of this binding.
     *
     * Classes extending `ListBinding` have to provide an implementation of `computeValue`.
     *
     * @return the current value
     */
    protected abstract fun computeValue(): ObservableList<E>?

    /**
     * Returns a string representation of this `ListBinding` object.
     *
     * @return a string representation of this `ListBinding` object.
     */
    override fun toString(): String {
        return if (this.validState) "ListBinding [value: ${get()}]" else "ListBinding [invalid]"
    }

}