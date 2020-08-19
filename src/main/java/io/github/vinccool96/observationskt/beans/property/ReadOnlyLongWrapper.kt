package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
open class ReadOnlyLongWrapper : SimpleLongProperty {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyLongWrapper`
     *
     * @param bean the bean of this `ReadOnlyLongWrapper`
     * @param name the name of this `ReadOnlyLongWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: Long) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyLongWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Long) : super(initialValue)

    /**
     * The constructor of `ReadOnlyLongWrapper`
     *
     * @param bean the bean of this `ReadOnlyLongWrapper`
     * @param name the name of this `ReadOnlyLongWrapper`
     */
    constructor(bean: Any?, name: String?) : super(bean, name)

    /**
     * The constructor of `ReadOnlyLongWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyLongWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyLongProperty
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

    private inner class ReadOnlyPropertyImpl : ReadOnlyLongPropertyBase() {

        override val bean: Any?
            get() = this@ReadOnlyLongWrapper.bean

        override val name: String?
            get() = this@ReadOnlyLongWrapper.name

        override fun get(): Long {
            return this@ReadOnlyLongWrapper.get()
        }

        fun fireChange() {
            this.fireValueChangedEvent()
        }

    }

}