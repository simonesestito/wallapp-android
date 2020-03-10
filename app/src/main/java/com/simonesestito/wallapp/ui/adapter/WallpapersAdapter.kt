/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.backend.repository.WallpaperRepository
import com.simonesestito.wallapp.enums.FORMAT_PREVIEW
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
                    wallpaperView,
                    useExactFormatSize = true
            )
            itemView.wallpaperImagePreview.transitionName = wallpaper.id
            itemView.wallpaperImagePreview.apply {
                setOnClickListener {
                    onItemClickListener?.invoke(wallpaper, it)
                }
            }
        }
    }

}

class WallpapersVH(view: View) : RecyclerView.ViewHolder(view) {
    val wallpaperView = itemView.wallpaperImagePreview!!
}

typealias WallpaperClickListener = (Wallpaper, View) -> Unit