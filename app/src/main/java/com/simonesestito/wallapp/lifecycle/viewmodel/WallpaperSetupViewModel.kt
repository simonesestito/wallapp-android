/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
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
import com.google.firebase.storage.FileDownloadTask
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.backend.repository.WallpaperRepository
import com.simonesestito.wallapp.enums.*
import com.simonesestito.wallapp.utils.*
import java.io.File
import javax.inject.Inject
import android.service.wallpaper.WallpaperService as WallpaperManager

class WallpaperSetupViewModel @Inject constructor(
        private val wallpaperRepository: WallpaperRepository,
        private val threads: ThreadUtils
) : ViewModel() {
    private var currentTempFile: File? = null
    private var currentFirebaseTask: FileDownloadTask? = null
    private val mutableDownloadStatus = MutableLiveData<@DownloadStatus Int>().apply {
        // Set initial value
        this.value = STATUS_NOTHING
    }

    /**
     * Updated from the Fragment view
     */
    @WallpaperLocation var currentWallpaperLocation: Int = WALLPAPER_LOCATION_BOTH

    /**
     * Expose a standard [LiveData], not a [MutableLiveData]
     */
    fun getDownloadStatus(): LiveData<Int> = mutableDownloadStatus

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

        mutableDownloadStatus.value = STATUS_DOWNLOADING

        if (!context.isConnectivityOnline()) {
            mutableDownloadStatus.value = STATUS_ERROR
            return
        }

        currentTempFile = context.createCacheFile("wall-${wallpaper.id}-$format")
        currentFirebaseTask = wallpaperRepository.downloadWallpaper(wallpaper, format, currentTempFile!!).apply {
            addOnCanceledListener { mutableDownloadStatus.value = STATUS_NOTHING }
            addOnFailureListener { mutableDownloadStatus.value = STATUS_ERROR }
            addOnSuccessListener { _ ->
                mutableDownloadStatus.value = STATUS_FINALIZING
                threads.runOnIoThread {
                    val success = supportApplyWallpaper(context, currentTempFile!!, location)
                    mutableDownloadStatus.postValue(if (success) STATUS_SUCCESS else STATUS_ERROR)
                    currentTempFile?.delete()
                }
            }
        }
    }

    /**
     * Apply a wallpaper in Preview Mode
     * First, do a backup
     * Then, apply the wallpaper to HOME location only
     */
    fun applyPreviewWallpaper(context: Context, wallpaper: Wallpaper) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            mutableDownloadStatus.value = STATUS_ERROR
            return
        }

        mutableDownloadStatus.value = STATUS_INIT
        threads.runOnIoThread {
            backupWallpaper(context)
            threads.runOnMainThread {
                if (mutableDownloadStatus.value == STATUS_INIT) {
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
                    mutableDownloadStatus.value = STATUS_NOTHING
                }
            }
        }
    }

    fun stopDownloadTask() {
        if (currentFirebaseTask?.isInProgress == true) {
            currentFirebaseTask?.cancel()
        }

        if (!isTaskInProgress()) {
            currentTempFile?.delete()
        }

        mutableDownloadStatus.value = STATUS_NOTHING
    }

    /**
     * Check if is safe or not to start a new download task
     * @return true if a task is in progress, starting a new task is not safe
     */
    private fun isTaskInProgress() =
            mutableDownloadStatus.value == STATUS_DOWNLOADING ||
                    mutableDownloadStatus.value == STATUS_FINALIZING

}