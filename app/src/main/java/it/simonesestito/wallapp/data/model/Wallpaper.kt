package it.simonesestito.wallapp.data.model

import it.simonesestito.wallapp.annotations.WallpaperFormat
import it.simonesestito.wallapp.utils.Identifiable


data class Wallpaper(override val id: String, val categoryId: String) : Identifiable<String> {
    fun getStoragePath(@WallpaperFormat format: String)
            = "categories/$categoryId/wallpapers/$id/$format"
}