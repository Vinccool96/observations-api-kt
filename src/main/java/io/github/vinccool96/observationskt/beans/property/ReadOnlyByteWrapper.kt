package io.github.vinccool96.observationskt.beans.property

/**
 * This class provides a convenient class to define read-only properties. It creates two properties that are
 * synchronized. One property is read-only and can be passed to external users. The other property is read- and writable
 * and should be used internally only.
 */
open class ReadOnlyByteWrapper : SimpleByteProperty {

    private lateinit var readOnlyPropertyImpl: ReadOnlyPropertyImpl

    /**
     * The constructor of `ReadOnlyByteWrapper`
     *
     * @param bean the bean of this `ReadOnlyByteWrapper`
     * @param name the name of this `ReadOnlyByteWrapper`
     * @param initialValue the initial value of the wrapped value
     */
    constructor(bean: Any?, name: String?, initialValue: Byte) : super(bean, name, initialValue)

    /**
     * The constructor of `ReadOnlyByteWrapper`
     *
     * @param initialValue the initial value of the wrapped value
     */
    constructor(initialValue: Byte) : super(initialValue)

    /**
     * The constructor of `ReadOnlyByteWrapper`
     *
     * @param bean the bean of this `ReadOnlyByteWrapper`
     * @param name the name of this `ReadOnlyByteWrapper`
     */
    constructor(bean: Any?, name: String?) : super(bean, name)

    /**
     * The constructor of `ReadOnlyByteWrapper`
     */
    constructor() : super()

    /**
     * Returns the readonly property, that is synchronized with this `ReadOnlyByteWrapper`.
     *
     * @return the readonly property
     */
    val readOnlyProperty: ReadOnlyByteProperty
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

    private inner class ReadOnlyPropertyImpl : ReadOnlyBytePropertyBase() {

        override val bean: Any?
            get() = this@ReadOnlyByteWrapper.bean

        override val name: String?
            get() = this@ReadOnlyByteWrapper.name

        override fun get(): Byte {
            return this@ReadOnlyByteWrapper.get()
        }

        fun fireChange() {
            this.fireValueChangedEvent()
        }

    }

}