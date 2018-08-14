package it.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

typealias ViewModelProviders = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>

@Singleton
class AppViewModelFactory @Inject constructor(private val viewModels: ViewModelProviders) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
            viewModels[modelClass]!!.get() as T
}