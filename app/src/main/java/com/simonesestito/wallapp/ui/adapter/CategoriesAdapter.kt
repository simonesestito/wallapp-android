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

package com.simonesestito.wallapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.cache.PaletteCache
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.backend.repository.CategoryRepository
import com.simonesestito.wallapp.databinding.CategoriesRecyclerItemBinding
import com.simonesestito.wallapp.ui.view.ColoredCardView
import com.simonesestito.wallapp.utils.localized
import com.simonesestito.wallapp.utils.suspendGenerate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Adapter for categories list
 * @param categoryRepository Used to load category header image.
 * List of categories is always given by the fragment using this Adapter
 */
class CategoriesAdapter @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val paletteCache: PaletteCache
) : AsyncAdapter<Category, CategoriesVH>() {
    var onItemClickListener: ((Category) -> Unit)? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.categories_recycler_item, parent, false)
        return CategoriesVH(view as ColoredCardView)
    }

    override fun onBindViewHolder(holder: CategoriesVH, position: Int) {
        val category = data[position]

        holder.apply {
            val resources = itemView.resources
            nameView.text = category.data.displayName.localized
            descriptionView.text = category.data.description.localized

            wallpapersCount.text = resources.getString(
                R.string.category_wallpapers_count_prefix,
                category.data.wallpapersCount
            )
            setUnseenCount(category.unseenCount)

            coroutineScope.launch {
                try {
                    val bitmap = categoryRepository.loadCover(category, holder.itemView)
                    val palette = paletteCache[category] ?: Palette.from(bitmap)
                        .suspendGenerate()
                        .also { paletteCache[category] = it }
                    holder.cardItem.updateCoverImage(bitmap, palette)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
//
            itemView.setOnClickListener {
                onItemClickListener?.invoke(category)
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Log.e("Adapter", "onDetachedFromRecyclerView")
        coroutineScope.cancel()
    }
}

class CategoriesVH(val cardItem: ColoredCardView) : RecyclerView.ViewHolder(cardItem) {
    private val viewBinding = CategoriesRecyclerItemBinding.bind(itemView)
    val nameView = viewBinding.categoryItemName
    val descriptionView = viewBinding.categoryItemDescription
    val wallpapersCount = viewBinding.categoryItemWallpapersCount

    fun setUnseenCount(count: Int) {
        if (count <= 0) {
            viewBinding.unseenCount.visibility = View.INVISIBLE
        } else {
            viewBinding.unseenCount.apply {
                visibility = View.VISIBLE
                text = count.toString()
            }
        }
    }
}