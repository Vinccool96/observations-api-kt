package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
open class ReadOnlyIntWrapper : SimpleIntProperty {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyIntWrapper`
     *
     * @param bean the bean of this `ReadOnlyIntWrapper`
     * @param name the name of this `ReadOnlyIntWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: Int) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyIntWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Int) : super(initialValue)

    /**
     * The constructor of `ReadOnlyIntWrapper`
     *
     * @param bean the bean of this `ReadOnlyIntWrapper`
     * @param name the name of this `ReadOnlyIntWrapper`
     */
    constructor(bean: Any?, name: String?) : super(bean, name)

    /**
     * The constructor of `ReadOnlyIntWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyIntWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyIntProperty
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

    private inner class ReadOnlyPropertyImpl : ReadOnlyIntPropertyBase() {

        override val bean: Any?
            get() = this@ReadOnlyIntWrapper.bean

        override val name: String?
            get() = this@ReadOnlyIntWrapper.name

        override fun get(): Int {
            return this@ReadOnlyIntWrapper.get()
        }

        fun fireChange() {
            this.fireValueChangedEvent()
        }

    }

}