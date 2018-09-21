/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.repository.ICategoryRepository
import com.simonesestito.wallapp.backend.repository.IWallpaperRepository
import javax.inject.Inject

class WallpapersViewModel @Inject constructor(private val categoryRepository: ICategoryRepository,
                                              private val wallpaperRepository: IWallpaperRepository) : ViewModel() {
    val categories by lazy { categoryRepository.getCategories() }

    fun getWallpapersByCategoryId(categoryId: String) =
            wallpaperRepository.getWallpapersByCategoryId(categoryId)

    fun updateSeenWallpapers(category: Category) {
        categoryRepository.markCategoryAsViewed(category)
    }
}