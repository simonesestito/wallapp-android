/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.storage

class DownloadTask(private val task: (DownloadTask) -> Unit,
                   private val onCancelListener: () -> Unit) {
    var onProgressUpdate: ((Int) -> Unit)? = null
    var onError: ((Exception) -> Unit)? = null
    var onSuccess: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    var isStarted = false
        private set

    fun start(): DownloadTask {
        if (!isStarted) {
            isStarted = true
            task(this)
        }

        return this
    }

    fun cancel() = onCancelListener()
}
