package io.github.vinccool96.observationskt.beans.binding

import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import kotlin.test.assertTrue

object DependencyUtils {

    fun checkDependencies(seq: ObservableList<*>, vararg deps: Any) {
        // we want to check the source dependencies, therefore we have to remove all intermediate bindings
        val copy: ObservableList<Any?> = ObservableCollections.observableArrayList(seq)
        val it = copy.listIterator()
        while (it.hasNext()) {
            val obj = it.next()
            if (obj is Binding<*>) {
                it.remove()
                for (newDep in obj.dependencies) {
                    it.add(newDep!!)
                }
            }
        }
        for (obj in deps) {
            assertTrue(copy.contains(obj))
        }
    }

}