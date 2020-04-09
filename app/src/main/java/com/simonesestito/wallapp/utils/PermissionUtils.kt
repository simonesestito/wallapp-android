/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.simonesestito.wallapp.ui.dialog.PermissionsDialog


/**
 * Ask for a system permission.
 *
 * In case an additional explanation has to be shown,
 * a dialog with a custom message will be shown.
 *
 * Target fragment needs to override [Fragment.onActivityResult] to detect user action
 */
fun Fragment.requestPermissionsRationale(@StringRes message: Int, requestCode: Int, vararg permissions: String) {
    if (shouldShowRequestPermissionRationale(permissions)) {
        val dialog = PermissionsDialog.createDialog(getString(message))
        dialog.show(parentFragmentManager, null)
        dialog.setTargetFragment(this, requestCode)
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

/**
 * Check if a set of permissions is granted
 */
fun Context.checkSelfPermissions(vararg permissions: String) =
        permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }