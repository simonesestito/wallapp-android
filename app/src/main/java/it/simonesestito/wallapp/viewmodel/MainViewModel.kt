package it.simonesestito.wallapp.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.backend.service.CategoryService
import it.simonesestito.wallapp.backend.service.WallpaperService

class MainViewModel : ViewModel() {
    val categories by lazy { CategoryService.getCategories() }

    fun getCategoryById(id: String) = CategoryService.getCategoryById(id)

    fun getWallpapersByCategoryId(categoryId: String) =
            WallpaperService.getWallpapersByCategoryId(categoryId)

    fun getWallpaperById(categoryId: String, wallpaperId: String) =
            WallpaperService.getWallpaper(categoryId, wallpaperId)
}
