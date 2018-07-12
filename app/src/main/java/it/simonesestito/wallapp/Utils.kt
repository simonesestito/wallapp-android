package it.simonesestito.wallapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView

val Any.TAG: String
    get() = this.javaClass.simpleName

fun <X, Y> LiveData<X>.map(converter: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, converter)
}

fun <L : List<I>, I, T> LiveData<L>.mapList(converter: (I) -> T): LiveData<List<T>> {
    return Transformations.map(this) { list ->
        list.map(converter)
    }
}

inline fun RecyclerView.onScrollListener(crossinline listener: (RecyclerView) -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            listener(recyclerView)
        }
    })
}