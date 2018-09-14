/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import com.simonesestito.wallapp.backend.repository.IWallpaperRepository
import javax.inject.Inject


class WallpaperViewModel @Inject constructor(
        private val wallpaperRepository: IWallpaperRepository
) : ViewModel() {
    fun getWallpapersByCategoryId(categoryId: String) =
            wallpaperRepository.getWallpapersByCategoryId(categoryId)

    fun getWallpaperById(categoryId: String, wallpaperId: String) =
            wallpaperRepository.getWallpaper(categoryId, wallpaperId)
}