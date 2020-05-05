/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
                mutableDownloadStatus.postValue(DownloadStatus.Progressing(it))
            }

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