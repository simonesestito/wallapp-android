/*
 * Copyright 2020 Simone Sestito
 * This file is part of WallApp.
 *
 * WallApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WallApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WallApp.  If not, see <http://www.gnu.org/licenses/>.
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