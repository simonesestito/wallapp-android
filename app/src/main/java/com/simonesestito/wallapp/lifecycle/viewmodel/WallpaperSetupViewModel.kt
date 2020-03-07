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
import com.simonesestito.wallapp.backend.DownloadService
import com.simonesestito.wallapp.backend.model.DownloadStatus
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.backend.repository.WallpaperRepository
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_BOTH
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_HOME
import com.simonesestito.wallapp.enums.WallpaperFormat
import com.simonesestito.wallapp.enums.WallpaperLocation
import com.simonesestito.wallapp.utils.*
import java.io.File
import javax.inject.Inject

class WallpaperSetupViewModel @Inject constructor(
        private val wallpaperRepository: WallpaperRepository,
        private val threads: ThreadUtils,
        private val downloadService: DownloadService
) : ViewModel() {
    private var currentTempFile: File? = null
    private var currentDownloadTask: DownloadService.Task? = null
    private val mutableDownloadStatus = MutableLiveData<DownloadStatus>().apply {
        // Set initial value
        this.value = DownloadStatus.Progressing(0)
    }

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
    fun applyWallpaper(context: Context,
                       wallpaper: Wallpaper,
                       @WallpaperFormat format: String,
                       @WallpaperLocation location: Int) {
        if (isTaskInProgress()) {
            return
        }

        if (!context.isConnectivityOnline()) {
            mutableDownloadStatus.value = DownloadStatus.Error
            return
        }

        currentTempFile = context.createCacheFile("wall-${wallpaper.id}-$format")
        currentDownloadTask = downloadService.downloadToFile(
                url = wallpaper.getStorageFileUrl(format),
                file = currentTempFile!!,
                onProgressUpdate = {
                    mutableDownloadStatus.value = DownloadStatus.Progressing(it)
                },
                onSuccess = {
                    mutableDownloadStatus.value = DownloadStatus.Finalizing
                    threads.runOnIoThread {
                        val success = supportApplyWallpaper(context, currentTempFile!!, location)
                        mutableDownloadStatus.postValue(
                                if (success)
                                    DownloadStatus.Success
                                else
                                    DownloadStatus.Error)
                        currentTempFile?.delete()
                    }
                },
                onCancel = {
                    mutableDownloadStatus.value = DownloadStatus.Cancelled
                },
                onError = {
                    mutableDownloadStatus.value = DownloadStatus.Error
                }
        )
    }

    /**
     * Apply a wallpaper in Preview Mode
     * First, do a backup
     * Then, apply the wallpaper to HOME location only
     */
    fun applyPreviewWallpaper(context: Context, wallpaper: Wallpaper) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            mutableDownloadStatus.value = DownloadStatus.Error
            return
        }

        mutableDownloadStatus.value = DownloadStatus.Progressing(0)
        threads.runOnIoThread {
            backupWallpaper(context)
            threads.runOnMainThread {
                if (mutableDownloadStatus.value !is DownloadStatus.Cancelled) {
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
        }
    }

    fun stopDownloadTask() {
        currentDownloadTask?.cancel()

        if (!isTaskInProgress()) {
            currentTempFile?.delete()
        }

        mutableDownloadStatus.value = DownloadStatus.Cancelled
    }

    /**
     * Check if is safe or not to start a new download task
     * @return true if a task is in progress, starting a new task is not safe
     */
    private fun isTaskInProgress() =
            mutableDownloadStatus.value is DownloadStatus.Progressing ||
                    mutableDownloadStatus.value == DownloadStatus.Finalizing

}