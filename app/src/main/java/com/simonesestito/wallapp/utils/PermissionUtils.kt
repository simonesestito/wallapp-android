/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.simonesestito.wallapp.R


/**
 * Ask for a system permission
 */
fun Fragment.requestPermissionsRationale(@StringRes message: Int, requestCode: Int, vararg permissions: String) {
    if (shouldShowRequestPermissionRationale(permissions)) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.permission_request_dialog_title)
            setMessage(message)
            setNegativeButton(R.string.permission_request_negative_button, null)
            setPositiveButton(R.string.permission_request_positive_button) { _, _ ->
                requestPermissions(permissions, requestCode)
            }
        }.show()
    } else {
        requestPermissions(permissions, requestCode)
    }
}

/**
 * Detect if at least 1 permission of the array needs an explanation dialog
 */
fun Fragment.shouldShowRequestPermissionRationale(permissions: Array<out String>) =
        permissions.any { shouldShowRequestPermissionRationale(it) }

/**
 * Check overlays permission
 */
fun Context.canDrawOverlays() =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)