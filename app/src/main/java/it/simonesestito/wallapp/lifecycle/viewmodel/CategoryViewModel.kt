package it.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import it.simonesestito.wallapp.backend.repository.CategoryRepository
import javax.inject.Inject

class CategoryViewModel @Inject constructor(private val categoryRepository: CategoryRepository) : ViewModel() {
    val categories by lazy { categoryRepository.getCategories() }

    fun getCategoryById(id: String) = categoryRepository.getCategoryById(id)
}
