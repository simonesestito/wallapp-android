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

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.REQUEST_WRITE_STORAGE_PERMISSION
import com.simonesestito.wallapp.backend.model.DownloadStatus
import com.simonesestito.wallapp.databinding.WallpaperBottomsheetBinding
import com.simonesestito.wallapp.databinding.WallpaperBottomsheetSetupBinding
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_BOTH
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_HOME
import com.simonesestito.wallapp.enums.WALLPAPER_LOCATION_LOCK
import com.simonesestito.wallapp.enums.WallpaperLocation
import com.simonesestito.wallapp.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WallpaperSetupBottomSheet : AbstractWallpaperBottomSheet() {
    lateinit var viewBinding: WallpaperBottomsheetBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = WallpaperBottomsheetBinding.bind(view)

        val wallpaperLocationChipGroup = viewBinding.wallpaperSetup.wallpaperLocationChipGroup

        // Apply default selection
        viewBinding.wallpaperSetup.wallpaperLocationChipGroup.check(R.id.wallpaperLocationChipBoth)

        // Necessary in the following listener
        var lastChecked = wallpaperLocationChipGroup.checkedChipId

        wallpaperLocationChipGroup.setOnCheckedChangeListener { group, checkedId ->
            // Always require a selection
            // NOTE: this block of code will no longer be necessary since material library 1.2.0
            if (checkedId == View.NO_ID)
                group.check(lastChecked)
            else
                lastChecked = checkedId


            try {
                viewModel.currentWallpaperLocation =
                    viewBinding.wallpaperSetup.getSelectedLocation()
            } catch (_: IllegalArgumentException) {
                // Thrown by getSelectedLocation in case of wrong selection
            }
        }

        viewBinding.wallpaperSetup.wallpaperApplyButton.setOnClickListener {
            CoroutineScope(coroutineContext).launch {
                viewModel.applyWallpaper(
                    requireContext(),
                    wallpaperArg,
                    getSuggestedWallpaperFormat(resources.displayMetrics),
                    // Use saved location in ViewModel instead of getting it now
                    // It can lead to crash in case of bad selection
                    viewModel.currentWallpaperLocation
                )
            }
        }

        viewBinding.wallpaperSetup.wallpaperDownloadButton.setOnClickListener { saveToGallery() }

        // -- API < 24 and MIUI fallback
        val isMIUI = requireContext().isPlatformMIUI()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isMIUI) {
            // Hide wallpaper location selection
            // It isn't supported before 7.0 (API 24)
            // On MIUI apps can't overwrite lockscreen wallpaper
            viewBinding.wallpaperSetup.wallpaperLocationTitle.visibility = View.GONE
            viewBinding.wallpaperSetup.wallpaperLocationChipGroup.visibility = View.GONE
            viewModel.currentWallpaperLocation = WALLPAPER_LOCATION_BOTH

            if (isMIUI) {
                // Display MIUI warning
                viewBinding.wallpaperSetup.wallpaperMiuiWarning.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getDownloadStatus().observe(this) { status ->
            Log.d(TAG, status.toString())
            when (status) {
                is DownloadStatus.Progressing -> onDownloadStarted(status.progress)
                DownloadStatus.Finalizing -> onDownloadFinalizing()
                DownloadStatus.Success -> onDownloadResult(true)
                DownloadStatus.Error -> onDownloadResult(false)
            }
        }
    }

    /**
     * Save current wallpaper to gallery
     */
    private fun saveToGallery() {
        // Check required permissions
        val permissions = viewModel.storagePermissions
        if (permissions.isNotEmpty() && !requireContext().checkSelfPermissions(*permissions)) {
            // Missing permissions
            requestPermissionsRationale(
                R.string.permission_write_storage_request_message,
                REQUEST_WRITE_STORAGE_PERMISSION,
                *permissions
            )
            return
        }

        // Start downloading the wallpaper
        CoroutineScope(coroutineContext).launch {
            viewModel.downloadToGallery(
                requireContext(), wallpaperArg, getSuggestedWallpaperFormat(
                    requireContext().resources.displayMetrics
                )
            )
        }
    }

    /**
     * Get the current selected wallpaper location by the checkedChipId from ChipGroup
     * @throws IllegalArgumentException If the selected chip has an unknown ID, it also occurs when no chip is actually selected
     */
    @WallpaperLocation
    @Throws(IllegalArgumentException::class)
    private fun WallpaperBottomsheetSetupBinding.getSelectedLocation() =
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
        viewBinding.wallpaperDownloading.wallpaperDownloadText.setText(R.string.wallpaper_setup_status_finalizing)
        updateProgress(PROGRESS_INDETERMINATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION && resultCode == Activity.RESULT_OK) {
            // Rationale dialog shown
            // User accepted, ask for permissions again
            requestPermissions(viewModel.storagePermissions, REQUEST_WRITE_STORAGE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
            // Permissions denied
            return
        }

        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            // Storage permission granted
            return saveToGallery()
        }
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