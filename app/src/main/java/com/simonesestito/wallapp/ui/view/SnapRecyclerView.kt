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
                doSnapAction()
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
        doSnapAction()
        addOnScrollListener(scrollListener)
    }

    private fun detachSnapListeners() {
        removeOnScrollListener(scrollListener)
    }

    private fun doSnapAction() {
        layoutManager ?: return
        snapHelper.findSnapView(layoutManager)?.let { view ->
            val viewIndex = layoutManager?.getPosition(view) ?: 0
            if (viewIndex != 0 && viewIndex != adapter?.itemCount) {
                val distance = snapHelper.calculateDistanceToFinalSnap(layoutManager!!, view)!!
                scrollBy(distance[0], distance[1])
            }
        }
    }
}
