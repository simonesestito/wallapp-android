package it.simonesestito.wallapp.utils

import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import it.simonesestito.wallapp.GlideApp
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.annotations.FORMAT_16_9
import it.simonesestito.wallapp.annotations.FORMAT_18_9
import it.simonesestito.wallapp.annotations.WallpaperFormat
import it.simonesestito.wallapp.annotations.dimensions

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

@WallpaperFormat
fun getSuggestedWallpaperFormat(displayMetrics: DisplayMetrics): String {
    // Calculate user aspect ratio
    val userRatio = displayMetrics.widthPixels / displayMetrics.heightPixels.toDouble()

    val formats = arrayOf(FORMAT_16_9, FORMAT_18_9)

    // Find if user display is exactly one of the formats
    formats.forEach { format ->
        if (format.dimensions.ratio == userRatio) {
            return format
        }
    }

    // If not exactly one of those formats, pick the best one
    return formats
            .sortedBy { format -> Math.abs(format.dimensions.ratio - userRatio) }
            .first()
}