package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.BindingHelperObserver
import io.github.vinccool96.observationskt.sun.binding.ExpressionHelper
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * Base class that provides most of the functionality needed to implement a [Binding] of an `Object`.
 *
 * `ObjectBinding` provides a simple invalidation-scheme. An extending class can register dependencies by calling
 * [bind]. If one of the registered dependencies becomes invalid, this `ObjectBinding` is marked as invalid. With
 * [unbind] listening to dependencies can be stopped.
 *
 * To provide a concrete implementation of this class, the method [computeValue] has to be implemented to calculate the
 * value of this binding based on the current state of the dependencies. It is called when [get] is called for an
 * invalid binding.
 *
 * See [DoubleBinding] for an example how this base class can be extended.
 *
 * @param T the type of the wrapped `Object`
 *
 * @see Binding
 * @see ObjectExpression
 */
abstract class ObjectBinding<T> : ObjectExpression<T>(), Binding<T> {

    private lateinit var holder: Holder<T>

    private var validState: Boolean = false

    private var observer: BindingHelperObserver? = null

    private var helper: ExpressionHelper<T>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in T>) {
        if (!isChangeListenerAlreadyAdded(listener)) {
            this.helper = ExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in T>) {
        if (isChangeListenerAlreadyAdded(listener)) {
            this.helper = ExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in T>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
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
            for (dep in dependencies) {
                dep.addListener(this.observer!!)
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
            for (dep in dependencies) {
                dep.removeListener(this.observer!!)
            }
            this.observer = null
        }
    }

    /**
     * A default implementation of `dispose` that is empty.
     */
    override fun dispose() {
    }

    @get:ReturnsUnmodifiableCollection
    override val dependencies: ObservableList<*>
        get() = ObservableCollections.emptyObservableList<Observable>()

    final override fun get(): T {
        if (!this.validState) {
            if (!this::holder.isInitialized) {
                this.holder = Holder(computeValue())
            } else {
                this.holder.value = computeValue()
            }
            this.validState = true
        }
        return this.holder.value
    }

    /**
     * The method onInvalidating() can be overridden by extending classes to react, if this binding becomes invalid. The
     * default implementation is empty.
     */
    protected open fun onInvalidating() {
    }

    override fun invalidate() {
        if (this.validState) {
            this.validState = false
            onInvalidating()
            ExpressionHelper.fireValueChangedEvent(this.helper)
        }
    }

    override val valid: Boolean
        get() = this.validState

    /**
     * Calculates the current value of this binding.
     *
     * Classes extending `ObjectBinding` have to provide an implementation of `computeValue`.
     *
     * @return the current value
     */
    protected abstract fun computeValue(): T

    /**
     * Returns a string representation of this `ObjectBinding` object.
     *
     * @return a string representation of this `ObjectBinding` object.
     */
    override fun toString(): String {
        return if (this.validState) "ObjectBinding [value: ${get()}]" else "ObjectBinding [invalid]"
    }

    private class Holder<T>(var value: T)

}