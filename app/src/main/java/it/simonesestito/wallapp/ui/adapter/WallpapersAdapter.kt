package it.simonesestito.wallapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.annotations.FORMAT_PREVIEW
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.backend.service.WallpaperService
import kotlinx.android.synthetic.main.wallpaper_item.view.*

/**
 * Wallpapers Adapter (format PREVIEW)
 */
class WallpapersAdapter : AsyncAdapter<Wallpaper, WallpapersVH>() {
    var onItemClickListener: WallpaperClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpapersVH {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.wallpaper_item, parent, false)
        return WallpapersVH(view)
    }

    override fun onBindViewHolder(holder: WallpapersVH, position: Int) {
        val wallpaper = data[position]
        holder.apply {
            WallpaperService.loadWallpaper(
                    wallpaper,
                    FORMAT_PREVIEW,
                    holder.wallpaperView
            )
            ViewCompat.setTransitionName(itemView, wallpaper.id)
            itemView.setOnClickListener {
                onItemClickListener?.invoke(wallpaper, it)
            }
        }
    }

}

class WallpapersVH(view: View) : RecyclerView.ViewHolder(view) {
    val wallpaperView = itemView.wallpaperImagePreview!!
}

typealias WallpaperClickListener = (Wallpaper, View) -> Unit