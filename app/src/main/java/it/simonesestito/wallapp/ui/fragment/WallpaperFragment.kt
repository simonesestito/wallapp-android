package it.simonesestito.wallapp.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.palette.graphics.Palette
import com.google.android.material.snackbar.Snackbar
import it.simonesestito.wallapp.ARG_WALLPAPER_SETUP_PARCELABLE
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.backend.service.WallpaperService
import it.simonesestito.wallapp.ui.dialog.WallpaperSetupBottomSheet
import it.simonesestito.wallapp.utils.addListener
import it.simonesestito.wallapp.utils.findNavController
import it.simonesestito.wallapp.utils.getSuggestedWallpaperFormat
import it.simonesestito.wallapp.utils.isLightColor
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
        WallpaperService.loadWallpaper(
                args.wallpaper,
                getSuggestedWallpaperFormat(resources.displayMetrics),
                imageView = wallpaperImage,
                onPaletteReady = { applyLayoutColor(it) }
        )

        view.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        view.downloadFab.setOnClickListener {
            WallpaperSetupBottomSheet()
                    .apply {
                        arguments = bundleOf(
                                ARG_WALLPAPER_SETUP_PARCELABLE to args.wallpaper
                        )
                    }
                    .show(childFragmentManager, null)
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

    override fun onStart() {
        super.onStart()
        hideAppbar()
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
        Snackbar.make(coordinatorRoot, R.string.available_soon, Snackbar.LENGTH_LONG).show()
        // TODO
    }

    private fun doPreview() {
        Snackbar.make(coordinatorRoot, R.string.available_soon, Snackbar.LENGTH_LONG).show()
        // TODO
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
