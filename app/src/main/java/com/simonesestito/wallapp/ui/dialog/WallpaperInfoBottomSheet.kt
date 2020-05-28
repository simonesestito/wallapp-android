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

package com.simonesestito.wallapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.simonesestito.wallapp.EXTRA_WALLPAPER_BOTTOMSHEET_PARCELABLE
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.utils.openUrl
import kotlinx.android.synthetic.main.wallpaper_info_bottomsheet.view.*

class WallpaperInfoBottomSheet : AbstractAppBottomSheet() {
    private val wallpaperArg: Wallpaper by lazy {
        arguments?.getParcelable<Wallpaper>(EXTRA_WALLPAPER_BOTTOMSHEET_PARCELABLE)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wallpaper_info_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.wallpaperAuthorBio.text = wallpaperArg.authorBio ?: Wallpaper.DEFAULT_AUTHOR_BIO
        view.wallpaperAuthorName.text = wallpaperArg.authorName ?: Wallpaper.DEFAULT_AUTHOR_NAME
        view.wallpaperAuthorSocial.setOnClickListener {
            // Normalize URL
            val url = if (wallpaperArg.authorSocial == null)
                Wallpaper.DEFAULT_AUTHOR_SOCIAL
            else if (wallpaperArg.authorSocial?.startsWith("http://") == true ||
                    wallpaperArg.authorSocial?.startsWith("https://") == true)
                wallpaperArg.authorSocial!!
            else
                "https://${wallpaperArg.authorSocial}"

            requireContext().openUrl(url)
        }
    }
}