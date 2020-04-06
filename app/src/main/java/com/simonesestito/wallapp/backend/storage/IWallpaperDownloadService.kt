/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import android.content.Context
import com.simonesestito.wallapp.backend.model.Wallpaper

interface IWallpaperDownloadService {
    fun requestPermission(context: Context)
    fun handlePermissionResult(): Boolean
    fun downloadWallpaperToStorage(wallpaper: Wallpaper): DownloadTask
}