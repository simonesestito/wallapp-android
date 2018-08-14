package it.simonesestito.wallapp.dagger.module

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