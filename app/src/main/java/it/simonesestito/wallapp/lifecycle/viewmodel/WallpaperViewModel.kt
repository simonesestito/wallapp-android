package it.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.backend.repository.WallpaperRepository


class WallpaperViewModel : ViewModel() {
    fun getWallpapersByCategoryId(categoryId: String) =
            WallpaperRepository.getWallpapersByCategoryId(categoryId)

    fun getWallpaperById(categoryId: String, wallpaperId: String) =
            WallpaperRepository.getWallpaper(categoryId, wallpaperId)
}