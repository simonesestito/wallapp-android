/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.simonesestito.wallapp.backend.model.Wallpaper

@RequiresApi(Build.VERSION_CODES.Q)
class WallpaperDownloadServiceV29: IWallpaperDownloadService {
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