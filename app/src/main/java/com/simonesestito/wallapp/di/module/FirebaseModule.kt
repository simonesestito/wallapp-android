/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.di.module

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides


@Module
class FirebaseModule {
    @Provides
    fun firestore() = FirebaseFirestore.getInstance()
}