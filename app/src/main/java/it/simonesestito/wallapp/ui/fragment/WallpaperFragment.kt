package it.simonesestito.wallapp.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.palette.graphics.Palette
import com.google.android.material.snackbar.Snackbar
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.ui.dialog.WallpaperPreviewBottomSheet
import it.simonesestito.wallapp.ui.dialog.WallpaperSetupBottomSheet
import it.simonesestito.wallapp.utils.*
import kotlinx.android.synthetic.main.wallpaper_fragment.*
import kotlinx.android.synthetic.main.wallpaper_fragment.view.*
import com.bumptech.glide.request.transition.Transition as GlideTransition

class WallpaperFragment : SharedElementsDestination() {
    override val title = ""

    private val args by lazy {
        WallpaperFragmentArgs.fromBundle(arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.wallpaper_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadWallpaper(
                args.wallpaper,
                getSuggestedWallpaperFormat(resources.displayMetrics),
                imageView = wallpaperImage,
                onPaletteReady = { applyLayoutColor(it) }
        )

        view.backButton.setOnClickListener {
            findNavController().popBackStack()
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
    }

    override fun onPause() {
        super.onPause()
        downloadFab?.show()
    }

    override fun onResume() {
        super.onResume()
        activity?.intent?.let {
            if (it.action == ACTION_PREVIEW_RESULT) {
                restoreWallpaper(requireContext())

                if (it.getIntExtra(EXTRA_WALLPAPER_PREVIEW_RESULT, 0) ==
                        RESULT_WALLPAPER_CONFIRMED) {
                    openSetupBottomSheet()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        hideAppbar()
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

    private fun openSetupBottomSheet() {
        WallpaperSetupBottomSheet()
                .apply {
                    arguments = bundleOf(
                            EXTRA_WALLPAPER_SETUP_PARCELABLE to args.wallpaper
                    )
                }
                .show(childFragmentManager, null)
    }

    private fun openPreviewBottomSheet() {
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(IntentFilter(ACTION_PREVIEW_RESULT)) { intent, context ->
                    val result = intent.getIntExtra(EXTRA_WALLPAPER_PREVIEW_RESULT, -1)
                    if (result == RESULT_WALLPAPER_CONFIRMED) {
                        // TODO Set confirmed wallpaper from preview mode
                        Toast.makeText(context, R.string.todo_coming_soon_message, Toast.LENGTH_SHORT).show()
                    }
                    return@registerReceiver true
                }

        WallpaperPreviewBottomSheet()
                .apply {
                    arguments = bundleOf(
                            EXTRA_WALLPAPER_SETUP_PARCELABLE to args.wallpaper
                    )
                }
                .show(childFragmentManager, null)
    }

    private fun applyLayoutColor(palette: Palette) {
        val primary = palette.getDominantColor(Color.WHITE)
        val isPrimaryLight = primary.isLightColor()
        statusBarColor = primary
        downloadFab.backgroundTintList = ColorStateList.valueOf(primary)

        val primaryUi = if (isPrimaryLight) Color.DKGRAY else Color.WHITE
        backButton.setColorFilter(primaryUi)
        downloadFab.imageTintList = ColorStateList.valueOf(primaryUi)

        val primaryIcons = if (isPrimaryLight) Color.DKGRAY else primary
        bottomAppBar.menu?.forEach { it.icon.setColorFilter(primaryIcons, PorterDuff.Mode.SRC_ATOP) }
    }

    private fun doShare() {
        Snackbar.make(coordinatorRoot, R.string.todo_coming_soon_message, Snackbar.LENGTH_LONG).show()
        // TODO Share wallpaper file
    }

    private fun doPreview() {
        // Check Overlay permission
        if (!requireContext().canDrawOverlays()) {
            startActivityForResult(
                    Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            "package:${BuildConfig.APPLICATION_ID}".toUri()
                    ),
                    REQUEST_PREVIEW_OVERLAY_PERMISSION
            )
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
        downloadFab?.hide()
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
        createdView.wallpaperImage.transitionName = args.transitionName
        bottomAppBar?.replaceMenu(R.menu.wallpaper_fragment_bottom_bar_menu)
    }
    //endregion
}
