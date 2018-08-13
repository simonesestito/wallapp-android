package it.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.lion4ik.arch.sharedelements.HasSharedElements
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.backend.model.Category
import it.simonesestito.wallapp.backend.model.Wallpaper
import it.simonesestito.wallapp.lifecycle.viewmodel.WallpaperViewModel
import it.simonesestito.wallapp.ui.adapter.WallpapersAdapter
import it.simonesestito.wallapp.utils.findNavController
import kotlinx.android.synthetic.main.single_category_fragment.*
import kotlinx.android.synthetic.main.single_category_fragment.view.*

class SingleCategoryFragment : AbstractAppFragment(), HasSharedElements {
    override val title
        get() = args.category.displayName

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(WallpaperViewModel::class.java)
    }
    private val args by lazy {
        SingleCategoryFragmentArgs.fromBundle(arguments)
    }
    private val adapter by lazy { WallpapersAdapter() }

    /**
     * Keep the current live data in memory
     * In case of necessity, we can remove any observer
     */
    private var oldLiveData: LiveData<List<Wallpaper>>? = null

    /**
     * Needed for SharedElements transaction
     */
    private val sharedElements = mutableMapOf<String, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.onItemClickListener = { wallpaper, transitionView ->
            // Setup SharedElements
            val name = ViewCompat.getTransitionName(transitionView) ?: ""
            sharedElements.clear()
            sharedElements[name] = transitionView

            val directions = SingleCategoryFragmentDirections
                    .toWallpaperDetails(wallpaper)
                    .setTransitionName(name)
            findNavController().navigate(directions)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.single_category_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup ScrollView params
        view.wallpapersRecyclerView.apply {
            setSlideOnFling(false)
            setSlideOnFlingThreshold(500)
            setItemTransformer(ScaleTransformer.Builder()
                    .setMaxScale(1.0f)
                    .setMinScale(0.85f)
                    .build())
        }
        populateView(args.category)
    }

    private fun populateView(category: Category) {
        // Set category description
        categoryDescription.text = category.description

        wallpapersRecyclerView.adapter = this.adapter

        // If there was an old LiveData, unregister it
        oldLiveData?.removeObservers(this)

        // Get wallpapers list from Firebase using LiveData,
        // updating the oldLiveData so we'll be able to dismiss it later
        oldLiveData = viewModel.getWallpapersByCategoryId(category.id)

        // Finally, observe for updates
        oldLiveData?.observe(this, Observer { walls ->
            // On wallpapers update, run DiffUtil to refresh the list
            this.adapter.updateDataSet(walls)
        })
    }

    override fun hasReorderingAllowed() = true
    override fun getSharedElements() = sharedElements
}
