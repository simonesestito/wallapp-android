/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
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
        view.wallpaperAuthorBio.text = wallpaperArg.authorBio ?: ""
        view.wallpaperAuthorName.text = wallpaperArg.authorName ?: ""
        view.wallpaperAuthorSocial.visibility = if (wallpaperArg.authorSocial == null) View.GONE else View.VISIBLE
        view.wallpaperAuthorSocial.setOnClickListener {
            // Normalize URL
            val url = if (wallpaperArg.authorSocial?.startsWith("http://") == true ||
                    wallpaperArg.authorSocial?.startsWith("https://") == true)
                wallpaperArg.authorSocial!!
            else
                "https://${wallpaperArg.authorSocial}"

            requireContext().openUrl(url)
        }
    }
}