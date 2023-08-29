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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.simonesestito.wallapp.R
import com.simonesestito.wallapp.backend.model.Category
import com.simonesestito.wallapp.databinding.ChildCategoriesFragmentBinding
import com.simonesestito.wallapp.di.component.AppInjector
import com.simonesestito.wallapp.enums.CategoryGroup
import com.simonesestito.wallapp.lifecycle.viewmodel.AppViewModelFactory
import com.simonesestito.wallapp.lifecycle.viewmodel.WallpapersViewModel
import com.simonesestito.wallapp.ui.adapter.CategoriesAdapter
import com.simonesestito.wallapp.utils.findNavController
import com.simonesestito.wallapp.utils.onScrollListener
import javax.inject.Inject

class ChildCategoriesFragment : AbstractAppFragment() {
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory

    @Inject
    lateinit var categoriesAdapter: CategoriesAdapter

    @CategoryGroup
    val categoryGroup: String
        get() = requireArguments().getString(ARG_CATEGORY_GROUP)!!

    private lateinit var viewBinding: ChildCategoriesFragmentBinding

    companion object {
        const val ARG_CATEGORY_GROUP = "category_group"

        fun newInstance(@CategoryGroup categoryGroup: String) = ChildCategoriesFragment().apply {
            arguments = bundleOf(ARG_CATEGORY_GROUP to categoryGroup)
        }
    }

    private val viewModel: WallpapersViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppInjector.getInstance().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.child_categories_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = ChildCategoriesFragmentBinding.bind(view)

        // Show loading spinner
        viewBinding.categoriesLoadingBar.show()

        // Set initial RecyclerView status, without any data
        viewBinding.categoriesRecyclerView.adapter = categoriesAdapter
        viewBinding.categoriesRecyclerView.layoutManager = LinearLayoutManager(context)
        viewBinding.categoriesRecyclerView.setHasFixedSize(true)

        categoriesAdapter.onItemClickListener = {
            val direction = CategoriesFragmentDirections.toCategory(it)
            findNavController().navigate(direction)
        }

        viewBinding.categoriesRecyclerView.onScrollListener { adjustElevation() }
        findElevatingAppbar()?.hideAppbarElevation()
    }

    override fun onResume() {
        super.onResume()
        adjustElevation()
    }

    private fun adjustElevation() {
        val layoutManager = viewBinding.categoriesRecyclerView.layoutManager as? LinearLayoutManager ?: return

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

    private fun onNewCategoriesList(list: List<Category>?) {
        // Hide loading spinner
        viewBinding.categoriesLoadingBar.hide()

        if (!list.isNullOrEmpty()) {
            // Update Adapter dataset
            categoriesAdapter.updateDataSet(list)

            // Hide Empty View
            viewBinding.categoriesEmptyView.visibility = View.GONE

            // Show RecyclerView
            viewBinding.categoriesRecyclerView.visibility = View.VISIBLE
        } else {
            // Hide RecyclerView
            viewBinding.categoriesRecyclerView.visibility = View.GONE

            // Show Empty View
            viewBinding.categoriesEmptyView.visibility = View.VISIBLE
        }
    }
}