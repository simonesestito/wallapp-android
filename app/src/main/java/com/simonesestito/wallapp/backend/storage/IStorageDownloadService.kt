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

package com.simonesestito.wallapp.backend.storage

import android.content.Context

/**
 * Download a file from a URL to external storage (/sdcard).
 * It downloads a file in WallApp relative subdir of Pictures system directory.
 * There are several implementations depending on the API level.
 *
 * Sub directory used: [com.simonesestito.wallapp.PICTURES_DOWNLOAD_SUBDIR]
 *
 * @see StorageDownloadServiceV29
 * @see StorageDownloadServiceLegacy
 */
interface IStorageDownloadService {
    val requiredPermissions: Array<String>
    suspend fun downloadToStorage(
        context: Context,
        url: String,
        filename: String,
        progress: (Int) -> Unit
    )
}