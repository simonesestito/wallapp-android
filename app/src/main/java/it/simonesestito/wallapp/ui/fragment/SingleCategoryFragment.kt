package it.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.data.model.Category
import it.simonesestito.wallapp.data.model.Wallpaper
import it.simonesestito.wallapp.ui.adapter.WallpapersAdapter
import it.simonesestito.wallapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.single_category_fragment.*
import kotlinx.android.synthetic.main.single_category_fragment.view.*

class SingleCategoryFragment : Fragment() {
    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }
    private val args: SingleCategoryFragmentArgs by lazy {
        SingleCategoryFragmentArgs.fromBundle(arguments)
    }
    private val adapter: WallpapersAdapter by lazy { WallpapersAdapter() }

    // Keep the current live data in memory
    // So in case of necessity, we can remove any observer
    private var oldLiveData: LiveData<List<Wallpaper>>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        // Inflate the layout for this fragment
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
    }

    override fun onResume() {
        super.onResume()
        adapter.onItemClickListener = { wallpaper ->
            val directions = SingleCategoryFragmentDirections
                    .toWallpaperDetails(wallpaper.categoryId, wallpaper.id)
            NavHostFragment.findNavController(this)
                    .navigate(directions)
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.onItemClickListener = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getCategoryById(args.categoryId).observe(this, Observer { category ->
            activity?.title = category.displayName
            populateView(category)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = args.categoryTitle
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
}
