package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.util.ArrayUtils

abstract class ExpressionHelper<T>(protected val observable: ObservableValue<T>) : ExpressionHelperBase() {

    protected abstract fun addListener(listener: InvalidationListener): ExpressionHelper<T>

    protected abstract fun removeListener(listener: InvalidationListener): ExpressionHelper<T>?

    protected abstract fun addListener(listener: ChangeListener<in T>): ExpressionHelper<T>

    protected abstract fun removeListener(listener: ChangeListener<in T>): ExpressionHelper<T>?

    protected abstract fun fireValueChangedEvent()

    abstract val invalidationListeners: Array<InvalidationListener>

    abstract val changeListeners: Array<ChangeListener<in T>>

    private class SingleInvalidation<T>(observable: ObservableValue<T>,
            private val listener: InvalidationListener) : ExpressionHelper<T>(observable) {

        override fun addListener(listener: InvalidationListener): ExpressionHelper<T> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: InvalidationListener): ExpressionHelper<T>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: ChangeListener<in T>): ExpressionHelper<T> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in T>): ExpressionHelper<T>? {
            return this
        }

        override fun fireValueChangedEvent() {
            try {
                this.listener.invalidated(this.observable)
            } catch (e: Exception) {
                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf(this.listener)

        override val changeListeners: Array<ChangeListener<in T>>
            get() = emptyArray()

    }

    private class SingleChange<T>(observable: ObservableValue<T>, private val listener: ChangeListener<in T>) :
            ExpressionHelper<T>(observable) {

        private var currentValue: T = this.observable.value

        override fun addListener(listener: InvalidationListener): ExpressionHelper<T> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): ExpressionHelper<T>? {
            return this
        }

        override fun addListener(listener: ChangeListener<in T>): ExpressionHelper<T> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in T>): ExpressionHelper<T>? {
            return if (this.listener == listener) null else this
        }

        override fun fireValueChangedEvent() {
            val oldValue = this.currentValue
            this.currentValue = this.observable.value
            val changed = if (this.currentValue == null) oldValue != null else this.currentValue != oldValue
            if (changed) {
                try {
                    this.listener.changed(this.observable, oldValue!!, this.currentValue)
                } catch (e: Exception) {
                    Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                }
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = emptyArray()

        override val changeListeners: Array<ChangeListener<in T>>
            get() = arrayOf(this.listener)

    }

    @Suppress("DuplicatedCode")
    private class Generic<T> : ExpressionHelper<T> {

        private var invalidationListenerArray: Array<InvalidationListener?>

        private var changeListenerArray: Array<ChangeListener<in T>?>

        private var invalidationSize: Int

        private var changeSize: Int

        private var locked: Boolean = false

        private var currentValue: T

        constructor(observable: ObservableValue<T>, listener0: InvalidationListener,
                listener1: InvalidationListener) : super(observable) {
            this.invalidationListenerArray = arrayOf(listener0, listener1)
            this.changeListenerArray = emptyArray()
            this.invalidationSize = 2
            this.changeSize = 0
            this.currentValue = observable.value
        }

        constructor(observable: ObservableValue<T>, listener0: ChangeListener<in T>,
                listener1: ChangeListener<in T>) : super(observable) {
            this.invalidationListenerArray = emptyArray()
            this.changeListenerArray = arrayOf(listener0, listener1)
            this.invalidationSize = 0
            this.changeSize = 2
            this.currentValue = observable.value
        }

        constructor(observable: ObservableValue<T>, invalidationListener: InvalidationListener,
                changeListener: ChangeListener<in T>) : super(observable) {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.changeListenerArray = arrayOf(changeListener)
            this.invalidationSize = 1
            this.changeSize = 1
            this.currentValue = observable.value
        }

        @Suppress("UNCHECKED_CAST")
        override fun addListener(listener: InvalidationListener): ExpressionHelper<T> {
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

        override fun removeListener(listener: InvalidationListener): ExpressionHelper<T>? {
            for (index in 0 until this.invalidationSize) {
                if (listener == this.invalidationListenerArray[index]) {
                    if (this.invalidationSize == 1) {
                        if (this.changeSize == 1) {
                            return SingleChange(this.observable, this.changeListeners[0])
                        }
                        this.invalidationListenerArray = emptyArray()
                        this.invalidationSize = 0
                    } else if (this.invalidationSize == 2 && this.changeSize == 0) {
                        return SingleInvalidation(this.observable, this.invalidationListeners[1 - index])
                    } else {
                        val numMoved = this.invalidationSize - index - 1
                        val oldListeners = this.invalidationListenerArray
                        if (this.locked) {
                            this.invalidationListenerArray = arrayOfNulls(this.invalidationListenerArray.size)
                            System.arraycopy(oldListeners, 0, this.invalidationListenerArray, 0, index)
                        }
                        if (numMoved > 0) {
                            System.arraycopy(oldListeners, index + 1, this.invalidationListenerArray, index, numMoved)
                        }
                        this.invalidationSize--
                        if (!this.locked) {
                            this.invalidationListenerArray[this.invalidationSize] = null // Let gc do its work
                        }
                    }
                    break
                }
            }
            return this
        }

        @Suppress("UNCHECKED_CAST")
        override fun addListener(listener: ChangeListener<in T>): ExpressionHelper<T> {
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

        override fun removeListener(listener: ChangeListener<in T>): ExpressionHelper<T>? {
            for (index in 0 until this.changeSize) {
                if (listener == this.changeListenerArray[index]) {
                    if (this.changeSize == 1) {
                        if (this.invalidationSize == 1) {
                            return SingleInvalidation(this.observable, this.invalidationListeners[0])
                        }
                        this.changeListenerArray = emptyArray()
                        this.changeSize = 0
                    } else if (this.changeSize == 2 && this.invalidationSize == 0) {
                        return SingleChange(this.observable, this.changeListeners[1 - index])
                    } else {
                        val numMoved = this.changeSize - index - 1
                        val oldListeners = this.changeListenerArray
                        if (this.locked) {
                            this.changeListenerArray = arrayOfNulls(this.changeListenerArray.size)
                            System.arraycopy(oldListeners, 0, this.changeListenerArray, 0, index)
                        }
                        if (numMoved > 0) {
                            System.arraycopy(oldListeners, index + 1, this.changeListenerArray, index, numMoved)
                        }
                        this.changeSize--
                        if (!this.locked) {
                            this.changeListenerArray[this.changeSize] = null // Let gc do its work
                        }
                    }
                    break
                }
            }
            return this
        }

        override fun fireValueChangedEvent() {
            val curInvalidationList = this.invalidationListenerArray
            val curInvalidationSize = this.invalidationSize
            val curChangeList = this.changeListenerArray
            val curChangeSize = this.changeSize

            try {
                this.locked = true
                for (i in 0 until curInvalidationSize) {
                    try {
                        curInvalidationList[i]?.invalidated(this.observable)
                    } catch (e: Exception) {
                        Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                    }
                }
                if (curChangeSize > 0) {
                    val oldValue = this.currentValue
                    this.currentValue = this.observable.value
                    val changed = if (this.currentValue == null) oldValue != null else currentValue != oldValue
                    if (changed) {
                        for (i in 0 until curChangeSize) {
                            try {
                                curChangeList[i]?.changed(this.observable, oldValue, this.currentValue)
                            } catch (e: Exception) {
                                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(
                                        Thread.currentThread(), e)
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

        override val changeListeners: Array<ChangeListener<in T>>
            get() = ArrayUtils.copyOfNotNulls(this.changeListenerArray)

    }

    companion object {

        fun <T> addListener(helper: ExpressionHelper<T>?, observable: ObservableValue<T>,
                listener: InvalidationListener): ExpressionHelper<T> {
            observable.value // validate
            return helper?.addListener(listener) ?: SingleInvalidation(observable, listener)
        }

        fun <T> removeListener(helper: ExpressionHelper<T>?, listener: InvalidationListener): ExpressionHelper<T>? {
            return helper?.removeListener(listener)
        }

        fun <T> addListener(helper: ExpressionHelper<T>?, observable: ObservableValue<T>,
                listener: ChangeListener<in T>): ExpressionHelper<T> {
            return helper?.addListener(listener) ?: SingleChange(observable, listener)
        }

        fun <T> removeListener(helper: ExpressionHelper<T>?, listener: ChangeListener<in T>): ExpressionHelper<T>? {
            return helper?.removeListener(listener)
        }

        fun <T> fireValueChangedEvent(helper: ExpressionHelper<T>?) {
            helper?.fireValueChangedEvent()
        }

    }
}