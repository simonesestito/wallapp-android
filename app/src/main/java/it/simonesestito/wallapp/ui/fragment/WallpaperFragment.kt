package it.simonesestito.wallapp.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.annotations.FORMAT_PREVIEW
import it.simonesestito.wallapp.data.model.Wallpaper
import it.simonesestito.wallapp.ui.activity.MainActivity
import it.simonesestito.wallapp.utils.setFirebaseImage
import kotlinx.android.synthetic.main.wallpaper_fragment.view.*

class WallpaperFragment : Fragment() {
    private val currentWallpaper: Wallpaper by lazy {
        val args = WallpaperFragmentArgs.fromBundle(arguments)
        return@lazy Wallpaper(args.wallpaperId, args.categoryId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.wallpaper_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: change format to display here
        view.wallpaperImage.setFirebaseImage(currentWallpaper.getStoragePath(FORMAT_PREVIEW))
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).supportActionBar?.hide()
            activity?.window?.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun onPause() {
        super.onPause()
        if (activity is MainActivity) {
            (activity as MainActivity).supportActionBar?.show()
            // TODO: reset status bar color
        }
    }
}
