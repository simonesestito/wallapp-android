package it.simonesestito.wallapp.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.data.repository.CategoryRepository
import it.simonesestito.wallapp.data.repository.WallpaperRepository

class MainViewModel : ViewModel() {
    private val categories = CategoryRepository.getCategories()

    fun getCategories() = this.categories

    fun getCategoryById(id: String) = CategoryRepository.getCategoryById(id)

    fun getWallpapersByCategoryId(categoryId: String)
            = WallpaperRepository.getWallpapersByCategoryId(categoryId)
}
