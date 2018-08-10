package it.simonesestito.wallapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.backend.model.Category
import it.simonesestito.wallapp.backend.repository.CategoryRepository
import kotlinx.android.synthetic.main.categories_recycler_item.view.*

/**
 * Adapter for categories list
 */
class CategoriesAdapter : AsyncAdapter<Category, CategoriesVH>() {
    var onItemClickListener: ((Category) -> Unit)? = null

    override fun onBindViewHolder(holder: CategoriesVH, position: Int) {
        val category = data[position]

        holder.apply {
            nameView.text = category.displayName
            descriptionView.text = category.description
            setWallpapersCount(category.wallpapersCount)
            CategoryRepository.loadCoverOn(category.id, coverView)
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