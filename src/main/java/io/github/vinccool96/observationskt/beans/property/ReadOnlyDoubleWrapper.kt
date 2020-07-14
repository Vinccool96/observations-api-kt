package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
class ReadOnlyDoubleWrapper : SimpleDoubleProperty {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyDoubleWrapper`
     *
     * @param bean the bean of this `ReadOnlyDoubleWrapper`
     * @param name the name of this `ReadOnlyDoubleWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: Double) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyDoubleWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Double) : super(initialValue)

    /**
     * The constructor of `ReadOnlyDoubleWrapper`
     *
     * @param bean the bean of this `ReadOnlyDoubleWrapper`
     * @param name the name of this `ReadOnlyDoubleWrapper`
     */
    constructor(bean: Any?, name: String) : super(bean, name)

    /**
     * The constructor of `ReadOnlyDoubleWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyStringWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyDoubleProperty
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

    private inner class ReadOnlyPropertyImpl : ReadOnlyDoublePropertyBase() {

        override val bean: Any?
            get() = this@ReadOnlyDoubleWrapper.bean

        override val name: String?
            get() = this@ReadOnlyDoubleWrapper.name

        override fun get(): Double {
            return this@ReadOnlyDoubleWrapper.get()
        }

        fun fireChange() {
            this.fireValueChangedEvent()
        }

    }

}