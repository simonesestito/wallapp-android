/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.model

import android.os.Parcel
import android.os.Parcelable
import com.simonesestito.wallapp.Identifiable

data class Category(
        val data: FirebaseCategory,
        val unseenCount: Int
) : Identifiable<String>, Parcelable {
    override val id: String = data.id

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(FirebaseCategory::class.java.classLoader)!!,
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(data, flags)
        parcel.writeInt(unseenCount)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel): Category {
            return Category(parcel)
        }

        override fun newArray(size: Int): Array<Category?> {
            return arrayOfNulls(size)
        }
    }
}