package it.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.backend.repository.impl.WallpaperRepository
import javax.inject.Inject


class WallpaperViewModel @Inject constructor(
        private val wallpaperRepository: WallpaperRepository
) : ViewModel() {
    fun getWallpapersByCategoryId(categoryId: String) =
            wallpaperRepository.getWallpapersByCategoryId(categoryId)

    fun getWallpaperById(categoryId: String, wallpaperId: String) =
            wallpaperRepository.getWallpaper(categoryId, wallpaperId)
}