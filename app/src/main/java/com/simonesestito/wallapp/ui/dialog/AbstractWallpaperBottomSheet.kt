/*
 * Copyright 2020 Simone Sestito
 * This file is part of WallApp.
 *
 * WallApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WallApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WallApp.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.simonesestito.wallapp.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.postDelayed
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.simonesestito.wallapp.BOTTOMSHEET_AUTO_DISMISS_DELAY
import com.simonesestito.wallapp.BOTTOMSHEET_FADE_ANIMATION_DURATION
import com.simonesestito.wallapp.EXTRA_WALLPAPER_BOTTOMSHEET_PARCELABLE
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.databinding.WallpaperBottomsheetBinding
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpaperSetupViewModel
import com.simonesestito.wallapp.utils.tryDismiss
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import javax.inject.Inject


/**
 * Base class for both Setup and Preview wallpaper bottom sheets
 * Created to optimize code recycling
 */
abstract class AbstractWallpaperBottomSheet : AbstractAppBottomSheet(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    companion object {
        const val PROGRESS_INDETERMINATE = -1
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModel: WallpaperSetupViewModel by viewModels { viewModelFactory }

    private lateinit var viewBinding: WallpaperBottomsheetBinding

    protected val wallpaperArg by lazy {
        arguments?.getParcelable<Wallpaper>(EXTRA_WALLPAPER_BOTTOMSHEET_PARCELABLE)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.wallpaper_bottomsheet, container, false)
        viewBinding = WallpaperBottomsheetBinding.bind(view)
        return view
    }

    protected fun showFailedResult(@StringRes failedText: Int = R.string.wallpaper_setup_status_failed) {
        hideDownloadLayout()

        viewBinding.wallpaperFeedback.wallpaperFeedbackImage.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_red_24dp)
        viewBinding.wallpaperFeedback.wallpaperFeedbackText.setText(failedText)

        showFeedbackLayout()
    }

    protected fun showSuccessResult(@StringRes successText: Int = R.string.wallpaper_setup_status_success) {
        hideDownloadLayout()

        viewBinding.wallpaperFeedback.wallpaperFeedbackImage.setImageResource(R.drawable.ic_sentiment_very_satisfied_green_24dp)
        viewBinding.wallpaperFeedback.wallpaperFeedbackText.setText(successText)

        showFeedbackLayout()

        // Auto dismiss after some time
        Handler(Looper.myLooper()!!).postDelayed(BOTTOMSHEET_AUTO_DISMISS_DELAY) {
            tryDismiss()
        }
    }

    private fun showFeedbackLayout() {
        val wallpaperFeedback = viewBinding.wallpaperFeedback.root

        if (wallpaperFeedback.visibility == View.VISIBLE)
            return

        wallpaperFeedback.apply {
            animate()
                .withStartAction {
                    wallpaperFeedback.alpha = 0f
                    wallpaperFeedback.visibility = View.VISIBLE
                }.alpha(1f)
                .setStartDelay(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                .start()
        }
    }

    private fun hideDownloadLayout() {
        hideSetupLayout()
        val wallpaperDownloading = viewBinding.wallpaperDownloading.root

        if (wallpaperDownloading.visibility != View.VISIBLE)
            return

        wallpaperDownloading.apply {
            animate()
                .alpha(0f)
                .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
                .withEndAction {
                    wallpaperDownloading.visibility = View.INVISIBLE
                }.start()
        }
    }

    protected fun hideSetupLayout() {
        val wallpaperSetup = viewBinding.wallpaperSetup.root
        if (wallpaperSetup.visibility != View.VISIBLE)
            return

        wallpaperSetup.animate()
            .alpha(0f)
            .setDuration(BOTTOMSHEET_FADE_ANIMATION_DURATION)
            .withEndAction {
                // Use INVISIBLE instead of GONE to preserve its space in layout
                wallpaperSetup.visibility = View.INVISIBLE
            }.start()
    }

    private fun showDownloadLayout() {
        hideSetupLayout()
        val wallpaperDownloading = viewBinding.wallpaperDownloading.root
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

        viewBinding.wallpaperDownloading.wallpaperDownloadText.setText(R.string.wallpaper_setup_status_downloading)
    }

    protected fun updateProgress(progress: Int) {
        showDownloadLayout()

        val wallpaperDownloadProgress = viewBinding.wallpaperDownloading.wallpaperDownloadProgress

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
        coroutineContext.cancel()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        viewModel.stopDownloadTask()
        coroutineContext.cancel()
    }
}