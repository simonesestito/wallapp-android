/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */
package com.simonesestito.wallapp.di.module

import android.content.Context
import androidx.room.Room
import com.simonesestito.wallapp.ROOM_DATABASE_NAME
import com.simonesestito.wallapp.backend.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun appDatabase(applicationContext: Context): AppDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            ROOM_DATABASE_NAME
    ).build()

    @Provides
    fun seenWallpapersCountDao(appDatabase: AppDatabase) = appDatabase.seenWallpapersCountDao()
}