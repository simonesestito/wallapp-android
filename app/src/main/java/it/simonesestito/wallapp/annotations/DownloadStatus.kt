package it.simonesestito.wallapp.annotations

import androidx.annotation.IntDef


@Retention(AnnotationRetention.SOURCE)
@IntDef(
        STATUS_DOWNLOADING,
        STATUS_FINALIZING,
        STATUS_ERROR,
        STATUS_SUCCESS,
        STATUS_NOTHING
)
annotation class DownloadStatus

/**
 * No download
 */
const val STATUS_NOTHING = 0

/**
 * Download in progress
 */
const val STATUS_DOWNLOADING = 1

/**
 * Download finished but doing some finalization logic
 */
const val STATUS_FINALIZING = 2

/**
 * Download finished and finalized successfully
 */
const val STATUS_SUCCESS = 3

/**
 * An error has encountered in download or finalization process
 */
const val STATUS_ERROR = 4
