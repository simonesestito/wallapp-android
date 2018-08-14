package it.simonesestito.wallapp.dagger.module

import dagger.Module
import dagger.Provides
import dagger.Reusable
import it.simonesestito.wallapp.MAX_CACHED_PALETTE_SIZE
import it.simonesestito.wallapp.backend.cache.PaletteCache


@Module
class CacheModule {
    @Provides
    @Reusable
    fun paletteCache() = PaletteCache(MAX_CACHED_PALETTE_SIZE)
}