/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.simonesestito.wallapp.Identifiable
import com.simonesestito.wallapp.KEY_COUNT
import com.simonesestito.wallapp.KEY_DESCRIPTION
import com.simonesestito.wallapp.KEY_DISPLAY_NAME
import com.simonesestito.wallapp.utils.LocalizedString

data class Category(
        override val id: String,
        val displayName: LocalizedString,
        val description: LocalizedString,
        val wallpapersCount: Long
) : Identifiable<String>, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            mapOf<String, Any>().apply {
                parcel.readMap(this, this.javaClass.classLoader)
            },
            mapOf<String, Any>().apply {
                parcel.readMap(this, this.javaClass.classLoader)
            },
            parcel.readLong())

    @Suppress("UNCHECKED_CAST")
    constructor(snap: DocumentSnapshot) : this(
            id = snap.id,
            displayName = snap.get(KEY_DISPLAY_NAME) as LocalizedString,
            description = snap.get(KEY_DESCRIPTION) as LocalizedString,
            wallpapersCount = snap.getLong(KEY_COUNT) ?: 0)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeMap(displayName)
        parcel.writeMap(description)
        parcel.writeLong(wallpapersCount)
    }

    override fun describeContents() = hashCode()

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel) = Category(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Category?>(size)
    }
}