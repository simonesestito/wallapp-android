package it.simonesestito.wallapp.backend.repository

import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.palette.graphics.Palette
import com.google.firebase.storage.FileDownloadTask
import it.simonesestito.wallapp.annotations.WallpaperFormat
import it.simonesestito.wallapp.backend.model.Wallpaper
import java.io.File


interface IWallpaperRepository {
    /**
     * Get all the wallpapers in a given category
     * @param categoryId
     * @return Wallpapers list wrapped in a LiveData
     */
    fun getWallpapersByCategoryId(categoryId: String): LiveData<List<Wallpaper>>

    /**
     * Get the wallpaper document from Firestore
     * @param categoryId Wallpaper's category ID
     * @param wallpaperId
     * @return LiveData of the Firestore document
     */
    fun getWallpaper(categoryId: String, wallpaperId: String): LiveData<Wallpaper?>

    /**
     * Download the wallpaper to a [File]
     * @param wallpaper Target wallpaper
     * @param format Wallpaper format to download
     * @param destination Destination file where to download the wallpaper
     * @return Firebase file download task
     */
    fun downloadWallpaper(wallpaper: Wallpaper, @WallpaperFormat format: String, destination: File): FileDownloadTask

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
                      onPaletteReady: ((Palette) -> Unit)? = null)
}