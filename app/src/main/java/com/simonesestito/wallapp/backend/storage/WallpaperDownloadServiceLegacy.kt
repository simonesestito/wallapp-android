/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import com.simonesestito.wallapp.backend.model.Wallpaper

@TargetApi(Build.VERSION_CODES.P)
class WallpaperDownloadServiceLegacy: IWallpaperDownloadService {
    override fun downloadWallpaperToStorage(wallpaper: Wallpaper): DownloadTask {
        TODO("Not yet implemented")
    }

    override fun requestPermission(context: Context) {
        TODO("Not yet implemented")
    }

    override fun handlePermissionResult(): Boolean {
        TODO("Not yet implemented")
    }
}