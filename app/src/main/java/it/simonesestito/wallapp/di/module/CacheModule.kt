/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.di.module

import dagger.Module
import dagger.Provides
import it.simonesestito.wallapp.MAX_CACHED_PALETTE_SIZE
import it.simonesestito.wallapp.backend.cache.PaletteCache
import javax.inject.Singleton


@Module
class CacheModule {
    @Provides
    @Singleton
    fun paletteCache() = PaletteCache(MAX_CACHED_PALETTE_SIZE)
}