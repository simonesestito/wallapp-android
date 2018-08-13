package it.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.backend.repository.CategoryRepository

class CategoryViewModel : ViewModel() {
    val categories by lazy { CategoryRepository.getCategories() }

    fun getCategoryById(id: String) = CategoryRepository.getCategoryById(id)
}
