/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.simonesestito.wallapp.backend.db.dao.SeenWallpapersCountDao
import com.simonesestito.wallapp.backend.db.entity.SeenWallpapersCount

@Database(entities = [SeenWallpapersCount::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun seenWallpapersCountDao(): SeenWallpapersCountDao
}