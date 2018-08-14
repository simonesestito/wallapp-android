package it.simonesestito.wallapp.backend.repository

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.palette.graphics.Palette
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.annotations.WallpaperFormat
import it.simonesestito.wallapp.backend.cache.PaletteCache
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveCollection
import it.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveDocument
import it.simonesestito.wallapp.utils.map
import it.simonesestito.wallapp.utils.mapList
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperRepository @Inject constructor(private val paletteCache: PaletteCache,
                                              private val firestore: FirebaseFirestore,
                                              private val storage: FirebaseStorage) {
    /**
     * Get all the wallpapers in a given category
     * @param categoryId
     * @return Wallpapers list wrapped in a LiveData
     */
    fun getWallpapersByCategoryId(categoryId: String): LiveData<List<Wallpaper>> {
        val ref = firestore
                .collection("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS")
                .whereEqualTo(KEY_PUBLISHED, true)
                .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)

        return FirestoreLiveCollection(ref).mapList {
            Wallpaper(it.id, categoryId)
        }
    }

    /**
     * Get the wallpaper document from Firestore
     * @param categoryId Wallpaper's category ID
     * @param wallpaperId
     * @return LiveData of the Firestore document
     */
    fun getWallpaper(categoryId: String, wallpaperId: String): LiveData<Wallpaper> {
        val ref = firestore
                .document("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS/$wallpaperId")

        return FirestoreLiveDocument(ref).map {
            Wallpaper(it.id, categoryId)
        }
    }

    /**
     * Download the wallpaper to a [File]
     * @param wallpaper Target wallpaper
     * @param format Wallpaper format to download
     * @param destination Destination file where to download the wallpaper
     * @return Firebase file download task
     */
    fun downloadWallpaper(wallpaper: Wallpaper, @WallpaperFormat format: String, destination: File) =
            storage
                    .getReference(wallpaper.getStorageFilePath(format))
                    .getFile(destination)

    /**
     * Load wallpaper and display in an ImageView or load its palette
     *
     * It's a single method rather than 2 different methods (one for ImageView, one for Palette)
     * to optimize the requests.
     * [it.simonesestito.wallapp.ui.fragment.WallpaperFragment] for example requires to both load
     * the wallpaper and the palette.
     * With this unique method, it starts just a single Glide request (instead of 2)
     *
     * @param wallpaper Object description of the subject wallpaper
     * @param format Wallpaper format requested
     * @param imageView ImageView where to display the wallpaper
     * @param onPaletteReady Callback called when a Palette is ready to be used, nullable
     */
    @MainThread
    fun loadWallpaper(wallpaper: Wallpaper,
                      @WallpaperFormat format: String,
                      imageView: ImageView,
                      onPaletteReady: ((Palette) -> Unit)? = null) {
        val imageRef = storage.getReference(wallpaper.getStorageFilePath(format))

        GlideApp
                .with(imageView)
                .asBitmap()
                .load(imageRef)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageView.setImageBitmap(resource)

                        // Generate Palette looking into cache first
                        // If onPaletteReady is null the caller doesn't need Palette
                        // But we can put it in cache for future usages
                        val cachedPalette = paletteCache[wallpaper]
                        if (onPaletteReady == null && cachedPalette != null) {
                            // Caller doesn't need any Palette and it's already present in cache
                            // Do nothing
                            return
                        }

                        if (onPaletteReady != null && cachedPalette != null) {
                            // Palette needed by caller and present in cache
                            onPaletteReady(cachedPalette)
                        }

                        // Palette is not in cache
                        // Calculate it
                        Palette.from(resource).generate { palette ->
                            palette ?: return@generate
                            paletteCache[wallpaper] = palette
                            onPaletteReady?.invoke(palette)
                        }
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        placeholder?.let { imageView.setImageDrawable(placeholder) }

                        // On loading started, if Palette is needed, check in cache
                        onPaletteReady?.let {
                            val cached = paletteCache[wallpaper]
                            if (cached != null) {
                                onPaletteReady(cached)
                            }
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        super.onLoadCleared(placeholder)
                        placeholder?.let { imageView.setImageDrawable(placeholder) }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        errorDrawable?.let { imageView.setImageDrawable(errorDrawable) }
                    }
                })
    }
}
