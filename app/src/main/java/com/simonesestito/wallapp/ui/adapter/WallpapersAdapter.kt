/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.annotations.FORMAT_PREVIEW
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.backend.repository.impl.WallpaperRepository
import kotlinx.android.synthetic.main.single_category_wallpaper_item.view.*
import javax.inject.Inject

/**
 * Wallpapers Adapter (format PREVIEW)
 */
class WallpapersAdapter @Inject constructor(private val wallpaperRepository: WallpaperRepository)
    : AsyncAdapter<Wallpaper, WallpapersVH>() {

    var onItemClickListener: WallpaperClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpapersVH {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_category_wallpaper_item, parent, false)
        return WallpapersVH(view)
    }

    override fun onBindViewHolder(holder: WallpapersVH, position: Int) {
        val wallpaper = data[position]
        holder.apply {
            wallpaperRepository.loadWallpaper(
                    wallpaper,
                    FORMAT_PREVIEW,
                    wallpaperView
            )
            ViewCompat.setTransitionName(itemView, wallpaper.id)
            itemView.setOnClickListener {
                onItemClickListener?.invoke(wallpaper, it)
            }
        }
    }

}

class WallpapersVH(view: View) : RecyclerView.ViewHolder(view) {
    val wallpaperView = itemView.wallpaperImagePreview!!
}

typealias WallpaperClickListener = (Wallpaper, View) -> Unit