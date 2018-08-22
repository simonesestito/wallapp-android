/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.model

import android.os.Parcel
import it.simonesestito.wallapp.backend.model.Wallpaper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WallpaperTest {
    @Test
    fun toParcel() {
        val wallpaper = Wallpaper("wall-id", "category-id")
        val parcel = Parcel.obtain()
        wallpaper.writeToParcel(parcel, 0)
        assertNotEquals(0, parcel.dataPosition())
        parcel.setDataPosition(0)
        assertEquals(0, parcel.dataPosition())
        assertEquals(wallpaper, Wallpaper(parcel))
        parcel.setDataPosition(0)
        assertEquals(wallpaper, Wallpaper.createFromParcel(parcel))
    }
}