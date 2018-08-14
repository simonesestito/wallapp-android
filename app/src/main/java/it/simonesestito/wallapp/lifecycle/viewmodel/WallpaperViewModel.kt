package it.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.backend.repository.IWallpaperRepository
import javax.inject.Inject


class WallpaperViewModel @Inject constructor(
        private val wallpaperRepository: IWallpaperRepository
) : ViewModel() {
    fun getWallpapersByCategoryId(categoryId: String) =
            wallpaperRepository.getWallpapersByCategoryId(categoryId)

    fun getWallpaperById(categoryId: String, wallpaperId: String) =
            wallpaperRepository.getWallpaper(categoryId, wallpaperId)
}