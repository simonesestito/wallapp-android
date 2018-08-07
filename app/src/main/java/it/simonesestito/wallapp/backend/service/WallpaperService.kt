package it.simonesestito.wallapp.backend.service

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.palette.graphics.Palette
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.annotations.FORMAT_PREVIEW
import it.simonesestito.wallapp.annotations.WallpaperFormat
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.utils.livedata.FirestoreLiveCollection
import it.simonesestito.wallapp.utils.livedata.FirestoreLiveDocument
import it.simonesestito.wallapp.utils.map
import it.simonesestito.wallapp.utils.mapList
import java.io.File

object WallpaperService {
    fun getWallpapersByCategoryId(categoryId: String): LiveData<List<Wallpaper>> {
        val ref = FirebaseFirestore.getInstance()
                .collection("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS")
                .whereEqualTo(KEY_PUBLISHED, true)
                .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)

        return FirestoreLiveCollection(ref).mapList {
            Wallpaper(it.id, categoryId)
        }
    }

    fun getWallpaper(categoryId: String, wallpaperId: String): LiveData<Wallpaper> {
        val ref = FirebaseFirestore.getInstance()
                .document("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS/$wallpaperId")

        return FirestoreLiveDocument(ref).map {
            Wallpaper(it.id, categoryId)
        }
    }

    fun loadWallpaperOn(wallpaper: Wallpaper,
                        @WallpaperFormat format: String,
                        imageView: ImageView,
                        showPlaceholder: Boolean = true) {
        val imageRef = FirebaseStorage
                .getInstance()
                .getReference(wallpaper.getStorageFilePath(format))

        GlideApp
                .with(imageView.context)
                .load(imageRef)
                .run {
                    return@run if (showPlaceholder)
                        placeholder(R.drawable.ic_image_placeholder)
                    else
                        this
                }
                .into(imageView)
    }


    fun getWallpaperColor(context: Context, wallpaper: Wallpaper, callback: (Palette) -> Unit) {
        val app = context.applicationContext as WallApp
        val cachedPalette = app.paletteCache[wallpaper]
        if (cachedPalette != null) {
            // Hit
            callback(cachedPalette)
        }

        // Miss

        val imageRef = FirebaseStorage
                .getInstance()
                .getReference(wallpaper.getStorageFilePath(FORMAT_PREVIEW))
        GlideApp
                .with(context)
                .asBitmap()
                .load(imageRef)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Palette.from(resource).generate { palette ->
                            palette ?: return@generate
                            callback(palette)
                            app.paletteCache[wallpaper] = palette
                        }
                    }
                })
    }

    fun downloadWallpaper(wallpaper: Wallpaper, @WallpaperFormat format: String, destination: File, onFinish: (Boolean, Exception?) -> Unit) =
            FirebaseStorage.getInstance()
                    .getReference(wallpaper.getStorageFilePath(format))
                    .getFile(destination)
                    .addOnCompleteListener { onFinish(it.isSuccessful, it.exception) }
}
