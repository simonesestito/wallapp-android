package it.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.data.model.Wallpaper
import it.simonesestito.wallapp.ui.activity.MainActivity
import it.simonesestito.wallapp.utils.TAG
import it.simonesestito.wallapp.utils.getSuggestedWallpaperFormat
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
        val format = getSuggestedWallpaperFormat(resources.displayMetrics)
        Log.d(TAG, format)
        view.wallpaperImage.setFirebaseImage(currentWallpaper.getStoragePath(format))
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).supportActionBar?.hide()
        }
    }

    override fun onPause() {
        super.onPause()
        if (activity is MainActivity) {
            (activity as MainActivity).supportActionBar?.show()
        }
    }
}
