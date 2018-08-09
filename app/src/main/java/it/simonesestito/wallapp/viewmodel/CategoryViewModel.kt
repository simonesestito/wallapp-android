package it.simonesestito.wallapp.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.backend.service.CategoryService

class CategoryViewModel : ViewModel() {
    val categories by lazy { CategoryService.getCategories() }

    fun getCategoryById(id: String) = CategoryService.getCategoryById(id)
}
