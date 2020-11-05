package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ObservableList

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
open class ReadOnlyListWrapper<E> : SimpleListProperty<E> {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyListWrapper`
     *
     * @param bean the bean of this `ReadOnlyListWrapper`
     * @param name the name of this `ReadOnlyListWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: ObservableList<E>?) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyListWrapper`
     *
     * @param bean the bean of this `ReadOnlyListWrapper`
     * @param name the name of this `ReadOnlyListWrapper`
     */
    constructor(bean: Any?, name: String?) : super(bean, name)

    /**
     * The constructor of `ReadOnlyListWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: ObservableList<E>?) : super(initialValue)

    /**
     * The constructor of `ReadOnlyListWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyListWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyListProperty<E>
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

    override fun fireValueChangedEvent(change: ListChangeListener.Change<out E>) {
        super.fireValueChangedEvent(change)
        if (this::readOnlyPropertyImpl.isInitialized) {
            change.reset()
            this.readOnlyPropertyImpl.fireValueChangedEvent(change)
        }
    }

    private inner class ReadOnlyPropertyImpl : ReadOnlyListPropertyBase<E>() {

        override val bean: Any? = this@ReadOnlyListWrapper.bean

        override val name: String? = this@ReadOnlyListWrapper.name

        override fun get(): ObservableList<E>? {
            return this@ReadOnlyListWrapper.get()
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = this@ReadOnlyListWrapper.sizeProperty

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = this@ReadOnlyListWrapper.emptyProperty

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

        public override fun fireValueChangedEvent(change: ListChangeListener.Change<out E>) {
            super.fireValueChangedEvent(change)
        }

    }

}