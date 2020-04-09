/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import android.content.Context
import androidx.fragment.app.Fragment

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
    fun requestPermission(context: Fragment)
    fun hasPermission(context: Context): Boolean
    fun handlePermissionResult(requestCode: Int, grantResults: IntArray): Boolean
    suspend fun downloadToStorage(context: Fragment, url: String, filename: String, progress: (Int) -> Unit)
}