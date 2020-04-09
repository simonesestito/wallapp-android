/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.repository.CategoryRepository
import com.simonesestito.wallapp.backend.repository.WallpaperRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class WallpapersViewModel @Inject constructor(private val categoryRepository: CategoryRepository,
                                              private val wallpaperRepository: WallpaperRepository)
    : ViewModel() {
    val allCategories by lazy { categoryRepository.getCategories() }
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun getWallpapersByCategoryId(categoryId: String) =
            wallpaperRepository.getWallpapersByCategoryId(categoryId)

    fun updateSeenWallpapers(category: Category) {
        coroutineScope.launch {
            categoryRepository.markCategoryAsViewed(category)
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}
