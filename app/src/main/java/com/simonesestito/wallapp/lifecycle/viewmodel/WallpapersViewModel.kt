/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.viewmodel

import android.widget.ImageView
import androidx.lifecycle.*
import androidx.palette.graphics.Palette
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.backend.repository.CategoryRepository
import com.simonesestito.wallapp.backend.repository.WallpaperRepository
import com.simonesestito.wallapp.enums.CategoryGroup
import com.simonesestito.wallapp.enums.FORMAT_PREVIEW
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class WallpapersViewModel @Inject constructor(private val categoryRepository: CategoryRepository,
                                              private val wallpaperRepository: WallpaperRepository)
    : ViewModel() {
    private val allCategories by lazy { categoryRepository.getCategories() }

    // Cached values
    private val wallpapersByCategoryId = MutableLiveData<List<Wallpaper>?>()
    private val wallpaper = MutableLiveData<Wallpaper>()

    fun getCategoriesByGroup(@CategoryGroup group: String) =
            allCategories.map { list ->
                list.filter { category -> category.data.group == group }
            }.asLiveData(viewModelScope.coroutineContext)

    fun getWallpapersByCategoryId(categoryId: String): LiveData<List<Wallpaper>?> {
        if (wallpapersByCategoryId.value?.firstOrNull()?.categoryId != categoryId) {
            wallpapersByCategoryId.value = null
            viewModelScope.launch {
                val walls = wallpaperRepository.getWallpapersByCategoryId(categoryId)
                wallpapersByCategoryId.postValue(walls)
            }
        }
        return wallpapersByCategoryId
    }

    fun getWallpaperById(categoryId: String, wallpaperId: String): LiveData<Wallpaper?> {
        val wallpaperPath = "$categoryId/$wallpaperId"
        if (wallpaper.value?.fullId != wallpaperPath) {
            wallpaper.value = null
            viewModelScope.launch {
                val fetchedWallpaper = wallpaperRepository.getWallpaper(categoryId, wallpaperId)
                wallpaper.postValue(fetchedWallpaper)
            }
        }

        return wallpaper
    }

    fun updateSeenWallpapers(category: Category) {
        viewModelScope.launch {
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
}
