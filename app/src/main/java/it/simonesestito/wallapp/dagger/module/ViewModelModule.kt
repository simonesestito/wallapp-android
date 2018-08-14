package it.simonesestito.wallapp.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.simonesestito.wallapp.dagger.annotation.ViewModelMapKey
import it.simonesestito.wallapp.lifecycle.viewmodel.AppViewModelFactory
import it.simonesestito.wallapp.lifecycle.viewmodel.CategoryViewModel
import it.simonesestito.wallapp.lifecycle.viewmodel.WallpaperSetupViewModel
import it.simonesestito.wallapp.lifecycle.viewmodel.WallpaperViewModel


@Module(includes = [ThreadModule::class])
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelMapKey(CategoryViewModel::class)
    internal abstract fun categoryViewModel(viewModel: CategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelMapKey(WallpaperViewModel::class)
    internal abstract fun wallpaperViewModel(viewModel: WallpaperViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelMapKey(WallpaperSetupViewModel::class)
    internal abstract fun wallpaperSetupViewModel(viewModel: WallpaperSetupViewModel): ViewModel
}