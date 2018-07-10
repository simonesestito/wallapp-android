package it.simonesestito.wallapp.model

import android.os.Parcel
import android.os.Parcelable

data class Category(
        val id: String,
        val displayName: String,
        val description: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            displayName = parcel.readString(),
            description = parcel.readString())

    constructor(id: String, map: Map<String, Any>) : this(
            id = id,
            displayName = map["displayName"].toString(),
            description = map["description"].toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(displayName)
        parcel.writeString(description)
    }

    override fun describeContents() = this.hashCode()

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel) = Category(parcel)
        override fun newArray(size: Int): Array<Category?> = arrayOfNulls(size)
    }
}