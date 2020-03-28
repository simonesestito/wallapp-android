/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.repository

import android.widget.ImageView
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.simonesestito.wallapp.backend.db.dao.SeenWallpapersCountDao
import com.simonesestito.wallapp.backend.db.entity.SeenWallpapersCount
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.model.FirebaseCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val firebaseCategoryRepository: FirebaseCategoryRepository,
                                             private val seenWallpapersCountDao: SeenWallpapersCountDao) {
    var categoryCounts: List<SeenWallpapersCount>? = null

    fun getCategories() =
            MediatorLiveData<List<Category>>().apply {
                addSource(firebaseCategoryRepository.getCategories()) { list ->
                    if (categoryCounts == null) {
                        // If counts list is null, query the database
                        CoroutineScope(Dispatchers.IO).launch {
                            categoryCounts = seenWallpapersCountDao.getAllCounts()
                            doCountsMapping(list, this@apply)
                        }
                    } else {
                        doCountsMapping(list, this)
                    }
                }
            }

    private fun doCountsMapping(firebaseCategories: List<FirebaseCategory>,
                                liveData: MutableLiveData<List<Category>>) {
        val counts = categoryCounts ?: return

        // Transform the seen wallpapers count list to a Map for better efficiency
        val countsMap = counts.map { it.categoryId to it.count }.toMap()

        val categories = firebaseCategories.map {
            val seen = countsMap[it.id] ?: it.wallpapersCount
            Category(data = it, unseenCount = it.wallpapersCount - seen)
        }

        liveData.postValue(categories)
    }

    fun loadCoverOn(category: Category, imageView: ImageView) =
            firebaseCategoryRepository.loadCoverOn(category.data, imageView)

    suspend fun markCategoryAsViewed(category: Category) =
            seenWallpapersCountDao.insertOrUpdate(SeenWallpapersCount(
                    category.data.id,
                    category.data.wallpapersCount
            ))
}