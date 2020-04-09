/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.*
import androidx.annotation.RequiresApi
import androidx.core.content.contentValuesOf
import com.simonesestito.wallapp.PICTURES_DOWNLOAD_SUBDIR
import java.io.File
import java.util.*


/**
 * Implementation of [IStorageDownloadService] to work on Android Q and higher
 * It uses new storage access APIs.
 * It doesn't require any special permission to work.
 *
 * @see [IStorageDownloadService]
 */
@RequiresApi(Build.VERSION_CODES.Q)
class StorageDownloadServiceV29(private val downloadService: DownloadService) : IStorageDownloadService {
    override val requiredPermissions = emptyArray<String>()

    override suspend fun downloadToStorage(context: Context, url: String, filename: String, progress: (Int) -> Unit) {
        val mime = when (filename.toLowerCase(Locale.ROOT).split('.').last()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            else -> "image/*"
        }

        // Create a record in MediaStore with isPending = 1
        val contentValues = contentValuesOf(
                DISPLAY_NAME to filename,
                MIME_TYPE to mime,
                RELATIVE_PATH to Environment.DIRECTORY_PICTURES + File.separator + PICTURES_DOWNLOAD_SUBDIR,
                IS_PENDING to 1
        )

        // Create file
        val fileUri = getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                .let { context.contentResolver.insert(it, contentValues)!! }

        // Open OutputStream
        val outputStream = context.contentResolver.openOutputStream(fileUri)!!

        // Download file
        outputStream.use { output ->
            downloadService.downloadToOutputStream(url, output, progress)
        }

        // Update ContentValues
        contentValues.clear()
        contentValues.put(IS_PENDING, 0)
        context.contentResolver.update(fileUri, contentValues, null, null)
        contentValues.clear()
    }
}