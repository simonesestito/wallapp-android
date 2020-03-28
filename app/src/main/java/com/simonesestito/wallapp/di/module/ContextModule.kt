/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.module

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val applicationContext: Context) {
    @Provides
    fun context() = applicationContext
}