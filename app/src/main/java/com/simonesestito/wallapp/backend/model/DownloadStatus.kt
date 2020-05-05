/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.model

sealed class DownloadStatus {
    data class Progressing(val progress: Int) : DownloadStatus()
    object Finalizing : DownloadStatus()
    object Error : DownloadStatus()
    object Success : DownloadStatus()
}