package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyBooleanPropertyBase
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntProperty
import io.github.vinccool96.observationskt.beans.property.ReadOnlyIntPropertyBase
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.sun.binding.BindingHelperObserver
import io.github.vinccool96.observationskt.sun.binding.MapExpressionHelper
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection

/**
 * Base class that provides most of the functionality needed to implement a [Binding] of an [ObservableMap].
 *
 * `MapBinding` provides a simple invalidation-scheme. An extending class can register dependencies by calling
 * [bind]. If one of the registered dependencies becomes invalid, this `MapBinding` is
 * marked as invalid. With [unbind] listening to dependencies can be stopped.
 *
 * To provide a concrete implementation of this class, the method [computeValue] has to be implemented to
 * calculate the value of this binding based on the current state of the dependencies. It is called when [get]
 * is called for an invalid binding.
 *
 * See [DoubleBinding] for an example how this base class can be extended.
 *
 * @param K the type of the key elements
 * @param V the type of the value elements
 *
 * @see Binding
 * @see MapExpression
 */
@Suppress("RedundantNullableReturnType")
abstract class MapBinding<K, V> : MapExpression<K, V>(), Binding<ObservableMap<K, V>?> {

    private var valueState: ObservableMap<K, V>? = null

    private var validState: Boolean = false

    private var observer: BindingHelperObserver? = null

    private var helper: MapExpressionHelper<K, V>? = null

    private val mapChangeListener: MapChangeListener<K, V> = MapChangeListener { change ->
        invalidateProperties()
        onInvalidating()
        MapExpressionHelper.fireValueChangedEvent(this.helper, change)
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
            return this@MapBinding.size
        }

        override val bean: Any? = this@MapBinding

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
            return this@MapBinding.isEmpty()
        }

        override val bean: Any? = this@MapBinding

        override val name: String? = "empty"

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

    }

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableMap<K, V>?>) {
        if (!hasListener(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableMap<K, V>?>) {
        if (hasListener(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ChangeListener<in ObservableMap<K, V>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: MapChangeListener<in K, in V>) {
        if (!hasListener(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: MapChangeListener<in K, in V>) {
        if (hasListener(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: MapChangeListener<in K, in V>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.mapChangeListeners.contains(listener)
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

    final override fun get(): ObservableMap<K, V>? {
        if (!this.validState) {
            this.valueState = computeValue()
            this.validState = true
            this.valueState?.addListener(this.mapChangeListener)
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
            this.valueState?.addListener(this.mapChangeListener)
            this.validState = false
            invalidateProperties()
            onInvalidating()
            MapExpressionHelper.fireValueChangedEvent(this.helper)
        }
    }

    final override val valid: Boolean
        get() = this.validState

    /**
     * Calculates the current value of this binding.
     *
     * Classes extending `MapBinding` have to provide an implementation of `computeValue`.
     *
     * @return the current value
     */
    protected abstract fun computeValue(): ObservableMap<K, V>?

    /**
     * Returns a string representation of this `MapBinding` object.
     *
     * @return a string representation of this `MapBinding` object.
     */
    override fun toString(): String {
        return if (this.validState) "MapBinding [value: ${get()}]" else "MapBinding [invalid]"
    }

}