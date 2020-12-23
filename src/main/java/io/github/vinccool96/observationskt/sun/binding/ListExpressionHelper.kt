package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableListValue
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.NonIterableChange.GenericAddRemoveChange
import io.github.vinccool96.observationskt.sun.collections.SourceAdapterChange
import io.github.vinccool96.observationskt.util.ArrayUtils

/**
 * A convenience class for creating implementations of [io.github.vinccool96.observationskt.beans.value.ObservableValue].
 * It contains all of the infrastructure support for value invalidation- and change event notification.
 *
 * This implementation can handle adding and removing listeners while the observers are being notified, but it is not
 * thread-safe.
 *
 * For the `ExpressionHelperBase` that is used with [io.github.vinccool96.observationskt.collections.ObservableListBase],
 * please refer to [io.github.vinccool96.observationskt.sun.collections.ListListenerHelper].
 */
@Suppress("UNCHECKED_CAST", "RedundantNullableReturnType")
abstract class ListExpressionHelper<E> protected constructor(protected val observable: ObservableListValue<E>) :
        ExpressionHelperBase() {

    protected abstract fun addListener(listener: InvalidationListener): ListExpressionHelper<E>

    protected abstract fun removeListener(listener: InvalidationListener): ListExpressionHelper<E>?

    protected abstract fun addListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E>

    protected abstract fun removeListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E>?

    protected abstract fun addListener(listener: ListChangeListener<in E>): ListExpressionHelper<E>

    protected abstract fun removeListener(listener: ListChangeListener<in E>): ListExpressionHelper<E>?

    protected abstract fun fireValueChangedEvent()

    protected abstract fun fireValueChangedEvent(change: Change<out E>)

    abstract val invalidationListeners: Array<InvalidationListener>

    abstract val changeListeners: Array<ChangeListener<in ObservableList<E>?>>

    abstract val listChangeListeners: Array<ListChangeListener<in E>>

    private class SingleInvalidation<E>(observable: ObservableListValue<E>,
            private val listener: InvalidationListener) : ListExpressionHelper<E>(observable) {

        override val invalidationListeners: Array<InvalidationListener> = arrayOf(this.listener)

        override val changeListeners: Array<ChangeListener<in ObservableList<E>?>> = arrayOf()

        override val listChangeListeners: Array<ListChangeListener<in E>> = arrayOf()

        override fun addListener(listener: InvalidationListener): ListExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: InvalidationListener): ListExpressionHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E>? {
            return this
        }

        override fun addListener(listener: ListChangeListener<in E>): ListExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ListChangeListener<in E>): ListExpressionHelper<E>? {
            return this
        }

        override fun fireValueChangedEvent() {
            this.listener.invalidated(this.observable)
        }

        override fun fireValueChangedEvent(change: Change<out E>) {
            this.listener.invalidated(this.observable)
        }

    }

    private class SingleChange<E>(observable: ObservableListValue<E>,
            private val listener: ChangeListener<in ObservableList<E>?>) : ListExpressionHelper<E>(observable) {

        override val invalidationListeners: Array<InvalidationListener> = arrayOf()

        override val changeListeners: Array<ChangeListener<in ObservableList<E>?>> = arrayOf(this.listener)

        override val listChangeListeners: Array<ListChangeListener<in E>> = arrayOf()

        private var currentValue: ObservableList<E>? = this.observable.value

        override fun addListener(listener: InvalidationListener): ListExpressionHelper<E> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): ListExpressionHelper<E>? {
            return this
        }

        override fun addListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: ListChangeListener<in E>): ListExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ListChangeListener<in E>): ListExpressionHelper<E>? {
            return this
        }

        override fun fireValueChangedEvent() {
            val oldValue = this.currentValue
            this.currentValue = this.observable.value
            if (this.currentValue !== oldValue) {
                this.listener.changed(this.observable, oldValue, this.currentValue)
            }
        }

        override fun fireValueChangedEvent(change: Change<out E>) {
            this.listener.changed(this.observable, this.currentValue, this.currentValue)
        }

    }

    private class SingleListChange<E>(observable: ObservableListValue<E>,
            private val listener: ListChangeListener<in E>) : ListExpressionHelper<E>(observable) {

        override val invalidationListeners: Array<InvalidationListener> = arrayOf()

        override val changeListeners: Array<ChangeListener<in ObservableList<E>?>> = arrayOf()

        override val listChangeListeners: Array<ListChangeListener<in E>> = arrayOf(this.listener)

        private var currentValue: ObservableList<E>? = this.observable.value

        override fun addListener(listener: InvalidationListener): ListExpressionHelper<E> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): ListExpressionHelper<E>? {
            return this
        }

        override fun addListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E>? {
            return this
        }

        override fun addListener(listener: ListChangeListener<in E>): ListExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ListChangeListener<in E>): ListExpressionHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun fireValueChangedEvent() {
            val oldValue = this.currentValue
            this.currentValue = this.observable.value
            if (this.currentValue !== oldValue) {
                val safeSize: Int = if (this.currentValue == null) 0 else this.currentValue!!.size
                val safeOldValue = if (oldValue == null) ObservableCollections.emptyObservableList() else
                    ObservableCollections.unmodifiableObservableList(oldValue)
                val change: Change<E> = GenericAddRemoveChange(0, safeSize, safeOldValue, this.observable)
                this.listener.onChanged(change)
            }
        }

        override fun fireValueChangedEvent(change: Change<out E>) {
            this.listener.onChanged(SourceAdapterChange(this.observable, change))
        }

    }

    private class Generic<E> private constructor(observable: ObservableListValue<E>) :
            ListExpressionHelper<E>(observable) {

        private var invalidationListenerArray: Array<InvalidationListener?> = arrayOf()

        private var changeListenerArray: Array<ChangeListener<in ObservableList<E>?>?> = arrayOf()

        private var listChangeListenerArray: Array<ListChangeListener<in E>?> = arrayOf()

        private var invalidationSize: Int = 0

        private var changeSize: Int = 0

        private var listChangeSize: Int = 0

        private var locked: Boolean = false

        private var currentValue: ObservableList<E>? = observable.value

        override val invalidationListeners: Array<InvalidationListener>
            get() = ArrayUtils.copyOfNotNulls(this.invalidationListenerArray)

        override val changeListeners: Array<ChangeListener<in ObservableList<E>?>>
            get() = ArrayUtils.copyOfNotNulls(this.changeListenerArray)

        override val listChangeListeners: Array<ListChangeListener<in E>>
            get() = ArrayUtils.copyOfNotNulls(this.listChangeListenerArray)

        constructor(observable: ObservableListValue<E>, listener0: InvalidationListener,
                listener1: InvalidationListener) : this(observable) {
            this.invalidationListenerArray = arrayOf(listener0, listener1)
            this.invalidationSize = 2
        }

        constructor(observable: ObservableListValue<E>, listener0: ChangeListener<in ObservableList<E>?>,
                listener1: ChangeListener<in ObservableList<E>?>) : this(observable) {
            this.changeListenerArray = arrayOf(listener0, listener1)
            this.changeSize = 2
        }

        constructor(observable: ObservableListValue<E>, listener0: ListChangeListener<in E>,
                listener1: ListChangeListener<in E>) : this(observable) {
            this.listChangeListenerArray = arrayOf(listener0, listener1)
            this.listChangeSize = 2
        }

        constructor(observable: ObservableListValue<E>, invalidationListener: InvalidationListener,
                changeListener: ChangeListener<in ObservableList<E>?>) : this(observable) {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.invalidationSize = 1
            this.changeListenerArray = arrayOf(changeListener)
            this.changeSize = 1
        }

        constructor(observable: ObservableListValue<E>, invalidationListener: InvalidationListener,
                listChangeListener: ListChangeListener<in E>) : this(observable) {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.invalidationSize = 1
            this.listChangeListenerArray = arrayOf(listChangeListener)
            this.listChangeSize = 1
        }

        constructor(observable: ObservableListValue<E>, changeListener: ChangeListener<in ObservableList<E>?>,
                listChangeListener: ListChangeListener<in E>) : this(observable) {
            this.changeListenerArray = arrayOf(changeListener)
            this.changeSize = 1
            this.listChangeListenerArray = arrayOf(listChangeListener)
            this.listChangeSize = 1
        }

        override fun addListener(listener: InvalidationListener): ListExpressionHelper<E> {
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

        override fun removeListener(listener: InvalidationListener): ListExpressionHelper<E>? {
            if (this.invalidationListenerArray.isNotEmpty()) {
                for (index in 0 until this.invalidationSize) {
                    if (listener == this.invalidationListenerArray[index]) {
                        if (this.invalidationSize == 1) {
                            if (this.changeSize == 1 && this.listChangeSize == 0) {
                                return SingleChange(this.observable, this.changeListeners[0])
                            } else if (this.changeSize == 0 && this.listChangeSize == 1) {
                                return SingleListChange(this.observable, this.listChangeListeners[0])
                            }
                            this.invalidationListenerArray = arrayOf()
                            this.invalidationSize = 0
                        } else if (this.invalidationSize == 2 && this.changeSize == 0 && this.listChangeSize == 0) {
                            return SingleInvalidation(this.observable, this.invalidationListeners[1 - index])
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

        override fun addListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E> {
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

        override fun removeListener(listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E>? {
            if (this.changeListenerArray.isNotEmpty()) {
                for (index in 0 until this.changeSize) {
                    if (listener == this.changeListenerArray[index]) {
                        if (this.changeSize == 1) {
                            if (this.invalidationSize == 1 && this.listChangeSize == 0) {
                                return SingleInvalidation(this.observable, this.invalidationListeners[0])
                            } else if (this.invalidationSize == 0 && this.listChangeSize == 1) {
                                return SingleListChange(this.observable, this.listChangeListeners[0])
                            }
                            this.changeListenerArray = arrayOf()
                            this.changeSize = 0
                        } else if (this.changeSize == 2 && this.invalidationSize == 0 && this.listChangeSize == 0) {
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

        override fun addListener(listener: ListChangeListener<in E>): ListExpressionHelper<E> {
            if (this.listChangeListenerArray.isEmpty()) {
                this.listChangeListenerArray = arrayOf(listener)
                this.listChangeSize = 1
            } else {
                val oldSize = this.listChangeListenerArray.size
                if (this.locked) {
                    val newSize = if (this.listChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                    this.listChangeListenerArray = this.listChangeListenerArray.copyOf(newSize)
                } else if (this.listChangeSize == oldSize) {
                    this.listChangeSize = trim(this.listChangeSize, this.listChangeListenerArray as Array<Any?>)
                    if (this.listChangeSize == oldSize) {
                        val newSize = if (this.listChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                        this.listChangeListenerArray = this.listChangeListenerArray.copyOf(newSize)
                    }
                }
                this.listChangeListenerArray[this.listChangeSize++] = listener
            }
            return this
        }

        override fun removeListener(listener: ListChangeListener<in E>): ListExpressionHelper<E>? {
            if (this.listChangeListenerArray.isNotEmpty()) {
                for (index in 0 until this.changeSize) {
                    if (listener == this.listChangeListenerArray[index]) {
                        if (this.listChangeSize == 1) {
                            if (this.invalidationSize == 1 && this.changeSize == 0) {
                                return SingleInvalidation(this.observable, this.invalidationListeners[0])
                            } else if (this.invalidationSize == 0 && this.changeSize == 1) {
                                return SingleChange(this.observable, this.changeListeners[0])
                            }
                            this.listChangeListenerArray = arrayOf()
                            this.listChangeSize = 0
                        } else if (this.listChangeSize == 2 && this.invalidationSize == 0 && this.changeSize == 0) {
                            return SingleListChange(this.observable, this.listChangeListeners[1 - index])
                        } else {
                            val numMoved = this.listChangeSize - index - 1
                            val oldListeners = this.listChangeListenerArray
                            if (this.locked) {
                                this.listChangeListenerArray = arrayOfNulls(this.listChangeListenerArray.size)
                                oldListeners.copyInto(this.listChangeListenerArray, 0, 0, index + 1)
                            }
                            if (numMoved > 0) {
                                oldListeners.copyInto(this.listChangeListenerArray, index, index + 1,
                                        this.listChangeSize)
                            }
                            this.listChangeSize--
                            if (!this.locked) {
                                this.listChangeListenerArray[this.listChangeSize] = null // Let gc do its work
                            }
                        }
                        break
                    }
                }
            }
            return this
        }

        override fun fireValueChangedEvent() {
            if (this.changeSize == 0 && this.listChangeSize == 0) {
                notifyListeners(this.currentValue, null, false)
            } else {
                val oldValue = this.currentValue
                this.currentValue = this.observable.value
                if (this.currentValue !== oldValue) {
                    var change: Change<E>? = null
                    if (this.listChangeSize > 0) {
                        val safeOldValue = if (oldValue == null) ObservableCollections.emptyObservableList() else
                            ObservableCollections.unmodifiableObservableList(oldValue)
                        change = GenericAddRemoveChange(0, safeOldValue.size, safeOldValue, this.observable)
                    }
                    notifyListeners(oldValue, change, false)
                } else {
                    notifyListeners(this.currentValue, null, true)
                }
            }
        }

        override fun fireValueChangedEvent(change: Change<out E>) {
            val mappedChange = if (this.listChangeSize != 0) SourceAdapterChange(this.observable, change) else null
            notifyListeners(this.currentValue, mappedChange, false)
        }

        private fun notifyListeners(oldValue: ObservableList<E>?, change: Change<E>?, noChange: Boolean) {
            val curInvalidationList = this.invalidationListenerArray
            val curInvalidationSize = this.invalidationSize
            val curChangeList = this.changeListenerArray
            val curChangeSize = this.changeSize
            val curListChangeList = this.listChangeListenerArray
            val curListChangeSize = this.listChangeSize
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
                        for (i in 0 until curListChangeSize) {
                            change.reset()
                            curListChangeList[i]!!.onChanged(change)
                        }
                    }
                }
            } finally {
                this.locked = false
            }
        }

    }

    companion object {

        fun <E> addListener(helper: ListExpressionHelper<E>?, observable: ObservableListValue<E>,
                listener: InvalidationListener): ListExpressionHelper<E> {
            observable.value // validate observable
            return helper?.addListener(listener) ?: SingleInvalidation(observable, listener)
        }

        fun <E> removeListener(helper: ListExpressionHelper<E>?,
                listener: InvalidationListener): ListExpressionHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> addListener(helper: ListExpressionHelper<E>?, observable: ObservableListValue<E>,
                listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E> {
            return helper?.addListener(listener) ?: SingleChange(observable, listener)
        }

        fun <E> removeListener(helper: ListExpressionHelper<E>?,
                listener: ChangeListener<in ObservableList<E>?>): ListExpressionHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> addListener(helper: ListExpressionHelper<E>?, observable: ObservableListValue<E>,
                listener: ListChangeListener<in E>): ListExpressionHelper<E> {
            return helper?.addListener(listener) ?: SingleListChange(observable, listener)
        }

        fun <E> removeListener(helper: ListExpressionHelper<E>?,
                listener: ListChangeListener<in E>): ListExpressionHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> fireValueChangedEvent(helper: ListExpressionHelper<E>?) {
            helper?.fireValueChangedEvent()
        }

        fun <E> fireValueChangedEvent(helper: ListExpressionHelper<E>?, change: Change<out E>) {
            helper?.fireValueChangedEvent(change)
        }

    }

}