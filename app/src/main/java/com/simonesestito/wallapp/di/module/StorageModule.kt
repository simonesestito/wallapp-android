/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.module

import android.os.Build
import com.simonesestito.wallapp.backend.storage.IWallpaperDownloadService
import com.simonesestito.wallapp.backend.storage.WallpaperDownloadServiceLegacy
import com.simonesestito.wallapp.backend.storage.WallpaperDownloadServiceV29
import dagger.Module
import dagger.Provides

@Module
class StorageModule {
    @Provides
    fun wallpaperDownloadService(): IWallpaperDownloadService {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            WallpaperDownloadServiceV29()
        else
            WallpaperDownloadServiceLegacy()
    }
}