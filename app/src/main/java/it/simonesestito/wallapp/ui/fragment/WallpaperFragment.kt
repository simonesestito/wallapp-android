package it.simonesestito.wallapp.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.view.forEach
import androidx.palette.graphics.Palette
import androidx.transition.TransitionInflater
import com.google.android.material.bottomsheet.BottomSheetBehavior
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.backend.service.WallpaperService
import it.simonesestito.wallapp.utils.*
import kotlinx.android.synthetic.main.wallpaper_fragment.*
import kotlinx.android.synthetic.main.wallpaper_fragment.view.*
import com.bumptech.glide.request.transition.Transition as GlideTransition

class WallpaperFragment : AbstractAppFragment() {
    override val title = ""

    private val args by lazy {
        WallpaperFragmentArgs.fromBundle(arguments)
    }

    private val bottomSheetBehavior: BottomSheetBehavior<out View> by lazy {
        BottomSheetBehavior.from(wallpaperBottomSheet)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pause enter transition and setup sharedElements one
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)
                .apply {
                    interpolator = DecelerateInterpolator(2.0f)
                    duration = 400
                }
                .addListener(
                        onStart = {
                            bottomAppBar?.visibility = View.INVISIBLE
                            downloadFab?.hide()
                        },
                        onEnd = {
                            bottomAppBar?.visibility = View.VISIBLE
                            context ?: return@addListener
                            val animation = AnimationUtils.loadAnimation(context, R.anim.bottom_bar_up)
                                    .addListener(
                                            onEnd = { downloadFab?.show() }
                                    )
                            bottomAppBar?.startAnimation(animation)
                        }
                )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.wallpaper_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Hide BottomSheet on startup
        bottomSheetBehavior.hide()

        // Finish to setup views for Shared Elements
        view.wallpaperImage.transitionName = args.transitionName
        prepareBottomMenu()
        startPostponedEnterTransition()

        view.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        view.downloadFab.setOnClickListener { bottomSheetBehavior.show() }

        WallpaperService.loadWallpaper(
                args.wallpaper,
                getSuggestedWallpaperFormat(resources.displayMetrics),
                imageView = wallpaperImage,
                onPaletteReady = { applyLayoutColor(it) }
        )
    }

    override fun onPause() {
        super.onPause()
        downloadFab?.show()
    }

    override fun onStart() {
        super.onStart()
        hideAppbar()
    }

    private fun prepareBottomMenu() {
        bottomAppBar?.replaceMenu(R.menu.wallpaper_fragment_bottom_bar_menu)
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
}
