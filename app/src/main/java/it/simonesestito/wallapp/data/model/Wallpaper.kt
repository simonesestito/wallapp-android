package it.simonesestito.wallapp.data.model

import it.simonesestito.wallapp.FIRESTORE_CATEGORIES
import it.simonesestito.wallapp.STORAGE_WALLPAPERS
import it.simonesestito.wallapp.annotations.WallpaperFormat
import it.simonesestito.wallapp.utils.Identifiable


data class Wallpaper(override val id: String, val categoryId: String) : Identifiable<String> {
    fun getStoragePath(@WallpaperFormat format: String) = "$FIRESTORE_CATEGORIES/$categoryId/$STORAGE_WALLPAPERS/$id/$format"
}