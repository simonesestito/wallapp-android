/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.repository.CategoryRepository
import com.simonesestito.wallapp.backend.repository.WallpaperRepository
import com.simonesestito.wallapp.enums.CATEGORY_GROUP_ORIGINAL
import com.simonesestito.wallapp.enums.CategoryGroup
import com.simonesestito.wallapp.utils.filter
import javax.inject.Inject

class WallpapersViewModel @Inject constructor(private val categoryRepository: CategoryRepository,
                                              private val wallpaperRepository: WallpaperRepository) : ViewModel() {
    private val allCategories by lazy { categoryRepository.getCategories() }

    private val mutableCategoryGroup = MutableLiveData<String>(CATEGORY_GROUP_ORIGINAL)
    var currentCategoryGroup: LiveData<String> = mutableCategoryGroup

    /**
     * LiveData with only the categories that should be shown on the UI at the moment
     */
    val currentCategories: LiveData<List<Category>> = Transformations.switchMap(currentCategoryGroup) { group ->
        allCategories.filter { category -> category.group == group }
    }

    fun updateCategoryGroup(@CategoryGroup group: String) {
        mutableCategoryGroup.value = group
    }

    fun getWallpapersByCategoryId(categoryId: String) =
            wallpaperRepository.getWallpapersByCategoryId(categoryId)

    fun updateSeenWallpapers(category: Category) {
        categoryRepository.markCategoryAsViewed(category)
    }
}
