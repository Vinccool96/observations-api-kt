package io.github.vinccool96.observationskt.beans.value

class ObservableValueStub<T>(initialValue: T) : ObservableValueBase<T>(), ObservableValue<T> {

    private var valueState: T = initialValue

    fun set(value: T) {
        this.valueState = value
        this.fireValueChangedEvent()
    }

    override val value: T
        get() = this.valueState

}