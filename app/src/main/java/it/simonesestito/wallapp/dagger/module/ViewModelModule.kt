package it.simonesestito.wallapp.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.simonesestito.wallapp.dagger.annotation.ViewModelMapKey
import it.simonesestito.wallapp.lifecycle.viewmodel.AppViewModelFactory
import it.simonesestito.wallapp.lifecycle.viewmodel.CategoryViewModel


@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelMapKey(CategoryViewModel::class)
    internal abstract fun postListViewModel(viewModel: CategoryViewModel): ViewModel

    //Add more ViewModels here
}