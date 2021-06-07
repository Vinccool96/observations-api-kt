package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.property.SimpleMapProperty
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.createType

object TestedObservableMaps {

    val HASH_MAP: Callable<ObservableMap<String?, String?>> = Callable {
        ObservableCollections.observableMap(HashMap())
    }

    val TREE_MAP: Callable<ObservableMap<String?, String?>> = CallableTreeMapImpl()

    val LINKED_HASH_MAP: Callable<ObservableMap<String?, String?>> = Callable {
        ObservableCollections.observableMap(LinkedHashMap())
    }

    val CONCURRENT_HASH_MAP: Callable<ObservableMap<String?, String?>> = CallableConcurrentHashMapImpl()

    val CHECKED_OBSERVABLE_HASH_MAP: Callable<ObservableMap<String?, String?>> = Callable {
        ObservableCollections.checkedObservableMap(ObservableCollections.observableMap(HashMap()),
                String::class.createType(nullable = true), String::class.createType(nullable = true))
    }

    val SYNCHRONIZED_OBSERVABLE_HASH_MAP: Callable<ObservableMap<String?, String?>> = Callable {
        ObservableCollections.synchronizedObservableMap(ObservableCollections.observableMap(HashMap()))
    }

    val OBSERVABLE_MAP_PROPERTY: Callable<ObservableMap<String?, String?>> = Callable {
        SimpleMapProperty(ObservableCollections.observableMap(HashMap()))
    }

    class CallableTreeMapImpl : Callable<ObservableMap<String?, String?>> {

        override fun call(): ObservableMap<String?, String?> {
            return ObservableCollections.observableMap(TreeMap())
        }

    }

    class CallableConcurrentHashMapImpl : Callable<ObservableMap<String?, String?>> {

        override fun call(): ObservableMap<String?, String?> {
            return ObservableCollections.observableMap(ConcurrentHashMap())
        }

    }

}