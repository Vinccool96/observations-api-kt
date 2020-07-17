package io.github.vinccool96.observationskt.beans.value

class ObservableObjectValueStub<T>(initialValue: T) : ObservableValueBase<T>(), ObservableObjectValue<T> {

    private var valueState: T = initialValue

    fun set(value: T) {
        this.valueState = value
        this.fireValueChangedEvent()
    }

    override fun get(): T {
        return this.valueState
    }

    override val value: T
        get() = this.get()

    fun fireChange() {
        this.fireValueChangedEvent()
    }

}