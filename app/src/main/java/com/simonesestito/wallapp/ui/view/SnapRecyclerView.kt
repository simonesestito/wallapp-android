/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView


class SnapRecyclerView : RecyclerView {
    private val snapHelper = LinearSnapHelper()
    private val scrollListener = object : OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == SCROLL_STATE_IDLE) {
                snapHelper.findSnapView(layoutManager)?.let { view ->
                    val distance = snapHelper.calculateDistanceToFinalSnap(layoutManager!!, view)!!
                    smoothScrollBy(distance[0], distance[1])
                }
            }
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    var snapEnabled: Boolean = false
        set(value) {
            if (field == value)
                return
            field = value
            
            if (value) {
                attachSnapListeners()
            } else {
                detachSnapListeners()
            }
        }

    private fun attachSnapListeners() {
        // Avoid glitch to return in position
        // Go to the right position without animation
        snapHelper.findSnapView(layoutManager)?.let { view ->
            val distance = snapHelper.calculateDistanceToFinalSnap(layoutManager!!, view)!!
            scrollBy(distance[0], distance[1])
        }

        // Set the listener
        addOnScrollListener(scrollListener)
    }

    private fun detachSnapListeners() {
        removeOnScrollListener(scrollListener)
    }
}
