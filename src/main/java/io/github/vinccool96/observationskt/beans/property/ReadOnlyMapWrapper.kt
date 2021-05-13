package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.MapChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableMap

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
open class ReadOnlyMapWrapper<K, V> : SimpleMapProperty<K, V> {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyMapWrapper`
     *
     * @param bean the bean of this `ReadOnlyMapWrapper`
     * @param name the name of this `ReadOnlyMapWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: ObservableMap<K, V>?) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyMapWrapper`
     *
     * @param bean the bean of this `ReadOnlyMapWrapper`
     * @param name the name of this `ReadOnlyMapWrapper`
     */
    constructor(bean: Any?, name: String?) : super(bean, name)

    /**
     * The constructor of `ReadOnlyMapWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: ObservableMap<K, V>?) : super(initialValue)

    /**
     * The constructor of `ReadOnlyMapWrapper`
     */
    constructor() : super()

    val readOnlyProperty: ReadOnlyMapProperty<K, V>
        get() {
            if (!this::readOnlyPropertyImpl.isInitialized) {
                this.readOnlyPropertyImpl = ReadOnlyPropertyImpl()
            }
            return this.readOnlyPropertyImpl
        }

    override fun fireValueChangedEvent() {
        super.fireValueChangedEvent()
        if (this::readOnlyPropertyImpl.isInitialized) {
            this.readOnlyPropertyImpl.fireValueChangedEvent()
        }
    }

    override fun fireValueChangedEvent(change: Change<out K, out V>) {
        super.fireValueChangedEvent(change)
        if (this::readOnlyPropertyImpl.isInitialized) {
            this.readOnlyPropertyImpl.fireValueChangedEvent(change)
        }
    }

    private inner class ReadOnlyPropertyImpl : ReadOnlyMapPropertyBase<K, V>() {

        override val bean: Any?
            get() = this@ReadOnlyMapWrapper.bean

        override val name: String?
            get() = this@ReadOnlyMapWrapper.name

        override fun get(): ObservableMap<K, V>? {
            return this@ReadOnlyMapWrapper.get()
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = this@ReadOnlyMapWrapper.sizeProperty

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = this@ReadOnlyMapWrapper.emptyProperty

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

        public override fun fireValueChangedEvent(change: Change<out K, out V>) {
            super.fireValueChangedEvent(change)
        }

    }

}