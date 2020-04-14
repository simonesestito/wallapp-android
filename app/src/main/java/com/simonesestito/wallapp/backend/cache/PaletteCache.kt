/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.cache

import android.util.Log
import android.util.LruCache
import androidx.palette.graphics.Palette
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.utils.TAG


class PaletteCache(maxSize: Int) : LruCache<String, Palette>(maxSize) {
    init {
        Log.d(TAG, "Creating new palette cache...")
    }

    operator fun get(wallpaper: Wallpaper): Palette? =
            get("wallpaper:${wallpaper.categoryId}/${wallpaper.id}")

    operator fun set(wallpaper: Wallpaper, palette: Palette) {
        put("wallpaper:${wallpaper.categoryId}/${wallpaper.id}", palette)
    }

    operator fun get(category: Category): Palette? =
            get("category:${category.id}")

    operator fun set(category: Category, palette: Palette) {
        put("category:${category.id}", palette)
    }
}
