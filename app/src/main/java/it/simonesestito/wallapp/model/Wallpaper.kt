package it.simonesestito.wallapp.model

import android.os.Parcel
import android.os.Parcelable


data class Wallpaper(val id: String): Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
    }

    override fun describeContents() = this.hashCode()

    companion object CREATOR : Parcelable.Creator<Wallpaper> {
        override fun createFromParcel(parcel: Parcel)= Wallpaper(parcel)
        override fun newArray(size: Int): Array<Wallpaper?> = arrayOfNulls(size)
    }
}