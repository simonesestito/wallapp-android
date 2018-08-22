/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.utils

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import it.simonesestito.wallapp.BACKUP_WALLPAPER_FILENAME
import it.simonesestito.wallapp.annotations.*
import java.io.File
import java.io.IOException

/**
 * Apply the wallpaper
 * Support method: callable from each Platform version
 * @param context Context
 * @param wallpaperFile File which contains the wallpaper
 * @param location Location where the wallpaper should be applied. On pre-Nougat has no effect
 * @return True in case of success, false otherwise
 */
@WorkerThread
fun supportApplyWallpaper(context: Context, wallpaperFile: File, @WallpaperLocation location: Int) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            applyWallpaper(context, wallpaperFile)
        } else {
            applyWallpaper(context, wallpaperFile, location)
        }

@RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
@WorkerThread
fun backupWallpaper(context: Context) {
    val destFile = File(context.noBackupFilesDir, BACKUP_WALLPAPER_FILENAME)
    ContextCompat.getSystemService(context, WallpaperManager::class.java)!!
            .drawable
            .toBitmap()
            .writeToFile(destFile, recycleOnEnd = true)
}

@WorkerThread
fun restoreWallpaper(context: Context) {
    val backupFile = File(context.noBackupFilesDir, BACKUP_WALLPAPER_FILENAME)
    if (!backupFile.exists()) {
        return
    }
    ContextCompat.getSystemService(context, WallpaperManager::class.java)!!
            .setStream(backupFile.inputStream())
    backupFile.delete()
}

@WallpaperFormat
fun getSuggestedWallpaperFormat(displayMetrics: DisplayMetrics): String {
    // Calculate user aspect ratio
    val userRatio = displayMetrics.widthPixels / displayMetrics.heightPixels.toDouble()

    // Find if user display is exactly one of the formats
    downloadableFormats.forEach { format ->
        if (format.dimensions.ratio == userRatio) {
            return format
        }
    }

    // If not exactly one of those formats, pick the best one
    return downloadableFormats
            .sortedBy { format -> Math.abs(format.dimensions.ratio - userRatio) }
            .first()
}

@Throws(IOException::class)
@WorkerThread
fun Bitmap.writeToFile(dest: File, recycleOnEnd: Boolean) {
    dest.outputStream().use {
        this.compress(Bitmap.CompressFormat.PNG, 100, it)
        if (recycleOnEnd) {
            this.recycle()
        }
    }
}

@WorkerThread
private fun applyWallpaper(context: Context, wallpaperFile: File): Boolean {
    val systemWallpaperService =
            ContextCompat.getSystemService(context, WallpaperManager::class.java)!!

    return try {
        systemWallpaperService.setStream(wallpaperFile.inputStream())
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@WorkerThread
private fun applyWallpaper(context: Context, wallpaperFile: File, @WallpaperLocation location: Int): Boolean {
    val which: Int = when (location) {
        WALLPAPER_LOCATION_HOME -> WallpaperManager.FLAG_SYSTEM
        WALLPAPER_LOCATION_LOCK -> WallpaperManager.FLAG_LOCK
        WALLPAPER_LOCATION_BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
        else -> throw IllegalArgumentException("Unknown location $location")
    }

    val systemWallpaperService =
            ContextCompat.getSystemService(context, WallpaperManager::class.java)!!

    Log.d("WallpaperUtils", "Flag which: $which")

    return try {
        systemWallpaperService
                .setStream(wallpaperFile.inputStream(), null, true, which) != 0
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}
