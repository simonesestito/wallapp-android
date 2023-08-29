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

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.simonesestito.wallapp.backend.db.dao.SeenWallpapersCountDao
import com.simonesestito.wallapp.backend.db.entity.SeenWallpapersCount
import com.simonesestito.wallapp.backend.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CategoryRepository @Inject constructor(
    private val firebaseCategoryRepository: FirebaseCategoryRepository,
    private val seenWallpapersCountDao: SeenWallpapersCountDao
) {
    fun getCategories(): Flow<List<Category>?> {
        return try {
            seenWallpapersCountDao.getAllCounts()
                .map { counts -> counts.map { it.categoryId to it.count }.toMap() }
                .map { counts ->
                    firebaseCategoryRepository.getCategories().map {
                        val seen = counts[it.id] ?: it.wallpapersCount
                        Category(data = it, unseenCount = it.wallpapersCount - seen)
                    }
                }
        } catch (e: Exception) {
            flowOf(null)
        }
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
                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ) = false

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
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
        seenWallpapersCountDao.insertOrUpdate(
            SeenWallpapersCount(
                category.data.id,
                category.data.wallpapersCount
            )
        )
}