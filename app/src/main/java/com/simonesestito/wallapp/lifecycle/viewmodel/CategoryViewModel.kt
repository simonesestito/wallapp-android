/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import com.simonesestito.wallapp.backend.repository.ICategoryRepository
import javax.inject.Inject

class CategoryViewModel @Inject constructor(private val categoryRepository: ICategoryRepository) : ViewModel() {
    val categories by lazy { categoryRepository.getCategories() }

    fun getCategoryById(id: String) = categoryRepository.getCategoryById(id)
}
