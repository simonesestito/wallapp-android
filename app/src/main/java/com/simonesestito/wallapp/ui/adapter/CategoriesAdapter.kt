/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.repository.CategoryRepository
import com.simonesestito.wallapp.utils.localized
import kotlinx.android.synthetic.main.categories_recycler_item.view.*
import javax.inject.Inject

/**
 * Adapter for categories list
 * @param categoryRepository Used to load category header image.
 * List of categories is always given by the fragment using this Adapter
 */
class CategoriesAdapter @Inject constructor(
        private val categoryRepository: CategoryRepository
) : AsyncAdapter<Category, CategoriesVH>() {
    var onItemClickListener: ((Category) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesVH {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.categories_recycler_item, parent, false)
        return CategoriesVH(view)
    }

    override fun onBindViewHolder(holder: CategoriesVH, position: Int) {
        val category = data[position]

        holder.apply {
            val resources = itemView.resources

            nameView.text = category.data.displayName.localized
            descriptionView.text = category.data.description.localized

            wallpapersCount.text = resources.getString(
                    R.string.category_wallpapers_count_prefix,
                    category.data.wallpapersCount)
            setUnseenCount(category.unseenCount)

            categoryRepository.loadCoverOn(category, coverView)

            itemView.setOnClickListener {
                onItemClickListener?.invoke(category)
            }
        }
    }
}

class CategoriesVH(item: View) : RecyclerView.ViewHolder(item) {
    val nameView = itemView.categoryItemName!!
    val descriptionView = itemView.categoryItemDescription!!
    val wallpapersCount = itemView.categoryItemWallpapersCount!!
    val coverView = itemView.categoryItemCoverImage!!

    fun setUnseenCount(count: Int) {
        if (count <= 0) {
            itemView.unseenCount.visibility = View.INVISIBLE
        } else {
            itemView.unseenCount.apply {
                visibility = View.VISIBLE
                text = count.toString()
            }
        }
    }
}