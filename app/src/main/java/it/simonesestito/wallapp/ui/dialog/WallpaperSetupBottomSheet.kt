/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.simonesestito.wallapp.BOTTOMSHEET_AUTO_DISMISS_DELAY
import it.simonesestito.wallapp.BOTTOMSHEET_FADE_ANIMATION_DURATION
import it.simonesestito.wallapp.EXTRA_WALLPAPER_SETUP_PARCELABLE
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.annotations.*
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.di.component.AppInjector
import it.simonesestito.wallapp.lifecycle.viewmodel.WallpaperSetupViewModel
import it.simonesestito.wallapp.utils.getSuggestedWallpaperFormat
import it.simonesestito.wallapp.utils.getViewModel
import it.simonesestito.wallapp.utils.tryDismiss
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
        arguments!!.getParcelable<Wallpaper>(EXTRA_WALLPAPER_SETUP_PARCELABLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.wallpaper_bottomsheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.copyrightNoticeText.movementMethod = LinkMovementMethod.getInstance()

        // Apply default selection
        view.wallpaperLocationChipGroup.check(R.id.wallpaperLocationChipBoth)

        view.wallpaperApplyButton.setOnClickListener { _ ->
            viewModel.applyWallpaper(
                    requireContext(),
                    wallpaperArg,
                    getSuggestedWallpaperFormat(resources.displayMetrics),
                    getSelectedLocation()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getDownloadStatus().observe(this, Observer { status ->
            when (status) {
                STATUS_DOWNLOADING -> onDownloadStarted()
                STATUS_FINALIZING -> onDownloadFinalizing()
                STATUS_SUCCESS -> onDownloadResult(true)
                STATUS_ERROR -> onDownloadResult(false)
                // Ignore STATUS_NOTHING
            }
        })
    }

    @WallpaperLocation
    private fun getSelectedLocation() =
            when (wallpaperLocationChipGroup.checkedChipId) {
                R.id.wallpaperLocationChipHome -> WALLPAPER_LOCATION_HOME
                R.id.wallpaperLocationChipLock -> WALLPAPER_LOCATION_LOCK
                R.id.wallpaperLocationChipBoth -> WALLPAPER_LOCATION_BOTH
                else -> throw RuntimeException("Undefined wallpaper location chip selection ID")
            }

    private fun onDownloadStarted() {
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
        Handler(Looper.myLooper()).postDelayed(BOTTOMSHEET_AUTO_DISMISS_DELAY) {
            tryDismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        viewModel.stopDownloadTask()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        viewModel.stopDownloadTask()
    }
}