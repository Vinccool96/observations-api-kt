package io.github.vinccool96.observationskt.collections

/**
 * Interface that receives notifications of changes to an `ObservableArray`.
 *
 * @param T The type of the `ObservableArray`
 *
 * @see ObservableArray
 */
interface ArrayChangeListener<T : ObservableArray<T>> {

    /**
     * Called after a change has been made to an [ObservableArray].
     *
     * @param observableArray The `ObservableArray`
     * @param sizeChanged indicates size of array changed
     * @param from A beginning (inclusive) of an interval related to the change
     * @param to An end (exclusive) of an interval related to the change.
     */
    fun onChanged(observableArray: T, sizeChanged: Boolean, from: Int, to: Int)

}