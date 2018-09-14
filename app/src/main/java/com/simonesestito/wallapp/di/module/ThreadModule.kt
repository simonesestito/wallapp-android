/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.module

import android.os.Handler
import android.os.Looper
import com.simonesestito.wallapp.IO_EXECUTOR_MAX_THREADS
import com.simonesestito.wallapp.di.annotation.IoThread
import com.simonesestito.wallapp.di.annotation.MainHandler
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class ThreadModule {
    @Provides
    @Singleton
    @MainHandler
    fun mainThread() = Handler(Looper.getMainLooper())

    @Provides
    @Singleton
    @IoThread
    fun ioThread() = Executors.newFixedThreadPool(IO_EXECUTOR_MAX_THREADS)!!
}