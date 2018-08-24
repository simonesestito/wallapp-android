/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.di.component

import dagger.Component
import it.simonesestito.wallapp.backend.service.PreviewService
import it.simonesestito.wallapp.di.module.*
import it.simonesestito.wallapp.ui.dialog.WallpaperPreviewBottomSheet
import it.simonesestito.wallapp.ui.dialog.WallpaperSetupBottomSheet
import it.simonesestito.wallapp.ui.fragment.CategoriesListFragment
import it.simonesestito.wallapp.ui.fragment.SingleCategoryFragment
import it.simonesestito.wallapp.ui.fragment.WallpaperFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ViewModelModule::class,
    FirebaseModule::class,
    CacheModule::class,
    InterfacesModule::class,
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