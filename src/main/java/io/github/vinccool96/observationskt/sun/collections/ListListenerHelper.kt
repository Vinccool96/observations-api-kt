package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.sun.binding.ExpressionHelperBase
import io.github.vinccool96.observationskt.util.ArrayUtils

@Suppress("DuplicatedCode", "UNCHECKED_CAST")
abstract class ListListenerHelper<E> : ExpressionHelperBase() {

    protected abstract fun addListener(listener: InvalidationListener): ListListenerHelper<E>

    protected abstract fun removeListener(listener: InvalidationListener): ListListenerHelper<E>?

    protected abstract fun addListener(listener: ListChangeListener<in E>): ListListenerHelper<E>

    protected abstract fun removeListener(listener: ListChangeListener<in E>): ListListenerHelper<E>?

    protected abstract fun fireValueChangedEvent(change: ListChangeListener.Change<out E>)

    abstract val invalidationListeners: Array<InvalidationListener>

    abstract val listChangeListeners: Array<ListChangeListener<in E>>

    private class SingleInvalidation<E>(private val listener: InvalidationListener) : ListListenerHelper<E>() {

        override fun addListener(listener: InvalidationListener): ListListenerHelper<E> {
            return Generic(
                    this.listener, listener)
        }

        override fun removeListener(listener: InvalidationListener): ListListenerHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: ListChangeListener<in E>): ListListenerHelper<E> {
            return Generic(
                    this.listener, listener)
        }

        override fun removeListener(listener: ListChangeListener<in E>): ListListenerHelper<E>? {
            return this
        }

        override fun fireValueChangedEvent(change: ListChangeListener.Change<out E>) {
            try {
                this.listener.invalidated(change.list)
            } catch (e: Exception) {
                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = arrayOf(this.listener)

        override val listChangeListeners: Array<ListChangeListener<in E>>
            get() = emptyArray()

    }

    private class SingleListChange<E>(private val listener: ListChangeListener<in E>) : ListListenerHelper<E>() {

        override fun addListener(listener: InvalidationListener): ListListenerHelper<E> {
            return Generic(
                    listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): ListListenerHelper<E>? {
            return this
        }

        override fun addListener(listener: ListChangeListener<in E>): ListListenerHelper<E> {
            return Generic(
                    this.listener, listener)
        }

        override fun removeListener(listener: ListChangeListener<in E>): ListListenerHelper<E>? {
            return if (this.listener == listener) null else this
        }

        override fun fireValueChangedEvent(change: ListChangeListener.Change<out E>) {
            try {
                this.listener.onChanged(change)
            } catch (e: Exception) {
                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
            }
        }

        override val invalidationListeners: Array<InvalidationListener>
            get() = emptyArray()

        override val listChangeListeners: Array<ListChangeListener<in E>>
            get() = arrayOf(this.listener)

    }

    private class Generic<E> :
            ListListenerHelper<E> {

        private var invalidationListenerArray: Array<InvalidationListener?>

        private var listChangeListenerArray: Array<ListChangeListener<in E>?>

        private var invalidationSize: Int

        private var listChangeSize: Int

        private var locked: Boolean = false

        constructor(listener0: InvalidationListener, listener1: InvalidationListener) : super() {
            this.invalidationListenerArray = arrayOf(listener0, listener1)
            this.listChangeListenerArray = emptyArray()
            this.invalidationSize = 2
            this.listChangeSize = 0
        }

        constructor(listener0: ListChangeListener<in E>, listener1: ListChangeListener<in E>) : super() {
            this.invalidationListenerArray = emptyArray()
            this.listChangeListenerArray = arrayOf(listener0, listener1)
            this.invalidationSize = 0
            this.listChangeSize = 2
        }

        constructor(invalidationListener: InvalidationListener,
                listChangeListener: ListChangeListener<in E>) : super() {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.listChangeListenerArray = arrayOf(listChangeListener)
            this.invalidationSize = 1
            this.listChangeSize = 1
        }

        override fun addListener(listener: InvalidationListener): ListListenerHelper<E> {
            if (this.invalidationListenerArray.isEmpty()) {
                this.invalidationListenerArray = arrayOf(listener)
                this.invalidationSize = 1
            } else {
                val oldSize = this.invalidationListenerArray.size
                if (this.locked) {
                    val newSize = if (this.invalidationSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                    this.invalidationListenerArray = this.invalidationListenerArray.copyOf(newSize)
                } else if (this.invalidationSize == oldSize) {
                    this.invalidationSize =
                            trim(
                                    this.invalidationSize, this.invalidationListenerArray as Array<Any?>)
                    if (this.invalidationSize == oldSize) {
                        val newSize = if (this.invalidationSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                        this.invalidationListenerArray = this.invalidationListenerArray.copyOf(newSize)
                    }
                }
                this.invalidationListenerArray[this.invalidationSize++] = listener
            }
            return this
        }

        override fun removeListener(listener: InvalidationListener): ListListenerHelper<E>? {
            for (index in 0 until this.invalidationSize) {
                if (listener == this.invalidationListenerArray[index]) {
                    if (this.invalidationSize == 1) {
                        if (this.listChangeSize == 1) {
                            return SingleListChange(
                                    this.listChangeListeners[0])
                        }
                        this.invalidationListenerArray = emptyArray()
                        this.invalidationSize = 0
                    } else if (this.invalidationSize == 2 && this.listChangeSize == 0) {
                        return SingleInvalidation(
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
            return this
        }

        override fun addListener(listener: ListChangeListener<in E>): ListListenerHelper<E> {
            if (this.listChangeListenerArray.isEmpty()) {
                this.listChangeListenerArray = arrayOf(listener)
                this.listChangeSize = 1
            } else {
                val oldSize = this.listChangeListenerArray.size
                if (this.locked) {
                    val newSize = if (this.listChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                    this.listChangeListenerArray = this.listChangeListenerArray.copyOf(newSize)
                } else if (this.listChangeSize == oldSize) {
                    this.listChangeSize =
                            trim(
                                    this.listChangeSize, this.listChangeListenerArray as Array<Any?>)
                    if (this.listChangeSize == oldSize) {
                        val newSize = if (this.listChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                        this.listChangeListenerArray = this.listChangeListenerArray.copyOf(newSize)
                    }
                }
                this.listChangeListenerArray[this.listChangeSize++] = listener
            }
            return this
        }

        override fun removeListener(listener: ListChangeListener<in E>): ListListenerHelper<E>? {
            for (index in 0 until this.listChangeSize) {
                if (listener == this.listChangeListenerArray[index]) {
                    if (this.listChangeSize == 1) {
                        if (this.invalidationSize == 1) {
                            return SingleInvalidation(
                                    this.invalidationListeners[0])
                        }
                        this.listChangeListenerArray = emptyArray()
                        this.listChangeSize = 0
                    } else if (this.listChangeSize == 2 && this.invalidationSize == 0) {
                        return SingleListChange(
                                this.listChangeListeners[1 - index])
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
            return this
        }

        override fun fireValueChangedEvent(change: ListChangeListener.Change<out E>) {
            val curInvalidationList = this.invalidationListenerArray
            val curInvalidationSize = this.invalidationSize
            val curListChangeList = this.listChangeListenerArray
            val curListChangeSize = this.listChangeSize

            try {
                this.locked = true
                for (i in 0 until curInvalidationSize) {
                    try {
                        curInvalidationList[i]?.invalidated(change.list)
                    } catch (e: Exception) {
                        Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                    }
                }
                for (i in 0 until curListChangeSize) {
                    change.reset()
                    try {
                        curListChangeList[i]?.onChanged(change)
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

        override val listChangeListeners: Array<ListChangeListener<in E>>
            get() = ArrayUtils.copyOfNotNulls(this.listChangeListenerArray)

    }

    companion object {
        fun <E> addListener(helper: ListListenerHelper<E>?, listener: InvalidationListener): ListListenerHelper<E> {
            return helper?.addListener(listener) ?: SingleInvalidation(
                    listener)
        }

        fun <E> removeListener(helper: ListListenerHelper<E>?, listener: InvalidationListener): ListListenerHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> addListener(helper: ListListenerHelper<E>?, listener: ListChangeListener<in E>): ListListenerHelper<E> {
            return helper?.addListener(listener) ?: SingleListChange(
                    listener)
        }

        fun <E> removeListener(helper: ListListenerHelper<E>?,
                listener: ListChangeListener<in E>): ListListenerHelper<E>? {
            return helper?.removeListener(listener)
        }

        fun <E> fireValueChangedEvent(helper: ListListenerHelper<E>?, change: ListChangeListener.Change<out E>) {
            if (helper != null) {
                change.reset()
                helper.fireValueChangedEvent(change)
            }
        }

        fun <E> hasListeners(helper: ListListenerHelper<E>?): Boolean {
            return helper != null
        }
    }
}