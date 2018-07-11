package it.simonesestito.wallapp.annotations

import kotlin.annotation.Retention
import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(FORMAT_RAW, FORMAT_FULL_HD, FORMAT_QUAD_HD, FORMAT_PREVIEW)
annotation class WallpaperFormat

const val FORMAT_RAW = "raw"
const val FORMAT_FULL_HD = "fhd"
const val FORMAT_QUAD_HD = "qhd"
const val FORMAT_PREVIEW = "preview"

// Not included in StringDef
// It isn't a wallpaper format
// It's used in categories only
const val FORMAT_COVER = "cover"