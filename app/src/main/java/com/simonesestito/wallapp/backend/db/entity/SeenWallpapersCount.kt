/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SeenWallpapersCount(
        @PrimaryKey val categoryId: String,
        val count: Int = 0
)