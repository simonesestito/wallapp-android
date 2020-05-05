/*
 * This file is part of WallApp for Android.
 * Copyright © 2020 Simone Sestito. All rights reserved.
 */

package com.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.enums.CategoryGroup
import com.simonesestito.wallapp.lifecycle.viewmodel.AppViewModelFactory
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpapersViewModel
import com.simonesestito.wallapp.ui.adapter.CategoriesAdapter
import com.simonesestito.wallapp.utils.findNavController
import com.simonesestito.wallapp.utils.getViewModel
import com.simonesestito.wallapp.utils.onScrollListener
import kotlinx.android.synthetic.main.child_categories_fragment.*
import kotlinx.android.synthetic.main.child_categories_fragment.view.*
import javax.inject.Inject

class ChildCategoriesFragment : AbstractAppFragment() {
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory

    @Inject
    lateinit var categoriesAdapter: CategoriesAdapter

    @CategoryGroup
    val categoryGroup: String
        get() = requireArguments().getString(ARG_CATEGORY_GROUP)!!

    companion object {
        const val ARG_CATEGORY_GROUP = "category_group"

        fun newInstance(@CategoryGroup categoryGroup: String) = ChildCategoriesFragment().apply {
            arguments = bundleOf(ARG_CATEGORY_GROUP to categoryGroup)
        }
    }

    private val viewModel: WallpapersViewModel by lazy {
        getViewModel<WallpapersViewModel>(viewModelFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.child_categories_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Show loading spinner
        view.categoriesLoadingBar.show()

        // Set initial RecyclerView status, without any data
        view.categoriesRecyclerView.adapter = categoriesAdapter
        view.categoriesRecyclerView.layoutManager = LinearLayoutManager(context)
        view.categoriesRecyclerView.setHasFixedSize(true)

        categoriesAdapter.onItemClickListener = {
            val direction = CategoriesFragmentDirections.toCategory(it)
            findNavController().navigate(direction)
        }

        view.categoriesRecyclerView.onScrollListener { adjustElevation() }
        findElevatingAppbar()?.hideAppbarElevation()
    }

    override fun onResume() {
        super.onResume()
        adjustElevation()
    }

    private fun adjustElevation() {
        val layoutManager = categoriesRecyclerView.layoutManager as? LinearLayoutManager ?: return

        // Find the first completely visible item
        // If it's the first one, hide the elevation
        // Else show it
        // Show the elevation only if the RecyclerView is scrolled
        val firstIndex = layoutManager.findFirstCompletelyVisibleItemPosition()
        adjustElevation(firstIndex)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadCategoriesList()
    }

    /**
     * Load categories according to the current category group
     */
    private fun loadCategoriesList() {
        viewModel.getCategoriesByGroup(categoryGroup)
                .observe(viewLifecycleOwner, this::onNewCategoriesList)
    }

    private fun onNewCategoriesList(list: List<Category>) {
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