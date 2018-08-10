package it.simonesestito.wallapp.backend.model

import android.os.Parcel
import android.os.Parcelable
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

    constructor(id: String, map: Map<String, Any>) : this(
            id = id,
            displayName = map[KEY_DISPLAY_NAME].toString(),
            description = map[KEY_DESCRIPTION].toString(),
            wallpapersCount = map[KEY_COUNT] as Long)

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