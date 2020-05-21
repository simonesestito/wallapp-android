/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
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
