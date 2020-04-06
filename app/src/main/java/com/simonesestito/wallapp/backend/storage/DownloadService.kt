/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import android.os.Handler
import android.os.Looper
import com.simonesestito.wallapp.utils.ThreadUtils
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * Download files
 */
class DownloadService @Inject constructor(private val threadUtils: ThreadUtils) {
    /**
     * Download a file by the given URL.
     * It downloads the file on a separate Thread
     *
     * @param url URL of the file
     * @param file File where to save the downloaded file
     *
     * @return Callback to cancel current download
     */
    fun downloadToFile(url: String, file: File): DownloadTask {
        val isDownloading = AtomicBoolean(true)
        return DownloadTask(
                onCancelListener = { isDownloading.set(false) },
                task = { executeDownloadToFile(it, url, file, isDownloading) }
        )
    }

    private fun executeDownloadToFile(task: DownloadTask, url: String, file: File, isDownloading: AtomicBoolean) {
        val currentThreadHandler = Handler(Looper.myLooper() ?: Looper.getMainLooper())

        task.onProgressUpdate?.invoke(0)

        threadUtils.runOnIoThread {
            try {
                // Open network connection
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                val contentLength = urlConnection.contentLength
                var oldProgress = 0
                urlConnection.inputStream.use { input ->
                    file.outputStream().use { output ->
                        // Manually copy the buffer so we can check the progress
                        // instead of using InputStream#copyTo Kotlin extension
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytesCopied = 0

                        var size = input.read(buffer)
                        while (size > 0 && isDownloading.get()) {
                            output.write(buffer, 0, size)
                            bytesCopied += size
                            size = input.read(buffer)

                            val currentProgress = bytesCopied * 100 / contentLength
                            if (currentProgress > oldProgress) {
                                currentThreadHandler.post {
                                    task.onProgressUpdate?.invoke(currentProgress)
                                }
                                oldProgress = currentProgress
                            }
                        }

                        if (isDownloading.get()) {
                            currentThreadHandler.post { task.onProgressUpdate?.invoke(100) }
                            currentThreadHandler.post(task.onSuccess)
                            isDownloading.set(false)
                        } else {
                            currentThreadHandler.post(task.onCancel)
                        }
                    }
                }
            } catch (e: IOException) {
                currentThreadHandler.post { task.onError?.invoke(e) }
            }
        }
    }
}