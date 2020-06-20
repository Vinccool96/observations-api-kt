package io.github.vinccool96.observationskt.beans

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

/**
 * Annotation that provides information about argument's name.
 *
 * @param value The name of the annotated argument
 *
 * @since JavaFX 8.0
 */
@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
annotation class NamedArg(val value: String = "")
