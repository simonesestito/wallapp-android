/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simonesestito.wallapp.backend.db.entity.SeenWallpapersCount

@Dao
interface SeenWallpapersCountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(seenWallpapersCount: SeenWallpapersCount)

    @Query("SELECT * FROM SeenWallpapersCount WHERE categoryId = :categoryId")
    suspend fun getCountByCategoryId(categoryId: String): SeenWallpapersCount?

    @Query("SELECT * FROM SeenWallpapersCount")
    suspend fun getAllCounts(): List<SeenWallpapersCount>
}