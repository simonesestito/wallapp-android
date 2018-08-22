/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.di.module

import android.os.Handler
import android.os.Looper
import dagger.Module
import dagger.Provides
import it.simonesestito.wallapp.IO_EXECUTOR_MAX_THREADS
import it.simonesestito.wallapp.di.annotation.IoThread
import it.simonesestito.wallapp.di.annotation.MainHandler
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