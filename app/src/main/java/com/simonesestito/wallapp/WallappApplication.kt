/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp

import android.app.Application
import android.content.Context

class WallappApplication : Application() {
    companion object {
        lateinit var INSTANCE: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this.applicationContext
    }
}