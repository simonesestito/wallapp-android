/*
 * Copyright 2020 Simone Sestito
 * This file is part of WallApp.
 *
 * WallApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WallApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WallApp.  If not, see <http://www.gnu.org/licenses/>.
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