/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.viewmodel

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.backend.repository.CategoryRepository
import com.simonesestito.wallapp.backend.repository.WallpaperRepository
import com.simonesestito.wallapp.enums.CategoryGroup
import com.simonesestito.wallapp.enums.FORMAT_PREVIEW
import com.simonesestito.wallapp.utils.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class WallpapersViewModel @Inject constructor(private val categoryRepository: CategoryRepository,
                                              private val wallpaperRepository: WallpaperRepository)
    : ViewModel() {
    private val allCategories by lazy { categoryRepository.getCategories() }
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Cached values
    private var wallpapersCategoryId: String = ""
    private var wallpapersByCategoryId: LiveData<List<Wallpaper>>? = null
    private var wallpaperId: String = ""
    private var wallpaperById: LiveData<Wallpaper?>? = null

    fun getCategoriesByGroup(@CategoryGroup group: String) =
            allCategories.map { list ->
                list.filter { category -> category.data.group == group }
            }

    fun getWallpapersByCategoryId(categoryId: String): LiveData<List<Wallpaper>> {
        if (wallpapersCategoryId != categoryId) {
            wallpapersCategoryId = categoryId
            wallpapersByCategoryId = wallpaperRepository.getWallpapersByCategoryId(categoryId)
        }

        return wallpapersByCategoryId!!
    }

    fun getWallpaperById(categoryId: String, wallpaperId: String): LiveData<Wallpaper?> {
        if (this.wallpaperId != "$categoryId/$wallpaperId") {
            this.wallpaperId = "$categoryId/$wallpaperId"
            this.wallpaperById = wallpaperRepository.getWallpaper(categoryId, wallpaperId)
        }

        return this.wallpaperById!!
    }

    fun updateSeenWallpapers(category: Category) {
        coroutineScope.launch {
            categoryRepository.markCategoryAsViewed(category)
        }
    }

    fun loadWallpaperOn(wallpaper: Wallpaper, target: ImageView, callback: (Palette) -> Unit) {
        wallpaperRepository.loadWallpaper(
                wallpaper,
                FORMAT_PREVIEW,
                imageView = target,
                useExactFormatSize = true,
                onPaletteReady = callback
        )
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}
