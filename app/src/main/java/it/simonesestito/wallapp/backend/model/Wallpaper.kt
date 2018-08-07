package it.simonesestito.wallapp.backend.model

import android.os.Parcel
import android.os.Parcelable
import it.simonesestito.wallapp.STORAGE_CATEGORIES
import it.simonesestito.wallapp.STORAGE_WALLPAPERS
import it.simonesestito.wallapp.annotations.WallpaperFormat
import it.simonesestito.wallapp.utils.Identifiable


data class Wallpaper(override val id: String, val categoryId: String) : Identifiable<String>, Parcelable {
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