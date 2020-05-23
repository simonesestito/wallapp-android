/*
 * Copyright 2020 Simone Sestito
 * This file is part of WallApp.
 *
 * WallApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WallApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WallApp.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.simonesestito.wallapp.di.component

import com.simonesestito.wallapp.WallappApplication
import com.simonesestito.wallapp.backend.androidservice.PreviewService
import com.simonesestito.wallapp.di.module.*
import com.simonesestito.wallapp.ui.dialog.AbstractWallpaperBottomSheet
import com.simonesestito.wallapp.ui.fragment.ChildCategoriesFragment
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

    fun inject(categoriesFragment: ChildCategoriesFragment)
    fun inject(wallpaperFragment: WallpaperFragment)
    fun inject(singleCategoryFragment: SingleCategoryFragment)
    fun inject(bottomSheet: AbstractWallpaperBottomSheet)
    fun inject(previewService: PreviewService)
}