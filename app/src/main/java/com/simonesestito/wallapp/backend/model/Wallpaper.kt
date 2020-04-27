/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.model

import android.os.Parcel
import android.os.Parcelable
import com.simonesestito.wallapp.Identifiable
import com.simonesestito.wallapp.SCALEWAY_BUCKET_URL
import com.simonesestito.wallapp.STORAGE_CATEGORIES
import com.simonesestito.wallapp.STORAGE_WALLPAPERS
import com.simonesestito.wallapp.enums.WallpaperFormat


data class Wallpaper constructor(
        override val id: String,
        val categoryId: String,
        val authorBio: String?,
        val authorName: String?,
        val authorSocial: String?
) : Identifiable<String>, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    fun getStorageFileUrl(@WallpaperFormat format: String) =
            "$SCALEWAY_BUCKET_URL/$STORAGE_CATEGORIES/$categoryId/$STORAGE_WALLPAPERS/$id/$format"

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(categoryId)
        parcel.writeString(authorBio)
        parcel.writeString(authorName)
        parcel.writeString(authorSocial)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Wallpaper> {
        override fun createFromParcel(parcel: Parcel): Wallpaper {
            return Wallpaper(parcel)
        }

        override fun newArray(size: Int): Array<Wallpaper?> {
            return arrayOfNulls(size)
        }
    }
}