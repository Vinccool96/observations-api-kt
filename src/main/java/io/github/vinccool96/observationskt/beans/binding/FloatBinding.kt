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
 * Base class that provides most of the functionality needed to implement a [Binding] of a `Float` value.
 *
 * `FloatBinding` provides a simple invalidation-scheme. An extending class can register dependencies by calling [bind].
 * If one of the registered dependencies becomes invalid, this `FloatBinding` is marked as invalid. With [unbind]
 * listening to dependencies can be stopped.
 *
 * To provide a concrete implementation of this class, the method [computeValue] has to be implemented to calculate the
 * value of this binding based on the current state of the dependencies. It is called when [get] is called for an
 * invalid binding.
 *
 * See [DoubleBinding] for an example how this base class can be extended.
 *
 * @see Binding
 * @see NumberBinding
 * @see FloatExpression
 */
abstract class FloatBinding : FloatExpression(), NumberBinding {

    private var valueState: Float = 0.0F

    private var validState: Boolean = false

    private var observer: BindingHelperObserver? = null

    private var helper: ExpressionHelper<Number?>? = null

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

    override fun addListener(listener: ChangeListener<in Number?>) {
        if (!hasListener(listener)) {
            this.helper = ExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in Number?>) {
        if (hasListener(listener)) {
            this.helper = ExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ChangeListener<in Number?>): Boolean {
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

    /**
     * Returns the result of [computeValue]. The method `computeValue()` is only called if the binding is invalid. The
     * result is cached and returned if the binding did not become invalid since the last call of `get()`.
     *
     * @return the current value
     */
    final override fun get(): Float {
        if (!this.validState) {
            this.valueState = computeValue()
            this.validState = true
        }
        return this.valueState
    }

    /**
     * The method onInvalidating() can be overridden by extending classes to react, if this binding becomes invalid. The
     * default implementation is empty.
     */
    protected open fun onInvalidating() {
    }

    final override fun invalidate() {
        if (this.validState) {
            this.validState = false
            onInvalidating()
            ExpressionHelper.fireValueChangedEvent(this.helper)
        }
    }

    final override val valid: Boolean
        get() = this.validState

    /**
     * Calculates the current value of this binding.
     *
     * Classes extending `FloatBinding` have to provide an implementation of `computeValue`.
     *
     * @return the current value
     */
    protected abstract fun computeValue(): Float

    /**
     * Returns a string representation of this `FloatBinding` object.
     *
     * @return a string representation of this `FloatBinding` object.
     */
    override fun toString(): String {
        return if (this.validState) "FloatBinding [value: ${get()}]" else "FloatBinding [invalid]"
    }

}