/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package it.simonesestito.wallapp.utils

import android.os.Handler
import dagger.Reusable
import it.simonesestito.wallapp.di.annotation.IoThread
import it.simonesestito.wallapp.di.annotation.MainHandler
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@Reusable
class ThreadUtils @Inject constructor(@MainHandler private val mainHandler: Handler,
                                      @IoThread private val ioThread: ExecutorService) {
    fun runOnMainThread(action: () -> Unit) {
        mainHandler.post(action)
    }

    fun runOnIoThread(action: () -> Unit) {
        ioThread.execute(action)
    }
}