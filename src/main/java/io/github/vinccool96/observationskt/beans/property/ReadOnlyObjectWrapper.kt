package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
open class ReadOnlyObjectWrapper<T> : SimpleObjectProperty<T> {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyObjectWrapper`
     *
     * @param bean the bean of this `ReadOnlyObjectWrapper`
     * @param name the name of this `ReadOnlyObjectWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: T) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyObjectWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: T) : super(initialValue)

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyObjectWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyObjectProperty<T>
        get() {
            if (!this::readOnlyPropertyImpl.isInitialized) {
                this.readOnlyPropertyImpl = ReadOnlyPropertyImpl()
            }
            return this.readOnlyPropertyImpl
        }

    override fun fireValueChangedEvent() {
        super.fireValueChangedEvent()
        if (this::readOnlyPropertyImpl.isInitialized) {
            this.readOnlyPropertyImpl.fireChange()
        }
    }

    private inner class ReadOnlyPropertyImpl : ReadOnlyObjectPropertyBase<T>() {

        override val bean: Any?
            get() = this@ReadOnlyObjectWrapper.bean

        override val name: String?
            get() = this@ReadOnlyObjectWrapper.name

        override fun get(): T {
            return this@ReadOnlyObjectWrapper.get()
        }

        fun fireChange() {
            this.fireValueChangedEvent()
        }

    }

}