package it.simonesestito.wallapp.dagger.component

import dagger.Component
import it.simonesestito.wallapp.dagger.module.CacheModule
import it.simonesestito.wallapp.dagger.module.FirebaseModule
import it.simonesestito.wallapp.dagger.module.ViewModelModule
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
    CacheModule::class
])
interface FragmentInjector {
    fun inject(categoriesListFragment: CategoriesListFragment)
    fun inject(wallpaperFragment: WallpaperFragment)
    fun inject(singleCategoryFragment: SingleCategoryFragment)
    fun inject(bottomSheet: WallpaperPreviewBottomSheet)
    fun inject(bottomSheet: WallpaperSetupBottomSheet)
}