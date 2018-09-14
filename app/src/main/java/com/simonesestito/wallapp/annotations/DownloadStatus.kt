/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.annotations

import androidx.annotation.IntDef


@Retention(AnnotationRetention.SOURCE)
@IntDef(
        STATUS_NOTHING,
        STATUS_INIT,
        STATUS_DOWNLOADING,
        STATUS_FINALIZING,
        STATUS_ERROR,
        STATUS_SUCCESS
)
annotation class DownloadStatus

/**
 * Initialization process
 */
const val STATUS_INIT = 0

/**
 * No download
 */
const val STATUS_NOTHING = 1

/**
 * Download in progress
 */
const val STATUS_DOWNLOADING = 2

/**
 * Download finished but doing some finalization logic
 */
const val STATUS_FINALIZING = 3

/**
 * Download finished and finalized successfully
 */
const val STATUS_SUCCESS = 4

/**
 * An error has encountered in download or finalization process
 */
const val STATUS_ERROR = 5
