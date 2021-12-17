package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableSetValue
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.collections.SetChangeListener
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change
import io.github.vinccool96.observationskt.util.ArrayUtils

/**
 * A convenience class for creating implementations of [io.github.vinccool96.observationskt.beans.value.ObservableValue].
 * It contains all the infrastructure support for value invalidation- and change event notification.
 *
 * This implementation can handle adding and removing listeners while the observers are being notified, but it is not
 * thread-safe.
 *
 * For the `ExpressionHelperBase` that is used with the [ObservableSets][ObservableSet] created with the methods of
 * [io.github.vinccool96.observationskt.collections.ObservableCollections], please refer to
 * [io.github.vinccool96.observationskt.sun.collections.SetListenerHelper].
 */
@Suppress("CascadeIf", "UNCHECKED_CAST")
abstract class SetExpressionHelper<E>(protected val observable: ObservableSetValue<E>) : ExpressionHelperBase() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Common implementations

    protected abstract fun addListener(listener: InvalidationListener): SetExpressionHelper<E>

    protected abstract fun removeListener(listener: InvalidationListener): SetExpressionHelper<E>?

    protected abstract fun addListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E>

    protected abstract fun removeListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E>?

    protected abstract fun addListener(listener: SetChangeListener<in E>): SetExpressionHelper<E>

    protected abstract fun removeListener(listener: SetChangeListener<in E>): SetExpressionHelper<E>?

    protected abstract fun fireValueChangedEvent()

    protected abstract fun fireValueChangedEvent(change: Change<out E>)

    abstract val invalidationListeners: Array<InvalidationListener>

    abstract val changeListeners: Array<ChangeListener<in ObservableSet<E>?>>

    abstract val setChangeListeners: Array<SetChangeListener<in E>>

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementations

    private class SingleInvalidation<E>(observable: ObservableSetValue<E>, private val listener: InvalidationListener) :
            SetExpressionHelper<E>(observable) {

        override fun addListener(listener: InvalidationListener): SetExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: InvalidationListener): SetExpressionHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E> {
            return this
        }

        override fun addListener(listener: SetChangeListener<in E>): SetExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: SetChangeListener<in E>): SetExpressionHelper<E> {
            return this
        }

        override fun fireValueChangedEvent() {
            this.listener.invalidated(this.observable)
        }

        override fun fireValueChangedEvent(change: Change<out E>) {
            this.listener.invalidated(this.observable)
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf(this.listener)

        override val changeListeners: Array<ChangeListener<in ObservableSet<E>?>>
            get() = arrayOf()

        override val setChangeListeners: Array<SetChangeListener<in E>>
            get() = arrayOf()

    }

    private class SingleChange<E>(observable: ObservableSetValue<E>,
            private val listener: ChangeListener<in ObservableSet<E>?>) : SetExpressionHelper<E>(observable) {

        private var currentValue: ObservableSet<E>? = this.observable.value

        override fun addListener(listener: InvalidationListener): SetExpressionHelper<E> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): SetExpressionHelper<E> {
            return this
        }

        override fun addListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: SetChangeListener<in E>): SetExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: SetChangeListener<in E>): SetExpressionHelper<E> {
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

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf()

        override val changeListeners: Array<ChangeListener<in ObservableSet<E>?>>
            get() = arrayOf(this.listener)

        override val setChangeListeners: Array<SetChangeListener<in E>>
            get() = arrayOf()

    }

    private class SingleSetChange<E>(observable: ObservableSetValue<E>,
            private val listener: SetChangeListener<in E>) : SetExpressionHelper<E>(observable) {

        private var currentValue: ObservableSet<E>? = this.observable.value

        override fun addListener(listener: InvalidationListener): SetExpressionHelper<E> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): SetExpressionHelper<E> {
            return this
        }

        override fun addListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E> {
            return this
        }

        override fun addListener(listener: SetChangeListener<in E>): SetExpressionHelper<E> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: SetChangeListener<in E>): SetExpressionHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun fireValueChangedEvent() {
            val oldValue = this.currentValue
            this.currentValue = this.observable.value
            val current = this.currentValue
            if (current != oldValue) {
                val change = SimpleChange(this.observable)
                if (current == null) {
                    for (element in oldValue!!) {
                        this.listener.onChanged(change.setRemoved(element))
                    }
                } else if (oldValue == null) {
                    for (element in current) {
                        this.listener.onChanged(change.setAdded(element))
                    }
                } else {
                    for (element in oldValue) {
                        if (!current.contains(element)) {
                            this.listener.onChanged(change.setRemoved(element))
                        }
                    }
                    for (element in current) {
                        if (!oldValue.contains(element)) {
                            this.listener.onChanged(change.setAdded(element))
                        }
                    }
                }
            }
        }

        override fun fireValueChangedEvent(change: Change<out E>) {
            this.listener.onChanged(SimpleChange(this.observable, change))
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf()

        override val changeListeners: Array<ChangeListener<in ObservableSet<E>?>>
            get() = arrayOf()

        override val setChangeListeners: Array<SetChangeListener<in E>>
            get() = arrayOf(this.listener)

    }

    private class Generic<E> : SetExpressionHelper<E> {

        private var invalidationListenerArray: Array<InvalidationListener?> = arrayOf()

        private var changeListenerArray: Array<ChangeListener<in ObservableSet<E>?>?> = arrayOf()

        private var setChangeListenerArray: Array<SetChangeListener<in E>?> = arrayOf()

        private var invalidationSize: Int = 0

        private var changeSize: Int = 0

        private var setChangeSize: Int = 0

        private var locked: Boolean = false

        private var currentValue: ObservableSet<E>? = this.observable.value

        constructor(observable: ObservableSetValue<E>, listener0: InvalidationListener,
                listener1: InvalidationListener) :
                super(observable) {
            this.invalidationListenerArray = arrayOf(listener0, listener1)
            this.invalidationSize = 2
        }

        constructor(observable: ObservableSetValue<E>, invalidationListener: InvalidationListener,
                changeListener: ChangeListener<in ObservableSet<E>?>) : super(observable) {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.changeListenerArray = arrayOf(changeListener)
            this.invalidationSize = 1
            this.changeSize = 1
        }

        constructor(observable: ObservableSetValue<E>, invalidationListener: InvalidationListener,
                setChangeListener: SetChangeListener<in E>) : super(observable) {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.setChangeListenerArray = arrayOf(setChangeListener)
            this.invalidationSize = 1
            this.setChangeSize = 1
        }

        constructor(observable: ObservableSetValue<E>, listener0: ChangeListener<in ObservableSet<E>?>,
                listener1: ChangeListener<in ObservableSet<E>?>) : super(observable) {
            this.changeListenerArray = arrayOf(listener0, listener1)
            this.changeSize = 2
        }

        constructor(observable: ObservableSetValue<E>, changeListener: ChangeListener<in ObservableSet<E>?>,
                setChangeListener: SetChangeListener<in E>) : super(observable) {
            this.changeListenerArray = arrayOf(changeListener)
            this.setChangeListenerArray = arrayOf(setChangeListener)
            this.changeSize = 1
            this.setChangeSize = 1
        }

        constructor(observable: ObservableSetValue<E>, listener0: SetChangeListener<in E>,
                listener1: SetChangeListener<in E>) : super(observable) {
            this.setChangeListenerArray = arrayOf(listener0, listener1)
            this.setChangeSize = 2
        }

        override fun addListener(listener: InvalidationListener): SetExpressionHelper<E> {
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

        override fun removeListener(listener: InvalidationListener): SetExpressionHelper<E> {
            if (this.invalidationListenerArray.isNotEmpty()) {
                for (index in 0 until this.invalidationSize) {
                    if (listener == this.invalidationListenerArray[index]) {
                        if (this.invalidationSize == 1) {
                            if (this.changeSize == 1 && this.setChangeSize == 0) {
                                return SingleChange(this.observable, this.changeListeners[0])
                            } else if (this.changeSize == 0 && this.setChangeSize == 1) {
                                return SingleSetChange(this.observable, this.setChangeListeners[0])
                            }
                            this.invalidationListenerArray = arrayOf()
                            this.invalidationSize = 0
                        } else if (this.invalidationSize == 2 && this.changeSize == 0 && this.setChangeSize == 0) {
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

        override fun addListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E> {
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

        override fun removeListener(listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E> {
            if (this.changeListenerArray.isNotEmpty()) {
                for (index in 0 until this.changeSize) {
                    if (listener == this.changeListenerArray[index]) {
                        if (this.changeSize == 1) {
                            if (this.invalidationSize == 1 && this.setChangeSize == 0) {
                                return SingleInvalidation(this.observable, this.invalidationListeners[0])
                            } else if (this.invalidationSize == 0 && this.setChangeSize == 1) {
                                return SingleSetChange(this.observable, this.setChangeListeners[0])
                            }
                            this.changeListenerArray = arrayOf()
                            this.changeSize = 0
                        } else if (this.changeSize == 2 && this.invalidationSize == 0 && this.setChangeSize == 0) {
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

        override fun addListener(listener: SetChangeListener<in E>): SetExpressionHelper<E> {
            if (this.setChangeListenerArray.isEmpty()) {
                this.setChangeListenerArray = arrayOf(listener)
                this.setChangeSize = 1
            } else {
                val oldSize = this.setChangeListenerArray.size
                if (this.locked) {
                    val newSize = if (this.setChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                    this.setChangeListenerArray = this.setChangeListenerArray.copyOf(newSize)
                } else if (this.setChangeSize == oldSize) {
                    this.setChangeSize = trim(this.setChangeSize, this.setChangeListenerArray as Array<Any?>)
                    if (this.setChangeSize == oldSize) {
                        val newSize = if (this.setChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                        this.setChangeListenerArray = this.setChangeListenerArray.copyOf(newSize)
                    }
                }
                this.setChangeListenerArray[this.setChangeSize++] = listener
            }
            return this
        }

        override fun removeListener(listener: SetChangeListener<in E>): SetExpressionHelper<E> {
            if (this.setChangeListenerArray.isNotEmpty()) {
                for (index in 0 until this.setChangeSize) {
                    if (listener == this.setChangeListenerArray[index]) {
                        if (this.setChangeSize == 1) {
                            if (this.invalidationSize == 1 && this.changeSize == 0) {
                                return SingleInvalidation(this.observable, this.invalidationListeners[0])
                            } else if (this.invalidationSize == 0 && this.changeSize == 1) {
                                return SingleChange(this.observable, this.changeListeners[0])
                            }
                            this.setChangeListenerArray = arrayOf()
                            this.setChangeSize = 0
                        } else if (this.setChangeSize == 2 && this.invalidationSize == 0 && this.changeSize == 0) {
                            return SingleSetChange(this.observable, this.setChangeListeners[1 - index])
                        } else {
                            val numMoved = this.setChangeSize - index - 1
                            val oldListeners = this.setChangeListenerArray
                            if (this.locked) {
                                this.setChangeListenerArray = arrayOfNulls(this.setChangeListenerArray.size)
                                oldListeners.copyInto(this.setChangeListenerArray, 0, 0, index + 1)
                            }
                            if (numMoved > 0) {
                                oldListeners.copyInto(this.setChangeListenerArray, index, index + 1,
                                        this.setChangeSize)
                            }
                            this.setChangeSize--
                            if (!this.locked) {
                                this.setChangeListenerArray[this.setChangeSize] = null // Let gc do its work
                            }
                        }
                        break
                    }
                }
            }
            return this
        }

        override fun fireValueChangedEvent() {
            if (this.changeSize == 0 && this.setChangeSize == 0) {
                notifyListeners(this.currentValue, null)
            } else {
                val oldValue = this.currentValue
                this.currentValue = this.observable.value
                notifyListeners(oldValue, null)
            }
        }

        override fun fireValueChangedEvent(change: Change<out E>) {
            val mappedChange = if (this.setChangeSize != 0) SimpleChange(this.observable, change) else null
            notifyListeners(this.currentValue, mappedChange)
        }

        @Suppress("NAME_SHADOWING")
        private fun notifyListeners(oldValue: ObservableSet<E>?, change: Change<E>?) {
            var change = change
            val current = this.currentValue
            val curInvalidationList = this.invalidationListenerArray
            val curInvalidationSize = this.invalidationSize
            val curChangeList = this.changeListenerArray
            val curChangeSize = this.changeSize
            val curSetChangeList = this.setChangeListenerArray
            val curSetChangeSize = this.setChangeSize
            try {
                this.locked = true
                for (i in 0 until curInvalidationSize) {
                    curInvalidationList[i]!!.invalidated(this.observable)
                }
                if (current != oldValue || change != null) {
                    for (i in 0 until curChangeSize) {
                        curChangeList[i]!!.changed(this.observable, oldValue, current)
                    }
                    if (curSetChangeSize > 0) {
                        if (change != null) {
                            for (i in 0 until curSetChangeSize) {
                                curSetChangeList[i]!!.onChanged(change)
                            }
                        } else {
                            change = SimpleChange(this.observable)
                            if (current != oldValue) {
                                if (current == null) {
                                    for (element in oldValue!!) {
                                        for (i in 0 until curSetChangeSize) {
                                            curSetChangeList[i]!!.onChanged(change.setRemoved(element))
                                        }
                                    }
                                } else if (oldValue == null) {
                                    for (element in current) {
                                        for (i in 0 until curSetChangeSize) {
                                            curSetChangeList[i]!!.onChanged(change.setAdded(element))
                                        }
                                    }
                                } else {
                                    for (element in oldValue) {
                                        if (!current.contains(element)) {
                                            for (i in 0 until curSetChangeSize) {
                                                curSetChangeList[i]!!.onChanged(change.setRemoved(element))
                                            }
                                        }
                                    }
                                    for (element in current) {
                                        if (!oldValue.contains(element)) {
                                            for (i in 0 until curSetChangeSize) {
                                                curSetChangeList[i]!!.onChanged(change.setAdded(element))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                this.locked = false
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = ArrayUtils.copyOfNotNulls(this.invalidationListenerArray)

        override val changeListeners: Array<ChangeListener<in ObservableSet<E>?>>
            get() = ArrayUtils.copyOfNotNulls(this.changeListenerArray)

        override val setChangeListeners: Array<SetChangeListener<in E>>
            get() = ArrayUtils.copyOfNotNulls(this.setChangeListenerArray)

    }

    class SimpleChange<E> : Change<E> {

        private var old: E? = null

        private var added: E? = null

        private var addOp: Boolean = false

        constructor(set: ObservableSet<E>, source: Change<out E>) : super(set) {
            this.old = source.elementRemoved
            this.added = source.elementAdded
            this.addOp = source.wasAdded
        }

        constructor(set: ObservableSet<E>) : super(set)

        fun setRemoved(old: E): SimpleChange<E> {
            this.old = old
            this.added = null
            this.addOp = false
            return this
        }

        fun setAdded(added: E): SimpleChange<E> {
            this.old = null
            this.added = added
            this.addOp = true
            return this
        }

        override val wasAdded: Boolean
            get() = this.addOp

        override val wasRemoved: Boolean
            get() = !this.addOp

        override val elementAdded: E?
            get() = this.added

        override val elementRemoved: E?
            get() = this.old

        override fun toString(): String {
            return if (this.addOp) "added: $added" else "removed: $old"
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static methods

    companion object {

        fun <E> addListener(helper: SetExpressionHelper<E>?, observable: ObservableSetValue<E>,
                listener: InvalidationListener): SetExpressionHelper<E> {
            observable.value // validate observable
            return helper?.addListener(listener) ?: SingleInvalidation(observable, listener)
        }

        fun <E> removeListener(helper: SetExpressionHelper<E>?,
                listener: InvalidationListener): SetExpressionHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> addListener(helper: SetExpressionHelper<E>?, observable: ObservableSetValue<E>,
                listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E> {
            return helper?.addListener(listener) ?: SingleChange(observable, listener)
        }

        fun <E> removeListener(helper: SetExpressionHelper<E>?,
                listener: ChangeListener<in ObservableSet<E>?>): SetExpressionHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> addListener(helper: SetExpressionHelper<E>?, observable: ObservableSetValue<E>,
                listener: SetChangeListener<in E>): SetExpressionHelper<E> {
            return helper?.addListener(listener) ?: SingleSetChange(observable, listener)
        }

        fun <E> removeListener(helper: SetExpressionHelper<E>?,
                listener: SetChangeListener<in E>): SetExpressionHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> fireValueChangedEvent(helper: SetExpressionHelper<E>?) {
            helper?.fireValueChangedEvent()
        }

        fun <E> fireValueChangedEvent(helper: SetExpressionHelper<E>?, change: Change<out E>) {
            helper?.fireValueChangedEvent(change)
        }

    }

}