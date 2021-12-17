package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
import io.github.vinccool96.observationskt.collections.ArrayChangeListener
import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.sun.collections.NonIterableArrayChange.GenericAddRemoveChange
import io.github.vinccool96.observationskt.sun.collections.SourceAdapterArrayChange
import io.github.vinccool96.observationskt.util.ArrayUtils

/**
 * A convenience class for creating implementations of [io.github.vinccool96.observationskt.beans.value.ObservableValue].
 * It contains all the infrastructure support for value invalidation- and change event notification.
 *
 * This implementation can handle adding and removing listeners while the observers are being notified, but it is not
 * thread-safe.
 *
 * For the `ExpressionHelperBase` that is used with the [ObservableArrays][ObservableArray] created with the methods of
 * [io.github.vinccool96.observationskt.collections.ObservableCollections], please refer to
 * [io.github.vinccool96.observationskt.sun.collections.ArrayListenerHelper].
 */
@Suppress("UNCHECKED_CAST")
abstract class ArrayExpressionHelper<T> protected constructor(protected val observable: ObservableArrayValue<T>) :
        ExpressionHelperBase() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Common implementations

    protected abstract fun addListener(listener: InvalidationListener): ArrayExpressionHelper<T>

    protected abstract fun removeListener(listener: InvalidationListener): ArrayExpressionHelper<T>?

    protected abstract fun addListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T>

    protected abstract fun removeListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T>?

    protected abstract fun addListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T>

    protected abstract fun removeListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T>?

    protected abstract fun fireValueChangedEvent()

    protected abstract fun fireValueChangedEvent(change: Change<out T>)

    abstract val invalidationListeners: Array<InvalidationListener>

    abstract val changeListeners: Array<ChangeListener<in ObservableArray<T>?>>

    abstract val arrayChangeListeners: Array<ArrayChangeListener<in T>>

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementations

    private class SingleInvalidation<T>(observable: ObservableArrayValue<T>,
            private val listener: InvalidationListener) : ArrayExpressionHelper<T>(observable) {

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf(this.listener)

        override val changeListeners: Array<ChangeListener<in ObservableArray<T>?>>
            get() = arrayOf()

        override val arrayChangeListeners: Array<ArrayChangeListener<in T>>
            get() = arrayOf()

        override fun addListener(listener: InvalidationListener): ArrayExpressionHelper<T> {
            return if (this.listener == listener) this else Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: InvalidationListener): ArrayExpressionHelper<T>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T> {
            return this
        }

        override fun addListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T> {
            return this
        }

        override fun fireValueChangedEvent() {
            this.listener.invalidated(this.observable)
        }

        override fun fireValueChangedEvent(change: Change<out T>) {
            this.listener.invalidated(this.observable)
        }

    }

    private class SingleChange<T>(observable: ObservableArrayValue<T>,
            private val listener: ChangeListener<in ObservableArray<T>?>) : ArrayExpressionHelper<T>(observable) {

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf()

        override val changeListeners: Array<ChangeListener<in ObservableArray<T>?>>
            get() = arrayOf(this.listener)

        override val arrayChangeListeners: Array<ArrayChangeListener<in T>>
            get() = arrayOf()

        private var currentValue: ObservableArray<T>? = this.observable.value

        override fun addListener(listener: InvalidationListener): ArrayExpressionHelper<T> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): ArrayExpressionHelper<T> {
            return this
        }

        override fun addListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T> {
            return if (this.listener == listener) this else Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T> {
            return this
        }

        override fun fireValueChangedEvent() {
            val oldValue = this.currentValue
            this.currentValue = this.observable.value
            if (this.currentValue !== oldValue) {
                this.listener.changed(this.observable, oldValue, this.currentValue)
            }
        }

        override fun fireValueChangedEvent(change: Change<out T>) {
            this.listener.changed(this.observable, this.currentValue, this.currentValue)
        }

    }

    private class SingleArrayChange<T>(observable: ObservableArrayValue<T>,
            private val listener: ArrayChangeListener<in T>) : ArrayExpressionHelper<T>(observable) {

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf()

        override val changeListeners: Array<ChangeListener<in ObservableArray<T>?>>
            get() = arrayOf()

        override val arrayChangeListeners: Array<ArrayChangeListener<in T>>
            get() = arrayOf(this.listener)

        private var currentValue: ObservableArray<T>? = this.observable.value

        override fun addListener(listener: InvalidationListener): ArrayExpressionHelper<T> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): ArrayExpressionHelper<T> {
            return this
        }

        override fun addListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T> {
            return this
        }

        override fun addListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T> {
            return if (this.listener == listener) this else Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T>? {
            return if (this.listener == listener) null else this
        }

        override fun fireValueChangedEvent() {
            val oldValue = this.currentValue
            this.currentValue = this.observable.value
            if (this.currentValue !== oldValue) {
                val safeSize: Int = this.currentValue?.size ?: 0
                val safeOldValue = if (oldValue == null)
                    ObservableCollections.emptyObservableArray(this.observable.baseArray)
                else ObservableCollections.unmodifiableObservableArray(oldValue)
                val change = GenericAddRemoveChange(0, safeSize, safeOldValue.toTypedArray(), this.observable)
                this.listener.onChanged(change)
            }
        }

        override fun fireValueChangedEvent(change: Change<out T>) {
            this.listener.onChanged(SourceAdapterArrayChange(this.observable, change))
        }

    }

    private class Generic<T> private constructor(observable: ObservableArrayValue<T>) :
            ArrayExpressionHelper<T>(observable) {

        private var invalidationListenerArray: Array<InvalidationListener?> = arrayOf()

        private var changeListenerArray: Array<ChangeListener<in ObservableArray<T>?>?> = arrayOf()

        private var arrayChangeListenerArray: Array<ArrayChangeListener<in T>?> = arrayOf()

        private var invalidationSize: Int = 0

        private var changeSize: Int = 0

        private var arrayChangeSize: Int = 0

        private var locked: Boolean = false

        private var currentValue: ObservableArray<T>? = this.observable.value

        constructor(observable: ObservableArrayValue<T>, listener0: InvalidationListener,
                listener1: InvalidationListener) : this(observable) {
            this.invalidationListenerArray = arrayOf(listener0, listener1)
            this.invalidationSize = 2
        }

        constructor(observable: ObservableArrayValue<T>, listener0: InvalidationListener,
                listener1: ChangeListener<in ObservableArray<T>?>) : this(observable) {
            this.invalidationListenerArray = arrayOf(listener0)
            this.invalidationSize = 1
            this.changeListenerArray = arrayOf(listener1)
            this.changeSize = 1
        }

        constructor(observable: ObservableArrayValue<T>, listener0: InvalidationListener,
                listener1: ArrayChangeListener<in T>) : this(observable) {
            this.invalidationListenerArray = arrayOf(listener0)
            this.invalidationSize = 1
            this.arrayChangeListenerArray = arrayOf(listener1)
            this.arrayChangeSize = 1
        }

        constructor(observable: ObservableArrayValue<T>, listener0: ChangeListener<in ObservableArray<T>?>,
                listener1: ChangeListener<in ObservableArray<T>?>) : this(observable) {
            this.changeListenerArray = arrayOf(listener0, listener1)
            this.changeSize = 2
        }

        constructor(observable: ObservableArrayValue<T>, listener0: ChangeListener<in ObservableArray<T>?>,
                listener1: ArrayChangeListener<in T>) : this(observable) {
            this.changeListenerArray = arrayOf(listener0)
            this.changeSize = 1
            this.arrayChangeListenerArray = arrayOf(listener1)
            this.arrayChangeSize = 1
        }

        constructor(observable: ObservableArrayValue<T>, listener0: ArrayChangeListener<in T>,
                listener1: ArrayChangeListener<in T>) : this(observable) {
            this.arrayChangeListenerArray = arrayOf(listener0, listener1)
            this.arrayChangeSize = 2
        }

        override fun addListener(listener: InvalidationListener): ArrayExpressionHelper<T> {
            if (this.invalidationListenerArray.isEmpty()) {
                this.invalidationListenerArray = arrayOf(listener)
                this.invalidationSize = 1
            } else {
                val oldSize = this.invalidationListenerArray.size
                if (this.locked) {
                    val newSize = if (this.invalidationSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                    this.invalidationListenerArray = this.invalidationListenerArray.copyOf(newSize)
                } else if (this.invalidationSize == oldSize) {
                    this.invalidationSize = trim(this.invalidationSize, this.invalidationListenerArray as Array<Any?>)
                    if (this.invalidationSize == oldSize) {
                        val newSize = if (this.invalidationSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                        this.invalidationListenerArray = this.invalidationListenerArray.copyOf(newSize)
                    }
                }
                this.invalidationListenerArray[this.invalidationSize++] = listener
            }
            return this
        }

        override fun removeListener(listener: InvalidationListener): ArrayExpressionHelper<T> {
            if (this.invalidationListenerArray.isNotEmpty()) {
                for (index in 0 until this.invalidationSize) {
                    if (listener == this.invalidationListenerArray[index]) {
                        if (this.invalidationSize == 1) {
                            if (this.changeSize == 1 && this.arrayChangeSize == 0) {
                                return SingleChange(this.observable, this.changeListeners[0])
                            } else if (this.changeSize == 0 && this.arrayChangeSize == 1) {
                                return SingleArrayChange(this.observable, this.arrayChangeListeners[0])
                            }
                            this.invalidationListenerArray = arrayOf()
                            this.invalidationSize = 0
                        } else if (this.invalidationSize == 2 && this.changeSize == 0 && this.arrayChangeSize == 0) {
                            return SingleInvalidation(this.observable,
                                    this.invalidationListeners[1 - index])
                        } else {
                            val numMoved = this.invalidationSize - index - 1
                            val oldListeners = this.invalidationListenerArray
                            if (this.locked) {
                                this.invalidationListenerArray = arrayOfNulls(this.invalidationListenerArray.size)
                                oldListeners.copyInto(this.invalidationListenerArray, 0, 0, index + 1)
                            }
                            if (numMoved > 0) {
                                oldListeners.copyInto(this.invalidationListenerArray, index, index + 1,
                                        this.invalidationSize)
                            }
                            this.invalidationSize--
                            if (!this.locked) {
                                this.invalidationListenerArray[this.invalidationSize] = null // Let gc do its work
                            }
                        }
                        break
                    }
                }
            }
            return this
        }

        override fun addListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T> {
            if (this.changeListenerArray.isEmpty()) {
                this.changeListenerArray = arrayOf(listener)
                this.changeSize = 1
            } else {
                val oldSize = this.changeListenerArray.size
                if (this.locked) {
                    val newSize = if (this.changeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                    this.changeListenerArray = this.changeListenerArray.copyOf(newSize)
                } else if (this.changeSize == oldSize) {
                    this.changeSize = trim(this.changeSize, this.changeListenerArray as Array<Any?>)
                    if (this.changeSize == oldSize) {
                        val newSize = if (this.changeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                        this.changeListenerArray = this.changeListenerArray.copyOf(newSize)
                    }
                }
                this.changeListenerArray[this.changeSize++] = listener
            }
            return this
        }

        override fun removeListener(listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T> {
            if (this.changeListenerArray.isNotEmpty()) {
                for (index in 0 until this.changeSize) {
                    if (listener == this.changeListenerArray[index]) {
                        if (this.changeSize == 1) {
                            if (this.invalidationSize == 1 && this.arrayChangeSize == 0) {
                                return SingleInvalidation(this.observable, this.invalidationListeners[0])
                            } else if (this.invalidationSize == 0 && this.arrayChangeSize == 1) {
                                return SingleArrayChange(this.observable, this.arrayChangeListeners[0])
                            }
                            this.changeListenerArray = arrayOf()
                            this.changeSize = 0
                        } else if (this.changeSize == 2 && this.invalidationSize == 0 && this.arrayChangeSize == 0) {
                            return SingleChange(this.observable, this.changeListeners[1 - index])
                        } else {
                            val numMoved = this.changeSize - index - 1
                            val oldListeners = this.changeListenerArray
                            if (this.locked) {
                                this.changeListenerArray = arrayOfNulls(this.changeListenerArray.size)
                                oldListeners.copyInto(this.changeListenerArray, 0, 0, index + 1)
                            }
                            if (numMoved > 0) {
                                oldListeners.copyInto(this.changeListenerArray, index, index + 1,
                                        this.changeSize)
                            }
                            this.changeSize--
                            if (!this.locked) {
                                this.changeListenerArray[this.changeSize] = null // Let gc do its work
                            }
                        }
                        break
                    }
                }
            }
            return this
        }

        override fun addListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T> {
            if (this.arrayChangeListenerArray.isEmpty()) {
                this.arrayChangeListenerArray = arrayOf(listener)
                this.arrayChangeSize = 1
            } else {
                val oldSize = this.arrayChangeListenerArray.size
                if (this.locked) {
                    val newSize = if (this.arrayChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                    this.arrayChangeListenerArray = this.arrayChangeListenerArray.copyOf(newSize)
                } else if (this.arrayChangeSize == oldSize) {
                    this.arrayChangeSize = trim(this.arrayChangeSize, this.arrayChangeListenerArray as Array<Any?>)
                    if (this.arrayChangeSize == oldSize) {
                        val newSize = if (this.arrayChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                        this.arrayChangeListenerArray = this.arrayChangeListenerArray.copyOf(newSize)
                    }
                }
                this.arrayChangeListenerArray[this.arrayChangeSize++] = listener
            }
            return this
        }

        override fun removeListener(listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T> {
            if (this.arrayChangeListenerArray.isNotEmpty()) {
                for (index in 0 until this.arrayChangeSize) {
                    if (listener == this.arrayChangeListenerArray[index]) {
                        if (this.arrayChangeSize == 1) {
                            if (this.invalidationSize == 1 && this.changeSize == 0) {
                                return SingleInvalidation(this.observable,
                                        this.invalidationListeners[0])
                            } else if (this.invalidationSize == 0 && this.changeSize == 1) {
                                return SingleChange(this.observable, this.changeListeners[0])
                            }
                            this.arrayChangeListenerArray = arrayOf()
                            this.arrayChangeSize = 0
                        } else if (this.arrayChangeSize == 2 && this.invalidationSize == 0 && this.changeSize == 0) {
                            return SingleArrayChange(this.observable,
                                    this.arrayChangeListeners[1 - index])
                        } else {
                            val numMoved = this.arrayChangeSize - index - 1
                            val oldListeners = this.arrayChangeListenerArray
                            if (this.locked) {
                                this.arrayChangeListenerArray = arrayOfNulls(this.arrayChangeListenerArray.size)
                                oldListeners.copyInto(this.arrayChangeListenerArray, 0, 0, index + 1)
                            }
                            if (numMoved > 0) {
                                oldListeners.copyInto(this.arrayChangeListenerArray, index, index + 1,
                                        this.arrayChangeSize)
                            }
                            this.arrayChangeSize--
                            if (!this.locked) {
                                this.arrayChangeListenerArray[this.arrayChangeSize] = null // Let gc do its work
                            }
                        }
                        break
                    }
                }
            }
            return this
        }

        override fun fireValueChangedEvent() {
            if (this.changeSize == 0 && this.arrayChangeSize == 0) {
                notifyListeners(this.currentValue, null, false)
            } else {
                val oldValue = this.currentValue
                this.currentValue = this.observable.value
                if (this.currentValue !== oldValue) {
                    var change: Change<T>? = null
                    if (this.arrayChangeSize > 0) {
                        val safeOldValue = if (oldValue == null)
                            ObservableCollections.emptyObservableArray(this.observable.baseArray)
                        else ObservableCollections.unmodifiableObservableArray(oldValue)
                        change = GenericAddRemoveChange(0, safeOldValue.size, safeOldValue.toTypedArray(),
                                this.observable)
                    }
                    notifyListeners(oldValue, change, false)

                } else {
                    notifyListeners(this.currentValue, null, true)
                }
            }
        }

        override fun fireValueChangedEvent(change: Change<out T>) {
            val mappedChange = if (this.arrayChangeSize > 0) SourceAdapterArrayChange(this.observable, change) else null
            notifyListeners(this.currentValue, mappedChange, false)
        }

        private fun notifyListeners(oldValue: ObservableArray<T>?, change: Change<T>?, noChange: Boolean) {
            val curInvalidationList = this.invalidationListenerArray
            val curInvalidationSize = this.invalidationSize
            val curChangeList = this.changeListenerArray
            val curChangeSize = this.changeSize
            val curArrayChangeList = this.arrayChangeListenerArray
            val curArrayChangeSize = this.arrayChangeSize
            try {
                this.locked = true
                for (i in 0 until curInvalidationSize) {
                    curInvalidationList[i]!!.invalidated(this.observable)
                }
                if (!noChange) {
                    for (i in 0 until curChangeSize) {
                        curChangeList[i]!!.changed(this.observable, oldValue, this.currentValue)
                    }
                    if (change != null) {
                        for (i in 0 until curArrayChangeSize) {
                            change.reset()
                            curArrayChangeList[i]!!.onChanged(change)
                        }
                    }
                }
            } finally {
                this.locked = false
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = ArrayUtils.copyOfNotNulls(this.invalidationListenerArray)

        override val changeListeners: Array<ChangeListener<in ObservableArray<T>?>>
            get() = ArrayUtils.copyOfNotNulls(this.changeListenerArray)

        override val arrayChangeListeners: Array<ArrayChangeListener<in T>>
            get() = ArrayUtils.copyOfNotNulls(this.arrayChangeListenerArray)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static methods

    companion object {

        fun <T> addListener(helper: ArrayExpressionHelper<T>?, observable: ObservableArrayValue<T>,
                listener: InvalidationListener): ArrayExpressionHelper<T> {
            return helper?.addListener(listener) ?: SingleInvalidation(observable, listener)
        }

        fun <T> removeListener(helper: ArrayExpressionHelper<T>?, listener: InvalidationListener):
                ArrayExpressionHelper<T>? {
            return helper?.removeListener(listener)
        }

        fun <T> addListener(helper: ArrayExpressionHelper<T>?, observable: ObservableArrayValue<T>,
                listener: ChangeListener<in ObservableArray<T>?>): ArrayExpressionHelper<T> {
            return helper?.addListener(listener) ?: SingleChange(observable, listener)
        }

        fun <T> removeListener(helper: ArrayExpressionHelper<T>?, listener: ChangeListener<in ObservableArray<T>?>):
                ArrayExpressionHelper<T>? {
            return helper?.removeListener(listener)
        }

        fun <T> addListener(helper: ArrayExpressionHelper<T>?, observable: ObservableArrayValue<T>,
                listener: ArrayChangeListener<in T>): ArrayExpressionHelper<T> {
            return helper?.addListener(listener) ?: SingleArrayChange(observable, listener)
        }

        fun <T> removeListener(helper: ArrayExpressionHelper<T>?, listener: ArrayChangeListener<in T>):
                ArrayExpressionHelper<T>? {
            return helper?.removeListener(listener)
        }

        fun <T> fireValueChangedEvent(helper: ArrayExpressionHelper<T>?) {
            helper?.fireValueChangedEvent()
        }

        fun <T> fireValueChangedEvent(helper: ArrayExpressionHelper<T>?, change: Change<out T>) {
            if (change.from < change.to || change.sizeChanged) {
                helper?.fireValueChangedEvent(change)
            }
        }

    }

}