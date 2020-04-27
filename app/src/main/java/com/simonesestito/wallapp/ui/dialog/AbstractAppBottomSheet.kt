/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.dialog

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simonesestito.wallapp.R

abstract class AbstractAppBottomSheet : BottomSheetDialogFragment() {
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            setNavigationBarColor()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    protected fun setNavigationBarColor(@ColorRes colorRes: Int = R.color.color_surface) {
        val window = dialog?.window ?: return
        val metrics = DisplayMetrics()
        window.windowManager.defaultDisplay.getMetrics(metrics)

        val dimDrawable = GradientDrawable()

        val navigationBarDrawable = GradientDrawable()
        navigationBarDrawable.shape = GradientDrawable.RECTANGLE
        navigationBarDrawable.setColor(ContextCompat.getColor(requireContext(), colorRes))

        val layers = arrayOf(dimDrawable, navigationBarDrawable)

        val windowBackground = LayerDrawable(layers)
        windowBackground.setLayerInsetTop(1, metrics.heightPixels)

        window.setBackgroundDrawable(windowBackground)
    }
}