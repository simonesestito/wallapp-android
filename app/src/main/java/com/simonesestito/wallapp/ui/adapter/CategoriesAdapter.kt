/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.repository.CategoryRepository
import com.simonesestito.wallapp.utils.TAG
import com.simonesestito.wallapp.utils.localized
import kotlinx.android.synthetic.main.categories_recycler_item.view.*
import javax.inject.Inject

/**
 * Adapter for categories list
 * @param categoryRepository Used to load category header image and to fetch unseen wallpapers.
 * List of categories is always given by the fragment using this Adapter
 */
class CategoriesAdapter @Inject constructor(private val categoryRepository: CategoryRepository)
    : AsyncAdapter<Category, CategoriesVH>() {
    var onItemClickListener: ((Category) -> Unit)? = null
    var lifecycleOwner: LifecycleOwner? = null

    /**
     * Store every unseen counter here
     * When a new value is obtained, put in cache here
     * When you need to read a value, always read here first
     */
    private val unseenCounterCache = mutableMapOf<String, Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesVH {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.categories_recycler_item, parent, false)
        return CategoriesVH(view)
    }

    override fun onBindViewHolder(holder: CategoriesVH, position: Int) {
        val category = data[position]

        holder.apply {
            nameView.text = category.displayName.localized
            descriptionView.text = category.description.localized
            setWallpapersCount(category.wallpapersCount)
            categoryRepository.loadCoverOn(category.id, coverView)
            itemView.setOnClickListener {
                onItemClickListener?.invoke(category)
            }
        }
        loadUnviewedWallsCount(holder, category, position)
    }

    private fun loadUnviewedWallsCount(holder: CategoriesVH, category: Category, position: Int) {
        // Check cache first.
        if (unseenCounterCache.containsKey(category.id)) {
            // Use cached value
            holder.setUnseenCount(unseenCounterCache[category.id] ?: 0L)
            return
        }

        if (lifecycleOwner == null) {
            Log.e(TAG, "No lifecycle owner found, unable to load walls count.")
            return
        }
        // Update cached value
        categoryRepository.getUnviewedCategoryWallpapers(category)
                .observe(lifecycleOwner!!, Observer { unseen ->
                    if (unseen != unseenCounterCache[category.id] ?: 0L) {
                        // Cache needs an update
                        unseenCounterCache[category.id] = unseen
                        notifyItemChanged(position)
                    }
                })
    }

}

class CategoriesVH(item: View) : RecyclerView.ViewHolder(item) {
    val nameView = itemView.categoryItemName!!
    val descriptionView = itemView.categoryItemDescription!!
    val coverView = itemView.categoryItemCoverImage!!

    fun setWallpapersCount(count: Long) {
        itemView.categoryItemWallpapersCount.text =
                itemView.context.getString(R.string.category_wallpapers_count_prefix, count)
    }

    fun setUnseenCount(count: Long) {
        if (count <= 0L) {
            itemView.unseenCount.visibility = View.INVISIBLE
        } else {
            itemView.unseenCount.apply {
                visibility = View.VISIBLE
                text = count.toString()
            }
        }
    }
}