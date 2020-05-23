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