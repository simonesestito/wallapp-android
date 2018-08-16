package it.simonesestito.wallapp.di.component

import dagger.Component
import it.simonesestito.wallapp.di.module.CacheModule
import it.simonesestito.wallapp.di.module.FirebaseModule
import it.simonesestito.wallapp.di.module.ViewModelModule
import it.simonesestito.wallapp.ui.dialog.WallpaperPreviewBottomSheet
import it.simonesestito.wallapp.ui.dialog.WallpaperSetupBottomSheet
import it.simonesestito.wallapp.ui.fragment.CategoriesListFragment
import it.simonesestito.wallapp.ui.fragment.SingleCategoryFragment
import it.simonesestito.wallapp.ui.fragment.WallpaperFragment

@Component(modules = [
    ViewModelModule::class,
    FirebaseModule::class,
    CacheModule::class
])
interface FragmentInjector {
    fun inject(categoriesListFragment: CategoriesListFragment)
    fun inject(wallpaperFragment: WallpaperFragment)
    fun inject(singleCategoryFragment: SingleCategoryFragment)
    fun inject(bottomSheet: WallpaperPreviewBottomSheet)
    fun inject(bottomSheet: WallpaperSetupBottomSheet)
}