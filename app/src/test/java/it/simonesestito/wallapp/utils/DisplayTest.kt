package it.simonesestito.wallapp.utils

import android.util.DisplayMetrics
import it.simonesestito.wallapp.annotations.FORMAT_16_9
import it.simonesestito.wallapp.annotations.FORMAT_18_9
import it.simonesestito.wallapp.annotations.FORMAT_IPHONE
import it.simonesestito.wallapp.annotations.FORMAT_IPHONE_X
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class DisplayTest {

    @Test
    fun display18_9() {
        val display = mockedMetrics(1440, 2960)
        val format = getSuggestedWallpaperFormat(display)
        assertEquals(FORMAT_18_9, format)
    }

    @Test
    fun display16_9() {
        val display = mockedMetrics(1080, 1920)
        val format = getSuggestedWallpaperFormat(display)
        assertEquals(FORMAT_16_9, format)
    }

    @Test
    fun displayIphone() {
        val display = mockedMetrics(750, 1334)
        val format = getSuggestedWallpaperFormat(display)
        assertEquals(FORMAT_IPHONE, format)
    }

    @Test
    fun displayIphoneX() {
        val display = mockedMetrics(1125, 2436)
        val format = getSuggestedWallpaperFormat(display)
        assertEquals(FORMAT_IPHONE_X, format)
    }

    private fun mockedMetrics(width: Int, height: Int) =
            Mockito.mock(DisplayMetrics::class.java).apply {
                widthPixels = width
                heightPixels = height
            }
}