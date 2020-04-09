/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.repository

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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

                val onChanged = { _: Any ->
                    doMappingAndSetResult(
                            firebaseCategories,
                            wallpapersCounts,
                            this
                    )
                }

                addSource(firebaseCategories, onChanged)
                addSource(wallpapersCounts, onChanged)
            }

    private fun doMappingAndSetResult(firebaseCategories: LiveData<List<FirebaseCategory>>,
                                      wallpapersCounts: LiveData<List<SeenWallpapersCount>>,
                                      resultLiveData: MutableLiveData<List<Category>>) {
        val countsMap = wallpapersCounts.value
                ?.map { it.categoryId to it.count }
                ?.toMap()
                ?: emptyMap()

        val categories = firebaseCategories.value?.map {
            val seen = countsMap[it.id] ?: it.wallpapersCount
            Category(data = it, unseenCount = it.wallpapersCount - seen)
        } ?: return

        resultLiveData.value = categories
    }

    fun loadCoverOn(category: Category, imageView: ImageView) {
        val shortAnim = imageView.resources.getInteger(android.R.integer.config_shortAnimTime)

        Glide
                .with(imageView)
                .load(category.data.previewImageUrl)
                .transition(DrawableTransitionOptions().crossFade(shortAnim))
                .into(imageView)
    }

    suspend fun markCategoryAsViewed(category: Category) =
            seenWallpapersCountDao.insertOrUpdate(SeenWallpapersCount(
                    category.data.id,
                    category.data.wallpapersCount
            ))
}