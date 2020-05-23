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

package com.simonesestito.wallapp.backend.androidservice

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.simonesestito.wallapp.*
import com.simonesestito.wallapp.backend.cache.PaletteCache
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.ui.activity.MainActivity
import com.simonesestito.wallapp.utils.TAG
import com.simonesestito.wallapp.utils.isLightColor
import com.simonesestito.wallapp.utils.restoreWallpaper
import kotlinx.android.synthetic.main.preview_floating_window.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class PreviewService : FloatingWindowService() {
    private val ioCoroutine = CoroutineScope(Dispatchers.IO)
    private val uiCoroutine = CoroutineScope(Dispatchers.Main)
    private lateinit var wallpaper: Wallpaper
    @Inject lateinit var paletteCache: PaletteCache

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
        AppInjector.getInstance().inject(this)
        startForeground()
    }

    private fun startForeground() {
        val pendingIntent = PendingIntent.getActivity(
                this,
                PREVIEW_SERVICE_PENDING_INTENT_ID,
                getMainActivityIntent(),
                0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                    PREVIEW_SERVICE_NOTIFICATION_CHANNEL,
                    getString(R.string.preview_mode_title),
                    NotificationManager.IMPORTANCE_LOW
            )
            notificationManager?.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, PREVIEW_SERVICE_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.brush)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentTitle(getString(R.string.preview_service_notification_title))
                .setContentText(getString(R.string.preview_service_notification_text))
                .setContentIntent(pendingIntent)
                .build()

        startForeground(PREVIEW_SERVICE_NOTIFICATION_ID, notification)
    }

    override fun onViewAdded(view: View, arguments: Bundle?) {
        super.onViewAdded(view, arguments)
        view.previewModeButtonPositive.setOnClickListener { sendResult(view, true) }
        view.previewModeButtonNegative.setOnClickListener { sendResult(view, false) }
        val default = Color.TRANSPARENT
        Log.d(TAG, "Wallpaper $wallpaper")
        val color = paletteCache[wallpaper]?.getDominantColor(default) ?: default
        Log.d(TAG, "Cached color: $color")
        if (color != default) {
            // Apply color to UI
            view.previewFloatingWindowRoot.setBackgroundColor(color)
            val uiColor = if (color.isLightColor()) Color.BLACK else Color.WHITE
            view.previewModeBannerTitle.setTextColor(uiColor)
            view.previewModeButtonPositive.setColorFilter(uiColor, PorterDuff.Mode.SRC_ATOP)
            view.previewModeButtonNegative.setColorFilter(uiColor, PorterDuff.Mode.SRC_ATOP)
        }
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

    private fun getMainActivityIntent() = Intent(this, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
        ioCoroutine.launch {
            if (!wallpaperConfirmed) {
                restoreWallpaper(this@PreviewService)
            }

            // Continue on Main thread
            uiCoroutine.launch {
                val intent = Intent()
                        .setAction(ACTION_PREVIEW_RESULT)
                        .putExtra(EXTRA_WALLPAPER_PREVIEW_RESULT,
                                if (wallpaperConfirmed)
                                    RESULT_WALLPAPER_CONFIRMED
                                else
                                    RESULT_WALLPAPER_CANCELED)

                // Notify the rest of the app about the user decision
                LocalBroadcastManager.getInstance(this@PreviewService)
                        .sendBroadcast(intent)

                // Resume app in the foreground
                startActivity(getMainActivityIntent())
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ioCoroutine.launch {
            // If it's already been restored, this method will just return
            restoreWallpaper(this@PreviewService)
        }
    }
}