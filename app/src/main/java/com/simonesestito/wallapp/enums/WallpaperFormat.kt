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

package com.simonesestito.wallapp.enums

import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(
        FORMAT_18_9,
        FORMAT_IPHONE,
        FORMAT_PREVIEW
)
annotation class WallpaperFormat

data class Dimensions(val width: Int, val height: Int) {
    val ratio: Double
        get() = width.toDouble() / height.toDouble()
}

const val FORMAT_18_9 = "18_9.png"
const val FORMAT_IPHONE = "iphone.png"

const val FORMAT_PREVIEW = "preview.jpg"

// Not included in StringDef
// It isn't a wallpaper format
// It's used in categories only
const val FORMAT_COVER = "cover.jpg"

val downloadableFormats = arrayOf(
        FORMAT_18_9,
        FORMAT_IPHONE
)

val @receiver:WallpaperFormat String.dimensions: Dimensions
    get() = when (this) {
        FORMAT_18_9 -> Dimensions(1440, 2880)
        FORMAT_IPHONE -> Dimensions(1242, 2688)
        FORMAT_PREVIEW -> Dimensions(720, 1280)
        else -> throw IllegalArgumentException("Unknown format: $this")
    }