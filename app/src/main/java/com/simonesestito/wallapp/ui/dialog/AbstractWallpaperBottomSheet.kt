/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.dialog

import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simonesestito.wallapp.BOTTOMSHEET_AUTO_DISMISS_DELAY
import com.simonesestito.wallapp.BOTTOMSHEET_FADE_ANIMATION_DURATION
import com.simonesestito.wallapp.EXTRA_WALLPAPER_SETUP_PARCELABLE
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpaperSetupViewModel
import com.simonesestito.wallapp.utils.getViewModel
import com.simonesestito.wallapp.utils.tryDismiss
import kotlinx.android.synthetic.main.wallpaper_bottomsheet.*
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_loading.*
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_result.*
import kotlinx.android.synthetic.main.wallpaper_preview_bottom_sheet.view.*
import kotlinx.android.synthetic.main.wallpaper_preview_bottom_sheet.wallpaperDownloading
import kotlinx.android.synthetic.main.wallpaper_preview_bottom_sheet.wallpaperFeedback
import javax.inject.Inject


/**
 * Base class for both Setup and Preview wallpaper bottom sheets
 * Created to optimize code recycling
 */
abstract class AbstractWallpaperBottomSheet : BottomSheetDialogFragment() {
    companion object {
        const val PROGRESS_INDETERMINATE = -1
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModel by lazy {
        getViewModel<WallpaperSetupViewModel>(viewModelFactory)
    }

    protected val wallpaperArg: Wallpaper by lazy {
        arguments?.getParcelable<Wallpaper>(EXTRA_WALLPAPER_SETUP_PARCELABLE)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            setNavigationBarColor()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.wallpaper_bottomsheet, container, false)

    protected fun showFailedResult(@StringRes failedText: Int = R.string.wallpaper_setup_status_failed) {
        hideDownloadLayout()

        wallpaperFeedbackImage?.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_red_24dp)
        wallpaperFeedbackText?.setText(failedText)

        showFeedbackLayout()
    }

    protected fun showSuccessResult(@StringRes successText: Int = R.string.wallpaper_setup_status_success) {
        hideDownloadLayout()

        wallpaperFeedbackImage?.setImageResource(R.drawable.ic_sentiment_very_satisfied_green_24dp)
        wallpaperFeedbackText?.setText(successText)

        showFeedbackLayout()

        // Auto dismiss after some time
        Handler(Looper.myLooper()!!).postDelayed(BOTTOMSHEET_AUTO_DISMISS_DELAY) {
            tryDismiss()
        }
    }

    private fun showFeedbackLayout() {
        if (wallpaperFeedback?.visibility == View.VISIBLE)
            return

        wallpaperFeedback?.apply {
            animate()
                    .withStartAction {
                        wallpaperFeedback?.alpha = 0f
                        wallpaperFeedback?.visibility = View.VISIBLE
                    }.alpha(1f)
                    .setStartDelay(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .start()
        }
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

    private fun hideDownloadLayout() {
        hideSetupLayout()

        if (wallpaperDownloading?.visibility != View.VISIBLE)
            return

        wallpaperDownloading?.apply {
            animate()
                    .alpha(0f)
                    .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .withEndAction {
                        wallpaperDownloading?.visibility = View.INVISIBLE
                    }.start()
        }
    }

    protected fun hideSetupLayout() {
        if (wallpaperSetup?.visibility != View.VISIBLE)
            return

        wallpaperSetup.animate()
                .alpha(0f)
                .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                .withEndAction {
                    // Use INVISIBLE instead of GONE to preserve its space in layout
                    wallpaperSetup?.visibility = View.INVISIBLE
                }.start()
    }

    private fun showDownloadLayout() {
        hideSetupLayout()
        if (wallpaperDownloading.visibility == View.VISIBLE)
            return

        wallpaperDownloading.animate()
                .alpha(1f)
                .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                .setStartDelay(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                .withStartAction {
                    // Switch from GONE (assigned in xml) to VISIBLE with alpha 0f
                    wallpaperDownloading.alpha = 0f
                    wallpaperDownloading.visibility = View.VISIBLE
                }
                .start()

        wallpaperDownloadText.setText(R.string.wallpaper_setup_status_downloading)
    }

    protected fun updateProgress(progress: Int) {
        showDownloadLayout()

        if (progress < 0) {
            // Set indeterminate
            wallpaperDownloadProgress.isIndeterminate = true
        } else {
            // Set determinate
            wallpaperDownloadProgress.isIndeterminate = false
            wallpaperDownloadProgress.progress = progress
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.stopDownloadTask()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        viewModel.stopDownloadTask()
    }
}