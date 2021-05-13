package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
open class ReadOnlySetWrapper<E> : SimpleSetProperty<E> {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlySetWrapper`
     *
     * @param bean the bean of this `ReadOnlySetWrapper`
     * @param name the name of this `ReadOnlySetWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: ObservableSet<E>?) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlySetWrapper`
     *
     * @param bean the bean of this `ReadOnlySetWrapper`
     * @param name the name of this `ReadOnlySetWrapper`
     */
    constructor(bean: Any?, name: String?) : super(bean, name)

    /**
     * The constructor of `ReadOnlySetWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: ObservableSet<E>?) : super(initialValue)

    /**
     * The constructor of `ReadOnlySetWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlySetWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlySetProperty<E>
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

    override fun fireValueChangedEvent(change: Change<out E>) {
        super.fireValueChangedEvent(change)
        if (this::readOnlyPropertyImpl.isInitialized) {
            this.readOnlyPropertyImpl.fireValueChangedEvent(change)
        }
    }

    private inner class ReadOnlyPropertyImpl : ReadOnlySetPropertyBase<E>() {

        override fun get(): ObservableSet<E>? {
            return this@ReadOnlySetWrapper.get()
        }

        override val bean: Any?
            get() = this@ReadOnlySetWrapper.bean

        override val name: String?
            get() = this@ReadOnlySetWrapper.name

        override val sizeProperty: ReadOnlyIntProperty
            get() = this@ReadOnlySetWrapper.sizeProperty

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = this@ReadOnlySetWrapper.emptyProperty

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

        public override fun fireValueChangedEvent(change: Change<out E>) {
            super.fireValueChangedEvent(change)
        }

    }

}