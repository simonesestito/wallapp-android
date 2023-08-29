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
