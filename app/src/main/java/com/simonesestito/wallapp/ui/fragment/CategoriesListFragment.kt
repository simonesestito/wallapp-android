/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.enums.CATEGORY_GROUP_COMMUNITY
import com.simonesestito.wallapp.enums.CATEGORY_GROUP_ORIGINAL
import com.simonesestito.wallapp.enums.CategoryGroup
import com.simonesestito.wallapp.lifecycle.viewmodel.AppViewModelFactory
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpapersViewModel
import com.simonesestito.wallapp.ui.adapter.CategoriesAdapter
import com.simonesestito.wallapp.utils.findNavController
import com.simonesestito.wallapp.utils.getViewModel
import com.simonesestito.wallapp.utils.onScrollListener
import kotlinx.android.synthetic.main.categories_fragment.*
import kotlinx.android.synthetic.main.categories_fragment.view.*
import javax.inject.Inject

class CategoriesListFragment : AbstractAppFragment() {
    override val title
        get() = getString(R.string.app_name)

    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    @Inject
    lateinit var categoriesAdapter: CategoriesAdapter

    private val viewModel: WallpapersViewModel by lazy {
        getViewModel<WallpapersViewModel>(viewModelFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)
        categoriesAdapter.lifecycleOwner = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.categories_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Show loading spinner
        view.categoriesLoadingBar.show()

        // Set initial RecyclerView status, without any data
        view.categoriesRecyclerView.adapter = categoriesAdapter
        view.categoriesRecyclerView.layoutManager = LinearLayoutManager(context)
        view.categoriesRecyclerView.setHasFixedSize(true)

        view.categoriesRecyclerView.onScrollListener { recyclerView ->
            val layoutManager = recyclerView.layoutManager
            if (layoutManager == null || layoutManager !is LinearLayoutManager)
                return@onScrollListener

            // Find the first completely visible item
            // If it's the first one, hide the elevation
            // Else show it
            // Show the elevation only if the RecyclerView is scrolled
            val firstIndex = layoutManager.findFirstCompletelyVisibleItemPosition()
            adjustElevation(firstIndex)
        }
        categoriesAdapter.onItemClickListener = {
            val direction = CategoriesListFragmentDirections.toCategory(it)
            findNavController().navigate(direction)
        }

        view.categoriesBottomNavigation.setOnNavigationItemSelectedListener {
            @CategoryGroup val categoryGroup = when (it.itemId) {
                R.id.categoryGroupCommunity -> CATEGORY_GROUP_COMMUNITY
                R.id.categoryGroupOriginal -> CATEGORY_GROUP_ORIGINAL
                else -> return@setOnNavigationItemSelectedListener false
            }

            viewModel.updateCategoryGroup(categoryGroup)

            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadCategoriesList()
    }

    /**
     * Load categories according to the current category group
     */
    private fun loadCategoriesList() {
        viewModel.currentCategories.observe(this) { list ->
            // Hide loading spinner
            categoriesLoadingBar.hide()

            if (list.isNotEmpty()) {
                // Update Adapter dataset
                categoriesAdapter.updateDataSet(list)

                // Hide Empty View
                categoriesEmptyView.visibility = View.GONE

                // Show RecyclerView
                categoriesRecyclerView.visibility = View.VISIBLE
            } else {
                // Hide RecyclerView
                categoriesRecyclerView.visibility = View.GONE

                // Show Empty View
                categoriesEmptyView.visibility = View.VISIBLE
            }
        }
    }
}
