/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.repository

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.simonesestito.wallapp.*
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.enums.FORMAT_COVER
import com.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveCollection
import com.simonesestito.wallapp.lifecycle.livedata.FirestoreLiveDocument
import com.simonesestito.wallapp.utils.TAG
import com.simonesestito.wallapp.utils.map
import com.simonesestito.wallapp.utils.mapList
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val firestore: FirebaseFirestore,
                                             private val storage: FirebaseStorage,
                                             private val auth: FirebaseAuth) {
    fun getCategories(): LiveData<List<Category>> {
        val ref = firestore
                .collection(FIRESTORE_CATEGORIES)
                .whereEqualTo(KEY_PUBLISHED, true)
                .orderBy(KEY_CREATION_DATE, Query.Direction.DESCENDING)

        return FirestoreLiveCollection(ref).mapList { snap ->
            Category(snap)
        }
    }

    fun getCategoryById(id: String): LiveData<Category> {
        val ref = firestore
                .document("$FIRESTORE_CATEGORIES/$id")

        return FirestoreLiveDocument(ref).map { snap ->
            Category(snap)
        }
    }

    fun loadCoverOn(categoryId: String, imageView: ImageView) {
        val imageRef = storage.getReference("$STORAGE_CATEGORIES/$categoryId/$FORMAT_COVER")
        val shortAnim = imageView.resources.getInteger(android.R.integer.config_shortAnimTime)

        GlideApp
                .with(imageView)
                .load(imageRef)
                .transition(DrawableTransitionOptions().crossFade(shortAnim))
                .into(imageView)
    }

    fun markCategoryAsViewed(category: Category) {
        val currentUser = auth.currentUser
        currentUser ?: return

        firestore.document("users/${currentUser.uid}/categories/${category.id}")
                .set(mapOf(
                        FIRESTORE_USED_VIEWED_WALLS_COUNT to category.wallpapersCount
                ), SetOptions.merge())
                .addOnCompleteListener {
                    Log.d(TAG, "User wallpapers count updated, success: ${it.isSuccessful}")
                    if (!it.isSuccessful) {
                        it.exception?.printStackTrace()
                    }
                }
    }

    fun getUnviewedCategoryWallpapers(category: Category): LiveData<Long> {
        val currentUser = auth.currentUser
        currentUser ?: return MutableLiveData<Long>().also {
            it.postValue(0L)
        }

        val ref = firestore
                .document("users/${currentUser.uid}/categories/${category.id}")

        return FirestoreLiveDocument(ref).map { snap ->
            val viewed = snap.getLong(FIRESTORE_USED_VIEWED_WALLS_COUNT) ?: 0L
            val unseen = category.wallpapersCount - viewed
            return@map if (viewed > 0 && unseen > 0) {
                // Unseen value is valid
                unseen
            } else {
                0L // Fallback value
            }
        }
    }
}