package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableMapValue
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.MapChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.util.ArrayUtils

/**
 * A convenience class for creating implementations of [io.github.vinccool96.observationskt.beans.value.ObservableValue].
 * It contains all of the infrastructure support for value invalidation- and change event notification.
 *
 * This implementation can handle adding and removing listeners while the observers are being notified, but it is not
 * thread-safe.
 *
 * For the `ExpressionHelperBase` that is used with the [ObservableMaps][ObservableMap] created with the methods of
 * [io.github.vinccool96.observationskt.collections.ObservableCollections], please refer to
 * [io.github.vinccool96.observationskt.sun.collections.MapListenerHelper].
 */
@Suppress("CascadeIf", "UNCHECKED_CAST", "RedundantNullableReturnType")
abstract class MapExpressionHelper<K, V> protected constructor(protected val observable: ObservableMapValue<K, V>) :
        ExpressionHelperBase() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Common implementations

    protected abstract fun addListener(listener: InvalidationListener): MapExpressionHelper<K, V>

    protected abstract fun removeListener(listener: InvalidationListener): MapExpressionHelper<K, V>?

    protected abstract fun addListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V>

    protected abstract fun removeListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V>?

    protected abstract fun addListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V>

    protected abstract fun removeListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V>?

    protected abstract fun fireValueChangedEvent()

    protected abstract fun fireValueChangedEvent(change: Change<out K, out V>)

    abstract val invalidationListeners: Array<InvalidationListener>

    abstract val changeListeners: Array<ChangeListener<in ObservableMap<K, V>?>>

    abstract val mapChangeListeners: Array<MapChangeListener<in K, in V>>

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementations

    private class SingleInvalidation<K, V>(observable: ObservableMapValue<K, V>,
            private val listener: InvalidationListener) : MapExpressionHelper<K, V>(observable) {

        override val invalidationListeners: Array<InvalidationListener> = arrayOf(this.listener)

        override val changeListeners: Array<ChangeListener<in ObservableMap<K, V>?>> = arrayOf()

        override val mapChangeListeners: Array<MapChangeListener<in K, in V>> = arrayOf()

        override fun addListener(listener: InvalidationListener): MapExpressionHelper<K, V> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: InvalidationListener): MapExpressionHelper<K, V>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V>? {
            return this
        }

        override fun addListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V>? {
            return this
        }

        override fun fireValueChangedEvent() {
            this.listener.invalidated(this.observable)
        }

        override fun fireValueChangedEvent(change: Change<out K, out V>) {
            this.listener.invalidated(this.observable)
        }

    }

    private class SingleChange<K, V>(observable: ObservableMapValue<K, V>,
            private val listener: ChangeListener<in ObservableMap<K, V>?>) : MapExpressionHelper<K, V>(observable) {

        override val invalidationListeners: Array<InvalidationListener> = arrayOf()

        override val changeListeners: Array<ChangeListener<in ObservableMap<K, V>?>> = arrayOf(this.listener)

        override val mapChangeListeners: Array<MapChangeListener<in K, in V>> = arrayOf()

        private var currentValue: ObservableMap<K, V>? = this.observable.value

        override fun addListener(listener: InvalidationListener): MapExpressionHelper<K, V> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): MapExpressionHelper<K, V>? {
            return this
        }

        override fun addListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V>? {
            return if (this.listener == listener) null else this
        }

        override fun addListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V>? {
            return this
        }

        override fun fireValueChangedEvent() {
            val oldValue = this.currentValue
            this.currentValue = this.observable.value
            if (this.currentValue !== oldValue) {
                this.listener.changed(this.observable, oldValue, this.currentValue)
            }
        }

        override fun fireValueChangedEvent(change: Change<out K, out V>) {
            this.listener.changed(this.observable, this.currentValue, this.currentValue)
        }

    }

    private class SingleMapChange<K, V>(observable: ObservableMapValue<K, V>,
            private val listener: MapChangeListener<in K, in V>) : MapExpressionHelper<K, V>(observable) {

        override val invalidationListeners: Array<InvalidationListener> = arrayOf()

        override val changeListeners: Array<ChangeListener<in ObservableMap<K, V>?>> = arrayOf()

        override val mapChangeListeners: Array<MapChangeListener<in K, in V>> = arrayOf(this.listener)

        private var currentValue: ObservableMap<K, V>? = this.observable.value

        override fun addListener(listener: InvalidationListener): MapExpressionHelper<K, V> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: InvalidationListener): MapExpressionHelper<K, V>? {
            return this
        }

        override fun addListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V> {
            return Generic(this.observable, listener, this.listener)
        }

        override fun removeListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V>? {
            return this
        }

        override fun addListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V> {
            return Generic(this.observable, this.listener, listener)
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V>? {
            return if (this.listener == listener) null else this
        }

        override fun fireValueChangedEvent() {
            val oldValue = this.currentValue
            this.currentValue = this.observable.value
            if (this.currentValue !== oldValue) {
                val change = SimpleChange(this.observable)
                if (this.currentValue == null) {
                    for (element in oldValue!!.entries) {
                        this.listener.onChanged(change.setRemoved(element.key, element.value))
                    }
                } else if (oldValue == null) {
                    for (element in this.currentValue!!.entries) {
                        this.listener.onChanged(change.setAdded(element.key, element.value))
                    }
                } else {
                    for (element in oldValue.entries) {
                        val key = element.key
                        val oldEntry = element.value
                        if (this.currentValue!!.contains(key)) {
                            val newEntry = this.currentValue!![key]
                            if (if (oldEntry == null) newEntry != null else newEntry != oldEntry) {
                                this.listener.onChanged(change.setPut(key, oldEntry, newEntry))
                            }
                        } else {
                            this.listener.onChanged(change.setRemoved(key, oldEntry))
                        }
                    }
                    for (element in this.currentValue!!.entries) {
                        val key = element.key
                        if (!oldValue.contains(key)) {
                            this.listener.onChanged(change.setAdded(key, element.value))
                        }
                    }
                }
            }
        }

        override fun fireValueChangedEvent(change: Change<out K, out V>) {
            this.listener.onChanged(SimpleChange(this.observable, change))
        }

    }

    private class Generic<K, V> private constructor(observable: ObservableMapValue<K, V>) :
            MapExpressionHelper<K, V>(observable) {

        private var invalidationListenerArray: Array<InvalidationListener?> = arrayOf()

        private var changeListenerArray: Array<ChangeListener<in ObservableMap<K, V>?>?> = arrayOf()

        private var mapChangeListenerArray: Array<MapChangeListener<in K, in V>?> = arrayOf()

        private var invalidationSize: Int = 0

        private var changeSize: Int = 0

        private var mapChangeSize: Int = 0

        private var locked: Boolean = false

        private var currentValue: ObservableMap<K, V>? = observable.value

        override val invalidationListeners: Array<InvalidationListener>
            get() = ArrayUtils.copyOfNotNulls(this.invalidationListenerArray)

        override val changeListeners: Array<ChangeListener<in ObservableMap<K, V>?>>
            get() = ArrayUtils.copyOfNotNulls(this.changeListenerArray)

        override val mapChangeListeners: Array<MapChangeListener<in K, in V>>
            get() = ArrayUtils.copyOfNotNulls(this.mapChangeListenerArray)

        constructor(observable: ObservableMapValue<K, V>, listener0: InvalidationListener,
                listener1: InvalidationListener) : this(observable) {
            this.invalidationListenerArray = arrayOf(listener0, listener1)
            this.invalidationSize = 2
        }

        constructor(observable: ObservableMapValue<K, V>, listener0: ChangeListener<in ObservableMap<K, V>?>,
                listener1: ChangeListener<in ObservableMap<K, V>?>) : this(observable) {
            this.changeListenerArray = arrayOf(listener0, listener1)
            this.changeSize = 2
        }

        constructor(observable: ObservableMapValue<K, V>, listener0: MapChangeListener<in K, in V>,
                listener1: MapChangeListener<in K, in V>) : this(observable) {
            this.mapChangeListenerArray = arrayOf(listener0, listener1)
            this.mapChangeSize = 2
        }

        constructor(observable: ObservableMapValue<K, V>, invalidationListener: InvalidationListener,
                changeListener: ChangeListener<in ObservableMap<K, V>?>) : this(observable) {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.invalidationSize = 1
            this.changeListenerArray = arrayOf(changeListener)
            this.changeSize = 1
        }

        constructor(observable: ObservableMapValue<K, V>, invalidationListener: InvalidationListener,
                mapChangeListener: MapChangeListener<in K, in V>) : this(observable) {
            this.invalidationListenerArray = arrayOf(invalidationListener)
            this.invalidationSize = 1
            this.mapChangeListenerArray = arrayOf(mapChangeListener)
            this.mapChangeSize = 1
        }

        constructor(observable: ObservableMapValue<K, V>, changeListener: ChangeListener<in ObservableMap<K, V>?>,
                mapChangeListener: MapChangeListener<in K, in V>) : this(observable) {
            this.changeListenerArray = arrayOf(changeListener)
            this.changeSize = 1
            this.mapChangeListenerArray = arrayOf(mapChangeListener)
            this.mapChangeSize = 1
        }

        override fun addListener(listener: InvalidationListener): MapExpressionHelper<K, V> {
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

        override fun removeListener(listener: InvalidationListener): MapExpressionHelper<K, V>? {
            if (this.invalidationListenerArray.isNotEmpty()) {
                for (index in 0 until this.invalidationSize) {
                    if (listener == this.invalidationListenerArray[index]) {
                        if (this.invalidationSize == 1) {
                            if (this.changeSize == 1 && this.mapChangeSize == 0) {
                                return SingleChange(this.observable, this.changeListeners[0])
                            } else if (this.changeSize == 0 && this.mapChangeSize == 1) {
                                return SingleMapChange(this.observable, this.mapChangeListeners[0])
                            }
                            this.invalidationListenerArray = arrayOf()
                            this.invalidationSize = 0
                        } else if (this.invalidationSize == 2 && this.changeSize == 0 && this.mapChangeSize == 0) {
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

        override fun addListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V> {
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

        override fun removeListener(listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V>? {
            if (this.changeListenerArray.isNotEmpty()) {
                for (index in 0 until this.changeSize) {
                    if (listener == this.changeListenerArray[index]) {
                        if (this.changeSize == 1) {
                            if (this.invalidationSize == 1 && this.mapChangeSize == 0) {
                                return SingleInvalidation(this.observable, this.invalidationListeners[0])
                            } else if (this.invalidationSize == 0 && this.mapChangeSize == 1) {
                                return SingleMapChange(this.observable, this.mapChangeListeners[0])
                            }
                            this.changeListenerArray = arrayOf()
                            this.changeSize = 0
                        } else if (this.changeSize == 2 && this.invalidationSize == 0 && this.mapChangeSize == 0) {
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

        override fun addListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V> {
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

        override fun removeListener(listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V>? {
            if (this.mapChangeListenerArray.isNotEmpty()) {
                for (index in 0 until this.mapChangeSize) {
                    if (listener == this.mapChangeListenerArray[index]) {
                        if (this.mapChangeSize == 1) {
                            if (this.invalidationSize == 1 && this.changeSize == 0) {
                                return SingleInvalidation(this.observable, this.invalidationListeners[0])
                            } else if (this.invalidationSize == 0 && this.changeSize == 1) {
                                return SingleChange(this.observable, this.changeListeners[0])
                            }
                            this.mapChangeListenerArray = arrayOf()
                            this.mapChangeSize = 0
                        } else if (this.mapChangeSize == 2 && this.invalidationSize == 0 && this.changeSize == 0) {
                            return SingleMapChange(this.observable, this.mapChangeListeners[1 - index])
                        } else {
                            val numMoved = this.mapChangeSize - index - 1
                            val oldListeners = this.mapChangeListenerArray
                            if (this.locked) {
                                this.mapChangeListenerArray = arrayOfNulls(this.mapChangeListenerArray.size)
                                oldListeners.copyInto(this.mapChangeListenerArray, 0, 0, index + 1)
                            }
                            if (numMoved > 0) {
                                oldListeners.copyInto(this.mapChangeListenerArray, index, index + 1,
                                        this.mapChangeSize)
                            }
                            this.mapChangeSize--
                            if (!this.locked) {
                                this.mapChangeListenerArray[this.mapChangeSize] = null // Let gc do its work
                            }
                        }
                        break
                    }
                }
            }
            return this
        }

        override fun fireValueChangedEvent() {
            if (this.changeSize == 0 && this.mapChangeSize == 0) {
                notifyListeners(this.currentValue, null)
            } else {
                val oldValue = this.currentValue
                this.currentValue = this.observable.value

                notifyListeners(oldValue, null)

            }
        }

        override fun fireValueChangedEvent(change: Change<out K, out V>) {
            val mappedChange = if (this.mapChangeSize != 0) SimpleChange(this.observable, change) else null
            notifyListeners(this.currentValue, mappedChange)
        }

        @Suppress("NAME_SHADOWING")
        private fun notifyListeners(oldValue: ObservableMap<K, V>?, change: Change<K, V>?) {
            val curInvalidationMap = this.invalidationListenerArray
            val curInvalidationSize = this.invalidationSize
            val curChangeMap = this.changeListenerArray
            val curChangeSize = this.changeSize
            val curMapChangeMap = this.mapChangeListenerArray
            val curMapChangeSize = this.mapChangeSize
            try {
                this.locked = true
                for (i in 0 until curInvalidationSize) {
                    curInvalidationMap[i]!!.invalidated(this.observable)
                }
                if (this.currentValue != oldValue || change != null) {
                    for (i in 0 until curChangeSize) {
                        curChangeMap[i]!!.changed(this.observable, oldValue, this.currentValue)
                    }
                    if (curMapChangeSize > 0) {
                        if (change != null) {
                            for (i in 0 until curMapChangeSize) {
                                curMapChangeMap[i]!!.onChanged(change)
                            }
                        } else {
                            val change = SimpleChange(this.observable)
                            if (this.currentValue == null) {
                                for (element in oldValue!!.entries) {
                                    change.setRemoved(element.key, element.value)
                                    for (i in 0 until curMapChangeSize) {
                                        curMapChangeMap[i]!!.onChanged(change)
                                    }
                                }
                            } else if (oldValue == null) {
                                for (element in this.currentValue!!.entries) {
                                    change.setAdded(element.key, element.value)
                                    for (i in 0 until curMapChangeSize) {
                                        curMapChangeMap[i]!!.onChanged(change)
                                    }
                                }
                            } else {
                                for (element in oldValue.entries) {
                                    val key = element.key
                                    val oldEntry = element.value
                                    if (this.currentValue!!.contains(key)) {
                                        val newEntry = this.currentValue!![key]
                                        if (if (oldEntry == null) newEntry != null else newEntry != oldEntry) {
                                            change.setPut(key, oldEntry, newEntry)
                                            for (i in 0 until curMapChangeSize) {
                                                curMapChangeMap[i]!!.onChanged(change)
                                            }
                                        }
                                    } else {
                                        change.setRemoved(key, oldEntry)
                                        for (i in 0 until curMapChangeSize) {
                                            curMapChangeMap[i]!!.onChanged(change)
                                        }
                                    }
                                }
                                for (element in this.currentValue!!.entries) {
                                    val key = element.key
                                    if (!oldValue.contains(key)) {
                                        change.setAdded(key, element.value)
                                        for (i in 0 until curMapChangeSize) {
                                            curMapChangeMap[i]!!.onChanged(change)
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

    }

    class SimpleChange<K, V> : Change<K, V> {

        private lateinit var keyHolder: KeyHolder<K>

        private var old: V? = null

        private var added: V? = null

        private var removeOp: Boolean = false

        private var addOp: Boolean = false

        constructor(set: ObservableMap<K, V>) : super(set)

        constructor(set: ObservableMap<K, V>, source: Change<out K, out V>) : super(set) {
            this.keyHolder = KeyHolder(source.key)
            this.old = source.valueRemoved
            this.added = source.valueAdded
            this.addOp = source.wasAdded
            this.removeOp = source.wasRemoved
        }

        fun setRemoved(key: K, old: V?): SimpleChange<K, V> {
            this.keyHolder = KeyHolder(key)
            this.old = old
            this.added = null
            this.addOp = false
            this.removeOp = true
            return this
        }

        fun setAdded(key: K, added: V?): SimpleChange<K, V> {
            this.keyHolder = KeyHolder(key)
            this.old = null
            this.added = added
            this.addOp = true
            this.removeOp = false
            return this
        }

        fun setPut(key: K, old: V?, added: V?): SimpleChange<K, V> {
            this.keyHolder = KeyHolder(key)
            this.old = old
            this.added = added
            this.addOp = true
            this.removeOp = true
            return this
        }

        override val wasAdded: Boolean
            get() = this.addOp

        override val wasRemoved: Boolean
            get() = this.removeOp

        override val key: K
            get() = this.keyHolder.key

        override val valueAdded: V?
            get() = this.added

        override val valueRemoved: V?
            get() = this.old

        private class KeyHolder<K>(val key: K)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static methods

    companion object {

        fun <K, V> addListener(helper: MapExpressionHelper<K, V>?, observable: ObservableMapValue<K, V>,
                listener: InvalidationListener): MapExpressionHelper<K, V> {
            observable.value // validate observable
            return helper?.addListener(listener) ?: SingleInvalidation(observable, listener)
        }

        fun <K, V> removeListener(helper: MapExpressionHelper<K, V>?,
                listener: InvalidationListener): MapExpressionHelper<K, V>? {
            return helper?.removeListener(listener)
        }

        fun <K, V> addListener(helper: MapExpressionHelper<K, V>?, observable: ObservableMapValue<K, V>,
                listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V> {
            return helper?.addListener(listener) ?: SingleChange(observable, listener)
        }

        fun <K, V> removeListener(helper: MapExpressionHelper<K, V>?,
                listener: ChangeListener<in ObservableMap<K, V>?>): MapExpressionHelper<K, V>? {
            return helper?.removeListener(listener)
        }

        fun <K, V> addListener(helper: MapExpressionHelper<K, V>?, observable: ObservableMapValue<K, V>,
                listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V> {
            return helper?.addListener(listener) ?: SingleMapChange(observable, listener)
        }

        fun <K, V> removeListener(helper: MapExpressionHelper<K, V>?,
                listener: MapChangeListener<in K, in V>): MapExpressionHelper<K, V>? {
            return helper?.removeListener(listener)
        }

        fun <K, V> fireValueChangedEvent(helper: MapExpressionHelper<K, V>?) {
            helper?.fireValueChangedEvent()
        }

        fun <K, V> fireValueChangedEvent(helper: MapExpressionHelper<K, V>?, change: Change<out K, out V>) {
            helper?.fireValueChangedEvent(change)
        }

    }

}
