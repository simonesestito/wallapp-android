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

package com.simonesestito.wallapp.lifecycle.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simonesestito.wallapp.PREFS_APPLIED_WALLPAPERS_COUNTER
import com.simonesestito.wallapp.backend.model.DownloadStatus
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.backend.storage.DownloadService
import com.simonesestito.wallapp.backend.storage.IStorageDownloadService
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_BOTH
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_HOME
import com.simonesestito.wallapp.enums.WallpaperFormat
import com.simonesestito.wallapp.enums.WallpaperLocation
import com.simonesestito.wallapp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class WallpaperSetupViewModel @Inject constructor(
        private val downloadService: DownloadService,
        private val storageDownloadService: IStorageDownloadService
) : ViewModel() {
    private var currentTempFile: File? = null
    private val mutableDownloadStatus = MutableLiveData<DownloadStatus>()

    /**
     * Updated from the Fragment view
     */
    @WallpaperLocation
    var currentWallpaperLocation: Int = WALLPAPER_LOCATION_BOTH

    /**
     * Expose a standard [LiveData], not a [MutableLiveData]
     */
    fun getDownloadStatus(): LiveData<DownloadStatus> = mutableDownloadStatus

    /**
     * To be notified about the status, observe [getDownloadStatus] return value
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun applyWallpaper(context: Context,
                               wallpaper: Wallpaper,
                               @WallpaperFormat format: String,
                               @WallpaperLocation location: Int) {
        if (isTaskInProgress()) {
            return
        }

        if (!context.isConnectivityOnline()) {
            mutableDownloadStatus.postValue(DownloadStatus.Error)
            return
        }

        increaseWallpaperCounter(context)

        currentTempFile = withContext(Dispatchers.IO) {
            context.createCacheFile("wall-${wallpaper.id}-$format")
        }

        try {
            downloadService.downloadToFile(wallpaper.getStorageFileUrl(format), currentTempFile!!) {
                mutableDownloadStatus.postValue(DownloadStatus.Progressing(it))
            }
        } catch (e: IOException) {
            mutableDownloadStatus.postValue(DownloadStatus.Error)
            return
        }

        mutableDownloadStatus.postValue(DownloadStatus.Finalizing)
        supportApplyWallpaper(context, currentTempFile!!, location)
        mutableDownloadStatus.postValue(DownloadStatus.Success)
        withContext(Dispatchers.IO) { currentTempFile?.delete() }
    }

    /**
     * Apply a wallpaper in Preview Mode
     * First, do a backup
     * Then, apply the wallpaper to HOME location only
     */
    suspend fun applyPreviewWallpaper(context: Context, wallpaper: Wallpaper) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            mutableDownloadStatus.value = DownloadStatus.Error
            return
        }

        withContext(Dispatchers.IO) { backupWallpaper(context) }

        increaseWallpaperCounter(context)

        if (coroutineContext.isActive) {
            // Apply wallpaper only if task has not been cancelled
            // and status is still the same
            applyWallpaper(
                    context,
                    wallpaper,
                    getSuggestedWallpaperFormat(context.resources.displayMetrics),
                    WALLPAPER_LOCATION_HOME
            )
        } else {
            Log.e(this@WallpaperSetupViewModel.TAG, "Task cancellation detected. Doing nothing")
        }
    }

    fun stopDownloadTask() {
        if (!isTaskInProgress()) {
            currentTempFile?.delete()
        }
    }

    private fun increaseWallpaperCounter(context: Context) {
        context.sharedPreferences.edit {
            val oldCounter = context.sharedPreferences.getInt(PREFS_APPLIED_WALLPAPERS_COUNTER, 0)
            putInt(PREFS_APPLIED_WALLPAPERS_COUNTER, oldCounter + 1)
        }
    }

    /**
     * Get required storage permissions to download the wallpaper
     */
    val storagePermissions = storageDownloadService.requiredPermissions

    @Throws(SecurityException::class)
    suspend fun downloadToGallery(context: Context, wallpaper: Wallpaper, @WallpaperFormat format: String) {
        val extension = format.split('.').last()

        try {
            mutableDownloadStatus.postValue(DownloadStatus.Progressing(0))

            storageDownloadService.downloadToStorage(
                    context,
                    wallpaper.getStorageFileUrl(format),
                    "${wallpaper.categoryId}_${wallpaper.id}.$extension") {
                Log.d(TAG, "Download progress: $it")
                if (it != 100 || mutableDownloadStatus.value is DownloadStatus.Progressing)
                    mutableDownloadStatus.postValue(DownloadStatus.Progressing(it))
            }

            Log.d(TAG, "Download completed")
            mutableDownloadStatus.postValue(DownloadStatus.Success)
        } catch (e: IOException) {
            mutableDownloadStatus.postValue(DownloadStatus.Error)
        }
    }

    /**
     * Check if is safe or not to start a new download task
     * @return true if a task is in progress, starting a new task is not safe
     */
    private fun isTaskInProgress() =
            mutableDownloadStatus.value is DownloadStatus.Progressing ||
                    mutableDownloadStatus.value == DownloadStatus.Finalizing

}