package it.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.arch.viewmodel.CategoryViewModel
import it.simonesestito.wallapp.ui.adapter.CategoriesAdapter
import it.simonesestito.wallapp.utils.findNavController
import it.simonesestito.wallapp.utils.onScrollListener
import kotlinx.android.synthetic.main.categories_fragment.*
import kotlinx.android.synthetic.main.categories_fragment.view.*

class CategoriesListFragment : AbstractAppFragment() {
    override val title
        get() = getString(R.string.app_name)

    private val viewModel: CategoryViewModel by lazy {
        ViewModelProviders.of(this).get(CategoryViewModel::class.java)
    }
    private val categoriesAdapter: CategoriesAdapter by lazy { CategoriesAdapter() }

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
            if (firstIndex == 0)
                hideAppbarElevation()
            else
                showAppbarElevation()
        }
        categoriesAdapter.onItemClickListener = {
            val direction = CategoriesListFragmentDirections.toCategory(it)
            findNavController().navigate(direction)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.categories.observe(this, Observer { list ->
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
        })
    }
}
