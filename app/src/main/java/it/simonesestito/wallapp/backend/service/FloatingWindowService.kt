/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.backend.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat


abstract class FloatingWindowService : Service() {
    private var floatingView: View? = null
    private val windowManager by lazy {
        ContextCompat.getSystemService(this, WindowManager::class.java)!!
    }

    /**
     * Inflate the view to attach later
     * @param arguments Bundle from [Intent.getExtras]
     * @param layoutInflater LayoutInflater useful to inflate the view
     * @return Created view
     */
    abstract fun onCreateView(arguments: Bundle?, layoutInflater: LayoutInflater): View

    /**
     * Create the required [WindowManager.LayoutParams] for the target view
     * @return Created layout params
     */
    @Suppress("DEPRECATION")
    open fun onCreateLayoutParams(): WindowManager.LayoutParams =
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    else
                        WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            )

    /**
     * Called the view has been added to the [WindowManager]
     * @param view The view just added
     * @param arguments Bundle included with the Intent
     */
    open fun onViewAdded(view: View, arguments: Bundle?) {}

    /**
     * Indicates if the window can be dragged and moved around the screen
     * Default: true
     */
    open val isFloatingWindow: Boolean
        get() = true

    //region Service lifecycle methods
    override fun onBind(p0: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (floatingView == null) {
            val inflater = LayoutInflater.from(this)
            val extras = intent?.extras
            val view = onCreateView(extras, inflater)

            val layoutParams = onCreateLayoutParams()
            windowManager.addView(view, layoutParams)
            if (isFloatingWindow) {
                view.setOnTouchListener(DragTouchListener(layoutParams, windowManager, view))
            }
            floatingView = view
            onViewAdded(view, extras)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager.removeView(floatingView)
        }
    }
    //endregion

    class DragTouchListener(private val params: WindowManager.LayoutParams,
                            private val windowManager: WindowManager,
                            private val floatingView: View) : View.OnTouchListener {
        private var initialX: Int = 0
        private var initialY: Int = 0
        private var initialTouchX: Float = 0f
        private var initialTouchY: Float = 0f

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingView, params)
                }

                else -> return false
            }
            return true
        }
    }
}