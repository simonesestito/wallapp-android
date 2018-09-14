/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.module

import com.simonesestito.wallapp.backend.repository.ICategoryRepository
import com.simonesestito.wallapp.backend.repository.IWallpaperRepository
import com.simonesestito.wallapp.backend.repository.impl.CategoryRepository
import com.simonesestito.wallapp.backend.repository.impl.WallpaperRepository
import dagger.Binds
import dagger.Module

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