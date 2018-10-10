/*
 * This file is part of WallApp for Android.
 * Copyright © 2018 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Wallpaper
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpapersViewModel
import com.simonesestito.wallapp.ui.adapter.WallpapersAdapter
import com.simonesestito.wallapp.utils.TAG
import com.simonesestito.wallapp.utils.findNavController
import com.simonesestito.wallapp.utils.getViewModel
import com.simonesestito.wallapp.utils.localized
import kotlinx.android.synthetic.main.single_category_fragment.*
import kotlinx.android.synthetic.main.single_category_fragment.view.*
import javax.inject.Inject

private const val KEY_LAYOUT_ROW_COUNT = "layout_row_count"

class SingleCategoryFragment : AbstractAppFragment(), SharedElementsStart {
    override val title
        get() = args.category.displayName.localized

    private val viewModel by lazy {
        getViewModel<WallpapersViewModel>(viewModelFactory)
    }
    private val args by lazy {
        SingleCategoryFragmentArgs.fromBundle(arguments)
    }

    /**
     * Keep the current live data in memory
     * In case of necessity, we can remove any observer
     */
    private var oldLiveData: LiveData<List<Wallpaper>>? = null

    /**
     * Needed for SharedElements transaction
     */
    private val sharedElements = mutableMapOf<String, View>()

    @Inject
    lateinit var adapter: WallpapersAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var currentLayoutSpanCount: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)

        adapter.onItemClickListener = { wallpaper, transitionView ->
            // Setup SharedElements
            val name = ViewCompat.getTransitionName(transitionView) ?: ""
            sharedElements.clear()
            sharedElements[name] = transitionView

            val directions = SingleCategoryFragmentDirections
                    .toWallpaperDetails(wallpaper.id, wallpaper.categoryId)
                    .setTransitionName(name)
            findNavController().navigate(directions)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.single_category_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.categoryLoadingSpinner.show()

        // Update current span count if it's present in savedInstanceState Bundle
        savedInstanceState?.getInt(KEY_LAYOUT_ROW_COUNT, currentLayoutSpanCount)?.let {
            currentLayoutSpanCount = it
        }

        // Update seen wallpapers count
        viewModel.updateSeenWallpapers(args.category)

        // Set category description
        categoryDescription.text = args.category.description.localized

        wallpapersRecyclerView.adapter = this.adapter
        wallpapersRecyclerView.layoutManager = GridLayoutManager(requireContext(), currentLayoutSpanCount, LinearLayoutManager.HORIZONTAL, false)
        adjustRecyclerViewState()

        // If there was an old LiveData, unregister it
        oldLiveData?.removeObservers(this)

        // Get wallpapers list from Firebase using LiveData,
        // updating the oldLiveData so we'll be able to dismiss it later
        oldLiveData = viewModel.getWallpapersByCategoryId(args.category.id)

        // Finally, observe for updates
        oldLiveData?.observe(this, Observer { walls ->
            val oldData = this.adapter.data

            // On wallpapers update, refresh the list
            this.adapter.updateDataSet(walls)

            Handler(Looper.getMainLooper()).postDelayed(500) {
                if (isDetached) return@postDelayed

                // Try adding a callback on async list differ
                // After a delay, if the list is not empty, scroll to 0
                if (adapter.data.isNotEmpty() &&
                        oldData.size < adapter.data.size &&
                        oldData != adapter.data) {
                    wallpapersRecyclerView?.smoothScrollToPosition(0)
                }
            }

            // Update content loading spinner
            categoryLoadingSpinner?.hide() // Loaded

            // If new list is empty, show empty view
            if (walls.isEmpty()) {
                singleCategoryEmptyView.visibility = View.VISIBLE
            } else {
                singleCategoryEmptyView.visibility = View.GONE
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_LAYOUT_ROW_COUNT, currentLayoutSpanCount)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.single_category_fragment_menu, menu)
        menu?.findItem(R.id.singleCategoryLayoutSwitch)?.setIcon(
                if (currentLayoutSpanCount == 1) R.drawable.ic_grid_large_black_24dp
                else R.drawable.ic_category_layout_single_row_scroll
        )
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.singleCategoryLayoutSwitch -> {
                (wallpapersRecyclerView?.layoutManager as GridLayoutManager?)?.let {
                    changeLayoutRowCount(if (it.spanCount == 1) 2 else 1)
                }
                activity?.invalidateOptionsMenu()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Change layout row count with fade animation
     */
    private fun changeLayoutRowCount(spanCount: Int) {
        currentLayoutSpanCount = spanCount
        val fadeOut = AlphaAnimation(1f, 0f)
        val shortDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        fadeOut.interpolator = DecelerateInterpolator()
        fadeOut.duration = shortDuration
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                (wallpapersRecyclerView.layoutManager as GridLayoutManager).also {
                    it.spanCount = spanCount
                    it.requestLayout()
                }

                adjustRecyclerViewState()

                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.interpolator = AccelerateInterpolator()
                fadeIn.duration = shortDuration
                wallpapersRecyclerView.startAnimation(fadeIn)
            }
        })
        wallpapersRecyclerView.startAnimation(fadeOut)
    }

    /**
     * Set correct padding and SnapHelper if necessary
     * Useful when restoring state
     */
    private fun adjustRecyclerViewState() {
        val layoutManager = wallpapersRecyclerView?.layoutManager as GridLayoutManager?
        val spanCount = layoutManager?.spanCount ?: currentLayoutSpanCount

        if (layoutManager == null) {
            Log.e(this@SingleCategoryFragment.TAG, "adjustRecyclerViewState(): layoutManager is null")
            return
        }

        // Post to the next tick to wait until layout request has finished
        Handler().post {
            wallpapersRecyclerView.snapEnabled = spanCount == 1
        }
    }

    override fun getSharedElements() = sharedElements
}
