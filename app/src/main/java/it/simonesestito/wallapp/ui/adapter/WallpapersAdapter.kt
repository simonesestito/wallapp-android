package it.simonesestito.wallapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.annotations.FORMAT_PREVIEW
import it.simonesestito.wallapp.data.model.Wallpaper
import it.simonesestito.wallapp.utils.setFirebaseImage
import kotlinx.android.synthetic.main.wallpaper_item.view.*

class WallpapersAdapter : AsyncAdapter<Wallpaper, WallpapersVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpapersVH {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.wallpaper_item, parent, false)
        return WallpapersVH(view)
    }

    override fun onBindViewHolder(holder: WallpapersVH, position: Int) {
        val wallpaper = data[position]
        holder.apply {
            setImage(wallpaper.getStoragePath(FORMAT_PREVIEW))
        }
    }

}

class WallpapersVH(view: View) : RecyclerView.ViewHolder(view) {
    fun setImage(ref: String) {
        itemView.wallpaperImagePreview.setFirebaseImage(ref)
    }
}