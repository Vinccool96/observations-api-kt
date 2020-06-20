package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.beans.Observable

abstract class BindingBase<T> : Binding<T> {

    protected abstract fun bind(vararg dependencies: Observable)

    protected abstract fun unbind(vararg dependencies: Observable)

}