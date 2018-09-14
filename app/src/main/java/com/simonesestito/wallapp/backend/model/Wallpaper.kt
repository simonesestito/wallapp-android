/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.model

import android.os.Parcel
import android.os.Parcelable
import com.simonesestito.wallapp.Identifiable
import com.simonesestito.wallapp.STORAGE_CATEGORIES
import com.simonesestito.wallapp.STORAGE_WALLPAPERS
import com.simonesestito.wallapp.annotations.WallpaperFormat


data class Wallpaper constructor(override val id: String, val categoryId: String) : Identifiable<String>, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(categoryId)
    }

    fun getStorageFilePath(@WallpaperFormat format: String) =
            "$STORAGE_CATEGORIES/$categoryId/$STORAGE_WALLPAPERS/$id/$format"

    override fun describeContents() = hashCode()

    companion object CREATOR : Parcelable.Creator<Wallpaper> {
        override fun createFromParcel(parcel: Parcel): Wallpaper {
            return Wallpaper(parcel)
        }

        override fun newArray(size: Int): Array<Wallpaper?> {
            return arrayOfNulls(size)
        }
    }

}