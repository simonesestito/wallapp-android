/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.vending.licensing.*
import com.simonesestito.wallapp.BuildConfig
import com.simonesestito.wallapp.LICENSING_PUBLIC_RSA_KEY
import com.simonesestito.wallapp.R


@SuppressLint("HardwareIds")
abstract class LicenseCheckerActivity : AppCompatActivity() {
    private val licenseChecker by lazy {
        val salt = byteArrayOf(
                -46, 65, 30, -128, -103, -57, 74, -64, 51, 88,
                -95, -45, 77, -117, -36, -113, -11, 32, -64, 89
        )
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val obfuscator = AESObfuscator(salt, BuildConfig.APPLICATION_ID, androidId)
        LicenseChecker(
                this,
                ServerManagedPolicy(this, obfuscator),
                LICENSING_PUBLIC_RSA_KEY
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        licenseChecker.checkAccess {
            when (it) {
                Policy.NOT_LICENSED -> showLicenseFailedDialog()
                Policy.RETRY -> showOfflineErrorDialog()
                Policy.LICENSED -> Log.d("LicenseCheckerActivity", "Check successful!")
                LicenseCheckerCallback.ERROR_NOT_MARKET_MANAGED,
                LicenseCheckerCallback.ERROR_INVALID_PACKAGE_NAME,
                LicenseCheckerCallback.ERROR_INVALID_PUBLIC_KEY,
                LicenseCheckerCallback.ERROR_NON_MATCHING_UID,
                LicenseCheckerCallback.ERROR_MISSING_PERMISSION -> {
                    Log.wtf("LicenseCheckerActivity", "License check terminated with application error ${it}")
                    showLicenseAppErrorDialog(it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        licenseChecker.onDestroy()
    }

    //region Error dialogs
    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle(title)
                .apply {
                    setPositiveButton(R.string.policy_checker_dialog_button) { _, _ ->
                        System.exit(0) // Force close
                    }
                }
                .create()
                .apply {
                    setCancelable(false)
                }.show()
    }

    private fun showErrorDialog(@StringRes title: Int, @StringRes message: Int) =
            showErrorDialog(getString(title), getString(message))

    private fun showLicenseFailedDialog() = showErrorDialog(R.string.policy_checker_failed_title, R.string.policy_checker_failed_message)

    private fun showOfflineErrorDialog() = showErrorDialog(R.string.policy_checker_offline_title, R.string.policy_checker_offline_message)

    private fun showLicenseAppErrorDialog(error: Int) = showErrorDialog(
            getString(R.string.policy_checker_failed_title),
            getString(R.string.policy_checker_failed_message) + "\nERR_CODE: $error"
    )
    //endregion

    private inline fun LicenseChecker.checkAccess(crossinline callback: (Int) -> Unit) {
        this.checkAccess(object : LicenseCheckerCallback {
            override fun allow(reason: Int) = callback(reason)
            override fun dontAllow(reason: Int) = callback(reason)
            override fun applicationError(error: Int) = callback(error)
        })
    }
}