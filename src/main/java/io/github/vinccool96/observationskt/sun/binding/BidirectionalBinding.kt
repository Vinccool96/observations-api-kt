package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.WeakListener
import io.github.vinccool96.observationskt.beans.property.*
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.util.StringConverter
import java.lang.ref.WeakReference
import java.text.Format
import java.text.ParseException

@Suppress("UNCHECKED_CAST")
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

    private class BidirectionalBooleanBinding(property1: BooleanProperty, property2: BooleanProperty) :
            BidirectionalBinding<Boolean?>(property1, property2) {

        private val propertyRef1: WeakReference<BooleanProperty> = WeakReference(property1)

        private val propertyRef2: WeakReference<BooleanProperty> = WeakReference(property2)

        private var updating: Boolean = false

        override val property1: Property<Boolean?>?
            get() = this.propertyRef1.get()

        override val property2: Property<Boolean?>?
            get() = this.propertyRef2.get()

        override fun changed(observable: ObservableValue<out Boolean?>, oldValue: Boolean?, newValue: Boolean?) {
            if (!this.updating) {
                val property1: BooleanProperty? = this.propertyRef1.get()
                val property2: BooleanProperty? = this.propertyRef2.get()
                if (property1 != null && property2 != null) {
                    try {
                        this.updating = true
                        if (property1 === observable) {
                            property2.set(newValue!!)
                        } else {
                            property1.set(newValue!!)
                        }
                    } catch (e: RuntimeException) {
                        try {
                            if (property1 === observable) {
                                property1.set(oldValue!!)
                            } else {
                                property2.set(oldValue!!)
                            }
                        } catch (e2: Exception) {
                            e2.addSuppressed(e)
                            unbind(property1, property2)
                            throw RuntimeException("Bidirectional binding failed together with an attempt to restore " +
                                    "the source property to the previous value. Removing the bidirectional binding " +
                                    "from properties $property1 and $property2", e2)
                        }
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

    private class BidirectionalDoubleBinding(property1: DoubleProperty, property2: DoubleProperty) :
            BidirectionalBinding<Number?>(property1, property2) {

        private val propertyRef1: WeakReference<DoubleProperty> = WeakReference(property1)

        private val propertyRef2: WeakReference<DoubleProperty> = WeakReference(property2)

        private var updating: Boolean = false

        override val property1: Property<Number?>?
            get() = this.propertyRef1.get()

        override val property2: Property<Number?>?
            get() = this.propertyRef2.get()

        override fun changed(observable: ObservableValue<out Number?>, oldValue: Number?, newValue: Number?) {
            if (!this.updating) {
                val property1: DoubleProperty? = this.propertyRef1.get()
                val property2: DoubleProperty? = this.propertyRef2.get()
                if (property1 != null && property2 != null) {
                    try {
                        this.updating = true
                        if (property1 === observable) {
                            property2.set(newValue!!.toDouble())
                        } else {
                            property1.set(newValue!!.toDouble())
                        }
                    } catch (e: RuntimeException) {
                        try {
                            if (property1 === observable) {
                                property1.set(oldValue!!.toDouble())
                            } else {
                                property2.set(oldValue!!.toDouble())
                            }
                        } catch (e2: Exception) {
                            e2.addSuppressed(e)
                            unbind(property1, property2)
                            throw RuntimeException("Bidirectional binding failed together with an attempt to restore " +
                                    "the source property to the previous value. Removing the bidirectional binding " +
                                    "from properties $property1 and $property2", e2)
                        }
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

    private class BidirectionalFloatBinding(property1: FloatProperty, property2: FloatProperty) :
            BidirectionalBinding<Number?>(property1, property2) {

        private val propertyRef1: WeakReference<FloatProperty> = WeakReference(property1)

        private val propertyRef2: WeakReference<FloatProperty> = WeakReference(property2)

        private var updating: Boolean = false

        override val property1: Property<Number?>?
            get() = this.propertyRef1.get()

        override val property2: Property<Number?>?
            get() = this.propertyRef2.get()

        override fun changed(observable: ObservableValue<out Number?>, oldValue: Number?, newValue: Number?) {
            if (!this.updating) {
                val property1: FloatProperty? = this.propertyRef1.get()
                val property2: FloatProperty? = this.propertyRef2.get()
                if (property1 != null && property2 != null) {
                    try {
                        this.updating = true
                        if (property1 === observable) {
                            property2.set(newValue!!.toFloat())
                        } else {
                            property1.set(newValue!!.toFloat())
                        }
                    } catch (e: RuntimeException) {
                        try {
                            if (property1 === observable) {
                                property1.set(oldValue!!.toFloat())
                            } else {
                                property2.set(oldValue!!.toFloat())
                            }
                        } catch (e2: Exception) {
                            e2.addSuppressed(e)
                            unbind(property1, property2)
                            throw RuntimeException("Bidirectional binding failed together with an attempt to restore " +
                                    "the source property to the previous value. Removing the bidirectional binding " +
                                    "from properties $property1 and $property2", e2)
                        }
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

    private class BidirectionalLongBinding(property1: LongProperty, property2: LongProperty) :
            BidirectionalBinding<Number?>(property1, property2) {

        private val propertyRef1: WeakReference<LongProperty> = WeakReference(property1)

        private val propertyRef2: WeakReference<LongProperty> = WeakReference(property2)

        private var updating: Boolean = false

        override val property1: Property<Number?>?
            get() = this.propertyRef1.get()

        override val property2: Property<Number?>?
            get() = this.propertyRef2.get()

        override fun changed(observable: ObservableValue<out Number?>, oldValue: Number?, newValue: Number?) {
            if (!this.updating) {
                val property1: LongProperty? = this.propertyRef1.get()
                val property2: LongProperty? = this.propertyRef2.get()
                if (property1 != null && property2 != null) {
                    try {
                        this.updating = true
                        if (property1 === observable) {
                            property2.set(newValue!!.toLong())
                        } else {
                            property1.set(newValue!!.toLong())
                        }
                    } catch (e: RuntimeException) {
                        try {
                            if (property1 === observable) {
                                property1.set(oldValue!!.toLong())
                            } else {
                                property2.set(oldValue!!.toLong())
                            }
                        } catch (e2: Exception) {
                            e2.addSuppressed(e)
                            unbind(property1, property2)
                            throw RuntimeException("Bidirectional binding failed together with an attempt to restore " +
                                    "the source property to the previous value. Removing the bidirectional binding " +
                                    "from properties $property1 and $property2", e2)
                        }
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

    private class BidirectionalIntBinding(property1: IntProperty, property2: IntProperty) :
            BidirectionalBinding<Number?>(property1, property2) {

        private val propertyRef1: WeakReference<IntProperty> = WeakReference(property1)

        private val propertyRef2: WeakReference<IntProperty> = WeakReference(property2)

        private var updating: Boolean = false

        override val property1: Property<Number?>?
            get() = this.propertyRef1.get()

        override val property2: Property<Number?>?
            get() = this.propertyRef2.get()

        override fun changed(observable: ObservableValue<out Number?>, oldValue: Number?, newValue: Number?) {
            if (!this.updating) {
                val property1: IntProperty? = this.propertyRef1.get()
                val property2: IntProperty? = this.propertyRef2.get()
                if (property1 != null && property2 != null) {
                    try {
                        this.updating = true
                        if (property1 === observable) {
                            property2.set(newValue!!.toInt())
                        } else {
                            property1.set(newValue!!.toInt())
                        }
                    } catch (e: RuntimeException) {
                        try {
                            if (property1 === observable) {
                                property1.set(oldValue!!.toInt())
                            } else {
                                property2.set(oldValue!!.toInt())
                            }
                        } catch (e2: Exception) {
                            e2.addSuppressed(e)
                            unbind(property1, property2)
                            throw RuntimeException("Bidirectional binding failed together with an attempt to restore " +
                                    "the source property to the previous value. Removing the bidirectional binding " +
                                    "from properties $property1 and $property2", e2)
                        }
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

    private class TypedGenericBidirectionalBinding<T>(property1: Property<T>, property2: Property<T>) :
            BidirectionalBinding<T>(property1, property2) {

        private val propertyRef1: WeakReference<Property<T>> = WeakReference(property1)

        private val propertyRef2: WeakReference<Property<T>> = WeakReference(property2)

        private var updating: Boolean = false

        override val property1: Property<T>?
            get() = this.propertyRef1.get()

        override val property2: Property<T>?
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
                                property1.value = oldValue
                            } else {
                                property2.value = oldValue
                            }
                        } catch (e2: Exception) {
                            e2.addSuppressed(e)
                            unbind(property1, property2)
                            throw RuntimeException("Bidirectional binding failed together with an attempt to restore " +
                                    "the source property to the previous value. Removing the bidirectional binding " +
                                    "from properties $property1 and $property2", e2)
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

    private class TypedNumberBidirectionalBinding<T : Number>(property1: Property<T>, property2: Property<Number?>) :
            BidirectionalBinding<Number?>(property1, property2) {

        private val propertyRef1: WeakReference<Property<T>> = WeakReference(property1)

        private val propertyRef2: WeakReference<Property<Number?>> = WeakReference(property2)

        private var updating: Boolean = false

        override val property1: Property<T>?
            get() = this.propertyRef1.get()

        override val property2: Property<Number?>?
            get() = this.propertyRef2.get()

        override fun changed(observable: ObservableValue<out Number?>, oldValue: Number?, newValue: Number?) {
            if (!this.updating) {
                val property1: Property<T>? = this.propertyRef1.get()
                val property2: Property<Number?>? = this.propertyRef2.get()
                if (property1 != null && property2 != null) {
                    try {
                        this.updating = true
                        if (property1 === observable) {
                            property2.value = newValue
                        } else {
                            property1.value = newValue as T
                        }
                    } catch (e: RuntimeException) {
                        try {
                            if (property1 === observable) {
                                property1.value = oldValue as T
                            } else {
                                property2.value = oldValue
                            }
                        } catch (e2: Exception) {
                            e2.addSuppressed(e)
                            unbind(property1, property2)
                            throw RuntimeException("Bidirectional binding failed together with an attempt to restore " +
                                    "the source property to the previous value. Removing the bidirectional binding " +
                                    "from properties $property1 and $property2", e2)
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

    abstract class StringConversionBidirectionalBinding<T>(stringProperty: Property<String?>,
            otherProperty: Property<T>) : BidirectionalBinding<Any?>(stringProperty, otherProperty) {

        private val stringPropertyRef: WeakReference<Property<String?>> = WeakReference(stringProperty)

        private val otherPropertyRef: WeakReference<Property<T>> = WeakReference(otherProperty)

        private var updating: Boolean = false

        override val property1: Property<String?>?
            get() = this.stringPropertyRef.get()

        override val property2: Property<T>?
            get() = this.otherPropertyRef.get()

        protected abstract fun toString(value: T): String

        protected abstract fun fromString(value: String): T

        override fun changed(observable: ObservableValue<out Any?>, oldValue: Any?, newValue: Any?) {
            if (!this.updating) {
                val property1: Property<String?>? = this.stringPropertyRef.get()
                val property2: Property<T>? = this.otherPropertyRef.get()
                if (property1 != null && property2 != null) {
                    try {
                        this.updating = true
                        if (property1 === observable) {
                            property2.value = fromString(newValue as String)
                        } else {
                            property1.value = toString(newValue as T)
                        }
                    } catch (e: RuntimeException) {
                        try {
                            if (property1 === observable) {
                                property1.value = oldValue as String
                            } else {
                                property2.value = oldValue as T
                            }
                        } catch (e2: Exception) {
                            e2.addSuppressed(e)
                            unbind(property1, property2)
                            throw RuntimeException("Bidirectional binding failed together with an attempt to restore " +
                                    "the source property to the previous value. Removing the bidirectional binding " +
                                    "from properties $property1 and $property2", e2)
                        }
                        throw RuntimeException("Bidirectional binding failed, setting to the previous value", e)
                    } finally {
                        this.updating = false
                    }
                } else {
                    property1?.removeListener(this)
                    property2?.removeListener(this as ChangeListener<in T>)
                }
            }
        }

    }

    private class StringFormatBidirectionalBinding<T>(stringProperty: Property<String?>, otherProperty: Property<T>,
            private val format: Format) : StringConversionBidirectionalBinding<T>(stringProperty, otherProperty) {

        override fun toString(value: T): String {
            return format.format(value)
        }

        @Throws(ParseException::class)
        override fun fromString(value: String): T {
            return format.parseObject(value) as T
        }

    }

    private class StringConverterBidirectionalBinding<T>(stringProperty: Property<String?>, otherProperty: Property<T>,
            private val converter: StringConverter<T>) :
            StringConversionBidirectionalBinding<T>(stringProperty, otherProperty) {

        override fun toString(value: T): String {
            return this.converter.toString(value)
        }

        override fun fromString(value: String): T {
            return this.converter.fromString(value)
        }

    }

    companion object {

        private fun checkParameters(property1: Any, property2: Any) {
            require(!(property1 === property2)) {"Cannot bind property to itself"}
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> bind(property1: Property<T>, property2: Property<T>): BidirectionalBinding<T> {
            checkParameters(property1, property2)
            val binding: BidirectionalBinding<*> = when {
                property1 is DoubleProperty && property2 is DoubleProperty -> BidirectionalDoubleBinding(property1,
                        property2)
                property1 is FloatProperty && property2 is FloatProperty -> BidirectionalFloatBinding(property1,
                        property2)
                property1 is LongProperty && property2 is LongProperty -> BidirectionalLongBinding(property1,
                        property2)
                property1 is IntProperty && property2 is IntProperty -> BidirectionalIntBinding(property1,
                        property2)
                property1 is DoubleProperty && property2 is DoubleProperty -> BidirectionalDoubleBinding(property1,
                        property2)
                else -> TypedGenericBidirectionalBinding(property1, property2)
            }
            property1.value = property2.value
            property1.addListener(binding as BidirectionalBinding<T>)
            property2.addListener(binding)
            return binding
        }

        fun <T> bind(stringProperty: Property<String?>, otherProperty: Property<T>, format: Format): Any {
            checkParameters(stringProperty, otherProperty)
            val binding: StringConversionBidirectionalBinding<T> =
                    StringFormatBidirectionalBinding(stringProperty, otherProperty, format)
            stringProperty.value = format.format(otherProperty.value)
            stringProperty.addListener(binding)
            otherProperty.addListener(binding)
            return binding
        }

        fun <T> bind(stringProperty: Property<String?>, otherProperty: Property<T>,
                converter: StringConverter<T>): Any {
            checkParameters(stringProperty, otherProperty)
            val binding: StringConversionBidirectionalBinding<T> =
                    StringConverterBidirectionalBinding(stringProperty, otherProperty, converter)
            stringProperty.value = converter.toString(otherProperty.value)
            stringProperty.addListener(binding)
            otherProperty.addListener(binding)
            return binding
        }

        fun <T> unbind(property1: Property<T>, property2: Property<T>) {
            checkParameters(property1, property2)
            val binding: BidirectionalBinding<Any?> = UntypedGenericBidirectionalBinding(property1, property2)
            property1.removeListener(binding)
            property2.removeListener(binding)
        }

        fun unbind(property1: Any, property2: Any) {
            checkParameters(property1, property2)
            val binding: BidirectionalBinding<Any?> = UntypedGenericBidirectionalBinding(property1, property2)
            if (property1 is ObservableValue<*>) {
                property1.removeListener(binding)
            }
            if (property2 is ObservableValue<*>) {
                property2.removeListener(binding)
            }
        }

    }
}