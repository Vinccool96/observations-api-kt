package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.property.SimpleListProperty
import java.util.*
import kotlin.reflect.full.createType

object TestedObservableLists {

    val ARRAY_LIST: Callable<ObservableList<String?>> = Callable { ObservableCollections.observableList(ArrayList()) }

    val LINKED_LIST: Callable<ObservableList<String?>> = Callable { ObservableCollections.observableList(LinkedList()) }

    val CHECKED_OBSERVABLE_ARRAY_LIST: Callable<ObservableList<String?>> = Callable {
        ObservableCollections.checkedObservableList(ObservableCollections.observableList(ArrayList()),
                String::class.createType(nullable = true))
    }

    val SYNCHRONIZED_OBSERVABLE_ARRAY_LIST: Callable<ObservableList<String?>> = Callable {
        ObservableCollections.synchronizedObservableList(ObservableCollections.observableList(ArrayList()))
    }

    val OBSERVABLE_LIST_PROPERTY: Callable<ObservableList<String?>> = Callable {
        SimpleListProperty(ObservableCollections.observableList(ArrayList()))
    }

}