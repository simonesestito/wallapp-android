/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.simonesestito.wallapp.PICTURES_DOWNLOAD_SUBDIR
import com.simonesestito.wallapp.WallappApplication
import java.io.File
import java.util.*


/**
 * Implementation of [IStorageDownloadService] for API < Q (29)
 * It directly writes to a file and requires storage permissions.
 *
 * @see IStorageDownloadService
 */
@TargetApi(Build.VERSION_CODES.P)
class StorageDownloadServiceLegacy(private val downloadService: DownloadService)
    : IStorageDownloadService {

    companion object {
        const val PERMISSION_REQUEST_CODE = 8
    }

    override suspend fun downloadToStorage(context: Fragment, url: String, filename: String, progress: (Int) -> Unit) {
        if (!hasPermission(context.requireContext()))
            throw SecurityException("Missing permissions")

        val file = createStorageFile(filename)
        downloadService.downloadToFile(url, file, progress)
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

    override fun hasPermission(context: Context) =
            context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED

    override fun requestPermission(context: Fragment) {
        context.requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Handle permission result from current [Fragment]
     *
     * @return true if permissions have been granted,
     *      false if this permission request hasn't been generated from this class
     *      or if not every permission has been granted
     */
    override fun handlePermissionResult(requestCode: Int, grantResults: IntArray): Boolean {
        return requestCode == PERMISSION_REQUEST_CODE &&
                !grantResults.contains(PackageManager.PERMISSION_DENIED)
    }

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