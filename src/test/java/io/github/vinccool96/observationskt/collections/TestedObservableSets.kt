package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.property.SimpleSetProperty
import java.util.*
import kotlin.reflect.full.createType

@Suppress("PropertyName")
object TestedObservableSets {

    val HASH_SET: Callable<ObservableSet<String?>> = Callable { ObservableCollections.observableSet(HashSet()) }

    val TREE_SET: Callable<ObservableSet<String?>> = CallableTreeSetImpl()

    val LINKED_HASH_SET: Callable<ObservableSet<String?>> = Callable {
        ObservableCollections.observableSet(LinkedHashSet())
    }

    val CHECKED_OBSERVABLE_HASH_SET: Callable<ObservableSet<String?>> = Callable {
        ObservableCollections.checkedObservableSet(ObservableCollections.observableSet(HashSet()),
                String::class.createType(nullable = true))
    }

    val SYNCHRONIZED_OBSERVABLE_HASH_SET: Callable<ObservableSet<String?>> = Callable {
        ObservableCollections.synchronizedObservableSet(ObservableCollections.observableSet(HashSet()))
    }

    val OBSERVABLE_SET_PROPERTY: Callable<ObservableSet<String?>> = Callable {
        SimpleSetProperty(ObservableCollections.observableSet(HashSet()))
    }

    class CallableTreeSetImpl : Callable<ObservableSet<String?>> {

        override fun call(): ObservableSet<String?> {
            return ObservableCollections.observableSet(TreeSet())
        }

    }

}