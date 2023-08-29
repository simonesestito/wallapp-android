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
import com.simonesestito.wallapp.databinding.WallpaperBottomsheetLoadingBinding
import com.simonesestito.wallapp.utils.isSetLiveWallpaper
import com.simonesestito.wallapp.utils.tryDismiss
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class WallpaperPreviewBottomSheet : AbstractWallpaperBottomSheet() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = WallpaperBottomsheetLoadingBinding.bind(view)

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
                        viewBinding.wallpaperDownloadText.setText(R.string.wallpaper_setup_status_downloading)
                        updateProgress(status.progress)
                    }
                }
                DownloadStatus.Finalizing -> {
                    viewBinding.wallpaperDownloadText.setText(R.string.wallpaper_setup_status_finalizing)
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
        viewBinding.wallpaperDownloadText.setText(R.string.wallpaper_preview_state_backup)

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