/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.dialog

import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.simonesestito.wallapp.EXTRA_WALLPAPER_PREVIEW_WINDOW_PARCELABLE
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.androidservice.PreviewService
import com.simonesestito.wallapp.backend.model.DownloadStatus
import com.simonesestito.wallapp.utils.isSetLiveWallpaper
import com.simonesestito.wallapp.utils.tryDismiss
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_loading.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class WallpaperPreviewBottomSheet : AbstractWallpaperBottomSheet() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wallpaperManager = ContextCompat.getSystemService(requireContext(), WallpaperManager::class.java)!!
        if (wallpaperManager.isSetLiveWallpaper()) {
            // Preview Mode doesn't support live wallpapers
            showFailedResult(R.string.wallpaper_preview_live_wallpaper_set_error)
            return
        }

        viewModel.getDownloadStatus().observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                is DownloadStatus.Progressing -> {
                    if (status.progress > 0) {
                        view.wallpaperDownloadText.setText(R.string.wallpaper_setup_status_downloading)
                        updateProgress(status.progress)
                    }
                }
                DownloadStatus.Finalizing -> {
                    view.wallpaperDownloadText.setText(R.string.wallpaper_setup_status_finalizing)
                    updateProgress(PROGRESS_INDETERMINATE)
                }
                DownloadStatus.Success -> {
                    tryDismiss()
                    startPreviewMode()
                }
                DownloadStatus.Error -> showFailedResult()
            }
        })

        updateProgress(PROGRESS_INDETERMINATE)
        view.wallpaperDownloadText.setText(R.string.wallpaper_preview_state_backup)

        CoroutineScope(coroutineContext).launch {
            viewModel.applyPreviewWallpaper(requireContext(), wallpaperArg)
        }
    }

    private fun startPreviewMode() {
        // Open the launcher
        startActivity(Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
        })

        // Start Preview service
        ContextCompat.startForegroundService(requireContext(), Intent(
                requireContext(),
                PreviewService::class.java
        ).putExtra(
                EXTRA_WALLPAPER_PREVIEW_WINDOW_PARCELABLE,
                wallpaperArg
        ))
    }
}