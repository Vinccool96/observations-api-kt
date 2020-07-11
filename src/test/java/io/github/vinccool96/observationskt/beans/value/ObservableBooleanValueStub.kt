package io.github.vinccool96.observationskt.beans.value

class ObservableBooleanValueStub(initialValue: Boolean) : ObservableValueBase<Boolean>(), ObservableBooleanValue {

    private var valueState: Boolean = initialValue

    constructor() : this(false)

    fun set(value: Boolean) {
        this.valueState = value
        this.fireValueChangedEvent()
    }

    override fun get(): Boolean {
        return this.valueState
    }

    override val value: Boolean
        get() = this.valueState

}