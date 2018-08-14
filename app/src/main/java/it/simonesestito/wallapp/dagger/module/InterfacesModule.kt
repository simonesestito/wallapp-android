package it.simonesestito.wallapp.dagger.module

import dagger.Binds
import dagger.Module
import it.simonesestito.wallapp.backend.repository.ICategoryRepository
import it.simonesestito.wallapp.backend.repository.impl.CategoryRepository

/**
 * Module to define all binds between interfaces and implementation classes
 */
@Module
abstract class InterfacesModule {
    @Binds
    abstract fun bind(categoryRepository: CategoryRepository): ICategoryRepository
}