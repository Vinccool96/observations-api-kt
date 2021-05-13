package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.SetChangeListener
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change
import io.github.vinccool96.observationskt.sun.binding.ExpressionHelperBase
import io.github.vinccool96.observationskt.util.ArrayUtils

@Suppress("CascadeIf", "UNCHECKED_CAST")
abstract class SetListenerHelper<E> : ExpressionHelperBase() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Common implementations

    protected abstract fun addListener(listener: InvalidationListener): SetListenerHelper<E>

    protected abstract fun removeListener(listener: InvalidationListener): SetListenerHelper<E>?

    protected abstract fun addListener(listener: SetChangeListener<in E>): SetListenerHelper<E>

    protected abstract fun removeListener(listener: SetChangeListener<in E>): SetListenerHelper<E>?

    protected abstract fun fireValueChangedEvent(change: Change<out E>)

    abstract val invalidationListeners: Array<InvalidationListener>

    abstract val setChangeListeners: Array<SetChangeListener<in E>>

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementations

    private class SingleInvalidation<E>(private val listener: InvalidationListener) : SetListenerHelper<E>() {

        override fun addListener(listener: InvalidationListener): SetListenerHelper<E> {
            return Generic(this.listener, listener)
        }

        override fun removeListener(listener: InvalidationListener): SetListenerHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: SetChangeListener<in E>): SetListenerHelper<E> {
            return Generic(this.listener, listener)
        }

        override fun removeListener(listener: SetChangeListener<in E>): SetListenerHelper<E> {
            return this
        }

        override fun fireValueChangedEvent(change: Change<out E>) {
            try {
                this.listener.invalidated(change.set)
            } catch (e: Exception) {
                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf(this.listener)

        override val setChangeListeners: Array<SetChangeListener<in E>>
            get() = arrayOf()

    }

    private class SingleChange<E>(private val listener: SetChangeListener<in E>) : SetListenerHelper<E>() {

        override fun addListener(listener: InvalidationListener): SetListenerHelper<E> {
            return Generic(listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): SetListenerHelper<E> {
            return this
        }

        override fun addListener(listener: SetChangeListener<in E>): SetListenerHelper<E> {
            return Generic(this.listener, listener)
        }

        override fun removeListener(listener: SetChangeListener<in E>): SetListenerHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun fireValueChangedEvent(change: Change<out E>) {
            try {
                this.listener.onChanged(change)
            } catch (e: Exception) {
                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf()

        override val setChangeListeners: Array<SetChangeListener<in E>>
            get() = arrayOf(this.listener)

    }

    private class Generic<E> : SetListenerHelper<E> {

        private var invalidationListenerArray: Array<InvalidationListener?> = arrayOf()

        private var changeListenerArray: Array<SetChangeListener<in E>?> = arrayOf()

        private var invalidationSize: Int = 0

        private var changeSize: Int = 0

        private var locked: Boolean = false

        constructor(listener0: InvalidationListener, listener1: InvalidationListener) : super() {
            this.invalidationListenerArray = arrayOf(listener0, listener1)
            this.invalidationSize = 2
        }

        constructor(invalidationListener: InvalidationListener, setChangeListener: SetChangeListener<in E>) : super() {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.changeListenerArray = arrayOf(setChangeListener)
            this.invalidationSize = 1
            this.changeSize = 1
        }

        constructor(listener0: SetChangeListener<in E>, listener1: SetChangeListener<in E>) : super() {
            this.changeListenerArray = arrayOf(listener0, listener1)
            this.changeSize = 2
        }

        override fun addListener(listener: InvalidationListener): SetListenerHelper<E> {
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

        override fun removeListener(listener: InvalidationListener): SetListenerHelper<E> {
            if (this.invalidationListenerArray.isNotEmpty()) {
                for (index in 0 until this.invalidationSize) {
                    if (listener == this.invalidationListenerArray[index]) {
                        if (this.invalidationSize == 1) {
                            if (this.changeSize == 1) {
                                return SingleChange(this.setChangeListeners[0])
                            }
                            this.invalidationListenerArray = arrayOf()
                            this.invalidationSize = 0
                        } else if (this.invalidationSize == 2 && this.changeSize == 0) {
                            return SingleInvalidation(this.invalidationListeners[1 - index])
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

        override fun addListener(listener: SetChangeListener<in E>): SetListenerHelper<E> {
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

        override fun removeListener(listener: SetChangeListener<in E>): SetListenerHelper<E> {
            if (this.changeListenerArray.isNotEmpty()) {
                for (index in 0 until this.changeSize) {
                    if (listener == this.changeListenerArray[index]) {
                        if (this.changeSize == 1) {
                            if (this.invalidationSize == 1) {
                                return SingleInvalidation(this.invalidationListeners[0])
                            }
                            this.changeListenerArray = arrayOf()
                            this.changeSize = 0
                        } else if (this.changeSize == 2 && this.invalidationSize == 0) {
                            return SingleChange(this.setChangeListeners[1 - index])
                        } else {
                            val numMoved = this.changeSize - index - 1
                            val oldListeners = this.changeListenerArray
                            if (this.locked) {
                                this.changeListenerArray = arrayOfNulls(this.changeListenerArray.size)
                                oldListeners.copyInto(this.changeListenerArray, 0, 0, index + 1)
                            }
                            if (numMoved > 0) {
                                oldListeners.copyInto(this.changeListenerArray, index, index + 1, this.changeSize)
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

        override fun fireValueChangedEvent(change: Change<out E>) {
            val curInvalidationList = this.invalidationListenerArray
            val curInvalidationSize = this.invalidationSize
            val curChangeList = this.changeListenerArray
            val curChangeSize = this.changeSize

            try {
                this.locked = true
                for (i in 0 until curInvalidationSize) {
                    try {
                        curInvalidationList[i]!!.invalidated(change.set)
                    } catch (e: Exception) {
                        Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                    }
                }
                for (i in 0 until curChangeSize) {
                    try {
                        curChangeList[i]!!.onChanged(change)
                    } catch (e: Exception) {
                        Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                    }
                }
            } finally {
                this.locked = false
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = ArrayUtils.copyOfNotNulls(this.invalidationListenerArray)

        override val setChangeListeners: Array<SetChangeListener<in E>>
            get() = ArrayUtils.copyOfNotNulls(this.changeListenerArray)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static methods

    companion object {

        fun <E> addListener(helper: SetListenerHelper<E>?, listener: InvalidationListener): SetListenerHelper<E> {
            return helper?.addListener(listener) ?: SingleInvalidation(listener)
        }

        fun <E> removeListener(helper: SetListenerHelper<E>?, listener: InvalidationListener): SetListenerHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> addListener(helper: SetListenerHelper<E>?, listener: SetChangeListener<in E>): SetListenerHelper<E> {
            return helper?.addListener(listener) ?: SingleChange(listener)
        }

        fun <E> removeListener(helper: SetListenerHelper<E>?,
                listener: SetChangeListener<in E>): SetListenerHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> fireValueChangedEvent(helper: SetListenerHelper<E>?, change: Change<out E>) {
            helper?.fireValueChangedEvent(change)
        }

    }

}