package io.github.vinccool96.observationskt.beans.value

class ObservableLongValueStub(initialValue: Long) : ObservableValueBase<Number>(), ObservableLongValue {

    private var valueState: Long = initialValue

    constructor() : this(0L)

    override fun get(): Long {
        return this.valueState
    }

    fun set(long: Long) {
        this.valueState = long
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

    override val value: Number
        get() = this.get()

}