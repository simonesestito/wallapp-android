/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.annotations

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(
        WALLPAPER_LOCATION_HOME,
        WALLPAPER_LOCATION_LOCK,
        WALLPAPER_LOCATION_BOTH
)
annotation class WallpaperLocation

const val WALLPAPER_LOCATION_HOME = 0
const val WALLPAPER_LOCATION_LOCK = 1
const val WALLPAPER_LOCATION_BOTH = 2