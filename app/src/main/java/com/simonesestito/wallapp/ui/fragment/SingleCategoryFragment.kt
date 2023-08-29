/*
 * Copyright 2020 Simone Sestito
 * This file is part of WallApp.
 *
 * WallApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WallApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WallApp.  If not, see <http://www.gnu.org/licenses/>.
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
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.simonesestito.wallapp.PREFS_SINGLE_CATEGORY_LAYOUT_ROWS
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.databinding.SingleCategoryFragmentBinding
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpapersViewModel
import com.simonesestito.wallapp.ui.adapter.WallpapersAdapter
import com.simonesestito.wallapp.utils.TAG
import com.simonesestito.wallapp.utils.findNavController
import com.simonesestito.wallapp.utils.localized
import com.simonesestito.wallapp.utils.sharedPreferences
import javax.inject.Inject

private const val KEY_LAYOUT_ROW_COUNT = "layout_row_count"

class SingleCategoryFragment : SharedElementsDestination() {
    private val viewModel: WallpapersViewModel by viewModels { viewModelFactory }

    private val args by lazy {
        SingleCategoryFragmentArgs.fromBundle(arguments ?: bundleOf())
    }

    @Inject
    lateinit var adapter: WallpapersAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var currentLayoutSpanCount: Int = 1

    private lateinit var viewBinding: SingleCategoryFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)

        adapter.onItemClickListener = { wallpaper, itemView ->
            // Setup SharedElements
            val directions = SingleCategoryFragmentDirections
                .toWallpaperDetails(wallpaper.id, wallpaper.categoryId)

            findNavController().navigate(
                directions, FragmentNavigator.Extras
                    .Builder()
                    .addSharedElement(itemView, itemView.transitionName)
                    .build()
            )
        }

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().title = args.category.data.displayName.localized
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Requires this fragment to call startPostponedEnterTransition manually.
        // Field changed here (in onDestroyView) so that:
        // - if we're about to do the return transition, it's set to false
        // - otherwise, it's set to true (default value in superclass)
        shouldStartTransition = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.single_category_fragment, container, false)
        viewBinding = SingleCategoryFragmentBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.categoryLoadingSpinner.show()

        // Update seen wallpapers count
        viewModel.updateSeenWallpapers(args.category)

        // Set category description
        viewBinding.categoryDescription.text = args.category.data.description.localized

        viewBinding.wallpapersRecyclerView.adapter = this.adapter
        viewBinding.wallpapersRecyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            currentLayoutSpanCount,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        adjustRecyclerViewState()

        // Finally, observe for updates
        viewModel.getWallpapersByCategoryId(args.category.id).observe(viewLifecycleOwner) { walls ->
            if (walls == null)
                return@observe

            val oldData = this.adapter.data

            // On wallpapers update, refresh the list
            this.adapter.updateDataSet(walls)

            Handler(Looper.getMainLooper()).postDelayed(500) {
                if (isDetached) return@postDelayed

                // Try adding a callback on async list differ
                // After a delay, if the list is not empty, scroll to 0
                if (adapter.data.isNotEmpty() &&
                    oldData.size < adapter.data.size &&
                    oldData != adapter.data
                ) {
                    viewBinding.wallpapersRecyclerView.smoothScrollToPosition(0)
                }
            }

            // Update content loading spinner
            viewBinding.categoryLoadingSpinner.hide() // Loaded

            // If new list is empty, show empty view
            if (walls.isEmpty()) {
                viewBinding.singleCategoryEmptyView.visibility = View.VISIBLE
            } else {
                viewBinding.singleCategoryEmptyView.visibility = View.GONE
            }
        }

        // Get layout span count from preferences
        val layoutRows =
            sharedPreferences.getInt(PREFS_SINGLE_CATEGORY_LAYOUT_ROWS, currentLayoutSpanCount)
        changeLayoutRowCount(layoutRows)

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            viewBinding.wallpapersRecyclerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.systemGestureInsets.bottom
            }

            return@setOnApplyWindowInsetsListener insets
        }
    }

    override fun onPrepareSharedElements(createdView: View) {
        val viewBinding = SingleCategoryFragmentBinding.bind(createdView)
        viewBinding.wallpapersRecyclerView.doOnLayout {
            if (viewBinding.wallpapersRecyclerView.childCount > 0)
                startPostponedEnterTransition()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_LAYOUT_ROW_COUNT, currentLayoutSpanCount)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.single_category_fragment_menu, menu)
        menu.findItem(R.id.singleCategoryLayoutSwitch)?.setIcon(
            if (currentLayoutSpanCount == 1) R.drawable.ic_grid_large_black_24dp
            else R.drawable.ic_category_layout_single_row_scroll
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.singleCategoryLayoutSwitch -> {
                (viewBinding.wallpapersRecyclerView.layoutManager as GridLayoutManager?)?.let {
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
        // Update field
        currentLayoutSpanCount = spanCount

        // Update SharedPreferences
        sharedPreferences.edit {
            putInt(PREFS_SINGLE_CATEGORY_LAYOUT_ROWS, spanCount)
        }

        val fadeOut = AlphaAnimation(1f, 0f)
        val shortDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        fadeOut.interpolator = DecelerateInterpolator()
        fadeOut.duration = shortDuration
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                (viewBinding.wallpapersRecyclerView.layoutManager as GridLayoutManager).also {
                    it.spanCount = spanCount
                    it.requestLayout()
                }

                adjustRecyclerViewState()

                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.interpolator = AccelerateInterpolator()
                fadeIn.duration = shortDuration
                viewBinding.wallpapersRecyclerView.startAnimation(fadeIn)
            }
        })
        viewBinding.wallpapersRecyclerView.startAnimation(fadeOut)
    }

    /**
     * Set correct padding and SnapHelper if necessary
     * Useful when restoring state
     */
    private fun adjustRecyclerViewState() {
        val layoutManager = viewBinding.wallpapersRecyclerView.layoutManager as GridLayoutManager?

        if (layoutManager == null) {
            Log.e(
                this@SingleCategoryFragment.TAG,
                "adjustRecyclerViewState(): layoutManager is null"
            )
            return
        }

        // Post to the next tick to wait until layout request has finished
        Handler().post {
            viewBinding.wallpapersRecyclerView.snapEnabled = currentLayoutSpanCount == 1
        }
    }
}
