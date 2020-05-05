/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.backend.repository

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.simonesestito.wallapp.backend.db.dao.SeenWallpapersCountDao
import com.simonesestito.wallapp.backend.db.entity.SeenWallpapersCount
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.model.FirebaseCategory
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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

    /**
     * Load the category cover bitmap.
     *
     * It doesn't attach the Bitmap to the target View.
     * Instead, it uses the view to provide a lifecycle to Glide.
     *
     * As written in Glide docs, you shouldn't call [Bitmap.recycle] on loaded Bitmap
     *
     * @param category Category indicating which cover to load
     * @param view View used for Glide lifecycle
     * @return Loaded Bitmap
     */
    suspend fun loadCover(category: Category, view: View): Bitmap = suspendCoroutine { cont ->
        Glide
                .with(view)
                .asBitmap()
                .load(category.data.previewImageUrl)
                .addListener(object : RequestListener<Bitmap> {
                    override fun onResourceReady(resource: Bitmap, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean) = false
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        cont.resumeWithException(e!!)
                        return true
                    }
                })
                .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Log.e("Category Glide", "onResourceReady")
                        cont.resume(resource)
                    }
                })
    }

    suspend fun markCategoryAsViewed(category: Category) =
            seenWallpapersCountDao.insertOrUpdate(SeenWallpapersCount(
                    category.data.id,
                    category.data.wallpapersCount
            ))
}