package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
class ReadOnlyShortWrapper : SimpleShortProperty {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyShortWrapper`
     *
     * @param bean the bean of this `ReadOnlyShortWrapper`
     * @param name the name of this `ReadOnlyShortWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: Short) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyShortWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Short) : super(initialValue)

    /**
     * The constructor of `ReadOnlyShortWrapper`
     *
     * @param bean the bean of this `ReadOnlyShortWrapper`
     * @param name the name of this `ReadOnlyShortWrapper`
     */
    constructor(bean: Any?, name: String?) : super(bean, name)

    /**
     * The constructor of `ReadOnlyShortWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyShortWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyShortProperty
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

    private inner class ReadOnlyPropertyImpl : ReadOnlyShortPropertyBase() {

        override val bean: Any?
            get() = this@ReadOnlyShortWrapper.bean

        override val name: String?
            get() = this@ReadOnlyShortWrapper.name

        override fun get(): Short {
            return this@ReadOnlyShortWrapper.get()
        }

        fun fireChange() {
            this.fireValueChangedEvent()
        }

    }

}