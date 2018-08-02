package it.simonesestito.wallapp.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.data.repository.CategoryRepository
import it.simonesestito.wallapp.data.repository.WallpaperRepository

class MainViewModel : ViewModel() {
    val categories by lazy { CategoryRepository.getCategories() }

    fun getCategoryById(id: String) = CategoryRepository.getCategoryById(id)

    fun getWallpapersByCategoryId(categoryId: String) =
            WallpaperRepository.getWallpapersByCategoryId(categoryId)

    fun getWallpaperById(categoryId: String, wallpaperId: String) =
            WallpaperRepository.getWallpaper(categoryId, wallpaperId)
}
