package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.WeakListener
import io.github.vinccool96.observationskt.beans.property.Property
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import java.lang.ref.WeakReference

abstract class BidirectionalBinding<T>(property1: Any, property2: Any) : ChangeListener<T>, WeakListener {

    private val cachedHashCode: Int = property1.hashCode() * property2.hashCode()

    protected abstract val property1: Any?

    protected abstract val property2: Any?

    override fun hashCode(): Int {
        return this.cachedHashCode
    }

    override val wasGarbageCollected: Boolean
        get() = this.property1 == null || this.property2 == null

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            if (this === other) {
                return true
            }

            val propertyA1 = this.property1
            val propertyA2 = this.property2
            if (propertyA1 == null || propertyA2 == null) {
                return false
            }
            if (other is BidirectionalBinding<*>) {
                val propertyB1 = other.property1
                val propertyB2 = other.property2
                if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
                    return true
                }
                return propertyA1 == propertyB2 && propertyA2 == propertyB1
            }
        }
        return false
    }

    private class TypedGenericBidirectionalBinding<T>(property1: Property<T>, property2: Property<T>) :
            BidirectionalBinding<T>(property1, property2) {

        private val propertyRef1: WeakReference<Property<T>> = WeakReference(property1)

        private val propertyRef2: WeakReference<Property<T>> = WeakReference(property2)

        private var updating: Boolean = false

        override val property1: Any?
            get() = this.propertyRef1.get()

        override val property2: Any?
            get() = this.propertyRef2.get()

        override fun changed(observable: ObservableValue<out T>, oldValue: T, newValue: T) {
            if (!this.updating) {
                val property1: Property<T>? = this.propertyRef1.get()
                val property2: Property<T>? = this.propertyRef2.get()
                if (property1 != null && property2 != null) {
                    try {
                        this.updating = true
                        if (property1 === observable) {
                            property2.value = newValue
                        } else {
                            property1.value = newValue
                        }
                    } catch (e: RuntimeException) {
                        try {
                            if (property1 === observable) {
                                property2.value = newValue
                            } else {
                                property1.value = newValue
                            }
                        } catch (e2: Exception) {
                            e2.addSuppressed(e)
                            unbind(property1, property2)
                            throw RuntimeException("Bidirectional binding failed together with an attempt to " +
                                    "restore the source property to the previous value. Removing the bidirectional " +
                                    "binding from properties $property1 and $property2", e2)
                        }
                        throw RuntimeException("Bidirectional binding failed, setting to the previous value", e)
                    } finally {
                        this.updating = false
                    }
                } else {
                    property1?.removeListener(this)
                    property2?.removeListener(this)
                }
            }
        }

    }

    private class UntypedGenericBidirectionalBinding(private val p1: Any, private val p2: Any) :
            BidirectionalBinding<Any?>(p1, p2) {

        override val property1: Any?
            get() = this.p1

        override val property2: Any?
            get() = this.p2

        override fun changed(observable: ObservableValue<out Any?>, oldValue: Any?, newValue: Any?) {
            throw RuntimeException("Should not reach here")
        }

    }

    companion object {

        private fun checkParameters(property1: Property<*>, property2: Property<*>) {
            require(!(property1 === property2)) {"Cannot bind property to itself"}
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> bind(property1: Property<T>, property2: Property<T>): BidirectionalBinding<*> {
            checkParameters(property1, property2)
            val binding: BidirectionalBinding<*> = TypedGenericBidirectionalBinding(property1, property2)
            property1.value = property2.value
            property1.addListener(binding as BidirectionalBinding<T>)
            property2.addListener(binding)
            return binding
        }

        fun <T> unbind(property1: Property<T>, property2: Property<T>) {
            checkParameters(property1, property2)
            val binding: BidirectionalBinding<Any?> = UntypedGenericBidirectionalBinding(property1, property2)
            property1.removeListener(binding)
            property2.removeListener(binding)
        }

    }
}