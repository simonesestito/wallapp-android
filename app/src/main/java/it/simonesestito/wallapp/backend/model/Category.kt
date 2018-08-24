/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.backend.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import it.simonesestito.wallapp.Identifiable
import it.simonesestito.wallapp.KEY_COUNT
import it.simonesestito.wallapp.KEY_DESCRIPTION
import it.simonesestito.wallapp.KEY_DISPLAY_NAME

data class Category(
        override val id: String,
        val displayName: String,
        val description: String,
        val wallpapersCount: Long
) : Identifiable<String>, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readLong())

    constructor(snap: DocumentSnapshot) : this(
            id = snap.id,
            displayName = snap.getString(KEY_DISPLAY_NAME) ?: "",
            description = snap.getString(KEY_DESCRIPTION) ?: "",
            wallpapersCount = snap.getLong(KEY_COUNT) ?: 0)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(displayName)
        parcel.writeString(description)
        parcel.writeLong(wallpapersCount)
    }

    override fun describeContents() = hashCode()

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel) = Category(parcel)
        override fun newArray(size: Int)= arrayOfNulls<Category?>(size)
    }
}