/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

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
    suspend fun downloadToStorage(url: String, filename: String, progress: (Int) -> Unit)
}