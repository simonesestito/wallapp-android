/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.module

import android.os.Build
import com.simonesestito.wallapp.backend.storage.DownloadService
import com.simonesestito.wallapp.backend.storage.IStorageDownloadService
import com.simonesestito.wallapp.backend.storage.StorageDownloadServiceLegacy
import com.simonesestito.wallapp.backend.storage.StorageDownloadServiceV29
import dagger.Module
import dagger.Provides

@Module
class StorageModule {
    @Provides
    fun storageDownloadService(downloadService: DownloadService): IStorageDownloadService {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            StorageDownloadServiceV29(downloadService)
        else
            StorageDownloadServiceLegacy(downloadService)
    }
}