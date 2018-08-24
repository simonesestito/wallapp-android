/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.ui.dialog

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.annotations.*
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.backend.service.PreviewService
import it.simonesestito.wallapp.di.component.AppInjector
import it.simonesestito.wallapp.lifecycle.viewmodel.WallpaperSetupViewModel
import it.simonesestito.wallapp.utils.getViewModel
import it.simonesestito.wallapp.utils.tryDismiss
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_loading.view.*
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_result.*
import kotlinx.android.synthetic.main.wallpaper_preview_bottom_sheet.*
import javax.inject.Inject


class WallpaperPreviewBottomSheet : ThemedBottomSheet() {
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
            inflater.inflate(R.layout.wallpaper_preview_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDownloadStatus().observe(this, Observer { status ->
            when (status) {
                STATUS_INIT -> {
                    view.wallpaperDownloadText.setText(R.string.wallpaper_preview_state_backup)
                }
                STATUS_DOWNLOADING -> {
                    view.wallpaperDownloadText.setText(R.string.wallpaper_setup_status_downloading)
                }
                STATUS_FINALIZING -> {
                    view.wallpaperDownloadText.setText(R.string.wallpaper_setup_status_finalizing)
                }
                STATUS_SUCCESS -> {
                    tryDismiss()
                    startPreviewMode()
                }
                STATUS_ERROR -> {
                    showFailedResult()
                }
            }
        })
        viewModel.applyPreviewWallpaper(requireContext(), wallpaperArg)
    }

    private fun showFailedResult() {
        wallpaperDownloading?.apply {
            animate()
                    .alpha(0f)
                    .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .withEndAction {
                        wallpaperDownloading?.visibility = View.INVISIBLE
                    }.start()
        }

        wallpaperFeedbackImage?.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_red_24dp)
        wallpaperFeedbackText?.setText(R.string.wallpaper_setup_status_failed)

        wallpaperFeedback?.apply {
            animate()
                    .withStartAction {
                        wallpaperFeedback?.alpha = 0f
                        wallpaperFeedback?.visibility = View.VISIBLE
                    }.alpha(1f)
                    .setStartDelay(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                    .withEndAction {
                        Handler(Looper.myLooper()).postDelayed(BOTTOMSHEET_AUTO_DISMISS_DELAY) {
                            tryDismiss()
                        }
                    }
                    .start()
        }
    }

    private fun startPreviewMode() {
        startActivity(Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
        })

        requireContext()
                .startService(
                        Intent(
                                requireContext(),
                                PreviewService::class.java
                        ).putExtra(
                                EXTRA_WALLPAPER_PREVIEW_WINDOW_PARCELABLE,
                                wallpaperArg
                        )
                )
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