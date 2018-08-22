/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.backend.cache

import android.util.LruCache
import androidx.palette.graphics.Palette
import it.simonesestito.wallapp.backend.model.Wallpaper


class PaletteCache(maxSize: Int) : LruCache<String, Palette>(maxSize) {
    operator fun set(wallpaper: Wallpaper, palette: Palette) {
        put("${wallpaper.categoryId}/${wallpaper.id}", palette)
    }

    operator fun get(wallpaper: Wallpaper): Palette? =
            get("${wallpaper.categoryId}/${wallpaper.id}")
}
