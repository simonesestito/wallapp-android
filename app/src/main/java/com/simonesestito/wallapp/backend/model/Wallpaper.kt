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

package com.simonesestito.wallapp.backend.model

import android.os.Parcelable
import com.simonesestito.wallapp.Identifiable
import com.simonesestito.wallapp.SCALEWAY_BUCKET_URL
import com.simonesestito.wallapp.STORAGE_CATEGORIES
import com.simonesestito.wallapp.STORAGE_WALLPAPERS
import com.simonesestito.wallapp.enums.WallpaperFormat
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wallpaper constructor(
        override val id: String,
        val categoryId: String,
        val authorBio: String?,
        val authorName: String?,
        val authorSocial: String?
) : Identifiable<String>, Parcelable {
    @IgnoredOnParcel
    val fullId = "$categoryId/$id"

    fun getStorageFileUrl(@WallpaperFormat format: String) =
            "$SCALEWAY_BUCKET_URL/$STORAGE_CATEGORIES/$categoryId/$STORAGE_WALLPAPERS/$id/$format"
}