/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.repository

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.simonesestito.wallapp.backend.db.dao.SeenWallpapersCountDao
import com.simonesestito.wallapp.backend.db.entity.SeenWallpapersCount
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.model.FirebaseCategory
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val firebaseCategoryRepository: FirebaseCategoryRepository,
                                             private val seenWallpapersCountDao: SeenWallpapersCountDao) {
    fun getCategories() =
            MediatorLiveData<List<Category>>().apply {
                val firebaseCategories = firebaseCategoryRepository.getCategories()
                val wallpapersCounts = seenWallpapersCountDao.getAllCounts()

                addSource(firebaseCategories) { value = doCountsMapping(firebaseCategories, wallpapersCounts) }
                addSource(wallpapersCounts) { value = doCountsMapping(firebaseCategories, wallpapersCounts) }
            }

    private fun doCountsMapping(firebaseCategories: LiveData<List<FirebaseCategory>>,
                                wallpapersCounts: LiveData<List<SeenWallpapersCount>>): List<Category> {
        val countsMap = wallpapersCounts.value
                ?.map { it.categoryId to it.count }
                ?.toMap()
                ?: emptyMap()

        return firebaseCategories.value?.map {
            val seen = countsMap[it.id] ?: it.wallpapersCount
            Category(data = it, unseenCount = it.wallpapersCount - seen)
        } ?: emptyList()
    }

    fun loadCoverOn(category: Category, imageView: ImageView) =
            firebaseCategoryRepository.loadCoverOn(category.data, imageView)

    suspend fun markCategoryAsViewed(category: Category) =
            seenWallpapersCountDao.insertOrUpdate(SeenWallpapersCount(
                    category.data.id,
                    category.data.wallpapersCount
            ))
}