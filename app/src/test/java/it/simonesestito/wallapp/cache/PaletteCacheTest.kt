/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.cache

import android.graphics.Color
import androidx.palette.graphics.Palette
import it.simonesestito.wallapp.backend.cache.PaletteCache
import it.simonesestito.wallapp.backend.model.Wallpaper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class PaletteCacheTest {
    private val cacheSize = 2
    private val wallA = Wallpaper("id-A", "cat-A")
    private val wallB = Wallpaper("id-B", "cat-B")
    private val wallC = Wallpaper("id-C", "cat-C")
    private val wallD = Wallpaper("id-D", "cat-D")
    private val paletteA = Palette.from(listOf(Palette.Swatch(Color.RED, 10)))
    private val paletteB = Palette.from(listOf(Palette.Swatch(Color.GREEN, 20)))
    private val paletteC = Palette.from(listOf(Palette.Swatch(Color.BLACK, 30)))
    private val paletteD = Palette.from(listOf(Palette.Swatch(Color.BLUE, 40)))

    private lateinit var cache: PaletteCache

    @Before
    fun init() {
        cache = PaletteCache(cacheSize)
    }

    @Test
    fun testSize() {
        assertEquals(cacheSize, cache.maxSize())
    }

    @Test
    fun singlePut() {
        assertEquals(0, cache.putCount())
        assertNull(cache[wallA])
        cache[wallA] = paletteA
        assertEquals(1, cache.putCount())
        assertEquals(paletteA, cache[wallA])
    }

    @Test
    fun fullSize() {
        assertEquals(0, cache.putCount())
        cache[wallA] = paletteA
        cache[wallB] = paletteB
        assertEquals(cacheSize, cache.putCount())
        assertEquals(paletteA, cache[wallA])
        assertEquals(paletteB, cache[wallB])
        assertNull(cache[wallC])
    }

    @Test
    fun overSize() {
        assertEquals(0, cache.putCount())
        cache[wallA] = paletteA
        cache[wallB] = paletteB
        cache[wallC] = paletteC
        cache[wallD] = paletteD
        assertEquals(cacheSize * 2, cache.putCount())
        assertNull(cache[wallA])
        assertNull(cache[wallB])
        assertEquals(paletteC, cache[wallC])
        assertEquals(paletteD, cache[wallD])
    }
}