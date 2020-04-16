/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.component

import com.simonesestito.wallapp.WallappApplication
import com.simonesestito.wallapp.backend.androidservice.PreviewService
import com.simonesestito.wallapp.di.module.*
import com.simonesestito.wallapp.ui.dialog.AbstractWallpaperBottomSheet
import com.simonesestito.wallapp.ui.fragment.CategoriesFragment
import com.simonesestito.wallapp.ui.fragment.SingleCategoryFragment
import com.simonesestito.wallapp.ui.fragment.WallpaperFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ViewModelModule::class,
    FirebaseModule::class,
    CacheModule::class,
    ThreadModule::class,
    DatabaseModule::class,
    ContextModule::class,
    StorageModule::class
])
interface AppInjector {
    companion object {
        private var injector: AppInjector? = null
        fun getInstance(): AppInjector {
            if (injector == null) {
                injector = DaggerAppInjector.builder()
                        .contextModule(ContextModule(WallappApplication.INSTANCE))
                        .build()
            }
            return injector!!
        }
    }

    fun inject(categoriesFragment: CategoriesFragment)
    fun inject(wallpaperFragment: WallpaperFragment)
    fun inject(singleCategoryFragment: SingleCategoryFragment)
    fun inject(bottomSheet: AbstractWallpaperBottomSheet)
    fun inject(previewService: PreviewService)
}