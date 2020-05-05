/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
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