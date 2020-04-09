/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.core.view.updatePadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.palette.graphics.Palette
import com.simonesestito.wallapp.*
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.backend.repository.WallpaperRepository
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.enums.FORMAT_PREVIEW
import com.simonesestito.wallapp.ui.dialog.WallpaperPreviewBottomSheet
import com.simonesestito.wallapp.ui.dialog.WallpaperSetupBottomSheet
import com.simonesestito.wallapp.utils.*
import kotlinx.android.synthetic.main.wallpaper_fragment.*
import kotlinx.android.synthetic.main.wallpaper_fragment.view.*
import javax.inject.Inject

class WallpaperFragment : SharedElementsDestination() {
    private val args by lazy {
        WallpaperFragmentArgs.fromBundle(arguments ?: bundleOf())
    }

    private val wallpaper: Wallpaper by lazy {
        Wallpaper(args.wallpaperId, args.categoryId)
    }

    @Inject
    lateinit var wallpaperRepository: WallpaperRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.wallpaper_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wallpaperRepository.loadWallpaper(
                wallpaper,
                FORMAT_PREVIEW,
                imageView = wallpaperImage,
                useExactFormatSize = true,
                onPaletteReady = { applyLayoutColor(it) }
        )

        view.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        view.downloadFab.setOnClickListener {
            openSetupBottomSheet()
        }

        view.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.wallpaperShare -> doShare()
                R.id.wallpaperPreview -> doPreview()
                else -> return@setOnMenuItemClickListener false
            }
            return@setOnMenuItemClickListener true
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            view.bottomAppBar.updatePadding(bottom = insets.systemWindowInsets.bottom)
            view.backButton.updatePadding(top = insets.systemWindowInsets.top)

            return@setOnApplyWindowInsetsListener insets
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)
    }

    override fun onStart() {
        super.onStart()
        hideAppbar()
    }

    override fun onResume() {
        super.onResume()
        if (activity?.intent?.action == Intent.ACTION_VIEW) {
            // Intent fully handled, remove it from Activity
            activity?.intent = null

            // Show FAB manually since it won't be shown with animation
            downloadFab?.show()

            // This fragment could be launched from URL
            // Handle non existing wallpapers or wrong URL here
            checkWallpaperExistence()
        }
    }

    override fun onPause() {
        super.onPause()
        downloadFab?.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_STORAGE_PERMISSION -> {
                if (grantResults.toList().containsOnly(PackageManager.PERMISSION_GRANTED)) {
                    doPreview()
                }
            }
        }
    }

    private fun checkWallpaperExistence() {
        wallpaperRepository.getWallpaper(wallpaper.categoryId, wallpaper.id)
                .observeOnce(this) {
                    if (it == null) {
                        // Error, this wallpaper doesn't exist
                        handleNonExistingWallpaper()
                    }
                }
    }

    private fun handleNonExistingWallpaper() {
        // Generate link from wallpaper details
        val url = "$BASE_WEBAPP_URL/${wallpaper.categoryId}/${wallpaper.id}"
        requireContext().openUrl(url, forceChrome = true)
    }

    private fun openSetupBottomSheet() {
        WallpaperSetupBottomSheet()
                .apply {
                    arguments = bundleOf(
                            EXTRA_WALLPAPER_SETUP_PARCELABLE to wallpaper
                    )
                }
                .show(childFragmentManager, null)
    }

    private fun openPreviewBottomSheet() {
        // Register receiver to be updated about user decision from PreviewService
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(IntentFilter(ACTION_PREVIEW_RESULT)) { intent, _ ->
                    // This activity is resumed in the foreground by PreviewService
                    // This block handles data received from PreviewService

                    // Handle received result
                    val result = intent.getIntExtra(EXTRA_WALLPAPER_PREVIEW_RESULT, -1)
                    if (result == RESULT_WALLPAPER_CONFIRMED) {
                        // Execute this code only when onStart() has been called
                        executeOnReady {
                            openSetupBottomSheet()
                        }
                    }
                    return@registerReceiver true // Unregister automatically
                }

        WallpaperPreviewBottomSheet()
                .apply {
                    arguments = bundleOf(
                            EXTRA_WALLPAPER_SETUP_PARCELABLE to wallpaper
                    )
                }
                .show(childFragmentManager, null)
    }

    private fun applyLayoutColor(palette: Palette) {
        val defaultColor = ResourcesCompat.getColor(resources, R.color.color_accent, null)
        val primary = palette.getDominantColor(defaultColor)
        val isPrimaryLight = primary.isLightColor()
        val vibrant = palette.getVibrantColor(defaultColor)
        val isVibrantLight = vibrant.isLightColor()

        // Download FAB background as vibrant
        downloadFab.backgroundTintList = ColorStateList.valueOf(vibrant)

        // Back button dark or white according to PRIMARY lightness
        backButton.setColorFilter(
                if (isPrimaryLight) Color.DKGRAY else Color.WHITE
        )

        // Download icon dark/white according to VIBRANT (currently fab background)
        downloadFab.imageTintList = ColorStateList.valueOf(
                if (isVibrantLight) Color.DKGRAY else Color.WHITE
        )

        // Set bottombar icons as vibrant (if dark) or dark gray
        val iconsColor = if (isVibrantLight) Color.DKGRAY else vibrant
        bottomAppBar.menu?.forEach { it.icon.setColorFilter(iconsColor, PorterDuff.Mode.SRC_ATOP) }
    }

    private fun doShare() {
        val url = "$BASE_WEBAPP_URL/${wallpaper.categoryId}/${wallpaper.id}"
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.wallpaper_share_text_message, url))
        startActivity(Intent.createChooser(i, getString(R.string.wallpaper_share_chooser_title)))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestOverlayPermission() {
        startActivityForResult(
                Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        "package:${BuildConfig.APPLICATION_ID}".toUri()
                ),
                REQUEST_PREVIEW_OVERLAY_PERMISSION
        )

        Toast.makeText(requireContext(), R.string.overlay_permission_request, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PREVIEW_OVERLAY_PERMISSION && requireContext().canDrawOverlays()) {
            // Overlay permission granted
            doPreview()
        } else if (requestCode == REQUEST_READ_STORAGE_PERMISSION && resultCode == Activity.RESULT_OK) {
            // User wants to grant storage permission
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_STORAGE_PERMISSION)
        }
    }

    private fun doPreview() {
        // Check Overlay permission
        if (!requireContext().canDrawOverlays()) {
            requestOverlayPermission()
            return
        }

        // Check EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return requestPermissionsRationale(
                    R.string.permission_read_storage_request_message,
                    REQUEST_READ_STORAGE_PERMISSION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        openPreviewBottomSheet()
    }

    //region SharedElements methods
    override fun onPreSharedElementsTransition() {
        super.onPreSharedElementsTransition()
        bottomAppBar?.visibility = View.INVISIBLE
    }

    override fun onPostSharedElementsTransition() {
        super.onPostSharedElementsTransition()
        context ?: return
        bottomAppBar?.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(context, R.anim.bottom_bar_up)
                .addListener(
                        onEnd = { downloadFab?.show() }
                )
        bottomAppBar?.startAnimation(animation)
    }

    override fun onPrepareSharedElements(createdView: View) {
        createdView.wallpaperImage.transitionName = args.wallpaperId
        bottomAppBar?.replaceMenu(R.menu.wallpaper_fragment_bottom_bar_menu)
    }
    //endregion
}
