package it.simonesestito.wallapp.utils

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import it.simonesestito.wallapp.GlideApp
import it.simonesestito.wallapp.R

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

fun ImageView.setFirebaseImage(imageRef: StorageReference) {
    GlideApp
            .with(context)
            .load(imageRef)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(this)
}

fun ImageView.setFirebaseImage(url: String) = setFirebaseImage(
        FirebaseStorage.getInstance()
                .getReference(url)
)