package it.simonesestito.wallapp.dagger.component

import dagger.Component
import it.simonesestito.wallapp.dagger.module.FirebaseModule
import it.simonesestito.wallapp.dagger.module.ViewModelModule
import it.simonesestito.wallapp.ui.fragment.CategoriesListFragment
import it.simonesestito.wallapp.ui.fragment.SingleCategoryFragment
import it.simonesestito.wallapp.ui.fragment.WallpaperFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class, FirebaseModule::class])
interface FragmentInjector {
    fun inject(categoriesListFragment: CategoriesListFragment)
    fun inject(wallpaperFragment: WallpaperFragment)
    fun inject(singleCategoryFragment: SingleCategoryFragment)
}