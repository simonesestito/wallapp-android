/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.simonesestito.wallapp.*
import com.simonesestito.wallapp.enums.CATEGORY_GROUP_ORIGINAL
import com.simonesestito.wallapp.enums.CategoryGroup
import com.simonesestito.wallapp.enums.FORMAT_COVER
import com.simonesestito.wallapp.utils.LocalizedString

data class Category(
        override val id: String,
        val displayName: LocalizedString,
        val description: LocalizedString,
        @CategoryGroup val group: String,
        val wallpapersCount: Long
) : Identifiable<String>, Parcelable {
    val previewImageUrl = "$SCALEWAY_BUCKET_URL/$STORAGE_CATEGORIES/$FORMAT_COVER"

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            mutableMapOf<String, Any>().apply {
                parcel.readMap(this as Map<*, *>, this.javaClass.classLoader)
            },
            mutableMapOf<String, Any>().apply {
                parcel.readMap(this as Map<*, *>, this.javaClass.classLoader)
            },
            parcel.readString()!!,
            parcel.readLong())

    @Suppress("UNCHECKED_CAST")
    constructor(snap: DocumentSnapshot) : this(
            id = snap.id,
            displayName = snap[KEY_DISPLAY_NAME] as LocalizedString,
            description = snap[KEY_DESCRIPTION] as LocalizedString,
            group = snap[KEY_CATEGORY_GROUP] as String? ?: CATEGORY_GROUP_ORIGINAL,
            wallpapersCount = snap[KEY_CATEGORY_ITEMS_COUNT] as Long? ?: 0)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeMap(displayName)
        parcel.writeMap(description)
        parcel.writeString(group)
        parcel.writeLong(wallpapersCount)
    }

    override fun describeContents() = hashCode()

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel) = Category(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Category?>(size)
    }
}