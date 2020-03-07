/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.dialog

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.simonesestito.wallapp.BOTTOMSHEET_AUTO_DISMISS_DELAY
import com.simonesestito.wallapp.BOTTOMSHEET_FADE_ANIMATION_DURATION
import com.simonesestito.wallapp.EXTRA_WALLPAPER_SETUP_PARCELABLE
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.DownloadStatus
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_BOTH
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_HOME
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_LOCK
import com.simonesestito.wallapp.enums.WallpaperLocation
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpaperSetupViewModel
import com.simonesestito.wallapp.utils.getSuggestedWallpaperFormat
import com.simonesestito.wallapp.utils.getViewModel
import com.simonesestito.wallapp.utils.tryDismiss
import kotlinx.android.synthetic.main.wallpaper_bottomsheet.*
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_loading.*
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_result.*
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_setup.*
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_setup.view.*
import javax.inject.Inject

class WallpaperSetupBottomSheet : ThemedBottomSheet() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        getViewModel<WallpaperSetupViewModel>(viewModelFactory)
    }

    private val wallpaperArg: Wallpaper by lazy {
        arguments?.getParcelable<Wallpaper>(EXTRA_WALLPAPER_SETUP_PARCELABLE)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.wallpaper_bottomsheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Apply default selection
        view.wallpaperLocationChipGroup.check(R.id.wallpaperLocationChipBoth)
        view.wallpaperLocationChipGroup.setOnCheckedChangeListener { _, _ ->
            try {
                viewModel.currentWallpaperLocation = getSelectedLocation()
            } catch (_: IllegalArgumentException) {
                // Thrown by getSelectedLocation in case of wrong selection
            }
        }

        view.wallpaperApplyButton.setOnClickListener { _ ->
            viewModel.applyWallpaper(
                    requireContext(),
                    wallpaperArg,
                    getSuggestedWallpaperFormat(resources.displayMetrics),
                    // Use saved location in ViewModel instead of getting it now
                    // It can lead to crash in case of bad selection
                    viewModel.currentWallpaperLocation
            )
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // Hide wallpaper location selection
            // It isn't supported before 7.0 (API 24)
            view.wallpaperLocationTitle?.visibility = View.GONE
            view.wallpaperLocationChipGroup?.visibility = View.GONE
            viewModel.currentWallpaperLocation = WALLPAPER_LOCATION_BOTH
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getDownloadStatus().observe(this, Observer { status ->
            when (status) {
                is DownloadStatus.Progressing -> onDownloadStarted(status.progress)
                DownloadStatus.Finalizing -> onDownloadFinalizing()
                DownloadStatus.Success -> onDownloadResult(true)
                DownloadStatus.Error -> onDownloadResult(false)
                // Ignore DownloadStatus.Cancelled
            }
        })
    }

    /**
     * Get the current selected wallpaper location by the checkedChipId from ChipGroup
     * @throws IllegalArgumentException If the selected chip has an unknown ID, it also occurs when no chip is actually selected
     */
    @WallpaperLocation
    @Throws(IllegalArgumentException::class)
    private fun getSelectedLocation() =
            when (wallpaperLocationChipGroup.checkedChipId) {
                R.id.wallpaperLocationChipHome -> WALLPAPER_LOCATION_HOME
                R.id.wallpaperLocationChipLock -> WALLPAPER_LOCATION_LOCK
                R.id.wallpaperLocationChipBoth -> WALLPAPER_LOCATION_BOTH
                else -> throw IllegalArgumentException("Unknown chip selection")
            }

    private fun onDownloadStarted(progress: Int) {
        if (progress == 0) {
            // Fade out setup
            wallpaperSetup.animate()
                    .alpha(0f)
                    .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .withEndAction {
                        // Use INVISIBLE instead of GONE to preserve its space in layout
                        wallpaperSetup?.visibility = View.INVISIBLE
                    }.start()

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
        } else {
            // TODO Show progress
        }
    }

    private fun onDownloadFinalizing() {
        wallpaperDownloadText.setText(R.string.wallpaper_setup_status_finalizing)
    }

    private fun onDownloadResult(success: Boolean) {
        if (success) {
            wallpaperFeedbackImage
                    ?.setImageResource(R.drawable.ic_sentiment_very_satisfied_green_24dp)
            wallpaperFeedbackText?.setText(R.string.wallpaper_setup_status_success)
        } else {
            wallpaperFeedbackImage
                    ?.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_red_24dp)
            wallpaperFeedbackText?.setText(R.string.wallpaper_setup_status_failed)
        }

        // Switch the view
        wallpaperDownloading?.apply {
            animate()
                    .alpha(0f)
                    .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .withEndAction {
                        wallpaperDownloading?.visibility = View.GONE
                    }.start()
        }

        wallpaperFeedback?.apply {
            animate()
                    .alpha(1f)
                    .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .setStartDelay(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .withStartAction {
                        wallpaperFeedback?.alpha = 0f
                        wallpaperFeedback?.visibility = View.VISIBLE
                    }.start()
        }

        // Auto dismiss after some time
        Handler(Looper.myLooper()!!).postDelayed(BOTTOMSHEET_AUTO_DISMISS_DELAY) {
            tryDismiss()
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