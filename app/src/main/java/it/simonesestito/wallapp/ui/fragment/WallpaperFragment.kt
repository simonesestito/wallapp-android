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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.palette.graphics.Palette
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.annotations.FORMAT_PREVIEW
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.backend.repository.IWallpaperRepository
import it.simonesestito.wallapp.di.component.DaggerFragmentInjector
import it.simonesestito.wallapp.ui.activity.MainActivity
import it.simonesestito.wallapp.ui.dialog.WallpaperPreviewBottomSheet
import it.simonesestito.wallapp.ui.dialog.WallpaperSetupBottomSheet
import it.simonesestito.wallapp.utils.*
import kotlinx.android.synthetic.main.wallpaper_fragment.*
import kotlinx.android.synthetic.main.wallpaper_fragment.view.*
import javax.inject.Inject
import com.bumptech.glide.request.transition.Transition as GlideTransition

class WallpaperFragment : SharedElementsDestination() {
    override val title = ""

    private val args by lazy {
        WallpaperFragmentArgs.fromBundle(arguments)
    }

    private val wallpaper: Wallpaper by lazy {
        Wallpaper(args.wallpaperId, args.categoryId)
    }

    @Inject lateinit var wallpaperRepository: IWallpaperRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.wallpaper_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wallpaperRepository.loadWallpaper(
                wallpaper,
                FORMAT_PREVIEW, //getSuggestedWallpaperFormat(resources.displayMetrics),
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerFragmentInjector.create().inject(this)

        // TODO: Handle wrong wallpaper from URL
    }

    override fun onPause() {
        super.onPause()
        downloadFab?.show()
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
                            EXTRA_WALLPAPER_SETUP_PARCELABLE to wallpaper
                    )
                }
                .show(childFragmentManager, null)
    }

    private fun openPreviewBottomSheet() {
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(IntentFilter(ACTION_PREVIEW_RESULT)) { intent, receiverContext ->
                    // Resume current Activity
                    receiverContext.startActivity(Intent(receiverContext, MainActivity::class.java))

                    // Handle received result
                    val result = intent.getIntExtra(EXTRA_WALLPAPER_PREVIEW_RESULT, -1)
                    if (result == RESULT_WALLPAPER_CONFIRMED) {
                        // Execute this code only when onStart() has been called
                        executeOnReady {
                            openSetupBottomSheet()
                        }
                    }
                    return@registerReceiver true
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
        val appAccent = ResourcesCompat.getColor(resources, R.color.color_accent, null)

        val primary = palette.getDominantColor(appAccent)
        statusBarColor = primary

        val vibrant = palette.getVibrantColor(appAccent)
        val isVibrantLight = vibrant.isLightColor()
        downloadFab.backgroundTintList = ColorStateList.valueOf(vibrant)

        val uiColor = if (isVibrantLight) Color.DKGRAY else Color.WHITE
        backButton.setColorFilter(uiColor)
        downloadFab.imageTintList = ColorStateList.valueOf(uiColor)

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
