package io.github.vinccool96.observationskt.sun.binding

import io.github.vinccool96.observationskt.beans.binding.StringBinding
import io.github.vinccool96.observationskt.beans.binding.StringExpression
import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.collections.ReturnsUnmodifiableCollection
import io.github.vinccool96.observationskt.util.ArrayUtils
import java.util.*

@Suppress("UNCHECKED_CAST")
abstract class StringFormatter : StringBinding() {

    private class ConvertStringBinding(private val observableValue: ObservableValue<*>) : StringBinding() {

        init {
            bind(observableValue)
        }

        override fun dispose() {
            unbind(observableValue)
        }

        override fun computeValue(): String? {
            val value: Any? = observableValue.value
            return if ((value == null)) "null" else value.toString()
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() = ObservableCollections.singletonObservableList(observableValue)

    }

    private class ConcatStringFormatter(private val dep: Array<ObservableValue<*>>, private val args: Array<Any>) :
            StringFormatter() {

        init {
            super.bind(*dep)
        }

        override fun dispose() {
            super.unbind(*dep)
        }

        override fun computeValue(): String? {
            val builder: StringBuilder = StringBuilder()
            for (obj: Any in args) {
                builder.append(extractValue(obj))
            }
            return builder.toString()
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() {
                return ObservableCollections.unmodifiableObservableList(ObservableCollections.observableArrayList(*dep))
            }
    }

    private class FormatStringFormatter(private val dep: Array<ObservableValue<*>>, private val args: Array<Any>,
            private val locale: Locale, private val format: String) : StringFormatter() {

        init {
            super.bind(*extractDependencies(*args))
        }

        override fun dispose() {
            super.unbind(*dep)
        }

        override fun computeValue(): String? {
            val values = extractValues(args)
            return String.format(locale, format, *values)
        }

        @get:ReturnsUnmodifiableCollection
        override val dependencies: ObservableList<*>
            get() {
                return ObservableCollections.unmodifiableObservableList(ObservableCollections.observableArrayList(*dep))
            }
    }

    companion object {

        private fun extractValue(obj: Any): Any? {
            return if (ObservableValue::class.isInstance(obj)) (obj as ObservableValue<*>).value else obj
        }

        private fun extractValues(objs: Array<Any>): Array<Any> {
            val n: Int = objs.size
            val values: Array<Any?> = arrayOfNulls(n)
            for (i in 0 until n) {
                values[i] = extractValue(objs[i])
            }
            return ArrayUtils.copyOfNotNulls(values)
        }

        fun extractDependencies(vararg args: Any?): Array<ObservableValue<*>> {
            val dependencies: MutableList<ObservableValue<*>> =
                    ArrayList()
            for (obj: Any? in args) {
                if (obj is ObservableValue<*>) {
                    dependencies.add(obj)
                }
            }
            return dependencies.toTypedArray()
        }

        fun convert(observableValue: ObservableValue<*>): StringExpression {
            return if (observableValue is StringExpression) {
                observableValue
            } else {
                ConvertStringBinding(observableValue)
            }
        }

        fun concat(vararg args: Any?): StringExpression {
            if (args.isEmpty()) {
                return StringConstant.valueOf("")
            }
            if (args.size == 1) {
                val cur: Any? = args[0]
                return if (cur is ObservableValue<*>) convert(cur) else StringConstant.valueOf(cur.toString())
            }
            if (extractDependencies(*args).isEmpty()) {
                val builder: StringBuilder = StringBuilder()
                for (obj: Any? in args) {
                    builder.append(obj)
                }
                return StringConstant.valueOf(builder.toString())
            }
            val dependencies = extractDependencies(*args)
            return ConcatStringFormatter(dependencies, args as Array<Any>)
        }

        fun format(locale: Locale, format: String, vararg args: Any): StringExpression {
            if (extractDependencies(*args).isEmpty()) {
                return StringConstant.valueOf(String.format(locale, format, *args))
            }
            val dependencies = extractDependencies(*args)
            val formatter: StringFormatter = FormatStringFormatter(dependencies, args as Array<Any>, locale, format)
            // Force calculation to check format
            formatter.get()
            return formatter
        }

        fun format(format: String, vararg args: Any): StringExpression {
            if (extractDependencies(*args).isEmpty()) {
                return StringConstant.valueOf(String.format(format, *args))
            }
            val formatter: StringFormatter = object : StringFormatter() {
                override fun dispose() {
                    super.unbind(*extractDependencies(*args))
                }

                override fun computeValue(): String? {
                    val values: Array<Any> = extractValues(args as Array<Any>)
                    return String.format(format, *values)
                }

                @get:ReturnsUnmodifiableCollection
                override val dependencies: ObservableList<*>
                    get() {
                        return ObservableCollections.unmodifiableObservableList(
                                ObservableCollections.observableArrayList(*extractDependencies(*args)))
                    }

                init {
                    super.bind(*extractDependencies(*args))
                }
            }
            // Force calculation to check format
            formatter.get()
            return formatter
        }
    }

}