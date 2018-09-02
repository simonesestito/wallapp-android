/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.backend.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import it.simonesestito.wallapp.*
import it.simonesestito.wallapp.utils.LocalizedString

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
            displayName = try {
                snap.get(KEY_DISPLAY_NAME) as LocalizedString
            } catch (e: ClassCastException) {
                //FIXME: temporary until all categories are translated
                mapOf(FIRESTORE_LOCALIZED_DEFAULT to snap.getString(KEY_DISPLAY_NAME)) as LocalizedString
            },
            description = try {
                snap.get(KEY_DESCRIPTION) as LocalizedString
            } catch (e: ClassCastException) {
                //FIXME: temporary until all categories are translated
                mapOf(FIRESTORE_LOCALIZED_DEFAULT to snap.getString(KEY_DESCRIPTION)) as LocalizedString
            },
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
        override fun newArray(size: Int)= arrayOfNulls<Category?>(size)
    }
}