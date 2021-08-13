package io.github.vinccool96.observationskt.beans.value

class ObservableDoubleValueStub(initialValue: Double) : ObservableValueBase<Number?>(), ObservableDoubleValue {

    private var valueState: Double = initialValue

    constructor() : this(0.0)

    fun set(double: Double) {
        this.valueState = double
        this.fireValueChangedEvent()
    }

    override fun get(): Double {
        return this.valueState
    }

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get()

    override val shortValue: Short
        get() = this.longValue.toShort()

    override val byteValue: Byte
        get() = this.longValue.toByte()

    override val value: Number
        get() = this.get()

}