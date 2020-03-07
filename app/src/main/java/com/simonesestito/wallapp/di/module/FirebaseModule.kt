/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class FirebaseModule {
    @Provides
    @Singleton
    fun firestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun auth() = FirebaseAuth.getInstance()
}