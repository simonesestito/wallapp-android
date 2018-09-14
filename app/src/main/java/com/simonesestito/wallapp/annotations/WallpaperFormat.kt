/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.annotations

import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(
        FORMAT_16_9,
        FORMAT_18_9,
        FORMAT_IPHONE,
        FORMAT_IPHONE_X,
        FORMAT_PREVIEW
)
annotation class WallpaperFormat

data class Dimensions(val width: Int, val height: Int) {
    val ratio: Double
        get() = width.toDouble() / height.toDouble()
}

const val FORMAT_16_9 = "16_9.png"
const val FORMAT_18_9 = "18_9.png"
const val FORMAT_IPHONE = "iphone.png"
const val FORMAT_IPHONE_X = "iphone-x.png"

const val FORMAT_PREVIEW = "preview.jpg"

// Not included in StringDef
// It isn't a wallpaper format
// It's used in categories only
const val FORMAT_COVER = "cover.jpg"

val downloadableFormats = arrayOf(
        FORMAT_16_9,
        FORMAT_18_9,
        FORMAT_IPHONE,
        FORMAT_IPHONE_X
)

val @receiver:WallpaperFormat String.dimensions: Dimensions
    get() = when (this) {
        FORMAT_16_9 -> Dimensions(1440, 2560)
        FORMAT_18_9 -> Dimensions(1440, 2880)
        FORMAT_IPHONE -> Dimensions(750, 1334)
        FORMAT_IPHONE_X -> Dimensions(1125, 2436)
        FORMAT_PREVIEW -> Dimensions(720, 1280)
        else -> throw IllegalArgumentException("Unknown format: $this")
    }