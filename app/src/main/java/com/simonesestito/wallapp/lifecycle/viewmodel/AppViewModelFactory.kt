/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Reusable
import javax.inject.Inject
import javax.inject.Provider

typealias ViewModelProviders = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>

@Reusable
class AppViewModelFactory @Inject constructor(private val viewModels: ViewModelProviders) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
            viewModels.getValue(modelClass).get() as T
}