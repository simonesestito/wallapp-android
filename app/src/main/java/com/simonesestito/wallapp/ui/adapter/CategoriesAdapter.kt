/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.repository.ICategoryRepository
import com.simonesestito.wallapp.utils.localized
import kotlinx.android.synthetic.main.categories_recycler_item.view.*
import javax.inject.Inject

/**
 * Adapter for categories list
 */
class CategoriesAdapter @Inject constructor(private val categoryRepository: ICategoryRepository)
    : AsyncAdapter<Category, CategoriesVH>() {
    var onItemClickListener: ((Category) -> Unit)? = null

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesVH {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.categories_recycler_item, parent, false)
        return CategoriesVH(view)
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
}