package it.simonesestito.wallapp.ui

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.persistence.repository.CategoryRepository
import it.simonesestito.wallapp.persistence.repository.WallpaperRepository

class MainViewModel : ViewModel() {
    private val categories = CategoryRepository.getCategories()

    fun getCategories() = this.categories

    fun getCategoryById(id: String) = CategoryRepository.getCategoryById(id)

    fun getWallpapersByCategoryId(categoryId: String)
            = WallpaperRepository.getWallpapersByCategoryId(categoryId)
}
