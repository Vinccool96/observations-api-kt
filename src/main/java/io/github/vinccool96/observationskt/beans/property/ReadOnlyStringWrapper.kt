package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
class ReadOnlyStringWrapper : SimpleStringProperty {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyStringWrapper`
     *
     * @param bean the bean of this `ReadOnlyStringWrapper`
     * @param name the name of this `ReadOnlyStringWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: String?) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyStringWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: String?) : super(initialValue)

    /**
     * The constructor of `ReadOnlyStringWrapper`
     *
     * @param bean the bean of this `ReadOnlyStringWrapper`
     * @param name the name of this `ReadOnlyStringWrapper`
     */
    constructor(bean: Any?, name: String) : super(bean, name)

    /**
     * The constructor of `ReadOnlyStringWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyStringWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyStringProperty
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

    private inner class ReadOnlyPropertyImpl : ReadOnlyStringPropertyBase() {

        override val bean: Any?
            get() = this@ReadOnlyStringWrapper.bean

        override val name: String?
            get() = this@ReadOnlyStringWrapper.name

        override fun get(): String? {
            return this@ReadOnlyStringWrapper.get()
        }

        fun fireChange() {
            this.fireValueChangedEvent()
        }

    }

}