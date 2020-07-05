package io.github.vinccool96.observationskt.beans

import kotlin.test.assertEquals

open class InvalidationListenerMock : InvalidationListener {

    private var observable: Observable? = null

    private var counter: Int = 0

    override fun invalidated(observable: Observable) {
        this.observable = observable
        this.counter++
    }

    fun reset() {
        this.observable = null
        this.counter = 0
    }

    fun check(observable: Observable?, counter: Int) {
        assertEquals(observable, this.observable)
        assertEquals(counter, this.counter)
        reset()
    }

}