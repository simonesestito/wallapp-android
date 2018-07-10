package it.simonesestito.wallapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

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