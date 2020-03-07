/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend

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
    class Task(private val onCancelListener: () -> Unit) {
        fun cancel() = onCancelListener()
    }

    /**
     * Download a file by the given URL.
     * It downloads the file on a separate Thread
     *
     * @param url URL of the file
     * @param file File where to save the downloaded file
     *
     * @return Callback to cancel current download
     */
    fun downloadToFile(url: String,
                       file: File,
                       onProgressUpdate: (Int) -> Unit,
                       onError: (Exception) -> Unit,
                       onSuccess: () -> Unit,
                       onCancel: () -> Unit
    ): Task {
        val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
        val isDownloading = AtomicBoolean(true)
        val task = Task(onCancelListener = { isDownloading.set(false) })

        onProgressUpdate(0)

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
                                handler.post { onProgressUpdate(currentProgress) }
                                oldProgress = currentProgress
                            }
                        }

                        if (isDownloading.get()) {
                            handler.post { onProgressUpdate(100) }
                            handler.post(onSuccess)
                            isDownloading.set(false)
                        } else {
                            handler.post { onCancel() }
                        }
                    }
                }
            } catch (e: IOException) {
                handler.post { onError(e) }
            }
        }

        return task
    }
}