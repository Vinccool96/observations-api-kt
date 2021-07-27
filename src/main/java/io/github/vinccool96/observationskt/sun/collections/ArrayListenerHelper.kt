package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.ArrayChangeListener
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.sun.binding.ExpressionHelperBase
import io.github.vinccool96.observationskt.util.ArrayUtils

@Suppress("DuplicatedCode", "UNCHECKED_CAST")
abstract class ArrayListenerHelper<T : ObservableArray<T>>(protected val observable: T) : ExpressionHelperBase() {

    protected abstract fun addListener(listener: InvalidationListener): ArrayListenerHelper<T>

    protected abstract fun removeListener(listener: InvalidationListener): ArrayListenerHelper<T>?

    protected abstract fun addListener(listener: ArrayChangeListener<T>): ArrayListenerHelper<T>

    protected abstract fun removeListener(listener: ArrayChangeListener<T>): ArrayListenerHelper<T>?

    protected abstract fun fireValueChangedEvent(sizeChanged: Boolean, from: Int, to: Int)

    abstract val invalidationListeners: Array<InvalidationListener>

    abstract val arrayChangeListeners: Array<ArrayChangeListener<T>>

    private class SingleInvalidation<T : ObservableArray<T>>(observable: T,
            private val listener: InvalidationListener) :
            ArrayListenerHelper<T>(observable) {

        override fun addListener(listener: InvalidationListener): ArrayListenerHelper<T> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: InvalidationListener): ArrayListenerHelper<T>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: ArrayChangeListener<T>): ArrayListenerHelper<T> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ArrayChangeListener<T>): ArrayListenerHelper<T>? {
            return this
        }

        override fun fireValueChangedEvent(sizeChanged: Boolean, from: Int, to: Int) {
            try {
                this.listener.invalidated(this.observable)
            } catch (T: Exception) {
                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), T)
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf(this.listener)

        override val arrayChangeListeners: Array<ArrayChangeListener<T>>
            get() = emptyArray()

    }

    private class SingleArrayChange<T : ObservableArray<T>>(observable: T,
            private val listener: ArrayChangeListener<T>) : ArrayListenerHelper<T>(observable) {

        override fun addListener(listener: InvalidationListener): ArrayListenerHelper<T> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): ArrayListenerHelper<T>? {
            return this
        }

        override fun addListener(listener: ArrayChangeListener<T>): ArrayListenerHelper<T> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ArrayChangeListener<T>): ArrayListenerHelper<T>? {
            return if (this.listener == listener) null else this
        }

        override fun fireValueChangedEvent(sizeChanged: Boolean, from: Int, to: Int) {
            try {
                this.listener.onChanged(this.observable, sizeChanged, from, to)
            } catch (T: Exception) {
                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), T)
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = emptyArray()

        override val arrayChangeListeners: Array<ArrayChangeListener<T>>
            get() = arrayOf(this.listener)

    }

    private class Generic<T : ObservableArray<T>> : ArrayListenerHelper<T> {

        private var invalidationListenerArray: Array<InvalidationListener?>

        private var arrayChangeListenerArray: Array<ArrayChangeListener<T>?>

        private var invalidationSize: Int

        private var arrayChangeSize: Int

        private var locked: Boolean = false

        constructor(observable: T, listener0: InvalidationListener, listener1: InvalidationListener) :
                super(observable) {
            this.invalidationListenerArray = arrayOf(listener0, listener1)
            this.arrayChangeListenerArray = emptyArray()
            this.invalidationSize = 2
            this.arrayChangeSize = 0
        }

        constructor(observable: T, listener0: ArrayChangeListener<T>, listener1: ArrayChangeListener<T>) :
                super(observable) {
            this.invalidationListenerArray = emptyArray()
            this.arrayChangeListenerArray = arrayOf(listener0, listener1)
            this.invalidationSize = 0
            this.arrayChangeSize = 2
        }

        constructor(observable: T, invalidationListener: InvalidationListener,
                listChangeListener: ArrayChangeListener<T>) : super(observable) {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.arrayChangeListenerArray = arrayOf(listChangeListener)
            this.invalidationSize = 1
            this.arrayChangeSize = 1
        }

        override fun addListener(listener: InvalidationListener): ArrayListenerHelper<T> {
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

        override fun removeListener(listener: InvalidationListener): ArrayListenerHelper<T>? {
            for (index in 0 until this.invalidationSize) {
                if (listener == this.invalidationListenerArray[index]) {
                    if (this.invalidationSize == 1) {
                        if (this.arrayChangeSize == 1) {
                            return SingleArrayChange(this.observable, this.arrayChangeListeners[0])
                        }
                        this.invalidationListenerArray = emptyArray()
                        this.invalidationSize = 0
                    } else if (this.invalidationSize == 2 && this.arrayChangeSize == 0) {
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
            return this
        }

        override fun addListener(listener: ArrayChangeListener<T>): ArrayListenerHelper<T> {
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

        override fun removeListener(listener: ArrayChangeListener<T>): ArrayListenerHelper<T>? {
            for (index in 0 until this.arrayChangeSize) {
                if (listener == this.arrayChangeListenerArray[index]) {
                    if (this.arrayChangeSize == 1) {
                        if (this.invalidationSize == 1) {
                            return SingleInvalidation(this.observable, this.invalidationListeners[0])
                        }
                        this.arrayChangeListenerArray = emptyArray()
                        this.arrayChangeSize = 0
                    } else if (this.arrayChangeSize == 2 && this.invalidationSize == 0) {
                        return SingleArrayChange(this.observable, this.arrayChangeListeners[1 - index])
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
            return this
        }

        override fun fireValueChangedEvent(sizeChanged: Boolean, from: Int, to: Int) {
            val curInvalidationList = this.invalidationListenerArray
            val curInvalidationSize = this.invalidationSize
            val curArrayChangeList = this.arrayChangeListenerArray
            val curArrayChangeSize = this.arrayChangeSize

            try {
                this.locked = true
                for (i in 0 until curInvalidationSize) {
                    try {
                        curInvalidationList[i]?.invalidated(this.observable)
                    } catch (T: Exception) {
                        Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), T)
                    }
                }
                for (i in 0 until curArrayChangeSize) {
                    try {
                        curArrayChangeList[i]?.onChanged(this.observable, sizeChanged, from, to)
                    } catch (T: Exception) {
                        Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), T)
                    }
                }
            } finally {
                this.locked = false
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = ArrayUtils.copyOfNotNulls(this.invalidationListenerArray)

        override val arrayChangeListeners: Array<ArrayChangeListener<T>>
            get() = ArrayUtils.copyOfNotNulls(this.arrayChangeListenerArray)

    }

    companion object {

        fun <T : ObservableArray<T>> addListener(helper: ArrayListenerHelper<T>?, observable: T,
                listener: InvalidationListener): ArrayListenerHelper<T> {
            return helper?.addListener(listener) ?: SingleInvalidation(observable, listener)
        }

        fun <T : ObservableArray<T>> removeListener(helper: ArrayListenerHelper<T>?,
                listener: InvalidationListener): ArrayListenerHelper<T>? {
            return helper?.removeListener(listener)
        }

        fun <T : ObservableArray<T>> addListener(helper: ArrayListenerHelper<T>?, observable: T,
                listener: ArrayChangeListener<T>): ArrayListenerHelper<T> {
            return helper?.addListener(listener) ?: SingleArrayChange(observable, listener)
        }

        fun <T : ObservableArray<T>> removeListener(helper: ArrayListenerHelper<T>?,
                listener: ArrayChangeListener<T>): ArrayListenerHelper<T>? {
            return helper?.removeListener(listener)
        }

        fun <T : ObservableArray<T>> fireValueChangedEvent(helper: ArrayListenerHelper<T>?, sizeChanged: Boolean,
                from: Int, to: Int) {
            if (from < to || sizeChanged) {
                helper?.fireValueChangedEvent(sizeChanged, from, to)
            }
        }

        fun <T : ObservableArray<T>> hasListeners(helper: ArrayListenerHelper<T>?): Boolean {
            return helper != null
        }

    }
}