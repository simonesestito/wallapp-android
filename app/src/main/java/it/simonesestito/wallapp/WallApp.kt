package it.simonesestito.wallapp

import android.app.Application
import it.simonesestito.wallapp.backend.cache.PaletteCache
import it.simonesestito.wallapp.utils.getOptimalCacheSize
import java.util.concurrent.Executors


class WallApp : Application() {
    val paletteCache by lazy { PaletteCache(getOptimalCacheSize()) }
    val ioThread = Executors.newFixedThreadPool(2)!!
}
