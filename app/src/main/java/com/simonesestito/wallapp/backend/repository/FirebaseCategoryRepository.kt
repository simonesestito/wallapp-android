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
package com.simonesestito.wallapp.backend.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.simonesestito.wallapp.FIRESTORE_CATEGORIES
import com.simonesestito.wallapp.KEY_CREATION_DATE
import com.simonesestito.wallapp.KEY_PUBLISHED
import com.simonesestito.wallapp.backend.model.FirebaseCategory
import com.simonesestito.wallapp.utils.toSuspendQuery
import javax.inject.Inject

class FirebaseCategoryRepository @Inject constructor(private val firestore: FirebaseFirestore) {
    suspend fun getCategories() = firestore
        .collection(FIRESTORE_CATEGORIES)
        .whereEqualTo(KEY_PUBLISHED, true)
        .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)
        .toSuspendQuery()
        .map { FirebaseCategory(it) }
}
