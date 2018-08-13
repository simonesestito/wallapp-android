package it.simonesestito.wallapp.utils

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.storage.FirebaseStorage
import it.simonesestito.wallapp.BACKUP_WALLPAPER_FILENAME
import it.simonesestito.wallapp.GlideApp
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.WallApp
import it.simonesestito.wallapp.annotations.*
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.backend.repository.WallpaperRepository.downloadWallpaper
import java.io.File
import java.io.IOException

typealias PaletteListener = (Palette) -> Unit

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
                  onPaletteReady: PaletteListener? = null) {
    val imageRef = FirebaseStorage
            .getInstance()
            .getReference(wallpaper.getStorageFilePath(format))

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
                    val app = imageView.context.applicationContext as WallApp
                    val cachedPalette = app.paletteCache[wallpaper]
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
                        app.paletteCache[wallpaper] = palette
                        onPaletteReady?.invoke(palette)
                    }
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                    placeholder?.let { imageView.setImageDrawable(placeholder) }

                    // On loading started, if Palette is needed, check in cache
                    onPaletteReady?.let {
                        val app = imageView.context.applicationContext as WallApp
                        val cached = app.paletteCache[wallpaper]
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


/**
 * Apply the wallpaper
 * Support method: callable from each Platform version
 * @param context Context
 * @param wallpaperFile File which contains the wallpaper. Download it using [downloadWallpaper]
 * @param location Location where the wallpaper should be applied. On pre-Nougat has no effect
 * @return True in case of success, false otherwise
 */
@WorkerThread
fun supportApplyWallpaper(context: Context, wallpaperFile: File, @WallpaperLocation location: Int) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || location == WALLPAPER_LOCATION_BOTH) {
            applyWallpaper(context, wallpaperFile)
        } else {
            applyWallpaper(context, wallpaperFile, location)
        }

@RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
@WorkerThread
fun backupWallpaper(context: Context) {
    val destFile = File(context.noBackupFilesDir, BACKUP_WALLPAPER_FILENAME)
    ContextCompat.getSystemService(context, WallpaperManager::class.java)!!
            .drawable
            .toBitmap()
            .writeToFile(destFile, recycleOnEnd = true)
}

@WorkerThread
fun restoreWallpaper(context: Context) {
    val backupFile = File(context.noBackupFilesDir, BACKUP_WALLPAPER_FILENAME)
    if (!backupFile.exists()) {
        return
    }
    ContextCompat.getSystemService(context, WallpaperManager::class.java)!!
            .setStream(backupFile.inputStream())
    backupFile.delete()
}

@WallpaperFormat
fun getSuggestedWallpaperFormat(displayMetrics: DisplayMetrics): String {
    // Calculate user aspect ratio
    val userRatio = displayMetrics.widthPixels / displayMetrics.heightPixels.toDouble()

    // Find if user display is exactly one of the formats
    downloadableFormats.forEach { format ->
        if (format.dimensions.ratio == userRatio) {
            return format
        }
    }

    // If not exactly one of those formats, pick the best one
    return downloadableFormats
            .sortedBy { format -> Math.abs(format.dimensions.ratio - userRatio) }
            .first()
}

@Throws(IOException::class)
@WorkerThread
fun Bitmap.writeToFile(dest: File, recycleOnEnd: Boolean) {
    dest.outputStream().use {
        this.compress(Bitmap.CompressFormat.PNG, 100, it)
        if (recycleOnEnd) {
            this.recycle()
        }
    }
}

@WorkerThread
private fun applyWallpaper(context: Context, wallpaperFile: File): Boolean {
    val systemWallpaperService =
            ContextCompat.getSystemService(context, WallpaperManager::class.java)!!

    return try {
        systemWallpaperService.setStream(wallpaperFile.inputStream())
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@WorkerThread
private fun applyWallpaper(context: Context, wallpaperFile: File, @WallpaperLocation location: Int): Boolean {
    @WallpaperLocation val which: Int = when (location) {
        WALLPAPER_LOCATION_HOME -> WallpaperManager.FLAG_SYSTEM
        WALLPAPER_LOCATION_LOCK -> WallpaperManager.FLAG_LOCK
        WALLPAPER_LOCATION_BOTH -> WallpaperManager.FLAG_LOCK or WallpaperManager.FLAG_LOCK
        else -> throw IllegalArgumentException("Unknown location $location")
    }

    val systemWallpaperService =
            ContextCompat.getSystemService(context, WallpaperManager::class.java)!!

    return try {
        systemWallpaperService
                .setStream(wallpaperFile.inputStream(), null, true, which) != 0
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}