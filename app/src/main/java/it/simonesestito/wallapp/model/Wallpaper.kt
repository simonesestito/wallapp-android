package it.simonesestito.wallapp.model

import it.simonesestito.wallapp.annotations.WallpaperFormat


data class Wallpaper(val id: String, val categoryId: String) {
    fun getStoragePath(@WallpaperFormat format: String)
            = "categories/$categoryId/wallpapers/$id/$format.png"
}