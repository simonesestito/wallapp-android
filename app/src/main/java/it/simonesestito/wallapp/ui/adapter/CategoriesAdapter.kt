package it.simonesestito.wallapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import it.simonesestito.wallapp.GlideApp
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.model.Category
import kotlinx.android.synthetic.main.categories_recycler_item.view.*


class CategoriesAdapter(private val context: Context) : RecyclerView.Adapter<CategoriesVH>() {
    private val data = mutableListOf<Category>()
    private var onItemClickListener: ((Category) -> Unit)? = null

    fun updateDataSet(newData: List<Category>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun onItemClick(listener: (Category) -> Unit) {
        this.onItemClickListener = listener
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CategoriesVH, position: Int) {
        val category = data[position]
        val ref = FirebaseStorage.getInstance()
                .getReference(category.coverUrl)

        holder.apply {
            setName(category.displayName)
            setDescription(category.description)
            setWallpapersCount(category.wallpapersCount)
            loadImage(ref)
        }
        holder.itemView.tag = position
        holder.itemView.setOnClickListener {
            val index = it.tag as Int
            onItemClickListener?.invoke(data[index])
        }
    }

    override fun onViewDetachedFromWindow(holder: CategoriesVH) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView
                .categoryItemCoverImage
                .setImageDrawable(context.getDrawable(R.drawable.ic_image_placeholder))
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

    fun loadImage(imageRef: StorageReference) {
        GlideApp
                .with(context)
                .load(imageRef)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(itemView.categoryItemCoverImage)
    }
}