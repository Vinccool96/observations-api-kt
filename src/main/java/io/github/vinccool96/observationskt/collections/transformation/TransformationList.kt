package io.github.vinccool96.observationskt.collections.transformation

import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableListBase
import io.github.vinccool96.observationskt.collections.WeakListChangeListener

/**
 * A base class for all lists that wraps other lists in a way that changes the list's elements, order, size or generally
 * its structure.
 *
 * If the source list is observable, a listener is automatically added to it and the events are delegated to
 * [sourceChanged]
 *
 * @param E the type parameter of this list
 * @param F the upper bound of the type of the source list
 *
 * @constructor Creates a new Transformation list wrapped around the source list.
 *
 * @param source the wrapped list
 *
 */
abstract class TransformationList<E, F>(source: ObservableList<out F>) : ObservableListBase<E>(), ObservableList<E> {

    /**
     * Contains the source list of this transformation list. This is never null and should be used to directly access
     * source list content. It is the source list specified in the constructor of this transformation list.
     *
     * @return The List that is directly wrapped by this TransformationList
     */
    val source: ObservableList<out F>

    /**
     * This field contains the result of expression "source instanceof [ObservableList]". If this is true, it is
     * possible to do transforms online.
     */
    private val sourceListener: ListChangeListener<F>

    init {
        this.source = source
        sourceListener = ListChangeListener {change -> sourceChanged(change)}
        this.source.addListener(WeakListChangeListener(this.sourceListener))
    }

    /**
     * Checks whether the provided list is in the chain under this `TransformationList`.
     *
     * This means the list is either the direct source as returned by [source] or the direct source is a
     * `TransformationList`, and the list is in its transformation chain.
     *
     * @param list the list to check
     *
     * @return true if the list is in the transformation chain as specified above.
     */
    fun isInTransformationChain(list: ObservableList<*>): Boolean {
        if (this.source === list) {
            return true
        }
        var currentSource: MutableList<*> = this.source
        while (currentSource is TransformationList<*, *>) {
            currentSource = currentSource.source
            if (currentSource === list) {
                return true
            }
        }
        return false
    }

    /**
     * Called when a change from the source is triggered.
     *
     * @param c the change
     */
    protected abstract fun sourceChanged(c: Change<out F>)

    /**
     * Maps the index of this list's element to an index in the direct source list.
     *
     * @param index the index in this list
     *
     * @return the index of the element's origin in the source list
     *
     * @see source
     */
    abstract fun getSourceIndex(index: Int): Int

    /**
     * Maps the index of this list's element to an index of the provided `list`.
     *
     * The `list` must be in the transformation chain.
     *
     * @param list a list from the transformation chain
     * @param index the index of an element in this list
     *
     * @return the index of the element's origin in the provided list
     *
     * @see isInTransformationChain
     */
    fun getSourceIndexFor(list: ObservableList<*>, index: Int): Int {
        if (!isInTransformationChain(list)) {
            throw IllegalArgumentException("Provided list is not in the transformation chain of this transformation " +
                    "list")
        }
        var currentSource: List<*> = this.source
        var idx = getSourceIndex(index)
        while (currentSource !== list && currentSource is TransformationList<*, *>) {
            val tSource = currentSource
            idx = tSource.getSourceIndex(idx)
            currentSource = tSource.source
        }
        return idx
    }

}