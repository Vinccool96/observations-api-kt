package io.github.vinccool96.observationskt.beans.value

class ObservableIntValueStub(initialValue: Int) : ObservableValueBase<Number>(), ObservableIntValue {

    private var valueState: Int = initialValue

    constructor() : this(0)

    override fun get(): Int {
        return this.valueState
    }

    fun set(int: Int) {
        this.valueState = int
        this.fireValueChangedEvent()
    }

    override val intValue: Int
        get() = this.get()

    override val longValue: Long
        get() = this.get().toLong()

    override val floatValue: Float
        get() = this.get().toFloat()

    override val doubleValue: Double
        get() = this.get().toDouble()

    override val value: Number
        get() = this.get()

}