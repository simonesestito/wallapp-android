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

package com.simonesestito.wallapp.backend.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
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
     * @param out Download to an OutputStream
     * @param progress Callback called on progress update, NOT necessarily on the main thread
     */
    suspend fun downloadToOutputStream(url: String, out: OutputStream, progress: (Int) -> Unit) {
        progress(0)

        withContext(Dispatchers.IO) {
            // Open network connection
            val urlConnection = URL(url).openConnection() as HttpURLConnection
            val contentLength = urlConnection.contentLength
            var oldProgress = 0
            urlConnection.inputStream.use { input ->
                out.use { output ->
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
                            progress(currentProgress)
                            oldProgress = currentProgress
                        }
                    }
                }
            }

            if (isActive) {
                progress(100)
            } else if (out is NamedFileOutputStream) {
                out.file.delete()
            }

            return@withContext
        }
    }

    /**
     * Download a file by the given URL.
     * It downloads the file on a separate Thread
     *
     * @param url URL of the file
     * @param file File where to save the downloaded file
     * @param progress Callback called on progress update
     */
    suspend fun downloadToFile(url: String, file: File, progress: (Int) -> Unit) {
        return downloadToOutputStream(url, file.namedOutputStream(), progress)
    }
}