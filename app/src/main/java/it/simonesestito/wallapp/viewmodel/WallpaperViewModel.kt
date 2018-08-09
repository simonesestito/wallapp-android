package it.simonesestito.wallapp.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.backend.service.WallpaperService


class WallpaperViewModel : ViewModel() {
    fun getWallpapersByCategoryId(categoryId: String) =
            WallpaperService.getWallpapersByCategoryId(categoryId)

    fun getWallpaperById(categoryId: String, wallpaperId: String) =
            WallpaperService.getWallpaper(categoryId, wallpaperId)
}