/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.dialog

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.DownloadStatus
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_BOTH
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_HOME
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_LOCK
import com.simonesestito.wallapp.enums.WallpaperLocation
import com.simonesestito.wallapp.utils.getSuggestedWallpaperFormat
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_loading.*
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_setup.*
import kotlinx.android.synthetic.main.wallpaper_bottomsheet_setup.view.*

class WallpaperSetupBottomSheet : AbstractWallpaperBottomSheet() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Apply default selection
        view.wallpaperLocationChipGroup.check(R.id.wallpaperLocationChipBoth)

        // Necessary in the following listener
        var lastChecked = view.wallpaperLocationChipGroup.checkedChipId
        
        view.wallpaperLocationChipGroup.setOnCheckedChangeListener { group, checkedId ->
            // Always require a selection
            // NOTE: this block of code will no longer be necessary since material library 1.2.0
            if (checkedId == View.NO_ID)
                group.check(lastChecked)
            else
                lastChecked = checkedId


            try {
                viewModel.currentWallpaperLocation = getSelectedLocation()
            } catch (_: IllegalArgumentException) {
                // Thrown by getSelectedLocation in case of wrong selection
            }
        }

        view.wallpaperApplyButton.setOnClickListener {
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
            hideSetupLayout()
        }
        updateProgress(progress)
    }

    private fun onDownloadFinalizing() {
        wallpaperDownloadText.setText(R.string.wallpaper_setup_status_finalizing)
        updateProgress(PROGRESS_INDETERMINATE)
    }

    private fun onDownloadResult(success: Boolean) {
        hideSetupLayout()

        if (success) {
            showSuccessResult()
        } else {
            showFailedResult()
        }
    }
}