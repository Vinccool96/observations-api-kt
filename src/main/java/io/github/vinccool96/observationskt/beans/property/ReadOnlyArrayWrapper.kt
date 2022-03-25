package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableArray

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
open class ReadOnlyArrayWrapper<T> : SimpleArrayProperty<T> {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    private val baseArrayOfNull: Array<T>

    /**
     * @constructor The constructor of `ReadOnlyArrayWrapper`
     *
     * @param bean the bean of this `ReadOnlyArrayWrapper`
     * @param name the name of this `ReadOnlyArrayWrapper`
     * @param initialValue the initial value of the wrapped value
     * @param baseArrayOfNull the base array when the value is `null`
     */
    constructor(bean: Any?, name: String?, initialValue: ObservableArray<T>?, baseArrayOfNull: Array<T>) :
            super(bean, name, initialValue, baseArrayOfNull) {
        this.baseArrayOfNull = baseArrayOfNull
    }

    /**
     * The constructor of `ReadOnlyArrayWrapper`
     *
     * @param bean the bean of this `ReadOnlyArrayWrapper`
     * @param name the name of this `ReadOnlyArrayWrapper`
     * @param baseArrayOfNull the base array when the value is `null`
     */
    constructor(bean: Any?, name: String?, baseArrayOfNull: Array<T>) : super(bean, name, baseArrayOfNull) {
        this.baseArrayOfNull = baseArrayOfNull
    }

    /**
     * The constructor of `ReadOnlyArrayWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     * @param baseArrayOfNull the base array when the value is `null`
     */
    constructor(initialValue: ObservableArray<T>?, baseArrayOfNull: Array<T>) : super(initialValue, baseArrayOfNull) {
        this.baseArrayOfNull = baseArrayOfNull
    }

    /**
     * The constructor of `ReadOnlyArrayWrapper`
     *
     * @param baseArrayOfNull the base array when the value is `null`
     */
    constructor(baseArrayOfNull: Array<T>) : super(baseArrayOfNull) {
        this.baseArrayOfNull = baseArrayOfNull
    }

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyListWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyArrayProperty<T>
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

    override fun fireValueChangedEvent(change: Change<out T>) {
        super.fireValueChangedEvent(change)
        if (this::readOnlyPropertyImpl.isInitialized) {
            change.reset()
            this.readOnlyPropertyImpl.fireValueChangedEvent(change)
        }
    }

    private inner class ReadOnlyPropertyImpl : ReadOnlyArrayPropertyBase<T>(this@ReadOnlyArrayWrapper.baseArrayOfNull) {

        override val bean: Any? = this@ReadOnlyArrayWrapper.bean

        override val name: String? = this@ReadOnlyArrayWrapper.name

        override fun get(): ObservableArray<T>? {
            return this@ReadOnlyArrayWrapper.get()
        }

        override val sizeProperty: ReadOnlyIntProperty
            get() = this@ReadOnlyArrayWrapper.sizeProperty

        override val emptyProperty: ReadOnlyBooleanProperty
            get() = this@ReadOnlyArrayWrapper.emptyProperty

        public override fun fireValueChangedEvent() {
            super.fireValueChangedEvent()
        }

        public override fun fireValueChangedEvent(change: Change<out T>) {
            super.fireValueChangedEvent(change)
        }

    }

}