package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.MapChangeListener.Change
import io.github.vinccool96.observationskt.sun.binding.ExpressionHelperBase
import io.github.vinccool96.observationskt.util.ArrayUtils

@Suppress("UNCHECKED_CAST")
abstract class MapListenerHelper<K, V> : ExpressionHelperBase() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Common implementations

    protected abstract fun addListener(listener: InvalidationListener): MapListenerHelper<K, V>

    protected abstract fun removeListener(listener: InvalidationListener): MapListenerHelper<K, V>?

    protected abstract fun addListener(listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V>

    protected abstract fun removeListener(listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V>?

    protected abstract fun fireValueChangedEvent(change: Change<out K, out V>)

    abstract val invalidationListeners: Array<InvalidationListener>

    abstract val mapChangeListeners: Array<MapChangeListener<in K, in V>>

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementations

    private class SingleInvalidation<K, V>(private val listener: InvalidationListener) : MapListenerHelper<K, V>() {

        override val invalidationListeners: Array<InvalidationListener> = arrayOf(this.listener)

        override val mapChangeListeners: Array<MapChangeListener<in K, in V>> = arrayOf()

        override fun addListener(listener: InvalidationListener): MapListenerHelper<K, V> {
            return Generic(this.listener, listener)
        }

        override fun removeListener(listener: InvalidationListener): MapListenerHelper<K, V>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V> {
            return Generic(this.listener, listener)
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V> {
            return this
        }

        override fun fireValueChangedEvent(change: Change<out K, out V>) {
            this.listener.invalidated(change.map)
        }

    }

    private class SingleChange<K, V>(private val listener: MapChangeListener<in K, in V>) : MapListenerHelper<K, V>() {

        override val invalidationListeners: Array<InvalidationListener> = arrayOf()

        override val mapChangeListeners: Array<MapChangeListener<in K, in V>> = arrayOf(this.listener)

        override fun addListener(listener: InvalidationListener): MapListenerHelper<K, V> {
            return Generic(listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): MapListenerHelper<K, V> {
            return this
        }

        override fun addListener(listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V> {
            return Generic(this.listener, listener)
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V>? {
            return if (this.listener == listener) null else this
        }

        override fun fireValueChangedEvent(change: Change<out K, out V>) {
            this.listener.onChanged(change)
        }

    }

    private class Generic<K, V> : MapListenerHelper<K, V> {

        private var invalidationListenerArray: Array<InvalidationListener?>

        private var mapChangeListenerArray: Array<MapChangeListener<in K, in V>?>

        private var invalidationSize: Int

        private var mapChangeSize: Int

        private var locked: Boolean = false

        constructor(listener0: InvalidationListener, listener1: InvalidationListener) : super() {
            this.invalidationListenerArray = arrayOf(listener0, listener1)
            this.mapChangeListenerArray = emptyArray()
            this.invalidationSize = 2
            this.mapChangeSize = 0
        }

        constructor(listener0: MapChangeListener<in K, in V>, listener1: MapChangeListener<in K, in V>) : super() {
            this.invalidationListenerArray = emptyArray()
            this.mapChangeListenerArray = arrayOf(listener0, listener1)
            this.invalidationSize = 0
            this.mapChangeSize = 2
        }

        constructor(invalidationListener: InvalidationListener,
                listChangeListener: MapChangeListener<in K, in V>) : super() {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.mapChangeListenerArray = arrayOf(listChangeListener)
            this.invalidationSize = 1
            this.mapChangeSize = 1
        }

        override fun addListener(listener: InvalidationListener): MapListenerHelper<K, V> {
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

        override fun removeListener(listener: InvalidationListener): MapListenerHelper<K, V> {
            for (index in 0 until this.invalidationSize) {
                if (listener == this.invalidationListenerArray[index]) {
                    if (this.invalidationSize == 1) {
                        if (this.mapChangeSize == 1) {
                            return SingleChange(this.mapChangeListeners[0])
                        }
                        this.invalidationListenerArray = emptyArray()
                        this.invalidationSize = 0
                    } else if (this.invalidationSize == 2 && this.mapChangeSize == 0) {
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
            return this
        }

        override fun addListener(listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V> {
            if (this.mapChangeListenerArray.isEmpty()) {
                this.mapChangeListenerArray = arrayOf(listener)
                this.mapChangeSize = 1
            } else {
                val oldSize = this.mapChangeListenerArray.size
                if (this.locked) {
                    val newSize = if (this.mapChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                    this.mapChangeListenerArray = this.mapChangeListenerArray.copyOf(newSize)
                } else if (this.mapChangeSize == oldSize) {
                    this.mapChangeSize = trim(this.mapChangeSize, this.mapChangeListenerArray as Array<Any?>)
                    if (this.mapChangeSize == oldSize) {
                        val newSize = if (this.mapChangeSize < oldSize) oldSize else (oldSize * 3) / 2 + 1
                        this.mapChangeListenerArray = this.mapChangeListenerArray.copyOf(newSize)
                    }
                }
                this.mapChangeListenerArray[this.mapChangeSize++] = listener
            }
            return this
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V> {
            for (index in 0 until this.mapChangeSize) {
                if (listener == this.mapChangeListenerArray[index]) {
                    if (this.mapChangeSize == 1) {
                        if (this.invalidationSize == 1) {
                            return SingleInvalidation(this.invalidationListeners[0])
                        }
                        this.mapChangeListenerArray = emptyArray()
                        this.mapChangeSize = 0
                    } else if (this.mapChangeSize == 2 && this.invalidationSize == 0) {
                        return SingleChange(this.mapChangeListeners[1 - index])
                    } else {
                        val numMoved = this.mapChangeSize - index - 1
                        val oldListeners = this.mapChangeListenerArray
                        if (this.locked) {
                            this.mapChangeListenerArray = arrayOfNulls(this.mapChangeListenerArray.size)
                            oldListeners.copyInto(this.mapChangeListenerArray, 0, 0, index + 1)
                        }
                        if (numMoved > 0) {
                            oldListeners.copyInto(this.mapChangeListenerArray, index, index + 1, this.mapChangeSize)
                        }
                        this.mapChangeSize--
                        if (!this.locked) {
                            this.mapChangeListenerArray[this.mapChangeSize] = null // Let gc do its work
                        }
                    }
                    break
                }
            }
            return this
        }

        override fun fireValueChangedEvent(change: Change<out K, out V>) {
            val curInvalidationMap = this.invalidationListenerArray
            val curInvalidationSize = this.invalidationSize
            val curMapChangeMap = this.mapChangeListenerArray
            val curMapChangeSize = this.mapChangeSize

            try {
                this.locked = true
                for (i in 0 until curInvalidationSize) {
                    try {
                        curInvalidationMap[i]?.invalidated(change.map)
                    } catch (e: Exception) {
                        Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                    }
                }
                for (i in 0 until curMapChangeSize) {
                    try {
                        curMapChangeMap[i]?.onChanged(change)
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

        override val mapChangeListeners: Array<MapChangeListener<in K, in V>>
            get() = ArrayUtils.copyOfNotNulls(this.mapChangeListenerArray)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static methods

    companion object {

        fun <K, V> addListener(helper: MapListenerHelper<K, V>?,
                listener: InvalidationListener): MapListenerHelper<K, V> {
            return helper?.addListener(listener) ?: SingleInvalidation(listener)
        }

        fun <K, V> removeListener(helper: MapListenerHelper<K, V>?,
                listener: InvalidationListener): MapListenerHelper<K, V>? {
            return helper?.removeListener(listener)
        }

        fun <K, V> addListener(helper: MapListenerHelper<K, V>?,
                listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V> {
            return helper?.addListener(listener) ?: SingleChange(listener)
        }

        fun <K, V> removeListener(helper: MapListenerHelper<K, V>?,
                listener: MapChangeListener<in K, in V>): MapListenerHelper<K, V>? {
            return helper?.removeListener(listener)
        }

        fun <K, V> fireValueChangedEvent(helper: MapListenerHelper<K, V>?, change: Change<out K, out V>) {
            helper?.fireValueChangedEvent(change)
        }

    }

}