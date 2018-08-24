/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.backend.cache

import android.util.Log
import android.util.LruCache
import androidx.palette.graphics.Palette
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.utils.TAG


class PaletteCache(maxSize: Int) : LruCache<String, Palette>(maxSize) {
    init {
        Log.wtf(TAG, "CREATING NEW PALETTE CACHE")
    }

    operator fun set(wallpaper: Wallpaper, palette: Palette) {
        put("${wallpaper.categoryId}/${wallpaper.id}", palette)
        Log.d(TAG, "Color inserted for wallpaper $wallpaper")
    }

    operator fun get(wallpaper: Wallpaper): Palette? =
            get("${wallpaper.categoryId}/${wallpaper.id}")
}
