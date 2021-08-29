package io.github.vinccool96.observationskt.beans.value

class ObservableByteValueStub(initialValue: Byte) : ObservableValueBase<Number?>(), ObservableByteValue {

    private var valueState: Byte = initialValue

    constructor() : this(0)

    override fun get(): Byte {
        return this.valueState
    }

    fun set(byte: Byte) {
        this.valueState = byte
        this.fireValueChangedEvent()
    }

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val shortValue: Short
        get() = this.get().toShort()

    override val byteValue: Byte
        get() = this.get()

    override val value: Number
        get() = this.get()

}