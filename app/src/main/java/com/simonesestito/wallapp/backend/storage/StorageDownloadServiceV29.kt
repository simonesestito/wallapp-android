/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

/**
 * Implementation of [IStorageDownloadService] to work on Android Q and higher
 * It uses new storage access APIs.
 * It doesn't require any special permission to work.
 *
 * @see [IStorageDownloadService]
 */
@RequiresApi(Build.VERSION_CODES.Q)
class StorageDownloadServiceV29(private val downloadService: DownloadService) : IStorageDownloadService {
    override fun requestPermission(context: Fragment) {
        TODO("Not yet implemented")
    }

    override fun hasPermission(context: Context): Boolean {
        TODO("Not yet implemented")
    }

    override fun handlePermissionResult(requestCode: Int, grantResults: IntArray): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun downloadToStorage(context: Fragment, url: String, filename: String, progress: (Int) -> Unit) {
        TODO("Not yet implemented")
    }

}