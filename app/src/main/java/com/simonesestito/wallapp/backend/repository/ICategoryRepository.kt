/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.repository

import android.widget.ImageView
import androidx.lifecycle.LiveData
import com.simonesestito.wallapp.backend.model.Category


interface ICategoryRepository {
    /**
     * List all the available categories from Firestore
     * @return LiveData observing the categories list
     */
    fun getCategories(): LiveData<List<Category>>

    /**
     * Get the category document from Firestore
     * @param id Category ID
     * @return LiveData of the Firestore document
     */
    fun getCategoryById(id: String): LiveData<Category>

    /**
     * Load cover image from Firebase Storage in a target [ImageView] asynchronously
     * @param categoryId Category ID
     * @param imageView Target ImageView
     */
    fun loadCoverOn(categoryId: String, imageView: ImageView)
}