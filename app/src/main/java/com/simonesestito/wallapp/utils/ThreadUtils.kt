/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.utils

import android.os.Handler
import com.simonesestito.wallapp.di.annotation.IoThread
import com.simonesestito.wallapp.di.annotation.MainHandler
import dagger.Reusable
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