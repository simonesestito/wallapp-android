/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simonesestito.wallapp.di.annotation.ViewModelMapKey
import com.simonesestito.wallapp.lifecycle.viewmodel.AppViewModelFactory
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpaperSetupViewModel
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpapersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelMapKey(WallpapersViewModel::class)
    internal abstract fun categoryViewModel(viewModel: WallpapersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelMapKey(WallpaperSetupViewModel::class)
    internal abstract fun wallpaperSetupViewModel(viewModel: WallpaperSetupViewModel): ViewModel
}