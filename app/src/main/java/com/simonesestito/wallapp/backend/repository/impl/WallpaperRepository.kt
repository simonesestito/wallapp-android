/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

@file:Suppress("DEPRECATION")

package com.simonesestito.wallapp.backend.repository.impl

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.palette.graphics.Palette
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.simonesestito.wallapp.*
import com.simonesestito.wallapp.annotations.WallpaperFormat
import com.simonesestito.wallapp.backend.cache.PaletteCache
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.backend.repository.IWallpaperRepository
import com.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveCollection
import com.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveDocument
import com.simonesestito.wallapp.utils.map
import com.simonesestito.wallapp.utils.mapList
import java.io.File
import javax.inject.Inject

class WallpaperRepository @Inject constructor(private val paletteCache: PaletteCache,
                                              private val firestore: FirebaseFirestore,
                                              private val storage: FirebaseStorage) : IWallpaperRepository {
    override fun getWallpapersByCategoryId(categoryId: String): LiveData<List<Wallpaper>> {
        val ref = firestore
                .collection("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS")
                .whereEqualTo(KEY_PUBLISHED, true)
                .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)

        return FirestoreLiveCollection(ref).mapList {
            Wallpaper(it.id, categoryId)
        }
    }

    override fun getWallpaper(categoryId: String, wallpaperId: String): LiveData<Wallpaper?> {
        val ref = firestore
                .document("$FIRESTORE_CATEGORIES/$categoryId/$FIRESTORE_WALLPAPERS/$wallpaperId")

        return FirestoreLiveDocument(ref).map {
            if (it.exists())
                Wallpaper(it.id, categoryId)
            else
                null
        }
    }

    override fun downloadWallpaper(wallpaper: Wallpaper, @WallpaperFormat format: String, destination: File) =
            storage
                    .getReference(wallpaper.getStorageFilePath(format))
                    .getFile(destination)

    @MainThread
    override fun loadWallpaper(wallpaper: Wallpaper,
                               @WallpaperFormat format: String,
                               imageView: ImageView,
                               onPaletteReady: ((Palette) -> Unit)?) {
        val imageRef = storage.getReference(wallpaper.getStorageFilePath(format))

        GlideApp
                .with(imageView)
                .asBitmap()
                .load(imageRef)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageView.setImageBitmap(resource)

                        // Generate Palette looking into cache first
                        // If onPaletteReady is null the caller doesn't need Palette
                        // But we can put it in cache for future usages
                        val cachedPalette = paletteCache[wallpaper]
                        if (onPaletteReady == null && cachedPalette != null) {
                            // Caller doesn't need any Palette and it's already present in cache
                            // Do nothing
                            return
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
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        placeholder?.let { imageView.setImageDrawable(placeholder) }

                        // On loading started, if Palette is needed, check in cache
                        onPaletteReady?.let {
                            val cached = paletteCache[wallpaper]
                            if (cached != null) {
                                onPaletteReady(cached)
                            }
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        super.onLoadCleared(placeholder)
                        placeholder?.let { imageView.setImageDrawable(placeholder) }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        errorDrawable?.let { imageView.setImageDrawable(errorDrawable) }
                    }
                })
    }
}
