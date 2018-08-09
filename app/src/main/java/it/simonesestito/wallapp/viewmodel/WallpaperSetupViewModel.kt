package it.simonesestito.wallapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FileDownloadTask
import it.simonesestito.wallapp.annotations.*
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.backend.service.WallpaperService
import java.io.File
import android.service.wallpaper.WallpaperService as WallpaperManager


class WallpaperSetupViewModel : ViewModel() {
    private var currentTempFile: File? = null
    private var currentFirebaseTask: FileDownloadTask? = null
    private val mutableDownloadStatus = MutableLiveData<@DownloadStatus Int>().apply {
        // Set initial value
        this.value = STATUS_NOTHING
    }

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

        currentTempFile = createTempFile("wall-${wallpaper.id}-$format")
        currentFirebaseTask = WallpaperService.downloadWallpaper(wallpaper, format, currentTempFile!!).apply {
            addOnCanceledListener { mutableDownloadStatus.value = STATUS_NOTHING }
            addOnFailureListener { mutableDownloadStatus.value = STATUS_ERROR }
            addOnSuccessListener { _ ->
                mutableDownloadStatus.value = STATUS_FINALIZING
                val success =
                        WallpaperService.supportApplyWallpaper(context, currentTempFile!!, location)
                mutableDownloadStatus.value = if (success) STATUS_SUCCESS else STATUS_ERROR
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
    }

    /**
     * Check if is safe or not to start a new download task
     * @return true if a task is in progress, starting a new task is not safe
     */
    private fun isTaskInProgress() =
            mutableDownloadStatus.value == STATUS_DOWNLOADING ||
                    mutableDownloadStatus.value == STATUS_FINALIZING

}