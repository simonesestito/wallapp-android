package it.simonesestito.wallapp.backend.service

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.backend.model.Wallpaper
import kotlinx.android.synthetic.main.preview_floating_window.view.*


class PreviewService : FloatingWindowService() {
    private lateinit var wallpaper: Wallpaper

    @SuppressLint("InflateParams")
    override fun onCreateView(arguments: Bundle?, layoutInflater: LayoutInflater): View {
        this.wallpaper = arguments!!.getParcelable(EXTRA_WALLPAPER_PREVIEW_WINDOW_PARCELABLE)!!
        return layoutInflater.inflate(R.layout.preview_floating_window, null, false)
    }

    override fun onViewAdded(view: View, arguments: Bundle?) {
        super.onViewAdded(view, arguments)
        view.previewModeButtonPositive.setOnClickListener { sendResult(true) }
        view.previewModeButtonNegative.setOnClickListener { sendResult(false) }
    }

    /**
     * Caller must have registered a [android.content.BroadcastReceiver] with [LocalBroadcastManager]
     * Broadcasts are sent with action [ACTION_PREVIEW_RESULT]
     */
    private fun sendResult(wallpaperConfirmed: Boolean) {
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