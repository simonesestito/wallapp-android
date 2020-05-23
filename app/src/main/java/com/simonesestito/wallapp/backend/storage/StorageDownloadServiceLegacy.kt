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

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.simonesestito.wallapp.PICTURES_DOWNLOAD_SUBDIR
import com.simonesestito.wallapp.WallappApplication
import kotlinx.coroutines.isActive
import java.io.File
import java.util.*
import kotlin.coroutines.coroutineContext


/**
 * Implementation of [IStorageDownloadService] for API < Q (29)
 * It directly writes to a file and requires storage permissions.
 *
 * @see IStorageDownloadService
 */
@TargetApi(Build.VERSION_CODES.P)
class StorageDownloadServiceLegacy(private val downloadService: DownloadService)
    : IStorageDownloadService {

    @Throws(SecurityException::class)
    override suspend fun downloadToStorage(context: Context, url: String, filename: String, progress: (Int) -> Unit) {
        val file = createStorageFile(filename)
        downloadService.downloadToFile(url, file, progress)
        if (coroutineContext.isActive)
            addToMediaStore(file)
    }

    private fun addToMediaStore(file: File) {
        val args = ContentValues()
        val imageSeconds = System.currentTimeMillis() / 1000
        val filename = file.name
        val mime = when (filename.toLowerCase(Locale.ROOT).split('.').last()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            else -> "image/*"
        }

        @Suppress("DEPRECATION")
        args.put(MediaStore.Images.ImageColumns.DATA, file.absolutePath)
        args.put(MediaStore.Images.ImageColumns.TITLE, filename)
        args.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, filename)
        args.put(MediaStore.Images.ImageColumns.DATE_ADDED, imageSeconds)
        args.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, imageSeconds)
        args.put(MediaStore.Images.ImageColumns.MIME_TYPE, mime)
        args.put(MediaStore.Images.ImageColumns.SIZE, file.length())

        WallappApplication.INSTANCE
                .contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, args)
    }

    override val requiredPermissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @TargetApi(Build.VERSION_CODES.P)
    @Suppress("DEPRECATION")
    private fun createStorageFile(filename: String): File {
        val picturesRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val wallappDir = picturesRoot.resolve(PICTURES_DOWNLOAD_SUBDIR)
        if (!wallappDir.exists())
            wallappDir.mkdirs()

        val file = wallappDir.resolve(filename)
        if (file.exists())
            file.delete()
        file.createNewFile()

        return file
    }
}