/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.lifecycle

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.simonesestito.wallapp.utils.TAG


class LifecycleExecutor(private inline val action: () -> Unit) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun executeAction(owner: LifecycleOwner) {
        Log.d(TAG, "LifecycleOwner ${owner.javaClass.simpleName} is ready: performing scheduled action")
        owner.lifecycle.removeObserver(this)
        action()
    }
}