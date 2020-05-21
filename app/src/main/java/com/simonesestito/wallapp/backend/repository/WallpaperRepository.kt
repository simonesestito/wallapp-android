/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

@file:Suppress("DEPRECATION")

package com.simonesestito.wallapp.backend.repository

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.simonesestito.wallapp.*
import com.simonesestito.wallapp.backend.cache.PaletteCache
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.enums.WallpaperFormat
import com.simonesestito.wallapp.enums.dimensions
import com.simonesestito.wallapp.utils.toSuspendDocument
import com.simonesestito.wallapp.utils.toSuspendQuery
import javax.inject.Inject

class WallpaperRepository @Inject constructor(private val paletteCache: PaletteCache,
                                              private val firestore: FirebaseFirestore) {
    suspend fun getWallpapersByCategoryId(categoryId: String): List<Wallpaper> {
        return firestore
                .collection("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS")
                .whereEqualTo(KEY_PUBLISHED, true)
                .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)
                .toSuspendQuery()
                .map {
                    Wallpaper(
                            it.id,
                            categoryId,
                            it.get(KEY_WALL_AUTHOR_BIO)?.toString(),
                            it.get(KEY_WALL_AUTHOR_NAME)?.toString(),
                            it.get(KEY_WALL_AUTHOR_SOCIAL)?.toString()
                    )
                }
    }

    suspend fun getWallpaper(categoryId: String, wallpaperId: String): Wallpaper? {
        val document = firestore
                .document("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS/$wallpaperId")
                .toSuspendDocument()

        return if (document.exists()) {
            Wallpaper(
                    document.id,
                    categoryId,
                    document.get(KEY_WALL_AUTHOR_BIO)?.toString(),
                    document.get(KEY_WALL_AUTHOR_NAME)?.toString(),
                    document.get(KEY_WALL_AUTHOR_SOCIAL)?.toString()
            )
        } else null
    }

    /**
     * @param useExactFormatSize Don't trasform image and its size. Set true only in case of problems (e.g.: return Shared Elements transition)
     */
    @MainThread
    fun loadWallpaper(wallpaper: Wallpaper,
                      @WallpaperFormat format: String,
                      imageView: ImageView,
                      useExactFormatSize: Boolean = false,
                      onPaletteReady: ((Palette) -> Unit)? = null) {
        val shortAnim = imageView.resources.getInteger(android.R.integer.config_shortAnimTime)

        Glide
                .with(imageView)
                .asBitmap()
                .placeholder(R.color.color_surface)
                .transition(BitmapTransitionOptions().crossFade(shortAnim))
                .apply {
                    if (useExactFormatSize) {
                        override(format.dimensions.width, format.dimensions.height)
                                .dontTransform()
                    }
                }
                .load(wallpaper.getStorageFileUrl(format))
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean) = false

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        resource ?: return false

                        // Generate Palette looking into cache first
                        // If onPaletteReady is null the caller doesn't need Palette
                        // But we can put it in cache for future usages
                        val cachedPalette = paletteCache[wallpaper]
                        if (onPaletteReady == null && cachedPalette != null) {
                            // Caller doesn't need any Palette and it's already present in cache
                            // Do nothing
                            return false
                        }

                        if (onPaletteReady != null && cachedPalette != null) {
                            // Palette needed by caller and present in cache
                            onPaletteReady(cachedPalette)
                        }

                        // Palette is not in cache
                        // Calculate it
                        Palette.from(resource).generate { palette ->
                            palette ?: return@generate
                            paletteCache[wallpaper] = palette
                            onPaletteReady?.invoke(palette)
                        }
                        return false
                    }

                })
                .into(imageView)
    }
}
