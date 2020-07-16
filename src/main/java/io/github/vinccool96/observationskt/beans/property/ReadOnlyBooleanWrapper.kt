package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
open class ReadOnlyBooleanWrapper : SimpleBooleanProperty {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyBooleanWrapper`
     *
     * @param bean the bean of this `ReadOnlyBooleanWrapper`
     * @param name the name of this `ReadOnlyBooleanWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: Boolean) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyBooleanWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Boolean) : super(initialValue)

    /**
     * The constructor of `ReadOnlyBooleanWrapper`
     *
     * @param bean the bean of this `ReadOnlyBooleanWrapper`
     * @param name the name of this `ReadOnlyBooleanWrapper`
     */
    constructor(bean: Any?, name: String?) : super(bean, name)

    /**
     * The constructor of `ReadOnlyBooleanWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyBooleanWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyBooleanProperty
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

    private inner class ReadOnlyPropertyImpl : ReadOnlyBooleanPropertyBase() {

        override val bean: Any?
            get() = this@ReadOnlyBooleanWrapper.bean

        override val name: String?
            get() = this@ReadOnlyBooleanWrapper.name

        override fun get(): Boolean {
            return this@ReadOnlyBooleanWrapper.get()
        }

        fun fireChange() {
            this.fireValueChangedEvent()
        }

    }

}