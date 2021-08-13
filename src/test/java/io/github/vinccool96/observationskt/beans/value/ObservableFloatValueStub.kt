package io.github.vinccool96.observationskt.beans.value

class ObservableFloatValueStub(initialValue: Float) : ObservableValueBase<Number?>(), ObservableFloatValue {

    private var valueState: Float = initialValue

    constructor() : this(0.0F)

    override fun get(): Float {
        return this.valueState
    }

    fun set(float: Float) {
        this.valueState = float
        this.fireValueChangedEvent()
    }

    override val intValue: Int
        get() = this.get().toInt()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val shortValue: Short
        get() = this.intValue.toShort()

    override val byteValue: Byte
        get() = this.intValue.toByte()

    override val value: Number
        get() = this.get()

}