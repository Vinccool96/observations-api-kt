package io.github.vinccool96.observationskt.beans.value

class ObservableStringValueStub(initialValue: String?) : ObservableValueBase<String?>(), ObservableStringValue {

    private var valueState: String? = initialValue

    fun set(value: String?) {
        this.valueState = value
        this.fireValueChangedEvent()
    }

    override fun get(): String? {
        return this.valueState
    }

    override val value: String?
        get() = this.get()

}