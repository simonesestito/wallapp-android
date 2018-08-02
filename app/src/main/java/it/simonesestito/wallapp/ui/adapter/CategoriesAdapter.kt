package it.simonesestito.wallapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.data.model.Category
import it.simonesestito.wallapp.utils.setFirebaseImage
import kotlinx.android.synthetic.main.categories_recycler_item.view.*


class CategoriesAdapter(private val context: Context) : AsyncAdapter<Category, CategoriesVH>() {
    private var onItemClickListener: ((Category) -> Unit)? = null

    fun onItemClick(listener: (Category) -> Unit) {
        this.onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: CategoriesVH, position: Int) {
        val category = data[position]

        holder.apply {
            setName(category.displayName)
            setDescription(category.description)
            setWallpapersCount(category.wallpapersCount)
            setImage(category.coverUrl)
            itemView.setOnClickListener {
                onItemClickListener?.invoke(data[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesVH {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.categories_recycler_item, parent, false)
        return CategoriesVH(view, context)
    }

}

class CategoriesVH(item: View, private val context: Context) : RecyclerView.ViewHolder(item) {
    fun setName(name: String) {
        itemView.categoryItemName.text = name
    }

    fun setDescription(description: String) {
        itemView.categoryItemDescription.text = description
    }

    fun setWallpapersCount(count: Long) {
        itemView.categoryItemWallpapersCount.text =
                context.getString(R.string.category_wallpapers_count_prefix, count)
    }

    fun setImage(ref: String) {
        itemView.categoryItemCoverImage.setFirebaseImage(ref)
    }
}