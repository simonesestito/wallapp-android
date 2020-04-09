/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

/**
 * Download files
 */
class DownloadService @Inject constructor() {
    /**
     * Download a file by the given URL.
     * It downloads the file on a separate Thread
     *
     * @param url URL of the file
     * @param file File where to save the downloaded file
     *
     * @return Callback to cancel current download
     */
    suspend fun downloadToFile(url: String, file: File, progress: (Int) -> Unit) {
        val currentThreadHandler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
        progress(0)

        withContext(Dispatchers.IO) {
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
                    while (size > 0 && isActive) {
                        output.write(buffer, 0, size)
                        bytesCopied += size
                        size = input.read(buffer)

                        val currentProgress = bytesCopied * 100 / contentLength
                        if (currentProgress > oldProgress) {
                            currentThreadHandler.post { progress(currentProgress) }
                            oldProgress = currentProgress
                        }
                    }
                }
            }
            if (isActive) {
                currentThreadHandler.post { progress(100) }
            } else {
                file.delete()
            }
        }
    }
}