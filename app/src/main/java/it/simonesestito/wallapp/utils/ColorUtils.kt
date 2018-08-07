package it.simonesestito.wallapp.utils

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

fun @receiver:ColorInt Int.isLightColor() =
        ColorUtils.calculateLuminance(this) >= 0.5