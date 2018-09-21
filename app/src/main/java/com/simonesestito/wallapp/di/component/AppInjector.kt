/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.component

import com.simonesestito.wallapp.backend.service.PreviewService
import com.simonesestito.wallapp.di.module.CacheModule
import com.simonesestito.wallapp.di.module.FirebaseModule
import com.simonesestito.wallapp.di.module.ThreadModule
import com.simonesestito.wallapp.di.module.ViewModelModule
import com.simonesestito.wallapp.ui.dialog.WallpaperPreviewBottomSheet
import com.simonesestito.wallapp.ui.dialog.WallpaperSetupBottomSheet
import com.simonesestito.wallapp.ui.fragment.CategoriesListFragment
import com.simonesestito.wallapp.ui.fragment.SingleCategoryFragment
import com.simonesestito.wallapp.ui.fragment.WallpaperFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ViewModelModule::class,
    FirebaseModule::class,
    CacheModule::class,
    ThreadModule::class
])
interface AppInjector {
    companion object {
        private var injector: AppInjector? = null
        fun getInstance(): AppInjector {
            if (injector == null) {
                injector = DaggerAppInjector.create()
            }
            return injector!!
        }
    }

    fun inject(categoriesListFragment: CategoriesListFragment)
    fun inject(wallpaperFragment: WallpaperFragment)
    fun inject(singleCategoryFragment: SingleCategoryFragment)
    fun inject(bottomSheet: WallpaperPreviewBottomSheet)
    fun inject(bottomSheet: WallpaperSetupBottomSheet)
    fun inject(previewService: PreviewService)
}