package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
class ReadOnlyFloatWrapper : SimpleFloatProperty {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyFloatWrapper`
     *
     * @param bean the bean of this `ReadOnlyFloatWrapper`
     * @param name the name of this `ReadOnlyFloatWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: Float) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyFloatWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Float) : super(initialValue)

    /**
     * The constructor of `ReadOnlyFloatWrapper`
     *
     * @param bean the bean of this `ReadOnlyFloatWrapper`
     * @param name the name of this `ReadOnlyFloatWrapper`
     */
    constructor(bean: Any?, name: String) : super(bean, name)

    /**
     * The constructor of `ReadOnlyFloatWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyStringWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyFloatProperty
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

    private inner class ReadOnlyPropertyImpl : ReadOnlyFloatPropertyBase() {

        override val bean: Any?
            get() = this@ReadOnlyFloatWrapper.bean

        override val name: String?
            get() = this@ReadOnlyFloatWrapper.name

        override fun get(): Float {
            return this@ReadOnlyFloatWrapper.get()
        }

        fun fireChange() {
            this.fireValueChangedEvent()
        }

    }

}