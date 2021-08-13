package io.github.vinccool96.observationskt.beans.value

class ObservableShortValueStub(initialValue: Short) : ObservableValueBase<Number?>(), ObservableShortValue {

    private var valueState: Short = initialValue

    constructor() : this(0)

    override fun get(): Short {
        return this.valueState
    }

    fun set(short: Short) {
        this.valueState = short
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
        get() = this.get()

    override val byteValue: Byte
        get() = this.get().toByte()

    override val value: Number
        get() = this.get()

}