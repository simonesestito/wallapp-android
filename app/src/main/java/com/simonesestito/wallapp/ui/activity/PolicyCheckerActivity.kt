/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.vending.licensing.AESObfuscator
import com.google.android.vending.licensing.LicenseChecker
import com.google.android.vending.licensing.LicenseCheckerCallback
import com.google.android.vending.licensing.ServerManagedPolicy
import com.simonesestito.wallapp.BuildConfig
import com.simonesestito.wallapp.LICENSING_PUBLIC_RSA_KEY
import com.simonesestito.wallapp.R


@SuppressLint("HardwareIds")
abstract class PolicyCheckerActivity : AppCompatActivity() {
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
        licenseChecker.checkAccess(AppLicenseCheckerCallback())
    }

    override fun onDestroy() {
        super.onDestroy()
        licenseChecker.onDestroy()
    }

    protected open fun showLicenseFailedDialog() {
        AlertDialog.Builder(this)
                .setMessage(R.string.policy_checker_failed_message)
                .setTitle(R.string.policy_checker_failed_title)
                .create()
                .apply {
                    setCancelable(false)
                }.show()
    }

    protected inner class AppLicenseCheckerCallback : LicenseCheckerCallback {

        override fun allow(reason: Int) {
            // Do nothing.
            Log.i("PolicyCheckerActivity", "License check successfully completed.")
        }

        override fun dontAllow(reason: Int) {
            Log.wtf("PolicyChecker", "License error 'dontAllow', pirated app.")
            showLicenseFailedDialog()
        }

        override fun applicationError(errorCode: Int) {
            Log.wtf("PolicyChecker", "License error with code $errorCode, pirated app.")
            showLicenseFailedDialog()
        }
    }
}