package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.beans.binding.Binding
import java.lang.ref.WeakReference

class BindingHelperObserver(binding: Binding<*>) : InvalidationListener {

    private val ref: WeakReference<Binding<*>> = WeakReference(binding)

    override fun invalidated(observable: Observable) {
        val binding = this.ref.get()
        if (binding == null) {
            observable.removeListener(this)
        } else {
            binding.invalidate()
        }
    }

}