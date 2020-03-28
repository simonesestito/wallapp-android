/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */
package com.simonesestito.wallapp.backend.repository

import android.widget.ImageView
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.simonesestito.wallapp.FIRESTORE_CATEGORIES
import com.simonesestito.wallapp.KEY_CREATION_DATE
import com.simonesestito.wallapp.KEY_PUBLISHED
import com.simonesestito.wallapp.backend.model.FirebaseCategory
import com.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveCollection
import com.simonesestito.wallapp.utils.mapList
import javax.inject.Inject

class FirebaseCategoryRepository @Inject constructor(private val firestore: FirebaseFirestore) {
    fun getCategories(): LiveData<List<FirebaseCategory>> {
        val ref = firestore
                .collection(FIRESTORE_CATEGORIES)
                .whereEqualTo(KEY_PUBLISHED, true)
                .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)

        return FirestoreLiveCollection(ref).mapList { snap ->
            FirebaseCategory(snap)
        }
    }

    fun loadCoverOn(category: FirebaseCategory, imageView: ImageView) {
        val shortAnim = imageView.resources.getInteger(android.R.integer.config_shortAnimTime)

        Glide
                .with(imageView)
                .load(category.previewImageUrl)
                .transition(DrawableTransitionOptions().crossFade(shortAnim))
                .into(imageView)
    }
}
