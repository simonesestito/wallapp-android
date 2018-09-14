/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.repository.impl

import android.widget.ImageView
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.simonesestito.wallapp.*
import com.simonesestito.wallapp.annotations.FORMAT_COVER
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.repository.ICategoryRepository
import com.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveCollection
import com.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveDocument
import com.simonesestito.wallapp.utils.map
import com.simonesestito.wallapp.utils.mapList
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val firestore: FirebaseFirestore,
                                             private val storage: FirebaseStorage) : ICategoryRepository {
    override fun getCategories(): LiveData<List<Category>> {
        val ref = firestore
                .collection(FIRESTORE_CATEGORIES)
                .whereEqualTo(KEY_PUBLISHED, true)
                .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)

        return FirestoreLiveCollection(ref).mapList { snap ->
            Category(snap)
        }
    }

    override fun getCategoryById(id: String): LiveData<Category> {
        val ref = firestore
                .document("$FIRESTORE_CATEGORIES/$id")

        return FirestoreLiveDocument(ref).map { snap ->
            Category(snap)
        }
    }

    override fun loadCoverOn(categoryId: String, imageView: ImageView) {
        val imageRef = storage.getReference("$STORAGE_CATEGORIES/$categoryId/$FORMAT_COVER")

        GlideApp
                .with(imageView)
                .load(imageRef)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(imageView)
    }
}