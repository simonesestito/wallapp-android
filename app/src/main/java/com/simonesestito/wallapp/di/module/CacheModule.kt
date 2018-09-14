/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.module

import com.simonesestito.wallapp.MAX_CACHED_PALETTE_SIZE
import com.simonesestito.wallapp.backend.cache.PaletteCache
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class CacheModule {
    @Provides
    @Singleton
    fun paletteCache() = PaletteCache(MAX_CACHED_PALETTE_SIZE)
}