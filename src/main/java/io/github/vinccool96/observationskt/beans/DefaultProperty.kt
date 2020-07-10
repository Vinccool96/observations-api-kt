package io.github.vinccool96.observationskt.beans

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.TYPE

/**
 * Specifies a property to which child elements will be added or set when an explicit property is not given.
 *
 * @param value The name of the default property.
 */
@Inherited
@MustBeDocumented
@Retention(RUNTIME)
@Target(TYPE)
annotation class DefaultProperty(val value: String)
