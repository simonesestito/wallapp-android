package it.simonesestito.wallapp.backend.service

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.dagger.component.DaggerServiceInjector
import it.simonesestito.wallapp.utils.ThreadUtils
import it.simonesestito.wallapp.utils.restoreWallpaper
import kotlinx.android.synthetic.main.preview_floating_window.view.*
import javax.inject.Inject


class PreviewService : FloatingWindowService() {
    private lateinit var wallpaper: Wallpaper
    @Inject lateinit var threads: ThreadUtils

    override val isFloatingWindow: Boolean
        get() = false // Don't allow dragging

    @SuppressLint("InflateParams")
    override fun onCreateView(arguments: Bundle?, layoutInflater: LayoutInflater): View {
        this.wallpaper = arguments!!.getParcelable(EXTRA_WALLPAPER_PREVIEW_WINDOW_PARCELABLE)!!

        return layoutInflater.inflate(R.layout.preview_floating_window, null, false).let {
            it.setOnApplyWindowInsetsListener { view, insets ->
                // Add status bar height
                view.setPadding(
                        view.paddingLeft,
                        view.paddingTop + insets.stableInsetTop,
                        view.paddingRight,
                        view.paddingEnd)
                view.requestLayout()
                return@setOnApplyWindowInsetsListener insets
            }
            return@let it
        }
    }

    override fun onCreate() {
        super.onCreate()
        DaggerServiceInjector.create().inject(this)
    }

    override fun onViewAdded(view: View, arguments: Bundle?) {
        super.onViewAdded(view, arguments)
        view.previewModeButtonPositive.setOnClickListener { sendResult(view, true) }
        view.previewModeButtonNegative.setOnClickListener { sendResult(view, false) }
    }

    override fun onCreateLayoutParams() =
            super.onCreateLayoutParams().apply {
                gravity = Gravity.TOP
                y = 0
                width = WindowManager.LayoutParams.MATCH_PARENT
                flags = flags or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
            }

    /**
     * Caller must have registered a [android.content.BroadcastReceiver] with [LocalBroadcastManager]
     * Broadcasts are sent with action [ACTION_PREVIEW_RESULT]
     * @param rootView The entire banner View
     * @param wallpaperConfirmed Boolean which indicates if the wallpaper has been accepted or declined by the user
     */
    private fun sendResult(rootView: View, wallpaperConfirmed: Boolean) {
        rootView.previewModeButtonPositive.visibility = View.GONE
        rootView.previewModeButtonNegative.visibility = View.GONE
        rootView.previewModeBannerTitle.setText(R.string.preview_mode_title_close_preview)

        // Restore wallpaper on IO Thread, it's a blocking operation
        threads.runOnIoThread {
            if (!wallpaperConfirmed) {
                restoreWallpaper(this)
            }

            // Continue on Main thread
            threads.runOnMainThread {
                val intent = Intent()
                        .setAction(ACTION_PREVIEW_RESULT)
                        .putExtra(EXTRA_WALLPAPER_PREVIEW_RESULT,
                                if (wallpaperConfirmed)
                                    RESULT_WALLPAPER_CONFIRMED
                                else
                                    RESULT_WALLPAPER_CANCELED)

                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(intent)
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        threads.runOnIoThread {
            // If it's already been restored, this method will just return
            restoreWallpaper(this)
        }
    }
}