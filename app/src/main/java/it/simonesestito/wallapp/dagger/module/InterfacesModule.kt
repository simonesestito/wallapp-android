package it.simonesestito.wallapp.dagger.module

import dagger.Binds
import dagger.Module
import it.simonesestito.wallapp.backend.repository.ICategoryRepository
import it.simonesestito.wallapp.backend.repository.IWallpaperRepository
import it.simonesestito.wallapp.backend.repository.impl.CategoryRepository
import it.simonesestito.wallapp.backend.repository.impl.WallpaperRepository

/**
 * Module to define all binds between interfaces and implementation classes
 */
@Module
abstract class InterfacesModule {
    @Binds
    abstract fun bindCategoryRepo(categoryRepository: CategoryRepository): ICategoryRepository

    @Binds
    abstract fun bindWallpaperRepo(wallpaperRepository: WallpaperRepository): IWallpaperRepository
}